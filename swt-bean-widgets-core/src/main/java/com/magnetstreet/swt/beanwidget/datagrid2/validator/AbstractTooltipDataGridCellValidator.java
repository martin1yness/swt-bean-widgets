package com.magnetstreet.swt.beanwidget.datagrid2.validator;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolTip;

/**
 * AbstractTooltipDataGridCellValidator
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 4/29/11
 */
public abstract class AbstractTooltipDataGridCellValidator implements IDataGridCellValidator {
    protected ToolTip validationErrorTooltip;
    protected boolean errorShowing = false;
    protected CellEditor editor;

    @Override public void showError(CellEditor editor) {
        if(errorShowing) return;
        this.editor = editor;
        validationErrorTooltip = new ToolTip(editor.getControl().getShell(), SWT.BALLOON | SWT.ICON_ERROR);
        validationErrorTooltip.setAutoHide(true);
        validationErrorTooltip.setText("Validation Error");
        validationErrorTooltip.setMessage(editor.getErrorMessage());
        validationErrorTooltip.setLocation(getXCoordPosition(editor.getControl()), getYCoordPosition(editor.getControl()));
        validationErrorTooltip.setVisible(true);
        errorShowing = true;
    }

    @Override public void hideError() {
        if(!errorShowing) return;
        validationErrorTooltip.setVisible(false);
        validationErrorTooltip.dispose();
        errorShowing = false;
    }

    @Override public boolean isErrorShowing() {
        return errorShowing;
    }

    protected int getXCoordPosition(Control c) {
        int x = c.getLocation().x;
        Control parent = c.getParent();
        while(parent != null) {
            x += parent.getLocation().x;
            if(parent == editor.getControl().getShell()) // Relative in Shell position Bug
                break;
            parent = parent.getParent();
        }

        return x + editor.getControl().getBounds().width;
    }

    protected int getYCoordPosition(Control c) {
        int y = c.getLocation().y;
        Control parent = c.getParent();
        while(parent != null) {
            y += parent.getLocation().y;
            if(parent == editor.getControl().getShell()) // Relative in Shell position Bug
                break;
            parent = parent.getParent();
        }

        return y + 2 * editor.getControl().getBounds().height;
    }
}
