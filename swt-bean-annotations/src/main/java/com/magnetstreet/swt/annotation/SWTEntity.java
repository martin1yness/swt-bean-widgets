package com.magnetstreet.swt.annotation;

import java.lang.annotation.*;

/**
 * DataView
 *
 * Meta Data tag for marking hibernate DAOs as a DataView
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Nov 30, 2009
 * @since Nov 30, 2009
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SWTEntity {
    public enum Type {EDITABLE_DATA_GRID,DATA_GRID}
    public enum Layout {COLUMN, CUSTOM}
    /**
     * The default representation of the entity, used primarily for
     * children beans when loaded in a dataview or datagrid. This
     * parameter is ignored on the parent bean when using a factory
     * method.
     */
    Type defaultCollectionType() default Type.DATA_GRID;

    /**
     * @return The layout style to use for the dynamically created dataview
     */
    Layout dataViewLayout() default Layout.COLUMN;
}