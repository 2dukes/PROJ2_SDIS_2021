package Threads;

import macros.Macros;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

public class ThreadPool {
    public static final int NUM_THREADS = 128;
    public static ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(NUM_THREADS);

    public static ScheduledThreadPoolExecutor getInstance() {
        return scheduledThreadPoolExecutor;
    }
}
