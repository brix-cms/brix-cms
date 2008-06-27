/**
 * 
 */
package brix.web.picker.reference;

import java.io.Serializable;

import org.apache.wicket.model.IModel;

import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixNode;
import brix.web.tree.NodeFilter;

public class ReferenceEditorConfiguration implements Serializable
{

    private boolean allowNodePicker = true;
    private boolean allowURLEdit = true;
    private boolean allowIndexedParameters = true;
    private boolean allowQueryParameters = true;
    private boolean displayFiles = true;
    private String workspaceName;

    private NodeFilter nodeFilter;

    public boolean isAllowNodePicker()
    {
        return allowNodePicker;
    }

    public ReferenceEditorConfiguration setAllowNodePicker(boolean allowNodePicker)
    {
        this.allowNodePicker = allowNodePicker;
        return this;
    }

    public boolean isAllowURLEdit()
    {
        return allowURLEdit;
    }

    public ReferenceEditorConfiguration setAllowURLEdit(boolean allowURLEdit)
    {
        this.allowURLEdit = allowURLEdit;
        return this;
    }

    public boolean isAllowIndexedParameters()
    {
        return allowIndexedParameters;
    }

    public ReferenceEditorConfiguration setAllowIndexedParameters(boolean allowIndexedParameters)
    {
        this.allowIndexedParameters = allowIndexedParameters;
        return this;
    }

    public boolean isAllowQueryParameters()
    {
        return allowQueryParameters;
    }

    public ReferenceEditorConfiguration setAllowQueryParameters(boolean allowQueryParameters)
    {
        this.allowQueryParameters = allowQueryParameters;
        return this;
    }

    public NodeFilter getNodeFilter()
    {
        return nodeFilter;
    }

    public ReferenceEditorConfiguration setNodeFilter(NodeFilter nodeFilter)
    {
        this.nodeFilter = nodeFilter;
        return this;
    }

    public ReferenceEditorConfiguration setDisplayFiles(boolean displayFiles)
    {
        this.displayFiles = displayFiles;
        return this;
    }

    public boolean isDisplayFiles()
    {
        return displayFiles;
    }

    public ReferenceEditorConfiguration setWorkspaceName(IModel<BrixNode> nodeModel)
    {
        return setWorkspaceName(nodeModel.getObject().getSession().getWorkspace().getName());
    }

    public ReferenceEditorConfiguration setWorkspaceName(JcrNode node)
    {
        return setWorkspaceName(node.getSession().getWorkspace().getName());
    }


    public ReferenceEditorConfiguration setWorkspaceName(String workspaceName)
    {
        this.workspaceName = workspaceName;
        return this;
    }

    public String getWorkspaceName()
    {
        return workspaceName;
    }
}