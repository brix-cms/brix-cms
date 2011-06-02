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

package org.brixcms.plugin.site.webdav;

import org.brixcms.Brix;
import org.brixcms.jcr.JcrNodeWrapperFactory;
import org.brixcms.jcr.RepositoryUtil;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrNodeIterator;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.wrapper.BrixNode;

import javax.jcr.Node;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RulesNode extends BrixNode {
    public static final String TYPE = Brix.NS_PREFIX + "webDavContainer";

    public static JcrNodeWrapperFactory FACTORY = new JcrNodeWrapperFactory() {
        @Override
        public boolean canWrap(Brix brix, JcrNode node) {
            return TYPE.equals(getNodeType(node));
        }

        @Override
        public JcrNode wrap(Brix brix, Node node, JcrSession session) {
            return new RulesNode(node, session);
        }

        @Override
        public void initializeRepository(Brix brix, Session session) {
            RepositoryUtil.registerNodeType(session.getWorkspace(), TYPE, false, false, true);
        }
    };

    private static final String RULES = "rules";

    public static RulesNode initialize(BrixNode node) {
        node.setNodeType(TYPE);
        return new RulesNode(node.getDelegate(), node.getSession());
    }

    public RulesNode(Node delegate, JcrSession session) {
        super(delegate, session);
    }

    public Rule getRule(String name) {
        if (!hasNode(RULES)) {
            return null;
        } else {
            JcrNode parent = getNode(RULES);
            if (parent.hasNode(name)) {
                Rule rule = Rule.load(parent.getNode(name));
                return rule;
            } else {
                return null;
            }
        }
    }

    public List<Rule> getRules(boolean sortByPriority) {
        if (!hasNode(RULES)) {
            return Collections.emptyList();
        } else {
            JcrNode rules = getNode(RULES);
            List<Rule> result = new ArrayList<Rule>();
            JcrNodeIterator n = rules.getNodes();
            while (n.hasNext()) {
                Rule rule = Rule.load(n.nextNode());
                result.add(rule);
            }

            if (sortByPriority) {
                Collections.sort(result, new Comparator<Rule>() {
                    public int compare(Rule o1, Rule o2) {
                        int i = o2.getPriority() - o1.getPriority();
                        if (i != 0) {
                            return i;
                        } else {
                            return o1.getName().compareToIgnoreCase(o2.getName());
                        }
                    }
                });
            } else {
                Collections.sort(result, new Comparator<Rule>() {
                    public int compare(Rule o1, Rule o2) {
                        return o1.getName().compareToIgnoreCase(o2.getName());
                    }
                });
            }

            return result;
        }
    }

    @Override
    public String getUserVisibleName() {
        return "WebDAV Rules";
    }

    public void removeRule(Rule rule) {
        if (hasNode(RULES)) {
            JcrNode parent = getNode(RULES);
            if (parent.hasNode(rule.getName())) {
                parent.getNode(rule.getName()).remove();
                parent.save();
            }
        }
    }

    public void saveRule(Rule rule) {
        JcrNode parent;
        if (!hasNode(RULES)) {
            parent = addNode(RULES, "nt:unstructured");
        } else {
            parent = getNode(RULES);
        }
        rule.save(parent);
        save();
    }
}
