package brix.web.tab;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;

public class BrixAjaxTabbedPanel extends BrixTabbedPanel
{

    public BrixAjaxTabbedPanel(String id, List<BrixTab> tabs)
    {
        super(id, tabs);
        setOutputMarkupId(true);
    }

    protected WebMarkupContainer newLink(String linkId, final int index)
    {
        return new AjaxFallbackLink(linkId)
        {

            private static final long serialVersionUID = 1L;

            public void onClick(AjaxRequestTarget target)
            {
                setSelectedTab(index);
                if (target != null)
                {
                    target.addComponent(BrixAjaxTabbedPanel.this);
                }
                onAjaxUpdate(target);
            }

        };
    }

    /**
     * A template method that lets users add additional behavior when ajax update occurs. This
     * method is called after the current tab has been set so access to it can be obtained via
     * {@link #getSelectedTab()}.
     * <p>
     * <strong>Note</strong> Since an {@link AjaxFallbackLink} is used to back the ajax update the
     * <code>target</code> argument can be null when the client browser does not support ajax and
     * the fallback mode is used. See {@link AjaxFallbackLink} for details.
     * 
     * @param target
     *            ajax target used to update this component
     */
    protected void onAjaxUpdate(AjaxRequestTarget target)
    {
    }

}
