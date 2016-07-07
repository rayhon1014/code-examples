package com.cf.util.http;


import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cf.util.http.exception.HttpServiceException;
import com.cf.util.http.model.HttpResponse;
import com.cf.util.proxy.ProxyInfo;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by ray on 6/12/15.
 */
@Component
public class DynamicHttpTemplate extends AbstractHttpTemplate{

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger
            .getLogger(DynamicHttpTemplate.class);

    @Value("${httpservice.phantomjs.enable:false}")
    private boolean phantomEnable;

    @Value("${httpservice.phantomjs.exec.path:}")
    private String phantomJsExecPath;

    @Value("${httpservice.phantomjs.debug.output.folder:}")
    private String phantomJsDebugOutputFolder;

    @Value("${httpservice.phantomjs.process.num:0}")
    private int phantomJsProcessNum;

    private String phantomJsUserAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.81 Safari/537.36";

    private List<PhantomJSDriver> phantomJsDrivers = new ArrayList<PhantomJSDriver>();
    private int lastSelectedDriver = 0;

    @PostConstruct
    public void setUp(){
        if (phantomEnable) {
            for(int i=0; i < phantomJsProcessNum; i++) {
                ProxyInfo proxyInfo = getProxyController().getProxy();
                //Create instance of PhantomJS driver
                DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
                capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomJsExecPath);
                capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "userAgent", phantomJsUserAgent);
//            capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "resourceTimeout", 1000);
//            capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "javascriptEnabled", false);
                capabilities.setCapability(CapabilityType.TAKES_SCREENSHOT, false);

                List<String> cliArgs = new ArrayList<String>();

                cliArgs.add("--proxy="+proxyInfo.getProxyIp()+":"+proxyInfo.getProxyPort());
                cliArgs.add("--proxy-auth="+proxyInfo.getProxyUsername()+":"+proxyInfo.getProxyPassword());
                cliArgs.add("--load-images=false");
                capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgs);

                PhantomJSDriver driver = new PhantomJSDriver(capabilities);
                // Set screen size
                driver.manage().window().setSize(new Dimension(2560, 1417));
//            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
//            driver.manage().timeouts().pageLoadTimeout(1, TimeUnit.SECONDS);
//            driver.manage().timeouts().setScriptTimeout(1, TimeUnit.SECONDS);

                phantomJsDrivers.add(driver);
            }
        } else {
            LOGGER.info("PhantomsJS is not enabled.");
        }
    }

    /**
     * Gets a proxy from the proxy list
     *
     * @return
     */
    public PhantomJSDriver getPhantomJsDriver() {
        synchronized (this) {
            if (phantomJsDrivers == null || phantomJsDrivers.size() == 0) {
                return null;
            }
            //simple round robin mechanism
            if (lastSelectedDriver == phantomJsDrivers.size() - 1) {
                lastSelectedDriver = 0;
            } else {
                lastSelectedDriver++;
            }

            PhantomJSDriver driver = phantomJsDrivers.get(lastSelectedDriver);
            LOGGER.info("Use ["+lastSelectedDriver+"]th proxy.");
            return driver;
        }
    }

    public HttpResponse sendRequest(String url, boolean useProxy, String method, Map<String, String> headerMap, String data) {
        return request(url, useProxy, method, headerMap, data, 0);
    }

    @Override
    public String getFinalUrl(String url, boolean useProxy) {
        try {
            //Create instance of PhantomJS driver
            PhantomJSDriver driver = getPhantomJsDriver();
            driver.get(url);
            Thread.sleep(1 * 1000L);

            String finalUrl = driver.getCurrentUrl();

            return finalUrl;
        } catch (Exception ex) {
            throw new HttpServiceException("Cannot get Final Url for [" + url + "] - error["+ex.getMessage()+"]");
        }
    }

    public HttpResponse request(String url, boolean useProxy, String method, Map<String, String> headerMap, String data, int delay){
        String content = null;
        try {
            //Create instance of PhantomJS driver
            PhantomJSDriver driver = getPhantomJsDriver();

            //Navigate to the page
            driver.get(url);

//            // Scroll to page bottom
//            driver.executeScript(" window.document.body.scrollTop = document.body.scrollHeight;");

            // Make delay
            if (delay > 0) {
                Thread.sleep(delay * 1000L);
            }

            content = driver.getPageSource();

            // Output html file for DEBUG purpose
            if (LOGGER.isDebugEnabled()) {
                String outFile = phantomJsDebugOutputFolder + "/" + new URL(url).getHost() + "/" + System.currentTimeMillis()+".html";
                FileUtils.writeStringToFile(new File(outFile), content);
                LOGGER.debug("Output html file of url ["+url+"] to ["+outFile+"].");
            }

            /**
             * Try to use JS to get html and get more control for timeout
             * */
//            driver.executeScript("");

            HttpResponse httpResponse = new HttpResponse();
            httpResponse.setStatus(200);
            httpResponse.setContent(content);

            return httpResponse;

        } catch(Exception e) {
            throw new HttpServiceException("Cannot get HTML content for [" + url + "] - error["+e.getMessage()+"]");
        }
    }
}