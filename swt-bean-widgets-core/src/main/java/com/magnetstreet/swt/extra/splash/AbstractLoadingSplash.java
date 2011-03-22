package com.magnetstreet.swt.extra.splash;

import org.eclipse.swt.widgets.Display;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AbstractLoadingSplash
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 1/5/11
 */
public abstract class AbstractLoadingSplash {
    protected ExecutorService singleThreadExecutorService = Executors.newSingleThreadExecutor();
    protected LinkedList<Listener> listeners = new LinkedList<Listener>();
    protected static AbstractSplash splash;

    public static interface Listener {
        public void handleError(LoadingTask task);
        public void handleSuccess();
        public void handleCancel();
    }

    /**
     * Called by the consuming class to display the implementing classes splash screen
     * operation sequence. Likely leads with a call to the protected showSplash() method
     * and ends with a call to the hideSplash() method.
     * @param tasks The tasks to run before returning control back to the calling method
     */
    public abstract void splash(Display display, LoadingTask...tasks);

    /**
     * Performs the splash operation without stopping programing control flow.
     * @param tasks The tasks to run before removing splash
     */
    public abstract void splashNonBlocking(Display display, LoadingTask...tasks);

    public void addListener(Listener listener) { listeners.add(listener); }

    public void cancelSplash() {
        singleThreadExecutorService.shutdownNow();
        for(Listener listener: listeners)
            listener.handleCancel();
    }

    /**
     * Uses a single thread to execute all tasks in order they appear in the array.
     * @param tasks The tasks to execute
     * @return A list of futures that could be completed, they return booleans true
     *         upon successful completion of the future and false otherwise. Each task
     *         also maintains a special state ENUM.
     */
    protected Future<Boolean>[] executeTasksSequentially(LoadingTask...tasks) {
        Future<Boolean>[] taskFutures = new Future[tasks.length];
        for(int i=0; i<tasks.length; i++) {
            taskFutures[i] = singleThreadExecutorService.submit(tasks[i]);
        }

        return taskFutures;
    }

    /**
     * Optional convenience method that applies a simple listener to the given loading tasks so that
     * when they are all completed, the splash can be removed from the display and cleaned up.
     *
     * Upon a task error state the abstract handleError(Throwable t) method is invoked
     *
     * Upon all tasks being in a completed state the handleCompletion() method is invoked
     *
     * Cancelled tasks are not handled explicitly by the default listener, if using the default
     * interrupt method upon cancel events, no action is required.
     * @param tasks
     */
    protected void applyDefaultCloseListener(final LoadingTask...tasks) {
        for(LoadingTask task: tasks) {
            task.addListener(new LoadingTask.LoadingTaskListener() {
                private AtomicInteger finished = new AtomicInteger(0);
                @Override public void notifyStatusChange(LoadingTask task, LoadingTask.STATUS previous, LoadingTask.STATUS current) {
                    switch(current) {
                        case COMPLETED:
                            finished.incrementAndGet();
                            break;
                        case ERROR:
                            for(Listener listener: listeners)
                                listener.handleError(task);
                    }
                    if(finished.get()==tasks.length) {
                        for(Listener listener: listeners)
                            listener.handleSuccess();
                    }
                }
            });
        }
    }
}
