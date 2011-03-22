package com.magnetstreet.swt.beanwidget.datagrid;

/**
 * DataGridFilter
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Dec 17, 2009
 * @since Dec 17, 2009
 */
public abstract class DataGridFilter<T> {
    public String name;
    public String description;

    /**
     * Determines if a data bean should be included in a data
     * grid's view. Inclusion algorithm must be defined by implementor.
     * @param bean The data bean to check for inclusion
     * @return True if bean should be included, false otherwise.
     */
    abstract public boolean include(T bean);

    @Override public boolean equals(Object that) {
        if(that instanceof DataGridFilter) {
            return ( ((DataGridFilter)that).name == this.name );
        }
        return false;
    }

    @Override public int hashCode() {
        return name.hashCode();
    }
}