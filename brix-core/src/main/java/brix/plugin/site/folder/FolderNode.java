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

package brix.plugin.site.folder;

import javax.jcr.Node;

import brix.Brix;
import brix.jcr.JcrNodeWrapperFactory;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixNode;
import brix.web.reference.Reference;

public class FolderNode extends BrixNode
{

    /**
     * NodeWrapperFactory that can create {@link FolderNode} wrappers
     */
    public static final JcrNodeWrapperFactory FACTORY = new JcrNodeWrapperFactory()
    {

        /** {@inheritDoc} */
        @Override
        public boolean canWrap(Brix brix, JcrNode node)
        {
            return node.isNodeType("nt:folder");
        }

        /** {@inheritDoc} */
        @Override
        public JcrNode wrap(Brix brix, Node node, JcrSession session)
        {
            return new FolderNode(node, session);
        }

    };


    public FolderNode(Node delegate, JcrSession session)
    {
        super(delegate, session);
    }

    private static final String REDIRECT_REFERENCE = FolderNodePlugin.TYPE + "RedirectReference";

    public void setRedirectReference(Reference reference)
    {
        ensureType();
        if (reference == null)
        {
            reference = new Reference();
        }
        reference.save(this, REDIRECT_REFERENCE);
    }

    public Reference getRedirectReference()
    {
        return Reference.load(this, REDIRECT_REFERENCE);
    }
    
    private void ensureType()
    {
        if (!isNodeType(FolderNodePlugin.TYPE))
        {
            addMixin(FolderNodePlugin.TYPE);
        }
    }

    @Override
    public String getUserVisibleType()
    {
    	return "Folder";
    }
}
