package brix.plugin.site.resource.managers;

import java.io.Serializable;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import brix.codepress.CodePressEnabler;
import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.resource.ResourceManager;

public class TextResourceManager implements ResourceManager
{

    public boolean handles(String mimeType)
    {
        return mimeType.startsWith("text/") || mimeType.equals("application/xml");
    }

    public boolean hasEditor()
    {
        return true;
    }

    public boolean hasViewer()
    {
        return true;
    }

    public Panel newEditor(String id, IModel<BrixNode> nodeModel)
    {
        return new EditorPanel(id, nodeModel);
    }

    public Panel newViewer(String id, IModel<BrixNode> nodeModel)
    {
        return new ViewerPanel(id, nodeModel);
    }

    private static class ViewerPanel extends Panel
    {

        public ViewerPanel(String id, final IModel<BrixNode> nodeModel)
        {
            super(id, nodeModel);

            IModel labelModel = new Model()
            {
                @Override
                public Serializable getObject()
                {
                    BrixFileNode node = (BrixFileNode)getModel().getObject();
                    return node.getDataAsString();
                }
            };

            add(new Label("label", labelModel));
        }

    }

    private static class EditorPanel extends Panel<BrixNode>
    {

        private BrixFileNode getFileNode()
        {
            return (BrixFileNode)getModelObject();
        }

        private void enableCodePress(TextArea area)
        {
            String mime = getFileNode().getMimeType();
            if (mime != null)
            {
                String language = null;
                if (mime.equals("text/javascript"))
                    language = "javascript";
                else if (mime.equals("text/css"))
                    language = "css";
                else if (mime.equals("text/html"))
                    language = "html";
                if (language != null)
                    area.add(new CodePressEnabler(language, true));
            }
        }

        private String content;

        public EditorPanel(String id, final IModel<BrixNode> nodeModel)
        {
            super(id, nodeModel);

            Form form = new Form("form");
            add(form);

            this.content = getFileNode().getDataAsString();

            TextArea textArea;
            form.add(textArea = new TextArea("textarea", new PropertyModel(this, "content")));
            enableCodePress(textArea);

            form.add(new Button("save")
            {
                @Override
                public void onSubmit()
                {
                    BrixFileNode node = getFileNode();
                    node.checkout();
                    node.setData(content);
                    node.save();
                    node.checkin();
                }
            });
        }

    };

}
