/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.brixcms.web;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

@SuppressWarnings("serial")
public class BrixFeedbackPanel extends FeedbackPanel {

    /**
     * @see org.apache.wicket.Component#Component(String)
     */
    public BrixFeedbackPanel(final String id) {
        super(id, null);
    }

    /**
     * @see org.apache.wicket.Component#Component(String)
     * 
     * @param id
     * @param filter
     */
    public BrixFeedbackPanel(final String id, IFeedbackMessageFilter filter) {
        super(id, filter);
    }

    @Override
    protected String getCSSClass(FeedbackMessage message) {
        String cssClass = "alert";
        switch (message.getLevel()) {
        case FeedbackMessage.UNDEFINED:
        case FeedbackMessage.DEBUG:
        case FeedbackMessage.INFO:
            return cssClass + " alert-info";
        case FeedbackMessage.SUCCESS:
            return cssClass + " alert-success";
        case FeedbackMessage.WARNING:
            return cssClass + " alert-warning";
        case FeedbackMessage.ERROR:
        case FeedbackMessage.FATAL:
            return cssClass + " alert-danger";
        default:
            return super.getCSSClass(message);
        }
    }

}
