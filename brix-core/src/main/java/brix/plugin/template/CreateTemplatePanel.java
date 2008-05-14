package brix.plugin.template;

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


public class CreateTemplatePanel extends SelectItemsPanel<Void>
{

    public CreateTemplatePanel(String id, String workspaceName, final String targetTemplateName)
    {
        super(id, workspaceName);

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
                        TemplatePlugin.get().createTemplate(nodes, targetTemplateName);
                        findParent(ModalWindow.class).close(target);
                    }                    
                }
                else
                {
                    message.setModelObject("You have to select at least one node.");
                }
                target.addComponent(message);
            }
        });
    }
    
}
