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

package org.brixcms.web.util.validators;

import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.brixcms.Path;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.SitePlugin;

public class NodePathValidator implements IValidator {
    private final IModel<BrixNode> nodeModel;

    public NodePathValidator(IModel<BrixNode> nodeModel) {
        this.nodeModel = nodeModel;
    }


    @SuppressWarnings("unchecked")
    public void validate(IValidatable validatable) {
        Object o = validatable.getValue();
        if (o != null) {
            JcrNode node = nodeModel.getObject();
            Path path = null;
            if (o instanceof Path) {
                path = (Path) o;
            } else {
                path = new Path(o.toString());
            }

            if (!path.isAbsolute()) {
                Path parent = new Path(node.getPath());
                if (!((BrixNode) node).isFolder())
                    parent = parent.parent();
                path = parent.append(path);
            } else {
                path = new Path(SitePlugin.get().toRealWebNodePath(path.toString()));
            }
            if (node.getSession().itemExists(path.toString()) == false) {
                ValidationError error = new ValidationError();
                error.setMessage("Node ${path} could not be found");
                error.addMessageKey("NodePathValidator");
                error.getVariables().put("path", path.toString());
                validatable.error(error);
            }
        }
    }
}
