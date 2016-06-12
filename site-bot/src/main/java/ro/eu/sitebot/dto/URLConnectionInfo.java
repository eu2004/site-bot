package ro.eu.sitebot.dto;

import ro.eu.sitebot.httpclient.ConnectionProtocol;

/**
 * Created by emilu on 11/16/2015.
 */
public class URLConnectionInfo {
    private String url;
    private String user;
    private String password;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ConnectionProtocol getProtocol(){
        if (url != null) {
            if (url.toUpperCase().startsWith(ConnectionProtocol.HTTP.name())) {
                return ConnectionProtocol.HTTP;
            }

            if (url.toUpperCase().startsWith(ConnectionProtocol.HTTPS.name())) {
                return ConnectionProtocol.HTTPS;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "URLConnectionInfo{" +
                "url='" + url + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
