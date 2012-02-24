package com.magnetstreet.swt.beanwidget.datagrid2.sorter;

import com.magnetstreet.swt.beanwidget.datagrid2.viewer.BeanValueProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * DataGridColumnSorter
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 2012-02-22
 */
public class DataGridColumnSorter extends ViewerComparator {
    protected String propertyName = "";    
    protected boolean direction = false; // False DESC, True ASC
    private Map<Object, Integer> beanToCategory = new HashMap<Object, Integer>();

    public DataGridColumnSorter(Comparator comparator, Map<Object, Integer> beanToCategory, boolean direction) {
        super(comparator);
        this.beanToCategory = beanToCategory;
        this.direction = direction;
    }

    public boolean getDirection() {
        return direction;
    }

    public void setDirection(boolean direction) {
        this.direction = direction;
    }

    @Override public int compare(Viewer viewer, Object e1, Object e2) {
        int cat1 = category(e1);
        int cat2 = category(e2);

        if (cat1 != cat2) {
            return cat1 - cat2;
        }

        return ((direction) ? 1 : -1) * getComparator().compare(e1, e2);
    }

    @Override public int category(Object element) {
        if(beanToCategory.containsKey(element) && beanToCategory.get(element)!=null)
            return beanToCategory.get(element);
        return super.category(element);
    }
}
