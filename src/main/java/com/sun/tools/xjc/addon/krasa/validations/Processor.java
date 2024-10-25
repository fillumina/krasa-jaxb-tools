package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.codemodel.JFieldVar;
import com.sun.tools.xjc.model.CAttributePropertyInfo;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CValuePropertyInfo;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSRestrictionSimpleType;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.impl.AttributeUseImpl;
import com.sun.xml.xsom.impl.ElementDecl;
import com.sun.xml.xsom.impl.SimpleTypeImpl;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Francesco Illuminati
 */
public class Processor {

    private final ValidationsOptions options;

    public Processor(ValidationsOptions options) {
        this.options = options;
    }

    public void process(Outline model) {
        for (ClassOutline classOutline : model.getClasses()) {
            String className = classOutline.implClass.name();
            List<CPropertyInfo> properties = classOutline.target.getProperties();

            for (CPropertyInfo property : properties) {

                String propertyName = property.getName(false);

                ValidationsLogger logger = options.isVerbose()
                        ? new SystemOutValidationsLogger(className, propertyName)
                        : SilentValidationLogger.INSTANCE;

                new TypeProcessor(classOutline, logger)
                        .processProperty(property);

            }
        }
    }

    class TypeProcessor {

        private final ValidationsLogger logger;
        private final ClassOutline classOutline;

        public TypeProcessor(ClassOutline classOutline, ValidationsLogger logger) {
            this.logger = logger;
            this.classOutline = classOutline;
        }

        public void processProperty(CPropertyInfo property) {
            if (property instanceof CElementPropertyInfo) {
                processElement((CElementPropertyInfo) property);

            } else if (property instanceof CAttributePropertyInfo) {
                processAttribute((CAttributePropertyInfo) property);

            } else if (property instanceof CValuePropertyInfo) {
                processAttribute((CValuePropertyInfo) property);
            }
        }

        /**
         * parses xsd:element
         */
        private void processElement(CElementPropertyInfo property) {
            String propertyName = property.getName(false);

            XSParticle particle = (XSParticle) property.getSchemaComponent();
            XSTerm term = particle.getTerm();
            if (!(term instanceof ElementDecl)) {
                return;
            }
            ElementDecl element = (ElementDecl) term;

            final int minOccurs = particle.getMinOccurs().intValue();
            final int maxOccurs = particle.getMaxOccurs().intValue();
            final boolean required = property.isRequired() || property.isCollectionRequired();
            final boolean nillable = element.isNillable() || property.isCollectionNillable();
            final String targetNamespace = element.getOwnerSchema().getTargetNamespace();

            final JFieldVar field = classOutline.implClass.fields().get(propertyName);
            final XSType elementType = element.getType();
            final boolean isComplexType = !(elementType instanceof XSSimpleType);

            FieldAnnotator annotator =
                    new FieldAnnotator(field, options.getAnnotationFactory(), logger);

            // minOccurs > 0 and required == false means the attribute is part of a <xsd:choice>
            // and @NotNull should not be added so only required quilifies to add @NotNull
            if (options.isNotNullAnnotations() && !nillable && required) {
                String message = notNullMessage(classOutline, field);
                annotator.addNotNullAnnotation(classOutline, field, message);
            }

            if ((property.isCollection() || isComplexType) &&
                    isEqualsOrNull(options.getTargetNamespace(),
                            targetNamespace))  {
                annotator.addValidAnnotation();
            }

            if (property.isCollection() && (maxOccurs != 0 || minOccurs != 0)) {
                // http://www.dimuthu.org/blog/2008/08/18/xml-schema-nillabletrue-vs-minoccurs0/comment-page-1/
                annotator.addSizeAnnotation(minOccurs, maxOccurs, null);
            }

            // using https://github.com/jirutka/validator-collection to annotate Lists of primitives
            final XSSimpleType simpleType;
            if (isComplexType) {
                simpleType = elementType.getBaseType().asSimpleType();
            } else {
                simpleType = elementType.asSimpleType();
            }

            if (simpleType != null) {
                Facet facet = new Facet(simpleType);
                FieldHelper fieldHelper = new FieldHelper(field);

                // if it's a complexyType it might add a facet referring to only one of the possibilities
                if (!isComplexType && options.isValidationCollection() && property.isCollection()) {
                    annotator.addEachSizeAnnotation(facet.minLength(), facet.maxLength());
                    annotator.addEachDigitsAnnotation(facet.totalDigits(), facet.fractionDigits());
                    annotator.addEachDecimalMinAnnotation(facet.minInclusive(), facet.minExclusive());
                    annotator.addEachDecimalMaxAnnotation(facet.maxInclusive(), facet.maxExclusive());
                }

                processType(simpleType, fieldHelper, annotator, facet);
            }
        }

        /**
         * parses xsd:attribute
         */
        private void processAttribute(CAttributePropertyInfo property) {
            String propertyName = property.getName(false);

            XSComponent definition = property.getSchemaComponent();
            if (definition instanceof AttributeUseImpl) {
                AttributeUseImpl particle = (AttributeUseImpl) definition;
                XSSimpleType type = particle.getDecl().getType();

                JFieldVar field = classOutline.implClass.fields().get(propertyName);

                if (field != null) {
                    FieldAnnotator annotator =
                            new FieldAnnotator(field, options.getAnnotationFactory(), logger);

                    if (particle.isRequired()) {
                        String message = notNullMessage(classOutline, field);
                        annotator.addNotNullAnnotation(classOutline, field, message);
                    }

                    processType(type, field, annotator);
                }
            }
        }

        /**
         * parses values
         *
         * NOTE: needed to process complexTypes extending a simpleType
         */
        private void processAttribute(CValuePropertyInfo property) {
            String propertyName = property.getName(false);

            XSComponent definition = property.getSchemaComponent();
            if (definition instanceof SimpleTypeImpl) {
                SimpleTypeImpl particle = (SimpleTypeImpl) definition;
                XSSimpleType simpleType = particle.asSimpleType();

                JFieldVar field = classOutline.implClass.fields().get(propertyName);

                if (field != null) {
                    FieldAnnotator annotator =
                            new FieldAnnotator(field, options.getAnnotationFactory(), logger);

                    processType(simpleType, field, annotator);
                }
            }
        }

        private void processType(XSSimpleType simpleType, JFieldVar field, FieldAnnotator annotator) {
            Facet facet = new Facet(simpleType);
            FieldHelper fieldHelper = new FieldHelper(field);
            processType(simpleType, fieldHelper, annotator, facet);
        }

        private void processType(
                XSSimpleType simpleType,
                FieldHelper fieldHelper,
                FieldAnnotator annotator,
                Facet facet) {

            if (fieldHelper.isArray()) {
                annotator.addSizeAnnotation(facet.minLength(), facet.maxLength(), facet.length());

            } else if (fieldHelper.isNumber() || fieldHelper.isString()) {
                if (options.isAllNumericConstraints()) {
                    annotator.addDecimalMinAnnotationInclusive(facet.minInclusive());
                    annotator.addDecimalMinAnnotationExclusive(facet.minExclusive());
                    annotator.addDecimalMaxAnnotationInclusive(facet.maxInclusive());
                    annotator.addDecimalMaxAnnotationExclusive(facet.maxExclusive());

                } else {
                    annotator.addDecimalMinAnnotationInclusive(fieldHelper.validValue(facet.minInclusive()));
                    annotator.addDecimalMinAnnotationExclusive(fieldHelper.validValue(facet.minExclusive()));
                    annotator.addDecimalMaxAnnotationInclusive(fieldHelper.validValue(facet.maxInclusive()));
                    annotator.addDecimalMaxAnnotationExclusive(fieldHelper.validValue(facet.maxExclusive()));
                }

                annotator.addDigitsAnnotation(facet.totalDigits(), facet.fractionDigits());

                if (fieldHelper.isString()) {
                    annotator.addSizeAnnotation(facet.minLength(), facet.maxLength(), facet.length());

                    Set<String> patternSet = gatherRegexpAndEnumeration(facet, simpleType);
                    annotator.addPatterns(patternSet);
                }
            } else if (fieldHelper.isStringList() && options.isValidationCollection()) {
                annotator.addEachSizeAnnotation(facet.minLength(), facet.maxLength());

                Set<String> patternSet = gatherRegexpAndEnumeration(facet, simpleType);
                annotator.addEachPatterns(patternSet);
            }
        }

        /**
         * Collect REGEXP and ENUMERATION types on parents.
         * ENUMERATION is treated as a pattern with fixed value
         */
        private Set<String> gatherRegexpAndEnumeration(Facet facet, XSSimpleType simpleType) {

            final List<String> patternList = facet.patternList();
            addIfNotNull(patternList, facet.pattern());

            final List<String> enumerationList = facet.enumerationList();
            addIfNotNull(enumerationList, facet.enumeration());

            XSSimpleType baseType = simpleType;
            while ((baseType = baseType.getSimpleBaseType()) != null) {
                if (baseType instanceof XSRestrictionSimpleType) {
                    Facet baseFacet = new Facet((XSRestrictionSimpleType) baseType);

                    addIfNotNull(patternList, baseFacet.pattern());
                    addAllIfNotNull(patternList, baseFacet.patternList());

                    addIfNotNull(enumerationList, baseFacet.enumeration());
                    addAllIfNotNull(enumerationList, baseFacet.enumerationList());
                }
            }

            if (!patternList.isEmpty() || !enumerationList.isEmpty()) {
                List<String> adjustedPatterns = patternList.stream()
                        .filter(p -> isValidRegexp(p))
                        .map(p -> replaceRegexp(p))
                        .distinct()
                        .collect(Collectors.toList());

                // escaped enumuerations can be treated as patterns
                List<String> adjustedEnumerations = enumerationList.stream()
                        .filter(p -> p != null && !p.isEmpty())
                        .map(p -> escapeRegexp(p))
                        .distinct()
                        .collect(Collectors.toList());

                addAllIfNotNull(adjustedPatterns, adjustedEnumerations);

                return new LinkedHashSet<>(adjustedPatterns);
            }

            return Collections.emptySet();
        }
    }

    static boolean isEqualsOrNull(String optionsNamespace, String actualTargetNamespace) {
        if (optionsNamespace == null ||
                optionsNamespace.isEmpty() ||
                "null".equals(optionsNamespace)) {
            return true;
        }
        return actualTargetNamespace.startsWith(optionsNamespace);
    }

    static void addIfNotNull(List<String> list, String item) {
        if (item != null) {
            list.add(item);
        }
    }

    static void addAllIfNotNull(List<String> dest, List<String> source) {
        if (source != null && !source.isEmpty()) {
            for (String s : source) {
                if (s != null) {
                    dest.add(s);
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
                .replace("\\c", "[-._:A-Za-z0-9]");
    }

    String notNullMessage(ClassOutline classOutline, JFieldVar field) {
        final String className = classOutline.implClass.name();
        final Class<? extends Annotation> notNullClass = options.getAnnotationFactory()
                .getNotNullClass();

        String message = null;

        if (options.isNotNullPrefixClassName()) {
            message = String.format("%s.%s {%s.message}",
                    className, field.name(),
                    notNullClass.getName());

        } else if (options.isNotNullPrefixFieldName()) {
            message = String.format("%s {%s.message}",
                    field.name(),
                    notNullClass.getName());

        } else if (options.isNotNullCustomMessage()) {
            message = String.format("{%s.message}",
                    notNullClass.getName());

        } else if (options.getNotNullCustomMessageText() != null) {
            message = options.getNotNullCustomMessageText()
                    .replace("{ClassName}", className)
                    .replace("{FieldName}", field.name());
        }

        return message;
    }

}
