package com.magnetstreet.swt.beanwidget.datagrid2.datatreegrid.header;

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

    public String getTooltip() { return getTitle(); }

    public int getWidth() { return getTitle() == null ? 1 : getTitle().length() * 5; }

    public boolean isResizable() { return true; }

    public boolean isMoveable() { return false; }

    public Image getImage() { return null; }
}
