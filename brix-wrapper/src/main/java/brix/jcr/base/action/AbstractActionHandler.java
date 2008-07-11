package brix.jcr.base.action;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.lock.Lock;
import javax.jcr.version.Version;

public class AbstractActionHandler
{

	public void beforeNodeAdd(Node parent, String name, String primaryType) throws RepositoryException
	{

	}

	public void afterNodeAdd(Node node) throws RepositoryException
	{

	}

	public void beforeNodeRemove(Node node) throws RepositoryException
	{

	}

	public void beforeNodeChildNodesOrderChange(Node node) throws RepositoryException
	{

	}

	public void afterNodeChildNodesOrderChange(Node node) throws RepositoryException
	{

	}
	
	public void beforeItemRefresh(Item item, boolean keepChanges) throws RepositoryException
	{
		
	}
	
	public void afterItemRefresh(Item item, boolean keepChanges) throws RepositoryException
	{
		
	}
	
	public void beforeItemSave(Item item) throws RepositoryException
	{
		
	}
	
	public void afterItemSave(Item item) throws RepositoryException
	{
		
	}

	public void beforePropertySet(Node node, String propertyName) throws RepositoryException
	{

	}

	public void afterPropertySet(Property property) throws RepositoryException
	{

	}

	public void beforePropertyRemove(Node node, String propertyName) throws RepositoryException
	{

	}

	public void afterPropertyRemove(Node node, String propertyName) throws RepositoryException
	{

	}

	public void beforeNodeRestoreVersion(Node node) throws RepositoryException
	{

	}

	public void afterNodeRestoreVersion(Node node) throws RepositoryException
	{

	}
	
	public void beforeNodeUpdate(Node node) throws RepositoryException
	{
		
	}
	
	public void afterNodeUpdate(Node node) throws RepositoryException
	{
		
	}

	public void beforeNodeAddMixin(Node node, String mixin) throws RepositoryException
	{

	}

	public void afterNodeAddMixin(Node node, String mixin) throws RepositoryException
	{

	}

	public void beforeNodeRemoveMixin(Node node, String mixin) throws RepositoryException
	{

	}

	public void afterNodeRemoveMixin(Node node, String mixin) throws RepositoryException
	{

	}
	
	public void beforeNodeCancelMerge(Node node, Version version) throws RepositoryException
	{
		
	}
	
	public void afterNodeCancelMerge(Node node, Version version) throws RepositoryException
	{
		
	}
	
	public void beforeNodeDoneMerge(Node node, Version version) throws RepositoryException
	{
		
	}
	
	public void afterNodeDoneMerge(Node node, Version version) throws RepositoryException
	{
		
	}
	
	public void beforeNodeCheckin(Node node) throws RepositoryException
	{
		
	}
	
	public void afterNodeCheckin(Node node, Version version) throws RepositoryException
	{
		
	}
	
	public void beforeNodeCheckout(Node node) throws RepositoryException
	{
		
	}
	
	public void afterNodeCheckout(Node node) throws RepositoryException
	{
		
	}
	
	public void beforeNodeLock(Node node, boolean isDeep, boolean isSessionScoped) throws RepositoryException
	{
		
	}
	
	public void afterNodeLock(Node node, boolean isDeep, boolean isSessionScoped, Lock lock) throws RepositoryException
	{
		
	}
	
	public void beforeNodeUnlock(Node node) throws RepositoryException
	{
		
	}
	
	public void afterNodeUnlock(Node node) throws RepositoryException
	{
		
	}

	public void beforeSessionNodeMove(String sourcePath, String destinationPath) throws RepositoryException
	{

	}

	public void afterSessionNodeMove(String sourcePath, String destinationPath) throws RepositoryException
	{

	}

	public void beforeSessionImportXML(String parentAbsPath) throws RepositoryException
	{

	}

	public void afterSessionImportXML(String parentAbsPath) throws RepositoryException
	{

	}
	
	public void beforeSessionRefresh(boolean keepChanges) throws RepositoryException
	{
		
	}
	
	public void afterSessionRefresh(boolean keepChanges) throws RepositoryException
	{
		
	}
	
	public void beforeSessionSave() throws RepositoryException
	{
		
	}
	
	public void afterSessionSave() throws RepositoryException
	{
		
	}

	public void beforeWorkspaceCopy(String srcAbsPath, String destAbsPath) throws RepositoryException
	{

	}

	
	public void afterWorkspaceCopy(String srcAbsPath, String destAbsPath) throws RepositoryException
	{

	}

	public void beforeWorkspaceCopy(String srcWorkspace, String srcAbsPath, String destAbsPath)
			throws RepositoryException
	{

	}

	public void afterWorkspaceCopy(String srcWorkspace, String srcAbsPath, String destAbsPath)
			throws RepositoryException
	{

	}

	public void beforeWorkspaceClone(String srcWorkspace, String srcAbsPath, String destAbsPath)
			throws RepositoryException
	{

	}

	public void afterWorkspaceClone(String srcWorkspace, String srcAbsPath, String destAbsPath)
			throws RepositoryException
	{

	}

	public void beforeWorkspaceMove(String srcAbsPath, String destAbsPath) throws RepositoryException
	{

	}

	public void afterWorkspaceMove(String srcAbsPath, String destAbsPath) throws RepositoryException
	{

	}
	
	public void beforeWorkspaceImportXML(String parentAbsPath) throws RepositoryException
	{

	}
	
	public void afterWorkspaceImportXML(String parentAbsPath) throws RepositoryException
	{

	}

	public int getPriority()
	{
		return 0;
	}
}
