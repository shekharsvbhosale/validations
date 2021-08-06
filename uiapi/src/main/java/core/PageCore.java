package core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;

public class PageCore implements UIElementsImpl {
    public static LocatorParser locatorParser;
    private static Logger LOGGER = LogManager.getLogger(PageCore.class);

    static {
        try {
            locatorParser = new LocatorParser(resourcePath + "props" + fileSeparator + "Locators.properties");
        } catch (IOException e) {
            LOGGER.error("Locator resource file not found. Terminating execution.");
            //Exit execution, since there is no possibility of performing ops on AUT without locators
            System.exit(-1);
        }
    }

    public WebDriver driver;
    public WebDriverWait driverWait;

    public PageCore(WebDriver _driver) {
        this.driver = _driver;
        driverWait = new WebDriverWait(driver, Duration.ofSeconds(2));
    }

    public static String getPropertyValue(String key) throws IOException {
        String filePath = resourcePath + "envConfig.properties";
        LoadEnvProperties loadEnvProperties = new LoadEnvProperties();
        return (String) loadEnvProperties.loadEnvPropsData(filePath)
                .get(key);
    }

    public void waitForElementToBeVisible(By locator) {
        driverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public WebElement fluentWaitWithCustomTimeout(String locatorKey, int timeout) throws TimeoutException {
        By waitElement = locatorParser.getElementLocator(locatorKey);
        Wait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeout))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(NoSuchElementException.class, TimeoutException.class);
        return fluentWait.until(driver -> driver.findElement(waitElement));
    }

    public String readRepositoryName(By locator) {
        waitForElementToBeVisible(locator);
        return driver.findElement(locator).getText();
    }

    public boolean checkIfElementIsPresent(String locator) {
        return driver.findElements(locatorParser.getElementLocator(locator)).size() > 0;
    }

    public String getTextFromCurrentElement(String locator, int timeout) {
        fluentWaitWithCustomTimeout(locator, timeout);
        return driver.findElement(locatorParser.getElementLocator(locator)).getText();
    }

    public WebElement getWebElement(String locator) {
        return fluentWaitWithCustomTimeout(locator, 3);
    }

    public String getTitleOfPage() {
        return driver.getTitle();
    }

    public String getCurrentURL() {
        return driver.getCurrentUrl();
    }
}
