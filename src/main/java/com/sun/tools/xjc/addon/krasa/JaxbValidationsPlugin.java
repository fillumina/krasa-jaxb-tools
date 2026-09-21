package com.sun.tools.xjc.addon.krasa;

import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.addon.krasa.validations.Processor;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsOptions;
import com.sun.tools.xjc.outline.Outline;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.xml.sax.ErrorHandler;

/**
 * Add <a href="https://jcp.org/en/jsr/detail?id=380">JSR 380 Bean Validation 2.0 Annotations</a>
 * to a  Java™ Architecture for XML Binding (JAXB) XJC generated classes.
 * <br>
 * The plugin is configured in {@code /resources/META-INF/services/com.sun.tools.xjc.Plugin} .
 *
 * @author Francesco Illuminati
 * @author Vojtěch Krása
 * @author cocorossello
 */
public class JaxbValidationsPlugin extends Plugin {

    static final String NAMESPACE = "http://jaxb.dev.java.net/plugin/code-injector";

    public static final String PLUGIN_NAME = "XJsr303Annotations";
    public static final String PLUGIN_OPTION_NAME = "-" + PLUGIN_NAME;
    public static final int PLUGIN_OPTION_NAME_LENGTH = PLUGIN_OPTION_NAME.length() + 1;

    /** The name of the specification this plugin implements, and the second name it answers to. */
    public static final String PLUGIN_ALIAS_NAME = "XBeanValidationAnnotations";
    public static final String PLUGIN_ALIAS_OPTION_NAME = "-" + PLUGIN_ALIAS_NAME;

    ValidationsOptions.Builder optionsBuilder = ValidationsOptions.builder();

    @Override
    public String getOptionName() {
        return PLUGIN_NAME;
    }

    /**
     * Reads one option of this plugin. XJC activates a plugin only for an argument equal to
     * {@code "-"} plus {@link #getOptionName()}, and hands every other argument to every plugin,
     * so the alias has to activate the plugin itself; see {@link #PLUGIN_ALIAS_OPTION_NAME}.
     */
    @Override
    public int parseArgument(Options opt, String[] args, int index)
            throws BadCommandLineException, IOException {
        final String argument = args[index];
        final String canonical = canonicalArgument(argument);
        if (!canonical.equals(argument)) {
            activate(opt);
        }
        return optionsBuilder.parseArgument(canonical);
    }

    /** @return the argument as if it had been written with {@link #PLUGIN_OPTION_NAME}. */
    private static String canonicalArgument(String argument) {
        return argument.startsWith(PLUGIN_ALIAS_OPTION_NAME)
                ? PLUGIN_OPTION_NAME + argument.substring(PLUGIN_ALIAS_OPTION_NAME.length())
                : argument;
    }

    /** Does what XJC does for the canonical name, which it does not do for an alias. */
    private void activate(Options opt) throws BadCommandLineException {
        if (opt.activePlugins.contains(this)) {
            return;
        }
        opt.activePlugins.add(this);
        opt.pluginURIs.addAll(getCustomizationURIs());
        onActivated(opt);
    }

    @Override
    public List<String> getCustomizationURIs() {
        return Collections.singletonList(NAMESPACE);
    }

    @Override
    public boolean isCustomizationTagName(String nsUri, String localName) {
        return nsUri.equals(NAMESPACE) && localName.equals("code");
    }

    @Override
    public String getUsage() {
        return ValidationsArgument.getUsageHelp();
    }

    @Override
    public boolean run(Outline model, Options opt, ErrorHandler errorHandler) {
        optionsBuilder.verbose(opt.verbose);

        ValidationsOptions options = buildOptions();

        options.logActualOptions();

        new Processor(options).process(model);

        return true;
    }

    // used in tests
    public ValidationsOptions buildOptions() {
        return optionsBuilder.build();
    }

}
