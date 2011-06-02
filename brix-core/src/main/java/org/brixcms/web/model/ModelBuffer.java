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

package org.brixcms.web.model;

import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.brixcms.BrixNodeModel;
import org.brixcms.jcr.wrapper.BrixNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Proxy a BrixNode model object, exposing lightweight transactional semantics through the {@link
 * org.brixcms.web.model.ModelBuffer.Model#apply} method. Properties of a target model are exposed as models themselves
 * and maintain status regarding whether changes have been made. When the apply method is called, the changes in the
 * cached models are iterated and flushed.
 * <p/>
 * This is typically used by clients that wish to present a cancelable user interface, without maintaining the original
 * state of target model object. If the user cancels the operation, the buffer is deallocated without ever applying the
 * changes to the target.
 * <p/>
 * After creating a ModelBuffer, use {@link ModelBuffer#forProperty(java.lang.String)} and {@link
 * ModelBuffer#forNodeProperty(java.lang.String)} to create and return these cached model objects. These can be provided
 * to user interface objects.
 * <p/>
 * {@link org.brixcms.plugin.site.page.admin.EditTab#EditTab(java.lang.String, org.apache.wicket.model.IModel<
 * org.brixcms.jcr.wrapper.BrixNode>)} provides a solid use case of this class.
 */
public class ModelBuffer implements IModel<Object> {
    private Object target;

    private Map<String, IModel> propertyMap;

    private List<Model> models = null;

    public ModelBuffer() {

    }

    /**
     * Constructor requiring a BrixNode to proxy.
     *
     * @param target
     */
    public ModelBuffer(Object target) {
        this.target = target;
    }



    public void detach() {
        if (target != null && target instanceof IModel) {
            ((IModel<?>) target).detach();
        }
    }

    public Object getObject() {
        return target;
    }

    public void setObject(Object object) {
        target = object;
    }

    public void apply() {
        if (models != null) {
            for (Model model : models) {
                model.apply();
            }
        }
    }

    /**
     * Buffer a node property for the target object.
     *
     * @param propertyName The JCR key name of the underlying property
     * @return model A model that buffers the requested property
     */
    public <T> IModel<T> forNodeProperty(String propertyName) {
        return forProperty(propertyName, true);
    }

    /**
     * Backing method for public forProperty API calls. Property buffers are cached by name, returning an existing buffer
     * if it was previously created.
     *
     * @param propertyName The JCR key name of the underlying property
     * @param isNode       Selector for string type or JcrNode type
     * @return model A model that buffers the requested property
     */
    @SuppressWarnings("unchecked")
    protected <T> IModel<T> forProperty(String propertyName, boolean isNode) {
        if (propertyMap != null && propertyMap.containsKey(propertyName)) {
            return propertyMap.get(propertyName);
        }
        IModel model = forModel(new PropertyModel(this, propertyName), isNode);
        if (propertyMap == null) {
            propertyMap = new HashMap<String, IModel>();
        }
        propertyMap.put(propertyName, model);
        return model;
    }

    /**
     * Create a proxy for a node property. In contrast to {@link org.brixcms.web.model.ModelBuffer#forProperty}, this
     * method always creates the buffered model.
     *
     * @param delegate Accessor to the underlying targetObject, typically a PropertyModel
     * @param isNode   if the datatype is a JcrNode
     * @return
     */
    public IModel forModel(IModel delegate, boolean isNode) {
        if (models == null) {
            models = new ArrayList<Model>();
        }
        Model model;
        if (!isNode)
            model = new Model(delegate);
        else
            model = new NodeModel(delegate);

        models.add(model);
        return model;
    }

    /**
     * Buffer a string property for the target object.
     *
     * @param propertyName The JCR key name of the underlying property
     * @return model A model that buffers the requested property
     */
    public <T> IModel<T> forProperty(String propertyName) {
        return forProperty(propertyName, false);
    }

    /**
     * Internal storage type for non-JcrNode properties
     */
    private static class Model implements IModel {
        private final IModel delegate;
        private Object value;
        private boolean valueSet = false;

        public Model(IModel delegate) {
            this.delegate = delegate;
        }

        public Object getObject() {
            if (valueSet) {
                return value;
            } else {
                return delegate.getObject();
            }
        }

        public void setObject(Object object) {
            valueSet = true;
            value = object;
        }

        public void detach() {
            delegate.detach();
            if (value instanceof IDetachable) {
                ((IDetachable) value).detach();
            }
        }

        protected void apply(IModel delegate, Object value) {
            delegate.setObject(value);
        }

        public void apply() {
            if (valueSet) {
                apply(delegate, value);
                valueSet = false;
            }
        }
    }

    /**
     * Internal storage type for JcrNode properties
     */
    private static class NodeModel extends Model {
        public NodeModel(IModel delegate) {
            super(delegate);
        }

        @Override
        public Object getObject() {
            Object value = super.getObject();
            if (value instanceof IModel) {
                return ((IModel) value).getObject();
            } else {
                return value;
            }
        }

        @Override
        public void setObject(Object object) {
            if (object instanceof BrixNode) {
                super.setObject(new BrixNodeModel((BrixNode) object));
            } else {
                super.setObject(object);
            }
        }

        @Override
        protected void apply(IModel delegate, Object value) {
            if (value instanceof IModel) {
                value = ((IModel) value).getObject();
            }
            super.apply(delegate, value);
        }
    }
}
