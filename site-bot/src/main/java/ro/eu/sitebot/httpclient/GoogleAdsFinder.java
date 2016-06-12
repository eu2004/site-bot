package ro.eu.sitebot.httpclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by emilu on 1/10/2016.
 */
@Component
public class GoogleAdsFinder {
    @Value("${url.google.ads.find.expression}")
    private String googleAdsFindExpression;

    public List<String> findAdsURLs(String pageSource) {
        if (pageSource == null) {
            return Collections.emptyList();
        }
        Pattern googleAdsFindExpressionPattern = Pattern.compile(googleAdsFindExpression);

        List<String> urls = new ArrayList<String>(1);
        Matcher matcher = googleAdsFindExpressionPattern.matcher(pageSource);
        while(matcher.find()) {
            urls.add(matcher.group(1));
        }
        return urls;
    }
}
