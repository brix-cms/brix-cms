package brix.plugin.fragment;

import java.util.Map;

import brix.Brix;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.node.tilepage.markup.AbstractTileTag;

/**
 * ComponentTag that that replaces the &lt;brix:fragment&gt; tags.
 * 
 * @author ivaynberg
 */
public class FragmentTag extends AbstractTileTag
{
    private final String workspaceName;

    public FragmentTag(String name, Type type, Map<String, String> attributeMap,
            BrixNode container, String fragmentName)
    {
        super(name, type, attributeMap, fragmentName);
        workspaceName = container.getSession().getWorkspace().getName();
    }

    @Override
    protected TileContainer getTileContainer()
    {
        return FragmentPlugin.getContainerNode(Brix.get(), workspaceName);
    }

}
