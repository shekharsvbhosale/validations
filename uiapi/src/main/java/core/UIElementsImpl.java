package core;

import java.nio.file.FileSystems;

/*Provision to contain common elements for all classes which implement this interface*/
public interface UIElementsImpl {
    String fileSeparator = FileSystems.getDefault().getSeparator();
    String resourcePath = System.getProperty("user.dir") + fileSeparator +
            "src" + fileSeparator + "main" + fileSeparator + "resources" + fileSeparator;
}
