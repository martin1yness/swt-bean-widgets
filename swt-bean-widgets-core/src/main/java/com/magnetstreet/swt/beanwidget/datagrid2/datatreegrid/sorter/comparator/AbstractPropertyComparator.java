package com.magnetstreet.swt.beanwidget.datagrid2.datatreegrid.sorter.comparator;

import com.magnetstreet.swt.util.BeanUtil;

import java.util.Comparator;

/**
 * AbstractPropertyComparator
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 10/13/11
 */
public abstract class AbstractPropertyComparator<T, R extends Comparable> implements Comparator<T> {
    private String propertyChain;

    public AbstractPropertyComparator(String propertyChain) {
        this.propertyChain = propertyChain;

    }

    protected R getPropertyValue(T bean) {
        return (R)BeanUtil.getFieldChainValueWithGetters(bean, propertyChain);
    }

    @Override public int compare(T o1, T o2) {
        R objA = getPropertyValue(o1);
        R objB = getPropertyValue(o2);
        if(objA==null && objB==null)
            return 0;
        if(objA==null)
            return 1;
        if(objB==null)
            return -1;
        return objA.compareTo(objB);
    }
}
