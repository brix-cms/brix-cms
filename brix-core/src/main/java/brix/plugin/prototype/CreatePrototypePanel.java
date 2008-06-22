package brix.plugin.prototype;

import java.util.List;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.model.Model;

import brix.jcr.JcrUtil;
import brix.jcr.api.JcrNode;


public class CreatePrototypePanel extends SelectItemsPanel<Void>
{

    public CreatePrototypePanel(String id, String workspaceId, final String targetPrototypeName)
    {
        super(id, workspaceId);

        final Component<String> message = new MultiLineLabel<String>("message", new Model<String>(
            ""));
        message.setOutputMarkupId(true);
        add(message);

        add(new AjaxLink<Void>("create")
        {
            @Override
            public void onClick(AjaxRequestTarget target)
            {
                List<JcrNode> nodes = getSelectedNodes();
                if (!nodes.isEmpty())
                {
                    Map<JcrNode, List<JcrNode>> dependencies = JcrUtil.getUnsatisfiedDependencies(
                        nodes, null);
                    if (!dependencies.isEmpty())
                    {
                        message.setModelObject(getDependenciesMessage(dependencies));                        
                    }
                    else
                    {
                        PrototypePlugin.get().createPrototype(nodes, targetPrototypeName);
                        findParent(ModalWindow.class).close(target);
                    }                    
                }
                else
                {
                    message.setModelObject(getString("youHaveToSelectAtLeastOneNode"));
                }
                target.addComponent(message);
            }
        });
    }
    
}
