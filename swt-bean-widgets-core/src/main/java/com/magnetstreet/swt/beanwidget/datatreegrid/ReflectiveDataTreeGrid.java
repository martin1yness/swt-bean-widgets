package com.magnetstreet.swt.beanwidget.datatreegrid;

import com.magnetstreet.swt.beanwidget.datagrid2.reflective.header.TemplatedColumnHeaderProvider;
import com.magnetstreet.swt.beanwidget.datatreegrid.viewer.TemplatedColumnLabelProvider;
import com.magnetstreet.swt.util.BeanUtil;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ReflectiveDataTreeGrid
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 10/17/11
 */
public abstract class ReflectiveDataTreeGrid<T extends Comparable<T>> extends AbstractDataTreeGrid<T> {
    protected Map<String, Map<Class, Method>> columnRowPropertyMapping = new LinkedHashMap<String, Map<Class, Method>>();
    protected Map<Class, Map<String, Method>> rowColumnPropertyMapping = new LinkedHashMap<Class, Map<String, Method>>();

    public ReflectiveDataTreeGrid(Composite composite, int i) {
        super(composite, i);
        defaultViewerFilter = new ViewerFilter() {
            @Override public boolean select(Viewer viewer, Object parentElement, Object element) {
                Class type =((TreeNode) element).getValue().getClass();
                if(filterDefinitions.containsKey(type)) {
                    for(String columnIdentifier: filterDefinitions.get(type).keySet()) {
                        if(!filterDefinitions.get(type).get(columnIdentifier).checkModelProperty(columnRowPropertyMapping.get(columnIdentifier).get(type)));
                            return false;
                    }
                }
                return true;
            }
            @Override public boolean isFilterProperty(Object element, String property) { return true; }
        };
    }

    /**
     * Binds a generic column identifier to an individual tree level's property. Example: Order has OrderItems,
     * Order.class binds 'Order Number' to 'orderNumber' and OrderItem.class binds 'Order Number' to 'itemNumber'.
     * @param type The class representing the object's type being bound
     * @param columnIdentifier The generic column identifier
     * @param propertyName The specific property name used to map column to row's model object's property.
     * @param <V> The tree level's model object type
     */
    public <V> void bindColumnIdentifier(Class<V> type, String columnIdentifier, String propertyName) {
        if(columnRowPropertyMapping==null || rowColumnPropertyMapping==null) {
            columnRowPropertyMapping = new LinkedHashMap<String, Map<Class, Method>>();
            rowColumnPropertyMapping = new LinkedHashMap<Class, Map<String, Method>>();
        }
        try {
            Method getter = propertyName == null ? null : BeanUtil.getGetterMethodForChainValue(type, propertyName);
            if(!rowColumnPropertyMapping.containsKey(type))
                rowColumnPropertyMapping.put(type, new LinkedHashMap<String, Method>());
            rowColumnPropertyMapping.get(type).put(columnIdentifier, getter);
            if(!columnRowPropertyMapping.containsKey(columnIdentifier))
                columnRowPropertyMapping.put(columnIdentifier, new HashMap<Class, Method>());
            columnRowPropertyMapping.get(columnIdentifier).put(type, getter);
            bindColumnWithDefaultTemplates(columnIdentifier, true);
        } catch (Throwable t) {
            throw new RuntimeException("Implementation error, unable to bind column identifier.", t);
        }
    }

    protected void bindColumnWithDefaultTemplates(String columnIdentifier, boolean sortable) {
        bindHeader(columnIdentifier, new TemplatedColumnHeaderProvider(columnIdentifier));
        for(Class type: columnRowPropertyMapping.get(columnIdentifier).keySet()) {
            bindViewer(type, columnIdentifier, new TemplatedColumnLabelProvider(columnRowPropertyMapping.get(columnIdentifier).get(type)));
            if(sortable)
                bindSorter(type, columnIdentifier, getDefaultColumnSorterComparable(columnRowPropertyMapping.get(columnIdentifier).get(type)));
        }
    }

    protected <R extends Comparable> Comparator<R> getDefaultColumnSorterComparable(final Method getter) {
        return new Comparator<R>() {
            @Override public int compare(R o1, R o2) {
                if(o1==null || o2==null)
                    return 1;
                try {
                    Object property1 = getter.invoke(o1);
                    Object property2 = getter.invoke(o2);
                    if(property1 == null && property2 == null)
                        return 0;
                    if(property1 == null)
                        return 1;
                    if(property2 == null)
                        return -1;
                    return ((Comparable)property1).compareTo((Comparable)property2);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException("Target doesn't have getter method, this is a bug.", e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Getter method isn't public, not a real bean!", e);
                }
            }
        };
    }
}
