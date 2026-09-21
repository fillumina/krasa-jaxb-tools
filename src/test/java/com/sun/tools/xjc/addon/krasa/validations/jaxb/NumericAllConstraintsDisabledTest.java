package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;
import java.util.List;

/**
 *
 * @author Francesco Illuminati
 */
public class NumericAllConstraintsDisabledTest extends AnnotationCheckerFixtureTest {

    public NumericAllConstraintsDisabledTest(ValidationsAnnotation library) {
        super(library, "numericAllConstraints", "a", "NumericAllConstraints");
    }

    @Override
    protected List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.generateAllNumericConstraints, false)
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .getOptionList();
    }
}
