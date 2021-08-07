package listeners;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/*This class is used to facilitate the program execution for failed cases for provided tries specified in
 * RetryFailedCases class. This is important since there are possibilities of a test case could fail
 * because of network glitch or timeout or some other problem.*/
public class RetryAnnotationTransformer implements IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(RetryFailedCases.class);
    }
}
