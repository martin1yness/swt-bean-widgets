package com.magnetstreet.swt.extra.splash;

import org.eclipse.swt.widgets.Display;

/**
 * DeterminateLoadingSplash
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 1/4/11
 */
public class DeterminateLoadingSplash extends AbstractLoadingSplash {

    private void applyListeners(LoadingTask...tasks) {
        for(LoadingTask task: tasks) {
            task.addListener(new LoadingTask.LoadingTaskListener() {
                @Override public void notifyStatusChange(LoadingTask task, LoadingTask.STATUS previous, LoadingTask.STATUS current) {
                    if(current==LoadingTask.STATUS.COMPLETED) {
                      /*  getDisplay().syncExec(new Runnable() {
                            public void run() {
                                progressBar.setState(progressBar.getState() + 1);
                                progressBar.setSelection(progressBar.getSelection() + 1);
                                getDisplay().readAndDispatch();
                            }
                        }); */
                    }
                }
            });
        }
    }

    @Override
    public void splash(Display display, LoadingTask... tasks) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void splashNonBlocking(Display display, LoadingTask... tasks) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
