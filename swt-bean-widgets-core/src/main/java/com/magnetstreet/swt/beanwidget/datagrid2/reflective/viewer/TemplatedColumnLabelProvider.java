package com.magnetstreet.swt.beanwidget.datagrid2.reflective.viewer;

import com.magnetstreet.swt.util.BeanUtil;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * TemplatedColumnLabelProvider
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 5/5/11
 */
public abstract class TemplatedColumnLabelProvider<T> extends ColumnLabelProvider {
    protected String propertyName;
    protected Font font;
    protected Color color, background, foreground;
    protected Image image;

    public TemplatedColumnLabelProvider() {
        super();
    }
    protected TemplatedColumnLabelProvider(String propertyName) {
        super();
        this.propertyName = propertyName;
    }
    protected TemplatedColumnLabelProvider(String propertyName, Font font, Color color, Color background, Color foreground, Image image) {
        this.propertyName = propertyName;
        this.font = font;
        this.color = color;
        this.background = background;
        this.foreground = foreground;
        this.image = image;
    }

    protected Font doGetFont(String propertyName, T modelObject) { return font; }
    protected Color doGetColor(String propertyName, T modelObject) { return color; }
    protected Color doGetBackground(String propertyName, T modelObject) { return background; }
    protected Color doGetForeground(String propertyName, T modelObject) { return foreground; }
    protected Image doGetImage(String propertyName, T modelObject) { return image; }
    protected String doGetText(String propertyName, T modelObject) {
        Object property = BeanUtil.getFieldValueWithGetter(modelObject, propertyName);
        if(property == null)
            return "";
        return "" + BeanUtil.getFieldValueWithGetter(modelObject, propertyName);
    }

    @Override final public Font getFont(Object element) {
        return doGetFont(propertyName, (T)element);
    }
    @Override final public Color getBackground(Object element) {
        return doGetBackground(propertyName, (T)element);
    }
    @Override final public Color getForeground(Object element) {
        return doGetForeground(propertyName, (T)element);
    }
    @Override final public Image getImage(Object element) {
        return doGetImage(propertyName, (T)element);
    }
    @Override final public String getText(Object element) {
        return doGetText(propertyName, (T)element);
    }

    final public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
    final public void setFont(Font font) {
        this.font = font;
    }
    final public void setColor(Color color) {
        this.color = color;
    }
    final public void setBackground(Color background) {
        this.background = background;
    }
    final public void setForeground(Color foreground) {
        this.foreground = foreground;
    }
    final public void setImage(Image image) {
        this.image = image;
    }
}
