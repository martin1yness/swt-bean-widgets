package com.magnetstreet.swt.extra;


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

import java.util.logging.Logger;

/**
 * The Jaguar CTabFolder Implementation
 * 
 * Provides an implementation of a CTabFolder swt custom widget
 * specific for jaguar
 * 
 * Additional Feature List:
 * - Binds ALT + # hotkeys to each tab.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0
 */
public class AdvancedCTabFolder extends CTabFolder {
    private Logger logger = Logger.getLogger(AdvancedCTabFolder.class.getSimpleName());

    private KeyAdapter ka = new KeyAdapter() {
        public void keyPressed(KeyEvent evnt) { }
        public void keyReleased(KeyEvent evnt) {
            /*
            if(evnt.stateMask != SWTEntity.ALT) return;
            try {
                Integer tabIndex = new Integer( ""+((char)evnt.keyCode) );
                if(getItemCount() >= tabIndex && tabIndex!=0)
                    setSelection(tabIndex-1);
            } catch (NumberFormatException nfe) {}
            */
        }
    };

	/**
	 * @param arg0
	 * @param arg1
	 */
	public AdvancedCTabFolder(Composite arg0, int arg1) {
		super(arg0, arg1);
        init();
	}

    private void init() {
        applyQuickSwitchKeyBindings();
    }

    private AdvancedCTabFolder getSelf() { return this; }

	/**
	 * Defines the ALT + # key bindings for quick tab switching. Should be called
     * after all children are added to the folder
	 */
	private void applyQuickSwitchKeyBindings() {
        getDisplay().addFilter(SWT.KeyUp, new Listener() {
            public void handleEvent(Event event) {
                if(event.stateMask != SWT.ALT) return;
                if(!Character.isDigit((char)event.keyCode)) return;
                Integer reqTabNum = new Integer(""+(char)event.keyCode);
                if(getItemCount() < reqTabNum) return;

                logger.warning("Selection line removed because of compile error in some version of SWT.");
                //if(event.widget == getSelf() || checkChildren(event.widget, getChildren())) setSelection(reqTabNum - 1);
            }
        });
    }

    private boolean checkChildren(Widget compareTo, Control[] children) {
        for(Control child: children) {
            if(child == compareTo) return true;
            if(child instanceof Composite)
                if( checkChildren(compareTo, ((Composite)child).getChildren()) ) return true;
        }
        return false;
    }
}
