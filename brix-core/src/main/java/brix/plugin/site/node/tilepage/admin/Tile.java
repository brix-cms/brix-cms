package brix.plugin.site.node.tilepage.admin;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import brix.jcr.api.JcrNode;
import brix.web.nodepage.BrixPageParameters;

public interface Tile
{

    // TODO remove tilePageParameters param in favor of PageParametersAware
    Component newViewer(String id, IModel<JcrNode> tileNode, BrixPageParameters tilePageParameters);

    TileEditorPanel newEditor(String id, IModel<JcrNode> tileContainerNode);

    String getDisplayName();

    String getTypeName();

    boolean requiresSSL(IModel<JcrNode> tileNode);
}
