package brix.plugin.site.resource;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;

public interface ResourceManager
{

    public boolean handles(String mimeType);

    public boolean hasEditor();

    public Panel newEditor(String id, IModel<BrixNode> nodeModel);

    public boolean hasViewer();

    public Panel newViewer(String id, IModel<BrixNode> nodeModel);

}
