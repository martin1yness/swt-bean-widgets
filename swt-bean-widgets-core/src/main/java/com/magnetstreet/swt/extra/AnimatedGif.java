package com.magnetstreet.swt.extra;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import java.io.InputStream;

/**
 * Animated Gif
 *
 * Provides a simple way to display an animated GIF within a SWT application.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Oct 12, 2009
 * @since Oct 12, 2009
 */
public class AnimatedGif extends Composite {
    private Log log = LogFactory.getLog(AnimatedGif.class);

    protected GC compositelGC;
    protected Color compositeBackground;
    protected ImageLoader loader;
    protected ImageData[] imageDataArray;
    protected Thread animateThread;
    protected Image image;
    protected final boolean useGIFBackground = false;

    protected InputStream gif;

    public static void main(String[] args) throws InterruptedException {
        InputStream gif = AnimatedGif.class.getClassLoader().getResourceAsStream("com/magnetstreet/jaguar/resources/images/indetermLoadingSplash.gif");
        Shell shell = new Shell(Display.getDefault(), SWT.NONE);
        shell.setText("Animated Gif Test...");
        shell.setSize(350, 150);
        shell.setLayout(new FillLayout());
        AnimatedGif aimg = new AnimatedGif(shell, SWT.NONE);
        aimg.setGif(gif);
        Rectangle splashRect = shell.getBounds();
        Rectangle displayRect = Display.getDefault().getBounds();
        int x = (displayRect.width - splashRect.width) / 2;
        int y = (displayRect.height - splashRect.height) / 2;
        shell.setLocation(x, y);
        shell.open();
        Thread.sleep(5000);
        shell.dispose();
    }

    public AnimatedGif(Composite composite, int i) {
        super(composite, i);
    }

    public void setGif(InputStream data) {
        gif = data;
        draw();
    }

    public void draw() {
        compositelGC = new GC(this);
        compositeBackground = getBackground();

        if (gif != null) {
            loader = new ImageLoader();
            try {
                imageDataArray = loader.load(gif);
                setSize(imageDataArray[0].width, imageDataArray[0].height); 
                if (imageDataArray.length > 1) {
                    animateThread = new Thread("Animation") {
                        public void run() {
                            /* Create an off-screen image to draw on, and fill it with the shell background. */
                            Image offScreenImage = new Image(getDisplay(), loader.logicalScreenWidth, loader.logicalScreenHeight);
                            GC offScreenImageGC = new GC(offScreenImage);
                            offScreenImageGC.setBackground(compositeBackground);
                            offScreenImageGC.fillRectangle(0, 0, loader.logicalScreenWidth, loader.logicalScreenHeight);

                            try {
                                /* Create the first image and draw it on the off-screen image. */
                                int imageDataIndex = 0;
                                ImageData imageData = imageDataArray[imageDataIndex];
                                if (image != null && !image.isDisposed()) image.dispose();
                                image = new Image(getDisplay(), imageData);
                                offScreenImageGC.drawImage(
                                    image,
                                    0,
                                    0,
                                    imageData.width,
                                    imageData.height,
                                    imageData.x,
                                    imageData.y,
                                    imageData.width,
                                    imageData.height);

                                /* Now loop through the images, creating and drawing each one
                                 * on the off-screen image before drawing it on the shell. */
                                int repeatCount = loader.repeatCount;
                                while (loader.repeatCount == 0 || repeatCount > 0) {
                                    switch (imageData.disposalMethod) {
                                    case SWT.DM_FILL_BACKGROUND:
                                        /* Fill with the background color before drawing. */
                                        Color bgColor = null;
                                        if (useGIFBackground && loader.backgroundPixel != -1) {
                                            bgColor = new Color(getDisplay(), imageData.palette.getRGB(loader.backgroundPixel));
                                        }
                                        offScreenImageGC.setBackground(bgColor != null ? bgColor : compositeBackground);
                                        offScreenImageGC.fillRectangle(imageData.x, imageData.y, imageData.width, imageData.height);
                                        if (bgColor != null) bgColor.dispose();
                                        break;
                                    case SWT.DM_FILL_PREVIOUS:
                                        /* Restore the previous image before drawing. */
                                        offScreenImageGC.drawImage(
                                            image,
                                            0,
                                            0,
                                            imageData.width,
                                            imageData.height,
                                            imageData.x,
                                            imageData.y,
                                            imageData.width,
                                            imageData.height);
                                        break;
                                    }

                                    imageDataIndex = (imageDataIndex + 1) % imageDataArray.length;
                                    imageData = imageDataArray[imageDataIndex];
                                    image.dispose();
                                    image = new Image(getDisplay(), imageData);

                                    offScreenImageGC.drawImage(
                                        image,
                                        0,
                                        0,
                                        imageData.width,
                                        imageData.height,
                                        imageData.x,
                                        imageData.y,
                                        imageData.width,
                                        imageData.height);

                                    /* Draw the off-screen image to the shell. */
                                    compositelGC.drawImage(offScreenImage, 0, 0);

                                    /* Sleep for the specified delay time (adding commonly-used slow-down fudge factors). */
                                    try {
                                        int ms = imageData.delayTime * 10;
                                        if (ms < 20) ms += 30;
                                        if (ms < 30) ms += 10;
                                        Thread.sleep(ms);
                                    } catch (InterruptedException e) {
                                    }

                                    /* If we have just drawn the last image, decrement the repeat count and start again. */
                                    if (imageDataIndex == imageDataArray.length - 1) repeatCount--;
                                }
                            } catch (SWTException ex) {
                                if(!ex.getMessage().contains("Widget is disposed"))
                                    log.error("Widget is disposed! Possible bug in class.", ex);
                            } finally {
                                if (offScreenImage != null && !offScreenImage.isDisposed()) offScreenImage.dispose();
                                if (offScreenImageGC != null && !offScreenImageGC.isDisposed()) offScreenImageGC.dispose();
                                if (image != null && !image.isDisposed()) image.dispose();
                            }
                        }
                    };
                    animateThread.setDaemon(true);
                    animateThread.start();
                }
            } catch (SWTException ex) {
                log.error("There was an error loading the GIF", ex);
            }
        }
    }
    
}
