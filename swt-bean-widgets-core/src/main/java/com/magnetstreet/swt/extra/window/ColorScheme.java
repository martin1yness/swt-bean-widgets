package com.magnetstreet.swt.extra.window;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import java.util.HashMap;

/**
 * ColorScheme
 *
 * Represents a color set used for forms and display. Allows customization of colors among
 * various instances of applications or even various windows.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since Sep 7, 2010
 */
public class ColorScheme {
    private Log logger = LogFactory.getLog(ColorScheme.class);
    private String name;

    private Image defaultBackgroundImage;
    private Color defaultBackgroundColor;
    private Color defaultForegroundColor;

    private HashMap<Class, Image> widgetDefaultBackgroundImage = new HashMap<Class, Image>();
    private HashMap<Class, Color> widgetDefaultBackgroundColor = new HashMap<Class, Color>();
    private HashMap<Class, Color> widgetDefaultForegroundColor = new HashMap<Class, Color>();

    public ColorScheme(String name) {
        this.name = name;
    }

    private Color getColor(int r, int g, int b){
        return new Color(Display.getDefault(), r, g ,b);
    }
    private Image getImage(String location) {
        if(location == null)
            return null;
        try {
            return new Image(Display.getDefault(), this.getClass().getClassLoader().getResourceAsStream(location));
        } catch(Throwable t) {
            logger.error("Unable to create background image from supplied location: " + location, t);
            return null;
        }
    }
    private void setDefaultColor(boolean bg, int red, int green, int blue) {
        if(bg)
            defaultBackgroundColor = getColor(red, green, blue);
        else
            defaultForegroundColor = getColor(red, green, blue);
    }
    private void mapWidgetColor(boolean bg, Class widget, int red, int green, int blue) {
        if(bg) {
            if(widgetDefaultBackgroundImage.containsKey(widget)) {
                Image tmp = widgetDefaultBackgroundImage.remove(widget);
                logger.info("Overriding widget background image ("+tmp+") with color("+red+","+green+","+blue+").");
            }
            widgetDefaultBackgroundColor.put(widget, getColor(red, green, blue));
        } else {
            widgetDefaultForegroundColor.put(widget, getColor(red, green, blue));
        }
    }

    public void setDefaultBackgroundImage(String location) {
        defaultBackgroundImage = getImage(location);
    }
    public void setDefaultBackgroundColor(int red, int green, int blue) {
        setDefaultColor(true, red, green, blue);
    }
    public void setDefaultForegroundColor(int red, int green, int blue) {
        setDefaultColor(false, red, green, blue);
    }
    public void mapWidgetBackgroundColorSystemDefault(Class widget) {
        widgetDefaultBackgroundColor.put(widget, null);
    }
    public void mapWidgetBackgroundColor(Class widget, int red, int green, int blue) {
        mapWidgetColor(true, widget, red, green, blue);
    }
    public void mapWidgetForegroundColorSystemDefault(Class widget) {
        widgetDefaultForegroundColor.put(widget, null);
    }
    public void mapWidgetForegroundColor(Class widget, int red, int green, int blue) {
        mapWidgetColor(false, widget, red, green, blue);
    }
    public void mapWidgetBackgroundImage(Class widget, String location) {
        if(widgetDefaultBackgroundColor.containsKey(widget)) {
            Color tmp = widgetDefaultBackgroundColor.remove(widget);
            logger.info("Overriding widget background color("+tmp+") with image ("+location+").");
        }
        widgetDefaultBackgroundImage.put(widget, getImage(location));
    }

    public String getName() { return name; }

    public void applyScheme(Control widget) {
        if(widgetDefaultBackgroundImage.containsKey(widget.getClass())) {
            widget.setBackgroundImage(widgetDefaultBackgroundImage.get(widget.getClass()));
        } else if(widgetDefaultBackgroundColor.containsKey(widget.getClass())) {
            widget.setBackground(widgetDefaultBackgroundColor.get(widget.getClass()));
        } else if(defaultBackgroundImage!=null) {
            widget.setBackgroundImage(defaultBackgroundImage);
        } else if(defaultBackgroundColor!=null) {
            widget.setBackground(defaultBackgroundColor);
        }

        if(widgetDefaultForegroundColor.containsKey(widget.getClass())) {
            widget.setForeground(widgetDefaultForegroundColor.get(widget.getClass()));
        } else if(defaultForegroundColor!=null) {
            widget.setForeground(defaultForegroundColor);
        }            
    }

    @Override protected Object clone() {
        try {
            ColorScheme result = (ColorScheme)super.clone();
            result.widgetDefaultBackgroundImage = (HashMap)widgetDefaultBackgroundImage.clone();
            result.widgetDefaultBackgroundColor = (HashMap)widgetDefaultBackgroundColor.clone();
            result.widgetDefaultForegroundColor = (HashMap)widgetDefaultForegroundColor.clone();
            return result;
        } catch (CloneNotSupportedException e) {
            return null; // never invoked
        }
    }
}
