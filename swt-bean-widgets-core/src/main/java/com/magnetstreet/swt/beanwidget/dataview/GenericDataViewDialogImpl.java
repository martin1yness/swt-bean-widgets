package com.magnetstreet.swt.beanwidget.dataview;

import com.magnetstreet.swt.extra.window.Window;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * GenericDataViewDialogImpl
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Jan 12, 2010
 * @since Jan 12, 2010
 */
public class GenericDataViewDialogImpl extends Window implements GenericDataViewDialog {
    protected Button okButton;
    protected Button cancelButton;
    protected DataView view;

    public enum EXIT_STATE{OK, CANCEL};
    public EXIT_STATE exitState;

    public GenericDataViewDialogImpl(Shell parent, int style) {
        super(parent, style);
    }

    @Override public boolean beforeClose() { return true; }

    protected void preInitGUI() {
        setLayout(new FormLayout());
    }
    protected void initGUI() {
        okButton = new Button(this, SWT.PUSH);
        okButton.setText("OK");
        FormData okButtonLData = new FormData(100, 25);
        okButtonLData.right = new FormAttachment(100,100,-110);
        okButtonLData.bottom = new FormAttachment(100,100,-5);
        okButton.setLayoutData(okButtonLData);

        cancelButton = new Button(this, SWT.PUSH);
        cancelButton.setText("Cancel");
        FormData cancelButtonLData = new FormData(100, 25);
        cancelButtonLData.right = new FormAttachment(100,100,-5);
        cancelButtonLData.bottom = new FormAttachment(100,100,-5);
        cancelButton.setLayoutData(cancelButtonLData);
    }
    protected void postInitGUI() { }
    protected void defineHotkeys() {
    }
    protected void applyWidgetActionListeners() {
        okButton.addSelectionListener(new SelectionAdapter() {
            @Override public void widgetSelected(SelectionEvent selectionEvent) {
                if(view!=null) ((AbstractDataView)view).updateViewDataObjectFromWidgets();
                exitState = EXIT_STATE.OK;
                beforeClose();
            }
        });
        cancelButton.addSelectionListener(new SelectionAdapter(){
            @Override public void widgetSelected(SelectionEvent selectionEvent) {
                exitState = EXIT_STATE.CANCEL;
                beforeClose();
            }
        });
    }
    protected String getTitle() {
        return null;
    }
    public void setDataView(DataView view) {
        this.view = view;
        FormData viewLData = new FormData();
        viewLData.top = new FormAttachment(0,100,5);
        viewLData.right = new FormAttachment(100,100,-5);
        viewLData.left = new FormAttachment(0,100,5);
        viewLData.bottom = new FormAttachment(100,100,-35);
        ((Composite)view).setLayoutData(viewLData);
        pack();
    }
    public DataView getDataView() {
        return view;
    }
}