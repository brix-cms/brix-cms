package brix.plugin.site.node.tilepage;

import org.apache.wicket.markup.html.internal.HtmlHeaderContainer;
import org.apache.wicket.markup.html.panel.Panel;

public class HeaderContributorPanel extends Panel
{

    public HeaderContributorPanel(String id)
    {
        super(id);
        setRenderBodyOnly(true);
    }

    @Override
    protected void onBeforeRender()
    {
        super.onBeforeRender();

        HtmlHeaderContainer container = new HtmlHeaderContainer("container")
        {
            protected boolean renderOpenAndCloseTags()
            {
                return false;
            }
        };

        add(container);
    }

}
