package com.magnetstreet.swt.util;

import org.eclipse.swt.SWT;

/**
 * KeyEventUtil
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 4/14/11
 */
public class KeyEventUtil {

    public static String getStateMaskString(int stateMask) {
        String string = "";
        if ((stateMask & SWT.CTRL) != 0)
            string += " CTRL";
        if ((stateMask & SWT.ALT) != 0)
            string += " ALT";
        if ((stateMask & SWT.SHIFT) != 0)
            string += " SHIFT";
        if ((stateMask & SWT.COMMAND) != 0)
            string += " COMMAND";
        return string;
    }

    static String getCharacterString(char character) {
        switch (character) {
            case 0:
                return "'\\0'";
            case SWT.BS:
                return "'\\b'";
            case SWT.CR:
                return "'\\r'";
            case SWT.DEL:
                return "DEL";
            case SWT.ESC:
                return "ESC";
            case SWT.LF:
                return "'\\n'";
            case SWT.TAB:
                return "'\\t'";
        }
        return "'" + character + "'";
    }

    static String keyCodeString(int keyCode) {
        switch (keyCode) {
            /* Keyboard and Mouse Masks */
            case SWT.ALT:
                return "ALT";
            case SWT.SHIFT:
                return "SHIFT";
            case SWT.CONTROL:
                return "CONTROL";
            case SWT.COMMAND:
                return "COMMAND";

            /* Non-Numeric Keypad Keys */
            case SWT.ARROW_UP:
                return "ARROW_UP";
            case SWT.ARROW_DOWN:
                return "ARROW_DOWN";
            case SWT.ARROW_LEFT:
                return "ARROW_LEFT";
            case SWT.ARROW_RIGHT:
                return "ARROW_RIGHT";
            case SWT.PAGE_UP:
                return "PAGE_UP";
            case SWT.PAGE_DOWN:
                return "PAGE_DOWN";
            case SWT.HOME:
                return "HOME";
            case SWT.END:
                return "END";
            case SWT.INSERT:
                return "INSERT";

            /* Virtual and Ascii Keys */
            case SWT.BS:
                return "BS";
            case SWT.CR:
                return "CR";
            case SWT.DEL:
                return "DEL";
            case SWT.ESC:
                return "ESC";
            case SWT.LF:
                return "LF";
            case SWT.TAB:
                return "TAB";

            /* Functions Keys */
            case SWT.F1:
                return "F1";
            case SWT.F2:
                return "F2";
            case SWT.F3:
                return "F3";
            case SWT.F4:
                return "F4";
            case SWT.F5:
                return "F5";
            case SWT.F6:
                return "F6";
            case SWT.F7:
                return "F7";
            case SWT.F8:
                return "F8";
            case SWT.F9:
                return "F9";
            case SWT.F10:
                return "F10";
            case SWT.F11:
                return "F11";
            case SWT.F12:
                return "F12";
            case SWT.F13:
                return "F13";
            case SWT.F14:
                return "F14";
            case SWT.F15:
                return "F15";

            /* Numeric Keypad Keys */
            case SWT.KEYPAD_ADD:
                return "KEYPAD_ADD";
            case SWT.KEYPAD_SUBTRACT:
                return "KEYPAD_SUBTRACT";
            case SWT.KEYPAD_MULTIPLY:
                return "KEYPAD_MULTIPLY";
            case SWT.KEYPAD_DIVIDE:
                return "KEYPAD_DIVIDE";
            case SWT.KEYPAD_DECIMAL:
                return "KEYPAD_DECIMAL";
            case SWT.KEYPAD_CR:
                return "KEYPAD_CR";
            case SWT.KEYPAD_0:
                return "KEYPAD_0";
            case SWT.KEYPAD_1:
                return "KEYPAD_1";
            case SWT.KEYPAD_2:
                return "KEYPAD_2";
            case SWT.KEYPAD_3:
                return "KEYPAD_3";
            case SWT.KEYPAD_4:
                return "KEYPAD_4";
            case SWT.KEYPAD_5:
                return "KEYPAD_5";
            case SWT.KEYPAD_6:
                return "KEYPAD_6";
            case SWT.KEYPAD_7:
                return "KEYPAD_7";
            case SWT.KEYPAD_8:
                return "KEYPAD_8";
            case SWT.KEYPAD_9:
                return "KEYPAD_9";
            case SWT.KEYPAD_EQUAL:
                return "KEYPAD_EQUAL";

            /* Other keys */
            case SWT.CAPS_LOCK:
                return "CAPS_LOCK";
            case SWT.NUM_LOCK:
                return "NUM_LOCK";
            case SWT.SCROLL_LOCK:
                return "SCROLL_LOCK";
            case SWT.PAUSE:
                return "PAUSE";
            case SWT.BREAK:
                return "BREAK";
            case SWT.PRINT_SCREEN:
                return "PRINT_SCREEN";
            case SWT.HELP:
                return "HELP";
        }
        return getCharacterString((char) keyCode);
    }

    public static boolean isNormalAsciiKey(int keyCode) {
        switch (keyCode) {
            /* Keyboard and Mouse Masks */
            case SWT.ALT:
                return false;
            case SWT.SHIFT:
                return false;
            case SWT.CONTROL:
                return false;
            case SWT.COMMAND:
                return false;

            /* Non-Numeric Keypad Keys */
            case SWT.ARROW_UP:
                return false;
            case SWT.ARROW_DOWN:
                return false;
            case SWT.ARROW_LEFT:
                return false;
            case SWT.ARROW_RIGHT:
                return false;
            case SWT.PAGE_UP:
                return false;
            case SWT.PAGE_DOWN:
                return false;
            case SWT.HOME:
                return false;
            case SWT.END:
                return false;
            case SWT.INSERT:
                return false;

            /* Virtual and Ascii Keys */
            case SWT.BS:
                return false;
            case SWT.CR:
                return false;
            case SWT.DEL:
                return false;
            case SWT.ESC:
                return false;
            case SWT.LF:
                return false;
            case SWT.TAB:
                return false;

            /* Functions Keys */
            case SWT.F1:
                return false;
            case SWT.F2:
                return false;
            case SWT.F3:
                return false;
            case SWT.F4:
                return false;
            case SWT.F5:
                return false;
            case SWT.F6:
                return false;
            case SWT.F7:
                return false;
            case SWT.F8:
                return false;
            case SWT.F9:
                return false;
            case SWT.F10:
                return false;
            case SWT.F11:
                return false;
            case SWT.F12:
                return false;
            case SWT.F13:
                return false;
            case SWT.F14:
                return false;
            case SWT.F15:
                return false;

            /* Numeric Keypad Keys */
            case SWT.KEYPAD_ADD:
                return false;
            case SWT.KEYPAD_SUBTRACT:
                return false;
            case SWT.KEYPAD_MULTIPLY:
                return false;
            case SWT.KEYPAD_DIVIDE:
                return false;
            case SWT.KEYPAD_DECIMAL:
                return false;
            case SWT.KEYPAD_CR:
                return false;
            case SWT.KEYPAD_0:
                return false;
            case SWT.KEYPAD_1:
                return false;
            case SWT.KEYPAD_2:
                return false;
            case SWT.KEYPAD_3:
                return false;
            case SWT.KEYPAD_4:
                return false;
            case SWT.KEYPAD_5:
                return false;
            case SWT.KEYPAD_6:
                return false;
            case SWT.KEYPAD_7:
                return false;
            case SWT.KEYPAD_8:
                return false;
            case SWT.KEYPAD_9:
                return false;
            case SWT.KEYPAD_EQUAL:
                return false;

            /* Other keys */
            case SWT.CAPS_LOCK:
                return false;
            case SWT.NUM_LOCK:
                return false;
            case SWT.SCROLL_LOCK:
                return false;
            case SWT.PAUSE:
                return false;
            case SWT.BREAK:
                return false;
            case SWT.PRINT_SCREEN:
                return false;
            case SWT.HELP:
                return false;
            default:
                return true;
        }
    }

    public static int convertKeypadNumberIfNumLock(int stateMask, int keycode) {
        if ((stateMask & SWT.NUM_LOCK) != 0) {
            switch(keycode) {
                case SWT.KEYPAD_0:
                    return '0';
                case SWT.KEYPAD_1:
                    return '1';
                case SWT.KEYPAD_2:
                    return '2';
                case SWT.KEYPAD_3:
                    return '3';
                case SWT.KEYPAD_4:
                    return '4';
                case SWT.KEYPAD_5:
                    return '5';
                case SWT.KEYPAD_6:
                    return '6';
                case SWT.KEYPAD_7:
                    return '7';
                case SWT.KEYPAD_8:
                    return '8';
                case SWT.KEYPAD_9:
                    return '9';
            }
        }
        return -1;
    }
}
