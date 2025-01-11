package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerTestHelper;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;

/**
 *
 * @author Francesco Illuminati
 */
public class ValidTest extends AnnotationCheckerTestHelper {

    public ValidTest() {
        super("valid", "a", "ContentListType,MessageContentType,MessageType");
    }

    @Override
    public void checkJakarta() throws Exception {
        withElement("ContentListType")
                .assertAnnotationNotPresent(ValidationsAnnotation.JAVAX);
    }

    @Override
    public void checkJavax() throws Exception {
        withElement("ContentListType")
                .assertAnnotationNotPresent(ValidationsAnnotation.JAKARTA);
    }

}
