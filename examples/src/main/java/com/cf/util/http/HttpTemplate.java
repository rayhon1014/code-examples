package com.cf.util.http;

import com.cf.util.http.exception.HttpServiceException;
import com.cf.util.http.model.HttpResponse;
import com.cf.util.proxy.ProxyInfo;
import com.google.common.base.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.co.bigbeeconsultants.http.Config;
import uk.co.bigbeeconsultants.http.HttpClient;
import uk.co.bigbeeconsultants.http.header.*;
import uk.co.bigbeeconsultants.http.request.RequestBody;
import uk.co.bigbeeconsultants.http.request.RequestBody$;
import uk.co.bigbeeconsultants.http.response.Response;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.util.Map;

@Component
public class HttpTemplate extends AbstractHttpTemplate{

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger
            .getLogger(HttpTemplate.class);


    public HttpResponse sendRequest(String url, boolean useProxy, String method, Map<String, String> headerMap, String data){
        Response response = null;
        String proxyIp = "";

        Headers headers = Headers.Empty();
        if (headerMap != null) {
            for (Map.Entry<String, String> entry: headerMap.entrySet()) {
                Header h = Header$.MODULE$.apply(entry.getKey(), entry.getValue());
                headers = headers.$plus(h);
            }
        }

        try{
            Config config;
            if (useProxy) {
                final ProxyInfo proxyInfo = getProxyController().getProxy();
                proxyIp = proxyInfo.getProxyIp();

                // Set proxy username and password
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

                config = new Config(getConnectionTimeOut(), getReadTimeOut(), true, 20, true, true,
                        scala.Option.apply(proxyInfo.getUserAgent()),
                        scala.Option.apply(proxyInfo.getProxy()),
                        null, null, null, headers, Config.standardSetup() );
            } else {
                config = new Config(getConnectionTimeOut(), getReadTimeOut(), true, 20, true, true,
                        scala.Option.apply("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 Safari/537.36"),
                        scala.Option.apply(java.net.Proxy.NO_PROXY),
                        null, null, null, headers, Config.standardSetup() );
            }

            // Allow insecure SSl connection
            config = config.allowInsecureSSL();

            HttpClient httpClient = new HttpClient(config);

            // Determine http method
            if (method.equals("POST")) {
                RequestBody requestBody;
                if (StringUtils.isNotBlank(headerMap.get("Content-Type"))) {
                    MediaType mediaType = MediaType$.MODULE$.apply(headerMap.get("Content-Type"));
                    requestBody = RequestBody$.MODULE$.apply(data, mediaType);
                } else {
                    requestBody = RequestBody$.MODULE$.apply(data, MediaType.TEXT_PLAIN());
                }

                response = httpClient.post(new URL(url), scala.Option.apply(requestBody), headers);
            } else {
                response = httpClient.get(new URL(url), headers);
            }

            int responseCode = response.status().code();
            String content = getResponseString(response);

            // Wrap as HttpResponse
            HttpResponse httpResponse = new HttpResponse();
            httpResponse.setStatus(responseCode);
            httpResponse.setContent(content);
            httpResponse.setRequestUrl(response.request().url().toExternalForm());

            LOGGER.info("Get response: method[" + method + "], proxy["+proxyIp+"], statusCode["+responseCode+"], url[" + url + "].");

            return httpResponse;
        } catch(Exception e) {
            throw new HttpServiceException("Get response error: method[" + method + "], proxy["+proxyIp+"], url[" + url + "], error["+e.getMessage()+"].");
        }
    }

    @Override
    public String getFinalUrl(String url, boolean useProxy) {
        // TODO: get final redirected url base on 301 or 302
        return null;
    }

    private String getResponseString(Response response) {
        String content;

        try {
            content = response.body().asString();
            if (StringUtils.isNotBlank(content)) {
                return content;
            }
        } catch (Exception ex) {
            LOGGER.warn("Cannot decode response content. url["+response.request().url()+"]. Reason - "
                    +ex.getClass().getName()+"["+ex.getMessage()+"]");
        }

        // Attempt to guess charset
        Charset[] tryCharsets = {Charsets.ISO_8859_1};
        byte[] bytes = response.body().asBytes();
        for (Charset cs: tryCharsets) {
            try {
                ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
                content = cs.newDecoder()
                        .onMalformedInput(CodingErrorAction.REPORT)
                        .onUnmappableCharacter(CodingErrorAction.REPORT)
                        .decode(byteBuffer).toString();
                if (StringUtils.isNotBlank(content)) {
                    return content;
                }
            } catch (Exception ex) {
                LOGGER.warn("Tried charset [" + cs.displayName() + "] but failed. Reason - " + ex.getMessage());
            }
        }

        LOGGER.error("Cannot decode response.");
        return null;
    }


}
