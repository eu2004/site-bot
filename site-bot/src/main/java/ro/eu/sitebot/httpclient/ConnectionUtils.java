package ro.eu.sitebot.httpclient;

import ro.eu.sitebot.dto.ProxyConnectionInfo;
import ro.eu.sitebot.dto.URLConnectionInfo;

/**
 * Created by emilu on 11/30/2015.
 */
class ConnectionUtils {

    private ConnectionUtils() {
    }

    public static boolean isProxySocksConnection(ProxyConnectionInfo proxyConnectionInfo) {
        return proxyConnectionInfo != null && ConnectionProtocol.SOCKS.equals(proxyConnectionInfo.getProtocol());
    }

    public static boolean isHttpsConnection(URLConnectionInfo urlConnectionInfo) {
        return urlConnectionInfo != null && ConnectionProtocol.HTTPS.equals(urlConnectionInfo.getProtocol());
    }

    public static boolean isProxyHttpConnection(ProxyConnectionInfo proxyConnectionInfo) {
        return proxyConnectionInfo != null && ConnectionProtocol.HTTP.equals(proxyConnectionInfo.getProtocol());
    }

    public static boolean isProxyHttpsConnection(ProxyConnectionInfo proxyConnectionInfo) {
        return proxyConnectionInfo != null && ConnectionProtocol.HTTPS.equals(proxyConnectionInfo.getProtocol());
    }
}
