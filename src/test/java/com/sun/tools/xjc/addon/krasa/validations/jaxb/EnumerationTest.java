package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import java.util.regex.Pattern;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Created on 15.02.16.
 */
public class EnumerationTest extends AnnotationCheckerFixtureTest {

    public EnumerationTest(ValidationsAnnotation library) {
        super(library, "enumeration", "a", "NaturalPerson");
    }

    @Test
    public void test() throws ClassNotFoundException {
        withElement("NaturalPerson")
                .assertImportSimpleName("Pattern")
                .withField("sex")
                        .withAnnotation("Pattern")
                                .assertParam("regexp", "(\\\\Qf\\\\E)|(\\\\Qm\\\\E)")
                        .end()
                .end()
                .withField("age")
                        .withAnnotation("Pattern")
                                .assertParam("regexp",
                                        "(\\\\Q0 (toddler)\\\\E)|(\\\\Q1-5\\\\E)|" +
                                        "(\\\\Q5-12\\\\E)|(\\\\Q12-18\\\\E)|(\\\\Q18+\\\\E)");
    }

    @Test
    public void testRegexpValidity() {
        String regexp = "(\\Q0 (toddler)\\E)|(\\Q1-5\\E)|(\\Q5-12\\E)|(\\Q12-18\\E)|(\\Q18+\\E)";
        Pattern pattern = Pattern.compile(regexp);

        assertTrue(pattern.matcher("0 (toddler)").matches());
        assertTrue(pattern.matcher("5-12").matches());
        assertTrue(pattern.matcher("18+").matches());
    }
}
