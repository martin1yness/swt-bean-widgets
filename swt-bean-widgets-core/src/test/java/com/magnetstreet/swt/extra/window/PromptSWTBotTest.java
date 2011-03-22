package com.magnetstreet.swt.extra.window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotLabel;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;

/**
 * PromptSWTBotTest
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since Sep 21, 2010
 */
public class PromptSWTBotTest {
    private static Window shell;
    private static SWTBot bot;

    static {
        new Thread() {
            @Override public void run() {
                shell = new Window() {
                    private Label labelA;
                    private Text textA;
                    private Button buttonA;

                    @Override public boolean beforeClose() { return true; }
                    @Override protected void preInitGUI() {
                        GridLayout layout = new GridLayout();
                        layout.numColumns = 2;
                        setLayout(layout);
                    }
                    @Override protected void initGUI() {
                        labelA = new Label(this, SWT.BORDER);
                        labelA.setText("Label A:");
                        textA = new Text(this, SWT.BORDER);
                        textA.setText("fill in...");
                        buttonA = new Button(this, SWT.PUSH);
                        buttonA.setText("BuTToN");
                    }
                    @Override protected void postInitGUI() {
                        pack();
                    }
                    @Override protected void defineHotkeys() { }
                    @Override protected void applyWidgetActionListeners() {
                        buttonA.addSelectionListener(new SelectionAdapter() {
                            @Override public void widgetSelected(SelectionEvent selectionEvent) {
                                prompt("Test Prompt", "Enter a test prompt answer:");
                            }
                        });
                    }

                    @Override protected String getTitle() { return "Test Window"; }
                };
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

    @Test public void testPrompt() throws Exception {
        SWTBotButton button = bot.button("BuTToN");
        button.click();

        SWTBotLabel label = bot.label("Enter a test prompt answer:");
        assertNotNull(label);

        //Thread.sleep(10000);
    }

}
