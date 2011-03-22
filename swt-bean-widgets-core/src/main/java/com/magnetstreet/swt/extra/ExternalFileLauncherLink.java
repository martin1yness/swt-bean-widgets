package com.magnetstreet.swt.extra;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * ExternalFileLauncherLink
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Oct 1, 2009
 * @since Oct 1, 2009
 */
public class ExternalFileLauncherLink extends Composite {
    private Log log = LogFactory.getLog(ExternalFileLauncherLink.class);
    private String fileLocation;
    private String fileDescription;
    private String fileType;

    protected Link fileDescriptionLinkCLabel;
    protected Label fileViewerIconContainerLabel;


    public static void main(String[] args) throws FileNotFoundException, SQLException {
        Shell shell = new Shell(Display.getDefault(), SWT.SHELL_TRIM);
        ExternalFileLauncherLink inst = new ExternalFileLauncherLink(shell, SWT.NONE);
        inst.configure(ExternalFileLauncherLink.class.getClassLoader().getResource("images/indetermLoadingSplash.gif").getFile(), "indetermLoadingSplash.gif", "gif");
        inst.initialize();
        shell.setBounds(100, 100, 200, 50);
        shell.open();
        while(!shell.isDisposed()) {
            if(Display.getDefault().readAndDispatch())
                Display.getDefault().sleep();
        }
    }

    public ExternalFileLauncherLink(Composite composite, int i) {
        super(composite, i);
    }

    public void configure(String url) {
        String[] parts = url.split("[.]");
        fileType = parts[parts.length - 1];
        fileLocation = url;
        fileDescription = parts[parts.length - 2];
    }
    /**
     * Configures the external file launcher, must be called before the initialize() method.
     * @param fileLocation
     * @param fileDescription
     * @param fileType
     */
    public void configure(String fileLocation, String fileDescription, String fileType) { this.fileLocation = fileLocation; this.fileDescription = fileDescription; this.fileType = fileType; }

    /**
     * Initializes the gui so it will be displayed, constructor does nothing but create parent.
     */
    public void initialize() {
        File file = new File(fileLocation);
        if(!file.exists()) return;
        initGUI();
        applyActionListeners();
    }

    protected void initGUI() {
        setLayout(new FormLayout());

        fileViewerIconContainerLabel = new Label(this, SWT.NONE);
        fileViewerIconContainerLabel.setText("X");
        Image image = null;
        Program p = Program.findProgram(fileType);
        if(p != null) {
            ImageData data = p.getImageData();
            if(data != null) {
                image = new Image(getDisplay(), data);
                fileViewerIconContainerLabel.setImage(image);
            }
        }
        FormData fileViewerIconContainerLabelLData = new FormData();
        fileViewerIconContainerLabelLData.left = new FormAttachment(0, 1000, 0);
        fileViewerIconContainerLabelLData.top = new FormAttachment(0, 1000, 0);
        fileViewerIconContainerLabel.setLayoutData(fileViewerIconContainerLabelLData);

        fileDescriptionLinkCLabel = new Link(this, SWT.LEFT);
        fileDescriptionLinkCLabel.setText("<a>"+fileDescription+"</a>");
        FormData fileDescriptionLinkCLabelLData = new FormData();
        int tmpLeftAlign = 10;
        try {
            tmpLeftAlign = image.getBounds().width+5;
        } catch(NullPointerException npe) { }
        fileDescriptionLinkCLabelLData.left = new FormAttachment(0, 1000, tmpLeftAlign);
        fileDescriptionLinkCLabelLData.right = new FormAttachment(1000, 1000, 0);
        fileDescriptionLinkCLabelLData.top = new FormAttachment(0, 1000, 0);
        fileDescriptionLinkCLabelLData.bottom = new FormAttachment(1000, 1000, 0);
        fileDescriptionLinkCLabel.setLayoutData(fileDescriptionLinkCLabelLData);

        pack();
    }

    protected void applyActionListeners() {
        fileViewerIconContainerLabel.addMouseListener(new MouseAdapter() {
            @Override public void mouseUp(MouseEvent mouseEvent) {
                super.mouseUp(mouseEvent);
                new Thread() {
                    @Override public void run() {
                        fileViewLauncherClicked();
                    }
                }.start();
            }
        });
        fileDescriptionLinkCLabel.addMouseListener(new MouseAdapter() {
            @Override public void mouseUp(MouseEvent mouseEvent) {
                super.mouseUp(mouseEvent);
                new Thread() {
                    @Override public void run() {
                        fileViewLauncherClicked();
                    }
                }.start();
            }
        });
    }

    protected void fileViewLauncherClicked() {
//        File fp = new File(fileLocation);
//        fp.renameTo(new File(fileLocation+"."+fileType));
        BareBonesBrowserLaunch.openURL(fileLocation);
        /*
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"iexplore", fp.getAbsolutePath()+"."+fileType});
            p.waitFor();
        } catch (IOException e) {
            log.error("Unable to open linked file.", e);
        } catch (InterruptedException e) {
            log.info("Open file process iterrupted.", e);
        }
        */
    }

}

class BareBonesBrowserLaunch {
    private static Log log = LogFactory.getLog(BareBonesBrowserLaunch.class);
    static final String[] browsers = { "google-chrome", "firefox", "opera",
            "epiphany", "konqueror", "conkeror", "midori", "kazehakase", "mozilla" };
    static final String errMsg = "Error attempting to launch web browser";

    public static void openURL(String url) {
        try {  //attempt to use Desktop library from JDK 1.6+
            Class<?> d = Class.forName("java.awt.Desktop");
            d.getDeclaredMethod("browse", new Class[] {java.net.URI.class}).invoke(
                    d.getDeclaredMethod("getDesktop").invoke(null),
                    new Object[] {java.net.URI.create(url)});
            //above code mimicks:  java.awt.Desktop.getDesktop().browse()
        }
        catch (Exception ignore) {  //library not available or failed
            String osName = System.getProperty("os.name");
            try {
                if (osName.startsWith("Mac OS")) {
                    Class.forName("com.apple.eio.FileManager").getDeclaredMethod(
                            "openURL", new Class[] {String.class}).invoke(null,
                            new Object[] {url});
                }
                else if (osName.startsWith("Windows"))
                    Runtime.getRuntime().exec(
                            "rundll32 url.dll,FileProtocolHandler " + url);
                else { //assume Unix or Linux
                    String browser = null;
                    for (String b : browsers)
                        if (browser == null && Runtime.getRuntime().exec(new String[]
                                {"which", b}).getInputStream().read() != -1)
                            Runtime.getRuntime().exec(new String[] {browser = b, url});
                    if (browser == null)
                        throw new Exception(Arrays.toString(browsers));
                }
            }
            catch (Exception e) {
                log.error(errMsg+"\n"+e.toString(), e);
            }
        }
    }

}