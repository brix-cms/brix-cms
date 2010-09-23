package brix.plugin.menu.editor.cell;

import brix.plugin.menu.Menu;
import brix.plugin.menu.editor.ReferenceColumnPanel;
import brix.plugin.site.picker.reference.ReferenceEditorConfiguration;
import brix.web.reference.Reference;
import com.inmethod.grid.column.AbstractColumn;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * Created by IntelliJ IDEA.
 * User: korbinianbachl
 * Date: 08.09.2010
 * Time: 21:11:23
 */
public class SwitcherColumn extends AbstractColumn {

    ReferenceEditorConfiguration conf;


    public SwitcherColumn(String id, IModel<String> displayModel, ReferenceEditorConfiguration conf) {
        super(id, displayModel);
        this.conf = conf;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Component newCell(WebMarkupContainer parent, String componentId, final IModel rowModel) {

        IModel<Menu.ChildEntry.MenuType> typeModel = new PropertyModel<Menu.ChildEntry.MenuType>(rowModel, "entry.menuType");
        IModel<Reference> referenceModel = new PropertyModel<Reference>(rowModel, "entry.reference");
        IModel<String> labelOrCodeModel = new PropertyModel<String>(rowModel, "entry.labelOrCode");

        return new SwitcherCellPanel(componentId, typeModel,referenceModel, labelOrCodeModel, conf) {

            @Override
            boolean isEditing() {
                return getGrid().isItemEdited(rowModel);
            }
        };
    }

}
