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

package org.brixcms.rmiserver.web.admin;

import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.StringValidator;
import org.brixcms.rmiserver.Role;
import org.brixcms.rmiserver.UserService.UserDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class UserDtoEditor extends GenericPanel<UserDto> {
    private static final long serialVersionUID = 1L;

    public UserDtoEditor(String id, IModel<UserDto> model, Mode mode) {
        super(id, model);

        add(new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this)));

        Form<?> form = new Form<Void>("form");
        add(form);

        FormComponent<?> login = new TextField<String>("login", new PropertyModel<String>(model,
                "login")).setRequired(true).add(StringValidator.lengthBetween(4, 32));
        login.setLabel(new Model<String>("Login"));
        login.setVisible(mode == Mode.CREATE || mode == Mode.EDIT);
        form.add(login);


        FormComponent<?> roles = new CheckBoxMultipleChoice<Role>("roles",
                new PropertyModel<Collection<Role>>(model, "roles"), new RoleCollection(),
                new RoleRenderer());
        roles.setVisible(mode == Mode.CREATE || mode == Mode.EDIT);
        form.add(roles);

        FormComponent<?> password1 = new PasswordTextField("password1",
                new PropertyModel<String>(model, "password")).setRequired(true).add(
                StringValidator.lengthBetween(4, 32)).setLabel(new Model<String>("Password"));
        password1.setVisible(mode == Mode.CREATE || mode == Mode.CHANGE_PASSWORD);
        FormComponent<?> password2 = new PasswordTextField("password2", new Model<String>());
        password2.setLabel(new Model<String>("Confirm Password"));
        form.add(password1, password2);
        password2.setVisible(mode == Mode.CREATE || mode == Mode.CHANGE_PASSWORD);
        form.add(new EqualPasswordInputValidator(password1, password2));

        form.add(new Button("ok") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                onOk(UserDtoEditor.this.getModelObject());
            }
        });

        form.add(new Link<Void>("cancel") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                onCancel();
            }
        });
    }

    protected abstract void onOk(UserDto dto);

    protected abstract void onCancel();

    public static enum Mode {
        CREATE,
        EDIT,
        CHANGE_PASSWORD
    }

    private class RoleRenderer implements IChoiceRenderer<Role> {
        private static final long serialVersionUID = 1L;

        public Object getDisplayValue(Role object) {
            return getString(Role.class.getName() + "." + object.name());
        }

        public String getIdValue(Role object, int index) {
            return object.name();
        }
    }

    private static class RoleCollection extends LoadableDetachableModel<List<? extends Role>> {
        private static final long serialVersionUID = 1L;

        @Override
        protected List<? extends Role> load() {
            return new ArrayList<Role>(Arrays.asList(Role.values()));
        }
    }
}
