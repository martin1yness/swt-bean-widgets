package com.magnetstreet.swt.beanwidget.datagrid2.reflective.editor;

import com.magnetstreet.swt.beanwidget.datagrid2.DataGrid;
import com.magnetstreet.swt.beanwidget.datagrid2.validator.IDataGridCellValidator;
import com.magnetstreet.swt.util.BeanUtil;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SelectableObjectEditingSupport
 *
 * Provides a combo box style selector using a collection of objects to map indexes and abstract
 * functions to provide to string methods on any type of collection of objects.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 5/5/11
 */
public abstract class SelectableObjectEditingSupport<T, CT> extends AbstractTemplatedDataGridCellEditingSupport<T, Integer>{
    private Logger logger = Logger.getLogger(SelectableObjectEditingSupport.class.getSimpleName());

    protected String[] objStrs;
    protected CT[] objectArr;

    public SelectableObjectEditingSupport(String propertyName, DataGrid<T> tDataGrid) {
        super(propertyName, tDataGrid);
    }
    @Override protected CellEditor instantiateCellEditor(Table composite) {
        if(objectArr==null) {
            objectArr = getSelectables();
            objStrs = new String[objectArr.length];
        }

        for(int i=0; i<objectArr.length; i++)
            objStrs[i] = selectableToString(objectArr[i]);

        return new ComboBoxCellEditor(composite, objStrs, SWT.READ_ONLY);
    }
    @Override protected IDataGridCellValidator instantiateValidator() {
        return null;
    }
    @Override protected Integer getControlValue(T modelObject) {
        if(objectArr==null) {
            objectArr = getSelectables();
            objStrs = new String[objectArr.length];
        }

        try {
            CT obj = (CT)BeanUtil.getFieldValueWithGetter(modelObject, propertyName);
            if(obj == null)
                return -1;
            String referencedStr = selectableToString(obj);
            for(int i=0; i<objStrs.length; i++) {
                if( objStrs[i].equals(referencedStr) )
                    return i;
            }
        } catch(Throwable t) {
            logger.log(Level.WARNING, "Unable to set value of combo.", t);
        }
        return cachedValue;
    }
    @Override protected void setModelValue(T modelObject, Integer newValidValueFromControl) {
        try {
            BeanUtil.setFieldValueWithSetter(modelObject, propertyName, objectArr[newValidValueFromControl]);
        } catch (Throwable t) {
            logger.log(Level.WARNING, "Unable to update model object with combo selection.", t);
        }
    }

    protected abstract CT[] getSelectables();
    protected abstract String selectableToString(CT obj);
}
