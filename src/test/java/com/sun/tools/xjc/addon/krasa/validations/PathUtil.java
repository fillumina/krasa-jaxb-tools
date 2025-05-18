package com.sun.tools.xjc.addon.krasa.validations;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

/**
 *
 * @author Francesco Illuminati
 */
public class PathUtil {
    private static final String PLACEHOLDER = "/placeholder.txt";

    public static String getAbsolutePathOfGeneratedTestSourcesDirectory() {
        return getAbsolutePathOfResource(PLACEHOLDER).replace(PLACEHOLDER, "") +
                "/../generated-test-sources/";
    }

    public static String getAbsolutePathOfResource(String resource) {
        URL url = CxfJavaGeneratorTestHelper.class.getResource(resource);
        try {
            String fullPath = Paths.get(url.toURI()).toFile().getAbsolutePath();
            return fullPath;
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

}
