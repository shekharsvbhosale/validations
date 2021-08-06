package config;

import core.PageCore;
import core.UIElementsImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.IOException;

public class TestCore implements UIElementsImpl {
    private static final Logger LOGGER = LogManager.getLogger(TestCore.class);

    public WebDriver driver;
    public boolean flagValue = false;

    @BeforeClass
    public void configureBrowser() throws IOException {
        String browser = PageCore.getPropertyValue("browser");
        if (browser.equalsIgnoreCase("chrome")) {
            LOGGER.info("Execution is started on Google Chrome");
            // Using executables is tedious task to for code maintenance and hence commented out
            System.setProperty("webdriver.chrome.driver", resourcePath + "drivers/chromedriver.exe");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("start-maximized");
            options.addArguments("--headless");
            driver = new ChromeDriver(options);
        }
        if (browser.equalsIgnoreCase("firefox")) {
            LOGGER.info("Execution is started on Google Chrome");
            System.setProperty("webdriver.gecko.driver", resourcePath + "drivers/geckdodriver.exe");
            driver = new FirefoxDriver();
        }
    }

    @AfterClass
    public void endExecution() {
        driver.manage().deleteAllCookies();
        driver.quit();
        LOGGER.info("Test finished.");
    }
}
