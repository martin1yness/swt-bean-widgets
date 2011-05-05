package com.magnetstreet.swt.util;

/**
 * StringUtil
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 5/5/11
 */
public class StringUtil {

    /**
     * Turns strings like 'someVarName' and 'SomeVarName' into 'Some Var Name'
     * @param text
     * @return
     */
    public static String camelCaseToTitle(String text) {
        String result = "" + Character.toUpperCase(text.charAt(0));
        for(int i=1; i<text.length(); i++) {
            if( Character.isUpperCase(text.charAt(i)) )
                result += " ";
            result += text.charAt(i);
        }

        return result;
    }
}
