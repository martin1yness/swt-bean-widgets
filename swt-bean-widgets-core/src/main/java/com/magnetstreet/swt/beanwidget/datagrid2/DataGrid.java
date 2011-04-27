package com.magnetstreet.swt.beanwidget.datagrid2;

import com.magnetstreet.swt.viewers.ColumnHeaderProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * DataGrid
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 4/27/11
 */
public interface DataGrid<T> {
    /**
     * Mimic's the JFace TableViewer refresh method, builds/reconfigures the viewer and refreshes it's layout.
     */
    public void refresh();

    /**
     * Defines how a column bound to the given property should be sorted when the user chooses to sort by that
     * column. The comparator should function as defined in the Java 1.6 spec.
     * @param property The String name of the property in the bean class that should consider the comparator
     *        when sorted by
     * @param comparator The Comparator to execute when sorted by the particular bean property
     */
    public void bindSorter(String property, Comparator<T> comparator);
    public void bindSorter(Field property, Comparator<T> comparator);
    public void bindSorter(Method getter, Comparator<T> comparator);

    /**
     * Adds a data bean filter which will filter out display results that would normally
     * be all displayed.
     * @param property String name of the property in the bean class that should be filtered on
     *        using the given filter.
     * @param valueGetter The callable which will return the string to match against the table's label provider for
     *        each row. In the case of Text/Combo widgets the 'getText()' method, the callable is used to allow
     *        for advanced custom functionality with special widgets and conversions that maybe needed.
     */
    public void bindFilter(String property, Callable<String> valueGetter);
    public void bindFilter(Field property, Callable<String> valueGetter);
    public void bindFilter(Method getter, Callable<String> valueGetter);

    /**
     * Defines how a column can providing editing support to each row's cell. Uses the JFaces editing support
     * interface to define the mapping between the GUI component and the backing object as well as the
     * control to be used to display the editable column.
     * @param property The name of the property in the bean to bind this editor
     * @param editingSupportDef The JFace EditingSupport implementation
     */
    public void bindEditor(String property, EditingSupport editingSupportDef);
    public void bindEditor(Field property, EditingSupport editingSupportDef);
    public void bindEditor(Method getter, EditingSupport editingSupportDef);

    /**
     * Defines how a bean property is displayed in the table cell normally, each viewable column requires
     * a bound viewer!
     * @param property The property to bind the column against
     * @param columnLabelProvider
     */
    public void bindViewer(String property, ColumnLabelProvider columnLabelProvider);
    public void bindViewer(Field property, ColumnLabelProvider columnLabelProvider);
    public void bindViewer(Method getter, ColumnLabelProvider columnLabelProvider);

    /**
     * Defines the header and column properties for a particular field, for every header defined
     * there is an expected viewer bound to the same property.
     * @param property The property to bind the column header to
     * @param columnHeaderProvider The column definition including header title
     */
    public void bindHeader(String property, ColumnHeaderProvider columnHeaderProvider);
    public void bindHeader(Field property, ColumnHeaderProvider columnHeaderProvider);
    public void bindHeader(Method getter, ColumnHeaderProvider columnHeaderProvider);

    /**
     * @return The bean(s) associated with the row(s) selected in the table
     */
    public List<T> getSelectedBeans();
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
