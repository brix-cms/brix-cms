package brix.jcr.wrapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;

import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;

public class WrapperRegistry
{

    private final List<Class< ? extends BrixNode>> wrappers = new ArrayList<Class< ? extends BrixNode>>();

    public WrapperRegistry()
    {
        registerWrapper(BrixNode.class);
        registerWrapper(BrixResourceNode.class);
    }

    public void registerWrapper(Class< ? extends BrixNode> wrapper)
    {
        if (wrapper == null)
        {
            throw new IllegalArgumentException("Argument 'wrapper' may not be null.");
        }
        synchronized (wrappers)
        {
            wrappers.add(0, wrapper);
        }
    };

    private JcrNode wrap(Class< ? extends BrixNode> wrapper, Node node, JcrSession session)
    {
        final String msg = "Wrapper must have a non-default constructor with arguments (Node, JcrSession).";
        try
        {
            Constructor< ? extends BrixNode> constructor = wrapper.getConstructor(Node.class,
                    JcrSession.class);
            return (JcrNode)constructor.newInstance(node, session);
        }
        catch (InstantiationException e)
        {
            throw new IllegalStateException(msg);
        }
        catch (IllegalArgumentException e)
        {
            throw new IllegalStateException(msg);
        }
        catch (IllegalAccessException e)
        {
            throw new IllegalStateException(msg);
        }
        catch (InvocationTargetException e)
        {
            throw new IllegalStateException(msg);
        }
        catch (SecurityException e)
        {
            throw new IllegalStateException(msg);
        }
        catch (NoSuchMethodException e)
        {
            throw new IllegalStateException(msg);
        }
    }

    public JcrNode wrap(JcrNode node)
    {
        List<Class< ? extends BrixNode>> copy;
        synchronized (wrappers)
        {
            copy = new ArrayList<Class< ? extends BrixNode>>(wrappers);
        }
        for (Class< ? extends BrixNode> c : copy)
        {
            try
            {
                Method m = c.getMethod("canHandle", JcrNode.class);
                Boolean res = (Boolean)m.invoke(null, node);
                if (res == true)
                {
                    return wrap(c, node.getDelegate(), node.getSession());
                }
            }
            catch (Exception e)
            {
                throw new IllegalStateException(
                        "Wrapper must have a static canHandle(JcrNode node) method.", e);
            }
        }
        return null;
    }
}
