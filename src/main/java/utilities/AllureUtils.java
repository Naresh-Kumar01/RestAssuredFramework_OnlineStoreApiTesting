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

public final class AllureUtils {

    private static final String RESULTS_DIRECTORY_PROPERTY = "allure.results.directory";
    private static final String DEFAULT_RESULTS_DIRECTORY = "target/allure-results";
    private static final String REPORT_NAME = "Online Store API - Allure Report";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private AllureUtils() {}

    public static void initializeReporting() {
        Path resultsDirectory = resolveResultsDirectory();
        try {
            Files.createDirectories(resultsDirectory);
            writeEnvironmentProperties();
            writeExecutorInfo();
        } catch (IOException e) {
            // ✅ Log warning instead of crashing the entire suite
            System.err.println("[AllureUtils] Warning: Could not initialize reporting: " + e.getMessage());
        }
    }

    public static void writeExecutorInfo() throws IOException {
        Map<String, Object> executor = new LinkedHashMap<>();
        executor.put("name", "Maven");
        executor.put("type", "maven");
        executor.put("reportName", REPORT_NAME);
        executor.put("buildName", "RestAssuredFramework_OnlineStoreApiTesting");
        // ✅ Fix: Allure expects Integer not Long for buildOrder
        executor.put("buildOrder", (int)(System.currentTimeMillis() % Integer.MAX_VALUE));
        executor.put("buildUrl", System.getProperty("build.url", "local"));
        executor.put("reportUrl", "target/site/allure-maven-plugin/index.html");

        Path executorFile = resolveResultsDirectory().resolve("executor.json");
        Files.writeString(executorFile, GSON.toJson(executor), StandardCharsets.UTF_8);
    }

    public static void setTestCaseMetadata(ITestResult result) {
        String description = result.getMethod().getDescription();
        if (description != null && !description.isBlank()) {
            // ✅ Safe null check before updating lifecycle
            Allure.getLifecycle().getCurrentTestCase().ifPresent(uuid ->
                Allure.getLifecycle().updateTestCase(uuid, 
                    testCase -> testCase.setDescription(description))
            );
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
        if (throwable == null) return;

        String message = throwable.getMessage();
        // ✅ Fix: use correct 3-arg overload (name, type, content)
        if (message != null && !message.isBlank()) {
            Allure.addAttachment("Failure Message", "text/plain", message);
        }
        Allure.addAttachment("Stack Trace", "text/plain", getStackTrace(throwable));
    }

    public static void attachText(String name, String content) {
        if (content == null || content.isBlank()) return;
        // ✅ Fix: correct 3-arg overload
        Allure.addAttachment(name, "text/plain", content);
    }

    public static void attachJson(String name, String json) {
        if (json == null || json.isBlank()) return;
        // ✅ Fix: correct 3-arg overload
        Allure.addAttachment(name, "application/json", json);
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
        Files.createDirectories(resultsDirectory);

        Path environmentFile = resultsDirectory.resolve("environment.properties");
        StringBuilder content = new StringBuilder();
        content.append("# Allure Environment Properties\n");
        content.append("# Generated: ")
               .append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
               .append('\n');
        environment.forEach((key, value) ->
            content.append(key).append('=').append(value).append('\n')
        );
        Files.writeString(environmentFile, content.toString(), StandardCharsets.UTF_8);
    }

    private static Map<String, String> buildEnvironmentMap() {
        Map<String, String> environment = new LinkedHashMap<>();
        environment.put("Framework", "RestAssured API Automation");
        environment.put("Reporting", "Allure");
        environment.put("OS", System.getProperty("os.name", "Unknown"));
        environment.put("OS_Version", System.getProperty("os.version", "Unknown"));
        environment.put("Java_Version", System.getProperty("java.version", "Unknown"));
        environment.put("User", System.getProperty("user.name", "Unknown"));
        // ✅ Fix: spaces in keys break environment.properties parsing
        environment.put("Base_URL", System.getProperty("base.url", "https://fakestoreapi.com"));
        return environment;
    }

    private static Path resolveResultsDirectory() {
        String dir = System.getProperty(RESULTS_DIRECTORY_PROPERTY, DEFAULT_RESULTS_DIRECTORY);
        return Paths.get(dir).toAbsolutePath(); // ✅ Fix: use absolute path to avoid resolution issues
    }

    private static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}