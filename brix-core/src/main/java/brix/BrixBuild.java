package brix;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Brix build properties
 * 
 * @author ivaynberg
 * 
 */
public class BrixBuild
{
    private static final String NUMBER = "build.number";
    private static final String FILE = "META-INF/brix-build.properties";

    /** build number */
    private String number;

    /** singleton instance */
    private static BrixBuild instance;

    /**
     * @return instance of {@link BrixBuild} singleton
     */
    public static synchronized BrixBuild instance()
    {
        if (instance == null)
        {
            instance = new BrixBuild();
        }
        return instance;
    }

    private BrixBuild()
    {
        InputStream is = getClass().getClassLoader().getResourceAsStream(FILE);
        if (is != null)
        {
            Properties props = new Properties();
            try
            {
                props.load(is);
                is.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException("Could not read " + FILE);
            }

            number = props.getProperty(NUMBER);
            if (number == null || NUMBER.equals(number))
            {
                number = "unknown";
            }
        }
    }

    /**
     * @return build number
     */
    public String getNumber()
    {
        return number;
    }


}
