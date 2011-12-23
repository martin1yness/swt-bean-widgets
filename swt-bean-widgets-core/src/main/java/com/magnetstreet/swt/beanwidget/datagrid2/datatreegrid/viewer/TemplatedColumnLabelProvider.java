package com.magnetstreet.swt.beanwidget.datagrid2.datatreegrid.viewer;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import java.lang.reflect.Method;

/**
 * TemplatedColumnLabelProvider
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 5/5/11
 * @param <T> The root type of the data tree grid.
 */
public class TemplatedColumnLabelProvider<T, P> extends ColumnLabelProvider {
    private Method propertyGetter;

    public TemplatedColumnLabelProvider(Method getter) {
        super();
        propertyGetter = getter;
    }

    @Override public String getText(Object element) {
        if(propertyGetter==null)
            return "";
        try {
            propertyGetter.setAccessible(true);
            return ((P)propertyGetter.invoke((T)element)).toString();
        } catch (Throwable t) {
            throw new RuntimeException("Unable to retrieve viewer with reflection, widget must be mis-configured.", t);
        }
    }
}
