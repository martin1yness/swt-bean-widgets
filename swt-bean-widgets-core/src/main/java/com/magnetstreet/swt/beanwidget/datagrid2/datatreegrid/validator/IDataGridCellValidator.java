package com.magnetstreet.swt.beanwidget.datagrid2.datatreegrid.validator;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;

/**
 * IDataGridCellValidator
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 4/29/11
 */
public interface IDataGridCellValidator extends ICellEditorValidator {
    /**
     * Provides a visual cue that the input was not valid.
     * @param cellEditor The jface object in control of providing the editor control
     */
    public void showError(CellEditor cellEditor);

    /**
     * Hides the error visual, executed when the user corrects the input.
     */
    public void hideError();

    /**
     * @return Whether the error is currently being displayed
     */
    public boolean isErrorShowing();
}
