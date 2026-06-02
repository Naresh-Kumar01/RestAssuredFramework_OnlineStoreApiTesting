package utilities;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Writes {@code environment.properties} into the Allure results directory
 * so the generated report displays runtime context (OS, Java, framework info).
 */
public final class AllureEnvironmentWriter {

    private static final String RESULTS_DIRECTORY_PROPERTY = "allure.results.directory";
    private static final String DEFAULT_RESULTS_DIRECTORY = "target/allure-results";

    private AllureEnvironmentWriter() {
    }

    public static void writeEnvironmentProperties() {
        Map<String, String> environment = buildEnvironmentMap();
        Path resultsDirectory = resolveResultsDirectory();
        Path environmentFile = resultsDirectory.resolve("environment.properties");

        try {
            Files.createDirectories(resultsDirectory);
            Properties properties = new Properties();
            environment.forEach(properties::setProperty);

            try (OutputStream outputStream = Files.newOutputStream(environmentFile)) {
                properties.store(outputStream, "Allure Environment Properties");
            }
        } catch (IOException exception) {
            throw new RuntimeException("Failed to write Allure environment properties", exception);
        }
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
}
