package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import org.junit.Test;

/**
 * Verification test: a numeric type must never end up with a pattern annotation.
 *
 * A pattern on a numeric type cannot be honored in Bean Validation:
 *
 * <ul>
 *   <li>{@code @Pattern} resolves to the Pattern validator, which accepts {@code CharSequence}
 *       only, so it cannot validate a number at all - it fails at validation time instead of
 *       checking anything;</li>
 *   <li>a pattern is not a numeric range: the fixture pins the digit 3 in second position
 *       ({@code [0-9]3[0-9]*}), which no {@code minInclusive}/{@code maxInclusive} pair can
 *       express.</li>
 * </ul>
 *
 * The fixture carries the same numeric simpleType twice, scalar and collection, and this test
 * verifies that neither field receives a pattern annotation - {@code @Pattern} in the first
 * case, {@code @EachPattern} (its collection counterpart, with the same CharSequence-only
 * restriction) in the second.
 *
 * This is the gate for anyone adding pattern support to numbers later: if either assertion
 * starts failing, the rule has been broken. To constrain a numeric value, use numeric facets
 * instead - see the README.
 *
 * Method names must start with {@code test}: the harness extends {@code TestCase}, so surefire
 * collects only JUnit3-style methods, and a JUnit4 {@code @Test} would silently never run.
 *
 * @author Francesco Illuminati
 */
public class NumericPatternTest extends AnnotationCheckerFixtureTest {

    public NumericPatternTest(ValidationsAnnotation library) {
        super(library, "numericPattern", "a", "NumericPattern");
    }

    /** Scalar numeric field: the pattern must not become {@code @Pattern}. */
    @Test
    public void testScalarNumericMustNotHavePattern() {
        withElement("NumericPattern")
                .withField("patternedInteger")
                .assertAnnotationNotPresent("Pattern");
    }

    /**
     * Numeric collection: the pattern must not become {@code @EachPattern}.
     *
     * Both checks are needed: "Pattern" is matched as a prefix of the annotation name, so it
     * does not match {@code @EachPattern}.
     */
    @Test
    public void testNumericCollectionMustNotHaveEachPattern() {
        withElement("NumericPattern")
                .withField("patternedIntegerList")
                .assertAnnotationNotPresent("Pattern")
                .assertAnnotationNotPresent("EachPattern");
    }
}
