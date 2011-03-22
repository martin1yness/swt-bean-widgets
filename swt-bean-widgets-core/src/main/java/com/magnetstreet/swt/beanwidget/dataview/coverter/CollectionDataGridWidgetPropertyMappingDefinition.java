package com.magnetstreet.swt.beanwidget.dataview.coverter;

import com.magnetstreet.swt.WidgetPropertyMappingDefinition;
import com.magnetstreet.swt.exception.ViewDataBeanValidationException;
import com.magnetstreet.swt.beanwidget.datagrid.DataGrid;
import com.magnetstreet.swt.beanwidget.datagrid.DataGridFactory;
import com.magnetstreet.swt.beanwidget.dataview.AbstractDataView;
import org.eclipse.swt.SWT;

import java.util.ArrayList;
import java.util.Collection;

/**
 * CollectionDataGridWidgetPropertyMappingDefinition
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Jan 12, 2010
 * @since Jan 12, 2010
 */
public class CollectionDataGridWidgetPropertyMappingDefinition extends WidgetPropertyMappingDefinition<DataGrid, Collection> {
    /**
     * {@inheritDoc}
     */
    public void convertPropertyToWidget(Collection property, DataGrid widget) {
        for(Object bean: property)
            widget.addDataBean(bean);
    }
    /**
     * {@inheritDoc}
     * @return the ArrayList that points to the collection of the objects stored in
     *         the original bean's property collection.
     */
    public Collection convertWidgetToProperty(DataGrid widget) throws ViewDataBeanValidationException {
        return widget.getAllBeans();
    }
    /**
     * {@inheritDoc}
     */
    public DataGrid createWidget(AbstractDataView parent, int style) {
        return DataGridFactory.getInstance().getEditableDataGrid(new ArrayList(), null, parent, SWT.FULL_SELECTION);
    }
}
