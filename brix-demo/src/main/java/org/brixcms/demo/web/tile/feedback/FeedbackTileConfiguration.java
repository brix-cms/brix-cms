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
        if (node.hasProperty("email")) {
            setEmail(node.getProperty("email").getString());
        }
        if (node.hasProperty("feedbackEmailLabel")) {
            setFeedbackEmailLabel(node.getProperty("feedbackEmailLabel").getString());
        }
        if (node.hasProperty("feedbackMessageLabel")) {
            setFeedbackMessageLabel(node.getProperty("feedbackMessageLabel").getString());
        }
        if (node.hasProperty("feedbackSubmittedMessage")) {
            setFeedbackSubmittedMessage(node.getProperty("feedbackSubmittedMessage").getString());
        }
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
