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

package brix.web.nodepage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.target.component.IPageRequestTarget;
import org.apache.wicket.util.lang.Objects;
import org.apache.wicket.util.string.StringValue;

import brix.exception.BrixException;
import brix.jcr.wrapper.BrixNode;

public class BrixPageParameters implements Serializable
{
    private static final long serialVersionUID = 1L;

    public BrixPageParameters()
    {

    }

    public BrixPageParameters(PageParameters params)
    {
        if (params != null)
        {
            for (String name : params.keySet())
            {
                addQueryParam(name, params.get(name));
            }
        }
    }

    public BrixPageParameters(BrixPageParameters copy)
    {
        if (copy == null)
        {
            throw new IllegalArgumentException("Copy argument may not be null.");
        }
        if (copy.indexedParameters != null)
            this.indexedParameters = new ArrayList<String>(copy.indexedParameters);

        if (copy.queryStringParameters != null)
            this.queryStringParameters = new ArrayList<QueryStringParameter>(
                    copy.queryStringParameters);
    }

    private List<String> indexedParameters = null;

    public static class QueryStringParameter implements Serializable
    {
        private static final long serialVersionUID = 1L;

        private final String key;
        private final String value;

        public QueryStringParameter(String key, String value)
        {
            this.key = key;
            this.value = value;
        }

        public String getKey()
        {
            return key;
        }

        public String getValue()
        {
            return value;
        }
    };

    private List<QueryStringParameter> queryStringParameters = null;

    public int getIndexedParamsCount()
    {
        return indexedParameters != null ? indexedParameters.size() : 0;
    }

    public void setIndexedParam(int index, Object object)
    {
        if (indexedParameters == null)
            indexedParameters = new ArrayList<String>(index);

        for (int i = indexedParameters.size(); i <= index; ++i)
        {
            indexedParameters.add(null);
        }

        String value = object != null ? object.toString() : null;
        if (value!=null)
        {
        	value=value.replace("/","%2f");
        }
		indexedParameters.set(index, value);
    }

    public StringValue getIndexedParam(int index)
    {
        if (indexedParameters != null)
        {
            if (index >= 0 && index < indexedParameters.size())
            {
                String value = indexedParameters.get(index);
				if (value!=null)
				{
					value=value.replaceAll("%2f", "/");
				}
                return StringValue.valueOf(value);
            }
        }
        return StringValue.valueOf((String)null);
    };

    public void removeIndexedParam(int index)
    {
        if (indexedParameters != null)
        {
            if (index >= 0 && index < indexedParameters.size())
            {
                indexedParameters.remove(index);
            }
        }
    }

    public List<QueryStringParameter> getQueryStringParams()
    {
        if (queryStringParameters == null)
        {
            return Collections.emptyList();
        }
        else
        {
            return Collections.unmodifiableList(new ArrayList<QueryStringParameter>(
                    queryStringParameters));
        }
    }

    public Set<String> getQueryParamKeys()
    {
        if (queryStringParameters == null || queryStringParameters.isEmpty())
        {
            return Collections.emptySet();
        }
        Set<String> set = new TreeSet<String>();
        for (QueryStringParameter entry : queryStringParameters)
        {
            set.add(entry.key);
        }
        return Collections.unmodifiableSet(set);
    }

    public StringValue getQueryParam(String name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("Parameter name may not be null.");
        }
        if (queryStringParameters != null)
        {
            for (QueryStringParameter entry : queryStringParameters)
            {
                if (entry.key.equals(name))
                {
                    return StringValue.valueOf(entry.value);
                }
            }
        }
        return StringValue.valueOf((String)null);
    }

    public List<StringValue> getQueryParams(String name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("Parameter name may not be null.");
        }
        if (queryStringParameters != null)
        {
            List<StringValue> result = new ArrayList<StringValue>();
            for (QueryStringParameter entry : queryStringParameters)
            {
                if (entry.key.equals(name))
                {
                    result.add(StringValue.valueOf(entry.value));
                }
            }
            return Collections.unmodifiableList(result);
        }
        else
        {
            return Collections.emptyList();
        }
    }

    public void removeQueryParam(String name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("Parameter name may not be null.");
        }
        if (queryStringParameters != null)
        {
            for (Iterator<QueryStringParameter> i = queryStringParameters.iterator(); i.hasNext();)
            {
                QueryStringParameter e = i.next();
                if (e.key.equals(name))
                {
                    i.remove();
                }
            }
        }
    }

    public void addQueryParam(String name, Object value)
    {
        addQueryParam(name, value, -1);
    }

    public void addQueryParam(String name, Object value, int index)
    {

        if (name == null)
        {
            throw new IllegalArgumentException("Parameter name may not be null.");
        }

        if (value == null)
        {
            throw new IllegalArgumentException("Parameter value may not be null.");
        }

        if (queryStringParameters == null)
            queryStringParameters = new ArrayList<QueryStringParameter>(1);
        QueryStringParameter entry = new QueryStringParameter(name, value.toString());

        if (index == -1)
            queryStringParameters.add(entry);
        else
            queryStringParameters.add(index, entry);
    }

    public void setQueryParam(String name, Object value, int index)
    {
        removeQueryParam(name);

        if (value != null)
        {
            addQueryParam(name, value);
        }
    }

    public void setQueryParam(String name, Object value)
    {
        setQueryParam(name, value, -1);
    }

    public void clearIndexedParams()
    {
        this.indexedParameters = null;
    }

    public void clearQueryParams()
    {
        this.queryStringParameters = null;
    }

    void assign(BrixPageParameters other)
    {
        if (this != other)
        {
            this.indexedParameters = other.indexedParameters;
            this.queryStringParameters = other.queryStringParameters;
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj instanceof BrixPageParameters == false)
        {
            return false;
        }

        BrixPageParameters rhs = (BrixPageParameters)obj;
        if (!Objects.equal(indexedParameters, rhs.indexedParameters))
        {
            return false;
        }

        if (queryStringParameters == null || rhs.queryStringParameters == null)
        {
            return rhs.queryStringParameters == queryStringParameters;
        }

        if (queryStringParameters.size() != rhs.queryStringParameters.size())
        {
            return false;
        }

        for (String key : getQueryParamKeys())
        {
            List<StringValue> values1 = getQueryParams(key);
            Set<String> v1 = new TreeSet<String>();
            List<StringValue> values2 = rhs.getQueryParams(key);
            Set<String> v2 = new TreeSet<String>();
            for (StringValue sv : values1)
            {
                v1.add(sv.toString());
            }
            for (StringValue sv : values2)
            {
                v2.add(sv.toString());
            }
            if (v1.equals(v2) == false)
            {
                return false;
            }
        }
        return true;
    }

    static BrixNodeWebPage getCurrentPage()
    {
        IRequestTarget target = RequestCycle.get().getRequestTarget();
        BrixNodeWebPage page = null;
        if (target != null && target instanceof IPageRequestTarget)
        {
            Page p = ((IPageRequestTarget)target).getPage();
            if (p instanceof BrixNodeWebPage)
            {
                page = (BrixNodeWebPage)p;
            }
        }
        if (page == null)
        {
            throw new BrixException(
                    "Couldn't obtain the BrixNodeWebPage instance from RequestTarget.");
        }
        return page;
    }

    public static BrixPageParameters getCurrent()
    {
        IRequestTarget target = RequestCycle.get().getRequestTarget();
        // this is required for getting current page parameters from page constructor
        // (the actual page instance is not constructed yet.
        if (target instanceof PageParametersRequestTarget)
        {
            return ((PageParametersRequestTarget)target).getPageParameters();
        }
        else
        {
            return getCurrentPage().getBrixPageParameters();
        }
    }

    /**
     * Constructs a url to the specified page appending these page parameters
     * 
     * @param page
     * @return url
     */
    public String urlFor(IModel<BrixNode> node)
    {
        IRequestTarget target = new BrixNodeRequestTarget(node, this);
        return RequestCycle.get().urlFor(target).toString();
    }

    /**
     * Constructs a url to the specified page appending these page parameters
     * 
     * @param page
     * @return url
     */
    public String urlFor(BrixNodeWebPage page)
    {
        IRequestTarget target = new BrixNodeRequestTarget(page, this);
        return RequestCycle.get().urlFor(target).toString();
    }

    public String toCallbackURL()
    {
        return urlFor(getCurrentPage());
    }

    public static boolean equals(BrixPageParameters p1, BrixPageParameters p2)
    {
        if (Objects.equal(p1, p2))
        {
            return true;
        }
        if (p1 == null && p2.getIndexedParamsCount() == 0 && p2.getQueryParamKeys().isEmpty())
        {
            return true;
        }
        if (p2 == null && p1.getIndexedParamsCount() == 0 && p1.getQueryParamKeys().isEmpty())
        {
            return true;
        }
        return false;
    }
}
