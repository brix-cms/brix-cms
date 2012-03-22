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

package org.brixcms.demo.web.tile.feedback;

import java.io.Serializable;

import org.brixcms.jcr.wrapper.BrixNode;

public class FeedbackTileConfiguration implements Serializable {

    private String email;
    private String feedbackEmailLabel;
    private String feedbackMessageLabel;
    private String feedbackSubmittedMessage;

    public FeedbackTileConfiguration() {
    }

    public FeedbackTileConfiguration(BrixNode node) {
        load(node);
    }

    public void load(BrixNode node) {
        setEmail(node.hasProperty("email") ? node.getProperty("email").getString() : null);
        setFeedbackEmailLabel(node.hasProperty("feedbackEmailLabel") ? node.getProperty("feedbackEmailLabel").getString() : null);
        setFeedbackMessageLabel(node.hasProperty("feedbackMessageLabel") ? node.getProperty("feedbackMessageLabel").getString() : null);
        setFeedbackSubmittedMessage(node.hasProperty("feedbackSubmittedMessage") ? node.getProperty("feedbackSubmittedMessage").getString()
                : null);
    }

    public void save(BrixNode node) {
        node.setProperty("email", getEmail());
        node.setProperty("feedbackEmailLabel", getFeedbackEmailLabel());
        node.setProperty("feedbackMessageLabel", getFeedbackMessageLabel());
        node.setProperty("feedbackSubmittedMessage", getFeedbackSubmittedMessage());
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFeedbackEmailLabel() {
        return feedbackEmailLabel;
    }

    public void setFeedbackEmailLabel(String feedbackEmailLabel) {
        this.feedbackEmailLabel = feedbackEmailLabel;
    }

    public String getFeedbackMessageLabel() {
        return feedbackMessageLabel;
    }

    public void setFeedbackMessageLabel(String feedbackMessageLabel) {
        this.feedbackMessageLabel = feedbackMessageLabel;
    }

    public String getFeedbackSubmittedMessage() {
        return feedbackSubmittedMessage;
    }

    public void setFeedbackSubmittedMessage(String feedbackSubmittedMessage) {
        this.feedbackSubmittedMessage = feedbackSubmittedMessage;
    }

}
