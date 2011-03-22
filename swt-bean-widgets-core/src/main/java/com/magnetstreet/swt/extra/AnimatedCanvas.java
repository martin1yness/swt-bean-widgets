package com.magnetstreet.swt.extra;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AnimatedCanvas
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 1/5/11
 */
public class AnimatedCanvas extends Canvas {
    private ImageLoader imageLoader;
    private ImageData[] images;
    private int width, height, x, y, delay;
    private AtomicBoolean animate = new AtomicBoolean(false);
    private AtomicInteger currentImageNumber = new AtomicInteger(0);
    private PaintListener paintListener = new PaintListener() {
        public void paintControl(PaintEvent event){
            int i = getCurrentImageNumber();
            Image image = new Image(getDisplay(), images[i]);
            GC gc = new GC(image);
            event.gc.drawImage(image,0,0);
            image.dispose();
            gc.dispose();
        }
    };

    public AnimatedCanvas(Composite composite, int i) {
        super(composite, i);
    }

    protected void checkSubclass () { }

    public synchronized void setImageLocation(URI imageLocation) throws IOException {
        imageLoader = new ImageLoader();
        images = imageLoader.load(imageLocation.toURL().openStream());
        width = images[0].width;
        height = images[0].height;
        x = images[0].x;
        y = images[0].y;
        delay = images[0].delayTime;
        setBackgroundImage(new Image(getDisplay(), images[0]));
        setSize(width, height);
    }

    private int getCurrentImageNumber() {
        if(currentImageNumber.get() == images.length-1) {
            currentImageNumber.set(0);
            return 0;
        }
        return currentImageNumber.incrementAndGet();
    }

    public void animate() {
        if(images == null) return;
        animate.set(true);
        addPaintListener(paintListener);
        getDisplay().timerExec(delay * 10, new Runnable() {
            public void run() {
                redraw();
                if (animate.get())
                    getDisplay().timerExec(delay * 10, this);
            }
        });
    }

    public void inanimate() {
        removePaintListener(paintListener);
        animate.set(false);
    }

    public static void main(String[] args) throws URISyntaxException, IOException {
        Shell shell = new Shell(SWT.SHELL_TRIM|SWT.ON_TOP|SWT.CURSOR_WAIT);
        AnimatedCanvas canvas = new AnimatedCanvas(shell, SWT.TRANSPARENT|SWT.CURSOR_WAIT);
        canvas.setImageLocation(AnimatedCanvas.class.getClassLoader().getResource("images/indetermLoadingSplash.gif").toURI());
        shell.pack();
        canvas.animate();
        shell.open();
        while(!shell.isDisposed()) {
            if(!shell.getDisplay().readAndDispatch())
                shell.getDisplay().sleep();
        }
    }
}
