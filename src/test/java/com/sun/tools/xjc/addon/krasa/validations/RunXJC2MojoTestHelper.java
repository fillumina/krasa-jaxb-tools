/*
 * Copyright 2021 Francesco Illuminati.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import junit.framework.TestResult;
import org.apache.maven.project.MavenProject;
import org.jvnet.jaxb2.maven2.AbstractXJC2Mojo;
import org.jvnet.jaxb2.maven2.ResourceEntry;
import org.jvnet.jaxb2.maven2.test.RunXJC2Mojo;

/**
 * Testing helper for generated classes. It uses XJC to compile the given XSD into java classes
 * (but doesn't compile them) that can be tested.
 *
 * Each test will be executed twice: once witht the JAVAX validation and the other time
 * with the JAKARTA one.
 *
 * NOTE: We cannot use reflection here because RunXJC2Mojo acts on the generation phase and the
 * generated artifacts are not compiled.
 *
 * @author Francesco Illuminati
 */
public abstract class RunXJC2MojoTestHelper extends RunXJC2Mojo {

    /** Contains ths the executed test names. */
    protected static final Set<String> executions = new HashSet<>();
    protected static final Set<String> executedTests = new HashSet<>();

    private final String folderName;
    private final String namespace;
    private final boolean separateAnnotation;
    private ValidationsAnnotation validationAnnotation;
    private ResourceEntry[] bindings;

    public RunXJC2MojoTestHelper(String folderName, String namespace) {
        this(folderName, namespace, false);
    }

    /**
     *
     * @param folderName the folder name containing the xsd and the -annotation.txt files
     * @param namespace the namespace used in the xsd
     * @param separateAnnotation if there are different -annotation.txt files for each annotation
     *                           (javax or jakarta).
     */
    public RunXJC2MojoTestHelper(String folderName, String namespace, boolean separateAnnotation) {
        this.folderName = folderName;
        this.namespace = namespace;
        this.separateAnnotation = separateAnnotation;
    }

    /** Override to test JAVAX annotated code generation */
    public void checkJavax() throws Exception {}

    /** Override to test JAKARTA annotated code generation */
    public void checkJakarta() throws Exception {}

    /** Override to provide bindings */
    public File getBindingDirectory() {
        return null;
    }

    @Override
    public File getGeneratedDirectory() {
        return new File(getBaseDir(), "target/generated-sources/" + folderName);
    }

    @Override
    public File getSchemaDirectory() {
        return new File(getBaseDir(), "src/test/resources/" + folderName);
    }

    @Override
    protected void configureMojo(AbstractXJC2Mojo mojo) {
        super.configureMojo(mojo);
        mojo.setProject(new MavenProject());
        mojo.setForceRegenerate(true);
        mojo.setExtension(true);

        File bindingDirectory = getBindingDirectory();
        if (bindingDirectory != null) {
            mojo.setBindingDirectory(bindingDirectory);
        }
	}

    // test with all options enabled.
    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.generateNotNullAnnotations, true)
                .add(ValidationsArgument.generateListAnnotations, true)
                .add(ValidationsArgument.targetNamespace, getNamespace())
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .getOptionList();
    }

    /** Execute the current test twice: once for each beckend (Javax and Jackarta). */
    @Override
    public void run(TestResult result) {
        String name = getName();
        final String simpleName = getClass().getSimpleName();
        if (!"testExecute".equals(name) &&
                !("testZDefault".equals(name) &&
                executions.contains(simpleName))) {
            generateAndCheckJakarta();
            super.run(result);

            generateAndCheckJavax();
            super.run(result);

            executions.add(simpleName);
            executedTests.add(simpleName + "." + name);
        }
    }

    /** Default test to execute if none provided. */
    public void testZDefault() {}

    /** @return comma separated values or a single one */
    public final String getNamespace() {
        return namespace;
    }

    /** @return the evaluated annotation. */
    public final ValidationsAnnotation getAnnotation() {
        return validationAnnotation;
    }

    /**
     * Return the evaluated annotation name.
     *
     * @return either 'javax' or 'jakarta' (in lowercase).
     * @see ValidationsAnnotation
     */
    protected final String getAnnotationLibraryName() {
        return getAnnotation().name().toLowerCase();
    }

    private String getAnnotationFileName(String ns) {
        final String className = getClass().getSimpleName()
                .replace("Test", "");
        final String annotationName = separateAnnotation ? getAnnotationLibraryName() : null;
        return className + option(ns) + option(annotationName) + "-annotation.txt";
    }

    private String option(String opt) {
        return opt == null || opt.isEmpty() ? "" : "-" + opt;
    }

    /** Must be overwritten to avoid starting the default test. */
    public final void testExecute() throws Exception {
        // override RunXJC2Mojo own method to allow tests to be executed after mojo creation
    }

    private void generateAndCheckJakarta() {
        validationAnnotation = ValidationsAnnotation.JAKARTA;
        try {
            generateClasses();
            checkAnnotationsInResourceFile();
            checkJakarta();
        } catch (Exception ex) {
            throw new AssertionError(ex);
        }
    }

    private void generateAndCheckJavax() {
        validationAnnotation = ValidationsAnnotation.JAVAX;
        try {
            generateClasses();
            checkAnnotationsInResourceFile();
            checkJavax();
        } catch (Exception ex) {
            throw new AssertionError(ex);
        }
    }

    private void generateClasses() throws Exception {
        super.testExecute();
    }

    private void checkAnnotationsInResourceFile() {
        String[] nsArray = getNamespace().split(",");
        for (String ns : nsArray) {
            String annotatonFilename = getAnnotationFileName(ns);
            Path filename = Paths.get(getGeneratedDirectory().getAbsolutePath() +
                    File.separator + annotatonFilename);

            writeAllElementsTo(ns, filename);

            checkAllAnnotations(filename, annotatonFilename);
        }
    }

    private void checkAllAnnotations(Path filename, String annotatonFilename) throws AssertionError {
        List<String> actual = readFile(filename);
        String annotationFilename = getBaseDir() + "/src/test/resources/" + folderName + "/" +
                annotatonFilename;
        Path annotations = Paths.get(annotationFilename);
        List<String> expected = readFile(annotations);

        if (expected.size() != actual.size()) {
            throw new AssertionError("wrong number of annotations in " + getExecutionName() +
                    " expected:" + expected.size() + " actual:" + actual.size());
        }

        for (int i=0,l=expected.size(); i<l; i++) {
            String expectedLine = expected.get(i).trim();
            String actualLine = actual.get(i).trim();

            assertEquals("annotation differs in " + getExecutionName(),
                    expectedLine, actualLine);
        }
    }

    private synchronized void writeAllElementsTo(String ns, Path filename) {
        try (BufferedWriter writer = Files.newBufferedWriter(filename, Charset.defaultCharset(),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            gatAllElementsAsString(ns, writer);
            writer.close();
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
                ArtifactTester<RunXJC2MojoTestHelper> artifactTester =
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
    private List<String> compactArrayAnnotations(List<String> annotationList) {
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

    private String getExecutionName() {
        return folderName + "-" +
                getNamespace() + "-" +
                getClass().getSimpleName() + "-" +
                getAnnotation().name();
    }

    /**
     * Get a tester for the specified element in the XSD.
     *
     * @param elementName The name of the root element created (the java class name created by JAXB).
     * @see #getNamespace()
     */
    public ArtifactTester<RunXJC2MojoTestHelper> withElement(String elementName) {
        return withElement(getNamespace(), elementName);
    }

    /**
     * Get a tester for the specified element in the XSD.
     *
     * @param namespace the namespace as in <namespace:elementname param='...'/>
     * @param elementName the name of the element without the namespace part
     * @return a tester
     */
    public ArtifactTester<RunXJC2MojoTestHelper> withElement(String namespace, String elementName) {
        final String filename = elementName + ".java";
        List<String> lines = readFile(namespace, filename);
        return new ArtifactTester(filename, lines, getAnnotation(), this);
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

    // It's a dirty little trick to change the name of the called method in the log
    @Override
    public String getName() {
        if (validationAnnotation == null) {
            // it's called by run() to get the function name to call
            return super.getName();
        }
        // it's called by descritpor after the function name has been determined
        return super.getName() + " " + validationAnnotation.name();
    }

}
