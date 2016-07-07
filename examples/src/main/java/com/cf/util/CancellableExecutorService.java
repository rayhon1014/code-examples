package com.cf.util;
import org.apache.http.concurrent.Cancellable;

import java.util.concurrent.*;

public class CancellableExecutorService extends ThreadPoolExecutor {

    public CancellableExecutorService(int coreThreadPoolSize, int maxThreadPoolSize, long keepAliveForThreads) {
        super(coreThreadPoolSize, maxThreadPoolSize, keepAliveForThreads,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }

    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        final RunnableFuture<T> rv;
        if (callable instanceof Cancellable) {
            final Cancellable cancellableTask = (Cancellable) callable;
            rv = new FutureTask<T>(callable) {
                @Override
                public boolean cancel(boolean mayInterruptIfRunning) {
                    final boolean cancel = super.cancel(mayInterruptIfRunning);
                    if (cancel) {
                        cancellableTask.cancel();
                    }
                    return cancel;
                }
            };
        } else {
            rv = super.newTaskFor(callable);
        }
        return rv;
    }
}
