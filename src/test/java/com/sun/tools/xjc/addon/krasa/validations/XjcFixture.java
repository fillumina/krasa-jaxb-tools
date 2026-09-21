package com.sun.tools.xjc.addon.krasa.validations;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * One fixture of the annotation tests: its schema, the classes generated from it, the expected
 * annotations, and the artefacts a test inspects.
 *
 * <p>
 * Generation is driven by {@link XjcRunner}, and the generated code is compared with the expected
 * file by {@link #checkTrace()}, which returns every finding instead of throwing the first one so a
 * caller can report them all. Every fixture test goes through this class.
 *
 * @author Francesco Illuminati
 */
class XjcFixture {

    private final String testName;
    private final String folderName;
    private final String namespace;
    private final boolean separateAnnotation;
    private ValidationsAnnotation validationAnnotation;

    /**
     * @param testName the test class name, which names the file holding the expected annotations
     * @param folderName the folder name containing the xsd and the -annotation.txt files
     * @param namespace the namespace used in the xsd
     * @param separateAnnotation if there are different -annotation.txt files for each annotation
     *                           (javax or jakarta).
     */
    XjcFixture(String testName, String folderName, String namespace, boolean separateAnnotation) {
        this.testName = testName;
        this.folderName = folderName;
        this.namespace = namespace;
        this.separateAnnotation = separateAnnotation;
    }

    /**
     * Sets the library the next generation runs for, before its arguments are built: an argument
     * list holds the library name, so the library has to be known by the time it is asked for.
     *
     * @param annotation the library to generate for, see {@link #getAnnotation()}
     */
    void setAnnotation(ValidationsAnnotation annotation) {
        this.validationAnnotation = annotation;
    }

    /**
     * Generates the classes for the library {@link #setAnnotation} has just set.
     *
     * @param args the plugin's command line arguments
     * @param bindingDirectory the bindings, or {@code null} when the fixture needs none
     */
    void generate(List<String> args, File bindingDirectory) throws Exception {
        new XjcRunner(getSchemaDirectory(), getGeneratedDirectory(), args, bindingDirectory).run();
    }

    /**
     * Compares what has been generated with the expected annotations.
     *
     * @return every finding, one per namespace, so that one mismatch does not hide the next
     */
    List<Throwable> checkTrace() {
        List<Throwable> findings = new ArrayList<>();
        String[] nsArray = getNamespace().split(",");
        for (String ns : nsArray) {
            try {
                String annotatonFilename = getAnnotationFileName(ns);
                Path filename = Paths.get(getGeneratedDirectory().getAbsolutePath() +
                        File.separator + annotatonFilename);

                writeAllElementsTo(ns, filename);

                checkAllAnnotations(filename, annotatonFilename);
            } catch (Throwable throwable) {
                findings.add(throwable);
            }
        }
        return findings;
    }

    File getGeneratedDirectory() {
        return new File(getBaseDir(), "target/generated-sources/" + folderName);
    }

    File getSchemaDirectory() {
        return new File(getBaseDir(), "src/test/resources/" + folderName);
    }

    /** @return the evaluated annotation. */
    ValidationsAnnotation getAnnotation() {
        return validationAnnotation;
    }

    /** @return comma separated values or a single one */
    String getNamespace() {
        return namespace;
    }

    /**
     * Return the evaluated annotation name.
     *
     * @return either 'javax' or 'jakarta' (in lowercase).
     * @see ValidationsAnnotation
     */
    String getAnnotationLibraryName() {
        return getAnnotation().name().toLowerCase();
    }

    String getExecutionName() {
        return folderName + "-" +
                getNamespace() + "-" +
                testName + "-" +
                getAnnotation().name();
    }

    /**
     * Get a tester for the specified element in the XSD.
     *
     * @param elementName The name of the root element created (the java class name created by JAXB).
     * @see #getNamespace()
     */
    ArtifactTester<XjcFixture> withElement(String elementName) {
        return withElement(getNamespace(), elementName);
    }

    /**
     * Get a tester for the specified element in the XSD.
     *
     * @param namespace the namespace as in <namespace:elementname param='...'/>
     * @param elementName the name of the element without the namespace part
     * @return a tester
     */
    ArtifactTester<XjcFixture> withElement(String namespace, String elementName) {
        final String filename = elementName + ".java";
        List<String> lines = readFile(namespace, filename);
        return new ArtifactTester<>(filename, lines, getAnnotation(), this);
    }

    private String getAnnotationFileName(String ns) {
        final String className = testName.replace("Test", "");
        final String annotationName = separateAnnotation ? getAnnotationLibraryName() : null;
        return className + option(ns) + option(annotationName) + "-annotation.txt";
    }

    private String option(String opt) {
        return opt == null || opt.isEmpty() ? "" : "-" + opt;
    }

    private void checkAllAnnotations(Path filename, String annotatonFilename) throws AssertionError {
        List<String> actual = readFile(filename);
        String annotationFilename = getBaseDir() + "/src/test/resources/" + folderName + "/" +
                annotatonFilename;
        Path annotations = Paths.get(annotationFilename);
        List<String> expected = readFile(annotations);

        if (expected.size() != actual.size()) {
            throw new AssertionError("wrong number of annotations in " + getExecutionName() +
                    " expected:" + expected.size() + " actual:" + actual.size() +
                    " generated file: " + filename.toString() +
                    " test file: " + annotationFilename);
        }

        for (int i=0,l=expected.size(); i<l; i++) {
            String expectedLine = expected.get(i).trim();
            String actualLine = actual.get(i).trim();

            if (!expectedLine.equals(actualLine)) {
                throw new AssertionError("annotation differs in " + getExecutionName() +
                        " expected:<" + expectedLine + "> but was:<" + actualLine + ">");
            }
        }
    }

    private synchronized void writeAllElementsTo(String ns, Path filename) {
        try (BufferedWriter writer = Files.newBufferedWriter(filename, Charset.defaultCharset(),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            gatAllElementsAsString(ns, writer);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void gatAllElementsAsString(String ns, Appendable buf) throws IOException {
        List<Path> fileList = allFilesInDirectory(getAbsolutePath(ns));
        Collections.sort(fileList);
        for (Path p : fileList) {
            String name = p.getFileName().toString();
            if (name.endsWith(".java") &&
                    !name.startsWith("package-info") &&
                    !name.startsWith("ObjectFactory")) {
                String filename = name.replace(".java", "");
                ArtifactTester<XjcFixture> artifactTester =
                        withElement(ns, filename);
                buf.append(filename).append(System.lineSeparator());
                List<String> attributeList = artifactTester.getAllFields();
                Collections.sort(attributeList);
                for (String attribute : attributeList) {
                    buf.append("    ").append(attribute).append(System.lineSeparator());
                    List<String> annotationList = artifactTester.getFieldAnnotations(attribute)
                            .stream()
                            .map(s -> s.trim())
                            .filter(s -> !s.startsWith("@Xml"))
                            .collect(Collectors.toList());
                    annotationList = compactArrayAnnotations(annotationList);
                    for (String a : annotationList) {
                        buf.append("        ").append(a).append(System.lineSeparator());
                    }
                }
            }
        }
    }

    /**
     * Parses annotations eventually spanning multiple lines and put them in a single line to
     * get them easier to test:
     *
     * <pre>
     * {@code
     * @Pattern.List({
     *     @Pattern(regexp = "[0-9]"),
     *     @Pattern(regexp = "[A-B]")
     * })
     * }
     * </pre>
     */
    static List<String> compactArrayAnnotations(List<String> annotationList) {
        List<String> reversedList = new ArrayList<>();
        for (int i=annotationList.size()-1; i>=0; i--) {
            String line = annotationList.get(i);
            if ("})".equals(line)) {
                for (int k=i-1; k>=0; k--) {
                    String startLine = annotationList.get(k);
                    if (startLine.endsWith("({")) {
                        List<String> sublist = annotationList.subList(k + 1, i);
                        String params = sublist.stream().collect(Collectors.joining(" "));
                        reversedList.add(startLine + params + "})");
                        i = k - 1;
                        break;
                    }
                }
            } else {
                reversedList.add(line);
            }
        }
        Collections.sort(reversedList);
        return reversedList;
    }

    private List<String> readFile(String ns, String filename) {
        String absoluteName = getAbsolutePath(ns) + filename;
        Path path = Paths.get(absoluteName);
        return readFile(path);
    }

    private List<String> readFile(Path path) {
        try {
            return Files.readAllLines(path);
        } catch (IOException ex) {
            throw new AssertionError("error loading file " + path, ex);
        }
    }

    private List<Path> allFilesInDirectory(String path) {
        try {
            return Files.list(new File(path).toPath())
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            throw new AssertionError("error loading files in " + path, ex);
        }
    }

    private String getAbsolutePath(String ns) {
        if (ns == null) {
            ns = "";
        } else {
            ns = File.separator + (ns.trim().isEmpty() ? "generated" : ns);
        }
        return getGeneratedDirectory().getAbsolutePath() + ns + File.separator;
    }

    private File getBaseDir() {
        try {
            return new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI())
                    .getParentFile().getParentFile().getAbsoluteFile();
        } catch (Exception ex) {
            throw new AssertionError(ex);
        }
    }
}
