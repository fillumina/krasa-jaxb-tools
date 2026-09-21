package com.sun.tools.xjc.addon.krasa.validations.exclude;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;
import java.util.List;

/**
 * The replacement may be another of the annotations this plugin manages, and both computed ones go.
 *
 * @author Francesco Illuminati
 */
public class ExcludeRewritesAnotherAnnotationTest extends AnnotationCheckerFixtureTest {

    public ExcludeRewritesAnotherAnnotationTest(ValidationsAnnotation library) {
        super(library, "exclude", "a", "RootType,ChildType");
    }

    @Override
    protected List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .add("-XJsr303Annotations:exclude=*#amount=@Digits(integer = 3, fraction = 2)")
                .getOptionList();
    }

}
