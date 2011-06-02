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

package org.brixcms.plugin.site.folder;

import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.IRenderable;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.AbstractColumn;
import com.inmethod.grid.column.AbstractLightWeightColumn;
import com.inmethod.grid.column.PropertyColumn;
import com.inmethod.grid.datagrid.DataGrid;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.Response;
import org.apache.wicket.util.string.Strings;
import org.brixcms.jcr.wrapper.BrixFileNode;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.web.generic.BrixGenericPanel;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListFolderNodesTab extends BrixGenericPanel<BrixNode> {
    public ListFolderNodesTab(String id, IModel<BrixNode> folderModel) {
        super(id, folderModel);

        List<IGridColumn> columns = new ArrayList<IGridColumn>();
        columns.add(new NameColumn(new ResourceModel("name")).setInitialSize(180));
        columns.add(new TypePropertyColumn(new ResourceModel("type")).setInitialSize(80));
        columns.add(new SizeColumn(new ResourceModel("size")).setInitialSize(100));
        columns.add(new MimeTypeColumn(new ResourceModel("mimeType")).setInitialSize(80));
        columns.add(new DatePropertyColumn(new ResourceModel("created"), "created", FolderDataSource.PROPERTY_CREATED)
                .setInitialSize(120));
        columns.add(new PropertyColumn(new ResourceModel("createdBy"), "createdBy",
                FolderDataSource.PROPERTY_CREATED_BY).setInitialSize(90));
        columns.add(new DatePropertyColumn(new ResourceModel("lastModified"), "lastModified",
                FolderDataSource.PROPERTY_LAST_MODIFIED).setInitialSize(120));
        columns.add(new PropertyColumn(new ResourceModel("lastModifiedBy"), "lastModifiedBy",
                FolderDataSource.PROPERTY_LAST_MODIFIED_BY).setInitialSize(110));

        FolderDataSource source = new FolderDataSource() {
            @Override
            BrixNode getFolderNode() {
                return getNode();
            }
        };

        DataGrid grid = new DataGrid("grid", source, columns) {
            @Override
            protected void onRowClicked(AjaxRequestTarget target, IModel rowModel) {
                //((ServletWebRequest) getRequest()).setForceNewVersion(true);

                BrixNode node = (BrixNode) rowModel.getObject();
                Page page = getPage();
                SitePlugin.get().selectNode(this, node);
                getRequestCycle().setResponsePage(page);
            }
        };
        grid.setContentHeight(30, SizeUnit.EM);

        add(grid);
    }

    private BrixNode getNode() {
        return (BrixNode) getModelObject();
    }

    private class NameColumn extends AbstractColumn {
        public NameColumn(IModel<String> headerModel) {
            super("name", headerModel, FolderDataSource.PROPERTY_NAME);
        }

        @Override
        public Component newCell(WebMarkupContainer parent, String componentId, IModel rowModel) {
            return new NamePanel(componentId, rowModel);
        }
    }

    private class NamePanel extends BrixGenericPanel<BrixNode> {
        public NamePanel(String id, final IModel<BrixNode> model) {
            super(id, model);

            Link<?> link;
            add(link = new Link<Void>("select") {
                @Override
                public void onClick() {
                    BrixNode node = model.getObject();
                    SitePlugin.get().selectNode(this, node);
                }

                @Override
                protected void onComponentTag(ComponentTag tag) {
                    if (model.getObject().isFolder()) {
                        tag.put("class", "brix-site-folder-node");
                    }
                    super.onComponentTag(tag);
                }
            });

            IModel<String> labelModel;
            if (model.getObject().getDepth() < ListFolderNodesTab.this.getModelObject().getDepth()) {
                labelModel = new Model<String>("..");
            } else {
                labelModel = new PropertyModel<String>(model, "userVisibleName");
            }
            link.add(new Label("label", labelModel));
        }
    }

    private static class TypePropertyColumn extends PropertyColumn {
        public TypePropertyColumn(IModel<String> headerModel) {
            super("type", headerModel, "userVisibleType", FolderDataSource.PROPERTY_TYPE);
        }
    }

    private class MimeTypeColumn extends AbstractLightWeightColumn {
        public MimeTypeColumn(IModel<String> headerModel) {
            super("mimeType", headerModel, FolderDataSource.PROPERTY_MIME_TYPE);
        }

        @Override
        public IRenderable newCell(IModel rowModel) {
            return new IRenderable() {
                public void render(IModel rowModel, Response response) {
                    BrixNode node = (BrixNode) rowModel.getObject();
                    if (node instanceof BrixFileNode) {
                        String mime = ((BrixFileNode) node).getMimeType();
                        if (mime != null) {
                            response.write(Strings.escapeMarkup(mime));
                        }
                    }
                }
            };
        }
    }

    private class SizeColumn extends AbstractLightWeightColumn {
        public SizeColumn(IModel<String> headerModel) {
            super("size", headerModel, FolderDataSource.PROPERTY_SIZE);
        }

        @Override
        public IRenderable newCell(IModel rowModel) {
            return new IRenderable() {
                public void render(IModel rowModel, Response response) {
                    BrixNode node = (BrixNode) rowModel.getObject();
                    if (node instanceof BrixFileNode) {
                        Long size = ((BrixFileNode) node).getContentLength();

                        response.write(size.toString());
                        response.write(" bytes");
                    }
                }
            };
        }
    }

    private static class DatePropertyColumn extends PropertyColumn {
        public DatePropertyColumn(IModel<String> headerModel, String propertyExpression, String sortProperty) {
            super(headerModel, propertyExpression, sortProperty);
        }

        @Override
        protected CharSequence convertToString(Object object) {
            if (object == null) {
                return "";
            } else {
                DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
                return df.format((Date) object);
            }
        }
    }
}
