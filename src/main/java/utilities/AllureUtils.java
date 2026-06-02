package utilities;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.testng.ITestResult;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;

/**
 * Central utility class for Allure report enrichment, attachments, and environment metadata.
 */
public final class AllureUtils {

    private static final String RESULTS_DIRECTORY_PROPERTY = "allure.results.directory";
    private static final String DEFAULT_RESULTS_DIRECTORY = "target/allure-results";
    private static final String REPORT_NAME = "Online Store API - Allure Report";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private AllureUtils() {
    }

    public static void initializeReporting() {
        Path resultsDirectory = resolveResultsDirectory();
        try {
            Files.createDirectories(resultsDirectory);
            writeEnvironmentProperties();
            writeExecutorInfo();
        } catch (IOException exception) {
            throw new RuntimeException("Failed to initialize Allure reporting", exception);
        }
    }

    public static void writeExecutorInfo() throws IOException {
        Map<String, Object> executor = new LinkedHashMap<>();
        executor.put("name", "Maven");
        executor.put("type", "maven");
        executor.put("reportName", REPORT_NAME);
        executor.put("buildName", "RestAssuredFramework_OnlineStoreApiTesting");
        executor.put("buildOrder", System.currentTimeMillis());
        executor.put("buildUrl", "local");
        executor.put("reportUrl", "target/site/allure-maven-plugin/index.html");

        Path executorFile = resolveResultsDirectory().resolve("executor.json");
        Files.writeString(executorFile, GSON.toJson(executor), StandardCharsets.UTF_8);
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

    public static void writeEnvironmentProperties() throws IOException {
        Map<String, String> environment = buildEnvironmentMap();
        Path resultsDirectory = resolveResultsDirectory();
        Path environmentFile = resultsDirectory.resolve("environment.properties");

        Files.createDirectories(resultsDirectory);
        StringBuilder content = new StringBuilder();
        content.append("# Allure Environment Properties\n");
        content.append("# Generated: ")
                .append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .append('\n');
        environment.forEach((key, value) -> content.append(key).append('=').append(value).append('\n'));
        Files.writeString(environmentFile, content.toString(), StandardCharsets.UTF_8);
    }

    private static Map<String, String> buildEnvironmentMap() {
        Map<String, String> environment = new LinkedHashMap<>();
        environment.put("Framework", "RestAssured API Automation");
        environment.put("Reporting", "Allure");
        environment.put("OS", System.getProperty("os.name"));
        environment.put("OS Version", System.getProperty("os.version"));
        environment.put("Java Version", System.getProperty("java.version"));
        environment.put("User", System.getProperty("user.name"));
        environment.put("Base URL", System.getProperty("base.url", "https://fakestoreapi.com"));
        return environment;
    }

    private static Path resolveResultsDirectory() {
        String configuredDirectory = System.getProperty(RESULTS_DIRECTORY_PROPERTY, DEFAULT_RESULTS_DIRECTORY);
        return Paths.get(configuredDirectory);
    }

    private static String getStackTrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
