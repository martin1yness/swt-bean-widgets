package com.magnetstreet.swt.extra.splash;

import com.magnetstreet.swt.extra.AnimatedCanvas;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import java.io.IOException;
import java.net.URI;

/**
 * AbstractSplash
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 1/4/11
 */
public abstract class AbstractSplash extends Shell {
    private AnimatedCanvas background;

    public AbstractSplash(Display d, URI imageFile) throws IOException {
        super(d, SWT.ON_TOP|SWT.APPLICATION_MODAL|SWT.CURSOR_WAIT);

        initializeComponents();

        background = new AnimatedCanvas(this, SWT.CURSOR_WAIT|SWT.TRANSPARENT);
        background.setImageLocation(imageFile);
        FormData backgroundFD = new FormData(background.getSize().x, background.getSize().y);
        backgroundFD.left = new FormAttachment(0,1,0);
        backgroundFD.right = new FormAttachment(1,1,0);
        backgroundFD.top = new FormAttachment(0,1,0);
        backgroundFD.bottom = new FormAttachment(1,1,0);
        background.setLayoutData(backgroundFD);
    }

    protected void checkSubclass () { }

    /**
     * Used by implementing classes to initialize additional GUI components over the top of
     * the background canvas.
     */
    protected abstract void initializeComponents();


    @Override public void open() {
        pack();
        super.open();    //To change body of overridden methods use File | Settings | File Templates.
        background.animate();
        Rectangle splashRect = getBounds();
        Rectangle displayRect = getDisplay().getBounds();
        int x = (displayRect.width - splashRect.width) / 2;
        int y = (displayRect.height - splashRect.height) / 2;
        setLocation(x, y);
    }

    @Override public void close() {
        background.inanimate();
        super.close();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public static void main(String[] args) throws Exception {
        URI fileLocation = AbstractSplash.class.getClassLoader().getResource("images/indetermLoadingSplash.gif").toURI();
        AbstractSplash splash = new AbstractSplash(Display.getDefault(), fileLocation) {
            private ProgressBar progressBar;

            @Override public void initializeComponents() {
                progressBar = new ProgressBar(this, SWT.NONE);
                FormData progressBarFormData = new FormData(25,25);
                progressBarFormData.left = new FormAttachment(0,100,5);
                progressBarFormData.right = new FormAttachment(100,100,-5);
                progressBarFormData.bottom = new FormAttachment(100,100,-5);
                progressBar.setLayoutData(progressBarFormData);
            }
        };

        splash.pack();
        splash.open();
        while(!splash.isDisposed()) {
            if(!splash.getDisplay().readAndDispatch())
                splash.getDisplay().sleep();
        }
    }
}
