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

package brix.jcr.wrapper;

import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.resource.ResourceNodePlugin;
import org.apache.wicket.util.io.Streams;
import org.apache.wicket.util.string.Strings;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

/**
 * Base class for nodes with content (with JCR primary type nt:file).
 * 
 * @see #initialize(JcrNode, String)
 * 
 * @author Matej Knopp
 */
public class BrixFileNode extends BrixNode
{

	/**
	 * Wraps the given delegate node using provided {@link JcrSession}.
	 * 
	 * @param delegate
	 * @param session
	 */
	public BrixFileNode(Node delegate, JcrSession session)
	{
		super(delegate, session);
	}

	/**
	 * Returns if the node is a file node,
	 * 
	 * @param node
	 * @return <code>true</code> if the node is a file node, <code>false</code>
	 *         otherwise
	 */
	public static boolean isFileNode(JcrNode node)
	{
		if (!node.getPrimaryNodeType().getName().equals("nt:file"))
		{
			return false;
		}

		return node.hasNode("jcr:content");
	}

	private JcrNode getContent()
	{
		return (JcrNode)getPrimaryItem();
	}

	/**
	 * Sets the encoding property
	 * 
	 * @param encoding
	 */
	public void setEncoding(String encoding)
	{
		getContent().setProperty("jcr:encoding", encoding);
	}

	/**
	 * Returns the encoding property
	 * 
	 * @return
	 */
	public String getEncoding()
	{
		return getContent().hasProperty("jcr:encoding") ? getContent().getProperty("jcr:encoding")
				.getString() : null;
	}

	/**
	 * Sets the mime type property
	 * 
	 * @param mimeType
	 */
	public void setMimeType(String mimeType)
	{
		getContent().setProperty("jcr:mimeType", mimeType);
	}

	/**
	 * Returns the mime type property. If the property is not specified, tries
	 * to determine mime type from node name extension.
	 * 
	 * @return
	 */
	public String getMimeType()
	{
		return getMimeType(true);
	}

	/**
	 * Returns the mime type for this node. If the property is not specified and
	 * <code>useExtension</code> is <code>true</code>, tries to determine mime
	 * type from extension.
	 * 
	 * @param useExtension
	 * @return
	 */
	public String getMimeType(boolean useExtension)
	{
		// FIXME Shouldn't have direct dependency on SitePlugin

		String mime = getContent().getProperty("jcr:mimeType").getString();
		if (useExtension && (Strings.isEmpty(mime) || mime.equals("application/octet-stream")))
		{
			ResourceNodePlugin plugin = (ResourceNodePlugin)SitePlugin.get(getBrix())
					.getNodePluginForType(ResourceNodePlugin.TYPE);
			return plugin.resolveMimeTypeFromFileName(getName());
		}
		return mime;
	}

	/**
	 * Returns the length of content in bytes
	 * 
	 * @return
	 */
	public long getContentLength()
	{
		return getContent().getProperty("jcr:data").getLength();
	}

	/**
	 * Sets the actual data of this node
	 * 
	 * @param data
	 */
	public void setData(String data)
	{
		if (data == null)
		{
			data = "";
		}
		setEncoding("UTF-8");
		getContent().setProperty("jcr:data", data);
	}

	/**
	 * Sets the actual data of this node. Provided as complementary setter for
	 * {@link #getDataAsString()}.
	 * 
	 * @param data
	 */
	public void setDataAsString(String data)
	{
		setData(data);
	}

	/**
	 * Sets the actual data of this node
	 * 
	 * @param data
	 */
	public void setData(Binary data)
	{
		getContent().setProperty("jcr:data", data);
	}

	/**
	 * Returns the data of this node as string
	 * 
	 * @return
	 */
	public String getDataAsString()
	{
		return getContent().getProperty("jcr:data").getString();
	}

	/**
	 * Returns the data of this node as stream
	 * 
	 * @return
	 */
	public InputStream getDataAsStream()
	{
        try {
            return getContent().getProperty("jcr:data").getBinary().getStream();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

	/**
	 * Writes the node data to the specified output stream.
	 * 
	 * @param stream
	 * @throws IOException
	 */
	public void writeData(OutputStream stream) throws IOException
	{
		Streams.copy(getDataAsStream(), stream);
	}

	/**
	 * Initializes the specified node to be a valid file node. The node's
	 * primary type must be nt:file.
	 * 
	 * @param node
	 * @param mimeType
	 * @return
	 */
	public static BrixFileNode initialize(JcrNode node, String mimeType)
	{
		if (node.isNodeType("nt:file") == false)
		{
			throw new IllegalStateException("Argument 'node' must have JCR type nt:file.");
		}
		else if (node instanceof BrixFileNode)
		{
			return (BrixFileNode)node;
		}
		node.addNode("jcr:content", "nt:resource");
		BrixFileNode wrapped = new BrixFileNode(node.getDelegate(), node.getSession());
		wrapped.setMimeType(mimeType);
		wrapped.getContent().setProperty("jcr:lastModified", Calendar.getInstance());
		wrapped.getContent().setProperty("jcr:data", "");
		return wrapped;
	}

	public static boolean isText(String mimeType)
	{
		if (Strings.isEmpty(mimeType))
		{
			return false;
		}
		if (mimeType.equals("text")||mimeType.startsWith("text/"))
		{
			return true;
		}
		if ("application/xml".equals(mimeType))
		{
			return true;
		}
		return false;
	}

	public static boolean isText(BrixFileNode node)
	{
		if (node == null)
		{
			throw new IllegalArgumentException("Argument 'node' cannot be null");
		}
		return isText(node.getMimeType());
	}
}
