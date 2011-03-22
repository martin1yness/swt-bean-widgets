package com.magnetstreet.swt.extra.window;

import com.magnetstreet.swt.extra.VisualInputText;
import com.magnetstreet.swt.util.ReturnKeyCommand;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

/**
 * Prompt
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since Sep 21, 2010
 */
public class Prompt extends Window {
    private String title, message;
    private Label messageLabel;
    private VisualInputText userInputText;
    private Button okButton, cancelButton;
    private PromptListener promptListener;

    public static class PromptListener {
        private String enteredValue;
        private boolean canceled;
        public void promptSuccess(String enteredValue) {
            canceled = false;
            this.enteredValue = enteredValue;
        }
        public void promptCancel() {
            canceled = true;
        }
        public String getEnteredValue() { return enteredValue; }
    }

    public Prompt(Window parent, PromptListener promptListener, String title, String msg) {
        super(parent, SWT.DIALOG_TRIM|SWT.APPLICATION_MODAL);
        this.promptListener = promptListener;
        this.title = title;
        this.message = msg;
    }

    @Override public boolean beforeClose() {
        return true;
    }

    @Override protected void preInitGUI() {
        this.setLayout(new FormLayout());
    }

    @Override protected void initGUI() {
        {
            messageLabel = new Label(this, SWT.NONE);
            FormData fd = new FormData(Math.min( (message.length()+10)*5, Display.getDefault().getPrimaryMonitor().getBounds().width-10), 25);
            fd.height = 25;
            fd.left = new FormAttachment(0, 100, 2);
            fd.right = new FormAttachment(100,100,-2);
            fd.top = new FormAttachment(0,100,10);
            messageLabel.setLayoutData(fd);
        }
        {
            userInputText = new VisualInputText(this, SWT.BORDER);
            FormData fd = new FormData(Math.min( (message.length()+10)*5, Display.getDefault().getPrimaryMonitor().getBounds().x-10), 25);
            fd.left = new FormAttachment(0, 100, 2);
            fd.right = new FormAttachment(100,100,-2);
            fd.top = new FormAttachment(0,100,45);
            userInputText.setLayoutData(fd);
        }
        {
            okButton = new Button(this, SWT.PUSH);
            FormData fd = new FormData(120, 35);
            fd.top = new FormAttachment(userInputText, 5);
            fd.right = new FormAttachment(100,100,-2);
            okButton.setLayoutData(fd);
            okButton.setText("Ok");
        }
        {
            cancelButton = new Button(this, SWT.PUSH);
            FormData fd = new FormData(120, 35);
            fd.top = new FormAttachment(userInputText, 5);
            fd.right = new FormAttachment(100,100,-125);
            cancelButton.setLayoutData(fd);
            cancelButton.setText("Cancel");
        }
    }

    @Override protected void postInitGUI() {
        messageLabel.setText(message);
        userInputText.setDefaultText("Quick access key 'SHIFT + ?'");
        userInputText.setAutoClearDefaultText(true);
        userInputText.setAutoRefillDefaultText(true);
        userInputText.forceFocus();
        pack();
    }

    @Override protected void defineHotkeys() {
        userInputText.enableHotKeyAccess(Display.getDefault(), '/', SWT.SHIFT);
    }

    @Override protected void applyWidgetActionListeners() {
        userInputText.setReturnKeyCommand(new ReturnKeyCommand<Object>() {
            @Override public Object command() {
                okActionSelection();
                return null;
            }
        });
        okButton.addSelectionListener(new SelectionAdapter(){
            @Override public void widgetSelected(SelectionEvent selectionEvent) {
                okActionSelection();
            }
        });
        cancelButton.addSelectionListener(new SelectionAdapter() {
            @Override public void widgetSelected(SelectionEvent selectionEvent) {
                cancelActionSelection();
            }
        });
    }

    @Override protected String getTitle() {
        return "Prompt: " + title;
    }

    private void okActionSelection() {
        promptListener.promptSuccess(userInputText.getText());
        close();
    }

    private void cancelActionSelection() {
        promptListener.promptCancel();
        close();
    }
}
