package com.sun.tools.xjc.addon.krasa.validations;

import java.util.Arrays;
import java.util.LinkedHashSet;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/** A single facet can exist without a matching collection of facets. */
public class FacetNullCollectionTest {

    @Test
    public void singlePatternWithoutCollectionIsRetained() {
        AbstractFacet facet = new AccumulatorFacet() {
            @Override
            public String pattern() {
                return "\\i+";
            }
        };
        assertEquals(new LinkedHashSet<>(Arrays.asList("[_:A-Za-z]+")), facet.getPatterns());
    }

    @Test
    public void singleEnumerationWithoutCollectionIsRetained() {
        AbstractFacet facet = new AccumulatorFacet() {
            @Override
            public String enumeration() {
                return "a.b";
            }
        };
        assertEquals(new LinkedHashSet<>(Arrays.asList("\\Qa.b\\E")), facet.getEnumerations());
    }
}
