package com.sun.tools.xjc.addon.krasa.validations.exclude;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerTestHelper;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;
import java.util.List;

/**
 * A parameter the generator never sets is resolved from the annotation itself, so {message} is the default message of that library.
 *
 * @author Francesco Illuminati
 */
public class ExcludePlaceholderDefaultsTest extends AnnotationCheckerTestHelper {

    public ExcludePlaceholderDefaultsTest() {
        super("exclude", "a", true, "RootType,ChildType");
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .add("-XJsr303Annotations:exclude=*#child=@NotNull(message = \"{message}\")")
                .getOptionList();
    }

}
