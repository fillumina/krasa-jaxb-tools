package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.codemodel.JFieldVar;
import com.sun.tools.xjc.outline.ClassOutline;
import cz.jirutka.validator.collection.constraints.*;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * @author Francesco Illuminati
 */
class FieldAnnotator {

    private static final String FRACTION = "fraction";
    private static final String INTEGER = "integer";
    private static final String MAX = "max";
    private static final String MIN = "min";
    private static final String INCLUSIVE = "inclusive";
    private static final String VALUE = "value";
    private static final String MESSAGE = "message";
    private static final String REGEXP = "regexp";

    private final ValidationsAnnotation annotationFactory;
    private final XjcAnnotator xjcAnnotator;

    public FieldAnnotator(
            JFieldVar field, ValidationsAnnotation annotationFactory, ValidationsLogger log) {
        this.annotationFactory = annotationFactory;
        this.xjcAnnotator = new XjcAnnotator(field, log);
    }

    void addEachSizeAnnotation(Integer minLength, Integer maxLength) {
        if ((minLength != null && minLength != 0) ||
                (maxLength != null && maxLength != 0)) {
            xjcAnnotator.annotate(EachSize.class)
                    .param(MIN, minLength)
                    .param(MAX, maxLength)
                    .log();
        }
    }

    void addEachDigitsAnnotation(Integer totalDigits, Integer fractionDigits) {
        if ((totalDigits != null && totalDigits != 0) ||
                (fractionDigits != null && fractionDigits != 0)) {
            xjcAnnotator.annotate(EachDigits.class)
                    .param(INTEGER, totalDigits, 0)
                    .param(FRACTION, fractionDigits, 0)
                    .log();
        }
    }

    void addEachDecimalMaxAnnotation(BigDecimal maxInclusive, BigDecimal maxExclusive) {
        if (maxInclusive != null || maxExclusive != null) {
            xjcAnnotator.annotate(EachDecimalMax.class)
                    .param(VALUE, maxInclusive)
                    .param(VALUE, maxExclusive)
                    .param(INCLUSIVE, maxInclusive != null)
                    .log();
        }
    }

    void addEachDecimalMinAnnotation(BigDecimal minInclusive, BigDecimal minExclusive) {
        if (minExclusive != null || minInclusive != null) {
            xjcAnnotator.annotate(EachDecimalMin.class)
                    .param(VALUE, minInclusive)
                    .param(VALUE, minExclusive)
                    .param(INCLUSIVE, minInclusive != null)
                    .log();
        }
    }

    void addNotNullAnnotation(ClassOutline classOutline, JFieldVar field, String message) {
        xjcAnnotator.annotate(annotationFactory.getNotNullClass())
                .param(MESSAGE, message)
                .log();
    }

    void addValidAnnotation() {
        xjcAnnotator.annotate(annotationFactory.getValidClass()).log();
    }

    void addSizeAnnotation(Integer minLength, Integer maxLength, Integer length) {
        if (isValidLength(minLength) || isValidLength(maxLength)) {
            xjcAnnotator.annotate(annotationFactory.getSizeClass())
                    .paramIf(isValidLength(minLength), MIN, minLength)
                    .paramIf(isValidLength(maxLength), MAX, maxLength)
                    .log();

        } else if (isValidLength(length)) {
            xjcAnnotator.annotate(annotationFactory.getSizeClass())
                    .param(MIN, length)
                    .param(MAX, length)
                    .log();
        }
    }

    void addDecimalMinAnnotationExclusive(BigDecimal min) {
        addDecimalMinAnnotation(min, true);
    }

    void addDecimalMinAnnotationInclusive(BigDecimal min) {
        addDecimalMinAnnotation(min, false);
    }

    private void addDecimalMinAnnotation(BigDecimal min, boolean exclusive) {
        if (min != null) {
            xjcAnnotator.annotate(annotationFactory.getDecimalMinClass())
                    .param(VALUE, min.toString())
                    .param(INCLUSIVE, !exclusive)
                    .log();
        }
    }

    void addDecimalMaxAnnotationExclusive(BigDecimal max) {
        addDecimalMaxAnnotation(max, true);
    }

    void addDecimalMaxAnnotationInclusive(BigDecimal max) {
        addDecimalMaxAnnotation(max, false);
    }

    private void addDecimalMaxAnnotation(BigDecimal max, boolean exclusive) {
        if (max != null) {
            xjcAnnotator.annotate(annotationFactory.getDecimalMaxClass())
                    .param(VALUE, max.toString())
                    .param(INCLUSIVE, (!exclusive))
                    .log();
        }
    }

    void addDigitsAnnotation(Integer totalDigits, Integer fractionDigits) {
        if (totalDigits != null) {
            xjcAnnotator.annotate(annotationFactory.getDigitsClass())
                    .param(INTEGER, getValueOrZeroOnNull(totalDigits))
                    .param(FRACTION, getValueOrZeroOnNull(fractionDigits))
                    .log();
        }
    }

    void addPatterns(List<List<String>> patterns, boolean multiPattern) {
        addMultiPatternAnnotations(annotationFactory.getPatternClass(), patterns, multiPattern);
    }

    void addEachPatterns(List<List<String>> patterns, boolean multiPattern) {
        addMultiPatternAnnotations(EachPattern.class, patterns, multiPattern);
    }

    private void addMultiPatternAnnotations(
            Class<? extends Annotation> annotation,
            List<List<String>> multiPatterns,
            boolean multiPattern) {
        switch (multiPatterns.size()) {
            case 0:
                // do nothing at all
                break;
            case 1:
                addPatternAnnotations(annotation, multiPatterns.get(0));
                break;
            default:
                if (multiPattern) {
                    multiPatterns.forEach(p -> addPatternAnnotations(annotation, p));
                } else {
                    addPatternListAnnotation(multiPatterns);
                }
        }
    }

    /**
     * Uses @Pattern.List to list all patterns.
     * If a type definition with patterns has a base type with patterns the two different set of
     * patterns are not alternatives (OR) but equally mandatory (AND) so a @Pattern.List
     * must be used.
     * <p>
     * see https://www.w3.org/TR/2011/CR-xmlschema11-2-20110721/datatypes.html#rf-pattern
     */
    void addPatternListAnnotation(List<List<String>> multiPatterns) {
        XjcAnnotator.Annotate.MultipleAnnotation multi = xjcAnnotator
                .annotate(annotationFactory.getPatternListClass())
                .multipleAnnotationContainer(VALUE);

        for (List<String> patterns : multiPatterns) {
            switch (patterns.size()) {
                case 0:
                    // do nothing
                    break;
                case 1:
                    multi.annotate(annotationFactory.getPatternClass())
                            .param(REGEXP, patterns.get(0))
                            .log();
                    break;
                default:
                    String pattern = consolidatePatterns(patterns);
                    multi.annotate(annotationFactory.getPatternClass())
                            .param(REGEXP, pattern)
                            .log();
            }
        }
    }

    private void addPatternAnnotations(
            Class<? extends Annotation> annotation,
            Collection<String> patterns) {
        switch (patterns.size()) {
            case 0:
                // do nothing at all
                break;
            case 1:
                addSinglePatternAnnotation(annotation, patterns.iterator().next());
                break;
            default:
                addPatternAnnotation(annotation, patterns);
        }
    }

    /**
     * Add all the patterns (A, B, C) as options in a single one (A|B|C).
     */
    private void addPatternAnnotation(Class<? extends Annotation> annotation, Collection<String> patterns) {
        String regexp = consolidatePatterns(patterns);
        addSinglePatternAnnotation(annotation, regexp);
    }

    private String consolidatePatterns(Collection<String> patterns) {
        StringBuilder sb = new StringBuilder();
        for (String p : patterns) {
            sb.append("(").append(p).append(")|");
        }
        String regexp = sb.substring(0, sb.length() - 1);
        return regexp;
    }

    private void addSinglePatternAnnotation(Class<? extends Annotation> annotation, String pattern) {
        xjcAnnotator.annotate(annotation)
                .param(REGEXP, pattern)
                .log();
    }

    static boolean isValidLength(Integer length) {
        return length != null && length != -1;
    }

    static Integer getValueOrZeroOnNull(Integer value) {
        return value == null ? Integer.valueOf(0) : value;
    }

}
