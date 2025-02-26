package com.sun.tools.xjc.addon.krasa.validations;

import java.util.stream.Stream;

/**
 * Helper to test if there are JAVAX annotated classes in a JAKARTA production and vice versa.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AnnotationCheckerTestHelper extends RunXJC2MojoTestHelper {

    private String elementName;

    public AnnotationCheckerTestHelper(String folderName, String namespace, String elementName) {
        super(folderName, namespace);
        this.elementName = elementName;
    }

    public AnnotationCheckerTestHelper(String folderName, String namespace,
            boolean separateAnnotation, String elementName) {
        super(folderName, namespace, separateAnnotation);
        this.elementName = elementName;
    }

    @Override
    public void checkJakarta() throws Exception {
        streamOfElementNames().forEach(en ->
                withElement(en)
                        .assertAnnotationNotPresent(ValidationsAnnotation.JAVAX));
    }

    @Override
    public void checkJavax() throws Exception {
        streamOfElementNames().forEach(en ->
                withElement(en)
                        .assertAnnotationNotPresent(ValidationsAnnotation.JAKARTA));
    }

    Stream<String> streamOfElementNames() {
        return Stream.of(elementName.split(","))
                .map(s -> s.trim())
                .filter(s -> s != null && !s.isEmpty());
    }

}
