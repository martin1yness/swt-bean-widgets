package com.magnetstreet.swt.beanwidget.datagrid2.header;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * ColumnHeaderProvider
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 4/27/11
 */
public abstract class ColumnHeaderProvider extends ColumnLabelProvider {
    public abstract String getTitle();

    public abstract String getTooltip();

    public abstract int getWidth();

    public abstract boolean isResizable();

    public abstract boolean isMoveable();

    public abstract Image getImage();
}
