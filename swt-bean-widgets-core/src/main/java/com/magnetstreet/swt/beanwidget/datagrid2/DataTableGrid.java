package com.magnetstreet.swt.beanwidget.datagrid2;

import com.magnetstreet.swt.beanwidget.datagrid2.filter.ColumnFilter;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * DataTableGrid
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 4/27/11
 */
public interface DataTableGrid<T> {
    /**
     * Mimic's the JFace TableViewer refresh method, builds/reconfigures the viewer and refreshes it's layout.
     */
    public void refresh();

    /**
     * Adds a data bean filter which will filter out display results that would normally
     * be all displayed.
     * @param property String name of the property in the bean class that should be filtered on
     *        using the given filter.
     * @param valueGetter The callable which will return the string to match against the table's label provider for
     *        each row. In the case of Text/Combo widgets the 'getText()' method, the callable is used to allow
     *        for advanced custom functionality with special widgets and conversions that maybe needed.
     * @deprecated This filter type forces the use of Regular Expressions, a more generic filter is provided with
     *             the ColumnFilter interface.
     */
    @Deprecated public void bindFilter(String property, Callable<String> valueGetter);
    @Deprecated public void bindFilter(Field property, Callable<String> valueGetter);
    @Deprecated public void bindFilter(Method getter, Callable<String> valueGetter);

    /**
     * Adds a data bean filter which will filter out display results that would normally
     * be all displayed.
     * @param property String name of the property in the bean class that should be filtered on
     *        using the given filter.
     * @param valueGetter The callable which will return the string to match against the table's label provider for
     *        each row. In the case of Text/Combo widgets the 'getText()' method, the callable is used to allow
     *        for advanced custom functionality with special widgets and conversions that maybe needed.
     */
    public void bindFilter(String property, ColumnFilter valueGetter);
    public void bindFilter(Field property, ColumnFilter valueGetter);
    public void bindFilter(Method getter, ColumnFilter valueGetter);

    /**
     * Removes the filter applied to a particular property
     * @param property The property to remove bound filter (if exists)
     */
    public void unbindFilter(String property);
    public void unbindFilter(Field property);
    public void unbindFilter(Method getter);

    /**
     * Allows direct access to the underlying JFace implementation this library builds upon.
     * @return The TableViewer used to create the data grid.
     */
    public TableViewer getTableViewer();

    /**
     * Allows for a double click action to invoke a custom listener. Often used to create dialogs that
     * provide details about the selected item in the table.
     * @param listener
     */
    public void addDoubleClickListener(IDoubleClickListener listener);

    /**
     * @return The bean(s) associated with the row(s) selected in the table
     */
    public List<T> getSelectedBeans();
    /**
     * @return All beans added to the grid
     */
    public Collection<T> getBeans();
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
