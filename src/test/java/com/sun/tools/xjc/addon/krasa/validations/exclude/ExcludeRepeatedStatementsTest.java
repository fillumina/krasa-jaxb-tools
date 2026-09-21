package com.sun.tools.xjc.addon.krasa.validations.exclude;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;
import java.util.List;

/**
 * The option accumulates: two statements, each doing its own thing.
 *
 * @author Francesco Illuminati
 */
public class ExcludeRepeatedStatementsTest extends AnnotationCheckerFixtureTest {

    public ExcludeRepeatedStatementsTest(ValidationsAnnotation library) {
        super(library, "exclude", "a", "RootType,ChildType");
    }

    @Override
    protected List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .add("-XJsr303Annotations:exclude=*#code")
                .add("-XJsr303Annotations:exclude=*#label=@Size(max = {max})")
                .getOptionList();
    }

}
