package com.magnetstreet.swt.extra.window;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * WindowUtil
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Nov 24, 2009
 * @since Nov 24, 2009
 */
public class WindowUtil {
    public static void placeShellAutomatically(Shell shell) {
		Composite parent = shell.getParent();
		int x = parent == null ? 0 : parent.getBounds().x + 25;
		int y = parent == null ? 0 : parent.getBounds().y + 25;
		shell.setBounds(x, y, shell.getBounds().width, shell.getBounds().height);
	}
}
