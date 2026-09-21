package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A fixture test that also checks the classes carry no annotation of the other library: a JAVAX
 * annotation must not appear in a JAKARTA production and the other way round.
 *
 * @author Francesco Illuminati
 */
public abstract class AnnotationCheckerFixtureTest extends FixtureTest {

    private final String elementNames;

    protected AnnotationCheckerFixtureTest(ValidationsAnnotation library, String folderName,
            String namespace, String elementNames) {
        this(library, folderName, namespace, false, elementNames);
    }

    protected AnnotationCheckerFixtureTest(ValidationsAnnotation library, String folderName,
            String namespace, boolean separateAnnotation, String elementNames) {
        super(library, folderName, namespace, separateAnnotation);
        this.elementNames = elementNames;
    }

    @Override
    protected void checkGeneratedAnnotations() {
        for (String elementName : elementNames(elementNames)) {
            try {
                withElement(elementName).assertAnnotationNotPresent(otherLibrary());
            } catch (Throwable throwable) {
                findings.addError(throwable);
            }
        }
    }

    /**
     * @param names the element names, separated by commas
     * @return the names, trimmed, with the empty ones left out
     */
    static List<String> elementNames(String names) {
        return Stream.of(names.split(","))
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toList());
    }

    private ValidationsAnnotation otherLibrary() {
        return getAnnotation() == ValidationsAnnotation.JAKARTA
                ? ValidationsAnnotation.JAVAX
                : ValidationsAnnotation.JAKARTA;
    }
}
