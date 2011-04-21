package com.magnetstreet.swt.extra;

import com.magnetstreet.swt.util.ReturnKeyCommand;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import java.util.logging.Logger;

/**
 * Visual Input Text Widget
 * 
 * This widget is an extension of the generic SWT Text widget, it adds the
 * coloring features defined by the JaguarSWTColor class and allows for
 * future feature additions across entire app if used inplace of all generic
 * Text widgets.
 * 
 * Extra Feature List:
 * - Default Text can be auto cleared and auto filed on focus gain/loss
 * - Enter/Return key presses trigger action, removes the need for buttons.
 * - Quick Key Access, allows for a hot key to trigger focus of text input.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0
 * @since 6/29/2009
 */
public class
        VisualInputText extends Text {
    private Logger logger = Logger.getLogger(VisualInputText.class.getSimpleName());
	private boolean autoClearDefaultText = true;
	private boolean autoRefillDefaultText = true;
	private boolean returnKeyCommandEnabled = true;
	private boolean hotKeyAccessEnabled = false;
	private ReturnKeyCommand<?> returnKeyCommand;
	private int hotKeyModifier;
	private int hotKey;
	private String defaultText = "";

	/**
	 * @param arg0
	 * @param arg1
	 */
	public VisualInputText(Composite arg0, int arg1) {
		super(arg0, arg1);
		init();
	}
	
	/**
	 * Allows the user to set the default string to be shown to the user
	 * in this text field.
	 */
	public void setDefaultText(String text) {
		super.setText(text);
		defaultText = text;
	}
	
	/**
	 * Convenience method to turns on hot key access for text widget when
	 * hot key is pressed within the shell of the widgets instantiation.
	 * @param display The shell to apply hot key to, used to 
	 *              limit listener to only certain parts of application.
	 * @param key The int value for the character on keyboard (ex 'c')
	 * @param modifier The SWT defined modifier (ex SWT.ALT) -1 for no modifier
	 */
	public void enableHotKeyAccess(Display display, int key, int modifier) {
		hotKeyAccessEnabled = true;
		hotKey = key;
		hotKeyModifier = modifier;
		display.addFilter(SWT.KeyUp, new Listener() {
			public void handleEvent(Event arg0) {
				if(hotKeyAccessEnabled && arg0.stateMask == hotKeyModifier && arg0.keyCode == hotKey)
					setFocus();
			}
		});
	}
	
	public void disableHotKeyAccess() { hotKeyAccessEnabled = false; }
	
	/**
	 * Allows for the return key listener to enabled/disabled
	 * @param enabled
	 */
	public void setReturnKeyEnabled(boolean enabled) {
		this.returnKeyCommandEnabled = enabled;
	}
	
	/**
	 * Allows the usage point to set a return key command for this
	 * instance.
	 * @param c
	 */
	public void setReturnKeyCommand(ReturnKeyCommand<?> c) {
		this.returnKeyCommand = c;
	}

	/**
	 * Allows changing the behavior of the default text, if true it will be cleared for
	 * the user when selected. Default is true
	 * @param autoClearDefaultText
	 */
	public void setAutoClearDefaultText(boolean autoClearDefaultText) { this.autoClearDefaultText = autoClearDefaultText; }

	/**
	 * Allows changing the behavior of the default text, if true when the user un focuses this field
	 * the default text will be re displayed. Default is true
	 * @param autoRefillDefaultText
	 */
	public void setAutoRefillDefaultText(boolean autoRefillDefaultText) { this.autoRefillDefaultText = autoRefillDefaultText; }
	
	@Override
	protected void checkSubclass() {}
	
	/**
	 * Handles initialization functions
	 */
	private void init() {
		setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));
		setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		addFocusUnfocusEventHandlers();
		addReturnKeyEventHandler();
		setReturnKeyCommand(new ReturnKeyCommand<Integer>() {
			@Override public Integer command() {
				logger.warning("No return key command defined for widget, please disable feature on widget or define action.");
                return null;
			}
		});
	}
	
	/**
	 * Define the focus and un-focus events. Adds coloring differentiation
	 * if defined in JaguarSWTColor
	 */
	private void addFocusUnfocusEventHandlers() {
		addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent arg0) {
				if(autoRefillDefaultText && getText().trim().equals(""))
					setText(defaultText);
				setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));
				setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
			}
			public void focusGained(FocusEvent arg0) {
				if(autoClearDefaultText && getText().trim().equalsIgnoreCase(defaultText))
					setText("");
				setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
				setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
			}
		});
	}
	
	private void addReturnKeyEventHandler() {
		addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent evt) {
				if( (evt.keyCode == SWT.CR || evt.keyCode == SWT.KEYPAD_CR) && returnKeyCommandEnabled && returnKeyCommand instanceof ReturnKeyCommand<?>)
					returnKeyCommand.run();					
			}
		});
	}
}
