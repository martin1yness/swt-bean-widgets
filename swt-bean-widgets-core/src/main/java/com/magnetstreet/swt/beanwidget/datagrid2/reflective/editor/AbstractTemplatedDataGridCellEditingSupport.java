package com.magnetstreet.swt.beanwidget.datagrid2.reflective.editor;

import com.magnetstreet.swt.beanwidget.datagrid2.DataTableGrid;
import com.magnetstreet.swt.beanwidget.datagrid2.editor.AbstractDataGridCellEditingSupport;

/**
 * AbstractTemplatedDataGridCellEditingSupport
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 5/5/11
 */
public abstract class AbstractTemplatedDataGridCellEditingSupport<T,K> extends AbstractDataGridCellEditingSupport<T,K> {
    protected String propertyName;
    public AbstractTemplatedDataGridCellEditingSupport(String propertyName, DataTableGrid<T> dataTableGrid) {
        super(dataTableGrid);
        this.propertyName = propertyName;
    }
}
