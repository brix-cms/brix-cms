package org.brixcms.web.util;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;

@SuppressWarnings("serial")
public class DisabledClassAppender extends Behavior {

    private final Component directingComponent;

    public DisabledClassAppender() {
        this(null);
    }

    public DisabledClassAppender(Component directingComponent) {
        this.directingComponent = directingComponent;
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        if (directingComponent != null) {
            if (!directingComponent.isEnabledInHierarchy()) {
                tag.append("class", "disabled", " ");
            }
        } else {
            if (!component.isEnabledInHierarchy()) {
                tag.append("class", "disabled", " ");
            }
        }
    }
}
