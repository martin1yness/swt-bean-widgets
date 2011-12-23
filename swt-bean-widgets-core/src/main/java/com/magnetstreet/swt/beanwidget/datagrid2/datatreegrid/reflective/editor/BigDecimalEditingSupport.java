package com.magnetstreet.swt.beanwidget.datagrid2.datatreegrid.reflective.editor;

import com.magnetstreet.swt.beanwidget.datagrid2.datatreegrid.DataTableGrid;
import com.magnetstreet.swt.beanwidget.datagrid2.datatreegrid.validator.AbstractTooltipDataGridCellValidator;
import com.magnetstreet.swt.beanwidget.datagrid2.datatreegrid.validator.IDataGridCellValidator;
import com.magnetstreet.swt.util.BeanUtil;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Table;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * BigDecimalEditingSupport
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 5/5/11
 */
public class BigDecimalEditingSupport<T> extends AbstractTemplatedDataGridCellEditingSupport<T, String>{
    private Logger logger = Logger.getLogger(BigDecimalEditingSupport.class.getSimpleName());

    public BigDecimalEditingSupport(String propertyName, DataTableGrid<T> dataTableGrid) {
        super(propertyName, dataTableGrid);
    }
    @Override protected CellEditor instantiateCellEditor(Table composite) {
        return new TextCellEditor(composite);
    }
    @Override protected IDataGridCellValidator instantiateValidator() {
        return new AbstractTooltipDataGridCellValidator<String>() {
            @Override public String isValid(Object value) {
                try {
                    Number number = NumberFormat.getNumberInstance().parse((String)value);
                    new BigDecimal((String)value);
                    return null;
                } catch (Throwable nfe) {
                    return "Unable to parse number: " + nfe.getMessage();
                }
            }
        };
    }
    @Override protected String getControlValue(T modelObject) {
        return BeanUtil.getFieldValueWithGetter(modelObject, propertyName).toString();
    }
    @Override protected void setModelValue(T modelObject, String newValidValueFromControl) {
        try {
            Number number = NumberFormat.getNumberInstance().parse(newValidValueFromControl);
            BeanUtil.setFieldValueWithSetter(modelObject, propertyName, new BigDecimal(number.toString()));
        } catch (Throwable e) {
            logger.log(Level.SEVERE, "Unable to set value: " + newValidValueFromControl, e);
        }
    }
}
