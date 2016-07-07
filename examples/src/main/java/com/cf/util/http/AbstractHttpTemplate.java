package com.cf.util.http;

import com.cf.util.HtmlUtil;
import com.cf.util.XMLUtil;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.cf.util.http.exception.HttpServiceException;
import com.cf.util.http.model.HttpResponse;
import com.cf.util.proxy.ProxyController;
import com.cf.util.proxy.ProxyInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ray on 6/12/15.
 */
public abstract class AbstractHttpTemplate implements com.cf.util.http.HttpService {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger
            .getLogger(HttpTemplate.class);

    @Value("${httpservice.proxy.enabled:false}")
    private boolean isProxyEnabled;

    @Value("${httpservice.cache.enabled:false}")
    private boolean isCacheEnabled;

    @Value("${httpservice.cache.location:}")
    private String cacheLocation;

    @Value("${httpservice.retry.count:0}")
    private int numberOfRetry;

    @Value("${httpservice.retry.elapse.ms:500}")
    private int retryElapseInMs;

    @Value("${httpservice.connection.timeout:50000}")
    private int connectionTimeOut;

    @Value("${httpservice.read.timeout:50000}")
    private int readTimeOut;

    @Autowired
    private ProxyController proxyController;


    public HttpResponse getContent(String url)
    {
        return getContent(url, isProxyEnabled, isCacheEnabled);
    }

    public HttpResponse getFormattedContent(String url)
    {
        HttpResponse httpResponse = getContent(url);
        if (httpResponse.getStatus() == 200) {
            String content = httpResponse.getContent();
            if(!XMLUtil.validate(content) && HtmlUtil.isHtml(content)){
                content = HtmlUtil.format(content);
                httpResponse.setContent(content);
            }
        }
        return httpResponse;
    }

    public HttpResponse getContent(String url, boolean isProxyEnabled, boolean isCacheEnabled)
    {
        return getContent(url, isProxyEnabled, isCacheEnabled, null);
    }

    public HttpResponse getContent(String url, Map<String, String> header)
    {
        return getContent(url, isProxyEnabled, isCacheEnabled, header);
    }

    public HttpResponse getContent(String url, boolean isProxyEnabled, boolean isCacheEnabled, Map<String, String> headerMap) {
        HttpResponse httpResponse = new HttpResponse();
        int retries = 0;

        String fileName = url.replaceAll("[^a-zA-Z0-9]+", "");
        String filePath = cacheLocation + fileName + ".html";
        File cacheFile = new File(filePath);

        if (cacheFile.exists() && isCacheEnabled) {
            try {
                String content = Files.toString(new File(filePath), Charsets.UTF_8);
                httpResponse.setStatus(200);
                httpResponse.setContent(content);
            } catch (IOException e) {
                throw new HttpServiceException("Retrieve cache failed: cache["+filePath+"], error["+e.getMessage()+"]");
            }
        } else {
            while (retries <= numberOfRetry) {
                try {
                    httpResponse = sendRequest(url, isProxyEnabled, "GET", headerMap, null);

                    try {
                        if (isCacheEnabled) {
                            FileUtils.writeStringToFile(cacheFile, httpResponse.getContent(), "UTF-8");
                        }
                    } catch (IOException e) {
                        LOGGER.error("Write cache failed: cache["+filePath+"], error["+e.getMessage()+"]");
                    }

                    return httpResponse;
                } catch (HttpServiceException se) {
                    if (retries >= numberOfRetry) throw se;

                    retries++;
                    LOGGER.warn("Get response failed: retry["+retries+"], error["+se.getMessage()+"]");

                    try {
                        Thread.sleep(retryElapseInMs);
                        continue;
                    } catch (InterruptedException e) {
                        LOGGER.error("Error while sleeping. Exception Message - " + e.getMessage());
                    }
                }
            }
        }

        return httpResponse;
    }

    public HttpResponse postContent(String url, String data) {
        return postContent(url, false, null, data);
    }

    public HttpResponse postContent(String url, boolean useProxy, Map<String, String> headerMap, String data) {
        LOGGER.debug("Post data. url=["+url+"], useProxy=["+useProxy+"], headerMap=["+headerMap+"], data = [" + data + "].");
        HttpResponse httpResponse = null;
        int retries = 0;

        String errorMsg = "";
        while (retries <= numberOfRetry) {
            try {
                httpResponse = sendRequest(url, useProxy, "POST", headerMap, data);
                break;
            } catch (HttpServiceException se) {
                errorMsg = se.getMessage();
                LOGGER.error("Error post the  content: [" + url + "] with retry [" + retries + "]. Exception Message - " + errorMsg);
                retries++;
                try {
                    Thread.sleep(retryElapseInMs);
                    continue;
                } catch (InterruptedException e) {
                    LOGGER.error("Error while sleeping. Exception Message - " + e.getMessage());
                }
            }
        }

        if (httpResponse == null) {
            LOGGER.error("No repsonse from post. url=["+url+"], useProxy=["+useProxy+"], headerMap=["+headerMap+"], data=["+data+"].");
            throw new HttpServiceException("No repsonse from post. url=["+url+"]. Error msg - " + errorMsg);
        }

        LOGGER.debug("Get post response. data = [" + httpResponse.getContent() + "].");
        return httpResponse;
    }


    public abstract HttpResponse sendRequest(String url, boolean useProxy, String method, Map<String, String> headerMap, String data);

    public abstract String getFinalUrl(String url, boolean useProxy);


    public HttpResponse ajaxCall(String url, boolean useProxy, String data) {
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Accept", "text/html, */*; q=0.01");
        headerMap.put("Accept-Language", "en-US,en;q=0.8");
        headerMap.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        headerMap.put("Proxy-Connection", "keep-alive");
        headerMap.put("Content-Length", "19");
        headerMap.put("X-Requested-With", "XMLHttpRequest");

        return postContent(url, useProxy, headerMap, data);
    }

    public boolean downloadFile(String url, String destPath, boolean useProxy) {
        File file = new File(destPath);
        File folder = file.getParentFile();

        // Create folder
        if (!folder.exists()) {
            try {
                FileUtils.forceMkdir(folder);
            } catch (IOException ex) {
                LOGGER.error("Create folder["+folder.getPath()+"] failed. Exception - " + ex.getMessage());
                return false;
            }
        }

        Proxy proxy = Proxy.NO_PROXY;

        if (useProxy) {
            // Get proxy
            final ProxyInfo proxyInfo = proxyController.getProxy();
            proxy = proxyInfo.getProxy();

            // Set up proxy authenication
            if(StringUtils.isNotBlank(proxyInfo.getProxyUsername()) &&
                    StringUtils.isNotBlank(proxyInfo.getProxyPassword())) {
                Authenticator authenticator = new Authenticator() {
                    public PasswordAuthentication getPasswordAuthentication() {
                        return (new PasswordAuthentication(proxyInfo.getProxyUsername(),
                                proxyInfo.getProxyPassword().toCharArray()));
                    }
                };
                Authenticator.setDefault(authenticator);
            }
        }

        // Download
        try {
            URL urlObj = new URL(url);
            URLConnection conn = urlObj.openConnection(proxy);
            ReadableByteChannel rbc = Channels.newChannel(conn.getInputStream());
            FileOutputStream fos = new FileOutputStream(destPath);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
        } catch (Exception ex) {
            LOGGER.error("Download photo["+url
                    +"] with proxy["+proxy+"] failed. Exception - " + ex.getMessage());
            return false;
        }

        return true;
    }

    public ProxyController getProxyController() {
        return proxyController;
    }

    public void setProxyController(ProxyController proxyController) {
        this.proxyController = proxyController;
    }

    public boolean isProxyEnabled() {
        return isProxyEnabled;
    }

    public void setProxyEnabled(boolean proxyEnabled) {
        isProxyEnabled = proxyEnabled;
    }

    public int getNumberOfRetry() {
        return numberOfRetry;
    }

    public void setNumberOfRetry(int numberOfRetry) {
        this.numberOfRetry = numberOfRetry;
    }

    public boolean isCacheEnabled() {
        return isCacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        isCacheEnabled = cacheEnabled;
    }

    public String getCacheLocation() {
        return cacheLocation;
    }

    public void setCacheLocation(String cacheLocation) {
        this.cacheLocation = cacheLocation;
    }

    public int getRetryElapseInMs() {
        return retryElapseInMs;
    }

    public void setRetryElapseInMs(int retryElapseInMs) {
        this.retryElapseInMs = retryElapseInMs;
    }

    public int getConnectionTimeOut() {
        return connectionTimeOut;
    }

    public void setConnectionTimeOut(int connectionTimeOut) {
        this.connectionTimeOut = connectionTimeOut;
    }

    public int getReadTimeOut() {
        return readTimeOut;
    }

    public void setReadTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
    }
}
