package com.cf.util.http;

import com.cf.util.http.model.HttpResponse;

import java.util.Map;

/**
 * Created by ray on 6/12/15.
 */
public interface HttpService {

    HttpResponse getContent(String url);

    HttpResponse getContent(String url, Map<String, String> headerMap);

    HttpResponse getFormattedContent(String url);

    HttpResponse getContent(String url, boolean useProxy, boolean isCacheEnabled);

    HttpResponse getContent(String url, boolean useProxy, boolean isCacheEnabled, Map<String, String> headerMap);

    HttpResponse postContent(String url, String data);

    HttpResponse postContent(String url, boolean useProxy, Map<String, String> headerMap, String data);

    HttpResponse ajaxCall(String url, boolean useProxy, String data);

    boolean downloadFile(String url, String destPath, boolean useProxy);
}
