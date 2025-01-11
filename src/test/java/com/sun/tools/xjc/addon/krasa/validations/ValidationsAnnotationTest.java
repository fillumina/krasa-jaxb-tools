package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import static junit.framework.TestCase.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati 
 */
public class ValidationsAnnotationTest {

    @Test
    public void testConversionFromJavaxClassNames() throws ClassNotFoundException {
        assertEquals("javax.validation.Valid",
                ValidationsAnnotation.JAVAX.getCanonicalClassName("Valid"));
        assertEquals("javax.validation.constraints.NotNull",
                ValidationsAnnotation.JAVAX.getCanonicalClassName("NotNull"));
        assertEquals("javax.validation.constraints.Size",
                ValidationsAnnotation.JAVAX.getCanonicalClassName("Size"));
        assertEquals("javax.validation.constraints.Digits",
                ValidationsAnnotation.JAVAX.getCanonicalClassName("Digits"));
        assertEquals("javax.validation.constraints.DecimalMin",
                ValidationsAnnotation.JAVAX.getCanonicalClassName("DecimalMin"));
        assertEquals("javax.validation.constraints.DecimalMax",
                ValidationsAnnotation.JAVAX.getCanonicalClassName("DecimalMax"));
        assertEquals("javax.validation.constraints.Pattern",
                ValidationsAnnotation.JAVAX.getCanonicalClassName("Pattern"));
        assertEquals("javax.validation.constraints.Pattern.List",
                ValidationsAnnotation.JAVAX.getCanonicalClassName("List"));
    }

    @Test
    public void testConversionFromJakartaClassNames() throws ClassNotFoundException {
        assertEquals("jakarta.validation.Valid",
                ValidationsAnnotation.JAKARTA.getCanonicalClassName("Valid"));
        assertEquals("jakarta.validation.constraints.NotNull",
                ValidationsAnnotation.JAKARTA.getCanonicalClassName("NotNull"));
        assertEquals("jakarta.validation.constraints.Size",
                ValidationsAnnotation.JAKARTA.getCanonicalClassName("Size"));
        assertEquals("jakarta.validation.constraints.Digits",
                ValidationsAnnotation.JAKARTA.getCanonicalClassName("Digits"));
        assertEquals("jakarta.validation.constraints.DecimalMin",
                ValidationsAnnotation.JAKARTA.getCanonicalClassName("DecimalMin"));
        assertEquals("jakarta.validation.constraints.DecimalMax",
                ValidationsAnnotation.JAKARTA.getCanonicalClassName("DecimalMax"));
        assertEquals("jakarta.validation.constraints.Pattern",
                ValidationsAnnotation.JAKARTA.getCanonicalClassName("Pattern"));
        assertEquals("jakarta.validation.constraints.Pattern.List",
                ValidationsAnnotation.JAKARTA.getCanonicalClassName("List"));
    }

    @Test(expected = ClassNotFoundException.class)
    public void shouldThrowExceptionIfJavaxClassNotFound() throws ClassNotFoundException {
        ValidationsAnnotation.JAVAX.getCanonicalClassName("NotExistentClassName");
    }

    @Test(expected = ClassNotFoundException.class)
    public void shouldThrowExceptionIfJakartaClassNotFound() throws ClassNotFoundException {
        ValidationsAnnotation.JAKARTA.getCanonicalClassName("NotExistentClassName");
    }
}
