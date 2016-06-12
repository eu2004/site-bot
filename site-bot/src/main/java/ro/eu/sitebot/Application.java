package ro.eu.sitebot;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ro.eu.sitebot.dto.ProxyConnectionInfo;
import ro.eu.sitebot.dto.URLConnectionInfo;
import ro.eu.sitebot.httpclient.URLAccessor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by emilu on 11/16/2015.
 */
@Component
class Application {
    private static final Logger logger = Logger.getLogger(Application.class);

    @Autowired
    private ProxyConnectionInfoLoader proxyConnectionInfoLoader;

    @Autowired
    private URLConnectionInfoLoader urlConnectionInfoLoader;

    @Autowired
    private URLAccessor urlAccessor;

    private Random napTimeRandomizer = new Random();

    private Random proxyIndexRandomizer = new Random();

    @Value("${be.nice.to.url.min.wait.in.sec}")
    private int beNiceToURLMin;
    @Value("${be.nice.to.url.max.wait.in.sec}")
    private int beNiceToURLMax;
    @Value("${connect.without.proxy}")
    private boolean connectWithoutProxy;

    @Value("${webdriver.chrome.driver}")
    private String webdriverChromeDriver;
    @Value("${webdriver.ie.driver}")
    private String webdriverIeDriver;
    @Value("${webdriver.edge.driver}")
    private String webdriverEdgeDriver;
    @Value("${webdriver.opera.driver}")
    private String webdriverOperaDriver;

    public void start() {
        logger.debug(proxyConnectionInfoLoader.getProxiesSet());
        logger.debug(urlConnectionInfoLoader.getURLsList());
        logger.debug("setup system properties");
        setSystemProperties();

        List<Pair<URLConnectionInfo, ProxyConnectionInfo>> randomUrlsAndProxiesPairs = new ArrayList<>(1);
        for(URLConnectionInfo urlConnectionInfo : urlConnectionInfoLoader.getURLsList()) {
            //first hit without proxy
            if (connectWithoutProxy) {
                randomUrlsAndProxiesPairs.add(new MutablePair(urlConnectionInfo, null));
            }

            //let's go through all proxies
            for(ProxyConnectionInfo proxyConnectionInfo : getProxiesInRandomOrder()){
                randomUrlsAndProxiesPairs.add(new MutablePair(urlConnectionInfo, proxyConnectionInfo));
            }
        }

        randomUrlsAndProxiesPairs = getElementsInRandomOrder(randomUrlsAndProxiesPairs);
        for (Pair<URLConnectionInfo, ProxyConnectionInfo> randomUrlsAndProxyPair : randomUrlsAndProxiesPairs) {
            accessURL(randomUrlsAndProxyPair.getLeft(), randomUrlsAndProxyPair.getRight());
        }
    }

    private void setSystemProperties() {
        System.setProperty("webdriver.chrome.driver", webdriverChromeDriver);
        System.setProperty("webdriver.ie.driver", webdriverIeDriver);
        System.setProperty("webdriver.edge.driver", webdriverEdgeDriver);
        System.setProperty("webdriver.opera.driver", webdriverOperaDriver);
    }

    private List<ProxyConnectionInfo> getProxiesInRandomOrder() {
        List<ProxyConnectionInfo> randomlyOrderedProxiesList = new ArrayList<ProxyConnectionInfo>(proxyConnectionInfoLoader.getProxiesSet());
        int[] positions = new int[randomlyOrderedProxiesList.size()];
        Set<Integer> generatedValues = new HashSet<Integer>(1);
        for(int i = 0; i < positions.length; i++) {
            positions[i] = generateRandomIndex(positions.length, i, generatedValues);
        }

        for(int i = 0; i < positions.length; i++) {
            ProxyConnectionInfo proxyConnectionInfo = randomlyOrderedProxiesList.get(i);
            randomlyOrderedProxiesList.set(i, randomlyOrderedProxiesList.get(positions[i]));
            randomlyOrderedProxiesList.set(positions[i], proxyConnectionInfo);
        }
        return randomlyOrderedProxiesList;
    }

    private List getElementsInRandomOrder(Collection elements) {
        List randomlyOrderedElementsList = new ArrayList(elements);
        int[] positions = new int[randomlyOrderedElementsList.size()];
        Set<Integer> generatedValues = new HashSet<Integer>(1);
        for(int i = 0; i < positions.length; i++) {
            positions[i] = generateRandomIndex(positions.length, i, generatedValues);
        }

        for(int i = 0; i < positions.length; i++) {
            Object proxyConnectionInfo = randomlyOrderedElementsList.get(i);
            randomlyOrderedElementsList.set(i, randomlyOrderedElementsList.get(positions[i]));
            randomlyOrderedElementsList.set(positions[i], proxyConnectionInfo);
        }
        return randomlyOrderedElementsList;
    }

    private int generateRandomIndex(final int maxLength, final int currentIndex, Set<Integer> generatedValues) {
        if(maxLength == 1) {
            return 0;
        }

        int randomValue = proxyIndexRandomizer.nextInt(maxLength);
        while (randomValue == currentIndex || generatedValues.contains(randomValue)) {
            randomValue = proxyIndexRandomizer.nextInt(maxLength);
        }
        generatedValues.add(randomValue);
        return randomValue;
    }

    /**
     * Stops the current thread for a random period of time (e.g. between 10 and 3 minutes)
     */
    private void holdYourHorsesForAWhile(){
        if (beNiceToURLMax < 1) {
            return;
        }

        long timeInMs = 1000 * napTimeRandomizer.nextInt(beNiceToURLMax - beNiceToURLMin + 1) + beNiceToURLMin;
        logger.info("Waiting for " + timeInMs/1000 + " sec ... ");
        try {
            Thread.currentThread().sleep(timeInMs);
        } catch (InterruptedException e) {
        }
    }

    private void accessURL(URLConnectionInfo urlConnectionInfo, ProxyConnectionInfo proxyConnectionInfo) {
        try {
            urlAccessor.access(urlConnectionInfo, proxyConnectionInfo);
            holdYourHorsesForAWhile();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        } catch (KeyStoreException e) {
            logger.error(e.getMessage(), e);
        } catch (KeyManagementException e) {
            logger.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            logger.error(e.getMessage(), e);
        }
    }
}