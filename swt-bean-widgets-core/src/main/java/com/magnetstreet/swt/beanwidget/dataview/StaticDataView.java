package com.magnetstreet.swt.beanwidget.dataview;

import com.magnetstreet.swt.exception.ViewDataBeanValidationException;
import com.magnetstreet.swt.util.EditableDataGridUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * StaticDataView
 *
 * Designed to work with the legacy Dataview's where the components and validation
 * was all statically defined in an extending class.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Dec 15, 2009
 * @since Dec 15, 2009
 * @deprecated This is only intended for legacy code dependency, declaratively define
 *             layouts and use the DynamicDataView class
 */
@Deprecated
public abstract class StaticDataView<T> extends AbstractDataView<T> {
    private Logger logger = Logger.getLogger(StaticDataView.class.getSimpleName());

    protected StaticDataView(Composite arg0, int arg1) {
        super(arg0, arg1);
    }

    /**
     * Default widget creator, dynamically creates widgets without any
     * layout information, override or add layouts later.
     */
    protected void initGUI() {
        Button enableEditingCheckbox = new Button(this, SWT.CHECK);
        for(Field f: this.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            try {
                boolean type = Control.class.isAssignableFrom(f.getType());
                boolean value = f.get(this)==null;
                if(type && value) {
                    f.set(this, f.getType().getConstructor(Composite.class, int.class).newInstance(this, SWT.NONE));
                }
            } catch(Throwable t) {
                throw new RuntimeException(t);
            }
        }
        registerEnableEditingWidget(enableEditingCheckbox);
    }
    /**
     * Runs checks to verify that all data is valid, if this fails data
     * cannot be saved. Returns a map of controls that where invalid with
     * a matching error string.
     */
    protected abstract Map<Control, String> validateUserInput();

    @Override
    public T getViewDataObject() throws ViewDataBeanValidationException {
        errorMap = validateUserInput();
        if(errorMap.size()!=0)
            throw new ViewDataBeanValidationException("Bean could not be validated, review error map.");
        return super.getViewDataObject();
    }

    public Control findWidget(Field beanProperty) {
        Map<Class, Boolean> objRestriction = new HashMap<Class, Boolean>();
        objRestriction.put(CLabel.class, false);
        objRestriction.put(Label.class, false);
        objRestriction.put(Control.class, true);
        beanProperty.setAccessible(true);
        Field controlField = EditableDataGridUtil.findSimilarlyNamedField(getClass(), beanProperty.getName(), objRestriction).get(0);
        controlField.setAccessible(true);

        try {
            return (Control)controlField.get(this);
        } catch (IllegalAccessException e) {
            logger.log(Level.SEVERE, "Unable to access control field.", e);
            return null;
        }
    }
}
