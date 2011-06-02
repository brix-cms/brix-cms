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

package org.brixcms.demo.web.tile.guestbook;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrNodeIterator;
import org.brixcms.jcr.wrapper.BrixNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GuestBookPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public GuestBookPanel(String id, IModel<BrixNode> model) {
        super(id, model);
        add(new FeedbackPanel("feedback"));
        add(new MessageForm("form"));
        add(new PropertyListView<Entry>("entries", new EntriesModel()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<Entry> item) {
                item.add(new Label("name"));
                item.add(new Label("message"));
                item.add(new Label("timestamp"));
            }
        });
    }

    protected void onMessage(Entry message) {
        JcrNode tile = (JcrNode) getDefaultModelObject();

        JcrNode entry = tile.addNode("entry");
        entry.setProperty("name", message.name);
        entry.setProperty("message", message.message);
        entry.setProperty("timestamp", System.currentTimeMillis());

        tile.getSession().save();
    }

    private class MessageForm extends Form<Entry> {
        private static final long serialVersionUID = 1L;

        private Entry entry = new Entry();

        public MessageForm(String id) {
            super(id);
            add(new TextField<String>("name", new PropertyModel<String>(this, "entry.name"))
                    .setRequired(true));
            add(new TextArea<String>("message", new PropertyModel<String>(this, "entry.message"))
                    .setRequired(true));
        }

        @Override
        protected void onSubmit() {
            onMessage(entry);
            entry = new Entry();
        }
    }

    private static class Entry implements Serializable {
        private static final long serialVersionUID = 1L;

        public String name;
        public String message;
        public Date timestamp;
    }

    private class EntriesModel extends LoadableDetachableModel<List<Entry>> {
        private static final long serialVersionUID = 1L;

        @Override
        protected List<Entry> load() {
            JcrNode tile = (JcrNode) getDefaultModelObject();
            JcrNodeIterator entryNodes = tile.getNodes("entry");
            ArrayList<Entry> entries = new ArrayList<Entry>((int) entryNodes.getSize());

            while (entryNodes.hasNext()) {
                JcrNode entryNode = entryNodes.nextNode();
                Entry entry = new Entry();
                entry.name = entryNode.getProperty("name").getString();
                entry.message = entryNode.getProperty("message").getString();
                entry.timestamp = new Date(entryNode.getProperty("timestamp").getLong());
                entries.add(entry);
            }

            return entries;
        }
    }
}
