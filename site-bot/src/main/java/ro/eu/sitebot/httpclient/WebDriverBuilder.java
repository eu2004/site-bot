package ro.eu.sitebot.httpclient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import ro.eu.sitebot.dto.ProxyConnectionInfo;

/**
 * Created by emilu on 1/9/2016.
 */
class WebDriverBuilder {
    private static final Logger logger = LogManager.getLogger(WebDriverBuilder.class);
    private BrowserType browserType;
    private ProxyConnectionInfo proxyConnectionInfo;

    WebDriverBuilder() {
    }

    public WebDriverBuilder browserType(BrowserType browserType) {
        this.browserType = browserType;
        return this;
    }

    public WebDriverBuilder proxyConnectionInfo(ProxyConnectionInfo proxyConnectionInfo){
        this.proxyConnectionInfo = proxyConnectionInfo;
        return this;
    }

    public WebDriver build() {
        DesiredCapabilities dc;
        WebDriver driver;
        switch (browserType){
            case FIREFOX:
                dc = DesiredCapabilities.firefox();
                setupProxy(proxyConnectionInfo, dc);
                driver = new FirefoxDriver(dc);
                break;
            case CHROME:
                dc = DesiredCapabilities.chrome();
                setupProxy(proxyConnectionInfo, dc);
                driver = new ChromeDriver(dc);
                break;
            case IE:
                dc = DesiredCapabilities.internetExplorer();
                setupProxy(proxyConnectionInfo, dc);
                driver = new InternetExplorerDriver(dc);
                break;
            case EDGE:
                dc = DesiredCapabilities.edge();
                setupProxy(proxyConnectionInfo, dc);
                driver = new EdgeDriver(dc);
                break;
            case OPERA:
                dc = DesiredCapabilities.operaBlink();
                setupProxy(proxyConnectionInfo, dc);
                driver = new OperaDriver(dc);
                break;
            case SAFARI:
                dc = DesiredCapabilities.safari();
                setupProxy(proxyConnectionInfo, dc);
                driver = new SafariDriver(dc);
                break;
            default:
                throw new IllegalArgumentException("No browser found for " + browserType);
        }

        return driver;
    }

    private void setupProxy(ProxyConnectionInfo proxyConnectionInfo, DesiredCapabilities dc) {
        if (proxyConnectionInfo == null) {
            logger.info("No proxy will be used.");
            return;
        }

        logger.info("Using proxy " + proxyConnectionInfo + " ...");
        org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
        switch (proxyConnectionInfo.getProtocol()) {
            case HTTP: {
                proxy.setHttpProxy(proxyConnectionInfo.getHost() + ":" + proxyConnectionInfo.getPort());
                break;
            }
            case HTTPS: {
                proxy.setSslProxy(proxyConnectionInfo.getHost() + ":" + proxyConnectionInfo.getPort());
                break;
            }
            case SOCKS: {
                proxy.setSocksProxy(proxyConnectionInfo.getHost() + ":" + proxyConnectionInfo.getPort());
                proxy.setSocksPassword(proxyConnectionInfo.getPassword());
                proxy.setSocksUsername(proxyConnectionInfo.getUser());
                break;
            }
            default: {
            }
        }

        dc.setCapability(CapabilityType.PROXY, proxy);
    }
}
