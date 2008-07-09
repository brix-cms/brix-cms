package brix.demo.web.tile.guestbook;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;

import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrNodeIterator;
import brix.jcr.wrapper.BrixNode;

public class GuestBookPanel extends Panel
{
    private static final long serialVersionUID = 1L;


    public GuestBookPanel(String id, IModel<BrixNode> model)
    {
        super(id, model);

        add(new MessageForm("form"));
        add(new PropertyListView<Entry>("entries", new EntriesModel())
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<Entry> item)
            {
                item.add(new Label("name"));
                item.add(new Label("message"));
                item.add(new Label("timestamp"));
            }

        });
    }

    private class MessageForm extends Form<Entry>
    {
        private static final long serialVersionUID = 1L;

        private Entry entry = new Entry();

        public MessageForm(String id)
        {
            super(id);
            add(new TextField<String>("name", new PropertyModel<String>(this, "entry.name")));
            add(new TextArea<String>("message", new PropertyModel<String>(this, "entry.message")));
        }

        @Override
        protected void onSubmit()
        {
            onMessage(entry);
            entry = new Entry();
        }
    }

    private static class Entry implements Serializable
    {
        private static final long serialVersionUID = 1L;

        public String name;
        public String message;
        public Date timestamp;
    }

    protected void onMessage(Entry message)
    {
        JcrNode tile = (JcrNode)getDefaultModelObject();

        JcrNode entry = tile.addNode("entry");
        entry.setProperty("name", message.name);
        entry.setProperty("message", message.message);
        entry.setProperty("timestamp", System.currentTimeMillis());

        tile.getSession().save();
    }

    private class EntriesModel extends LoadableDetachableModel<List<Entry>>
    {

        private static final long serialVersionUID = 1L;

        @Override
        protected List<Entry> load()
        {
            JcrNode tile = (JcrNode)getDefaultModelObject();
            JcrNodeIterator entryNodes = tile.getNodes("entry");
            ArrayList<Entry> entries = new ArrayList<Entry>((int)entryNodes.getSize());

            while (entryNodes.hasNext())
            {
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
