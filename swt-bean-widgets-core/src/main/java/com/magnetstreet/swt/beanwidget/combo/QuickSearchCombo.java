package com.magnetstreet.swt.beanwidget.combo;

import com.magnetstreet.swt.util.KeyEventUtil;
import com.magnetstreet.swt.util.ReturnKeyCommand;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import java.util.logging.Logger;

/**
 * Quick Search Combo
 * 
 * Custom implementation of the eclipse SWT Combo widget, allows mutli key quick searching
 * (AutoFill) like in many common applications.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0
 * @since June 29, 2009
 */
public class QuickSearchCombo extends Combo {
    private Logger logger = Logger.getLogger("QuickSearchCombo");

	private ReturnKeyCommand<?> returnKeyCommand;
	private boolean returnKeyCommandEnabled = true;
	private int resetPauseTime = 1000;		// The time the user is allowed to pause inbetween keystokes before the search string is reset.
	
	private volatile long lastKeyStrokeTime = 0;		// Tracks time in between keystrokes, used to reset search string.
	private volatile String search = "";	// Buffer for the item search

	public QuickSearchCombo(Composite arg0, int arg1) {
		super(arg0, arg1);
		init();
	}
	
	/**
	 * Sets the time in milliseconds that the user can pause before search is
	 * reset. Default is 1000 milliseconds (1s)
	 * @param ms millisecond integer
	 */
	public void setSearchPauseResetTime(int ms) { this.resetPauseTime = ms; }
	
	/**
	 * Whether to perform return key action
	 * @param enabled
	 */
	public void setReturnKeyCommandEnabled(boolean enabled) {
		returnKeyCommandEnabled = enabled;
	}
	
	/**
	 * Sets the command to execute when return key is released.
	 * @param command
	 */
	public void setReturnKeyCommand(ReturnKeyCommand<?> command) {
		this.returnKeyCommand = command;
	}
	
	@Override
	protected void checkSubclass() {}
	
	/**
	 * Implement custom functionality.
	 */
	private void init() {
		setupJaguarColors();
		createFocusUnfocusListeners();
		createKeyEventListeners();
		setReturnKeyCommand(new ReturnKeyCommand<Integer>() {
			@Override
			public Integer command() {
                logger.warning("Undefined Return Key Command, disable the return key feature or define an action on widget.");
                return null;
			}
		});
	}
	
	/**
	 * Define all colors to match those defined the Jaguar SWT color class.
	 */
	private void setupJaguarColors() {
		setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));
		setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
	}
	
	/**
	 * Defines the functionality of focus and unfocus events. Manages
	 * color changes and clearing search buffer.
	 */
	private void createFocusUnfocusListeners() {
		this.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent arg0) {
				setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));
				setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
				search = "";
			}
			public void focusGained(FocusEvent arg0) {
				setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
				setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
			}
		});
	}
	
	/**
	 * Defines the key stroke event handler, this handler is used to search the
	 * list based on keystrokes inputed by the user.
	 */
	private void createKeyEventListeners() {
		addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent arg0) {
				selectComboItemBySearch();
			}
			public void keyPressed(KeyEvent arg0) {
				if( (lastKeyStrokeTime + resetPauseTime) < System.currentTimeMillis() )
					search = "";
				lastKeyStrokeTime = System.currentTimeMillis();
				
				if( (arg0.keyCode == SWT.CR || arg0.keyCode == SWT.KEYPAD_CR)
                        && returnKeyCommandEnabled && returnKeyCommand instanceof ReturnKeyCommand<?>) {
					returnKeyCommand.run();
					return;
				}

				switch(arg0.keyCode) {
					case SWT.ESC:
						search = "";
						break;
					case SWT.DEL:
						search = "";
						break;
					case SWT.BS:
						try {
							search = search.substring(0, search.length()-1);
						}catch(StringIndexOutOfBoundsException e) { search = ""; }
						break;
					case SWT.ARROW_DOWN:
						setVisible(true);
						selectNext();
						break;
					case SWT.ARROW_UP:
						setVisible(true);
						selectPrevious();
					default:
                        if(KeyEventUtil.isNormalAsciiKey(arg0.keyCode)) {
						    search += arg0.character;
                        } else if(KeyEventUtil.convertKeypadNumberIfNumLock(arg0.stateMask, arg0.keyCode) != -1) {
                            search += KeyEventUtil.convertKeypadNumberIfNumLock(arg0.stateMask, arg0.keyCode);
                        }
				}
				
			}
		});
	}
	
	/**
	 * Selects the item immediately before the currently selected item, will 
	 * not go past the first item. 
	 */
	private void selectPrevious() {
		int prev = this.getSelectionIndex();
		select( (prev < 0) ? 0 : prev );
		search = "";
	}
	
	/**
	 * Selects the item immediately after the currently selected item, will
	 * not go past last item.
	 */
	private void selectNext() {
		int next = this.getSelectionIndex();
		select( (next > getItems().length) ? getItems().length : next );
		search = "";
	}
	
	/**
	 * Uses the class instance variable 'search' to try and find an element in
	 * the combo's list that matches.
	 */
	private void selectComboItemBySearch() {
		if("".equals(search.trim())) return;
		String[] items = getItems();
		for(String item: items) {
			if(item.toLowerCase().startsWith(search.trim())) {
				setText(item);
				select(indexOf(item));
				
				return;
			}
		}
		select(0);
	}
}
