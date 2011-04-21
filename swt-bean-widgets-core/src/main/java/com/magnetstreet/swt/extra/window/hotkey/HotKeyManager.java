package com.magnetstreet.swt.extra.window.hotkey;

import com.magnetstreet.swt.extra.window.hotkey.exception.DuplicateHotKeyDefinitionExcpetion;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hot Key Manager
 *
 * Provides an abstraction to the SWT display filters that adds some additional
 * constraints and usability.
 *
 * Features
 * - Forces one listener to one hot key combitions per manager
 * - Attaches String descriptions to hot keys for automatic hot key
 *   help dialog creation.
 * - Possibility of automatic creation based on configuration files.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Aug 6, 2009
 * @since Aug 6, 2009
 */
public class HotKeyManager {
    private static Logger logger = Logger.getLogger(HotKeyManager.class.getSimpleName());
    private Display display;
    private Map<String, HotKey> hotKeyMap = new HashMap<String, HotKey>();

    /**
     * Constructor attaches hot key manager to a display
     * @param display Display hot manager is managing
     */
    public HotKeyManager(Display display) {
        this.display = display;
    }

    /**
     * Provides some abstraction to the global hotkey definitions, allowing hotkeys to be
     * defined without knowledge of SWT's global filter logic attached to particular displays.
     *
     * This abstraction also enforces no duplicate hotkeys per composite (NOTE: This is only
     * affective if each composite uses its own display!).
     *
     * Also allows easily listing all hotkeys defined by the implementing class.
     * @param key
     * @param modifier
     * @param action
     */
    public void registerHotKey(int key, int modifier, Runnable action, String description) {
        logger.log(Level.FINEST, "Registering Hotkey: " + key + " mod:" + modifier);
        HotKey hkey = new HotKey(key, modifier, action, description);
        if( hotKeyMap.containsKey(key+"|"+modifier) )
            throw new DuplicateHotKeyDefinitionExcpetion("Attempted to add duplicate global hot key listener. " + key + " - mod: " + modifier);

        hotKeyMap.put(key+"|"+modifier, new HotKey(key, modifier, action, description));
        display.addFilter(SWT.KeyUp, hkey.listener);
    }
    
    /**
     * Removes a hotkey from the display implementing composite used.
     * @param key
     * @param modifier
     */
    public void removeHotKey(int key, int modifier) {
        HotKey hkey = hotKeyMap.get(key+"|"+modifier);
        if(hkey == null) return;
        display.removeFilter(SWT.KeyUp, hkey.listener);
        hotKeyMap.remove(key+"|"+modifier);
    }

    /**
     * Shows a dialog explaining all of the defined hotkeys for the current implementation of
     * this composite.
     */
    public void showHotkeyHelpDialog() {
        HotkeyDialog dialog = new HotkeyDialog(display.getActiveShell(), SWT.DIALOG_TRIM);
        for(HotKey hkey: hotKeyMap.values()) {
            String key = "";
            switch(hkey.key) {
                case SWT.F1: key = "F1"; break;
                case SWT.F2: key = "F2"; break;
                case SWT.F3: key = "F3"; break;
                case SWT.F4: key = "F4"; break;
                case SWT.F5: key = "F5"; break;
                case SWT.F6: key = "F6"; break;
                case SWT.F7: key = "F7"; break;
                case SWT.F8: key = "F8"; break;
                case SWT.F9: key = "F9"; break;
                case SWT.F10: key = "F10"; break;
                case SWT.F11: key = "F11"; break;
                case SWT.F12: key = "F12"; break;
                case SWT.ESC: key = "ESC"; break;
                default: key = new Character((char)hkey.key).toString();
            }
            String modifier = "";
            switch(hkey.modifier) {
                case SWT.ALT: modifier = "ALT+"; break;
                case SWT.CTRL: modifier = "CTRL+"; break;
                case SWT.SHIFT: modifier = "SHIFT+"; break;
                case SWT.ALT|SWT.CTRL: modifier = "CTRL+ALT+"; break;
                case SWT.ALT|SWT.SHIFT: modifier = "SHIFT+ALT+"; break;
                case SWT.CTRL|SWT.SHIFT: modifier = "SHFIT+CTRL+"; break;
                case SWT.CTRL|SWT.SHIFT|SWT.ALT: modifier = "SHIFT+CTRL+ALT+"; break;
            }
            dialog.addHotkeyDescription(modifier+""+key, hkey.description);
        }
        dialog.open();
    }
}
