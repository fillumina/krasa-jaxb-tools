package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerTestHelper;

/**
 * The example documents a kind of type extension which is not handled by XJC that simply
 * ignores it.
 * Refer to <a href='https://github.com/fillumina/krasa-jaxb-tools/issues/16'>issue #16</a>.
 *
 * @author Francesco Illuminati
 */
public class ExtendTest extends AnnotationCheckerTestHelper {

    public ExtendTest() {
        super("extend", "a", "AComplexType");
    }

}
