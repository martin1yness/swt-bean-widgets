package com.magnetstreet.swt.extra.window;

/**
 * The Window Listener Interface
 *
 * Used to implement a watcher that will get notified
 * whenever certain events happen in a Window object
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 3/18/2009
 */
public interface WindowListener {
    public void statusChanged(final Window.STATE newState);

    // @todo unimplemented
    public void alertInvoked();
    // @todo unimplemented
    public void confirmInvoked();
}
