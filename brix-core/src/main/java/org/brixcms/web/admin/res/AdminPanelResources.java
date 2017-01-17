package org.brixcms.web.admin.res;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

@SuppressWarnings("serial")
public class AdminPanelResources extends Behavior {

    private static final ResourceReference JS_TETHER = new JavaScriptResourceReference(AdminPanelResources.class, "tether.js") {
        public List<HeaderItem> getDependencies() {
            return Arrays.asList(JavaScriptHeaderItem.forReference(Application.get().getJavaScriptLibrarySettings().getJQueryReference()));
        };
    };
    private static final ResourceReference JS_BOOTSTRAP = new JavaScriptResourceReference(AdminPanelResources.class, "bootstrap.js") {
        public List<HeaderItem> getDependencies() {
            return Arrays.asList(JavaScriptHeaderItem.forReference(JS_TETHER));
        };
    };
    private static final ResourceReference CSS_BOOTSTRAP = new CssResourceReference(AdminPanelResources.class, "bootstrap.css");
    private static final ResourceReference CSS = new CssResourceReference(AdminPanelResources.class, "style.css");
    public static final PackageResourceReference LOGO = new PackageResourceReference(AdminPanelResources.class, "brix-logo.png");

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(CSS_BOOTSTRAP));
        response.render(CssHeaderItem.forReference(CSS));
        response.render(JavaScriptHeaderItem.forReference(JS_TETHER));
        response.render(JavaScriptHeaderItem.forReference(JS_BOOTSTRAP));
    }

}
