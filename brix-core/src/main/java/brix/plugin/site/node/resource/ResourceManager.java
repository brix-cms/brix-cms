package brix.plugin.site.node.resource;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import brix.jcr.api.JcrNode;

public interface ResourceManager
{

    public boolean handles(String mimeType);

    public boolean hasEditor();

    public Panel newEditor(String id, IModel<JcrNode> nodeModel);

    public boolean hasViewer();

    public Panel newViewer(String id, IModel<JcrNode> nodeModel);

}
