package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;
import java.util.ArrayList;
import java.util.List;

/**
 * The plugin answers to its former name and to {@code -XBeanValidationAnnotations}, and both must
 * generate the same annotations: the expectation file is the one the old name is checked against,
 * copied under this class's name.
 *
 * @author Francesco Illuminati
 */
public class AliasNameTest extends AnnotationCheckerFixtureTest {

    private static final String ALIAS = "-XBeanValidationAnnotations";

    public AliasNameTest(ValidationsAnnotation library) {
        super(library, "valid", "a", "ContentListType,MessageContentType,MessageType");
    }

    /**
     * The same arguments {@code ArgumentBuilder} produces, with the name this test is about.
     */
    @Override
    protected List<String> getArgs() {
        List<String> args = new ArrayList<>();
        args.add(ALIAS);
        args.add(withValue(ValidationsArgument.generateNotNullAnnotations, true));
        args.add(withValue(ValidationsArgument.generateListAnnotations, true));
        args.add(withValue(ValidationsArgument.targetNamespace, getNamespace()));
        args.add(withValue(ValidationsArgument.validationAnnotations, getAnnotation().name()));
        return args;
    }

    private String withValue(ValidationsArgument argument, Object value) {
        return ALIAS + ":" + argument.name() + "=" + value;
    }

}
