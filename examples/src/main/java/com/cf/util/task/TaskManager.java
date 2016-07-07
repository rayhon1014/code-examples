package com.cf.util.task;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


/**
 * Created by IntelliJ IDEA.
 * User: raymond
 * Date: 11/19/14
 * Time: 3:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class TaskManager<T> {

    private static final Logger LOGGER = Logger.getLogger(TaskManager.class);

    public List<T> parallel(List<Task<T>> tasks, int threadCount) {
        return parallel(tasks, threadCount, 0);
    }

    public List<T> parallel(List<Task<T>> tasks, int threadCount, int eachTaskTimeoutInSeconds) {

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<T> resultList = parallel(tasks, executor, eachTaskTimeoutInSeconds);
        return resultList;
    }

    public List<T> parallel(List<Task<T>> tasks, ExecutorService executor, int eachTaskTimeoutInMs) {
        List<T> resultList = new ArrayList<T>();
        try {
            List<Future<T>> futureList = executor.invokeAll(tasks, eachTaskTimeoutInMs, TimeUnit.MILLISECONDS);

            LOGGER.info("Begin future.get() matcher threads ...");
            int totalFutures = futureList.size();
            int onePercent = totalFutures / 100;
            int count = 0;

            for (int i=0; i<futureList.size(); i++) {
                Future<T> future = futureList.get(i);
                Task<T> task = tasks.get(i);
                if (onePercent > 0 && count % onePercent == 0) {
                    LOGGER.info("Pull Result ["+tasks.get(count).getTaskName()+"] : " + count / onePercent + "%, [" + count + "]  out of [" + totalFutures + "]");
                }
                try{
                    T result;
                    result = future.get();
                    LOGGER.info("Done ["+tasks.get(count).getTaskName()+"]");
                    resultList.add(result);
                    task.onComplete(result);
                }
                catch(Exception e) {
                    LOGGER.error("Get task ["+task.getTaskName()+"] future result failed. Exception message - "
                            + e.getMessage() + "; Exception class - "+e.getClass().getName());
                }
                count++;
            }
        }
        catch(Exception e)
        {
            LOGGER.error("Something bad going on in parallel method");
            throw new RuntimeException("Failed in parallel run", e);
        }
        return resultList;
    }


    public void background(Task task)
    {
        int numThreads = 1;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        LOGGER.info("Run background task:["+task.getTaskName()+"] ");
        executor.submit(task);
        executor.shutdown();
    }

}
