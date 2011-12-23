package com.magnetstreet.swt.example.datagrid2.reflective;

import com.magnetstreet.swt.beanwidget.datagrid2.datatreegrid.reflective.ReflectiveDataTableGrid;
import com.magnetstreet.swt.beanwidget.datagrid2.datatreegrid.reflective.editor.StringEditingSupport;
import com.magnetstreet.swt.example.bean.Division;
import org.eclipse.swt.widgets.Composite;

/**
 * DivisionReflectiveDataTableGrid
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 9/21/11
 */
public class DivisionReflectiveDataTableGrid extends ReflectiveDataTableGrid<Division> {
    public DivisionReflectiveDataTableGrid(Composite composite, int i) {
        super(composite, i);
    }

    @Override protected void preInit() {
        id_immutable();
        name_mutable();
        description_mutable();
    }

    protected void id_immutable() {
        bindColumnWithDefaults("id", true);
    }

    protected void name_mutable() {
        bindColumnWithDefaults("name", true);
        bindEditor("name", new StringEditingSupport<Division>("name", this) {
            @Override protected void setModelValue(Division modelObject, String newValidValueFromControl) {
                super.setModelValue(modelObject, newValidValueFromControl);
            }
        });
    }

    protected void description_mutable() {
        bindColumnWithDefaults("description", true);
        bindEditor("description", new StringEditingSupport<Division>("description", this) {
            @Override protected void setModelValue(Division modelObject, String newValidValueFromControl) {
                super.setModelValue(modelObject, newValidValueFromControl);
            }
        });
    }
}
