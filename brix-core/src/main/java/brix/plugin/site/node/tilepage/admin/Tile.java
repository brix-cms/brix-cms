package brix.plugin.site.node.tilepage.admin;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.web.nodepage.BrixPageParameters;

public interface Tile
{

    // TODO remove tilePageParameters param in favor of PageParametersAware
    Component newViewer(String id, IModel<BrixNode> tileNode, BrixPageParameters tilePageParameters);

    TileEditorPanel newEditor(String id, IModel<BrixNode> tileContainerNode);

    String getDisplayName();

    String getTypeName();

    boolean requiresSSL(IModel<BrixNode> tileNode);
}
