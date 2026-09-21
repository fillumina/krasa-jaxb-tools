package com.sun.tools.xjc.addon.krasa.validations;

import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * The element names a checker fixture covers: separated by commas, trimmed, the empty ones left out.
 *
 * @author Francesco Illuminati
 */
public class AnnotationCheckerElementNamesTest {

    @Test
    public void shouldReadASingleElement() {
        assertEquals(Arrays.asList("single-element"),
                AnnotationCheckerFixtureTest.elementNames("single-element"));
    }

    @Test
    public void shouldReadTwoElements() {
        assertEquals(Arrays.asList("element1", "element2"),
                AnnotationCheckerFixtureTest.elementNames("element1,element2"));
    }

    @Test
    public void shouldTrimTheElements() {
        assertEquals(Arrays.asList("element1", "element2"),
                AnnotationCheckerFixtureTest.elementNames("element1 , element2"));
    }

    @Test
    public void shouldIgnoreAnEmptyElement() {
        assertEquals(Arrays.asList("element1"),
                AnnotationCheckerFixtureTest.elementNames("element1 ,  "));
    }

    @Test
    public void shouldIgnoreAnEmptyElementInTheMiddle() {
        assertEquals(Arrays.asList("element1", "element2"),
                AnnotationCheckerFixtureTest.elementNames("element1 , , element2"));
    }
}
