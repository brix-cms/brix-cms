package brix.demo.web;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.wicket.Page;
import org.apache.wicket.request.target.coding.HybridUrlCodingStrategy;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.string.Strings;

/**
 * Hybrid strategy that encodes parameters into the query string instead of the request path.
 * 
 * @author igor.vaynberg
 */
public class QueryStringHybridUrlCodingStrategy extends HybridUrlCodingStrategy
{

    /** {@inheritDoc} */
    public QueryStringHybridUrlCodingStrategy(String mountPath, Class< ? extends Page> pageClass,
            boolean redirectOnBookmarkableRequest)
    {
        super(mountPath, pageClass, redirectOnBookmarkableRequest);
    }

    /** {@inheritDoc} */
    public QueryStringHybridUrlCodingStrategy(String mountPath, Class< ? extends Page> pageClass)
    {
        super(mountPath, pageClass);
    }

    /**
     * Encodes Map into a url fragment and append that to the provided url buffer.
     * 
     * @param url
     *            url so far
     * 
     * @param parameters
     *            Map object to be encoded
     */
    @Override
    protected void appendParameters(AppendingStringBuffer url, Map< ? , ? > parameters)
    {
        if (parameters != null && parameters.size() > 0)
        {
            for (Entry< ? , ? > entry1 : parameters.entrySet())
            {
                Object value = ((Entry< ? , ? >)entry1).getValue();
                if (value != null)
                {
                    if (value instanceof String[])
                    {
                        String[] values = (String[])value;
                        for (String value1 : values)
                        {
                            appendValue(url, ((Entry< ? , ? >)entry1).getKey().toString(), value1);
                        }
                    }
                    else
                    {
                        appendValue(url, ((Entry< ? , ? >)entry1).getKey().toString(), value
                                .toString());
                    }
                }
            }
        }
    }

    private void appendValue(AppendingStringBuffer url, String key, String value)
    {
        final String escapedKey = urlEncodePathComponent(key);
        final String escapedValue = urlEncodePathComponent(value);

        if (!Strings.isEmpty(escapedValue))
        {
            url.append((url.indexOf("?") < 0) ? "?" : "&");
            url.append(escapedKey).append("=").append(escapedValue);
        }
    }
}
