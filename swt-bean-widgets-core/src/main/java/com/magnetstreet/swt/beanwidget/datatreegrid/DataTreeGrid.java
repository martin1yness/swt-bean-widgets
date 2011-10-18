package com.magnetstreet.swt.beanwidget.datatreegrid;

import com.magnetstreet.swt.beanwidget.datagrid2.filter.ColumnFilter;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeViewer;

import java.util.Collection;

/**
 * DataTreeGrid
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 10/11/11
 */
public interface DataTreeGrid<T extends Comparable<T>> {
    /**
     * Mimic's the JFace TableViewer refresh method, builds/reconfigures the viewer and refreshes it's layout.
     */
    public void refresh();

    /**
     * Allows for after instantiation filters to be added to the table to allow users to modify result set in memory.
     * Usually, a call to #refresh() is desirable after a filter control is updated.
     * @param modelType The type class definition the filter is to act upon. (Defines tree level)
     * @param columnIdentifier The identifier used to match bindings of each object to a column.
     * @param columnFilter The typed filter to be used to determine if element should be included in visible tree.
     * @param <V> The type of object the filter is designed to work on, each tree level is of a type.
     */
    public <V> void bindFilter(Class<V> modelType, String columnIdentifier, ColumnFilter<V> columnFilter);

    /**
     * Allows for a double click action to invoke a custom listener. Often used to create dialogs that
     * provide details about the selected item in the table.
     * @param listener
     */
    public void addDoubleClickListener(IDoubleClickListener listener);

    public TreeViewer getTreeViewer();

    public void uncheckAllItems();
    public void checkAllItems();

    public <V> Collection<V> getCheckedBeans(Class<V> type);
    public Collection<T> getCheckedRootBeans();

    public <V> Collection<V> getSelectedBeans(Class<V> type);
    public Collection<T> getSelectedRootBeans();

    public String captureSerializedColumnWidths();
    public void applySerializedColumnWidths(String widths);

    /**
     * Used to populate the data grid in a batch
     * @param beans
     */
    public void setBeans(Collection<T> beans);
    /**
     * Used to populate the data grid with data
     * @param bean The bean to be added to grid
     */
    public void addBean(T bean);
    /**
     * Removes a data bean from the grid, doesn't necessarily redraw!
     * @param bean
     */
    public void removeBean(T bean);
    /**
     * Removes all of the data beans attached to the grid, doesn't necessarily redraw!
     */
    public void removeAllBeans();
}
