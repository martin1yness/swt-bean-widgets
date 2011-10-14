package com.magnetstreet.swt.util;

/**
 * SWTUtil
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 10/14/11
 */
public class SWTUtil {
    public static boolean hasStyle(int appliedStyles, int style) {
        return (appliedStyles | style) == appliedStyles;
    }
}
