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

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.rmiserver.Role;
import org.brixcms.rmiserver.User;
import org.brixcms.rmiserver.UserService;
import org.brixcms.rmiserver.UserService.UserDto;
import org.brixcms.rmiserver.web.admin.UserDtoEditor.Mode;

import java.util.Iterator;

/**
 * Homepage
 */
@AllowedRoles(Role.ADMIN)
public class UsersPage extends WebPage {
    private static final long serialVersionUID = 1L;

    @SpringBean
    public UserService users;

    /**
     * Constructor that is invoked when page is invoked without a session.
     *
     * @param parameters Page parameters
     */
    public UsersPage(final PageParameters parameters) {
        DataView<User> list = null;
        add(list = new DataView<User>("list", new UsersDataProvider(), 20) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<User> item) {
                item.add(new Label("login", new PropertyModel<String>(item.getModel(),
                        "login")));
                item.add(new Label("roles", new UserRolesModel(item.getModel())));

                final User user = item.getModelObject();
                if (user.isLocked()) {
                    item.add(new LockedFragment("actions", item.getModel()));
                } else {
                    item.add(new ActionsFragment("actions", item.getModel()));
                }
            }
        });
        add(new PagingNavigator("pager", list));

        add(new WebMarkupContainer("editor"));

        add(new Link<Void>("create") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                onCreateUser();
            }
        });
    }

    private void onCreateUser() {
        IModel<UserDto> dto = new Model<UserDto>(new UserDto());
        addOrReplace(new UserDtoEditor("editor", dto, Mode.CREATE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onCancel() {
                removeEditor();
            }

            @Override
            protected void onOk(UserDto dto) {
                users.create(dto);
                removeEditor();
            }
        });
    }

    private void onChangePassword(final IModel<User> model) {
        final User user = model.getObject();
        IModel<UserDto> dto = new Model<UserDto>(users.dto(user));
        addOrReplace(new UserDtoEditor("editor", dto, Mode.CHANGE_PASSWORD) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onCancel() {
                removeEditor();
            }

            @Override
            protected void onOk(UserDto dto) {
                users.updatePassword(model.getObject(), dto.password);
                removeEditor();
            }
        });
    }

    private void removeEditor() {
        replace(new WebMarkupContainer("editor"));
    }

    private void onEditUser(final IModel<User> model) {
        final User user = model.getObject();
        IModel<UserDto> dto = new Model<UserDto>(users.dto(user));
        addOrReplace(new UserDtoEditor("editor", dto, Mode.EDIT) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onCancel() {
                removeEditor();
            }

            @Override
            protected void onOk(UserDto dto) {
                users.update(model.getObject(), dto);
                removeEditor();
            }
        });
    }

    private class UsersDataProvider implements IDataProvider<User> {
        private static final long serialVersionUID = 1L;

        public Iterator<User> iterator(int first, int count) {
            return users.query(first, count).iterator();
        }

        public IModel<User> model(User object) {
            return new UserModel(object);
        }

        public int size() {
            return users.queryCount();
        }

        public void detach() {
        }
    }

    private class UserRolesModel extends LoadableDetachableModel<String> {
        private static final long serialVersionUID = 1L;

        private final IModel<User> delegate;

        public UserRolesModel(IModel<User> delegate) {
            this.delegate = delegate;
        }

        @Override
        protected String load() {
            User user = delegate.getObject();
            StringBuilder buff = new StringBuilder();
            Iterator<Role> it = user.getRoles().iterator();
            while (it.hasNext()) {
                Role role = it.next();
                buff.append(roleToString(role));
                if (it.hasNext()) {
                    buff.append(", ");
                }
            }
            return buff.toString();
        }

        private String roleToString(Role role) {
            return getString(Role.class.getName() + "." + role.name());
        }
    }

    private class LockedFragment extends GenericFragment<User> {
        public LockedFragment(String id, IModel<User> model) {
            super(id, "locked", UsersPage.this, model);
        }
    }

    private class ActionsFragment extends GenericFragment<User> {
        public ActionsFragment(String id, IModel<User> model) {
            super(id, "actions", UsersPage.this, model);
            add(new Link<User>("edit", model) {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick() {
                    onEditUser(getModel());
                }

                @Override
                public boolean isVisible() {
                    return !getModelObject().isLocked();
                }
            });

            add(new Link<User>("password", model) {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick() {
                    onChangePassword(getModel());
                }

                @Override
                public boolean isVisible() {
                    return !getModelObject().isLocked();
                }
            });
        }
    }
}
