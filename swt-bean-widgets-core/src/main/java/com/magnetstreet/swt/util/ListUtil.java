/**
 * Copyright &copy; 2009 MagnetStreet <magnetstreet.com>
 */
package com.magnetstreet.swt.util;

import com.magnetstreet.swt.beanwidget.list.AbstractSpecificList;
import org.eclipse.swt.custom.CLabel;

import java.util.Set;

/**
 * ListUtil
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Nov 17, 2009
 * @since Nov 17, 2009
 */
public class ListUtil {
    /**
     * Copies the toSTring of each selected object in the AbstractSpecificList in a
     * new line delimited fashion on the CLabel given, removing whatever was in the
     * CLabel to begin with.
     * @param list
     * @param label
     */
    public static void copyListSelectionToLabel(AbstractSpecificList list, CLabel label) {
        String labelStr = "";
        Set<?> selectedObjs = list.getSelectedObjects();
        for (Object selectedObj : selectedObjs)
            labelStr += selectedObj.toString() + "\r\n";
        label.setText(labelStr);
    }
}
