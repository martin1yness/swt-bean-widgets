package com.magnetstreet.swt.beanwidget.datagrid2;

import com.magnetstreet.swt.beanwidget.datagrid2.sorter.DataGridColumnSorter;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AbstractDataGrid
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 2011-12-23
 */
public abstract class AbstractDataGrid<T> extends Composite {
    protected List<T> beans = new ArrayList<T>();

    private final Map<Object, Integer> beanToCategory = new HashMap<Object, Integer>();
    protected final Class<T> parentType;

    protected ColumnViewer viewer;

    public AbstractDataGrid(Composite parent, int style) {
        super(parent, style);
        parentType = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public ColumnViewer getViewer() {
        return viewer;
    }

    public void setViewer(ColumnViewer viewer) {
        this.viewer = viewer;
    }

    protected void initialize() {

    }

    public void clearBeanToCategoryMappings() {
        beanToCategory.clear();
    }

    protected Map<Object, Integer> getBeanToCategory() { return beanToCategory; }

    /**
     * Groups beans in viewer. When sorting, the category will take precedence then the normal sorting
     * operations will sort within each category.
     * @param bean
     * @param category
     */
    public void bindBeanToCategory(T bean, Integer category) {
        beanToCategory.put(bean, category);
    }

    public void setBeans(Collection<T> beans) { this.beans.clear(); this.beans.addAll(beans); }
    public List<T> getBeans() { return this.beans; }
    public void addBean(T bean) { this.beans.add(bean); }
    public void removeBean(T bean) { this.beans.remove(bean); }
    public void removeAllBeans() { this.beans = new ArrayList<T>(); }
}
