package com.cf.util.proxy;

import com.cf.util.HttpRequestSimulator;

import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * Created by IntelliJ IDEA.
 * User: raymond
 * Date: 11/7/12
 * Time: 10:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProxyInfo {

    private String proxyIp;
    private int proxyPort;
    private String proxyUsername;
    private String proxyPassword;
    private Proxy proxy;
    private String userAgent;
    private long speed; // response time on milliseconds

    public ProxyInfo(String proxyIp, int proxyPort)
    {
        this.proxyIp = proxyIp;
        this.proxyPort = proxyPort;
        this.userAgent = HttpRequestSimulator.getUserAgent();
        proxy =  new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyIp, proxyPort));
    }

    public ProxyInfo(String proxyIp, int proxyPort, String proxyUsername, String proxyPassword)
    {
        this.proxyIp = proxyIp;
        this.proxyPort = proxyPort;
        this.proxyUsername = proxyUsername;
        this.proxyPassword = proxyPassword;
        this.userAgent = HttpRequestSimulator.getUserAgent();
        proxy =  new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyIp, proxyPort));
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public String getProxyIp() {
        return proxyIp;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public String getProxyUsername() {
        return proxyUsername;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProxyInfo proxyInfo = (ProxyInfo) o;

        if (proxyPort != proxyInfo.proxyPort) return false;
        if (!proxyIp.equals(proxyInfo.proxyIp)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = proxyIp.hashCode();
        result = 31 * result + proxyPort;
        return result;
    }
}
