package com.sun.tools.xjc.addon.krasa.validations;

import java.util.stream.Stream;
import junit.framework.TestResult;

/**
 * Helper to test if there are JAVAX annotated classes in a JAKARTA production and vice versa.
 *
 * @author Francesco Illuminati
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
    public void checkJakarta(TestResult result) throws Exception {
        streamOfElementNames().forEach(en -> {
            try {
                withElement(en)
                        .assertAnnotationNotPresent(ValidationsAnnotation.JAVAX);
            } catch (Throwable throwable) {
                recordFailure(result, throwable);
            }
        });
    }

    @Override
    public void checkJavax(TestResult result) throws Exception {
        streamOfElementNames().forEach(en -> {
            try {
                withElement(en)
                        .assertAnnotationNotPresent(ValidationsAnnotation.JAKARTA);
            } catch (Throwable throwable) {
                recordFailure(result, throwable);
            }
        });
    }

    Stream<String> streamOfElementNames() {
        return Stream.of(elementName.split(","))
                .map(s -> s.trim())
                .filter(s -> s != null && !s.isEmpty());
    }

}
