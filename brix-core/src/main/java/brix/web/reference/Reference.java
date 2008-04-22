package brix.web.reference;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.Strings;

import brix.BrixNodeModel;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrNodeIterator;
import brix.jcr.api.JcrValue;
import brix.jcr.wrapper.BrixNode;
import brix.web.nodepage.BrixNodeRequestTarget;
import brix.web.nodepage.BrixPageParameters;

public class Reference implements Serializable, IDetachable
{

    private IModel<JcrNode> nodeModel;
    private String url;
    private BrixPageParameters parameters;

    public static enum Type {
        NODE,
        URL
    };

    private Type type = Type.NODE;

    public Reference()
    {

    }

    public Reference(Reference copy)
    {
        this.type = copy.type;
        if (copy.nodeModel != null)
            this.nodeModel = new BrixNodeModel(copy.nodeModel.getObject());
        this.url = copy.url;

        if (copy.parameters != null)
            this.parameters = new BrixPageParameters(copy.parameters);
    }

    public void setType(Type type)
    {
        if (type == null)
        {
            throw new IllegalArgumentException("Argument 'type' may not be null.");
        }
        this.type = type;
    }

    public Type getType()
    {
        return type;
    }

    public IModel<JcrNode> getNodeModel()
    {
        if (nodeModel == null)
        {
            nodeModel = new BrixNodeModel(null);
        }
        return nodeModel;
    }

    public void setNodeModel(IModel<JcrNode> nodeModel)
    {
        this.nodeModel = nodeModel;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public BrixPageParameters getParameters()
    {
        if (parameters == null)
        {
            parameters = new BrixPageParameters();
        }
        return parameters;
    }

    public void setParameters(BrixPageParameters parameters)
    {
        this.parameters = parameters;
    }

    public IRequestTarget getRequestTarget()
    {
        final IModel<JcrNode> model = getNodeModel();
        return new BrixNodeRequestTarget(model != null ? model : new BrixNodeModel("invalidId",
                "invalidWorkspace"), parameters != null ? parameters : new BrixPageParameters())
        {
            @Override
            public String getNodeURL()
            {
                if (getType() == Type.NODE)
                {
                    return model != null ? super.getNodeURL() : "";
                }
                else
                {
                    return getUrl();
                }
            }
        };
    }

    public void save(JcrNode parent, String property)
    {
        if (parent.hasNode(property))
        {
            parent.getNode(property).remove();
        }
        if (isEmpty() == false || hasParameters())
        {
            JcrNode child = parent.addNode(property, "nt:unstructured");
            save(child);
        }
    }

    public void save(JcrNode node)
    {

        BrixNode brixNode = (BrixNode)node;

        brixNode.setHidden(true);

        node.setProperty("type", getType().toString());
        node.setProperty("url", getUrl());
        node.setProperty("node", getNodeModel().getObject());

        if (parameters != null)
        {
            if (parameters.getIndexedParamsCount() > 0)
            {
                String array[] = new String[parameters.getIndexedParamsCount()];
                for (int i = 0; i < array.length; ++i)
                {
                    array[i] = parameters.getIndexedParam(i).toString();
                }
                node.setProperty("indexedParameters", array);
            }
            if (parameters.getQueryParamKeys().size() > 0)
            {
                for (String s : parameters.getQueryParamKeys())
                {
                    JcrNode param = node.addNode("parameter", "nt:unstructured");
                    param.setProperty("key", s);
                    List<StringValue> values = parameters.getQueryParams(s);
                    String valuesArray[] = new String[values.size()];
                    for (int i = 0; i < valuesArray.length; ++i)
                    {
                        valuesArray[i] = values.get(i).toString();
                    }
                    param.setProperty("values", valuesArray);
                }
            }
        }
    }

    public static Reference load(JcrNode node, String property)
    {
        Reference ref = new Reference();
        if (node.hasNode(property))
        {
            ref.load(node.getNode(property));
        }
        return ref;
    }

    public void load(JcrNode node)
    {
        setType(Type.valueOf(node.getProperty("type").getString()));
        if (node.hasProperty("node"))
        {
            setNodeModel(new BrixNodeModel(node.getProperty("node").getNode()));
        }
        if (node.hasProperty("url"))
        {
            setUrl(node.getProperty("url").getString());
        }
        if (node.hasProperty("indexedParameters"))
        {
            JcrValue values[] = node.getProperty("indexedParameters").getValues();
            getParameters().clearIndexedParams();
            for (int i = 0; i < values.length; ++i)
            {
                getParameters().setIndexedParam(i, values[i].getString());
            }
        }
        if (node.hasNode("parameter"))
        {
            getParameters().clearQueryParams();
            JcrNodeIterator i = node.getNodes("parameter");
            while (i.hasNext())
            {
                JcrNode n = i.nextNode();
                if (n.hasProperty("key") && n.hasProperty("values"))
                {
                    String key = n.getProperty("key").getString();
                    JcrValue values[] = n.getProperty("values").getValues();
                    for (JcrValue v : values)
                    {
                        getParameters().addQueryParam(key, v.getString());
                    }
                }
            }
        }
    }

    public void detach()
    {
        if (nodeModel != null)
        {
            nodeModel.detach();
        }
    }

    public boolean isEmpty()
    {
        if (type == Type.URL)
        {
            return Strings.isEmpty(getUrl());
        }
        else if (type == Type.NODE)
        {
            return getNodeModel().getObject() == null;
        }
        else
        {
            return false;
        }
    }

    public boolean hasParameters()
    {
        if (parameters == null)
        {
            return false;
        }
        else
        {
            return parameters.getIndexedParamsCount() > 0 &&
                    parameters.getQueryParamKeys().size() > 0;
        }
    }

    public String generateUrl()
    {
        if (isEmpty())
        {
            return "";
        }
        else
        {
            String url = RequestCycle.get().urlFor(getRequestTarget()).toString();
            return url;
        }
    }
}
