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

package brix.tinymce;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.util.collections.MiniMap;
import org.apache.wicket.util.template.PackagedTextTemplate;

public class TinyMceEnabler extends AbstractBehavior
{

    private List<Component> components = new ArrayList<Component>(1);

    @Override
    public void bind(Component component)
    {
        if (!(component instanceof TextArea))
        {
            throw new IllegalStateException(getClass().getName() + " can only be added to " +
                    TextArea.class.getName());
        }
        components.add(component);
        component.setOutputMarkupId(true);
    }

    @Override
    public void renderHead(IHeaderResponse response)
    {

        response.renderJavascriptReference(new ResourceReference(TinyMceEnabler.class,
            "tiny_mce/tiny_mce.js"));

        StringBuilder idlist = new StringBuilder();
        Iterator<Component> it = components.iterator();
        while (it.hasNext())
        {
            idlist.append("\"").append(it.next().getMarkupId()).append("\"");
            if (it.hasNext())
            {
                idlist.append(",");
            }
        }

        MiniMap vars = new MiniMap(1);
        vars.put("idlist", idlist.toString());

        PackagedTextTemplate enabler = new PackagedTextTemplate(TinyMceEnabler.class, "enabler.js");

        response.renderJavascript(enabler.asString(vars), getClass().getName() +
                System.identityHashCode(this));

    }

}
