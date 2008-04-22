package brix.web.admin;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import javax.jcr.ImportUUIDBehavior;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebResponse;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import brix.BrixRequestCycle;
import brix.jcr.api.JcrSession;

public class JcrAdminPage extends WebPage
{

    public JcrAdminPage()
    {
        Form form = new Form("form");
        add(form);

        form.add(new DropDownChoice("workspace", new PropertyModel(this, "workspace"),
                workspacesModel)
        {
            @Override
            protected boolean wantOnSelectionChangedNotifications()
            {
                return true;
            }
        }.setNullValid(false).setRequired(true));

        form.add(new Link("export")
        {
            @Override
            public void onClick()
            {
                getRequestCycle().setRequestTarget(new ExportRequestTarget());
            }
        });

        final FileUploadField upload = new FileUploadField("upload");
        form.add(upload);

        form.add(new Button("import")
        {
            @Override
            public void onSubmit()
            {
                InputStream s;
                try
                {
                    s = upload.getFileUpload().getInputStream();
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
                if (s != null)
                {
                    getJcrSession(workspace).importXML("/", s,
                            ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING);
                }
            }
        });

    }

    private static class ContentHandlerWrapper implements ContentHandler
    {

        private final ContentHandler delegate;

        public ContentHandlerWrapper(ContentHandler delegate)
        {
            this.delegate = delegate;
        }

        public void characters(char[] ch, int start, int length) throws SAXException
        {
            if (ignore == 0)
                delegate.characters(ch, start, length);
        }

        public void endDocument() throws SAXException
        {
            delegate.endDocument();
        }

        public void endElement(String uri, String localName, String name) throws SAXException
        {
            if (ignore == 0)
                delegate.endElement(uri, localName, name);

            if (ignore > 0)
                --ignore;
        }

        public void endPrefixMapping(String prefix) throws SAXException
        {
            delegate.endPrefixMapping(prefix);
        }

        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException
        {
            if (ignore == 0)
                delegate.ignorableWhitespace(ch, start, length);
        }

        public void processingInstruction(String target, String data) throws SAXException
        {
            delegate.processingInstruction(target, data);
        }

        public void setDocumentLocator(Locator locator)
        {
            delegate.setDocumentLocator(locator);
        }

        public void skippedEntity(String name) throws SAXException
        {
            delegate.skippedEntity(name);
        }

        public void startDocument() throws SAXException
        {
            delegate.startDocument();
        }

        private boolean isJcrSystem(Attributes atts)
        {
            for (int i = 0; i < atts.getLength(); ++i)
            {
                if (atts.getQName(i).equals("sv:name") && atts.getValue(i).equals("jcr:system"))
                    return false;
            }
            return false;
        }

        public void startElement(String uri, String localName, String name, Attributes atts)
                throws SAXException
        {
            if (isJcrSystem(atts))
            {
                ignore = 1;
            }
            else
            {
                if (ignore > 0)
                    ++ignore;
            }

            if (ignore == 0)
                delegate.startElement(uri, localName, name, atts);
        }

        public void startPrefixMapping(String prefix, String uri) throws SAXException
        {
            delegate.startPrefixMapping(prefix, uri);
        }

        private int ignore = 0;
    };

    private void exportView(JcrSession session, OutputStream out)
    {
        SAXTransformerFactory stf = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
        try
        {
            TransformerHandler th = stf.newTransformerHandler();
            th.getTransformer().setOutputProperty(OutputKeys.METHOD, "xml");
            th.getTransformer().setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            th.getTransformer().setOutputProperty(OutputKeys.INDENT, "no");
            th.setResult(new StreamResult(out));

            session.exportSystemView("/", new ContentHandlerWrapper(th), false, false);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private class ExportRequestTarget implements IRequestTarget
    {

        public void respond(RequestCycle requestCycle)
        {
            WebResponse response = (WebResponse)requestCycle.getResponse();
            response.setAttachmentHeader(workspace + ".xml");
            exportView(getJcrSession(workspace), response.getOutputStream());
        }

        public void detach(RequestCycle requestCycle)
        {


        }

    };

    // TODO: Determine the default workspace somehow
    private String workspace = null; //brix.BrixRequestCycle.Locator.getBrix().getDefaultWorkspaceName();

    private IModel workspacesModel = new LoadableDetachableModel()
    {
        @Override
        protected Object load()
        {
            String[] names = getJcrSession().getWorkspace().getAccessibleWorkspaceNames();
            return Arrays.asList(names);
        }
    };

    private JcrSession getJcrSession(String workspaceName)
    {
        return BrixRequestCycle.Locator.getSession(workspaceName);
    }

    private JcrSession getJcrSession()
    {
        return getJcrSession(null);
    }
}
