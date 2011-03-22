package com.magnetstreet.swt.extra.splash;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * IndeterminateLoadingSplash
 *
 * A simple widget that displays an indeterminate loading image
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 1.1.0 Jan 25, 2011
 * @since 1.1.0
 */
public class IndeterminateLoadingSplash2 extends AbstractLoadingSplash {
    private static class Splash extends AbstractSplash {
        protected long creationTime;
        public Splash(Display d, URI imageFile) throws IOException {
            super(d, imageFile);
            creationTime = System.currentTimeMillis();
        }
        @Override protected void initializeComponents() { }
    }

    private static URI backgroundImage;

    public IndeterminateLoadingSplash2() {
        addListener(new Listener() {
            @Override public void handleError(LoadingTask task) {
                MessageBox alert = new MessageBox(new Shell(), SWT.ERROR);
                alert.setMessage("Unable to execute loading tasks...");
                alert.setText(task.getError().getMessage()+"\n\n"+task.getError());
                alert.open();
            }
            @Override public void handleSuccess() {
                splash.getDisplay().syncExec(new Runnable() {
                    public void run() {
                        splash.close();
                        splash = null;
                    }
                });
            }
            @Override public void handleCancel() { handleSuccess(); }
        });
    }

    public static void main(String[] args) throws InterruptedException {
        IndeterminateLoadingSplash2.setImage("images/indetermLoadingSplash.gif");
        IndeterminateLoadingSplash2 splasher = new IndeterminateLoadingSplash2();

        splasher.splashNonBlocking(Display.getDefault(), new LoadingTask("Test Task", "This is a sample task.", 5000L) {
            public void doAction() throws InterruptedException {
                Thread.sleep(6000);
                System.out.println("Second action completed.");
            }
        });
        while(splash!=null && !splash.isDisposed()) {
            if(!splash.isDisposed() && !splash.getDisplay().readAndDispatch())
                splash.getDisplay().sleep();
        }

        splasher.splash(Display.getDefault(), new LoadingTask("Test Task", "This is a sample task.", 5000L) {
            public void doAction() throws InterruptedException {
                Thread.sleep(3000);
                System.out.println("First action completed.");
            }
        });

        System.exit(0);
    }

    public static void setImage(String url) {
        try {
            backgroundImage = IndeterminateLoadingSplash.class.getClassLoader().getResource(url).toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Unable to set background image location.", e);
        }
    }

    @Deprecated
    public static void openSplash(Display display) { /* no longer available */ }

    @Deprecated
    public static long closeSplash() { return 1; }

    @Override public void splash(Display display, LoadingTask... tasks) {
        if(splash!=null)
            throw new RuntimeException("Cannot splash more than once without closing the original splash.");
        try {
            splash = new Splash(display, backgroundImage);
            splash.open();
            applyDefaultCloseListener(tasks);
            executeTasksSequentially(tasks);
            while(splash != null && !splash.isDisposed()) {
                if(!splash.getDisplay().readAndDispatch())
                    splash.getDisplay().sleep();
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to create loading splash.", e);
        }
    }

    @Override public void splashNonBlocking(Display display, LoadingTask... tasks) {
        if(splash!=null)
            throw new RuntimeException("Cannot splash more than once without closing the original splash.");
        try {
            splash = new Splash(display, backgroundImage);
            splash.open();
            applyDefaultCloseListener(tasks);
            executeTasksSequentially(tasks);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create loading splash.", e);
        }
    }
}
