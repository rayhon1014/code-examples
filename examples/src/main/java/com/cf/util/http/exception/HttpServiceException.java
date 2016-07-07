package com.cf.util.http.exception;

/**
 * Created by IntelliJ IDEA.
 * User: raymond
 * Date: 10/9/12
 * Time: 1:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class HttpServiceException extends RuntimeException {

    public HttpServiceException() {
    }

    public HttpServiceException(String msg) {
        super(msg);
    }
}
