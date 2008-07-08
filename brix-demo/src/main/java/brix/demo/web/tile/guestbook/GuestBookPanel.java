package brix.demo.web.tile.guestbook;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;

public class GuestBookPanel extends Panel
{
    private static final long serialVersionUID = 1L;

    public GuestBookPanel(String id, IModel<BrixNode> model)
    {
        super(id, model);
    }

}
