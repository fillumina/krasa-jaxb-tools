package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMod;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * The replacement of an {@code exclude} statement: the text it accepts, what it refuses, and the
 * annotation it writes out of the values the plugin computed.
 *
 * @author Francesco Illuminati
 */
public class ReplacementTest {

    private static final ValidationsAnnotation JAVAX = ValidationsAnnotation.JAVAX;

    /** A generated field and what the plugin would have written on it, to be replaced. */
    private static class Fixture {

        final JCodeModel codeModel = new JCodeModel();
        final JDefinedClass clazz;
        final JFieldVar field;
        final List<XjcAnnotator.Annotate> computed = new ArrayList<>();

        Fixture(String propertyName, String computedAnnotation, String value) throws Exception {
            clazz = codeModel._class("a.RootType");
            field = clazz.field(JMod.PROTECTED, String.class, propertyName);
            XjcAnnotator annotator = new XjcAnnotator(field, SilentValidationLogger.INSTANCE, computed);
            if ("Size".equals(computedAnnotation)) {
                annotator.annotate(JAVAX.getSizeClass()).param("min", 2).param("max", 20);
            } else if ("Pattern".equals(computedAnnotation)) {
                annotator.annotate(JAVAX.getPatternClass()).param("regexp", value);
            } else {
                annotator.annotate(JAVAX.getNotNullClass());
            }
        }

        void replace(String replacement, String propertyName) {
            Replacement.parse(replacement, JAVAX)
                    .writeInto(field, "a.RootType", propertyName, computed,
                            SilentValidationLogger.INSTANCE);
        }

        /** @return the generated source, which is what the user reads. */
        String rendered() throws Exception {
            File dir = Files.createTempDirectory("replacement").toFile();
            codeModel.build(dir);
            return new String(Files.readAllBytes(new File(dir, "a/RootType.java").toPath()),
                    StandardCharsets.UTF_8);
        }
    }

    @Test
    public void aReplacementNamesOnlyTheAnnotationsWeManage() {
        try {
            Replacement.parse("@MyOwnConstraint", JAVAX);
            fail("an unknown annotation must be refused");
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage(), ex.getMessage().contains("not one of the annotations"));
        }
    }

    @Test
    public void aReplacementIsOneAnnotation() {
        try {
            Replacement.parse("@Size(min = 1) @NotNull", JAVAX);
            fail("two annotations must be refused");
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage(), ex.getMessage().contains("one annotation"));
        }
    }

    @Test
    public void aParameterTheAnnotationDoesNotHaveIsRefused() throws Exception {
        Fixture fixture = new Fixture("label", "Size", null);
        try {
            fixture.replace("@Size(nope = 1)", "label");
            fail("an unknown parameter must be refused");
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage(), ex.getMessage().contains("has no parameter nope"));
            assertTrue(ex.getMessage(), ex.getMessage().contains("min"));
        }
    }

    @Test
    public void aPlaceholderNothingKnowsIsRefused() throws Exception {
        Fixture fixture = new Fixture("label", "Size", null);
        try {
            fixture.replace("@Size(max = {nope})", "label");
            fail("an unknown placeholder must be refused");
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage(), ex.getMessage().contains("{nope}"));
        }
    }

    @Test
    public void theComputedValueIsWritten() throws Exception {
        Fixture fixture = new Fixture("label", "Size", null);
        fixture.replace("@Size(max = {max})", "label");

        String source = fixture.rendered();
        assertTrue(source, source.contains("@Size(max = 20)"));
        assertTrue(source, !source.contains("min"));
    }

    @Test
    public void aSubstitutedValueIsNotScannedAgain() throws Exception {
        Fixture fixture = new Fixture("code", "Pattern", "[A-Z]{2}");
        fixture.replace("@Pattern(regexp = \"{regexp}\")", "code");

        assertTrue(fixture.rendered(), fixture.rendered().contains("@Pattern(regexp = \"[A-Z]{2}\")"));
    }

    @Test
    public void theClassAndThePropertyAreTheOtherTwoNames() throws Exception {
        Fixture fixture = new Fixture("label", "NotNull", null);
        fixture.replace("@NotNull(message = \"{className}.{fieldName}\")", "label");

        String source = fixture.rendered();
        assertTrue(source, source.contains("@NotNull(message = \"a.RootType.label\")"));
    }

    @Test
    public void aParameterNotWrittenByThePluginFallsBackToItsDefault() throws Exception {
        Fixture fixture = new Fixture("child", "NotNull", null);
        fixture.replace("@NotNull(message = \"{message}\")", "child");

        String source = fixture.rendered();
        assertTrue(source, source.contains("javax.validation.constraints.NotNull.message"));
    }

}
