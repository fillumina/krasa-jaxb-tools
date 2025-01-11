package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.addon.krasa.JaxbValidationsPlugin;
import java.io.IOException;
import java.util.List;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class ValidationsArgumentTest {

    private static final String NAMESPACE = "xyz";
    private static final String ANNOTATION = "javax";
    private static final String MESSAGE = "custom message";

    @Test
    public void shouldParseArguments() throws BadCommandLineException, IOException {
        JaxbValidationsPlugin plugin = new JaxbValidationsPlugin();

        List<String> arguments = ArgumentBuilder.builder()
                .add(ValidationsArgument.targetNamespace, NAMESPACE)
                .add(ValidationsArgument.generateListAnnotations, true)
                .add(ValidationsArgument.validationAnnotations, ANNOTATION)
                .add(ValidationsArgument.generateNotNullAnnotations, true)
                .add(ValidationsArgument.generateServiceValidationAnnotations, true)
                .add(ValidationsArgument.notNullAnnotationsCustomMessages, MESSAGE)
                .add(ValidationsArgument.verbose, true)
                .getOptionList();

        String[] args = arguments.toArray(new String[arguments.size()]);
        Options opt = new Options();

        for (int i=0; i<args.length; i++) {
            plugin.parseArgument(opt, args, i);
        }

        ValidationsOptions options = plugin.buildOptions();

        assertEquals(NAMESPACE, options.getTargetNamespace());
        assertEquals(ValidationsAnnotation.JAVAX, options.getAnnotationFactory());
        assertTrue(options.isNotNullAnnotations());
        assertTrue(options.isValidationCollection());
        assertEquals(options.getNotNullCustomMessageText(), MESSAGE);
        assertTrue(options.isVerbose());

    }

    @Test
    public void shouldNotNullFlagsBeFalseByDefault() {
        JaxbValidationsPlugin plugin = new JaxbValidationsPlugin();
        ValidationsOptions options = plugin.buildOptions();

        assertFalse(options.isNotNullCustomMessage());
        assertFalse(options.isNotNullPrefixFieldName());
        assertFalse(options.isNotNullPrefixClassName());
    }

    @Test
    public void shouldSetNotNullAnnotationsCustomMessagesOnClassName()
            throws BadCommandLineException, IOException {

        String option = NotNullAnnotationCustomMessageType.Classname.value();

        JaxbValidationsPlugin plugin = setupNotNullMessage(option);
        ValidationsOptions options = plugin.buildOptions();

        assertTrue(options.isNotNullCustomMessage());
        assertFalse(options.isNotNullPrefixFieldName());
        assertTrue(options.isNotNullPrefixClassName());
    }

    @Test
    public void shouldSetNotNullAnnotationsCustomMessagesOnFieldName()
            throws BadCommandLineException, IOException {

        String option = NotNullAnnotationCustomMessageType.Fieldname.value();

        JaxbValidationsPlugin plugin = setupNotNullMessage(option);
        ValidationsOptions options = plugin.buildOptions();

        assertTrue(options.isNotNullCustomMessage());
        assertTrue(options.isNotNullPrefixFieldName());
        assertFalse(options.isNotNullPrefixClassName());
    }

    private JaxbValidationsPlugin setupNotNullMessage(String option)
            throws IOException, BadCommandLineException {
        JaxbValidationsPlugin plugin = new JaxbValidationsPlugin();

        List<String> arguments = ArgumentBuilder.builder()
                .add(ValidationsArgument.notNullAnnotationsCustomMessages, option)
                .getOptionList();

        String[] args = arguments.toArray(new String[arguments.size()]);
        Options opt = new Options();

        for (int i=0; i<args.length; i++) {
            plugin.parseArgument(opt, args, i);
        }

        plugin.buildOptions();
        return plugin;
    }

    @Test
    public void shouldConvertToBooleanWithDefault() {
        assertTrue(ValidationsArgument.toBoolean("True", false));
        assertTrue(ValidationsArgument.toBoolean("true", false));
        assertTrue(ValidationsArgument.toBoolean("TRUE", false));
        assertTrue(ValidationsArgument.toBoolean(" true", false));
        assertTrue(ValidationsArgument.toBoolean("true ", false));
        assertTrue(ValidationsArgument.toBoolean(" true ", false));

        assertFalse(ValidationsArgument.toBoolean("False", true));
        assertFalse(ValidationsArgument.toBoolean("false", true));
        assertFalse(ValidationsArgument.toBoolean("FALSE", true));
        assertFalse(ValidationsArgument.toBoolean("FalSe", true));
    }

    @Test
    public void shouldConvertToBoolean() {
        assertTrue(ValidationsArgument.toBoolean("True"));
        assertTrue(ValidationsArgument.toBoolean("true"));
        assertTrue(ValidationsArgument.toBoolean("TRUE"));
        assertTrue(ValidationsArgument.toBoolean(" true"));
        assertTrue(ValidationsArgument.toBoolean("true "));
        assertTrue(ValidationsArgument.toBoolean(" true "));

        assertFalse(ValidationsArgument.toBoolean("False"));
        assertFalse(ValidationsArgument.toBoolean("false"));
        assertFalse(ValidationsArgument.toBoolean("FALSE"));
        assertFalse(ValidationsArgument.toBoolean("FalSe"));
    }

}
