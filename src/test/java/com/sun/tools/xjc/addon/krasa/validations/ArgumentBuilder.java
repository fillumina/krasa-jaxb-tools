package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.tools.xjc.addon.krasa.JaxbValidationsPlugin;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francesco Illuminati
 */
public class ArgumentBuilder {

    public static ArgumentBuilder builder() {
        return new ArgumentBuilder();
    }

    private final List<String> list = new ArrayList<>();

    public ArgumentBuilder() {
        list.add(JaxbValidationsPlugin.PLUGIN_OPTION_NAME);
    }

    public ArgumentBuilder add(String argument) {
        list.add(argument);
        return this;
    }

    public ArgumentBuilder add(ValidationsArgument argument, Object value) {
        list.add(argument.withValue(java.util.Objects.toString(value)));
        return this;
    }

    public ArgumentBuilder addWithoutValue(ValidationsArgument argument) {
        list.add(argument.fullOptionName());
        return this;
    }

    public List<String> getOptionList() {
        return list;
    }

}
