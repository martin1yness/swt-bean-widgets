package com.magnetstreet.swt.extra.window.hotkey;

import com.magnetstreet.swt.extra.window.hotkey.exception.DuplicateHotKeyDefinitionExcpetion;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * HotKeyManagerTest
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 4/11/11
 */
public class HotKeyManagerTest {
    HotKeyManager hotKeyManager;
    Display display;
    volatile int hotkeyRunCount = 0;

    @Before
    public void setupHotkeyManager() {
        display = new Display();
        hotKeyManager = new HotKeyManager(display);
    }

    @After
    public void destroyHotkeyManager() {
        display.dispose();
    }

    @Test public void testAddRemoveGlobalHotKey() {
        hotKeyManager.registerHotKey('n', SWT.SHIFT|SWT.CTRL, new Runnable() {
            public void run() {
                hotkeyRunCount++;
            }
        }, "Does something interesting");

        hotKeyManager.removeHotKey('n', SWT.SHIFT|SWT.CTRL);

        hotKeyManager.registerHotKey('n', SWT.SHIFT|SWT.CTRL, new Runnable() {
            public void run() {
                hotkeyRunCount++;
            }
        }, "Does something interesting");
    }

    @Test(expected = DuplicateHotKeyDefinitionExcpetion.class)
    public void testAddingSameHotkeyThrowsException() {
        hotKeyManager.registerHotKey('n', SWT.SHIFT|SWT.CTRL, new Runnable() {
            public void run() {
                hotkeyRunCount++;
            }
        }, "Does something interesting");
        hotKeyManager.registerHotKey('n', SWT.SHIFT|SWT.CTRL, new Runnable() {
            public void run() {
                hotkeyRunCount++;
            }
        }, "Does something interesting");
    }
}
