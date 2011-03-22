package com.magnetstreet.swt.extra.window.hotkey;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * HotkeyDialog
 *
 * FUTURE: Make hot key list editable so users can view and change hotkeys.
 *         Could use the DataGrid if a HotKey table was created in database!
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Aug 6, 2009
 * @since Aug 6, 2009
 */
public class HotkeyDialog extends Dialog {
    private Map<String, String> hotkeyDescriptionMap = new LinkedHashMap<String, String>();

    public HotkeyDialog(Shell shell, int i) {
        super(shell, i);
    }

    /**
     * Adds a hot key description line to the dialog
     * @param keyComboDesc The key combo description (i.e. CTRL + C, ALT + F, etc...)
     * @param actionDesc
     */
    public void addHotkeyDescription(String keyComboDesc, String actionDesc) {
        hotkeyDescriptionMap.put(keyComboDesc, actionDesc);
    }

    public void open() {
        final Shell dialogShell = new Shell(getParent(), SWT.DIALOG_TRIM);
        dialogShell.setText("Hot Key Manager");
        GridLayout gl = new GridLayout();
        gl.numColumns = 2;
        gl.verticalSpacing = 0;
        dialogShell.setLayout(gl);
        for(String key: hotkeyDescriptionMap.keySet()) {
            Label label = new Label(dialogShell, SWT.NONE);
            label.setText(key);
            label.setLayoutData(new GridData(120, 25));
            Label label2 = new Label(dialogShell, SWT.NONE);
            label2.setText(hotkeyDescriptionMap.get(key));
            label2.setLayoutData(new GridData(400, 25));
        }

        GridData gd = new GridData(120, 25);
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = GridData.CENTER;
        Button b = new Button(dialogShell, SWT.PUSH|SWT.CENTER);
        b.setLayoutData(gd);
        b.setSize(120, 25);
        b.setText("Ok");
        b.addSelectionListener(new SelectionAdapter() {
            @Override public void widgetSelected(SelectionEvent evnt) {
                dialogShell.close();
            }
        });

        dialogShell.pack();
        dialogShell.setLocation(getParent().toDisplay(100, 100));
	    dialogShell.open();
    }

    public static void main(String[] args) {
        Display display = Display.getDefault();
        Shell shell = new Shell(display);
        HotkeyDialog inst = new HotkeyDialog(shell, SWT.SHELL_TRIM|SWT.OK);
        inst.addHotkeyDescription("ALT + D", "Doesn't do anything, but i wish it did!");
        inst.addHotkeyDescription("CTRL + ALT + DEL + D", "Doesn't do anything either!I wish it did!");
		inst.open();
        while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
    }
}
