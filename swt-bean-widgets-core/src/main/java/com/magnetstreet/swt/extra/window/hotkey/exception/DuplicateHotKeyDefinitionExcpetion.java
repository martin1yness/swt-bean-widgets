package com.magnetstreet.swt.extra.window.hotkey.exception;

/**
 * DuplicateHotKeyDefinitionExcpetion
 *
 * Thrown when a duplicate key + modifier combitination is attempted to be created
 * on a display within the HotKeyManager class.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Aug 6, 2009
 * @since Aug 6, 2009
 */
public class DuplicateHotKeyDefinitionExcpetion extends RuntimeException {
    public DuplicateHotKeyDefinitionExcpetion() {
    }
    public DuplicateHotKeyDefinitionExcpetion(String s) {
        super(s);
    }
    public DuplicateHotKeyDefinitionExcpetion(String s, Throwable throwable) {
        super(s, throwable);
    }
    public DuplicateHotKeyDefinitionExcpetion(Throwable throwable) {
        super(throwable);
    }
}
