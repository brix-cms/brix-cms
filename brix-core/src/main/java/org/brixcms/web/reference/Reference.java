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

package org.brixcms.web.reference;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.Strings;
import org.brixcms.BrixNodeModel;
import org.brixcms.jcr.api.JcrNodeIterator;
import org.brixcms.jcr.api.JcrValue;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.web.nodepage.BrixNodeRequestHandler;
import org.brixcms.web.nodepage.BrixPageParameters;

public class Reference implements Serializable, IDetachable {
    private static final long serialVersionUID = 1L;

    private IModel<BrixNode> nodeModel;
    private String url;
    private BrixPageParameters parameters;
    private Type type = Type.NODE;

    /**
     * Saves the reference in the parent node. If {@code null} is passed in for the reference any existing one will be
     * removed.
     *
     * @param parent        parent node
     * @param referenceName reference name
     * @param reference     reference to save or {@code null} to remove an existing one
     */
    public static void save(BrixNode parent, String referenceName, Reference reference) {
        if (reference == null) {
            // handle removal of a reference
            if (parent.hasNode(referenceName)) {
                parent.getNode(referenceName).remove();
            }
        } else {
            reference.save(parent, referenceName);
        }
    }

    public static Reference loadOrNull(BrixNode node, String property) {
        if (node.hasNode(property)) {
            Reference ref = new Reference();
            ref.load((BrixNode) node.getNode(property));
            return ref;
        } else {
            return null;
        }
    }

    public void load(BrixNode node) {
        setType(Type.valueOf(node.getProperty("type").getString()));
        if (node.hasProperty("node")) {
            setNodeModel(new BrixNodeModel((BrixNode) node.getProperty("node").getNode()));
        }
        if (node.hasProperty("url")) {
            setUrl(node.getProperty("url").getString());
        }
        if (node.hasProperty("indexedParameters")) {
            JcrValue values[] = node.getProperty("indexedParameters").getValues();
            getParameters().clearIndexed();
            for (int i = 0; i < values.length; ++i) {
                getParameters().set(i, values[i].getString());
            }
        }
        if (node.hasNode("parameter")) {
            getParameters().clearNamed();
            JcrNodeIterator i = node.getNodes("parameter");
            while (i.hasNext()) {
                BrixNode n = (BrixNode) i.nextNode();
                if (n.hasProperty("key") && n.hasProperty("values")) {
                    String key = n.getProperty("key").getString();
                    JcrValue values[] = n.getProperty("values").getValues();
                    for (JcrValue v : values) {
                        getParameters().set(key, v.getString());
                    }
                }
            }
        }
    }

    public static Reference load(BrixNode node, String property) {
        Reference ref = new Reference();
        if (node.hasNode(property)) {
            ref.load((BrixNode) node.getNode(property));
        }
        return ref;
    }

    public Reference() {

    }

    public Reference(Reference copy) {
        this.type = copy.type;
        if (copy.nodeModel != null)
            this.nodeModel = new BrixNodeModel(copy.nodeModel.getObject());
        this.url = copy.url;

        if (copy.parameters != null)
            this.parameters = new BrixPageParameters(copy.parameters);
    }

    public IModel<BrixNode> getNodeModel() {
        if (nodeModel == null) {
            nodeModel = new BrixNodeModel();
        }
        return nodeModel;
    }

    public void setNodeModel(IModel<BrixNode> nodeModel) {
        this.nodeModel = nodeModel;
    }

    public BrixPageParameters getParameters() {
        if (parameters == null) {
            parameters = new BrixPageParameters();
        }
        return parameters;
    }

    public void setParameters(BrixPageParameters parameters) {
        this.parameters = parameters;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        if (type == null) {
            throw new IllegalArgumentException("Argument 'type' may not be null.");
        }
        this.type = type;
    }

    public final String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public void detach() {
        if (nodeModel != null) {
            nodeModel.detach();
        }
    }

    /**
     * Generates a url that points to the resource this reference is holding.
     *
     * @return url to referenced resource
     */
    public String generateUrl() {
        if (isEmpty()) {
            return "";
        } else {
            if (Type.URL == type && hasProtocol()) {
                // referenced resource is an absolute url, return as is
                return url;
            } else {
                // generate url to referenced resource
                String url = RequestCycle.get().urlFor(getRequestTarget()).toString();
                return url;
            }
        }
    }

    /**
     * Checks if the url this reference points to includes a protocol. Should only be called if reference type is {@value
     * Type#URL}
     *
     * @return true if the url contains a protocol
     */
    private boolean hasProtocol() {
        if (Strings.isEmpty(url)) {
            return false;
        } else {
            return url.indexOf("://") > 0;
        }
    }

    public IRequestHandler getRequestTarget() {
        final IModel<BrixNode> model = getNodeModel();
        return new BrixNodeRequestHandler(model != null ? model : new BrixNodeModel("invalidId",
                "invalidWorkspace"), parameters != null ? parameters : new BrixPageParameters()) {
            @Override
            public String getNodeURL() {
                if (getType() == Type.NODE) {
                    return model != null ? super.getNodeURL() : "";
                } else {
                    return getUrl();
                }
            }
        };
    }

    public void makeEmpty() {
        if (type == Type.URL) {
            setUrl(null);
        } else if (type == Type.NODE) {
            getNodeModel().setObject(null);
        }
    }

    public void save(BrixNode node) {
        BrixNode brixNode = (BrixNode) node;

        brixNode.setHidden(true);

        node.setProperty("type", getType().toString());
        node.setProperty("url", getUrl());
        node.setProperty("node", getNodeModel().getObject());

        if (parameters != null) {
            if (parameters.getIndexedCount() > 0) {
                String array[] = new String[parameters.getIndexedCount()];
                for (int i = 0; i < array.length; ++i) {
                    array[i] = parameters.get(i).toString();
                }
                node.setProperty("indexedParameters", array);
            }
            if (parameters.getNamedKeys().size() > 0) {
                for (String s : parameters.getNamedKeys()) {
                    BrixNode param = (BrixNode) node.addNode("parameter", "nt:unstructured");
                    param.setProperty("key", s);
                    List<StringValue> values = parameters.getValues(s);
                    String valuesArray[] = new String[values.size()];
                    for (int i = 0; i < valuesArray.length; ++i) {
                        valuesArray[i] = values.get(i).toString();
                    }
                    param.setProperty("values", valuesArray);
                }
            }
        }
    }

    public void save(BrixNode parent, String property) {
        if (parent.hasNode(property)) {
            parent.getNode(property).remove();
        }
        if (isEmpty() == false || hasParameters()) {
            BrixNode child = (BrixNode) parent.addNode(property, "nt:unstructured");
            save(child);
        }
    }

    public boolean hasParameters() {
        if (parameters == null) {
            return false;
        } else {
            return parameters.getIndexedCount() > 0
                    && parameters.getNamedKeys().size() > 0;
        }
    }

    public boolean isEmpty() {
        if (type == Type.URL) {
            return Strings.isEmpty(getUrl());
        } else if (type == Type.NODE) {
            return getNodeModel().getObject() == null;
        } else {
            return false;
        }
    }

    public static enum Type {
        NODE, URL
    }
}
