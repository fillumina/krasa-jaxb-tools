package com.sun.tools.xjc.addon.krasa.validations;

import java.util.Collection;
import java.util.function.Predicate;

/**
 *
 * @author Francesco Illuminati
 */
class Utils {

    static boolean isEqualsOrNull(String optionsNamespace, String actualTargetNamespace) {
        if (optionsNamespace == null ||
                optionsNamespace.isEmpty() ||
                "null".equals(optionsNamespace)) {
            return true;
        }
        return actualTargetNamespace.startsWith(optionsNamespace);
    }

    static <C extends Collection<T>, T> void addIfNotNullOrEmpty(C collection, T item, Predicate<T> isEmpty) {
        if (item != null && !isEmpty.test(item)) {
            collection.add(item);
        }
    }

    static <T> void addAllIfNotNullOrEmpty(Collection<T> dest, Collection<T> source, Predicate<T> isEmpty) {
        if (source != null && !source.isEmpty()) {
            for (T t : source) {
                if (t != null && !isEmpty.test(t)) {
                    dest.add(t);
                }
            }
        }
    }

    /*
     * \Q indicates begin of quoted regex text, \E indicates end of quoted regex text
     */
    static String escapeRegexp(String pattern) {
        return java.util.regex.Pattern.quote(pattern);
    }

    // cxf-codegen fix
    static boolean isValidRegexp(String pattern) {
        return pattern != null && !"\\c+".equals(pattern);
    }

    static String replaceRegexp(String pattern) {
        return pattern
                .replace("\\i", "[_:A-Za-z]")
                .replace("\\c", "[-._:A-Za-z0-9]")
                .replace("{IsBasicLatin}", "{InBasicLatin}");
    }


}
