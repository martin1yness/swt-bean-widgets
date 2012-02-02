package com.magnetstreet.swt.example;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import com.magnetstreet.swt.example.CustomerListWindow;
import static junit.framework.Assert.assertTrue;

/**
 * CustomerListWindowSWTBotTest
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Jan 12, 2010
 * @since Jan 12, 2010
 */
public class CustomerListWindowSWTBotTest {
    private static CustomerListWindow shell;
    private static SWTBot bot;

    static {
        new Thread() {
            @Override public void run() {
                shell = new CustomerListWindow();
                shell.pack();
                shell.openBlocking();
            }
        }.start();
    }

    @BeforeClass public static void beforeClass() throws Exception {
        bot = new SWTBot();
        // slow down tests
        SWTBotPreferences.PLAYBACK_DELAY = 10;
        SWTBotPreferences.TYPE_INTERVAL = 1000;
    }

    @AfterClass public static void shutdownClass() throws Exception {
        shell.dispose();
    }

    @Test public  void testDblClickOpenDialog() throws InterruptedException {
        bot.table().doubleClick(1,1);
        assertTrue(bot.checkBox().isChecked());
    }
}
