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

package org.brixcms.workspace;

import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

/**
 * Contains common and convenience Workspace Manager methods.
 *
 * @author Matej Knopp
 */
public abstract class AbstractWorkspaceManager implements WorkspaceManager {
    protected static final String NODE_NAME = "brix:workspace";

    protected static final String NODE_PATH = "/" + NODE_NAME;

    protected static final String PROPERTIES_NODE = "properties";

    protected static final String DELETED_PROPERTY = "deleted";

    // for compatibility with existing workspaces
    protected final String[] LEGACY_PREFIXES = new String[]{
            "brix-workspace-", "brix_ws_"};

    protected final String WORKSPACE_PREFIX = "bx_";

    private Map<AttributeKeyAndValue, Set<String>> attributeToWorkspaceListMap = new HashMap<AttributeKeyAndValue, Set<String>>();

    private Map<String, Map<String, String>> workspaceToWorkspaceAttributesMap = new HashMap<String, Map<String, String>>();


    public synchronized List<Workspace> getWorkspacesFiltered(
            Map<String, String> workspaceAttributes) {
        if (workspaceAttributes.isEmpty()) {
            return getWorkspaces();
        } else {
            List<Set<String>> workspaces = new ArrayList<Set<String>>();
            for (Entry<String, String> entry : workspaceAttributes.entrySet()) {
                AttributeKeyAndValue keyAndValue = new AttributeKeyAndValue(entry.getKey(), entry
                        .getValue());
                Set<String> w = attributeToWorkspaceListMap.get(keyAndValue);
                if (w == null || w.isEmpty()) {
                    return Collections.emptyList();
                } else {
                    workspaces.add(w);
                }
            }

            Set<String> intersection = intersect(workspaces);
            List<Workspace> result = new ArrayList<Workspace>(intersection.size());

            for (String s : intersection) {
                result.add(getWorkspace(s));
            }

            return result;
        }
    }

    public Workspace getWorkspace(String workspaceId) {
        if (workspaceExists(workspaceId)) {
            return new WorkspaceImpl(workspaceId);
        } else {
            return null;
        }
    }

    abstract protected void delete(String workspaceId) throws RepositoryException;

    abstract protected String getAttribute(String workspaceId, String key);

    abstract protected Iterator<String> getAttributeKeys(String workspaceId);

    protected synchronized Iterator<String> getCachedAttributeKeys(String workspaceId) {
        Map<String, String> attributes = workspaceToWorkspaceAttributesMap.get(workspaceId);
        if (attributes == null || attributes.isEmpty()) {
            List<String> empty = Collections.emptyList();
            return empty.iterator();
        } else {
            return new ArrayList<String>(attributes.keySet()).iterator();
        }
    }

    protected String getWorkspaceId(UUID uuid) {
        final long most = uuid.getMostSignificantBits();
        final long least = uuid.getLeastSignificantBits();
        final String smost = LongEncoder.encode(most);
        final String sleast = LongEncoder.encode(least);
        return WORKSPACE_PREFIX + smost + sleast;
    }

    protected AbstractWorkspaceManager initialize() {
        attributeToWorkspaceListMap.clear();
        workspaceToWorkspaceAttributesMap.clear();
        return this;
    }

    private Set<String> intersect(List<Set<String>> sets) {
        if (sets.isEmpty()) {
            return Collections.emptySet();
        } else if (sets.size() == 1) {
            return sets.get(0);
        }

        Collections.sort(sets, new Comparator<Set<String>>() {
            public int compare(Set<String> o1, Set<String> o2) {
                return o1.size() - o2.size();
            }
        });

        Set<String> current = sets.get(0);

        for (int i = 1; i < sets.size(); ++i) {
            current = intersect(current, sets.get(i));
        }

        return current;
    }

    private Set<String> intersect(Set<String> s1, Set<String> s2) {
        Set<String> result = new HashSet<String>(s1.size());
        for (String s : s1) {
            if (s2.contains(s)) {
                result.add(s);
            }
        }
        return result;
    }

    protected boolean isBrixWorkspace(String id) {
        if (id.startsWith(WORKSPACE_PREFIX)) {
            return true;
        }
        for (int i = 0; i < LEGACY_PREFIXES.length; i++) {
            if (id.startsWith(LEGACY_PREFIXES[i])) {
                return true;
            }
        }
        return false;
    }

    protected synchronized void removeCachedWorkspaceAttributes(String workspaceId) {
        workspaceToWorkspaceAttributesMap.remove(workspaceId);
        for (Iterator<Entry<AttributeKeyAndValue, Set<String>>> i = attributeToWorkspaceListMap
                .entrySet().iterator(); i.hasNext();) {
            Entry<AttributeKeyAndValue, Set<String>> e = i.next();
            if (e.getValue().contains(workspaceId)) {
                e.getValue().remove(workspaceId);
                if (e.getValue().isEmpty()) {
                    i.remove();
                }
            }
        }
    }

    abstract protected void setAttribute(String workspaceId, String attributeKey,
                                         String attributeValue) throws RepositoryException;

    protected void setCachedAttribute(String workspaceId, String key, String value) {
        removeCachedAttribute(workspaceId, key);
        if (value != null) {
            Map<String, String> workspaceAttributes = workspaceToWorkspaceAttributesMap
                    .get(workspaceId);
            if (workspaceAttributes == null) {
                workspaceAttributes = new HashMap<String, String>();
                workspaceToWorkspaceAttributesMap.put(workspaceId, workspaceAttributes);
            }
            workspaceAttributes.put(key, value);

            AttributeKeyAndValue keyAndValue = new AttributeKeyAndValue(key, value);
            Set<String> workspaces = attributeToWorkspaceListMap.get(keyAndValue);
            if (workspaces == null) {
                workspaces = new HashSet<String>();
                attributeToWorkspaceListMap.put(keyAndValue, workspaces);
            }
            if (!workspaces.contains(workspaceId)) {
                workspaces.add(workspaceId);
            }
        }
    }

    protected void removeCachedAttribute(String workspaceId, String key) {
        String value = getCachedAttribute(workspaceId, key);
        if (value != null) {
            Map<String, String> attributes = workspaceToWorkspaceAttributesMap.get(workspaceId);
            attributes.remove(key);
            AttributeKeyAndValue keyAndValue = new AttributeKeyAndValue(key, value);
            Set<String> workspaces = attributeToWorkspaceListMap.get(keyAndValue);
            if (workspaces != null) {
                workspaces.remove(workspaceId);
            }
        }
    }

    protected String getCachedAttribute(String workspaceId, String key) {
        Map<String, String> attributes = workspaceToWorkspaceAttributesMap.get(workspaceId);
        if (attributes != null) {
            return attributes.get(key);
        } else {
            return null;
        }
    }

    private class AttributeKeyAndValue {
        private final String key;
        private final String value;

        public AttributeKeyAndValue(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj instanceof AttributeKeyAndValue == false)
                return false;
            AttributeKeyAndValue that = (AttributeKeyAndValue) obj;
            return equals(key, that.key) && equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return hashCode(key) + 31 * hashCode(value);
        }

        private boolean equals(Object o1, Object o2) {
            return o1 == o2 || (o1 != null && o1.equals(o2));
        }

        private int hashCode(Object o) {
            return o != null ? o.hashCode() : 0;
        }
    }

    protected class WorkspaceImpl implements Workspace {
        private final String id;

        public WorkspaceImpl(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public String getAttribute(String attributeKey) {
            return AbstractWorkspaceManager.this.getAttribute(getId(), attributeKey);
        }

        public Iterator<String> getAttributeKeys() {
            return AbstractWorkspaceManager.this.getAttributeKeys(getId());
        }

        public void setAttribute(String attributeKey, String attributeValue) {
            try {
                AbstractWorkspaceManager.this.setAttribute(getId(), attributeKey, attributeValue);
            } catch (RepositoryException e) {
                throw new JcrException(e);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof WorkspaceImpl == false) {
                return false;
            }
            WorkspaceImpl that = (WorkspaceImpl) obj;

            if (id == that.id) {
                return true;
            } else if (id == null || that.id == null) {
                return false;
            }
            return id.equals(that.id);
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }

        public void delete() {
            try {
                AbstractWorkspaceManager.this.delete(getId());
            } catch (RepositoryException e) {
                throw new JcrException(e);
            }
        }
    }

    ;
}
