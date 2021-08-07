package config;

import core.LoadEnvProperties;
import core.LocatorParser;
import core.UIElementsImpl;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import java.io.IOException;

@Epic("AUT Configuration")
@Feature("Browser configuration")
public class TestCore implements UIElementsImpl {
    private static final Logger LOGGER = LogManager.getLogger(TestCore.class);
    public static LocatorParser locatorParser;
    public static LoadEnvProperties loadEnvProperties;
    public WebDriver driver;
    static {
        try {
            locatorParser = new LocatorParser(resourcePath + "props" + fileSeparator + "Locators.properties");
            loadEnvProperties = new LoadEnvProperties();
        } catch (IOException e) {
            LOGGER.error("Locator resource file not found. Terminating execution.");
            System.exit(-1);
        }
    }

    @BeforeClass
    public void configureBrowser() throws IOException {
        String browser = getBrowser();
        if (browser.equalsIgnoreCase("chrome")) {
            LOGGER.info("Execution is started on Google Chrome");
            // Using executables is tedious task to for code maintenance and hence commented out
            System.setProperty("webdriver.chrome.driver", resourcePath + "drivers/chromedriver.exe");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("start-maximized");
            //options.addArguments("--headless");
            driver = new ChromeDriver(options);
        }
        if (browser.equalsIgnoreCase("firefox")) {
            LOGGER.info("Execution is started on Mozilla Firefox");
            System.setProperty("webdriver.gecko.driver", resourcePath + "drivers/geckodriver.exe");
            driver = new FirefoxDriver();
            driver.manage().window().maximize();
        }
        if (browser.equalsIgnoreCase("edge")) {
            LOGGER.info("Execution is started on Microsoft Edge");
            System.setProperty("webdriver.edge.driver", resourcePath + "drivers/msedgedriver.exe");
            driver = new EdgeDriver();
            driver.manage().window().maximize();
        }
    }

    /*Method to return current instance of browser*/
    public WebDriver getDriver()
    {
        return driver;
    }

    /*Method to read default value needed for configuration*/
    public static String getPropertyValue(String key) throws IOException {
        String filePath = resourcePath + "envConfig.properties";
        return (String) loadEnvProperties.loadEnvPropsData(filePath)
                .get(key);
    }

    /*This method is used mainly to get user input for browser.
     * If user do not provide input, default value will be picked up from configuration and will be returned
     * for further execution.*/
    public String getBrowser() throws IOException {
        String browser = System.getProperty("browser");
        if (browser == null || browser.isEmpty())
            browser = getPropertyValue("browser");
        return browser;
    }

    @AfterClass
    public void endExecution() {
        driver.manage().deleteAllCookies();
        driver.quit();
        LOGGER.info("Test finished.");
    }
}
