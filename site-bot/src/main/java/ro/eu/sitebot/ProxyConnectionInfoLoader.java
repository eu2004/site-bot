package ro.eu.sitebot;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.core.io.Resource;
import ro.eu.sitebot.dto.ProxyConnectionInfo;
import ro.eu.sitebot.httpclient.ConnectionProtocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by emilu on 11/16/2015.
 */
@Component
class ProxyConnectionInfoLoader implements ApplicationContextAware{
    private static final Logger logger = LogManager.getLogger(ProxyConnectionInfoLoader.class);
    private Set<ProxyConnectionInfo> proxiesSet;
    private ApplicationContext applicationContext;

    @Value("${proxies.file}")
    private String proxiesFile;

    public Set<ProxyConnectionInfo> getProxiesSet() {
        if (proxiesSet == null) {
            try {
                loadProxiesSet();
            } catch (IOException e) {
                logger.error("Error loading proxies list " + e.getMessage(), e);
            }
        }

        return proxiesSet;
    }

    private void loadProxiesSet() throws IOException {
        if (proxiesSet == null) {
            synchronized (this){
                if (proxiesSet != null) {
                    return;
                }
                proxiesSet = new LinkedHashSet<ProxyConnectionInfo>(1);
                Resource resource = applicationContext.getResource("classpath:" + proxiesFile);
                loadProxiesSet(resource, proxiesSet);
            }
        }
    }

    private void loadProxiesSet(Resource resource, Set<ProxyConnectionInfo> proxiesList) throws IOException {
        InputStream inputStream = null;
        try{
            inputStream = resource.getInputStream();
            CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream));
            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                if (nextLine != null && nextLine.length > 0) {
                    ProxyConnectionInfo proxyConnectionInfo = new ProxyConnectionInfo();
                    proxyConnectionInfo.setHost(nextLine[0]);
                    if (nextLine.length > 1) {
                        proxyConnectionInfo.setPort(Integer.parseInt(nextLine[1]));
                    }
                    if (nextLine.length > 2) {
                        proxyConnectionInfo.setUser(nextLine[2]);
                    }
                    if (nextLine.length > 3) {
                        proxyConnectionInfo.setPassword(nextLine[3]);
                    }
                    if (nextLine.length > 4) {
                        proxyConnectionInfo.setProtocol(ConnectionProtocol.valueOf(nextLine[4].toUpperCase()));
                    }
                    proxiesList.add(proxyConnectionInfo);
                }
            }
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.warn("Error closing dto stream " + e.getMessage(), e);
                }
            }
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
