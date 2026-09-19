package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerTestHelper;

/**
 * Verification test: a numeric value pinned by {@code fixed} must become a fixed range, on elements
 * and on attributes alike, and an enumeration must not be translated at all.
 *
 * <p>
 * {@code fixed} is a single value and nothing else, so a range that begins and ends there
 * represents it exactly - every fixed value is expressible, nothing is left out. An enumeration is
 * a value <i>set</i>, and Bean Validation has no "one of these values" constraint: only the
 * single-valued case could be expressed while a multi-valued one stayed silently ignored, which is
 * the partial promise that the pattern refusal was about. So enumeration is left alone whatever its
 * cardinality, and this fixture pins both cases.
 *
 * <p>
 * A fixed value equal to the limit of the Java type is not the schema's constraint but the type's
 * own, so it is dropped like every other natural bound.
 *
 * <p>
 * Method names must start with {@code test}: the harness extends {@code TestCase}, so surefire
 * collects only JUnit3-style methods, and a JUnit4 {@code @Test} would silently never run.
 *
 * @author Francesco Illuminati
 */
public class NumericValueTest extends AnnotationCheckerTestHelper {

    public NumericValueTest() {
        super("numericValue", "a", "NumericValue");
    }

    /** A value pinned by fixed on an element is a range that begins and ends there. */
    public void testFixedElementHasAFixedRange() {
        withElement("NumericValue")
                .withField("fixedValue")
                .withAnnotation("DecimalMin").assertValue("1")
                .withAnnotation("DecimalMax").assertValue("1");
    }

    /** The same on an attribute. */
    public void testFixedAttributeHasAFixedRange() {
        withElement("NumericValue")
                .withField("fixedAttribute")
                .withAnnotation("DecimalMin").assertValue("1")
                .withAnnotation("DecimalMax").assertValue("1");
    }

    /** A fixed value equal to the type's own limit is noise, not a constraint. */
    public void testFixedValueAtTheTypeLimitIsDropped() {
        withElement("NumericValue")
                .withField("fixedAtTheTypeLimit")
                .assertAnnotationNotPresent("DecimalMin")
                .assertAnnotationNotPresent("DecimalMax");
    }

    /** An enumeration of one value is a value set too, and it is left alone. */
    public void testSingleValuedEnumerationHasNoBounds() {
        withElement("NumericValue")
                .withField("singleEnumerated")
                .assertAnnotationNotPresent("DecimalMin")
                .assertAnnotationNotPresent("DecimalMax");
    }

    /** So is an enumeration of several values. */
    public void testMultiValuedEnumerationHasNoBounds() {
        withElement("NumericValue")
                .withField("multiEnumerated")
                .assertAnnotationNotPresent("DecimalMin")
                .assertAnnotationNotPresent("DecimalMax");
    }
}
