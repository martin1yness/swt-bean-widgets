package com.magnetstreet.swt.beanwidget.datagrid2.datatreegrid.reflective;

import com.magnetstreet.swt.beanwidget.datagrid2.datatreegrid.AbstractDataTableGrid;
import com.magnetstreet.swt.beanwidget.datagrid2.datatreegrid.reflective.header.TemplatedColumnHeaderProvider;
import com.magnetstreet.swt.beanwidget.datagrid2.datatreegrid.reflective.viewer.TemplatedColumnLabelProvider;
import com.magnetstreet.swt.util.StringUtil;
import com.magnetstreet.swt.util.BeanUtil;
import org.eclipse.swt.widgets.Composite;

import java.util.Comparator;

/**
 * ReflectiveDataTableGrid
 *
 * Uses reflection and typing to simplify the creation of data grids.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 5/5/11
 */
public abstract class ReflectiveDataTableGrid<T> extends AbstractDataTableGrid<T> {
    public ReflectiveDataTableGrid(Composite composite, int i) {
        super(composite, i);
    }

    protected void bindColumnWithDefaults(String propertyName, boolean sortable) {
        bindHeader(propertyName, getDefaultColumnHeaderTemplate(propertyName));
        bindViewer(propertyName, getDefaultColumnLabelProvider(propertyName));
        if(sortable)
            bindSorter(propertyName, getDefaultColumnSorterComparable(propertyName));
    }

    protected Comparator<T> getDefaultColumnSorterComparable(final String propertyName) {
        return new Comparator<T>() {
            @Override public int compare(T o1, T o2) {
                if(o1==null || o2==null)
                    return 1;
                Object property1 = BeanUtil.getFieldValueWithGetter(o1, propertyName);
                Object property2 = BeanUtil.getFieldValueWithGetter(o2, propertyName);
                if(property1 == null && property2 == null)
                    return 0;
                if(property1 == null)
                    return 1;
                if(property2 == null)
                    return -1;
                return ((Comparable)property1).compareTo((Comparable)property2);
            }
        };
    }

    protected TemplatedColumnLabelProvider<T> getDefaultColumnLabelProvider(String propertyName) {
        return new TemplatedColumnLabelProvider<T>(propertyName) { };
    }

    protected TemplatedColumnHeaderProvider<T> getDefaultColumnHeaderTemplate(String propertyName) {
        return new TemplatedColumnHeaderProvider<T>(StringUtil.camelCaseToTitle(propertyName));
    }


}
