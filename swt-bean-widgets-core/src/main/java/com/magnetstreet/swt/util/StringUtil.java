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
     * also 'someVar.someChildVar' into 'Some Var Some Child Var'
     * @param text
     * @return
     */
    public static String camelCaseToTitle(String text) {
        StringBuilder sb = new StringBuilder(text);
        sb.deleteCharAt(0);
        sb.insert(0, Character.toUpperCase(text.charAt(0)));
        for(int i=1; i<sb.length(); i++) {
            if( Character.isUpperCase(sb.charAt(i)) ) {
                sb.insert(i++, " ");
            } else if(sb.charAt(i) == '.') {
                sb.deleteCharAt(i).insert(i, " ");
                sb.insert(i+1, Character.toUpperCase(sb.charAt(i+1))).deleteCharAt(i+2);
                i++;
            }
        }

        return sb.toString();
    }
}
