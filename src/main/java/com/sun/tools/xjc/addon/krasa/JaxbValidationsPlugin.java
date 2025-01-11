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
    public static final int PLUGIN_OPTION_NAME_LENGHT = PLUGIN_OPTION_NAME.length() + 1;

    ValidationsOptions.Builder optionsBuilder = ValidationsOptions.builder();

    @Override
    public String getOptionName() {
        return PLUGIN_NAME;
    }

    @Override
    public int parseArgument(Options opt, String[] args, int index)
            throws BadCommandLineException, IOException {
        return optionsBuilder.parseArgument(args[index]);
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
