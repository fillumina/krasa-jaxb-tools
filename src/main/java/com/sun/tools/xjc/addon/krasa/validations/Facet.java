package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSSimpleType;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Francesco Illuminati
 */
class Facet {
    private final XSSimpleType simpleType;

    Facet(XSSimpleType simpleType) {
        this.simpleType = simpleType;
    }

    Integer minLength() {
        return getIntegerFacet(XSFacet.FACET_MINLENGTH);
    }

    Integer maxLength() {
        return getIntegerFacet(XSFacet.FACET_MAXLENGTH);
    }

    Integer length() {
        return getIntegerFacet(XSFacet.FACET_LENGTH);
    }

    Integer totalDigits() {
        return getIntegerFacet(XSFacet.FACET_TOTALDIGITS);
    }

    Integer fractionDigits() {
        return getIntegerFacet(XSFacet.FACET_FRACTIONDIGITS);
    }

    BigDecimal minInclusive() {
        return getDecimalFacet(XSFacet.FACET_MININCLUSIVE);
    }

    BigDecimal minExclusive() {
        return getDecimalFacet(XSFacet.FACET_MINEXCLUSIVE);
    }

    BigDecimal maxInclusive() {
        return getDecimalFacet(XSFacet.FACET_MAXINCLUSIVE);
    }

    BigDecimal maxExclusive() {
        return getDecimalFacet(XSFacet.FACET_MAXEXCLUSIVE);
    }

    String pattern() {
        return getStringFacet(XSFacet.FACET_PATTERN);
    }

    LinkedHashSet<String> patternList() {
        return getMultipleStringFacets(XSFacet.FACET_PATTERN);
    }

    String enumeration() {
        return getStringFacet(XSFacet.FACET_ENUMERATION);
    }

    LinkedHashSet<String> enumerationList() {
        return getMultipleStringFacets(XSFacet.FACET_ENUMERATION);
    }

    private LinkedHashSet<String> getMultipleStringFacets(String param) {
        final List<XSFacet> facets = simpleType.getFacets(param);
        if (facets != null) {
            return facets.stream()
                    .map(facet -> facet.getValue().value)
                    .filter(v -> v != null && !v.isEmpty())
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        return new LinkedHashSet<>();
    }

    private String getStringFacet(String param) {
        final XSFacet facet = simpleType.getFacet(param);
        return facet == null ? null : facet.getValue().value;
    }

    private Integer getIntegerFacet(String param) {
        final XSFacet facet = simpleType.getFacet(param);
        if (facet != null) {
            try {
                return Integer.valueOf(facet.getValue().value);
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        return null;
    }

    private BigDecimal getDecimalFacet(String param) {
        final XSFacet facet = simpleType.getFacet(param);
        if (facet != null) {
            final String str = facet.getValue().value;
            try {
                return new BigDecimal(str);
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        return null;
    }

}
