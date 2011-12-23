package com.magnetstreet.swt.beanwidget.datagrid2;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

/**
 * AbstractDataGrid
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 2011-12-23
 */
public abstract class AbstractDataGrid<T> extends Composite {
    protected Viewer viewer;

    public AbstractDataGrid(Composite parent, int style) {
        super(parent, style);
    }

    public Viewer getViewer() {
        return viewer;
    }

    public void setViewer(Viewer viewer) {
        this.viewer = viewer;
    }
}
