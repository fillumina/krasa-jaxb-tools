package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.addon.krasa.JaxbValidationsPlugin;
import static com.sun.tools.xjc.addon.krasa.JaxbValidationsPlugin.PLUGIN_OPTION_NAME;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author Francesco Illuminati
 */
public enum ValidationsArgument {
    targetNamespace(
            // type:
            String.class,
            // help message:
            "adds @Valid annotation to all elements with given namespace.",
            // setter:
            (p, v) -> {
                if (v != null && !v.contains(" ")) {
                    p.targetNamespace(v);
                } else {
                    return "invalid namespace"; // error message
                }
                return null; // OK
            },
            // getter:
            (p) ->  p.getTargetNamespace()),
    generateAllNumericConstraints(
            Boolean.class,
            "adds @DecinalMin and @DecimalMax annotations even if within the range of the java type",
            (p,v) -> setBoolean(v, r -> p.allNumericConstraints(r)),
            (p) -> p.isAllNumericConstraints()),
    generateNotNullAnnotations(
            Boolean.class,
            "adds a @NotNull when an element has minOccours not 0, is required or is not nillable",
            (p,v) -> setBoolean(v, r -> p.notNullAnnotations(r)),
            (p) -> p.isNotNullAnnotations()),
    notNullAnnotationsCustomMessages(
            String.class,
            "allowed values: true/false, 'FieldName', 'ClassName' or an actual message",
            (p,v) -> {
                Boolean b = toBoolean(v);

                if (b != null) {
                    p.notNullCustomMessage(b);
                } else {
                    if (NotNullAnnotationCustomMessageType.Classname.equalsIgnoreCase(v)) {
                        p.notNullCustomMessage(true);
                        p.notNullPrefixFieldName(false);
                        p.notNullPrefixClassName(true);
                        p.notNullCustomMessageText(null);
                    } else if (NotNullAnnotationCustomMessageType.Fieldname.equalsIgnoreCase(v)) {
                        p.notNullCustomMessage(true);
                        p.notNullPrefixFieldName(true);
                        p.notNullPrefixClassName(false);
                        p.notNullCustomMessageText(null);
                    } else if (v.equalsIgnoreCase("false")) {
                        p.notNullCustomMessage(false);
                        p.notNullPrefixFieldName(false);
                        p.notNullPrefixClassName(false);
                        p.notNullCustomMessageText(null);
                    } else {
                        p.notNullCustomMessage(false);
                        p.notNullPrefixFieldName(false);
                        p.notNullPrefixClassName(false);
                        p.notNullCustomMessageText(v);
                    }

                }
                return null;
            },
            (p) -> {
                if (p.isNotNullPrefixFieldName()) {
                    return "FieldName";
                } else if (p.isNotNullPrefixClassName()) {
                    return "ClassName";
                } else if (p.getNotNullCustomMessageText() != null) {
                    return p.getNotNullCustomMessageText();
                } else {
                    return p.isNotNullCustomMessage();
                }
            }),
    verbose(Boolean.class,
            "increases verbosity",
            (p,v) -> setBoolean(v, r -> p.verbose(r)),
            (p) -> p.isVerbose()),
    validationAnnotations(
            String.class,
            "selects which type of annotation to use: " + ValidationsAnnotation.getValuesAsString(),
            (p,v) -> {
                ValidationsAnnotation va;
                try {
                    va = ValidationsAnnotation.valueOf(v.toUpperCase());
                } catch (IllegalArgumentException | NullPointerException ex) {
                    return "passed value is not allowed, use one of: " +
                            ValidationsAnnotation.getValuesAsString();
                }
                p.annotationFactory(va);
                return null;
            },
            (p) -> p.getAnnotationFactory()),
    generateListAnnotations(
            Boolean.class,
            "generates github.com/jirutka/validator-collection annotations",
            (p,v) -> setBoolean(v, r -> p.validationCollection(r)),
            (p) -> p.isValidationCollection()),
    // it's just a placeholder that will be read directly by ValidSEIGenerator
    generateServiceValidationAnnotations(
            String.class,
            "used by cxf-codegen to performs validation on fields annotated with @Valid",
            (p,v) -> {
                if ("in".equalsIgnoreCase(v)) {
                    p.validOut(false);
                } else if ("out".equalsIgnoreCase(v)) {
                    p.validIn(false);
                }
                //log("'" + policy + "' parsed as " + "in = " + validIn + ", out = " + validOut);
                return null;
            }, // read by ValidSEIGenerator
            (p) -> null);

    // parameter type
    private final Class<?> type;

    // help text
    private final String help;

    // set the value into the builder and return null if ok or a text with the error
    private final BiFunction<ValidationsOptions.Builder, String, String> setter;

    // get the value
    private final Function<ValidationsOptions, Object> getter;

    ValidationsArgument(
            Class<?> type,
            String help,
            BiFunction<ValidationsOptions.Builder, String, String> setter,
            Function<ValidationsOptions, Object> getter) {
        this.type = type;
        this.help = help;
        this.setter = setter;
        this.getter = getter;
    }

    String setValue(ValidationsOptions.Builder optionBuilder, String value) {
        return setter.apply(optionBuilder, value);
    }

    Object getValue(ValidationsOptions options) {
        return getter.apply(options);
    }

    String getTypeName() {
        return type.getSimpleName();
    }

    String withValue(String value) {
        return JaxbValidationsPlugin.PLUGIN_OPTION_NAME + ":" + name() + "=" + value;
    }

    String fullOptionName() {
        return JaxbValidationsPlugin.PLUGIN_OPTION_NAME + ":" + name();
    }

    String fullName() {
        return JaxbValidationsPlugin.PLUGIN_NAME + ":" + name();
    }

    public static String getUsageHelp() {
        return new StringBuilder()
                .append("  -")
                .append(PLUGIN_OPTION_NAME)
                .append("      :  ")
                .append("inject Bean validation annotations (JSR 303)")
                .append(System.lineSeparator())
                .append("   Options:")
                .append(helpMessageWithPrefix("     "))
                .append(System.lineSeparator())
                .toString();
    }

    static ValidationsArgument parse(final String name) throws BadCommandLineException {
        ValidationsArgument argument = ValidationsArgument.valueOf(name);
        if (argument == null) {
            throw new BadCommandLineException(JaxbValidationsPlugin.PLUGIN_NAME +
                    " unrecognized option " + name + ", usage:\n" +
                    ValidationsArgument.helpMessageWithPrefix(""));
        }
        return argument;
    }

    /**
     * @param linePrefix a string prefixed to each output line
     * @return a multi line string containing an help for each option.
     */
    public static String helpMessageWithPrefix(String linePrefix) {
        StringBuilder buf = new StringBuilder();
        for (ValidationsArgument a : values()) {
            buf
                    .append(linePrefix)
                    .append(a.name())
                    .append(": ")
                    .append("(")
                    .append(a.type.getSimpleName())
                    .append(") ")
                    .append(a.help)
                    .append(System.lineSeparator());
        }
        return buf.toString();
    }

    public String errorMessage(String wrongValue) {
        return fullName() + " option expected a value of type " + type.getSimpleName() +
                " but got '" + Objects.toString(wrongValue) + "'";
    }

    static String setBoolean(String value, Consumer<Boolean> setter) {
        if (value == null || "".equals(value.trim())) {
            setter.accept(true);
            return null;
        }
        Boolean bool = toBoolean(value);
        if (bool == null) {
            return "argument not valid, must be 'true' or 'false'";
        }
        setter.accept(bool);
        return null;
    }

    /**
     * @return the boolean value of v (no case sensitive) or null otherwise.
     */
    static boolean toBoolean(String v, Boolean defaultIfNull) {
        if (v != null) {
            return toBoolean(v);
        }
        return defaultIfNull != null ? defaultIfNull : false;
    }

    /**
     * @return the boolean value of v (no case sensitive) or null otherwise.
     */
    static Boolean toBoolean(String v) {
        if (v != null) {
            String lc = v.toLowerCase().trim();
            if ("true".equals(lc)) {
                return Boolean.TRUE;
            } else if ("false".equals(lc)) {
                return Boolean.FALSE;
            }
        }
        return null;
    }

}
