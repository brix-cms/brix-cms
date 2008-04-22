package brix.web.tab;

import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.Loop.LoopItem;

public class BrixTabbedPanel extends TabbedPanel
{

    public BrixTabbedPanel(String id, List<BrixTab> tabs)
    {
        super(id, tabs);
    }

    protected LoopItem newTabContainer(final int tabIndex)
    {
        return new LoopItem(tabIndex)
        {
            private static final long serialVersionUID = 1L;

            protected void onComponentTag(ComponentTag tag)
            {
                super.onComponentTag(tag);
                String cssClass = (String)tag.getString("class");
                if (cssClass == null)
                {
                    cssClass = " ";
                }
                cssClass += " tab" + getIteration();

                if (getIteration() == getSelectedTab())
                {
                    cssClass += " selected";
                }
                if (getIteration() == getTabs().size() - 1)
                {
                    cssClass += " last";
                }
                tag.put("class", cssClass.trim());
            }

            @Override
            public boolean isVisible()
            {
                return ((BrixTab)getTabs().get(tabIndex)).isVisible();
            }

        };
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onBeforeRender()
    {
        if (!hasBeenRendered() && getSelectedTab() == -1)
        {
            List<BrixTab> tabs = getTabs();
            for (int i = 0; i < tabs.size(); ++i)
            {
                BrixTab tab = tabs.get(i);
                if (tab.isVisible())
                {
                    setSelectedTab(i);
                    break;
                }
            }
        }
        super.onBeforeRender();
    }

    @Override
    public void setSelectedTab(int index)
    {
        BrixTab tab = (BrixTab)getTabs().get(index);
        if (tab.isVisible() == false)
        {
            if (get(TAB_PANEL_ID) == null)
            {
                add(new Label(TAB_PANEL_ID));
            }
            else
            {
                replace(new Label(TAB_PANEL_ID));
            }
        }
        else
        {
            super.setSelectedTab(index);
        }
    }

}
