package com.magnetstreet.swt.example.datagrid2.reflective;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.StringContains.containsString;

/**
 * OrderReflectiveWithFiltersWindowTest
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 5/5/11
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class DivisionReflectiveDataGridSimpleInclusiveExclusiveFilterWindowTest {
    private static Shell shell;
    private static DivisionReflectiveDataGridSimpleInclusiveExclusiveFilterWindow inst;
    private static SWTBot bot;

    static {
        Thread t = new Thread(new Runnable() {
            public void run() {
                shell = new Shell(Display.getDefault());
                inst = new DivisionReflectiveDataGridSimpleInclusiveExclusiveFilterWindow(shell);
                inst.setBlockOnOpen(true);
                inst.open();
            }
        });
        t.start();
    }


    @BeforeClass
    public static void setupSwtBot() {
        bot = new SWTBot();
    }

    @Before
    public void setDefaultConfiguration() {
        bot.textWithLabel("Description:").setText("");
    }

    @Test public void testFilters() throws InterruptedException {
        SWTBotTable tbl = bot.table();
        tbl.header("Id").click();   // sorty by id ASC
        tbl.header("Id").click();   // sorty by id DESC
        assertThat(tbl.rowCount(), is(3));

        bot.textWithLabel("Description:").setText("second");
        assertThat(tbl.rowCount(), is(1));

        bot.textWithLabel("Description:").setText("in -second");
        assertThat(tbl.rowCount(), is(2));
        assertThat(tbl.cell(0,2), is(containsString("third")));
        assertThat(tbl.cell(1,2), is(containsString("first")));
    }

}
