package com.magnetstreet.swt.extra.window.hotkey;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * HotKey
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Aug 6, 2009
 * @since Aug 6, 2009
 */
public class HotKey {
    public final int key;
    public final int modifier;
    public final Listener listener;
    public final String description;
    public boolean enabled;

    HotKey(int key, int modifier, final Runnable action, String description) {
        this.key = key;
        this.modifier = modifier;
        this.enabled = true;
        this.description = description;
        this.listener = new Listener() {
            public void handleEvent(Event event) {
                if(trigger(event)) action.run();
            }
        };
    }
    public boolean trigger(Event evnt) {
        return (enabled && evnt.stateMask == modifier && evnt.keyCode == key);
    }

    @Override public boolean equals(Object o) {
        if(o instanceof HotKey) {
            HotKey obj = (HotKey)o;
            if(key == obj.key && modifier == obj.modifier) return true;
        }
        return false;
    }
}