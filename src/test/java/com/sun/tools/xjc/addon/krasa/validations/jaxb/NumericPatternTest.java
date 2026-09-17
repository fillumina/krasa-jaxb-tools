package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerTestHelper;
import org.junit.Test;

/**
 * A literal numeric pattern (i.e. "1") on a numeric field is expressed with
 * {@code @DecimalMin}/{@code @DecimalMax} because {@code @Pattern} only accepts strings.
 *
 * @author Dmitry Lebedko
 */
public class NumericPatternTest extends AnnotationCheckerTestHelper {

    public NumericPatternTest() {
        super("numericPattern", "a", "NumericPattern");
    }

    @Test
    public void shouldConvertLiteralPatternIntoRange() {
        withElement("NumericPattern")
                .withField("aunsignedByte")
                        .assertAnnotationNotPresent("Pattern")
                        .withAnnotation("DecimalMin").assertParam("value", "1").end()
                        .withAnnotation("DecimalMax").assertParam("value", "1").end()
                        .end()
                .withField("adecimal")
                        .assertAnnotationNotPresent("Pattern")
                        .withAnnotation("DecimalMin").assertParam("value", "-1.5").end()
                        .withAnnotation("DecimalMax").assertParam("value", "-1.5").end()
                        .end()
                .withField("astring")
                        .assertAnnotationNotPresent("DecimalMin")
                        .withAnnotation("Pattern").assertParam("regexp", "1").end()
                        .end()
                .withField("nonLiteral")
                        .assertAnnotationNotPresent("Pattern")
                        .withAnnotation("DecimalMin").assertParam("value", "0").end()
                        .withAnnotation("DecimalMax").assertParam("value", "999");
    }

}
