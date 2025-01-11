package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.tools.xjc.addon.krasa.validations.NotNullAnnotationCustomMessageType;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati 
 */
public class NotNullAnnotationCustomMessageTypeTest {

    @Test
    public void shouldRecognizeFieldName() {
        assertTrue(NotNullAnnotationCustomMessageType.Fieldname.equalsIgnoreCase("fieldName"));
        assertTrue(NotNullAnnotationCustomMessageType.Fieldname.equalsIgnoreCase("FieldName"));
        assertTrue(NotNullAnnotationCustomMessageType.Fieldname.equalsIgnoreCase("fieldname"));
        assertTrue(NotNullAnnotationCustomMessageType.Fieldname.equalsIgnoreCase("fIeLdNAmE"));

        assertFalse(NotNullAnnotationCustomMessageType.Fieldname.equalsIgnoreCase("field name"));
    }

    @Test
    public void shouldRecognizeClassName() {
        assertTrue(NotNullAnnotationCustomMessageType.Classname.equalsIgnoreCase("className"));
        assertTrue(NotNullAnnotationCustomMessageType.Classname.equalsIgnoreCase("ClassName"));
        assertTrue(NotNullAnnotationCustomMessageType.Classname.equalsIgnoreCase("classname"));
        assertTrue(NotNullAnnotationCustomMessageType.Classname.equalsIgnoreCase("cLaSsNAmE"));

        assertFalse(NotNullAnnotationCustomMessageType.Classname.equalsIgnoreCase("Class name"));
    }

}
