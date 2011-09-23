package com.magnetstreet.swt.extra.window;

import com.magnetstreet.swt.extra.VisualInputText;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Prompt
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since Sep 21, 2010
 */
public class Prompt extends Dialog {
    private String message, title;
    private Label messageLabel;
    private VisualInputText userInputText;

    private String input;

    public Prompt(Shell parent, String title, String msg) {
        super(parent);
        this.message = msg;
        this.title = title;
    }

    @Override public boolean close() {
        input = userInputText.getText();
        return super.close();
    }

    @Override protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override protected Control createDialogArea(Composite parent) {
        Composite container = new Composite(parent, SWT.EMBEDDED);
        container.setLayout(new FormLayout());
        {
            messageLabel = new Label(container, SWT.NONE);
            FormData fd = new FormData(Math.min( (message.length()+10)*5, Display.getDefault().getPrimaryMonitor().getBounds().width-10), 25);
            fd.height = 25;
            fd.left = new FormAttachment(0, 100, 2);
            fd.right = new FormAttachment(100,100,-2);
            fd.top = new FormAttachment(0,100,10);
            messageLabel.setLayoutData(fd);
        }
        {
            userInputText = new VisualInputText(container, SWT.BORDER);
            FormData fd = new FormData(Math.min( (message.length()+10)*5, Display.getDefault().getPrimaryMonitor().getBounds().x-10), 25);
            fd.left = new FormAttachment(0, 100, 2);
            fd.right = new FormAttachment(100,100,-2);
            fd.top = new FormAttachment(0,100,45);
            userInputText.setLayoutData(fd);
        }
        messageLabel.setText(message);
        userInputText.setDefaultText("Quick access key 'SHIFT + ?'");
        userInputText.setAutoClearDefaultText(true);
        userInputText.setAutoRefillDefaultText(true);
        userInputText.forceFocus();

        getShell().setText(title);
        return container;
    }

    public String getUserInput() {
        return input;
    }
}
