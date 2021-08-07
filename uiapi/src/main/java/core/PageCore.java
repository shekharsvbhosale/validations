package core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.IOException;
import java.time.Duration;

/*This class is backbone of Test Execution. It provides abstract layer of common provisions to perform UI operations
* for all UI pages.*/

public class PageCore implements UIElementsImpl {
    public static LocatorParser locatorParser;
    private static final Logger LOGGER = LogManager.getLogger(PageCore.class);
    public static LoadEnvProperties loadEnvProperties;

    //Load environment properties at first
    static {
        try {
            locatorParser = new LocatorParser(resourcePath + "props" + fileSeparator + "Locators.properties");
            loadEnvProperties = new LoadEnvProperties();
        } catch (IOException e) {
            LOGGER.error("Locator resource file not found. Terminating execution.");
            //Exit execution, since there is no possibility of performing ops on AUT without locators
            System.exit(-1);
        }
    }

    public WebDriver driver;
    public WebDriverWait driverWait;

    //Constructor to pass on current driver instance to next method/class
    public PageCore(WebDriver _driver) {
        this.driver = _driver;
        driverWait = new WebDriverWait(driver, Duration.ofSeconds(2));
    }

    //Method to retrieve the value for provided key
    public static String getPropertyValue(String key) throws IOException {
        String filePath = resourcePath + "envConfig.properties";
        return (String) loadEnvProperties.loadEnvPropsData(filePath)
                .get(key);
    }

    //Method to wait for page elements
    public WebElement fluentWaitWithCustomTimeout(String locatorKey, int timeout) throws TimeoutException {
        By waitElement = locatorParser.getElementLocator(locatorKey);
        Wait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeout))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(NoSuchElementException.class, TimeoutException.class);
        return fluentWait.until(driver -> driver.findElement(waitElement));
    }

    public String getTitleOfPage() {
        return driver.getTitle();
    }

    public String getCurrentURL() {
        return driver.getCurrentUrl();
    }


}
