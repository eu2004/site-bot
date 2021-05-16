package ro.eu.sitebot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by emilu on 11/16/2015.
 */
public class ApplicationLauncher {
    private static final Logger logger = LogManager.getLogger(ApplicationLauncher.class);
    private static final String VERSION = "4";

    public static void main(String[] args){
        logger.info("Start application version: " + VERSION);
        ApplicationContext appContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        launch(appContext);
        logger.info("Stop application version: " + VERSION);
    }

    private static void launch(ApplicationContext appContext) {
        try{
            appContext.getBean(Application.class).start();
        }catch (Exception ex) {
            logger.error("Error running site bot application " + ": " + ex.getMessage(), ex);
        }
    }
}