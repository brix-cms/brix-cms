package org.brixcms.web.util;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;

@SuppressWarnings("serial")
public class DisabledClassAppender extends Behavior {

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        if(!component.isEnabledInHierarchy()){
            tag.append("class", "disabled", " ");
        }
    }
}
