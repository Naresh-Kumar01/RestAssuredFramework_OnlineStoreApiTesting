package listeners;

import org.testng.ITestListener;
import org.testng.ITestResult;

import utilities.AllureUtils;

/**
 * Supplemental TestNG listener for Allure metadata and failure attachments.
 * Core Allure lifecycle is handled automatically via {@code AllureTestNg} SPI registration.
 */
public class AllureTestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        AllureUtils.setTestCaseMetadata(result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        AllureUtils.attachFailureDetails(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (result.getThrowable() != null) {
            AllureUtils.attachFailureDetails(result);
        }
    }
}
