package com.magnetstreet.swt.beanwidget.dataview;

import com.magnetstreet.swt.exception.ViewDataBeanValidationException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract Data View
 *
 * Extended to provide a GUI layer ontop of Hibernate annotated objects.
 * @version 0.2.0 Added automatic widget to object conversion
 *          0.1.0 Original Version
 * @param <T>
 */
public abstract class AbstractDataView<T> extends org.eclipse.swt.widgets.Composite implements DataView<T> {
	private Logger logger = Logger.getLogger(AbstractDataView.class.getSimpleName());
    
	protected T viewDataObject;

    protected Button enableEditingWidget;
    public Set<Control> widgetExclusionSet = new HashSet<Control>();

    public Map<Control, String> errorMap = new HashMap<Control, String>();

    protected static void showTestGUI(AbstractDataView inst) {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		inst.setParent(shell);
		Point size = inst.getSize();
		shell.setLayout(new FillLayout());
		shell.layout();
		if(size.x == 0 && size.y == 0) {
			inst.pack();
			shell.pack();
		} else {
			Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
			shell.setSize(shellBounds.width, shellBounds.height);
		}
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	protected AbstractDataView(Composite arg0, int arg1) {
		super(arg0, arg1);
        initialize();
	}

	/**
	 * Called immediately after instantiation, similar to the servlet design pattern with
	 * extra restrictions, calls the preInitGUI(), initGUI(), postInitGUI()
	 * methods. After PreInit and before init GUIs, the default enableEditingWidget is created.
	 */
	protected void initialize() {
		preInitGUI();
		initGUI();
        postInitGUI();
	}
	/**
	* Overriding checkSubclass allows this class to extend org.eclipse.swt.widgets.Composite
	*/
	protected void checkSubclass() {}

	/**
	 * Registered pre-defined required enable editing control widget
	 * @param widget The button that a selection listener should be attached
	 * 				 to tracking a toggle of enabled/disabled status for controls
	 */
	protected void registerEnableEditingWidget(Button widget) {
		this.enableEditingWidget = widget;
		this.enableEditingWidget.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent arg0) {
				toggleWidgetsEditable();
			}
		});
        toggleEnabledAllWidgets(this, false);
        enableEditingWidget.setSelection(false);
	}

    /**
     * Registers a given control on the exclusion list, so that various
     * features including the enable/disable editing widget feature
     * will not affect it.
     * @param widget The widget to exclude
     */
    protected void excludeWidget(Control widget) {
        widgetExclusionSet.add(widget);
    }

    /**
     * if the given widget was on the exlusion list it will be removed from
     * the exclusion list. Else nothing happens
     * @param widget The widget to remove from the exclusion list.
     */
    protected void includeWidget(Control widget) {
        if(widgetExclusionSet.contains(widget))
            widgetExclusionSet.remove(widget);
    }

	/**
	 * Any operations to be performed before controls are added to the composite
	 */
	protected abstract void preInitGUI();
	/**
	 * Creates all controls/widgets that will be part of the composite, this method should
     * be overrided to define the controls/widgets in a gui, otherwise they are created
     * with no layout and no style.
	 */
	protected abstract void initGUI();
	/**
	 * Any actions that need to occur just after controls/widgets are added to
	 * the composite.
	 */
	protected abstract void postInitGUI();

	/**
	 * Provides a global means of enabling/disabling editing on a data view control
	 */
	public void toggleWidgetsEditable() {
        boolean enabled = enableEditingWidget.getSelection();
		logger.logp(Level.FINER, "AbstractDataView", "toggleWidgetsEditable", "Set " + getClass().getName() + " widgets editable: " + enabled);
		toggleEnabledAllWidgets(this, enabled);
        enableEditingWidget.redraw();
	}
    /**
     * Auxillary recursive function for enabling/disabling all widgets within a given
     * composite, traditionally this dataview instance.
     * @param parent
     * @param enabled
     */
    private void toggleEnabledAllWidgets(Composite parent, boolean enabled) {
        Control[] controls = parent.getChildren();
        for(Control control: controls) {
            if(widgetExclusionSet.contains(control)) continue;
            if(control instanceof Composite) {
                toggleEnabledAllWidgets((Composite)control, enabled);
            }
            if(control != enableEditingWidget) control.setEnabled(enabled);
        }
    }

	/**
	 * Updates the viewDataObject according to any modifications made to
	 * widgets on parent control, called every time a getViewDataObject() is called.
     *
     * Is automatically called by the getViewDataObject() method.
	 */
	protected abstract void updateViewDataObjectFromWidgets();

	/**
	 * Updates all of the widgets to match what a view data object defines as
	 * the newest data, called every time a setViewDataObject() is called. Intended
     * only for setup. Loops through dynamic widgets and puts the beans value into
     * the widget for display and edit.
     *
     * Is automatically called by the setViewDataObject() method.
	 */
	protected abstract void updateWidgetsFromViewDataObject();

    /**
     * {@inheritDoc}
     */
    public void showInputErrors() {
        String errorMsg = "";
        for(String error: getValidationErrorMap().values())
            errorMsg += error + "\r\n";
        for(Control controlValidation: getValidationErrorMap().keySet()) {
            controlValidation.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
            controlValidation.setToolTipText(getValidationErrorMap().get(controlValidation));
        }
        MessageBox box = new MessageBox(getParent().getShell(), SWT.OK);
		box.setText("Input Valiation Error(s)");
		box.setMessage(errorMsg);
		box.open();
    }
    /**
     * {@inheritDoc}
     */
    public Map<Control, String> getValidationErrorMap() { return errorMap; }
    /**
     * {@inheritDoc}
     */
    public void resetViewDataObject() {
        updateWidgetsFromViewDataObject();
    }

	/**
	 * The object the data view represents, example: hibernate annotation mapped object,
	 * should not be called by the updateViewDataObjectFromWidgets() as this will create a
	 * stack overflow error, only use is by external data view users.
	 * @return the viewDataObject
	 */
	public T getViewDataObject() throws ViewDataBeanValidationException {
		updateViewDataObjectFromWidgets();
		return viewDataObject;
	}
	/**
     * Allows setting of the data view's bean. Will store a reference to the bean
     * and dynamically build widgets. Use sparingly.
	 * @param viewDataObject the viewDataObject to set
	 */
	public void setViewDataObject(T viewDataObject) {
        this.viewDataObject = viewDataObject;
        updateWidgetsFromViewDataObject();
	}



    /**
     * Builds a default CLabel
     * @param title The text for the label to contain
     * @return The created CLabel
     */
    protected CLabel getDefaultCLabel(String title) {
        CLabel label = new CLabel(this, SWT.RIGHT);
        label.setText(title);
        return label;
    }

    /**
     * Clone implementation clears data view object, does not copy it as this may
     * interfere with hibernate management of its persistence.
     * @return
     */
    @Override public Object clone() {
        try {
            AbstractDataView<T> clone = (AbstractDataView<T>)super.clone();
            clone.setViewDataObject(null);
            return clone;
        } catch (CloneNotSupportedException e) {
            logger.log(Level.SEVERE, "Clone Error", e);
            return null;
        }
    }
}