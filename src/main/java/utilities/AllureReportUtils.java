package utilities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.testng.ITestResult;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;

/**
 * Utility methods for attaching content and enriching Allure reports
 * from test code or listeners.
 */
public final class AllureReportUtils {

    private AllureReportUtils() {
    }

    public static void setTestCaseMetadata(ITestResult result) {
        String description = result.getMethod().getDescription();
        if (description != null && !description.isBlank()) {
            Allure.getLifecycle().updateTestCase(testCase -> testCase.setDescription(description));
        }

        String[] groups = result.getMethod().getGroups();
        if (groups != null) {
            for (String group : groups) {
                Allure.label("tag", group);
            }
        }
    }

    public static void attachFailureDetails(ITestResult result) {
        Throwable throwable = result.getThrowable();
        if (throwable == null) {
            return;
        }

        attachText("Failure Message", throwable.getMessage());
        attachText("Stack Trace", getStackTrace(throwable));
    }

    public static void attachText(String name, String content) {
        if (content == null || content.isBlank()) {
            return;
        }
        Allure.addAttachment(name, "text/plain", content, StandardCharsets.UTF_8.name());
    }

    public static void attachJson(String name, String json) {
        if (json == null || json.isBlank()) {
            return;
        }
        Allure.addAttachment(name, "application/json", json, StandardCharsets.UTF_8.name());
    }

    public static void logStep(String stepName) {
        Allure.step(stepName, Status.PASSED);
    }

    public static void logStep(String stepName, Status status) {
        Allure.step(stepName, status);
    }

    private static String getStackTrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
