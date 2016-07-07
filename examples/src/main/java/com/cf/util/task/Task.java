package com.cf.util.task;

import java.util.concurrent.Callable;

/**
 * Created by IntelliJ IDEA.
 * User: raymond
 * Date: 11/19/14
 * Time: 3:47 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Task<T> extends Callable<T> {

    String getTaskName();
    void onComplete(T result);

}
