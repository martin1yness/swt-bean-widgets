/**
 * Copyright &copy; 2009 MagnetStreet <magnetstreet.com>
 */
package com.magnetstreet.swt.util;

import org.eclipse.swt.widgets.Spinner;

import java.math.BigDecimal;

/**
 * SpinnerUtil
 *
 * Helper functions depended upon when using the Spinner widget, especially
 * within the DataViews!
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Nov 12, 2009
 * @since Nov 12, 2009
 */
public class SpinnerUtil {
    public static void setupMoneySpinner(Spinner widget, BigDecimal initalValue) {
        if(widget==null || initalValue==null) return;
        int selection = initalValue.multiply(new BigDecimal(100)).setScale(0).toBigInteger().intValue();
        widget.setValues(selection, 0, Integer.MAX_VALUE, 2, 1, 100);
    }
    public static BigDecimal getMoneySpinnerValue(Spinner widget) {
        BigDecimal r = new BigDecimal(widget.getSelection());
        return r.divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
    }

    public static void setupPercentSpinner(Spinner widget, Integer initalValue) {
        if(widget==null || initalValue==null) return;
        widget.setValues(initalValue, 0, 100, 0, 1, 10);
    }
    public static Integer getPercentSpinnerValue(Spinner widget) {
        return widget.getSelection();
    }
}
