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

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.brixcms.demo.web.WicketApplication;
import org.brixcms.jcr.wrapper.BrixNode;

public class FeedbackTilePanel extends Panel {
    private static final long serialVersionUID = 1L;

    private final FeedbackTileConfiguration configuration;

    public FeedbackTilePanel(String id, IModel<BrixNode> nodeModel) {
        super(id, nodeModel);

        configuration = getConfiguration(nodeModel);
        add(new FeedbackPanel("feedback"));
        add(new FeedbackForm("form"));
    }

    private FeedbackTileConfiguration getConfiguration(IModel<BrixNode> nodeModel) {
        return new FeedbackTileConfiguration(nodeModel.getObject());
    }

    private class FeedbackForm extends Form<FeedbackMessage> {
        private static final long serialVersionUID = 1L;

        public FeedbackForm(String id) {
            super(id);
            setModel(new CompoundPropertyModel<FeedbackTilePanel.FeedbackMessage>(new FeedbackMessage()));
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();
            add(new TextField<String>("email").setLabel(new PropertyModel<String>(configuration, "feedbackEmailLabel")).setRequired(true));
            add(new TextArea<String>("message").setLabel(new PropertyModel<String>(configuration, "feedbackMessageLabel"))
                    .setRequired(true));
        }

        @Override
        protected void onSubmit() {
            if (sendMail(getModelObject())) {
                setModelObject(new FeedbackMessage());
                success(configuration.getFeedbackSubmittedMessage());
            }
        }
    }

    private boolean sendMail(FeedbackMessage feedbackMessage) {
        try {
            Session mailSession = Session.getDefaultInstance(WicketApplication.get().getProperties(), null);
            Transport transport = mailSession.getTransport();

            MimeMessage message = new MimeMessage(mailSession);
            message.setSubject("Feedback from " + feedbackMessage.email);
            message.setContent(feedbackMessage.message, "text/plain");
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(configuration.getEmail()));

            transport.connect();
            transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
            transport.close();
            return true;
        } catch (Exception e) {
            error(getString("email.error", null, "unable to send feedback"));
            return false;
        }
    }

    private static class FeedbackMessage implements Serializable {
        private static final long serialVersionUID = 1L;

        private String email;
        private String message;
    }

}
