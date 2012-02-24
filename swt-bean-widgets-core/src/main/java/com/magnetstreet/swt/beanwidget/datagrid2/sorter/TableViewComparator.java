package com.magnetstreet.swt.beanwidget.datagrid2.sorter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import java.util.Comparator;

/**
 * TableViewComparator
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 4/27/11
 * @deprecated Use com.magnetstreet.swt.beanwidget.datagrid2.sorter.DataGridColumnSorter
 */
@Deprecated
public abstract class TableViewComparator extends ViewerComparator {
    protected String property = "";
    protected boolean direction = false; // False DESC, True ASC

    public TableViewComparator() {
        super();
    }

    public TableViewComparator(Comparator comparator) {
        super(comparator);
    }

    public void setProperty(String property) {
        if(this.property.equals(property))
            direction = !direction;
        else {
            this.property = property;
            direction = false;
        }
    }



    @Override public abstract int compare(Viewer viewer, Object e1, Object e2);
}
