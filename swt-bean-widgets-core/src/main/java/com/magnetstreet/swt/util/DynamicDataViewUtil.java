/**
 * Copyright &copy; 2009 MagnetStreet <magnetstreet.com>
 */
package com.magnetstreet.swt.util;

import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Control;

/**
 * DataViewUtil
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Oct 21, 2009
 * @since Oct 21, 2009
 */
public class DynamicDataViewUtil {
    /**
     * Sets the layout information for a label based on some simple rules
     * @param label
     * @param row
     * @param col
     */
    public static void setupDefaultLabel(CLabel label, int row, int col) {
        FormData fd = new FormData(90, 20);
        fd.left = new FormAttachment(col, 2, 2);
        fd.top = new FormAttachment(0,1,row*25);
        label.setLayoutData(fd);
    }

    /**
     * sets the layout information for a control based on simple rules
     * @param c
     * @param row
     * @param col
     * @param colspan
     */
    public static void setupDefaultControl(Control c, int row, int col, int colspan) {
        FormData fd = new FormData(110, 15);
        fd.top = new FormAttachment(0,1,row*25);
        fd.left = new FormAttachment(col, 2, 95);
        fd.right = new FormAttachment(colspan * (col + 1),2,0);
        c.setLayoutData(fd);
    }
}
