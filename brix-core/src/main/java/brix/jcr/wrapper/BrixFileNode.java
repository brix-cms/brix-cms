package brix.jcr.wrapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import javax.jcr.Node;

import org.apache.wicket.util.io.Streams;

import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;

public class BrixFileNode extends BrixNode
{

    public BrixFileNode(Node delegate, JcrSession session)
    {
        super(delegate, session);
    }

    public static boolean isFileNode(JcrNode node)
    {
        if (!node.getPrimaryNodeType().getName().equals("nt:file"))
        {
            return false;
        }

        return node.hasNode("jcr:content");
    }

    private JcrNode getContent()
    {
        return (JcrNode)getPrimaryItem();
    }

    public void setEncoding(String encoding)
    {
        getContent().setProperty("jcr:encoding", encoding);
    }

    public String getEncoding()
    {
        return getContent().hasProperty("jcr:encoding") ? getContent().getProperty("jcr:encoding")
                .getString() : null;
    }

    public void setMimeType(String mimeType)
    {
        getContent().setProperty("jcr:mimeType", mimeType);
    }

    public String getMimeType()
    {
        return getContent().getProperty("jcr:mimeType").getString();
    }

    public void setData(String data)
    {
        if (data == null)
        {
            data = "";
        }
        setEncoding("UTF-8");
        getContent().setProperty("jcr:data", data);
    }

    public void setDataAsString(String data)
    {
        setData(data);
    }

    public void setData(InputStream data)
    {
        getContent().setProperty("jcr:data", data);
    }

    public String getDataAsString()
    {
        return getContent().getProperty("jcr:data").getString();
    }

    public InputStream getDataAsStream()
    {
        return getContent().getProperty("jcr:data").getStream();
    }

    public void writeData(OutputStream stream) throws IOException
    {
        Streams.copy(getDataAsStream(), stream);
    }

    public static BrixFileNode initialize(JcrNode node, String mimeType)
    {
        if (node.isNodeType("nt:file") == false) 
        {
            throw new IllegalStateException("Argument 'node' must have JCR type nt:file.");
        }
        node.addNode("jcr:content", "nt:resource");
        BrixFileNode wrapped = new BrixFileNode(node.getDelegate(), node.getSession());
        wrapped.setMimeType(mimeType);
        wrapped.getContent().setProperty("jcr:lastModified", Calendar.getInstance());
        return wrapped;
    }


}
