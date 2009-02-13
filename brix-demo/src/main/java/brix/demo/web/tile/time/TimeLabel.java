package brix.demo.web.tile.time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixNode;

/**
 * Label used to render time in {@link TimeTile}
 * 
 * @author igor.vaynberg
 * 
 */
public class TimeLabel extends Label
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * 
     * @param id
     *            component id
     * @param nodeModel
     *            time tile node
     */
    public TimeLabel(String id, IModel<BrixNode> nodeModel)
    {
        super(id, new TimeStringModel(nodeModel));
    }

    /**
     * Constructs time expression string
     * 
     * @author igor.vaynberg
     * 
     */
    private static class TimeStringModel extends AbstractReadOnlyModel<String>
    {
        private static final long serialVersionUID = 1L;

        /** jcr tile node that contains the time expression format */
        private final IModel<BrixNode> tileNode;

        /**
         * Constructor
         * 
         * @param tileNode
         */
        public TimeStringModel(IModel<BrixNode> tileNode)
        {
            super();
            this.tileNode = tileNode;
        }

        /** {@inheritDoc} */
        @Override
        public String getObject()
        {
            // get the tile's jcr node and retrieve the format expression
            JcrNode tileNode = this.tileNode.getObject();
            String format = tileNode.hasProperty("format") ? tileNode.getProperty("format")
                .getString() : null;

            // create the time expression
            if (format == null)
            {
                format = "MM/dd/yyyy HH:mm:ss z";
            }
            DateFormat fmt = new SimpleDateFormat(format);
            return fmt.format(new Date());
        }

        /** {@inheritDoc} */
        @Override
        public void detach()
        {
            // detach inner model
            tileNode.detach();

            super.detach();
        }

    }

}