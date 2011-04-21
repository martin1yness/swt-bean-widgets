package com.magnetstreet.swt.extra.splash;


import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * LoadingTask
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 1/4/11
 */
public abstract class LoadingTask implements Callable<Boolean> {
    public enum STATUS{INITIATED,RUNNING,CANCELED,ERROR,COMPLETED}
    private Throwable error;

    public static interface LoadingTaskListener {
        public void notifyStatusChange(LoadingTask task, STATUS previous, STATUS current);
    }

    private Logger logger = Logger.getLogger("LoadingTask");
    protected String name, description;
    protected Long expectedTime;

    private STATUS status = LoadingTask.STATUS.INITIATED;
    private Long startTime, endTime;

    private LinkedList<LoadingTaskListener> listeners = new LinkedList<LoadingTaskListener>();

    public LoadingTask(String name, String description, Long expectedTime) {
        this.name = name;
        this.description = description;
        this.expectedTime = expectedTime;
    }

    public void addListener(LoadingTaskListener listener) { listeners.add(listener); }

    /**
     * Used to get the milliseconds it took to run this task, or how long the task has been
     * running so far.
     * @return Milliseconds the task has been running, or total time it had run (if completed)
     */
    public long getRunningTime() {
        if(startTime==null) return 0;
        if(endTime==null) return System.currentTimeMillis() - startTime;
        return endTime - startTime;
    }

    protected abstract void doAction() throws Exception;

    /**
     * Convenience method for determining if the action is taking longer than expected.
     * @return True if the task took or is taking longer than expected, false otherwise
     */
    public boolean runningLong() {
        return getRunningTime() > expectedTime;
    }

    /**
     * @return The status of the action
     */
    public STATUS getStatus() {
        return status;
    }
    private void setStatus(STATUS status) {
        this.status = status;
    }


    private void updateStatus(STATUS newStatus) {
        STATUS last = getStatus();
        setStatus(newStatus);
        for(LoadingTaskListener listener: listeners) {
            listener.notifyStatusChange(this, last, getStatus());
        }
    }

    /**
     * executes
     * @return
     * @throws Exception
     */
    public Boolean call() throws Exception {
        try {
            startTime = System.currentTimeMillis();
            updateStatus(LoadingTask.STATUS.RUNNING);
            doAction();
            updateStatus(LoadingTask.STATUS.COMPLETED);
            endTime = System.currentTimeMillis();
        } catch(Throwable t) {
            if(t instanceof InterruptedException)
                updateStatus(LoadingTask.STATUS.CANCELED);
            else
                updateStatus(LoadingTask.STATUS.ERROR);
            error = t;
            logger.log(Level.WARNING, "Unable to complete loading action.", t);
            return false;
        }
        return true;
    }

    public Throwable getError() {
        return error;
    }
}
