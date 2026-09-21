package com.sun.tools.xjc.addon.krasa.validations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import org.junit.Test;

/**
 * The tester reads a generated class as text, and the expectations are written the way the generator
 * writes it. These cases pin that reading: which lines belong to a field, what a class looks like to
 * it, and what it refuses. A change to the generator's formatting has to keep this contract.
 *
 * @author Francesco Illuminati
 */
public class ArtifactTesterTest {

    private static final String FILENAME = "SampleType.java";

    private static final List<String> SAMPLE = Arrays.asList(
            "package a;",
            "",
            "import javax.validation.Valid;",
            "import javax.validation.constraints.NotNull;",
            "import javax.validation.constraints.Size;",
            "",
            "public class SampleType {",
            "",
            "    /**",
            "     * A javadoc, which ends the block above the field.",
            "     */",
            "    @Size(min = 0, max = 200)",
            "    @Valid",
            "    protected List<String> annotated;",
            "",
            "    @NotNull",
            "    protected String plain;",
            "",
            "    protected BigDecimal withoutAnnotations;",
            "",
            "    protected void aProtectedMethod(String value) {",
            "    }",
            "}");

    private ArtifactTester<String> tester() {
        return new ArtifactTester<>(FILENAME, SAMPLE, ValidationsAnnotation.JAVAX, "the parent");
    }

    @Test
    public void shouldReadTheFieldsInTheOrderTheyAppear() {
        assertEquals(Arrays.asList("annotated", "plain", "withoutAnnotations"),
                tester().getAllFields());
    }

    @Test
    public void shouldReadTheAnnotationsOfAField() {
        assertEquals(Arrays.asList("    @Size(min = 0, max = 200)", "    @Valid"),
                tester().getFieldAnnotations("annotated"));
    }

    @Test
    public void shouldReadTheAnnotationsOfTheFieldAfterAJavadoc() {
        assertEquals(Arrays.asList("    @NotNull"),
                tester().getFieldAnnotations("plain"));
    }

    @Test
    public void shouldReadNoAnnotationForAPlainField() {
        assertEquals(Collections.emptyList(),
                tester().getFieldAnnotations("withoutAnnotations"));
    }

    @Test
    public void shouldRefuseAFieldItCannotFind() {
        assertThrows(AssertionError.class, () -> tester().withField("missing"));
    }

    @Test
    public void shouldReadTheParametersOfAnAnnotation() {
        tester().withField("annotated")
                .withAnnotation("Size")
                .assertParam("min", 0)
                .assertParam("max", 200);
    }

    @Test
    public void shouldRefuseAParameterWithAnotherValue() {
        assertThrows(AssertionError.class, () -> tester().withField("annotated")
                .withAnnotation("Size").assertParam("max", 999));
    }

    @Test
    public void shouldRefuseAnAnnotationThatIsNotOnTheField() {
        assertThrows(AssertionError.class, () -> tester().withField("plain").withAnnotation("Size"));
    }

    @Test
    public void shouldAcceptAnAnnotationWithoutParameters() {
        tester().withField("plain").withAnnotation("NotNull").assertNoParameters();
    }

    @Test
    public void shouldRefuseAnAnnotationThatHasParametersWhereNoneAreExpected() {
        assertThrows(AssertionError.class, () -> tester().withField("annotated")
                .withAnnotation("Size").assertNoParameters());
    }

    @Test
    public void shouldAcceptTheImportOfItsOwnLibrary() {
        tester().assertAnnotationNotPresent(ValidationsAnnotation.JAKARTA);
    }

    @Test
    public void shouldRefuseTheImportOfTheOtherLibrary() {
        assertThrows(AssertionError.class,
                () -> tester().assertAnnotationNotPresent(ValidationsAnnotation.JAVAX));
    }

    @Test
    public void shouldRequireTheImportItIsAskedFor() {
        tester().assertImportCanonicalName("javax.validation.constraints.Size");
    }

    @Test
    public void shouldRefuseAnImportThatIsNotThere() {
        assertThrows(AssertionError.class,
                () -> tester().assertImportCanonicalName("javax.validation.constraints.Digits"));
    }
}
