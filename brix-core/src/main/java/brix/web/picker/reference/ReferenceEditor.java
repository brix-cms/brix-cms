package brix.web.picker.reference;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import brix.web.nodepage.BrixPageParameters;
import brix.web.reference.Reference;
import brix.web.tab.CachingAbstractTab;

public class ReferenceEditor extends Panel<Reference>
{

    public ReferenceEditor(String id, ReferenceEditorConfiguration configuration)
    {
        super(id);
        this.configuration = configuration;
        init();
    }

    public ReferenceEditor(String id, IModel<Reference> model, ReferenceEditorConfiguration configuration)
    {
        super(id, model);
        this.configuration = configuration;
        init();
    }

    private Reference getReference()
    {
        return (Reference)getModelObject();
    }

    private void init()
    {
        List<ITab> tabs = new ArrayList<ITab>();
        tabs.add(new CachingAbstractTab(new ResourceModel("reference"))
        {
            @Override
            public Panel newPanel(String panelId)
            {
                return new NodeUrlTab(panelId, getModel())
                {
                    @Override
                    protected ReferenceEditorConfiguration getConfiguration()
                    {
                        return configuration;
                    }
                };
            }

            @Override
            public boolean isVisible()
            {
                return configuration.isAllowNodePicker() || configuration.isAllowURLEdit();
            }
        });
        tabs.add(new CachingAbstractTab(new ResourceModel("queryParameters"))
        {
            @Override
            public Panel newPanel(String panelId)
            {
                return new QueryParametersTab(panelId)
                {
                    @Override
                    protected BrixPageParameters getPageParameters()
                    {
                        return getReference().getParameters();
                    };
                };
            }

            @Override
            public boolean isVisible()
            {
                return configuration.isAllowQueryParameters();
            }
        });
        tabs.add(new CachingAbstractTab(new ResourceModel("indexedParameters"))
        {
            @Override
            public Panel newPanel(String panelId)
            {
                return new IndexedParametersTab(panelId)
                {
                    @Override
                    protected BrixPageParameters getPageParameters()
                    {
                        return getReference().getParameters();
                    }
                };
            }

            @Override
            public boolean isVisible()
            {
                return configuration.isAllowIndexedParameters();
            }
        });

        add(new AjaxTabbedPanel("tabbedPanel", tabs));
    }

    private final ReferenceEditorConfiguration configuration;
}
