package com.magnetstreet.swt.extra.splash;

import com.magnetstreet.swt.extra.AnimatedGif;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import java.io.InputStream;
import java.util.logging.Logger;

/**
 * CancelableLoadingSplash
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Oct 12, 2009
 * @since Oct 12, 2009
 */
public class CancelableLoadingSplash extends Composite {
    private static Logger log = Logger.getLogger(CancelableLoadingSplash.class.getSimpleName());
    private static String splashImgLoc = "images/indetermLoadingSplash.gif";
    public InputStream loadingGifImage = AnimatedGif.class.getClassLoader().getResourceAsStream(splashImgLoc);

    public static boolean wasCancelled = false;
    private long cancelWaitTimeout;
    protected volatile boolean cancelled = false;
    protected volatile boolean finished = false;
    
    protected AnimatedGif loadingImage;
    protected CLabel statusTextCLabel;
    protected ProgressBar progressBar;
    protected Button cancelButton;

    public static void setImage(String url) {
        splashImgLoc = url;
    }

    public static void main(String[] args) throws InterruptedException {
        CancelableLoadingSplash.splash(new Display(), 5000, new Thread[]{
                new Thread("Some task...") {
                    public void run() {
                        try { Thread.sleep(1000); } catch (InterruptedException e) {}
                    }
                },
                new Thread("Processing some other task...") {
                    public void run() {
                        try { Thread.sleep(10000); } catch (InterruptedException e) {}
                    }
                },
                new Thread("Third task in series") {
                    public void run() {
                        try { Thread.sleep(1000); } catch (InterruptedException e) {}
                    }
                },
                new Thread("Finally, we are at the last task...") {
                    public void run() {
                        try { Thread.sleep(1000); } catch (InterruptedException e) {}
                    }
                }
        });
    }

    public static void splash(final Display display, long cancelWaitTimeout, final Thread[] tasks) {
        Shell shell = new Shell(display, SWT.ON_TOP);
        shell.setText("Loading...");
        shell.setLayout(new FillLayout());
        CancelableLoadingSplash splashComposite = new CancelableLoadingSplash(shell, SWT.NONE);
        splashComposite.setCancelWaitTimeout(cancelWaitTimeout);
        Rectangle splashRect = shell.getBounds();
        Rectangle displayRect = display.getBounds();
        int x = (displayRect.width - splashRect.width) / 2;
        int y = (displayRect.height - splashRect.height) / 2;
        shell.setLocation(x, y);
        shell.pack();
        shell.open();

        splashComposite.progressBar.setMinimum(0);
        splashComposite.progressBar.setMaximum(tasks.length);
        for(int i=0; i<tasks.length; i++) {
            if(splashComposite.cancelled) break;
            splashComposite.statusTextCLabel.setText(tasks[i].getName());
            splashComposite.progressBar.setSelection(i);
            tasks[i].start();
            while(tasks[i].isAlive()) {
                display.readAndDispatch();
                if(splashComposite.cancelled) {
                    tasks[i].interrupt();
                    long start = System.currentTimeMillis();
                    while((System.currentTimeMillis() - start) < splashComposite.cancelWaitTimeout && tasks[i].isAlive()) {
                        display.readAndDispatch();
                        splashComposite.statusTextCLabel.setText("Cancelling... Hard beforeClose in " + (start + splashComposite.cancelWaitTimeout - System.currentTimeMillis() )/1000 +"s");
                    }
                    if(!tasks[i].isAlive()) break;
                    log.warning("Forced a hard beforeClose of loading task '" + tasks[i].getName() + "'");
                    tasks[i].stop();
                }
            }
        }
        shell.dispose();
    }

    public CancelableLoadingSplash(Composite c, int style) {
        super(c, style);
        setLayout(new FormLayout());

        loadingImage = new AnimatedGif(this, SWT.NONE);
        loadingImage.setGif(loadingGifImage);
        FormData loadingImageLData = new FormData(loadingImage.getSize().x, loadingImage.getSize().y);
        loadingImageLData.left = new FormAttachment(0, 100, 0);
        loadingImageLData.right = new FormAttachment(100, 100, 0);
        loadingImageLData.top = new FormAttachment(0, 100, 0);
        loadingImageLData.bottom = new FormAttachment(100, 100, -50);
        loadingImage.setLayoutData(loadingImageLData);

        statusTextCLabel = new CLabel(this, SWT.NONE);
        statusTextCLabel.setText("Loading...");
        FormData statusTextLData = new FormData(loadingImage.getSize().x-5, 20);
        statusTextLData.left = new FormAttachment(0, 100, 5);
        statusTextLData.right = new FormAttachment(100, 100, -5);
        statusTextLData.bottom = new FormAttachment(100, 100, -28);
        statusTextCLabel.setLayoutData(statusTextLData);

        progressBar = new ProgressBar(this, SWT.NONE);
        FormData progressBarLData = new FormData();
        progressBarLData.height = 25;
        progressBarLData.left = new FormAttachment(0, 100, 5);
        progressBarLData.right = new FormAttachment(100, 100, -100);
        progressBarLData.bottom= new FormAttachment(100, 100, -2);
        progressBar.setLayoutData(progressBarLData);

        cancelButton = new Button(this, SWT.PUSH);
        FormData cancelButtonLData = new FormData(90, 25);
        cancelButtonLData.right = new FormAttachment(100, 100, -5);
        cancelButtonLData.bottom = new FormAttachment(100, 100, -2);
        cancelButton.setLayoutData(cancelButtonLData);
        cancelButton.setText("Cancel");
        cancelButton.addSelectionListener(new SelectionAdapter(){
            @Override public void widgetSelected(SelectionEvent selectionEvent) {
                cancelled = true;
                wasCancelled = true;
            }
        });
    }

    public void setCancelWaitTimeout(long cancel_wait_timeout) {
        cancelWaitTimeout = cancel_wait_timeout;
    }
}
