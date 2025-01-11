package com.sun.tools.xjc.addon.krasa.validations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Load a class textually and allows to assert its content.
 *
 * @author Francesco Illuminati
 */
public class ArtifactTester<P> {

    private final String filename;
    private final List<String> lines;
    private final ValidationsAnnotation validationsAnnotation;
    private final P parent;

    ArtifactTester(String filename, List<String> lines,
            ValidationsAnnotation validationsAnnotation,
            P parent) {
        this.filename = filename;
        this.lines = lines;
        this.validationsAnnotation = validationsAnnotation;
        this.parent = parent;
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

    public ArtifactTester<P> assertImport(Class<?> clazz) {
        return assertImportCanonicalName(clazz.getCanonicalName());
    }

    /**
     * Check if the given simple annotation name is present in the include statement.
     *
     * @param simpleName the simple name of the class to check (i.e. 'Valid').
     * @return a tester
     */
    public ArtifactTester<P> assertImportSimpleName(String simpleName) throws ClassNotFoundException {
        String canonicalName = validationsAnnotation.getCanonicalClassName(simpleName);
        return assertImportCanonicalName(canonicalName);
    }

    /**
     * Check if the given canonical name for the annotation is present in the import statement.
     *
     * @param canonicalName the canonical name of the class to check (i.e. 'javax.validation.constraints.DecimalMin')
     * @return a tester
     */
    public ArtifactTester<P> assertImportCanonicalName(String canonicalName) {
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
    public DeclarationTester<P> classAnnotations() {
        final String clazzName = filename.replace(".java", "");
        int line = getLineForClass(clazzName, "public class ");
        List<String> annotationList = getFieldAnnotations(clazzName, line);
        String definition = lines.get(line);
        return new DeclarationTester<>(this, filename, clazzName, definition, annotationList);
    }

    public DeclarationTester<P> withField(String fieldName) {
        int line = getLineForField(fieldName);
        List<String> annotationList = getFieldAnnotations(fieldName, line);
        String definition = lines.get(line);
        return new DeclarationTester<>(this, filename, fieldName, definition, annotationList);
    }

    public DeclarationTester<P> withMethod(String methodName) {
        int line = getLineForMethod(methodName);
        List<String> annotationList = getFieldAnnotations(methodName, line);
        String definition = lines.get(line);
        return new DeclarationTester<>(this, filename, methodName, definition, annotationList);
    }

    public DeclarationTester<P> withParameter(String parameterName) {
        int line = getLineForParameter(parameterName);
        List<String> annotationList = getFieldAnnotations(parameterName, line);
        String definition = lines.get(line);
        return new DeclarationTester<>(this, filename, parameterName, definition, annotationList);
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
        int prevAttribute = prevLine(fieldName, line);
        List<String> annotationList = lines.subList(prevAttribute, line);
        return annotationList;
    }

    /** Allows for fluid interface: go back to test helper. */
    public P end() {
        return parent;
    }

    private int getLineForClass(String className, String startingWith) {
        for (int i = 0, l = lines.size(); i < l; i++) {
            String line = lines.get(i).trim();
            if (line.startsWith(startingWith + className)) {
                return i;
            }
        }
        throw new AssertionError("attribute " + className + " not found in file " + filename);
    }

    private int getLineForMethod(String methodName) {
        for (int i = 0, l = lines.size(); i < l; i++) {
            String line = lines.get(i).trim();
            if (line.endsWith(methodName + "(")) {
                return i;
            }
        }
        throw new AssertionError("method " + methodName + " not found in file " + filename);
    }

    private int getLineForParameter(String parameterName) {
        for (int i = 0, l = lines.size(); i < l; i++) {
            String line = lines.get(i).trim();
            if (line.endsWith(parameterName)) {
                return i;
            }
        }
        throw new AssertionError("method " + parameterName + " not found in file " + filename);
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

    private int prevLine(String fieldName, int fieldLine) {
        for (int i = fieldLine - 1; i >= 0; i--) {
            String line = lines.get(i).trim();
            if (line.trim().isEmpty() || line.startsWith("public ") ||
                    line.startsWith("protected ") || line.startsWith("*/")) {
                return i + 1;
            }
        }
        throw new AssertionError("cannot extract validatitions for " + fieldName + " in file " + filename);
    }

}
