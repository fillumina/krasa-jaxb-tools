package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.codemodel.JFieldVar;
import cz.jirutka.validator.collection.constraints.EachDecimalMax;
import cz.jirutka.validator.collection.constraints.EachDecimalMin;
import cz.jirutka.validator.collection.constraints.EachDigits;
import cz.jirutka.validator.collection.constraints.EachPattern;
import cz.jirutka.validator.collection.constraints.EachSize;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The replacement of an {@code exclude} statement: one of the annotations this plugin manages,
 * written in place of the annotations it would have computed for a class or a property.
 *
 * <p>
 * The parameters are written as plain text, where {@code {name}} stands for something the plugin
 * knows: {@code {className}} and {@code {fieldName}} for the declaration, the value the plugin was
 * about to write for that parameter, or the default the annotation itself declares for a parameter
 * the plugin does not write.
 *
 * @author Francesco Illuminati
 */
class Replacement {

    private static final Pattern ANNOTATION =
            Pattern.compile("^@([A-Za-z][A-Za-z0-9]*)\\s*(?:\\((.*)\\))?$", Pattern.DOTALL);
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{([A-Za-z][A-Za-z0-9]*)\\}");

    /** @return the annotations a replacement may name, by simple name. */
    private static Map<String, Class<? extends Annotation>> managed(ValidationsAnnotation factory) {
        Map<String, Class<? extends Annotation>> managed = new LinkedHashMap<>();
        managed.put("Valid", factory.getValidClass());
        managed.put("NotNull", factory.getNotNullClass());
        managed.put("Size", factory.getSizeClass());
        managed.put("Digits", factory.getDigitsClass());
        managed.put("DecimalMin", factory.getDecimalMinClass());
        managed.put("DecimalMax", factory.getDecimalMaxClass());
        managed.put("Pattern", factory.getPatternClass());
        managed.put("EachSize", EachSize.class);
        managed.put("EachPattern", EachPattern.class);
        managed.put("EachDigits", EachDigits.class);
        managed.put("EachDecimalMin", EachDecimalMin.class);
        managed.put("EachDecimalMax", EachDecimalMax.class);
        return managed;
    }

    static Replacement parse(String text, ValidationsAnnotation factory) {
        Matcher matcher = ANNOTATION.matcher(text.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("a replacement is one annotation, as in "
                    + "@Size(min = 1): " + text);
        }
        String name = matcher.group(1);
        Map<String, Class<? extends Annotation>> managed = managed(factory);
        Class<? extends Annotation> annotationClass = managed.get(name);
        if (annotationClass == null) {
            throw new IllegalArgumentException("@" + name + " is not one of the annotations this "
                    + "plugin manages: " + managed.keySet());
        }
        return new Replacement(text.trim(), annotationClass, parameters(matcher.group(2)));
    }

    /** @return the parameters as written, values still quoted. */
    private static Map<String, String> parameters(String text) {
        Map<String, String> parameters = new LinkedHashMap<>();
        if (text == null) {
            return parameters;
        }
        int index = 0;
        while (index < text.length()) {
            while (index < text.length() && (Character.isWhitespace(text.charAt(index)) || text.charAt(index) == ',')) {
                index++;
            }
            if (index >= text.length()) {
                break;
            }
            int nameStart = index;
            while (index < text.length() && text.charAt(index) != '=') {
                index++;
            }
            if (index >= text.length()) {
                throw new IllegalArgumentException("parameter without a value: "
                        + text.substring(nameStart).trim());
            }
            String name = text.substring(nameStart, index).trim();
            index++;
            while (index < text.length() && Character.isWhitespace(text.charAt(index))) {
                index++;
            }
            String value;
            if (index < text.length() && text.charAt(index) == '"') {
                int valueStart = index++;
                while (index < text.length() && text.charAt(index) != '"') {
                    index += text.charAt(index) == '\\' ? 2 : 1;
                }
                if (index >= text.length()) {
                    throw new IllegalArgumentException("unterminated string in " + text);
                }
                value = text.substring(valueStart, ++index);
            } else {
                int valueStart = index;
                while (index < text.length() && text.charAt(index) != ',') {
                    index++;
                }
                value = text.substring(valueStart, index).trim();
            }
            parameters.put(name, value);
        }
        return parameters;
    }

    private final String text;
    private final Class<? extends Annotation> annotationClass;
    private final Map<String, String> parameters;

    private Replacement(String text, Class<? extends Annotation> annotationClass,
            Map<String, String> parameters) {
        this.text = text;
        this.annotationClass = annotationClass;
        this.parameters = parameters;
    }

    /**
     * Writes the annotation on the field, resolving what the text asks for against the annotations
     * the plugin computed for it.
     */
    void writeInto(JFieldVar field, String className, String propertyName,
            List<XjcAnnotator.Annotate> computed, ValidationsLogger logger) {
        XjcAnnotator.Annotate annotation = new XjcAnnotator(field, logger).annotate(annotationClass);
        for (Map.Entry<String, String> parameter : parameters.entrySet()) {
            String name = parameter.getKey();
            String value = resolveValue(parameter.getValue(), annotationClass, className, propertyName,
                    computed);
            writeParameter(annotation, annotationClass, name, value);
        }
        annotation.log();
    }

    /**
     * Writes the annotation the plugin computed, with the parameters a statement set on top of them.
     */
    static void writeComputed(JFieldVar field, XjcAnnotator.Annotate computed,
            Map<String, String> overrides, ValidationsLogger logger) {
        Class<? extends Annotation> type = computed.getAnnotationClass();
        XjcAnnotator.Annotate annotation = new XjcAnnotator(field, logger).annotate(type);
        Map<String, String> parameters = new LinkedHashMap<>(computed.getParameters());
        parameters.putAll(overrides);
        for (Map.Entry<String, String> parameter : parameters.entrySet()) {
            writeParameter(annotation, type, parameter.getKey(), parameter.getValue());
        }
        annotation.log();
    }

    /** Writes one parameter as the annotation declares it, so numbers stay numbers. */
    static void writeParameter(XjcAnnotator.Annotate annotation,
            Class<? extends Annotation> annotationType, String name, String value) {
        Class<?> type = parameterType(annotationType, name);
        if (type == String.class) {
            annotation.param(name, unquote(value));
        } else if (type == Integer.class || type == int.class) {
            annotation.param(name, Integer.valueOf(value));
        } else if (type == Boolean.class || type == boolean.class) {
            annotation.param(name, Boolean.valueOf(value));
        } else if (type == BigDecimal.class || type == Long.class || type == long.class) {
            annotation.param(name, new BigDecimal(value).toString());
        } else {
            throw new IllegalArgumentException("@" + annotationType.getSimpleName() + "." + name
                    + " is of a type this plugin cannot write: " + type.getSimpleName());
        }
    }

    static Class<?> parameterType(Class<? extends Annotation> annotationType, String name) {
        try {
            return annotationType.getMethod(name).getReturnType();
        } catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException("@" + annotationType.getSimpleName()
                    + " has no parameter " + name + ", it has " + parameterNames(annotationType));
        }
    }

    /** @return the value with every placeholder replaced, in one pass. */
    static String resolveValue(String value, Class<? extends Annotation> annotationClass,
            String className, String propertyName, List<XjcAnnotator.Annotate> computed) {
        Matcher matcher = PLACEHOLDER.matcher(value);
        StringBuilder resolved = new StringBuilder();
        int end = 0;
        while (matcher.find()) {
            resolved.append(value, end, matcher.start());
            resolved.append(valueOf(matcher.group(1), annotationClass, className, propertyName, computed));
            end = matcher.end();
        }
        return resolved.append(value.substring(end)).toString();
    }

    private static String valueOf(String name, Class<? extends Annotation> annotationClass,
            String className, String propertyName, List<XjcAnnotator.Annotate> computed) {
        if ("className".equals(name)) {
            return className;
        }
        if ("fieldName".equals(name)) {
            return propertyName;
        }
        String found = null;
        List<String> where = new ArrayList<>();
        for (XjcAnnotator.Annotate annotation : computed) {
            String value = annotation.getParameters().get(name);
            if (value != null) {
                where.add("@" + annotation.getAnnotationClass().getSimpleName());
                if (found == null) {
                    found = value;
                } else if (!found.equals(value)) {
                    throw new IllegalArgumentException("{" + name + "} is " + found + " in " + where
                            + " but " + value + " in @"
                            + annotation.getAnnotationClass().getSimpleName()
                            + ": name the annotation you mean");
                }
            }
        }
        if (found != null) {
            return found;
        }
        Object defaultValue = defaultValue(annotationClass, name);
        if (defaultValue != null) {
            return String.valueOf(defaultValue);
        }
        throw new IllegalArgumentException("{" + name + "} is neither a parameter of "
                + computedNames(computed) + " nor a parameter of @" + annotationClass.getSimpleName()
                + " (" + parameterNames(annotationClass) + "), and it is not className or fieldName");
    }

    private static Object defaultValue(Class<? extends Annotation> annotationClass, String name) {
        try {
            return annotationClass.getMethod(name).getDefaultValue();
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    private static List<String> parameterNames(Class<? extends Annotation> annotationType) {
        List<String> names = new ArrayList<>();
        for (Method method : annotationType.getDeclaredMethods()) {
            names.add(method.getName());
        }
        return names;
    }

    private static String computedNames(List<XjcAnnotator.Annotate> computed) {
        List<String> names = new ArrayList<>();
        for (XjcAnnotator.Annotate annotation : computed) {
            names.add("@" + annotation.getAnnotationClass().getSimpleName());
        }
        return names.toString();
    }

    private static String unquote(String value) {
        if (value.length() > 1 && value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1).replace("\\\"", "\"").replace("\\\\", "\\");
        }
        return value;
    }

    @Override
    public String toString() {
        return text;
    }
}
