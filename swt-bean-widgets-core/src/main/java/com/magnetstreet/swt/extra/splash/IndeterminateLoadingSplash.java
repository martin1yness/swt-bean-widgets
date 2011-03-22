package com.magnetstreet.swt.extra.splash;

import com.magnetstreet.swt.extra.AnimatedGif;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import java.io.InputStream;

/**
 * IndeterminateLoadingSplash
 *
 * A simple widget that displays an indeterminate loading image
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Sep 25, 2009
 * @since Sep 25, 2009
 */
public class IndeterminateLoadingSplash {

    private static class Splash extends Composite {
        public InputStream gifImageStream = AnimatedGif.class.getClassLoader().getResourceAsStream(splashImgLoc);
        private long creation = 0L;
        public Splash(Composite composite, int i) {
            super(composite, i);
            creation = System.currentTimeMillis();
            setLayout(new FillLayout());
            AnimatedGif aimg = new AnimatedGif(this, SWT.NONE);
            aimg.setGif(gifImageStream);
        }
    }

    private static Splash splash;
    private static String splashImgLoc = "images/indetermLoadingSplash.gif";
    private static Long open;
    private static Long close;

    public static void main(String[] args) throws InterruptedException {
        IndeterminateLoadingSplash.openSplash(new Display());
        Thread.sleep(5000);
        IndeterminateLoadingSplash.closeSplash();
    }

    public static void setImage(String url) {
        splashImgLoc = url;
    }

    public static void openSplash(Display display) {
        Shell shell = new Shell(display, SWT.ON_TOP);
        shell.setText("Loading...");
        shell.setLayout(new FillLayout());
        shell.setSize(350, 150);
        splash = new Splash(shell, SWT.NONE);
        Rectangle splashRect = shell.getBounds();
        Rectangle displayRect = display.getBounds();
        int x = (displayRect.width - splashRect.width) / 2;
        int y = (displayRect.height - splashRect.height) / 2;
        shell.setLocation(x, y);
        shell.open();
    }

    public static long closeSplash() {
        long r = -1L;
        if(splash != null) {
            r = splash.creation;
            if(!splash.getShell().isDisposed()) {
                splash.getShell().dispose();
            }
            splash = null;
        }
        return r;
    }
}
