package brix.plugin.fragment;

import java.util.Map;

import org.apache.wicket.model.IModel;

import brix.Brix;
import brix.BrixNodeModel;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.markup.AbstractTileTag;

/**
 * ComponentTag that that replaces the &lt;brix:fragment&gt; tags.
 * 
 * @author ivaynberg
 */
public class FragmentTag extends AbstractTileTag
{
    private final String workspaceName;

    private final IModel<BrixNode> container;

    public FragmentTag(String name, Type type, Map<String, String> attributeMap,
            BrixNode container, String fragmentName)
    {
        super(name, type, attributeMap, fragmentName);
        this.container = new BrixNodeModel(container);
        workspaceName = container.getSession().getWorkspace().getName();
    }

    @Override
    protected TileContainer getTileContainer()
    {
        final Brix brix = this.container.getObject().getBrix();
        this.container.detach();
        return FragmentPlugin.getContainerNode(brix, workspaceName);
    }

}
