package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;

public class NotNullMessageTextTest extends NotNullBase {

    public NotNullMessageTextTest(ValidationsAnnotation library) {
        super(library, "{FieldName} in {ClassName} should be not null");
    }

}
