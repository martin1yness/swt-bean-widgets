package com.magnetstreet.swt.example;

import com.magnetstreet.swt.extra.window.Window;
import com.magnetstreet.swt.beanwidget.datagrid.EditableDataGrid;
import com.magnetstreet.swt.beanwidget.datagrid.DataGridFactory;
import com.magnetstreet.swt.example.persistence.CustomerRecordData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

/**
 * CustomerListWindow
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Jan 6, 2010
 * @since Jan 6, 2010
 */
public class CustomerListWindow extends Window {
    private CustomerRecordData data;

    protected EditableDataGrid dataGrid;
    protected Button closeButton;

    public static void main(String[] args0) {
        Window.testGUI(CustomerListWindow.class);
        
    }

    @Override public boolean beforeClose() { return true; }

    /**
     * Called prior to the initGUI() method, used to segrigate commands that are
     * not directly related to GUI initalization, but are required to occure before
     * the GUI is built.
     */
    protected void preInitGUI() {
        data = new CustomerRecordData();
        setLayout(new FormLayout());
    }
    /**
     * Intended to house all GUI Controls/Widget creation within this container control. Layout
     * and placement operations are also included.
     */
    protected void initGUI() {
        dataGrid = DataGridFactory.getInstance().getEditableDataGrid(data.getAllCustomers(), null, this, SWT.FULL_SELECTION);
        FormData dataGridLData = new FormData(500, 300);
        dataGridLData.left = new FormAttachment(0,100,0);
        dataGridLData.right = new FormAttachment(100,100,5);
        dataGridLData.top = new FormAttachment(0,100,0);
        dataGridLData.bottom = new FormAttachment(100,100,-35);
        dataGrid.setLayoutData(dataGridLData);

        closeButton = new Button(this, SWT.PUSH);
        closeButton.setText("Close");
        FormData closeButtonLData = new FormData(100, 25);
        closeButtonLData.right = new FormAttachment(100,100,-5);
        closeButtonLData.bottom = new FormAttachment(100,100,-5);
        closeButton.setLayoutData(closeButtonLData);
    }
    /**
     * Any post GUI creation operations
     */
    protected void postInitGUI() {
        pack();
    }
    /**
     * {@inheritDoc}
     */
    protected void defineHotkeys() {
        hotKeyManager.registerHotKey(SWT.F4, SWT.CTRL, new Runnable() {
            public void run() {
                beforeClose();
            }
        }, "Close Window");
    }
    /**
     * {@inheritDoc}
     */
    protected void applyWidgetActionListeners() {
        closeButton.addSelectionListener(new SelectionAdapter() {
            @Override public void widgetSelected(SelectionEvent selectionEvent) {
                beforeClose();
            }
        });
    }
    /**
     * {@inheritDoc}
     */
    protected String getTitle() {
        return "Customer Record(s)";
    }
}
