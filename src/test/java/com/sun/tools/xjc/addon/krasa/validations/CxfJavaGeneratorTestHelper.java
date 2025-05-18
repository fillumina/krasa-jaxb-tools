package com.sun.tools.xjc.addon.krasa.validations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Use the Cxf to generate java classes and expose some test helper methods
 *
 * @author Francesco Illuminati
 */
public class CxfJavaGeneratorTestHelper extends CxfJavaGenerator {

    private final ValidationsAnnotation validationsAnnotation;

    public CxfJavaGeneratorTestHelper(String[] args, String[] xjcArgs, String outputDir) {
        super(args, xjcArgs, outputDir);
        this.validationsAnnotation = extractValidationsAnnotation(xjcArgs);
        execute();
    }

    public ArtifactTester<CxfJavaGeneratorTestHelper> withGeneratedInterface(String filename) {
        List<String> lines = readLines(filename);
        return new ArtifactTester<>(filename, lines, validationsAnnotation, this);
    }

    private List<String> readLines(String filename) {
        String absoluteName = PathUtil.getAbsolutePathOfGeneratedTestSourcesDirectory() +
                getOutputDir() + "/" + filename;
        Path path = Paths.get(absoluteName);
        List<String> lines = readFile(path);
        return lines;
    }

    private List<String> readFile(Path path) {
        try {
            return Files.readAllLines(path);
        } catch (IOException ex) {
            throw new AssertionError("error loading file " + path, ex);
        }
    }

    private ValidationsAnnotation extractValidationsAnnotation(String[] xjcArgs) {
        for (String arg : xjcArgs) {
            if (arg.startsWith(ValidationsArgument.validationAnnotations.fullOptionName())) {
                int idx = arg.indexOf("=");
                if (idx != -1) {
                    String validationName = arg.substring(idx + 1);
                    return ValidationsAnnotation.valueOf(validationName);
                }
            }
        }
        return ValidationsAnnotation.JAVAX; // default?
    }

}
