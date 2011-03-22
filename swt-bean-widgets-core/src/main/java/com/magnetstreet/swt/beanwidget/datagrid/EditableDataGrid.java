package com.magnetstreet.swt.beanwidget.datagrid;

import com.magnetstreet.swt.beanwidget.callback.SaveBeanCallback;

/**
 * EditableDataGrid
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Dec 17, 2009
 * @since Dec 17, 2009
 */
public interface EditableDataGrid<T> extends DataGrid<T>{
    /**
     * Allows application to enable/disable editing for particular columns in a data grid
     * @param colNumber The column ID this property affects
     * @param editable True to enable editing, false to disable it.
     */
    public void setColumnEditable(int colNumber, boolean editable);

    /**
     * Allows consuming classes to define actions that persist a bean once it is
     * updated in the table. Designed to work with the ERB and Hibernate merge
     * function.
     * @param saveBeanCallback
     */
    public void setSaveBeanCallback(SaveBeanCallback<T> saveBeanCallback);
}
