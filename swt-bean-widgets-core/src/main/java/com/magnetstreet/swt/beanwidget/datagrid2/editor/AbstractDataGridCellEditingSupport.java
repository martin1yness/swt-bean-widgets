package com.magnetstreet.swt.beanwidget.datagrid2.editor;

import com.magnetstreet.swt.beanwidget.datagrid2.DataTableGrid;
import com.magnetstreet.swt.beanwidget.datagrid2.validator.IDataGridCellValidator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Table;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AbstractDataGridCellEditingSupport
 *
 * <T> is the model object type
 * <K> is this cell editor's return type (usually String or Integer). Must implement Comparable
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 4/28/11
 */
public abstract class AbstractDataGridCellEditingSupport<T,K> extends EditingSupport {
    private Logger logger = Logger.getLogger(AbstractDataGridCellEditingSupport.class.getSimpleName());

    protected IDataGridCellValidator validator;
    protected CellEditor cellEditor;

    protected K cachedValue;

    protected AtomicBoolean changed = new AtomicBoolean(false);

    public AbstractDataGridCellEditingSupport(DataTableGrid<T> dataTableGrid) {
        super(dataTableGrid.getTableViewer());
    }

    protected abstract CellEditor instantiateCellEditor(Table composite);
    protected abstract IDataGridCellValidator instantiateValidator();
    protected abstract K getControlValue(T modelObject);
    protected abstract void setModelValue(T modelObject, K newValidValueFromControl);

    protected void setValueChanged(K newValue) {
        if(newValue == null)
            changed.set(false);
        else if(cachedValue == null)
            changed.set(true);
        else
            changed.set(((Comparable<K>)cachedValue).compareTo(newValue) != 0);
    }

    public boolean getChanged() {
        return changed.get();
    }

    @Override protected synchronized CellEditor getCellEditor(Object element) {
        if(cellEditor == null) {
            cellEditor = instantiateCellEditor(((TableViewer) getViewer()).getTable());
            cellEditor.addListener(new ICellEditorListener() {
                @Override public void applyEditorValue() {
                    setValueChanged((K) cellEditor.getValue());
                }
                @Override public void cancelEditor() {
                    changed.set(false);
                    if(validator!=null)
                        validator.hideError();
                }
                @Override public void editorValueChanged(boolean oldValidState, boolean newValidState) {
                    if(validator!=null) {
                        if(validator.isValid(cellEditor.getValue()) == null)
                            validator.hideError();
                        else
                            validator.showError(cellEditor);
                    }
                }
            });
            validator = instantiateValidator();
            cellEditor.setValidator(validator);
        }
        return cellEditor;
    }
    @Override protected Object getValue(Object element) {
        cachedValue = getControlValue((T)element);
        return cachedValue;
    }
    @Override protected void setValue(Object element, Object value) {
        if( !getChanged() ) {
            return;
        }
        if(validator!=null) {
            validator.hideError();
            if(!cellEditor.isValueValid()) {
                validator.showError(cellEditor);
                return;
            }
        }
        try {
            setModelValue((T)element, (K)value);
            getViewer().refresh();
        } catch(Throwable t) {
            logger.log(Level.WARNING, "Table setter method was unable to save inputted data to the backing object.", t);
        }
    }
    @Override protected boolean canEdit(Object element) { return true; }
}
