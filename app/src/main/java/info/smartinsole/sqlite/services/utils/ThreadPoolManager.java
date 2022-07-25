package info.smartinsole.sqlite.services.utils;

import android.os.Process;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager {
    private static ThreadPoolManager sInstance = null;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;
    private int CORE_POOL_SIZE = 1;    // Initial pool size
    private int Maximum_POOL_SIZE =1; // Max pool size
    private long KEEP_ALIVE_TIME =1;  // Time idle thread waits before terminating
    private final ExecutorService mExecutorService; //Executor Service
    private BlockingQueue<Runnable> mTaskQueue = null; // Task Queue
    private List<Future> mRunningTaskList;

    // The class is used as a singleton
    static {
        KEEP_ALIVE_TIME_UNIT = TimeUnit.MILLISECONDS;
        sInstance = new ThreadPoolManager();
    }

    // Made constructor private to avoid the class being initiated from outside
    private ThreadPoolManager() {

        // initialize a queue for the thread pool. New tasks will be added to this queue
        mTaskQueue = new ArrayBlockingQueue<Runnable>(3000000);
        mRunningTaskList = new ArrayList<>();
        mExecutorService = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                Maximum_POOL_SIZE,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                mTaskQueue,
                new BackgroundThreadFactory());
    }

    /**
     * @return The instance of ThreadPoolManager
     */
    public static ThreadPoolManager getsInstance() {
        return sInstance;
    }

    /**
     * Add a callable to the queue, which will be executed by the next available thread in the pool
      */
    public void addCallable(Callable callable){
        Future future = mExecutorService.submit(callable);
        mRunningTaskList.add(future);
        Log.d("ThreadManager: ", "Callable Added!");
    }

    /**
     * Remove all tasks in the queue and stop all running threads
     * Notify UI thread about the cancellation
     */
    public void cancelAllTasks() {
        synchronized (this) {
            //mTaskQueue.clear();
            for (Future task : mRunningTaskList) {
                if (!task.isDone()) {
                    task.cancel(true);
                }
            }
            mRunningTaskList.clear();
        }
    }

    /*
     * A ThreadFactory implementation which create new threads for the thread pool.
     * The threads created is set to background priority, so it does not compete with the UI thread.
     */
    private static class BackgroundThreadFactory implements ThreadFactory {
        private static int sTag = 1;

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setName("CustomThread" + sTag);
            thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);

            // A exception handler is created to log the exception from threads
            thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable ex) {
                    Log.e(thread.getName(), " encountered an error: " + ex.getMessage());
                }
            });
            return thread;
        }
    }

}