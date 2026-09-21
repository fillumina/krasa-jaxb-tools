package com.sun.tools.xjc.addon.krasa.validations;

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
        for (String elementName : elementNames.split(",")) {
            String name = elementName.trim();
            if (name.isEmpty()) {
                continue;
            }
            try {
                withElement(name).assertAnnotationNotPresent(otherLibrary());
            } catch (Throwable throwable) {
                findings.addError(throwable);
            }
        }
    }

    private ValidationsAnnotation otherLibrary() {
        return getAnnotation() == ValidationsAnnotation.JAKARTA
                ? ValidationsAnnotation.JAVAX
                : ValidationsAnnotation.JAKARTA;
    }
}
