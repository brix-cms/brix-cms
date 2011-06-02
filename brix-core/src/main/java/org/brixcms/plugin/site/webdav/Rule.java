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

package org.brixcms.plugin.site.webdav;

import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Objects;
import org.brixcms.BrixNodeModel;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.wrapper.BrixNode;

public class Rule implements IDetachable {
    private static final String PROPERTY_PATH_PREFIX = "pathPrefix";
    private static final String PROPERTY_PRIORITY = "priority";
    private static final String PROPERTY_TEMPLATE = "template";
    private static final String PROPERTY_TYPE = "type";
    private static final String PROPERTY_EXTENSIONS = "extension";
    private String pathPrefix;
    private int priority;
    private IModel<BrixNode> templateModel;
    private Type type;
    final private String name;
    private String extensions;

    public static Rule load(JcrNode node) {
        Rule r = new Rule(node.getName());

        if (node.hasProperty(PROPERTY_PATH_PREFIX))
            r.pathPrefix = node.getProperty(PROPERTY_PATH_PREFIX).getString();
        if (node.hasProperty(PROPERTY_PRIORITY))
            r.priority = (int) node.getProperty(PROPERTY_PRIORITY).getLong();
        if (node.hasProperty(PROPERTY_TEMPLATE))
            r.templateModel = new BrixNodeModel((BrixNode) node.getProperty(PROPERTY_TEMPLATE).getNode());
        if (node.hasProperty(PROPERTY_TYPE))
            r.type = Type.valueOf(node.getProperty(PROPERTY_TYPE).getString());
        if (node.hasProperty(PROPERTY_EXTENSIONS))
            r.extensions = node.getProperty(PROPERTY_EXTENSIONS).getString();

        return r;
    }

    public Rule(String name) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("Argument 'name' may not be null.");
        }
        this.name = name;
    }

    public String getExtensions() {
        return extensions;
    }

    public void setExtensions(String extensions) {
        this.extensions = extensions;
    }

    public String getName() {
        return name;
    }

    public String getPathPrefix() {
        return pathPrefix;
    }

    public void setPathPrefix(String pathPrefix) {
        this.pathPrefix = pathPrefix;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public IModel<BrixNode> getTemplateModel() {
        if (templateModel == null)
            templateModel = new BrixNodeModel();

        return templateModel;
    }

    public void setTemplateModel(IModel<BrixNode> templateModel) {
        this.templateModel = templateModel;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Rule == false) {
            return false;
        }
        Rule rhs = (Rule) obj;
        return Objects.equal(name, rhs.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : null;
    }


    public void detach() {
        if (templateModel != null) {
            templateModel.detach();
        }
    }

    public boolean matches(String path) {
        path = path.toLowerCase();

        if (getPathPrefix() != null && !path.startsWith(getPathPrefix().toLowerCase())) {
            return false;
        }

        if (extensions != null && extensions.length() > 0) {
            boolean found = false;
            for (String ext : extensions.toLowerCase().split(",")) {
                if (path.endsWith("." + ext.trim())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    public void save(JcrNode parent) {
        if (parent.hasNode(name)) {
            parent.getNode(name).remove();
        }
        JcrNode node = parent.addNode(name, "nt:unstructured");
        node.setProperty(PROPERTY_PATH_PREFIX, pathPrefix);
        node.setProperty(PROPERTY_PRIORITY, priority);
        node.setProperty(PROPERTY_TEMPLATE, templateModel != null ? templateModel.getObject() : null);
        node.setProperty(PROPERTY_TYPE, type != null ? type.toString() : null);
        node.setProperty(PROPERTY_EXTENSIONS, extensions);
    }

    public enum Type {
        PAGE, TEMPLATE
    }
}
