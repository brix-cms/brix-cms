package brix.demo.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Map.Entry;

public class PropertyUtils
{
    /**
     * Loads properties from a classpath resource
     * 
     * @param props
     * @param resource
     * @param throwExceptionIfNotFound
     * @return loaded properties
     */
    public static Properties loadFromClassPath(String resource, boolean throwExceptionIfNotFound)
    {
        URL url = PropertyUtils.class.getClassLoader().getResource(resource);
        if (url == null)
        {
            if (throwExceptionIfNotFound)
            {
                throw new IllegalStateException("could not find classpath poperties resource: " +
                    resource);
            }
            else
            {
                return new Properties();
            }
        }
        try
        {
            Properties props = new Properties();
            InputStream is = url.openStream();
            try
            {
                props.load(url.openStream());
            }
            finally
            {
                is.close();
            }
            return props;
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not read properties at classpath resource: " +
                resource, e);
        }
    }

    public static enum MergeMode {
        OVERRIDE_ONLY,
        MERGE
    }

    public static Properties merge(MergeMode mode, Properties... sources)
    {
        Properties props = new Properties();

        for (int i = 0; i < sources.length; i++)
        {
            final Properties source = sources[i];
            for (Entry<Object, Object> prop : source.entrySet())
            {
                final boolean exists = props.containsKey(prop.getKey());
                boolean set = false;
                switch (mode)
                {
                    case MERGE :
                        set = true;
                        break;
                    case OVERRIDE_ONLY :
                        set = exists;
                        break;
                }
                if (set || i == 0)
                {
                    props.put(prop.getKey(), prop.getValue());
                }
            }
        }
        return props;
    }

    public static void merge(Properties p1, Properties p2)
    {
        for (Entry<Object, Object> p : p2.entrySet())
        {
            p1.put(p.getKey(), p.getValue());
        }
    }
}
