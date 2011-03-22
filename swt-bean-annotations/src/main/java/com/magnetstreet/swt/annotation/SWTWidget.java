package com.magnetstreet.swt.annotation;


import java.lang.annotation.*;

/**
 * SWT Widget
 *
 * Used to annotate a property that can be displayed using a SWT widget.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Nov 23, 2009
 * @since Nov 23, 2009
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SWTWidget {
    /**
     * @return A label text value to display next to widget, leave blank for no label
     *         location of display will depend on automatic layout algorithm or user
     *         customizations. Some generated layouts will completely ignore the labelText.
     */
    String labelText() default "";

    /**
     * @return A recommended width that will be used to generate the actual height. The unit
     *         type is arbitrary but can be considered a pixel.
     */
    int widthHint() default 50;

    /**
     * @return A recommended height that will be used to generate the actual height. The unit
     *         type is arbitrary but can be considered a pixel.
     */
    int heightHint() default 50;

    /**
     * @return Depicts whether the widget should default to read-only
     */
    boolean readOnly() default false;
}