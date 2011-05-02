package com.magnetstreet.swt.beanwidget.datagrid2.editor;

import com.magnetstreet.swt.beanwidget.datagrid2.validator.IDataGridCellValidator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Table;

import java.util.logging.Logger;

/**
 * AbstractDataGridCellEditingSupport
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 4/28/11
 */
public abstract class AbstractDataGridCellEditingSupport extends EditingSupport {
    private Logger logger = Logger.getLogger(AbstractDataGridCellEditingSupport.class.getSimpleName());

    protected IDataGridCellValidator validator;
    protected CellEditor cellEditor;

    public AbstractDataGridCellEditingSupport(TableViewer viewer) {
        super(viewer);
    }

    protected abstract CellEditor instantiateCellEditor(Table composite);
    protected abstract IDataGridCellValidator instantiateValidator();
    protected abstract Object getControlValue(Object modelObject);
    protected abstract void setModelValue(Object modelObject, Object newValidValueFromControl);

    @Override protected CellEditor getCellEditor(Object element) {
        if(cellEditor == null) {
            cellEditor = instantiateCellEditor(((TableViewer) getViewer()).getTable());
            validator = instantiateValidator();
            cellEditor.setValidator(validator);
        }
        return cellEditor;
    }
    @Override protected Object getValue(Object element) {
        return getControlValue(element);
    }
    @Override protected void setValue(Object element, Object value) {
        if(validator!=null) {
            validator.hideError();
            if(!cellEditor.isValueValid()) {
                validator.showError(cellEditor);
                return;
            }
        }
        setModelValue(element, value);
        getViewer().refresh();
    }
    @Override protected boolean canEdit(Object element) { return true; }
}
