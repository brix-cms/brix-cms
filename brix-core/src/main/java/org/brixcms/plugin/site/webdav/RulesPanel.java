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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.Brix;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.plugin.site.webdav.Rule.Type;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.workspace.Workspace;

import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.CheckBoxColumn;
import com.inmethod.grid.column.editable.EditableCellPanel;
import com.inmethod.grid.column.editable.EditablePropertyColumn;
import com.inmethod.grid.column.editable.SubmitCancelColumn;
import com.inmethod.grid.datagrid.DataGrid;

public class RulesPanel extends BrixGenericPanel<RulesNode> {
// ------------------------------ FIELDS ------------------------------
    ;
    private Component feedback;
    private DataGrid<DataSource, Rule> dataGrid;
    private AjaxLink<?> removeSelected;

    public RulesPanel(String id, IModel<Workspace> workspaceModel) {
        super(id, new RulesNodeModel(workspaceModel));


        add(feedback = new FeedbackPanel("feedback").setOutputMarkupId(true));

        List<IGridColumn<DataSource, Rule>> columns = new ArrayList<IGridColumn<DataSource, Rule>>();

        columns.add(new CheckBoxColumn("checkbox"));
        columns.add(new PriorityColumn(new ResourceModel("priority"), "priority").setInitialSize(60));
        columns.add(new EditablePropertyColumn(new ResourceModel("pathPrefix"), "pathPrefix"));
        columns.add(new EditablePropertyColumn(new ResourceModel("extensions"), "extensions").setInitialSize(100));
        columns.add(new TypeColumn(new ResourceModel("type"), "type").setInitialSize(90));
        columns.add(new TemplateColumn("template", new ResourceModel("template")).setInitialSize(250));

        columns.add(new SubmitColumn("edit", new ResourceModel("edit")));

        dataGrid = new DataGrid<DataSource, Rule>("grid", new DataSource(), columns) {
            @Override
            public void onItemSelectionChanged(IModel item, boolean newValue) {
                super.onItemSelectionChanged(item, newValue);
                if (AjaxRequestTarget.get() != null)
                    AjaxRequestTarget.get().addComponent(removeSelected);
            }
        };
        add(dataGrid);
        dataGrid.setContentHeight(30, SizeUnit.EM);
        dataGrid.setClickRowToSelect(true);

        add(removeSelected = new AjaxLink<Void>("removeSelected") {
            @Override
            public boolean isEnabled() {
                return !dataGrid.getSelectedItems().isEmpty();
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                for (IModel<Rule> model : dataGrid.getSelectedItems()) {
                    Rule rule = model.getObject();
                    RulesPanel.this.getModelObject().removeRule(rule);
                }
                dataGrid.resetSelectedItems();
                dataGrid.markAllItemsDirty();
                dataGrid.update();
            }
        });
        removeSelected.setOutputMarkupId(true);

        add(new AjaxLink<Void>("add") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                String name = "rule-" + UUID.randomUUID().toString();
                Rule rule = new Rule(name);
                rule.setType(Type.PAGE);
                RulesPanel.this.getModelObject().saveRule(rule);
                dataGrid.markAllItemsDirty();
                dataGrid.update();
            }
        });
    }

    private final static class RulesNodeModel extends LoadableDetachableModel<RulesNode> {
        private final IModel<Workspace> workspaceModel;

        public RulesNodeModel(IModel<Workspace> workspaceModel) {
            this.workspaceModel = workspaceModel;
        }

        @Override
        protected RulesNode load() {
            JcrSession session = Brix.get().getCurrentSession(workspaceModel.getObject().getId());
            return SitePlugin.get().getWebDavRules(session);
        }
    }

    private class DataSource implements IDataSource<Rule> {
        public void detach() {

        }

        public IModel<Rule> model(Rule object) {
            return new Model<Rule>(object);
        }

        public void query(IQuery query, IQueryResult result) {
            List<Rule> rules = RulesPanel.this.getModelObject().getRules(true);
            result.setTotalCount(rules.size());
            result.setItems(rules.iterator());
        }
    }

    private final class SubmitColumn extends SubmitCancelColumn {
        private SubmitColumn(String columnId, IModel headerModel) {
            super(columnId, headerModel);
        }

        @Override
        protected void onError(AjaxRequestTarget target, IModel rowModel, WebMarkupContainer rowComponent) {
            target.addComponent(feedback);
        }

        @Override
        protected void onSubmitted(AjaxRequestTarget target, IModel rowModel, WebMarkupContainer rowComponent) {
            target.addComponent(feedback);
            Rule rule = (Rule) rowModel.getObject();
            RulesPanel.this.getModelObject().saveRule(rule);
            dataGrid.markAllItemsDirty();
            super.onSubmitted(target, rowModel, rowComponent);
        }
    }

    private final class PriorityColumn extends EditablePropertyColumn {
        private PriorityColumn(IModel headerModel, String propertyExpression) {
            super(headerModel, propertyExpression);
        }

        @Override
        protected void addValidators(FormComponent component) {
            super.addValidators(component);
            component.setType(Integer.class);
            component.setRequired(true);
        }
    }

    private final class TypeColumn extends EditablePropertyColumn {
        public TypeColumn(IModel headerModel, String propertyExpression) {
            super(headerModel, propertyExpression);
        }

        @Override
        protected EditableCellPanel newCellPanel(String componentId, IModel rowModel, IModel cellModel) {
            return new TypePanel(componentId, this, rowModel, cellModel);
        }

        @Override
        protected CharSequence convertToString(Object object) {
            return object != null ? getString(((Type) object).toString()) : null;
        }
    }

    private final class TemplateColumn extends EditablePropertyColumn {
        public TemplateColumn(String columnId, IModel headerModel) {
            super(columnId, headerModel, null);
        }

        @Override
        public Component newCell(WebMarkupContainer parent, String componentId, final IModel rowModel) {
            String workspace = RulesPanel.this.getModelObject().getSession().getWorkspace().getName();
            return new NodeColumnPanel(componentId, ((Rule) rowModel.getObject()).getTemplateModel(), workspace) {
                @Override
                protected boolean isEditing() {
                    return getGrid().isItemEdited(rowModel);
                }
            };
        }

        @Override
        public boolean isLightWeight(IModel rowModel) {
            return false;
        }
    }
}