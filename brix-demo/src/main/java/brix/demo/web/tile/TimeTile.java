package brix.demo.web.tile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.admin.Tile;
import brix.plugin.site.page.admin.TileEditorPanel;
import brix.web.nodepage.BrixPageParameters;

public class TimeTile implements Tile
{

    public Component newViewer(String id, IModel<BrixNode> tileNode, BrixPageParameters pageParameters)
    {
        return new TimeLabel(id, tileNode).setRenderBodyOnly(true);
    }

    private static class TimeLabel extends Label
    {

        public TimeLabel(String id, IModel<BrixNode> nodeModel)
        {
            super(id);
            setModel(new TimeStringModel(nodeModel));
        }

        private class TimeStringModel extends AbstractReadOnlyModel
        {
            private final IModel<BrixNode> tileNode;

            public TimeStringModel(IModel<BrixNode> tileNode)
            {
                super();
                this.tileNode = tileNode;
            }

            @Override
            public Object getObject()
            {
                JcrNode tileNode = this.tileNode.getObject();
                String format = tileNode.hasProperty("format") ? tileNode.getProperty("format")
                        .getString() : null;
                if (format == null)
                {
                    format = "MM/dd/yyyy HH:mm:ss z";
                }
                DateFormat fmt = new SimpleDateFormat(format);
                return fmt.format(new Date());
            }

            @Override
            public void detach()
            {
                tileNode.detach();
                super.detach();
            }

        }

    }

    public TileEditorPanel newEditor(String id, IModel<BrixNode> tileContainerNode)
    {
        return new TimeTileEditor(id, tileContainerNode);
    }

    public String getDisplayName()
    {
        return "Current Time Tile";
    }

    public String getTypeName()
    {
        return "brix.web.TimeTile";
    }

    public boolean requiresSSL(IModel<BrixNode> tileNode)
    {
        return false;
    }

}
