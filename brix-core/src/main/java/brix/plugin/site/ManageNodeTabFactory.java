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

package brix.plugin.site;

import java.util.List;

import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.registry.ExtensionPoint;
import brix.web.tab.IBrixTab;

/**
 * Factory for creating site node management tabs.
 * 
 * @author Matej Knopp
 */
public interface ManageNodeTabFactory
{

	public static final ExtensionPoint<ManageNodeTabFactory> POINT = new ExtensionPoint<ManageNodeTabFactory>()
	{
		public brix.registry.ExtensionPoint.Multiplicity getMultiplicity()
		{
			return Multiplicity.COLLECTION;
		}

		public String getUuid()
		{
			return ManageNodeTabFactory.class.getName();
		}
	};

	/**
	 * Returns list of node management tabs for given node. 
	 * 
	 * @param nodeModel
	 * @return
	 */
	public List<IBrixTab> getManageNodeTabs(IModel<BrixNode> nodeModel);

}
