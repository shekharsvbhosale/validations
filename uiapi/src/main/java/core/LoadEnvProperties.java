package core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.Properties;

/*This class facilitates the environment configuration handling.
* Before starting test execution, properties are loaded based on which execution proceeds*/

public class LoadEnvProperties{

    private static final Logger LOGGER = LogManager.getLogger(LoadEnvProperties.class);
    String fileSeparator = FileSystems.getDefault().getSeparator();

    public Properties loadEnvPropsData(String filePath) throws IOException {
        Properties properties = new Properties();

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LOGGER.error("Unable to find environment config file.");
        }
        properties.load(inputStream);
        return properties;
    }
}
