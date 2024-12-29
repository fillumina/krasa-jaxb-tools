package com.sun.tools.xjc.addon.krasa.validations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Check an XJC generated class for annotations, field types and annotation parameters.
 *
 * @author Francesco Illuminati
 */
public class ArtifactTester {

    private final String filename;
    private final List<String> lines;
    private final RunXJC2MojoTestHelper outer;

    ArtifactTester(String filename, List<String> lines, final RunXJC2MojoTestHelper outer) {
        this.outer = outer;
        this.filename = filename;
        this.lines = lines;
    }

    public void assertAnnotationNotPresent(ValidationsAnnotation annotation) {
        final String annotationString = annotation.name().toLowerCase();
        final String check = "import " + annotationString + ".validation";
        for (int i=0,l=lines.size(); i<l; i++) {
            String line = lines.get(i);
            if (line.startsWith(check)) {
                throw new AssertionError("line " + i + " contains unwanted annotation type: " +
                        annotationString + " -> '" + line + "'");
            }
        }
    }


    /**
     * Check if the given simple annotation name is present in the include statement.
     *
     * @param simpleName the simple name of the class to check (i.e. 'Valid').
     * @return a tester
     */
    public ArtifactTester assertImportSimpleName(String simpleName) throws ClassNotFoundException {
        String canonicalName = outer.getAnnotation().getCanonicalClassName(simpleName);
        return assertImportCanonicalName(canonicalName);
    }

    /**
     * Check if the given canonical name for the annotation is present in the import statement.
     *
     * @param canonicalName the canonical name of the class to check (i.e. 'javax.validation.constraints.DecimalMin')
     * @return a tester
     */
    public ArtifactTester assertImportCanonicalName(String canonicalName) {
        Objects.requireNonNull(canonicalName);
        if (!canonicalName.contains(".")) {
            throw new AssertionError("the name passed doesn't seem to be a canonical name: " + canonicalName);
        }
        lines.stream()
                .filter(s -> s.contains("import " + canonicalName + ";")).findAny()
                .orElseThrow(() -> new AssertionError("annotation not found: " + canonicalName));
        return this;
    }

    /**
     * Check annotations relative to the class.
     */
    public ClassTester classAnnotations() {
        final String clazzName = filename.replace(".java", "");
        int line = getLineForClass(clazzName);
        List<String> annotationList = getFieldAnnotations(clazzName, line);
        String definition = lines.get(line);
        return new ClassTester(this, filename, clazzName, definition, annotationList);
    }

    /** Check a specific field. */
    public ClassTester withField(String fieldName) {
        int line = getLineForField(fieldName);
        List<String> annotationList = getFieldAnnotations(fieldName, line);
        String definition = lines.get(line);
        return new ClassTester(this, filename, fieldName, definition, annotationList);
    }

    /**
     * @param fieldName
     * @return the annotations relative to the given field
     */
    public List<String> getFieldAnnotations(String fieldName) {
        int line = getLineForField(fieldName);
        return getFieldAnnotations(fieldName, line);
    }

    List<String> getAllFields() {
        List<String> list = new ArrayList<>();
        for (int i = 0, l = lines.size(); i < l; i++) {
            String line = lines.get(i).trim();
            if (line.startsWith("protected ") && line.endsWith(";")) {
                int idx = line.lastIndexOf(' ') + 1;
                String fieldName = line.substring(idx, line.length() - 1);
                list.add(fieldName);
            }
        }
        return list;
    }

    private List<String> getFieldAnnotations(String fieldName, int line) {
        int prevAttribute = prevFieldLine(fieldName, line);
        List<String> annotationList = lines.subList(prevAttribute, line);
        return annotationList;
    }

    /** Allows for fluid interface: go back to test helper. */
    public RunXJC2MojoTestHelper end() {
        return outer;
    }

    private int getLineForClass(String className) {
        for (int i = 0, l = lines.size(); i < l; i++) {
            String line = lines.get(i).trim();
            if (line.startsWith("public class " + className)) {
                return i;
            }
        }
        throw new AssertionError("attribute " + className + " not found in file " + filename);
    }

    private int getLineForField(String fieldName) {
        for (int i = 0, l = lines.size(); i < l; i++) {
            String line = lines.get(i).trim();
            if (line.startsWith("protected ") && line.endsWith(fieldName + ";")) {
                return i;
            }
        }
        throw new AssertionError("attribute " + fieldName + " not found in file " + filename);
    }

    private int prevFieldLine(String fieldName, int fieldLine) {
        for (int i = fieldLine - 1; i >= 0; i--) {
            String line = lines.get(i).trim();
            if (line.trim().isEmpty() || line.startsWith("public ") || line.startsWith("protected ")) {
                return i + 1;
            }
        }
        throw new AssertionError("cannot extract validatitions for " + fieldName + " in file " + filename);
    }

}
