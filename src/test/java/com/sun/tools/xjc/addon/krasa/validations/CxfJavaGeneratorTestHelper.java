package com.sun.tools.xjc.addon.krasa.validations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Assert;

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
        return readFile(generatedFile(filename));
    }

    /**
     * Asserts that the given file, relative to the output directory, has been generated.
     */
    public CxfJavaGeneratorTestHelper assertGeneratedFileExists(String filename) {
        Path path = generatedFile(filename);
        Assert.assertTrue("expected the file to be generated: " + path, Files.exists(path));
        return this;
    }

    /**
     * Asserts that the given file, relative to the output directory, has not been generated.
     */
    public CxfJavaGeneratorTestHelper assertGeneratedFileAbsent(String filename) {
        Path path = generatedFile(filename);
        Assert.assertFalse("expected the file not to be generated: " + path, Files.exists(path));
        return this;
    }

    private Path generatedFile(String filename) {
        return Paths.get(PathUtil.getAbsolutePathOfGeneratedTestSourcesDirectory() +
                getOutputDir() + "/" + filename);
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
