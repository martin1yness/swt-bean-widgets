package com.magnetstreet.swt.beanwidget.dataview.layout;

import com.magnetstreet.swt.beanwidget.dataview.DynamicDataView;
import com.magnetstreet.swt.util.DynamicDataViewUtil;
import org.eclipse.swt.widgets.Control;

import java.util.SortedSet;

/**
 * Column Layout
 *
 * Custom layout designed for data views to layout out the
 * properties of an annotated object in vertical columns.
 *
 * Current design is to pass these dataview layouts into the
 * data view factory when requesting a DataView
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Nov 24, 2009
 * @since Nov 24, 2009
 */
public class ColumnLayout extends BaseLayout {

    public ColumnLayout() {
        super();
    }

    /**
     * NONE     - Display collections on a new line across all columns
     * INLINE   - Display collection boxed into the column it would normally be placed
     * LEFT     - Collection will appear to the left of all other components maximizing its size
     * RIGHT    - Collection will appear to the right of all other components maximizing its size
     * TOP      - Collection will appear above all other components maximizing its size
     * BOTTOM   - Collection will appear below all other components maximizing its size
     */
    public enum CollectionOrientation{NONE, INLINE, LEFT, RIGHT, TOP, BOTTOM}

    /*
     * Configuration parameters
     */
    protected int columns = 2;
    protected CollectionOrientation collectionOrientation = CollectionOrientation.NONE;

    /**
     * {@inheritDoc}
     */
    @Override public void layout(SortedSet<DynamicDataView.DynamicWidget> widgets) {
        int counter = columns;
        for(DynamicDataView.DynamicWidget dw: widgets) {
            int col = counter % columns;
            int row = (counter) / columns;
            DynamicDataViewUtil.setupDefaultLabel(dw.label, row, col);
            DynamicDataViewUtil.setupDefaultControl((Control)dw.widget, row, col, 1);
            counter++;
        }
    }

    /**
     * Set the number of columns to be used in the layout.
     * @param num
     */
    public void setColumns(int num) {
        columns = num;
    }

}
