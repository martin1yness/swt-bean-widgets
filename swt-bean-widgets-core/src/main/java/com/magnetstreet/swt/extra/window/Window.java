/**
 * Copyright &copy; 2009 MagnetStreet <magnetstreet.com>
 */
package com.magnetstreet.swt.extra.window;

import com.magnetstreet.swt.extra.window.hotkey.HotKeyManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JaguarComposite
 *
 * An extention of the composite SWT widget to avoid some code
 * duplication.
 *
 * Features
 * - Hot Key Management
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Aug 6, 2009
 * @since Aug 6, 2009
 * @deprecated Features are provided by different facilities supported by the eclipse team, this class will no longer
 *             be maintained
 */
@Deprecated
public abstract class
        Window extends Composite {
    private static Set<Window> liveWindows = new HashSet<Window>();   // Solves GOTCHA with locally created windows that disappear after opening and exiting function

    private static ColorScheme globalColorScheme;
    private ColorScheme localColorScheme;

    private static String glblTitlePrefix, glblTitlePostfix;

    public enum STATE { OPENED, OPENING, HIDDEN, CLOSED, NEW, USER }
    private Logger logger = Logger.getLogger("Window");

    private java.util.List<WindowListener> listeners = new LinkedList<WindowListener>();
    private STATE state = STATE.NEW;
    protected static HotKeyManager hotKeyManager;
    protected static Image windowIcon;

    public Window() {
        super(new Shell(), SWT.SHELL_TRIM);
        liveWindows.add(this);
    }

    public Window(Shell parent, int style) {
        super(new Shell(parent, style), style);
        liveWindows.add(this);
    }

    public Window(Window parent, int style) {
        super(new Shell(parent.getShell(), style), style);
        liveWindows.add(this);
    }

    private void initialize() {
        getShell().addListener(SWT.Close, new Listener() {
            public void handleEvent(Event event) {
                event.doit = false;
                close();
            }
        });
        if(hotKeyManager==null) {
            hotKeyManager = new HotKeyManager(getDisplay());
            hotKeyManager.registerHotKey('/', SWT.SHIFT|SWT.CTRL, new Runnable() {
                public void run() {
                    hotKeyManager.showHotkeyHelpDialog();
                }
            }, "Opens the hot key help dialog.");
        }
        initGUI();
        applyWidgetActionListeners();
        defineHotkeys();
    }

    public void registerGlobalHotKey(int key, int modifier, Runnable action, String description) {
        hotKeyManager.registerHotKey(key, modifier, action, description);
    }

    public void removeGlobalHotKey(int key, int modifier) {
        hotKeyManager.removeHotKey(key, modifier);
    }

    /**
     * Convience function for opening window
     */
    public void performOpenAction() {
        switch(state) {
            case OPENED:
                RuntimeException re = new RuntimeException("Cannot open a window multiple times, each instance can only be opened once! This window's state is already in OPENED.");
                logger.log(Level.SEVERE,"Cannot open a window multiple times.", re);
                alert("Error", re.getMessage());
                break;
            case HIDDEN:
                unhide();
                break;
            case CLOSED:
                String msg = "This window instance had been closed and the SWT widget disposed, a new instance is required to open it again.";
                logger.log(Level.SEVERE,"Already closed", new RuntimeException(msg));
                alert("Bug", msg);
                break;
            case USER:
                logger.warning("Windows is in an intermediate state awaiting input from the user, open command ignored.");
                break;
            case OPENING:
                logger.warning("Window already in opening state, will NOT try to re-open!");
                break;
            default:
                try {
                    setState(STATE.OPENING);
                    long start = System.currentTimeMillis();
                    preInitGUI();

                    if(windowIcon!=null) getShell().setImage(windowIcon);
                    getShell().setLayout(new org.eclipse.swt.layout.FillLayout());

                    initialize();

                    getShell().pack();
                    WindowUtil.placeShellAutomatically(getShell());

                    postInitGUI();

                    getShell().open();                    
                    logger.logp(Level.FINER, "Window", "performOpenAction", "Took "+(System.currentTimeMillis() - start)+"ms to load Window("+this.getClass().getSimpleName()+")");
                    setState(STATE.OPENED);
                    redraw();
                    getShell().forceFocus();
                    getInitialFocusWidget().forceFocus();
                } catch(Exception e) {
                    logger.log(Level.SEVERE,"Unknown exception caught while opening window.", e);
                    setState(STATE.CLOSED);
                }
        }
    }

    /**
     * Runs the color scheme applyScheme method on self and all controls that haven't had any colors
     * nor images set on either the foreground nor the background. Any user modification will make the
     * scheme skip over the control.
     *
     * Executes color scheme application recursively
     */
    private void applyColorScheme(Composite parent) {
        if(globalColorScheme == null && localColorScheme == null) return;
        try {
            if(globalColorScheme != null)
                globalColorScheme.applyScheme(parent);
            if(localColorScheme != null)
                localColorScheme.applyScheme(parent);
            Control[] controls = parent.getChildren();
            for(Control control: controls) {
                if(control instanceof Composite) {
                    applyColorScheme((Composite)control);
                    continue;
                }
                if(globalColorScheme != null)
                    globalColorScheme.applyScheme(control);
                if(localColorScheme != null)
                    localColorScheme.applyScheme(control);
            }
        } catch (Throwable e) {
            logger.log(Level.SEVERE,"Cannot apply color scheme.", e);
        }
    }

    private void setState(STATE newState) {
        state = newState;
        for(WindowListener l: listeners)
            l.statusChanged(newState);
    }

    private synchronized STATE getState() {
        return state;
    }

    public void addWindowListener(WindowListener l) { this.listeners.add(l); }

    protected void alert(String title, String msg) {
        STATE origState = getState();
        setState(STATE.USER);
        MessageBox box;
        try {
            box = new MessageBox(getShell(), SWT.OK);
        } catch (SWTException swte) {
            box = new MessageBox(new Shell(), SWT.OK);
        }
		box.setText(title);
		box.setMessage(msg);
		box.open();
        setState(origState);
    }
    protected boolean confirm(String title, String msg) {
        STATE origState = getState();
        setState(STATE.USER);
        MessageBox box;
        try {
            box = new MessageBox(getShell(), SWT.OK|SWT.CANCEL);
        } catch (SWTException swte) {
            box = new MessageBox(new Shell(), SWT.OK|SWT.CANCEL);
        }
		box.setText(title);
		box.setMessage(msg);
        int ret = box.open();
        setState(origState);
        return (ret == SWT.OK);
    }
    protected String prompt(String title, String msg) {
        STATE origState = getState();
        setState(STATE.USER);
        Prompt.PromptListener promptListener = new Prompt.PromptListener();
        Prompt prompt = new Prompt(this, promptListener, title, msg);        
        prompt.openBlocking();
        setState(origState);
        return promptListener.getEnteredValue();
    }

    /**
     * Opens in a traditional non blocking manor
     */
    public void open() {
        BusyIndicator.showWhile(getDisplay(), new Runnable() {
            public void run() {
                getDisplay().syncExec(new Runnable() {
                    public void run() {
                        performOpenAction();
                    }
                });
            }
        });
    }
    /**
     * Convenience function for opening the window in blocking mode so it waits for the
     * window to beforeClose, this is useful when simulating dialogs and PRIMARY_MODAL
     */
    public void openBlocking() {
        BusyIndicator.showWhile(getDisplay(), new Runnable() {
            public void run() {
                getDisplay().syncExec(new Runnable() {
                    public void run() {
                        performOpenAction();
                    }
                });
            }
        });

        while( !isDisposed() ) {
            try {
                if(!getShell().getDisplay().readAndDispatch()) getShell().getDisplay().sleep();
            } catch(Throwable e) {
                logger.log(Level.SEVERE,"Window crashed while in blocking open mode.", e);
            }
        }
    }

    public static void setGlobalColorScheme(ColorScheme globalColorScheme) {
        Window.globalColorScheme = globalColorScheme;
    }

    public void setLocalColorScheme(ColorScheme localColorScheme) {
        this.localColorScheme = localColorScheme;
    }

    public static void redrawAll() {
        for(Window window: liveWindows)
            window.redraw();
    }

    public static boolean closeAll() {
        for(Window window: liveWindows.toArray(new Window[liveWindows.size()])) {
            if(!window.close()) {
                window.alert("Shutdown Error", "Unable to close window '"+window.getShell().getText()+"', please handle this windows requests before existing.");
                return false;
            }
        }
        return true;
    }

    public static void applyTitleTagPrefix(String tag) {
        glblTitlePrefix = tag;
    }

    public static void applyTitleTagPostfix(String tag) {
        glblTitlePostfix = tag;
    }

    @Override public void redraw() {
        applyTitle();
        applyColorScheme(this);
        applyColorSchemeOverrides( (localColorScheme==null) ? globalColorScheme : localColorScheme );
        super.redraw();
    }

    /**
     * Applies the title to the window's shell, manages the pre and post fix
     * global title features.
     */
    private void applyTitle() {
        String title = getTitle();
        if(glblTitlePostfix!=null)
            title += glblTitlePostfix;
        if(glblTitlePrefix!=null)
            title = glblTitlePrefix + title;
        getShell().setText(""+title);
    }

    public void setSize(int width, int height) {
        getParent().setSize(width, height);
    }

    /**
     * Sets the default window Icon for windows globally
     * @param path A path to the icon image
     */
    public static void setWindowIcon(String path) {
        windowIcon = new Image(Display.getDefault(), Window.class.getClassLoader().getResourceAsStream(path));
    }

    /**
     * Internal beforeClose method used for house keeping, sets the state on window so it can
     * easily be debugged for memory leaks.
     *
     * NOT intended to be overriden!
     * @return Whether the close was successful or not. More so whether the 'beforeClose()' method on the window
     *         stopped the close request from being executed.
     */
    protected boolean close() {
        if(beforeClose()) {
            setState(STATE.CLOSED);
            if(!getShell().isDisposed()) getShell().dispose();
            else logger.warning("Shell already disposed! This is not necessary of the beforeClose() function.");
            liveWindows.remove(this);
            return true;
        }
        return false;
    }
    /**
     * Intended to be overriden to stop any operations before
     * closing the window. By default it just executes dispose on
     * the shell.
     *
     * After beforeClose the instance must be recreated to be reopened, otherwise
     * an exception will occur.
     *
     * @return Must return if the beforeClose was successful, i.e. the window should be unregistered and garbage collected.
     * @todo wrap with aspect that ensures the method is not called from anything other than the close() method!
     */
    abstract public boolean beforeClose();

    /**
     * Method of hiding a window without disposing it so it can be
     * reopened without reinstantiation.
     */
    public void hide() {
        getShell().setVisible(false);
        setState(STATE.HIDDEN);
    }
    /**
     * Method of unhiding a window that has been previously hidden.
     */
    public void unhide() {
        getShell().setVisible(true);
        setState(STATE.OPENED);
    }

    /**
     * Used to retrieve the widget which should be focused on open. This
     * method is intended to be Overridden!
     * @return Control to be focused on window open
     */
    protected Control getInitialFocusWidget() {
        return getShell();
    }

    /**
     * Executed immediately before the initGUI() method, therefore should contain any
     * operations that must occur prior to building GUI components.
     */
    protected abstract void preInitGUI();
    /**
     * Builds all GUI components and places them in the composite.
     */
    protected abstract void initGUI();
    /**
     * Called immediately after the initGUI() method, all post GUI build and placement
     * operations should be included here.
     */
    protected abstract void postInitGUI();
    
    /**
     * Defines hotkeys for Controls or actions defined within this composite. Attaches
     * hot keys to the display the composite was created with! Called automatically after
     * postInitGUI();
     */
    protected abstract void defineHotkeys();

    /**
     * Place to define the action listeners for the window's widgets. Called after
     * the initGUI() method.
     */
    protected abstract void applyWidgetActionListeners();

    /**
     * Allows extending classes to set individual colors on specific widgets that will
     * override any ColorScheme defined in the application or instance scope.
     *
     * The default action is to override nothing.
     */
    protected void applyColorSchemeOverrides(ColorScheme scheme) {}

    /**
     * Called to determine the tile for the window instance
     * @return The title for the window.
     */
    protected abstract String getTitle();

    /**
     * Builds a packed Shell around the given composite instance for simple
     * GUI testing.
     * @param clazz The composite class that should be constructed, must extend {net.sourceforge.hibernateswt.widget.window.Window}
     * @deprecated Not very useful to save a few lines of code, often windows are too unique to use anyway
     */
    @Deprecated
    protected static void testGUI(Class<? extends Window> clazz) {
        try {
            Window inst = null;
            try {
                inst = clazz.getConstructor().newInstance();
            } catch(NoSuchMethodException nsme) {
                inst = clazz.getConstructor(Shell.class, int.class).newInstance(new Shell(), SWT.SHELL_TRIM);
            }
            inst.open();
            while (!inst.isDisposed()) {
                if (!inst.getShell().getDisplay().readAndDispatch())
                    inst.getShell().getDisplay().sleep();
            }
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }
}
