package ro.eu.sitebot;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import ro.eu.sitebot.dto.URLConnectionInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by emilu on 11/16/2015.
 */
@Component
class URLConnectionInfoLoader implements ApplicationContextAware{
    private static final Logger logger = Logger.getLogger(URLConnectionInfoLoader.class);
    private ApplicationContext applicationContext;
    private List<URLConnectionInfo> urlsList;

    @Value("${urls.file}")
    private String urlsFile;

    public List<URLConnectionInfo> getURLsList() {
        if (urlsList == null) {
            try {
                loadURLsList();
            } catch (IOException e) {
                logger.error("Error loading urls " + e.getMessage(), e);
            }
        }
        return urlsList;
    }

    private void loadURLsList() throws IOException {
        if (urlsList == null) {
            synchronized (this){
                if (urlsList != null) {
                    return;
                }
                urlsList = new ArrayList<URLConnectionInfo>(1);
                Resource resource = applicationContext.getResource("classpath:" + urlsFile);
                loadURLsList(resource, urlsList);
            }
        }
    }

    private void loadURLsList(Resource resource, List<URLConnectionInfo> urlsList) throws IOException {
        InputStream inputStream = null;
        try{
            inputStream = resource.getInputStream();
            CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream));
            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                if (nextLine != null && nextLine.length > 0) {
                    URLConnectionInfo urlConnectionInfo = new URLConnectionInfo();
                    urlConnectionInfo.setUrl(nextLine[0]);
                    if (nextLine.length > 1) {
                        urlConnectionInfo.setUser(nextLine[1]);
                    }
                    if (nextLine.length > 2) {
                        urlConnectionInfo.setPassword(nextLine[2]);
                    }
                    urlsList.add(urlConnectionInfo);
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