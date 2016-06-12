package ro.eu.sitebot.dto;

import ro.eu.sitebot.httpclient.ConnectionProtocol;

/**
 * Created by emilu on 11/16/2015.
 */
public class ProxyConnectionInfo {
    private String host;
    private int port;
    private ConnectionProtocol protocol = ConnectionProtocol.HTTP;
    private String user;
    private String password;

    public ConnectionProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(ConnectionProtocol protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProxyConnectionInfo that = (ProxyConnectionInfo) o;

        if (port != that.port) return false;
        if (!host.equals(that.host)) return false;
        if (protocol != that.protocol) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        return !(password != null ? !password.equals(that.password) : that.password != null);

    }

    @Override
    public int hashCode() {
        int result = host.hashCode();
        result = 31 * result + port;
        result = 31 * result + protocol.hashCode();
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProxyConnectionInfo{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", protocol=" + protocol +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
