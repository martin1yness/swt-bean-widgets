package com.magnetstreet.swt.beanwidget.datatreegrid;

import java.util.Collection;

/**
 * DataTreeGrid
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 10/11/11
 */
public interface DataTreeGrid<T extends Comparable<T>> {
    /**
     * Mimic's the JFace TableViewer refresh method, builds/reconfigures the viewer and refreshes it's layout.
     */
    public void refresh();

    public Collection<?> getSelectedBeans();

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
