package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import org.junit.Test;

/** Checks distinct lengths of an XML list and of each repeated value. */
public class ListItemLengthTest extends AnnotationCheckerFixtureTest {

    public ListItemLengthTest(ValidationsAnnotation library) {
        super(library, "listItemLength", "a", "Container");
    }

    @Test
    public void exactItemLengthAndListLengthStayOnTheirRespectiveValues() {
        withElement("Container")
                .withField("fromMinimum")
                    .withAnnotation("EachSize").assertParam("min", 3).assertParam("max", 3)
                    .end().end()
                .withField("fromMaximum")
                    .withAnnotation("EachSize").assertParam("min", 3).assertParam("max", 3)
                    .end().end()
                .withField("repeated")
                    .withAnnotation("EachSize").assertParam("min", 4).assertParam("max", 4)
                    .end().end()
                .withField("listValue")
                    .withAnnotation("Size").assertParam("min", 2).assertParam("max", 2);
    }
}
