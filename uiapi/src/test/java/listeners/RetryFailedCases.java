package listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/*Class to specify retry count for failed test cases*/
public class RetryFailedCases implements IRetryAnalyzer {
    private static final int maxTry = 1;
    private int count = 0;

    @Override
    public boolean retry(ITestResult tResult) {
        if (!tResult.isSuccess()) {
            if (count < maxTry) {
                count++;
                tResult.setStatus(ITestResult.FAILURE);
                return true;
            } else {
                tResult.setStatus(ITestResult.FAILURE);
            }
        } else {
            tResult.setStatus(ITestResult.SUCCESS);
        }
        return false;
    }
}
