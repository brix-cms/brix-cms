package brix.web.tile.pagetile;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import brix.Path;
import brix.jcr.api.JcrNode;
import brix.plugin.site.node.tilepage.admin.TileEditorPanel;
import brix.web.util.validators.NodePathValidator;

public class PageTileEditorPanel extends TileEditorPanel
{

    public PageTileEditorPanel(String id, IModel<JcrNode> tileContainerNode)
    {
        super(id, tileContainerNode);

        TextField tf;
        add(tf = new TextField("node", new PropertyModel(this, "node")));
        tf.setRequired(true);
        tf.add(new NodePathValidator(tileContainerNode));
    }

    private String node;

    @Override
    public void load(JcrNode node)
    {
        JcrNode pageNode = node.getProperty("pageNode").getNode();
        this.node = pageNode.getPath();
    }

    @Override
    public void save(JcrNode node)
    {
        Path path = new Path(this.node);
        if (path.isAbsolute() == false)
        {
            path = new Path(((JcrNode)getModelObject()).getPath()).parent().append(path);
        }
        JcrNode pageNode = (JcrNode)node.getSession().getItem(path.toString());
        node.setProperty("pageNode", pageNode);
    }

}
