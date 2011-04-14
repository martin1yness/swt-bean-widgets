package com.magnetstreet.swt.beanwidget.combo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * QuickSearchComboSWTBotTest
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 4/14/11
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class QuickSearchComboSWTBotTest {

    private static Thread swtThread;

    private static Shell shell;
    private static SWTBot bot;
    private static QuickSearchCombo quickSearchCombo;

    static {
        swtThread = new Thread() {
            @Override public void run() {
                shell = new Shell(new Display());
                shell.setLayout(new FillLayout());
                quickSearchCombo = new QuickSearchCombo(shell, SWT.BORDER);
                quickSearchCombo.add("AabBcc");
                quickSearchCombo.add("aabBcc");
                quickSearchCombo.add("aaBBcc");
                quickSearchCombo.add("aabbcc");
                quickSearchCombo.add("AAbbcc");

                shell.pack();
                shell.open();
                while(!shell.isDisposed()) {
                    if(shell.getDisplay().readAndDispatch()) shell.getDisplay().sleep();
                }
            }
        };
        swtThread.start();
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        bot = new SWTBot();
        // slow down tests
        SWTBotPreferences.PLAYBACK_DELAY = 10;
        SWTBotPreferences.TYPE_INTERVAL = 1000;
    }

    @AfterClass
    public static void afterClass() throws Exception {
        swtThread.interrupt();
    }

    @Test
    public void testQuickSearchComboKeyListeners() throws InterruptedException {
        SWTBotCombo combo = bot.comboBox();
        combo.pressShortcut(SWT.NONE, 'a');
        assertThat(combo.getText(), is("AabBcc"));
        combo.pressShortcut(SWT.NONE, 'a');
        assertThat(combo.getText(), is("AabBcc"));
        combo.pressShortcut(org.eclipse.swtbot.swt.finder.keyboard.Keystrokes.DOWN);
        assertThat(combo.getText(), is("aabBcc"));
        combo.pressShortcut(org.eclipse.swtbot.swt.finder.keyboard.Keystrokes.DOWN);
        assertThat(combo.getText(), is("aaBBcc"));
        combo.pressShortcut(org.eclipse.swtbot.swt.finder.keyboard.Keystrokes.UP);
        assertThat(combo.getText(), is("aabBcc"));
    }
}