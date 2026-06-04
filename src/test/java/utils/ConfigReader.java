package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private final Properties properties;
    
    // Windows aur Linux dono standard ko support karne ke liye forward slash (/) use karein
    private static final String CONFIG_FILE_PATH = "src/test/resources/config.properties";
    
    public ConfigReader() {
        properties = new Properties();
        
        // System.getProperty("user.dir") se current project directory dynamic ho jati hai
        String absolutePath = System.getProperty("user.dir") + "/" + CONFIG_FILE_PATH;
        
        try (FileInputStream fileInputStream = new FileInputStream(absolutePath)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load config.properties file from path: " + absolutePath, e);
        }
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public int getIntProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Property key '" + key + "' not found in config file.");
        }
        return Integer.parseInt(value.trim());
    }
}