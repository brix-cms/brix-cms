package brix.web.tab;

import org.apache.wicket.extensions.markup.html.tabs.ITab;

public interface BrixTab<T> extends ITab<T>
{

    public boolean isVisible();

}
