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
package brix.markup.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.markup.parser.filter.EnclosureHandler;

import brix.markup.MarkupSource;
import brix.markup.MarkupHelper;
import brix.markup.tag.ComponentTag;
import brix.markup.tag.Item;
import brix.markup.tag.Tag;
import brix.markup.transform.MarkupSourceTransformer;
import brix.plugin.site.page.AbstractContainer;
import brix.plugin.site.page.tile.TileTag;

/**
 * Enables the use of wicket:enclosures with brix:tile tags.
 * 
 * It will resolve the enclosure child elements based on either "brix:tile id" or wicket:id attributes.
 * You can reference wicket components within the tile using the 'child' attribute and a comma separated path.
 * 
 *   e.g. where the tile has the wicket component title as a child of wrapper
 *   &lt;wicket:enclosure child="mytile,wrapper,title"&gt;
 *   	&lt;brix:tile id="mytile" /&gt;
 *   &lt;/wicket:enclosure&gt;
 *   
 * TODO: 
 *   - investigate if it is possible to have commas in brix:tile id or wicket:id as this would break id matching
 *   - investigate if this works with wicket auto-tags
 * 
 * @author James McIntosh
 *
 */
public class EnclosureMarkupSourceTransformer extends MarkupSourceTransformer {

	public static final String ENCLOSURE = org.apache.wicket.markup.ComponentTag.DEFAULT_WICKET_NAMESPACE + ":enclosure";
	
	public static final String WICKET_ID = org.apache.wicket.markup.ComponentTag.DEFAULT_WICKET_NAMESPACE + ":id";
	
	public EnclosureMarkupSourceTransformer(MarkupSource delegate) {
		super(delegate);
	}

	@Override
	protected List<Item> transform(List<Item> items) {
		Tag enclosure = null;
		List<String> children = null;
		List<Tag> enclosureChildTags = null;
		for (Item i : items) {
			if (i instanceof Tag) {
				Tag tag = (Tag)i;
				if (isEnclosure(tag)) {
					if (tag.getType() == Tag.Type.OPEN) {
						// found opening enclosure
						enclosure = tag;
						children = getChildren(enclosure);
						enclosureChildTags = new ArrayList<Tag>();
					} else if (tag.getType() == Tag.Type.CLOSE) {
						// tidy up on close tag
						updateEnclosureChildId(enclosure, children, enclosureChildTags);
						enclosure = null;
						children = null;
						enclosureChildTags = null;
					}
				} else if (enclosure != null && !children.isEmpty()) {
					// if we have open enclosure tag and children to find
					int index = checkIfTagIsChild(tag, children, enclosureChildTags);
					if (index != -1) {
						onChildFound(tag, children, enclosureChildTags, index);
					}
				}
			}
		}
		return items;
	}

	/**
	 * Replaces the supplied enclosure child tag with the actual resolved comma separated component path
	 * @param enclosure
	 * @param children
	 * @param enclosureChildTags
	 */
	private void updateEnclosureChildId(Tag enclosure, List<String> children, List<Tag> enclosureChildTags) {
		Map<String, String> attributes = enclosure.getAttributeMap();
		if (attributes != null) {
			String child = "";
			for (int i=0; i<children.size(); i++) {
				String childid = children.get(i);
				if (childid != null) {
					child += (child.length() > 0 ? Component.PATH_SEPARATOR : "") + childid; 
				} else {
					Tag tag = enclosureChildTags.get(i);
					String id = getGeneratedTagId(tag);
					if (id != null) {
						// nested children are delimited by commas
						child += (child.length() > 0 ? Component.PATH_SEPARATOR : "") + id; 
					}
				}
			}
			attributes.put(EnclosureHandler.CHILD_ATTRIBUTE, child);
		}
	}
	
	/**
	 * Resolved the actual generated component wicket:id for the tag
	 * @param tag
	 * @return
	 */
	private String getGeneratedTagId(Tag tag) {
		if (tag instanceof ComponentTag) {
			 return MarkupHelper.getComponentID((ComponentTag)tag);
		}
		return null;
	}
	
	/**
	 * Checks for an id attribute if a brix:tile then a wicket:id
	 * @param tag
	 * @return
	 */
	private String getTagId(Tag tag) {
		Map<String, String> attributes = tag.getAttributeMap();
		String id = null;
		if (attributes != null) {
			// look for brix:tile first "id" first then "wicket:id"
			if (tag instanceof TileTag) {
				id = attributes.get(AbstractContainer.MARKUP_TILE_ID);
			}
			if (id == null) {
				id = attributes.get(getWicketIdAttributeName(tag));
			}
			return id;
		}
		return null;
	}
	
	private String getWicketIdAttributeName(Tag tag) {
		// TODO: this reference possibly could be smarter by somehow using ComponentTag.getId()
		// ComponentTag comments indicate that wicket:id is the default and that this could 
		// potentially be different in cases such as auto-tags
		return WICKET_ID;
	}

	/**
	 * Gets the value of the "child" attribute and splits the path into a list
	 * @param enclosure
	 * @return ids
	 */
	private List<String> getChildren(Tag enclosure) {
		Map<String, String> attributes = enclosure.getAttributeMap();
		if (attributes != null) {
			if (attributes.containsKey(EnclosureHandler.CHILD_ATTRIBUTE)) {
				// split for nested components
				String[] children = attributes.get(EnclosureHandler.CHILD_ATTRIBUTE).split(""+Component.PATH_SEPARATOR);
				ArrayList<String> list = new ArrayList<String>();
				for (String child : children) {
					list.add(child);
				}
				return list;
			}
		}
		return null;
	}
	
	/**
	 * Inspects the current tag and determine if it is one of the required child components for the enclosure
	 * @param tag
	 * @param children
	 * @param enclosureChildTags
	 * @return
	 */
	private int checkIfTagIsChild(Tag tag, List<String> children, List<Tag> enclosureChildTags) {
		String id = getTagId(tag);
		if (id != null) {
			for (String child : children) {
				if (child == null) {
					continue;
				}
				if (child.equals(id)) {
					return children.indexOf(child);
				}
			}
		}
		return -1;
	}

	/**
	 * Updates the list of remaining child components to find and the list of already found tags which represent them
	 * @param tag
	 * @param children
	 * @param enclosureChildTags
	 * @param index
	 */
	private void onChildFound(Tag tag, List<String> children, List<Tag> enclosureChildTags, int index) {
		if (index != -1) {
			children.remove(index);
			children.add(index, null);
			enclosureChildTags.add(index, tag);
		}
	}

	private boolean isEnclosure(Tag tag) {
		return ENCLOSURE.equals(tag.getName());
	}

}
