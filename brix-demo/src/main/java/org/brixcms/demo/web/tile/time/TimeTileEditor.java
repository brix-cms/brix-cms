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

package org.brixcms.demo.web.tile.time;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.page.tile.admin.TileEditorPanel;

import java.text.SimpleDateFormat;

public class TimeTileEditor extends TileEditorPanel {
    private String format;

    public TimeTileEditor(String id, IModel<BrixNode> tileContainerNode) {
        super(id);
        add(new TextField<String>("format", new PropertyModel<String>(this, "format"))
                .setLabel(Model.of("format")).add(new IValidator<String>() {
                    public void validate(IValidatable<String> validatable) {
                        String expr = validatable.getValue();
                        try {
                            new SimpleDateFormat(expr);
                        } catch (IllegalArgumentException e) {
                            validatable.error(new ValidationError()
                                    .setMessage("${input} is an illegal date format pattern"));
                        }
                    }
                }));
    }

    @Override
    public void load(BrixNode node) {
        if (node.hasProperty("format")) {
            format = node.getProperty("format").getString();
        }
    }

    @Override
    public void save(BrixNode node) {
        node.setProperty("format", format);
    }
}
