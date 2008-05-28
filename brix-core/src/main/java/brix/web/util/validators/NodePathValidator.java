package brix.web.util.validators;

import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import brix.Path;
import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SitePlugin;

public class NodePathValidator implements IValidator
{

    private final IModel<BrixNode> nodeModel;

    public NodePathValidator(IModel<BrixNode> nodeModel)
    {
        this.nodeModel = nodeModel;
    }


    @SuppressWarnings("unchecked")
    public void validate(IValidatable validatable)
    {
        Object o = validatable.getValue();
        if (o != null)
        {
            JcrNode node = nodeModel.getObject();
            Path path = null;
            if (o instanceof Path)
            {
                path = (Path)o;
            }
            else
            {
                path = new Path(o.toString());
            }

            if (!path.isAbsolute())
            {
                Path parent = new Path(node.getPath());
                if (!((BrixNode)node).isFolder())
                    parent = parent.parent();
                path = parent.append(path);
            }
            else
            {
                path = new Path(SitePlugin.get().toRealWebNodePath(path.toString()));
            }
            if (node.getSession().itemExists(path.toString()) == false)
            {
                ValidationError error = new ValidationError();
                error.setMessage("Node ${path} could not be found");
                error.addMessageKey("NodePathValidator");
                error.getVariables().put("path", path.toString());
                validatable.error(error);
            }
        }

    }

}
