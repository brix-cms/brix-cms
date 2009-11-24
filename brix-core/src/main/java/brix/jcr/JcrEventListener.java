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

package brix.jcr;

import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;

import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrNodeIterator;
import brix.jcr.api.JcrProperty;
import brix.jcr.base.SaveEvent;
import brix.jcr.base.SaveEventListener;
import brix.jcr.wrapper.BrixNode;

public class JcrEventListener implements SaveEventListener
{

	public void onEvent(EventIterator events)
	{
		while (events.hasNext())
		{
			handleEvent(events.nextEvent());
		}
	}

	private void touch(JcrNode node)
	{
		if (node.isNodeType("nt:file") || node.isNodeType("nt:folder"))
		{
			new BrixNode(node.getDelegate(), node.getSession()).touch();
		}
	}

	private void handleSaveEvent(SaveEvent event)
	{
		JcrNode node = event.getNode();
		touch(node);
		if (node.getDepth() == 0 || node.isNodeType("nt:folder"))
		{
			// go through immediate children to determine if there are any
			// modified ones
			JcrNodeIterator iterator = node.getNodes();
			while (iterator.hasNext())
			{
				JcrNode n = iterator.nextNode();
				if (n.isModified() || n.isNew())
				{
					touch(n);
				}
				else if (n.hasNode("jcr:content"))
				{
					JcrNode content = n.getNode("jcr:content");
					if (content.isModified() || content.isNew())
					{
						touch(n);
					}
					else if (content.hasProperty("jcr:data"))
					{
						JcrProperty data = content.getProperty("jcr:data");
						if (data.isNew() || data.isModified())
						{
							touch(n);
						}
					}
				}
			}
		}
	}

	private void handleEvent(Event event)
	{
		if (event instanceof SaveEvent)
		{
			handleSaveEvent((SaveEvent) event);
		}
	}

}
