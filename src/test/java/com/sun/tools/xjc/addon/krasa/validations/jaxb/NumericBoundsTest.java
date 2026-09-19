package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerTestHelper;

/**
 * Verification test: the collection path must drop the natural bounds of the numeric
 * type, exactly as the scalar path already does.
 *
 * <p>
 * {@code xsd:int} declares {@code minInclusive} -2147483648 and {@code maxInclusive}
 * 2147483647, which every int satisfies: emitted on a collection they say nothing and
 * they also misrepresent a restriction. The scalar path filters them through
 * {@code NumericRange.valid}; the collection path did not, so a plain
 * {@code List<Integer>} carried both as {@code @EachDecimalMin}/{@code @EachDecimalMax}.
 *
 * <p>
 * The filtering is type aware, and it applies to the <i>element</i> type of the
 * collection: {@code NumericRange} knows the limits of Java {@code Byte}, {@code Short},
 * {@code Integer} and {@code Long} only, so a bound is dropped when the element type
 * already implies it. The fixture holds one field per case, and the last two are the
 * check that the filter is not too broad.
 *
 * <p>
 * Method names must start with {@code test}: the harness extends {@code TestCase}, so
 * surefire collects only JUnit3-style methods, and a JUnit4 {@code @Test} would
 * silently never run.
 *
 * @author Francesco Illuminati
 */
public class NumericBoundsTest extends AnnotationCheckerTestHelper {

    public NumericBoundsTest() {
        super("numericBounds", "a", "NumericBounds");
    }

    /** The natural bounds of xsd:int must not reach the collection. */
    public void testPlainIntCollectionHasNoNaturalBounds() {
        withElement("NumericBounds")
                .withField("plainIntList")
                .assertAnnotationNotPresent("EachDecimalMin")
                .assertAnnotationNotPresent("EachDecimalMax");
    }

    /** Bounds declared by the schema must survive the same filtering, whatever the
     *  element type known to NumericRange (Integer here) or not (BigDecimal here). */
    public void testDeclaredBoundsAreKeptOnACollection() {
        withElement("NumericBounds")
                .withField("boundedIntList")
                .withAnnotation("EachDecimalMin").assertValue("0")
                .withAnnotation("EachDecimalMax").assertValue("9999")
                .end()
                .withField("decimalList")
                .withAnnotation("EachDecimalMin").assertValue("0")
                .withAnnotation("EachDecimalMax").assertValue("100");
    }
}
