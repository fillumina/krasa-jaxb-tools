package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.addon.krasa.JaxbValidationsPlugin;
import java.util.Objects;

/**
 * Set the options using the included builder and then create an immutable option bean to
 * be passed around.
 *
 * @author Francesco Illuminati
 */
public class ValidationsOptions {
    // set default values in Builder not here
    private final String targetNamespace;
    private boolean multiPattern = false;
    private final boolean verbose;
    private final boolean allNumericConstraints;
    private final boolean notNullAnnotations;
    private final boolean notNullCustomMessage;
    private final boolean notNullPrefixFieldName;
    private final boolean notNullPrefixClassName;
    private final String notNullCustomMessageText;
    private final boolean validationCollection;
    private final ValidationsAnnotation annotationFactory;
    private final boolean validIn;
    private final boolean validOut;

    public void logActualOptions() {
        if (verbose) {
            System.out.println(getActualOptionValuesAsString());
        }
    }

    /** @return a multi line string containing the value for each option. */
    private String getActualOptionValuesAsString() {
        String linePrefix = "    ";
        StringBuilder buf = new StringBuilder();
        buf
                .append("[info] ")
                .append(JaxbValidationsPlugin.PLUGIN_NAME)
                .append(" options:")
                .append(System.lineSeparator());

        for (ValidationsArgument a : ValidationsArgument.values()) {
            buf
                    .append(linePrefix)
                    .append(a.name())
                    .append(": ")
                    .append(Objects.toString(a.getValue(this)))
                    .append(System.lineSeparator());
        }

        return buf.toString();
    }

    public String getTargetNamespace() {
        return targetNamespace;
    }

    public boolean isMultiPattern() {
        return multiPattern;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public boolean isAllNumericConstraints() {
        return allNumericConstraints;
    }

    public boolean isNotNullAnnotations() {
        return notNullAnnotations;
    }

    public boolean isNotNullCustomMessage() {
        return notNullCustomMessage;
    }

    public boolean isNotNullPrefixFieldName() {
        return notNullPrefixFieldName;
    }

    public boolean isNotNullPrefixClassName() {
        return notNullPrefixClassName;
    }

    public String getNotNullCustomMessageText() {
        return notNullCustomMessageText;
    }

    public boolean isValidationCollection() {
        return validationCollection;
    }

    public ValidationsAnnotation getAnnotationFactory() {
        return annotationFactory;
    }

    public boolean isValidIn() {
        return validIn;
    }

    public boolean isValidOut() {
        return validOut;
    }

    public static class Builder {
        private String targetNamespace = null;
        private boolean multiPattern = false;
        private boolean verbose = false;
        private boolean allNumericConstraints = false;
        private boolean notNullAnnotations = true;
        private boolean notNullCustomMessage = false;
        private boolean notNullPrefixFieldName = false;
        private boolean notNullPrefixClassName = false;
        private String notNullCustomMessageText = null;
        private boolean validationCollection = false;
        private ValidationsAnnotation annotationFactory = ValidationsAnnotation.JAVAX;
        private boolean validIn = true;
        private boolean validOut = true;

        private Builder() {
        }

        /** @return 1 if the argument is referring to this plugin, 0 otherwise. */
        public int parseArgument(String option) throws BadCommandLineException {
            if (option.startsWith(JaxbValidationsPlugin.PLUGIN_OPTION_NAME)) {
                int idx = option.indexOf("=");
                if (idx != -1) {
                    final String name = option.substring(
                            JaxbValidationsPlugin.PLUGIN_OPTION_NAME_LENGHT, idx);
                    final String value = option.substring(idx + 1);
                    ValidationsArgument argument = ValidationsArgument.parse(name);
                    setValue(argument, value);
                } else if (option.length() > JaxbValidationsPlugin.PLUGIN_OPTION_NAME_LENGHT) {
                    final String name = option.substring(
                            JaxbValidationsPlugin.PLUGIN_OPTION_NAME_LENGHT);
                    ValidationsArgument argument = ValidationsArgument.parse(name);
                    setValue(argument, "true");
                }
                return 1;
            }
            return 0;
        }

        private void setValue(ValidationsArgument argument, final String value)
                throws BadCommandLineException {
            try {
                String error = argument.setValue(this, value);
                if (error != null) {
                    throw new BadCommandLineException(
                            "option " + argument.name() + ": " +
                            (error.length() > 0 ? error + ", " : "") +
                            "cannot accept '" + value + "' as a " + argument.getTypeName());
                }
            } catch (NullPointerException ex) {
                throw new BadCommandLineException(argument.errorMessage(value));
            }
        }

        public Builder targetNamespace(final String value) {
            this.targetNamespace = value;
            return this;
        }

        public Builder multiPattern(final boolean value) {
            this.multiPattern = value;
            return this;
        }

        public Builder verbose(final boolean value) {
            this.verbose = value;
            return this;
        }

        public Builder notNullAnnotations(final boolean value) {
            this.notNullAnnotations = value;
            return this;
        }

        public Builder allNumericConstraints(final boolean value) {
            this.allNumericConstraints = value;
            return this;
        }

        public Builder notNullCustomMessage(final boolean value) {
            this.notNullCustomMessage = value;
            return this;
        }

        public Builder notNullPrefixFieldName(final boolean value) {
            this.notNullPrefixFieldName = value;
            return this;
        }

        public Builder notNullPrefixClassName(final boolean value) {
            this.notNullPrefixClassName = value;
            return this;
        }

        public Builder notNullCustomMessageText(final String value) {
            this.notNullCustomMessageText = value;
            return this;
        }

        public Builder validationCollection(final boolean value) {
            this.validationCollection = value;
            return this;
        }

        public Builder annotationFactory(final ValidationsAnnotation value) {
            this.annotationFactory = value;
            return this;
        }

        public Builder validIn(final boolean value) {
            this.validIn = value;
            return this;
        }

        public Builder validOut(final boolean value) {
            this.validOut = value;
            return this;
        }

        public ValidationsOptions build() {
            return new com.sun.tools.xjc.addon.krasa.validations.ValidationsOptions(targetNamespace, multiPattern,
                    verbose, allNumericConstraints, notNullAnnotations, notNullCustomMessage,
                    notNullPrefixFieldName, notNullPrefixClassName, notNullCustomMessageText,
                    validationCollection, annotationFactory, validIn, validOut);
        }
    }

    public static ValidationsOptions.Builder builder() {
        return new ValidationsOptions.Builder();
    }

    private ValidationsOptions(final String targetNamespace, final boolean multiPattern, final boolean verbose,
            final boolean allNumericConstraints, final boolean notNullAnnotations,
            final boolean notNullCustomMessage, final boolean notNullPrefixFieldName,
            final boolean notNullPrefixClassName, final String notNullCustomMessageText,
            final boolean validationCollection,
            final ValidationsAnnotation annotationFactory,
            final boolean validIn,
            final boolean validOut) {
        this.targetNamespace = targetNamespace;
        this.multiPattern = multiPattern;
        this.verbose = verbose;
        this.allNumericConstraints = allNumericConstraints;
        this.notNullAnnotations = notNullAnnotations;
        this.notNullCustomMessage = notNullCustomMessage;
        this.notNullPrefixFieldName = notNullPrefixFieldName;
        this.notNullPrefixClassName = notNullPrefixClassName;
        this.notNullCustomMessageText = notNullCustomMessageText;
        this.validationCollection = validationCollection;
        this.annotationFactory = annotationFactory;
        this.validIn = validIn;
        this.validOut = validOut;
    }

}
