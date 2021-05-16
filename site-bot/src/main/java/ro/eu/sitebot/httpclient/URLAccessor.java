package ro.eu.sitebot.httpclient;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ro.eu.sitebot.dto.ProxyConnectionInfo;
import ro.eu.sitebot.dto.URLConnectionInfo;

/**
 * Created by emilu on 11/25/2015.
 */
@Component
public class URLAccessor {
    private static final Logger logger = LogManager.getLogger(URLAccessor.class);

    @Value("${url.page.reading.random.min.time.in.sec}")
    private int randomMinTimeInSec;

    @Value("${url.page.reading.random.max.time.in.sec}")
    private int randomMaxTimeInSec;

    @Value("${url.timeout.in.sec}")
    private int urlTimeoutInSec;

    @Value("${req.browser}")
    private String browser;

    @Value("${url.click.on.google.ads}")
    private boolean clickOnGoogleAds = false;

    @Value("${google.ads.position.px}")
    private String googleAdsPostions;

    @Value("${google.ads.html.frame}")
    private String googleAdsHtmlFrame;

    private Random timeRandomizer = new Random();

    private Random googleAdsClickRandomizer = new Random();

    private WebDriverBuilder webDriverBuilder = new WebDriverBuilder();

    public void access(final URLConnectionInfo urlConnectionInfo, ProxyConnectionInfo proxyConnectionInfo) throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, URISyntaxException {
        logger.info("Connecting to " + urlConnectionInfo);

        WebDriver driver = webDriverBuilder.browserType(BrowserType.valueOf(browser)).proxyConnectionInfo(proxyConnectionInfo).build();
        driver.manage().window().maximize();
        accessURL(driver, urlConnectionInfo.getUrl());
    }

    private void accessURL(WebDriver driver, final String url) {
        try {
            //access URL
            driver.navigate().to(url);
            waitUntilPageIsLoaded(driver, url);
            if (isPageLoadedOK(driver.getPageSource())) {
                waitOnPage();
                clickOnGoogleAds(driver);
            }
        } catch (TimeoutException te) {
            logger.error("Timeout connection error: " + te.getMessage());
        } finally {
            driver.quit();
        }
    }

    private void clickOnGoogleAds(WebDriver driver) {
        if (!clickOnGoogleAds) {
            return;
        }

        List<Pair<Integer, Integer>> googleAdsPositions = getGoogleAdsPositions();
        if (googleAdsPositions.size() == 0) {
            return;
        }
        try {
            Robot robot = new Robot();

            //close popups
            mouseClick(robot, new MutablePair<>(1259, 342));
            mouseClick(robot, new MutablePair<>(1259, 342));

            //double click on ads
            int googleAdsIndex = googleAdsClickRandomizer.nextInt(googleAdsPositions.size());
            mouseDoubleClick(robot, googleAdsPositions.get(googleAdsIndex));
            mouseDoubleClick(robot, googleAdsPositions.get(googleAdsIndex));
            mouseDoubleClick(robot, googleAdsPositions.get(googleAdsIndex));
            waitOnPage();
        } catch (AWTException e) {
        }
    }

    private static void mouseDoubleClick(Robot robot, Pair<Integer, Integer> pixel) {
        logger.info("Click at " + pixel);
        try {

            robot.mouseMove(pixel.getLeft(), pixel.getRight());

            robot.mousePress(InputEvent.BUTTON1_MASK);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
            }
            robot.mouseRelease(InputEvent.BUTTON1_MASK);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }

            robot.mousePress(InputEvent.BUTTON1_MASK);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
            }
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
        }catch (Exception ex) {
            logger.error("Error on click " + ex.getMessage(), ex);
        }
    }

    private static void mouseClick(Robot robot, Pair<Integer, Integer> pixel) {
        logger.info("Click at " + pixel);
        try {
            robot.mouseMove(pixel.getRight(), pixel.getLeft());

            robot.mousePress(InputEvent.BUTTON1_MASK);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
            }
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
        }catch (Exception ex) {
            logger.error("Error on click " + ex.getMessage(), ex);
        }
    }

    private List<Pair<Integer, Integer>> getGoogleAdsPositions() {
        String[] positions = googleAdsPostions.split(";");
        List<Pair<Integer, Integer>> adsPositions = new ArrayList<>(1);
        for(String pos : positions) {
            String[] xy = pos.split(",");
            adsPositions.add(new MutablePair<>(Integer.valueOf(xy[0]), Integer.valueOf(xy[1])));
        }
        return adsPositions;
    }

    private boolean isPageLoadedOK(String pageSource) {
        if (pageSource == null) {
            return false;
        }
        pageSource = pageSource.trim().toLowerCase();

        if(!pageSource.contains("<html")
                || !pageSource.contains("<body")
                || pageSource.contains("500 internal server error")
                || pageSource.contains("504 gateway time-out")
                || pageSource.contains("502 bad gateway")){
            //504 Gateway Time-out
            //Unable to connect
            //The site could be temporarily unavailable or too busy. Try again in a few moments
            return false;
        }

        return true;
    }

    private void waitUntilPageIsLoaded(WebDriver driver, final String url) {
        new WebDriverWait(driver, urlTimeoutInSec).until(new ExpectedCondition<Object>() {
            public Boolean apply(WebDriver d) {
                boolean result = d.getCurrentUrl().equals(url);
                logger.debug("d.getCurrentUrl() " + d.getCurrentUrl());
                if (clickOnGoogleAds){
                    try {
                        return (d.switchTo().frame(googleAdsHtmlFrame).getPageSource() != null);
                    }catch (NoSuchElementException e){
                        return false;
                    }
                }else {
                    return result;
                }
            }
        });
    }

    private void waitOnPage() {
        long randomTime = randomMaxTimeInSec > randomMinTimeInSec ? (timeRandomizer.nextInt(randomMaxTimeInSec - randomMinTimeInSec + 1) + randomMinTimeInSec) : 10000;
        logger.info("Waiting for " + randomTime + " seconds on page");
        try {
            Thread.currentThread().sleep(randomTime * 1000);
        } catch (InterruptedException e) {
        }
    }
}