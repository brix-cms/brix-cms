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

package org.brixcms.plugin.site.picker.reference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.lang.Objects;
import org.apache.wicket.util.string.StringValue;
import org.brixcms.web.nodepage.BrixPageParameters;

import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.CheckBoxColumn;
import com.inmethod.grid.column.editable.EditablePropertyColumn;
import com.inmethod.grid.column.editable.SubmitCancelColumn;
import com.inmethod.grid.datagrid.DataGrid;

public abstract class QueryParametersTab extends Panel {
    AjaxLink<?> removeSelected;

    private Entry newEntry = new Entry();
    private final DataSource dataSource = new DataSource();

    public QueryParametersTab(String id) {
        super(id);
        setOutputMarkupId(true);

        final FeedbackPanel feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        Form<Entry> newForm = new Form<Entry>("newForm", new CompoundPropertyModel<Entry>(new PropertyModel<Entry>(
                this, "newEntry")));
        add(newForm);

        newForm.add(new TextField<String>("key").setRequired(true));
        newForm.add(new TextField<String>("value").setRequired(true));
        newForm.add(new AjaxButton("add") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                dataSource.addEntry(newEntry);
                dataSource.storeToPageParameters();
                target.addComponent(QueryParametersTab.this);
                newEntry = new Entry();
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.addComponent(feedback);
            }
        });

        List<IGridColumn> columns = new ArrayList<IGridColumn>();
        columns.add(new CheckBoxColumn("checkbox"));
        columns.add(new EditablePropertyColumn(new ResourceModel("key"), "key") {
            @Override
            protected void addValidators(FormComponent component) {
                component.setRequired(true);
            }
        });
        columns.add(new EditablePropertyColumn(new ResourceModel("value"), "value") {
            @Override
            protected void addValidators(FormComponent component) {
                component.setRequired(true);
            }
        });
        columns.add(new SubmitCancelColumn("submitCancel", new ResourceModel("edit")) {
            @Override
            protected void onSubmitted(AjaxRequestTarget target, IModel rowModel, WebMarkupContainer rowComponent) {
                dataSource.storeToPageParameters();
                super.onSubmitted(target, rowModel, rowComponent);
                target.addComponent(feedback);
            }

            @Override
            protected void onError(AjaxRequestTarget target, IModel rowModel, WebMarkupContainer rowComponent) {
                target.addComponent(feedback);
            }
        });

        final DataGrid grid = new DataGrid("grid", dataSource, columns) {
            @Override
            public void onItemSelectionChanged(IModel item, boolean newValue) {
                AjaxRequestTarget target = AjaxRequestTarget.get();
                if (target != null) {
                    target.addComponent(removeSelected);
                }
                super.onItemSelectionChanged(item, newValue);
            }
        };
        grid.setRowsPerPage(Integer.MAX_VALUE);
        grid.setAllowSelectMultiple(true);
        grid.setContentHeight(14, SizeUnit.EM);
        grid.setSelectToEdit(false);
        add(grid);

        add(removeSelected = new AjaxLink<Void>("removeSelected") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Collection<IModel> items = grid.getSelectedItems();
                if (items.size() > 0) {
                    for (IModel model : items) {
                        Entry entry = (Entry) model.getObject();
                        dataSource.removeEntry(entry);
                    }
                    grid.resetSelectedItems();
                    dataSource.storeToPageParameters();
                    grid.markAllItemsDirty();
                    grid.update();
                } else {
                    target.appendJavaScript("alert('" + getString("noItemsSelected") + "');");
                }
            }

            @Override
            public boolean isEnabled() {
                return !grid.getSelectedItems().isEmpty();
            }
        });
    }

    protected abstract BrixPageParameters getPageParameters();

    private class DataSource implements IDataSource {
        public void detach() {
            entries = null;
        }

        public IModel model(Object object) {
            return new Model<Serializable>((Serializable) object) {
                @Override
                public boolean equals(Object obj) {
                    if (this == obj) {
                        return true;
                    }
                    if (obj instanceof Model == false) {
                        return false;
                    }
                    Model that = (Model) obj;
                    return Objects.equal(getObject(), that.getObject());
                }

                @Override
                public int hashCode() {
                    return getObject().hashCode();
                }
            };
        }

        private Set<Entry> getEntries() {
            if (entries == null) {
                entries = new TreeSet<Entry>();
                for (String s : getPageParameters().getNamedKeys()) {
                    for (StringValue v : getPageParameters().getValues(s)) {
                        Entry e = new Entry();
                        e.key = s;
                        e.value = v.toString();
                        entries.add(e);
                    }
                }
            }
            return entries;
        }

        public void query(IQuery query, IQueryResult result) {
            result.setTotalCount(getEntries().size());
            result.setItems(getEntries().iterator());
        }

        private void addEntry(Entry entry) {
            getEntries().add(entry);
        }

        private void removeEntry(Entry entry) {
            getEntries().remove(entry);
        }

        private void storeToPageParameters() {
            if (entries != null) {
                getPageParameters().clearNamed();
                for (Entry entry : entries) {
                    getPageParameters().set(entry.key, entry.value);
                }
            }
        }

        private Set<Entry> entries = null;
    }

    private static class Entry implements Serializable, Comparable<Entry> {
        private String key;
        private String value;

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof Entry == false) {
                return false;
            }
            Entry that = (Entry) obj;
            return Objects.equal(key, that.key) && Objects.equal(value, that.value);
        }

        public int compareTo(Entry o) {
            int v = key.compareTo(o.key);
            if (v != 0) {
                return v;
            } else {
                return value.compareTo(o.value);
            }
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(new Object[]{this.key, this.value});
        }
    }

    ;
}
