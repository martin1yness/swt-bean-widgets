package com.magnetstreet.swt.beanwidget.datagrid2.reflective.editor;

import com.magnetstreet.swt.beanwidget.datagrid2.DataGrid;
import com.magnetstreet.swt.beanwidget.datagrid2.editor.DateTimeCellEditor;
import com.magnetstreet.swt.beanwidget.datagrid2.validator.IDataGridCellValidator;
import com.magnetstreet.swt.util.BeanUtil;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CalendarEditingSupport
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 5/5/11
 */
public class CalendarEditingSupport<T> extends AbstractTemplatedDataGridCellEditingSupport<T, Calendar> {
    private Logger logger = Logger.getLogger(CalendarEditingSupport.class.getSimpleName());

    public CalendarEditingSupport(String propertyName, DataGrid<T> tDataGrid) {
        super(propertyName, tDataGrid);
    }
    @Override protected CellEditor instantiateCellEditor(Table composite) {
        return new DateTimeCellEditor(composite, SWT.DATE|SWT.MEDIUM);
    }
    @Override protected IDataGridCellValidator instantiateValidator() {
        return null;
    }
    @Override protected Calendar getControlValue(T modelObject) {
        return (Calendar)BeanUtil.getFieldValueWithGetter(modelObject, propertyName);
    }
    @Override protected void setModelValue(T modelObject, Calendar newValidValueFromControl) {
        try {
            String setterName = BeanUtil.getSetterMethodNameForField(propertyName);
            modelObject.getClass().getDeclaredMethod(setterName, Calendar.class).invoke(modelObject, newValidValueFromControl);
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Unable to set Calendar value.", t);
        }
    }
}
