package brix.plugin.user;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import brix.workspace.Workspace;


/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date Mar 10, 2009 4:21:27 PM
 */
public class UserPanel extends Panel {

    public UserPanel(String panelId, IModel<Workspace> workspaceModel) {
        super(panelId, workspaceModel);
    }
}
