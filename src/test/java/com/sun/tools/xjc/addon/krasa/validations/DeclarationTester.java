package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;

/**
 * Assert annotations on a declaration (might be fields, methods or classes).
 *
 * @author Francesco Illuminati
 */
public class DeclarationTester<P> {

    final ArtifactTester<P> parent;
    final String filename;
    final String attributeName;
    final String definition;
    final List<String> annotationList;

    public DeclarationTester(ArtifactTester<P> parent, String filename,
            String attributeName, String definition, List<String> annotationList) {
        this.parent = parent;
        this.filename = filename;
        this.attributeName = attributeName;
        this.definition = definition;
        this.annotationList = annotationList;
    }

    public ArtifactTester<P> end() {
        return parent;
    }

    public DeclarationTester<P> assertClass(Class<?> clazz) {
        String className = clazz.getSimpleName();
        if (!definition.contains(className + " ")) {
            throw new AssertionError("attribute " + attributeName +
                    " in " + filename + " expected of class " +
                    clazz.getName() + " but is: " + definition);
        }
        return this;
    }

    public DeclarationTester<P> assertAnnotationNotPresent(String annotation) {
        long counter = annotationList.stream()
                .filter(l -> l.trim().startsWith("@" + annotation)).count();
        if (counter != 0) {
            throw new AssertionError("annotation " + annotation +
                    " of declaration " + attributeName + " in " + filename + " found");
        }
        return this;
    }

    public AnnotationTester<P> withAnnotation(String annotation) {
        String line = annotationList.stream()
                .filter(l -> l.trim().startsWith("@" + annotation)).findFirst()
                .orElseThrow(() -> new AssertionError("annotation " + annotation +
                        " of attribute " + attributeName + " in " + filename + " not found "));
        return new AnnotationTester<P>(this, line, annotation);
    }

    public DeclarationTester<P> assertNoAnnotationsPresent() {
        if (!annotationList.isEmpty()) {
            throw new AssertionError("attribute " + attributeName +
                    " in " + filename + " contains annotations: " + annotationList.toString());
        }
        return this;
    }

    public DeclarationTester<P> assertType(String type) {
        if (!definition.contains(type + " ")) {
            throw new AssertionError("attribute " + attributeName +
                    " is not of type " + type + " but: " + definition);
        }
        return this;
    }
}
