package com.magnetstreet.swt.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * DateUtil
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Dec 17, 2009
 * @since Dec 17, 2009
 */
public class DateUtil {
    public static SimpleDateFormat defaultDateFormat = new SimpleDateFormat("MM-dd-yyy HH:mm:ss");

    public static String getDefaultDateTimeFormatted(Calendar cal) {
        if(cal == null) return null;
        return defaultDateFormat.format(cal.getTime());
    }
}
