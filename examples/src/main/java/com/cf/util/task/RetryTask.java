package com.cf.util.task;

import java.util.concurrent.CancellationException;

/**
 * Created by IntelliJ IDEA.
 * User: raymond
 * Date: 11/19/14
 * Time: 5:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class RetryTask<T> implements com.cf.util.task.Task<T> {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(RetryTask.class);

    private final com.cf.util.task.Task<T> _wrappedTask;

    private final int _retries;

    /**
     * Creates a new RetryTask around an existing Callable. Supplying
     * zero or a negative number for the tries parameter will allow the
     * task to retry an infinite number of times -- use with caution!
     *
     * @param task the Callable to wrap
     * @param retries the max number of retries
     */
    public RetryTask(final com.cf.util.task.Task<T> task, final int retries) {
        _wrappedTask = task;
        _retries = retries;
    }

    @Override
    public String getTaskName() {
        return _wrappedTask.getTaskName();
    }

    @Override
    public void onComplete(T result) {
        _wrappedTask.onComplete(result);
    }

    /**
     * Invokes the wrapped Callable's call method, optionally retrying
     * if an exception occurs. See class documentation for more detail.
     *
     * @return the return value of the wrapped call() method
     */
    public T call() throws Exception {
        int retriesLeft = _retries;

        while (true) {
            try {
                return _wrappedTask.call();
            } catch (final InterruptedException e) {
                // We don't attempt to retry these
                throw e;
            } catch (final CancellationException e) {
                // We don't attempt to retry these either
                throw e;
            } catch (final Exception e) {
                // Are we allowed to try again?
                if (retriesLeft <= 0)
                    throw e;

                LOGGER.warn("Caught exception, retrying[" + (_retries - retriesLeft) + "].. Error was: " + e.getMessage());

                retriesLeft--;
            }
        }
    }

}
