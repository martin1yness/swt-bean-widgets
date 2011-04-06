package com.magnetstreet.swt.util;

/**
 * This is special closure style pattern that allows a command
 * to be attached to a custom widget's key event listener watching
 * for return keys only. This is a convenience method and could be done
 * using the standard SWT KeyAdapter.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0
 * @since 6/29/2009
 * @deprecated This is achievable through the Selection listener on a text field
 */
@Deprecated
public abstract class ReturnKeyCommand<T> {
	private int runCount;
	private T returnValue;
	
	public void run() {
		returnValue = command();
		runCount++;
	}
	
	public abstract T command();
	
	public int getRunCount() { return runCount; }
	public T getReturnValue() { return returnValue; }
}
