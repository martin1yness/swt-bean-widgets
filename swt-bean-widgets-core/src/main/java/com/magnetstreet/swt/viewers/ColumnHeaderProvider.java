package com.magnetstreet.swt.viewers;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * ColumnHeaderProvider
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 4/27/11
 */
public class ColumnHeaderProvider extends ColumnLabelProvider {
    protected String title, tooltip;
    protected int width;
    protected boolean resizable, moveable;
    protected Image image;

    public ColumnHeaderProvider(String title, String tooltip, int width, boolean resizable, boolean moveable, Image image) {
        this.title = title;
        this.tooltip = tooltip;
        this.width = width;
        this.resizable = resizable;
        this.moveable = moveable;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getTooltip() {
        return tooltip;
    }

    public int getWidth() {
        return width;
    }

    public boolean isResizable() {
        return resizable;
    }

    public boolean isMoveable() {
        return moveable;
    }

    public Image getImage() {
        return image;
    }
}
