package com.sun.tools.xjc.addon.krasa.validations;

import java.math.BigDecimal;
import java.util.LinkedHashSet;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractFacet {

    public abstract Integer minLength();

    public abstract Integer maxLength();

    public abstract Integer length();

    public abstract Integer totalDigits();

    public abstract Integer fractionDigits();

    public abstract BigDecimal minInclusive();

    public abstract BigDecimal minExclusive();

    public abstract BigDecimal maxInclusive();

    public abstract BigDecimal maxExclusive();

    public abstract String pattern();

    public abstract LinkedHashSet<String> patternList();

    public abstract String enumeration();

    public abstract LinkedHashSet<String> enumerationList();

    public LinkedHashSet<String> getPatterns() {
        final LinkedHashSet<String> patterns = patternList();
        final String pattern = pattern();
        Utils.addIfNotNullOrEmpty(patterns, pattern, String::isEmpty);
        if (patterns != null && !patterns.isEmpty()) {
            return patterns.stream()
                    .filter(p -> Utils.isValidRegexp(p))
                    .map(p -> Utils.replaceRegexp(p))
                    .collect(LinkedHashSet::new, LinkedHashSet::add, LinkedHashSet::addAll);
        }
        return patterns;
    }

    public LinkedHashSet<String> getEnumerations() {
        final LinkedHashSet<String> enumerations = enumerationList();
        final String enumeration = enumeration();
        Utils.addIfNotNullOrEmpty(enumerations, enumeration, String::isEmpty);
        if (enumerations != null && !enumerations.isEmpty()) {
            return enumerations.stream()
                    .filter(p -> p != null && !p.isEmpty())
                    .map(p -> Utils.escapeRegexp(p))
                    .collect(LinkedHashSet::new, LinkedHashSet::add, LinkedHashSet::addAll);
        }
        return enumerations;
    }

}
