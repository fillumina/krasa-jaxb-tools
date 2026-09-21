package com.sun.tools.xjc.addon.krasa.validations.exclude;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;
import java.util.List;

/**
 * The fixture of the {@code exclude} option as it is generated today, with no exclusion: the
 * reference the other classes in this package are derived from.
 *
 * @author Francesco Illuminati
 */
public class ExcludeBaselineTest extends AnnotationCheckerFixtureTest {

    public ExcludeBaselineTest(ValidationsAnnotation library) {
        super(library, "exclude", "a", "RootType,ChildType");
    }

    @Override
    protected List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .getOptionList();
    }
}
