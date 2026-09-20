package com.sun.tools.xjc.addon.krasa.validations.exclude;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerTestHelper;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;
import java.util.List;

/**
 * The option accumulates: two statements, each doing its own thing.
 *
 * @author Francesco Illuminati
 */
public class ExcludeRepeatedStatementsTest extends AnnotationCheckerTestHelper {

    public ExcludeRepeatedStatementsTest() {
        super("exclude", "a", "RootType,ChildType");
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .add("-XJsr303Annotations:exclude=*#code")
                .add("-XJsr303Annotations:exclude=*#label=@Size(max = {max})")
                .getOptionList();
    }

}
