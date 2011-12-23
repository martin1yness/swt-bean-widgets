package com.magnetstreet.swt.beanwidget.datagrid2.datatreegrid;

import com.magnetstreet.swt.beanwidget.datagrid2.datatreegrid.filter.ColumnFilter;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeViewer;

import java.util.Collection;
import java.util.List;

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
    public void deselectAllItems();
    public void selectAllItems();

    public <V> Collection<V> getCheckedBeans(Class<V> type);
    public Collection getCheckedBeans();
    public Collection<T> getCheckedRootBeans();

    public <V> Collection<V> getSelectedBeans(Class<V> type);
    public Collection getSelectedBeans();
    public Collection<T> getSelectedRootBeans();

    public Collection getExpandedBeans();
    public void expandBeans(Collection beansToExpand);

    /**
     * Checks the corresponding TreeItems of the beans supplied. If an empty collection or null value is
     * given, #uncheckAllItems() is called. Collection can be mixed types that match children elements in the
     * generated TreeNode tree structure.
     *
     *
     * @param beans Beans to check in the TreeTable
     * @throws com.magnetstreet.swt.exception.InvalidGridStyleException a RuntimeException when the table was not created
     *                                                                  with the SWT.CHECK style
     */
    public void checkBeans(Collection beans);
    /**
     * Selects the corresponding TreeItems of the beans supplied. If an empty collection or null value is
     * given, #deselectAllItems() is called. Collection can be mixed types that match children elements in the
     * generated TreeNode tree structure.
     *
     *
     * @param beans Beans to select in the TreeTable
     * @throws com.magnetstreet.swt.exception.InvalidGridStyleException a RuntimeException when the table was not created
     *                                                                  with the SWT.MULTI style and the size of beans is larger than one.
     */
    public void selectBeans(Collection beans);

    public String captureSerializedColumnWidths();
    public void applySerializedColumnWidths(String widths);

    public Object getTopBean();
    public void setTopBean(Comparable bean);

    /**
     * Non-typed bean getter by criteria
     * @param matcher The criteria to check all the tree grid's beans against
     * @return List of beans matching the given criteria
     */
    public List getBeans(Comparable matcher);
    /**
     * Used to get beans from table that match a given criteria
     * @param type The type of bean being matched (Use non type generic method for multiple types)
     * @param matcher The criteria to check the bean type against
     * @param <V> The type of bean matcher
     * @return List of beans matching the given criteria and object type
     */
    public <V> List<V> getBeans(Class<V> type, Comparable<V> matcher);
    /**
     * @return All top level beans
     */
    public List<T> getBeans();
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
