package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;
import java.util.List;
import org.junit.Test;

/**
 * A collection must not carry {@code @Valid} when the option asks for it.
 *
 * <p>
 * Hibernate Validator deprecates {@code @Valid} on a container (HV000271) and asks for it on the
 * type argument instead: {@code List<@Valid Foo>}. This generator cannot write a type argument
 * annotation - codemodel has no support for them - so the option drops the annotation from the
 * container instead. It is a workaround, not the fix, and it is off by default.
 *
 * <p>
 * The option is passed as the text a user would type, not through the enum, so that this test
 * compiles and fails on the missing option rather than on a missing constant.
 *
 * @author Francesco Illuminati
 */
public class NoValidOnCollectionsTest extends AnnotationCheckerFixtureTest {

    private static final String NO_VALID_ON_COLLECTIONS =
            "-XJsr303Annotations:generateValidOnCollections=false";

    public NoValidOnCollectionsTest(ValidationsAnnotation library) {
        super(library, "pattern", "a", "ProcessContainer");
    }

    @Override
    protected List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.generateNotNullAnnotations, true)
                .add(ValidationsArgument.generateListAnnotations, true)
                .add(ValidationsArgument.targetNamespace, getNamespace())
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .add(NO_VALID_ON_COLLECTIONS)
                .getOptionList();
    }

    /** The container loses the annotation the validator deprecated. */
    @Test
    public void testCollectionMustNotCarryValid() {
        withElement("ProcessContainer")
                .withField("someList")
                .assertAnnotationNotPresent("Valid");
    }
}
