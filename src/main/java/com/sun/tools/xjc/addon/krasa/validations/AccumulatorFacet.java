package com.sun.tools.xjc.addon.krasa.validations;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AccumulatorFacet extends AbstractFacet {

    public final static AccumulatorFacet EMPTY = new AccumulatorFacet() {
        @Override
        public void apply(AbstractFacet facet) {}
    };

    private AccumulatorFacet itemFacet;

    private Integer minLength;
    private Integer maxLength;
    private Integer length;
    private Integer totalDigits;
    private Integer fractionDigits;
    private BigDecimal minInclusive;
    private BigDecimal minExclusive;
    private BigDecimal maxInclusive;
    private BigDecimal maxExclusive;
    private String pattern;
    private LinkedHashSet<String> patternList;
    private String enumeration;
    private LinkedHashSet<String> enumerationList;

    private final LinkedHashSet<LinkedHashSet<String>> multiPatterns = new LinkedHashSet<>();
    private final LinkedHashSet<String> multiEnumerations = new LinkedHashSet<>();

    public void apply(AbstractFacet facet) {
        Integer minLength = facet.minLength();
        if (minLength != null) {
            this.minLength = minLength;
        }

        Integer maxLength = facet.maxLength();
        if (maxLength != null) {
            this.maxLength = maxLength;
        }

        Integer length = facet.length();
        if (length != null) {
            this.length = length;
        }

        Integer totalDigits = facet.totalDigits();
        if (totalDigits != null) {
            this.totalDigits = totalDigits;
        }

        Integer fractionDigits = facet.fractionDigits();
        if (fractionDigits != null) {
            this.fractionDigits = fractionDigits;
        }

        BigDecimal minInclusive = facet.minInclusive();
        if (minInclusive != null) {
            this.minInclusive = minInclusive;
        }

        BigDecimal minExclusive = facet.minExclusive();
        if (minExclusive != null) {
            this.minExclusive = minExclusive;
        }

        BigDecimal maxInclusive = facet.maxInclusive();
        if (maxInclusive != null) {
            this.maxInclusive = maxInclusive;
        }

        BigDecimal maxExclusive = facet.maxExclusive();
        if (maxExclusive != null) {
            this.maxExclusive  = maxExclusive;
        }

        String pattern = facet.pattern();
        if (pattern != null) {
            this.pattern = pattern;
        }

        LinkedHashSet<String> patternList = facet.patternList();
        if (patternList != null) {
            this.patternList = patternList;
        }

        String enumeration = facet.enumeration();
        if (enumeration != null) {
            this.enumeration = enumeration;
        }

        LinkedHashSet<String> enumerationList = facet.enumerationList();
        if (enumerationList != null) {
            this.enumerationList = enumerationList;
        }

        Utils.addIfNotNullOrEmpty(multiPatterns, facet.getPatterns(), Collection::isEmpty);
        Utils.addAllIfNotNullOrEmpty(multiEnumerations, facet.getEnumerations(), String::isEmpty);
    }

    public AccumulatorFacet createItemFacet() {
        if (itemFacet == null) {
            itemFacet = new AccumulatorFacet();
        }
        return itemFacet;
    }

    public AccumulatorFacet getItemFacet() {
        return itemFacet;
    }

    public LinkedHashSet<LinkedHashSet<String>> getMultiPatterns() {
        return multiPatterns;
    }

    public LinkedHashSet<String> getMultiEnumerations() {
        return multiEnumerations;
    }

    @Override
    public Integer minLength() {
        return this.minLength;
    }

    @Override
    public Integer maxLength() {
        return this.maxLength;
    }

    @Override
    public Integer length() {
        return this.length;
    }

    @Override
    public Integer totalDigits() {
        return totalDigits;
    }

    @Override
    public Integer fractionDigits() {
        return fractionDigits;
    }

    @Override
    public BigDecimal minInclusive() {
        return minInclusive;
    }

    @Override
    public BigDecimal minExclusive() {
        return minExclusive;
    }

    @Override
    public BigDecimal maxInclusive() {
        return maxInclusive;
    }

    @Override
    public BigDecimal maxExclusive() {
        return maxExclusive;
    }

    @Override
    public String pattern() {
        return pattern;
    }

    @Override
    public LinkedHashSet<String> patternList() {
        return patternList;
    }

    @Override
    public String enumeration() {
        return enumeration;
    }

    @Override
    public LinkedHashSet<String> enumerationList() {
        return enumerationList;
    }

}
