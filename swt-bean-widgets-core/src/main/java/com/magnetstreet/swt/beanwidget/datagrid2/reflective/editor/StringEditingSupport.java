package com.magnetstreet.swt.beanwidget.datagrid2.reflective.editor;

import com.magnetstreet.swt.beanwidget.datagrid2.DataGrid;
import com.magnetstreet.swt.beanwidget.datagrid2.validator.IDataGridCellValidator;
import com.magnetstreet.swt.util.BeanUtil;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Table;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * StringEditingSupport
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 5/5/11
 */
public class StringEditingSupport<T> extends AbstractTemplatedDataGridCellEditingSupport<T, String> {
    private Logger logger = Logger.getLogger(StringEditingSupport.class.getSimpleName());

    public StringEditingSupport(String propertyName, DataGrid<T> tDataGrid) {
        super(propertyName, tDataGrid);
    }
    @Override protected CellEditor instantiateCellEditor(Table composite) {
        return new TextCellEditor(composite);
    }
    @Override protected IDataGridCellValidator instantiateValidator() {
        return null;
    }
    @Override protected String getControlValue(T modelObject) {
        return BeanUtil.getFieldValueWithGetter(modelObject, propertyName).toString();
    }
    @Override protected void setModelValue(T modelObject, String newValidValueFromControl) {
        try {
            BeanUtil.setFieldValueWithSetter(modelObject, propertyName, newValidValueFromControl);
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Unable to set value: " + newValidValueFromControl, t);
        }
    }
}
