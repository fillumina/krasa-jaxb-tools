package com.sun.tools.xjc.addon.krasa.validations;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import static junit.framework.TestCase.assertEquals;
import org.junit.Test;

/**
 * Helper to test if there are JAVAX annotated classes in a JAKARTA production and vice versa.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AnnotationCheckerTestHelperTest {

    @Test
    public void shouldStreamASingleElement() {
        assertEquals(Arrays.asList("single-element"),
                getOutputStreamAsList("single-element"));
    }

    @Test
    public void shouldStream2Elements() {
        assertEquals(Arrays.asList("element1", "element2"),
                getOutputStreamAsList("element1,element2"));
    }

    @Test
    public void shouldStream2ElementsWithSpaces() {
        assertEquals(Arrays.asList("element1", "element2"),
                getOutputStreamAsList("element1 , element2"));
    }

    @Test
    public void shouldStream1ElementWithEmptyOne() {
        assertEquals(Arrays.asList("element1"),
                getOutputStreamAsList("element1 ,  "));
    }

    @Test
    public void shouldStream2ElementsWithEmptyOne() {
        assertEquals(Arrays.asList("element1", "element2"),
                getOutputStreamAsList("element1 , , element2"));
    }

    private List<String> getOutputStreamAsList(String elementName) {
        AnnotationCheckerTestHelper acth = new AnnotationCheckerTestHelper(
                "foldername", "namespace", elementName);

        return acth.streamOfElementNames().collect(Collectors.toList());
    }

}
