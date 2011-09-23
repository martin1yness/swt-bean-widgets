package com.magnetstreet.swt.beanwidget.datagrid2.reflective.viewer;

import com.magnetstreet.swt.util.BeanUtil;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TemplatedColumnLabelProvider
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 5/5/11
 */
public abstract class TemplatedColumnLabelProvider<T> extends ColumnLabelProvider {
    private Logger logger = Logger.getLogger(TemplatedColumnLabelProvider.class.getName());

    protected String propertyName, toolTipText;
    protected Font font, toolTipFont;
    protected Color color, background, foreground, toolTipBackgroundColor, toolTipForegroundColor;
    protected Image image, toolTipImage;
    protected Point toolTipShift;
    protected int toolTipTimeDisplayed, toolTipDisplayDelayTime, toolTipStyle;

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
    protected Image doGetToolTipImage(String propertyName, T modelObject) { return toolTipImage; }
    protected Color doGetToolTipBackgroundColor(String propertyName, T modelObject) { return toolTipBackgroundColor; }
    protected Color doGetToolTipForegroundColor(String propertyName, T modelObject) { return toolTipForegroundColor; }
    protected Font doGetToolTipFont(String propertyName, T modelObject) { return toolTipFont; }
    protected Point doGetToolTipShift(String propertyName, T modelObject) { return toolTipShift; }
    protected int doGetToolTipTimeDisplayed(String propertyName, T modelObject) { return toolTipTimeDisplayed; }
    protected int doGetToolTipDisplayDelayTime(String propertyName, T modelObject) { return toolTipDisplayDelayTime; }
    protected int doGetToolTipStyle(String propertyName, T modelObject) { return toolTipStyle; }
    protected String doGetToolTipText(String propertyName, T modelObject) { return doGetText(propertyName, modelObject); }
    protected String doGetText(String propertyName, T modelObject) {
        try {
            Object property = BeanUtil.getFieldChainValueWithGetters(modelObject, propertyName);
            return "" + property.toString();
        } catch(Throwable t) {
            logger.log(Level.FINEST, "Unabe to retrieve property '"+propertyName+"' from model.", t);
            return "";
        }
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


    @Override final public String getToolTipText(Object element) {
        return doGetToolTipText(propertyName, (T)element);
    }
    @Override final public Image getToolTipImage(Object object) {
        return doGetToolTipImage(propertyName, (T)object);
    }
    @Override final public Color getToolTipBackgroundColor(Object object) {
        return doGetToolTipBackgroundColor(propertyName, (T)object);
    }
    @Override final public Color getToolTipForegroundColor(Object object) {
        return doGetToolTipForegroundColor(propertyName, (T)object);
    }
    @Override final public Font getToolTipFont(Object object) {
        return doGetToolTipFont(propertyName, (T)object);
    }
    @Override final public Point getToolTipShift(Object object) {
        return doGetToolTipShift(propertyName, (T)object);
    }
    @Override final public int getToolTipTimeDisplayed(Object object) {
        return doGetToolTipTimeDisplayed(propertyName, (T)object);
    }
    @Override final public int getToolTipDisplayDelayTime(Object object) {
        return doGetToolTipDisplayDelayTime(propertyName, (T)object);
    }
    @Override final public int getToolTipStyle(Object object) {
        return doGetToolTipStyle(propertyName, (T)object);
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
