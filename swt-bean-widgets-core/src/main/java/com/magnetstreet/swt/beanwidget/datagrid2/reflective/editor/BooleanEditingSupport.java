package com.magnetstreet.swt.beanwidget.datagrid2.reflective.editor;

import com.magnetstreet.swt.beanwidget.datagrid2.DataTableGrid;
import com.magnetstreet.swt.beanwidget.datagrid2.validator.IDataGridCellValidator;
import com.magnetstreet.swt.util.BeanUtil;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.swt.widgets.Table;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * BooleanEditingSupport
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 5/5/11
 */
public class BooleanEditingSupport<T>  extends AbstractTemplatedDataGridCellEditingSupport<T, Boolean> {
    private Logger logger = Logger.getLogger(BooleanEditingSupport.class.getSimpleName());

    public BooleanEditingSupport(String propertyName, DataTableGrid<T> dataTableGrid) {
        super(propertyName, dataTableGrid);
    }
    @Override protected CellEditor instantiateCellEditor(Table composite) {
        return new CheckboxCellEditor(composite);
    }
    @Override protected IDataGridCellValidator instantiateValidator() {
        return null;
    }
    @Override protected Boolean getControlValue(T modelObject) {
        return (Boolean)BeanUtil.getFieldValueWithGetter(modelObject, propertyName);
    }
    @Override protected void setModelValue(T modelObject, Boolean newValidValueFromControl) {
        try {
            BeanUtil.setFieldValueWithSetter(modelObject, propertyName, newValidValueFromControl);
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Unable to set boolean value.", t);
        }
    }
}
