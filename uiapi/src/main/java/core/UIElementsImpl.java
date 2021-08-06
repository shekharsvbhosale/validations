package core;

import java.nio.file.FileSystems;

public interface UIElementsImpl {
    String fileSeparator = FileSystems.getDefault().getSeparator();
    String resourcePath = System.getProperty("user.dir") + fileSeparator +
            "src" + fileSeparator + "main" + fileSeparator + "resources" + fileSeparator;
}
