package com.magnetstreet.swt.beanwidget.datagrid2.datatreegrid.filter;

/**
 * ColumnFilter
 *
 * Filters work on a property of a model object in DataGrids. So this defines a filter for a column
 * of a datagrid which is why it works on a property instead of the entire model.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 9/21/11
 * T is the property type the filer is filtering against.
 */
public interface ColumnFilter<T> {
    /**
     * Checks if the given model object passes the filtering criteria of the implementation.
     * @param modelObjectProperty The model represented by the row being tested for filtering
     * @return True if the model's data row property should be displayed, false otherwise.
     */
    public boolean checkModelProperty(T modelObjectProperty);
}
