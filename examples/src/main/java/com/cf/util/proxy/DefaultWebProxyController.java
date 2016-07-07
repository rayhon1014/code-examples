package com.cf.util.proxy;

/**
 * Created by IntelliJ IDEA.
 * User: raymond
 * Date: 9/14/12
 * Time: 12:32 PM
 * To change this template use File | Settings | File Templates.
 */


import com.cf.util.FileUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


@Component
public class DefaultWebProxyController implements com.cf.util.proxy.ProxyController {

    private List<com.cf.util.proxy.ProxyInfo> proxies = new ArrayList<com.cf.util.proxy.ProxyInfo>();
    private int lastSelectedProxy = 0;

    @Value("${httpservice.proxyList.location:}")
    protected String proxyListPath;

    /**
     * Creates an instance of the {@code DefaultProxyController}
     */
    public DefaultWebProxyController() {
    }

    @PostConstruct
    public void init() throws IOException {
        File proxyFile = new File(proxyListPath);
        List<String> lines = new ArrayList<String>();
        if(proxyFile.exists())
        {
            lines = FileUtils.readLines(new File(proxyListPath));
        }
        else
        {
            lines = FileUtil.readFileFromClassPath(proxyListPath);
        }
        for (String line : lines) {
            String[] parts = line.split(":");
            com.cf.util.proxy.ProxyInfo proxy;
            if(parts.length == 2)
            {
                proxy = new com.cf.util.proxy.ProxyInfo(parts[0], Integer.parseInt(parts[1]));
                proxies.add(proxy);
            }
            else if(parts.length == 4)
            {
                proxy = new com.cf.util.proxy.ProxyInfo(parts[0], Integer.parseInt(parts[1]), parts[2], parts[3]);
                proxies.add(proxy);
            }
        }
    }

    /**
     * Creates an instance of the {@code DefaultProxyController}
     *
     * @param proxies
     */
    public DefaultWebProxyController(Collection<? extends com.cf.util.proxy.ProxyInfo> proxies) {
        setProxies(proxies);
    }


    /**
     * Sets proxies list
     *
     * @param proxies
     */
    public void setProxies(Collection<? extends com.cf.util.proxy.ProxyInfo> proxies) {
        synchronized (this) {
            this.proxies = new LinkedList<com.cf.util.proxy.ProxyInfo>(proxies);
            this.lastSelectedProxy = 0;
        }
    }

    /**
     * Returns proxies list
     *
     * @return
     */
    public List<? extends com.cf.util.proxy.ProxyInfo> getProxies() {
        return proxies;
    }

    /**
     * Gets a proxy from the proxy list
     *
     * @return
     */
    public com.cf.util.proxy.ProxyInfo getProxy() {
        synchronized (this) {
            if (proxies == null || proxies.size() == 0) {
                return null;
            }
            //simple round robin mechanism
            if (lastSelectedProxy == proxies.size() - 1) {
                lastSelectedProxy = 0;
            } else {
                lastSelectedProxy++;
            }

            ProxyInfo proxy = proxies.get(lastSelectedProxy);
            return proxy;
        }
    }

    public int getProxyListSize(){
        return proxies.size();
    }

    public String getProxyListPath() {
        return proxyListPath;
    }

    public void setProxyListPath(String proxyListPath) {
        this.proxyListPath = proxyListPath;
    }
}
