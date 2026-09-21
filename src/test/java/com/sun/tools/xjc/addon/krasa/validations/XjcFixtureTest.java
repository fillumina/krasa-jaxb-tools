package com.sun.tools.xjc.addon.krasa.validations;

import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * The annotations a fixture writes into its trace are joined and sorted before they are compared with
 * the expected file, which is what makes a multi-line annotation comparable. These cases pin that.
 *
 * @author Francesco Illuminati
 */
public class XjcFixtureTest {

    @Test
    public void shouldJoinAMultiLineAnnotationIntoOneLine() {
        assertEquals(Arrays.asList(
                "@Pattern.List({@Pattern(regexp = \"[0-9]\"), @Pattern(regexp = \"[A-B]\")})"),
                XjcFixture.compactArrayAnnotations(Arrays.asList(
                        "@Pattern.List({",
                        "@Pattern(regexp = \"[0-9]\"),",
                        "@Pattern(regexp = \"[A-B]\")",
                        "})")));
    }

    @Test
    public void shouldLeaveAnAnnotationThatIsAlreadyOneLine() {
        assertEquals(Arrays.asList("@Size(max = 5)"),
                XjcFixture.compactArrayAnnotations(Arrays.asList("@Size(max = 5)")));
    }

    @Test
    public void shouldSortWhatItReturns() {
        // the input is not the reverse of the expected order, so returning it backwards is not enough
        assertEquals(Arrays.asList("@NotNull", "@Size(max = 5)", "@Valid"),
                XjcFixture.compactArrayAnnotations(
                        Arrays.asList("@Valid", "@NotNull", "@Size(max = 5)")));
    }
}
