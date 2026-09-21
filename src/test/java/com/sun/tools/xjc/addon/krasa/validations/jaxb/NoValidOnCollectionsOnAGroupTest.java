package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerFixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;
import java.util.List;
import org.junit.Test;

/**
 * A model group is a container too, so the option that drops {@code @Valid} from collections has to
 * reach it - otherwise the option would say "collections" and mean only some of them.
 *
 * @author Francesco Illuminati
 */
public class NoValidOnCollectionsOnAGroupTest extends AnnotationCheckerFixtureTest {

    private static final String NO_VALID_ON_COLLECTIONS =
            "-XJsr303Annotations:generateValidOnCollections=false";

    public NoValidOnCollectionsOnAGroupTest(ValidationsAnnotation library) {
        super(library, "modelGroup", "a", "Grouped");
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

    /** The model group's container loses @Valid with the option off. */
    @Test
    public void testTheGroupDoesNotCarryValidWhenAsked() {
        withElement("Grouped")
                .withField("itemAndNote")
                .assertAnnotationNotPresent("Valid");
    }
}
