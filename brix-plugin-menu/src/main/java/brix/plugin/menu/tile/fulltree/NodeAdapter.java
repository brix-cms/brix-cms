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

package brix.plugin.menu.tile.fulltree;

import brix.jcr.AbstractNodeAdapter;
import brix.jcr.wrapper.BrixNode;

class NodeAdapter extends AbstractNodeAdapter
{
	private static final String P_MENU = "menu";
	private static final String P_SELECTED_LI_CSS_CLASS = "selectedLiCssClass";
	private static final String P_OUTER_UL_CSS_CLASS = "outerUlCssClass";


	public NodeAdapter(BrixNode node)
	{
		super(node);
	}

	public BrixNode getMenuNode()
	{
		return getProperty(P_MENU, (BrixNode)null);
	}

	public void setMenuNode(BrixNode node)
	{
		setProperty(P_MENU, node);
	}

	public String getSelectedLiCssClass()
	{
		return getProperty(P_SELECTED_LI_CSS_CLASS, (String)null);
	}

	public void setSelectedLiCssClass(String cssClass)
	{
		setProperty(P_SELECTED_LI_CSS_CLASS, cssClass);
	}

	public String getOuterUlCssClass()
	{
		return getProperty(P_OUTER_UL_CSS_CLASS, (String)null);
	}

	public void setOuterUlCssClass(String cssClass)
	{
		setProperty(P_OUTER_UL_CSS_CLASS, cssClass);
	}

}
