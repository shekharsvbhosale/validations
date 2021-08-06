package core;

import org.openqa.selenium.By;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class LocatorParser {
    protected FileInputStream inputStream;
    protected String locatorFilePath;
    protected Properties properties = new Properties();

    public LocatorParser(String filePath) throws IOException {
        this.locatorFilePath = filePath;
        inputStream = new FileInputStream(locatorFilePath);
        properties.load(inputStream);
    }

    public By getElementLocator(String locatorKey) {
        String propertyString = properties.getProperty(locatorKey);
        String locatorType = propertyString.split("!")[0];
        String locatorValue = propertyString.split("!")[1];
        By locator;

        switch (locatorType) {
            case "id":
                locator = By.id(locatorValue);
                break;
            case "tagName":
                locator = By.tagName(locatorValue);
                break;
            case "linkText":
                locator = By.linkText(locatorValue);
                break;
            case "partialLink":
                locator = By.partialLinkText(locatorValue);
                break;
            case "css":
                locator = By.cssSelector(locatorValue);
                break;
            case "name":
                locator = By.name(locatorValue);
                break;
            case "xpath":
                locator = By.xpath(locatorValue);
                break;
            default:
                locator = null;
                break;
        }
        return locator;
    }
}
