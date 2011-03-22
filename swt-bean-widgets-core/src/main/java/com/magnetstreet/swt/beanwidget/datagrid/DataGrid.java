package com.magnetstreet.swt.beanwidget.datagrid;

import com.magnetstreet.swt.beanwidget.Widget;
import com.magnetstreet.swt.util.TableColumnComparator;

import java.util.Collection;
import java.util.List;

/**
 * DataGrid
 *
 * Represents a table like widget that encapsulates many data views for
 * read or editing.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Jul 28, 2009
 * @since Jul 28, 2009
 */
public interface DataGrid<T> extends Widget {
    /**
     * Allows custom compare algorithm to be used when ordering data grid by a column.
     * @param colNumber The column ID that this comparator should be applied to
     * @param comparatorAlgorithm The aglorithm being applied.
     */
    public void setColumnComparator(int colNumber, TableColumnComparator comparatorAlgorithm);

    /**
     * Allows columns to be hidden from view, useful for data that is not entered
     * by a users.
     * @param colNumber
     * @param visiable
     */
    public void setColumnVisible(int colNumber, boolean visiable);

    /**
     * Must be called to make display changes visible.
     */
    public void redraw();

    /**
     * Used to populate the data grid in a batch
     * @param beans
     */
    public void addAllDataBeans(Collection<T> beans);
    /**
     * Used to populate the data grid with data
     * @param bean The bean to be added to grid
     */
    public void addDataBean(T bean);

    /**
     * Removes a data bean from the grid, doesn't necessarily redraw!
     * @param bean
     */
    public void removeBean(T bean);

    /**
     * Removes all of the data beans attached to the grid, doesn't necessarily redraw!
     */
    public void removeAllBeans();

    /**
     * Adds a data bean filter which will filter out display results that would normally
     * be all displayed.
     * @param filter The DataGridFilter to use when building table.
     */
    public void addDataFilter(String name, String description, DataGridFilter<T> filter);
    /**
     * Removes a data filter based on the name it was created with.
     * @param name
     */
    public void removeDataFilter(String name);
    /**
     * Removes all data filters, on next redraw all data will be displayed.
     */
    public void clearDataFilters();

    /**
     * @return The bean(s) associated with the row(s) selected in the table
     */
    public List<T> getSelectedBeans();

    /**
     * @return A complete list of all beans that had been added to the grid.
     */
    public List<T> getAllBeans();
}

