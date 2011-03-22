package com.magnetstreet.swt.beanwidget.dataview.layout;

import com.magnetstreet.swt.beanwidget.dataview.DynamicDataView;

import java.util.SortedSet;

/**
 * BaseLayout
 *
 * Does the shared work that all layout implementations would have to accomplish to begin
 * thier customization. Intended to be extended by all layout managers.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Nov 30, 2009
 * @since Nov 30, 2009
 */
public abstract class BaseLayout {

    public BaseLayout() {}

    /**
     * Called by the DynmaicDataView widget to define the
     * layout of all dynamic widgets
     */
    public abstract void layout(SortedSet<DynamicDataView.DynamicWidget> widgets);
}
