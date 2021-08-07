package listeners;

import config.TestCore;
import io.qameta.allure.Attachment;
import io.qameta.allure.Issues;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.SkipException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

/*Listener class to perform operations related to test methods. This class implements ITestListener interface.
* We are implementing only required methods which are executed based on test method execution status like
* started, failed or skipped*/
public class TestReportListener extends TestCore implements ITestListener {

    private static final Logger LOGGER = LogManager.getLogger(TestReportListener.class);

    private static String getMethodNameUnderExecution(ITestResult iTestResult) {
        return iTestResult.getTestClass().getName() + "." + iTestResult.getMethod().getConstructorOrMethod().getName();
    }

    @Attachment(value = "Screenshot", type = "image/png")
    public byte[] saveScreenshotPNG(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    /*On success, screenshot is added just for additional reference in future*/
    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        LOGGER.info(getMethodNameUnderExecution(iTestResult) + " test is succeeded.");
        Object testClass = iTestResult.getInstance();
        WebDriver driver = ((TestCore) testClass).getDriver();
        saveScreenshotPNG(driver);
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        LOGGER.info(getMethodNameUnderExecution(iTestResult) + " test is failed.");
        Object testClass = iTestResult.getInstance();
        WebDriver driver = ((TestCore) testClass).getDriver();
        saveScreenshotPNG(driver);
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        Method method = iTestResult.getMethod().getConstructorOrMethod().getMethod();
        if (method != null && method.isAnnotationPresent(Issues.class)
                && Arrays.asList(method.getAnnotation(Issues.class).value()).isEmpty()) {
            ArrayList<String> issues = new ArrayList<>();
            Arrays.asList(method.getAnnotation(Issues.class).value()).forEach(issue -> issues.add(issue.value()));
            throw new SkipException(String.format("Execution Condition failure::: %s ::: Skipping this test", issues));
        }
    }

}

