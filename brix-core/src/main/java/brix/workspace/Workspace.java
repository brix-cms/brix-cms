package brix.workspace;

import java.util.Iterator;

public interface Workspace
{
    public String getId();
     
    public void setAttribute(String attributeKey, String attributeValue);
    
    public String getAttribute(String attributeKey);
    
    public Iterator<String> getAttributeKeys();
}
