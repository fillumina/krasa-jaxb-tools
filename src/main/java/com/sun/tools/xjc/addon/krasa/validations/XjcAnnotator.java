package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JFieldVar;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Add annotations with parameters to a {@link JFieldVar} making it sure there aren't
 * duplications.
 *
 * @author Francesco Illuminati
 */
class XjcAnnotator {
    private final JFieldVar field;
    private final ValidationsLogger logger;
    private final Set<Class<? extends Annotation>> annotationSet = new HashSet<>();

    public XjcAnnotator(JFieldVar field, ValidationsLogger logger) {
        this.field = field;
        this.logger = logger;
    }

    Annotate annotate(Class<? extends Annotation> annotation) {
        return new Annotate(annotation);
    }

    public class Annotate {
        private final JAnnotationUse annotationUse;
        private final Map<String,String> parameterMap = new LinkedHashMap<>();

        public Annotate(JAnnotationUse annotationUse) {
            this.annotationUse = annotationUse;
        }

        public Annotate(Class<? extends Annotation> annotation) {
            if (annotationSet.add(annotation)) {
                annotationUse = field.annotate(annotation);
            } else {
                this.annotationUse = null;
            }
        }

        public Annotate paramIf(boolean condition, String name, Integer value) {
            if (condition && annotationUse != null && value != null && !parameterMap.containsKey(name)) {
                annotationUse.param(name, value);
                parameterMap.put(name, value.toString());
            }
            return this;
        }

        public Annotate param(String name, Integer value) {
            if (annotationUse != null && value != null && !parameterMap.containsKey(name)) {
                annotationUse.param(name, value);
                parameterMap.put(name, value.toString());
            }
            return this;
        }

        public Annotate param(String name, Boolean value) {
            if (annotationUse != null && value != null && !parameterMap.containsKey(name)) {
                annotationUse.param(name, value);
                parameterMap.put(name, value.toString());
            }
            return this;
        }

        public Annotate param(String name, BigDecimal value) {
            if (annotationUse != null && value != null && !parameterMap.containsKey(name)) {
                annotationUse.param(name, value.toString());
                parameterMap.put(name, value.toString());
            }
            return this;
        }

        public Annotate param(String name, String value) {
            if (annotationUse != null && value != null && !parameterMap.containsKey(name)) {
                annotationUse.param(name, value);
                parameterMap.put(name, value);
            }
            return this;
        }

        public Annotate param(String name, String value, String defaultValue) {
            if (annotationUse != null && !parameterMap.containsKey(name)) {
                String v = value == null ? defaultValue : value;
                annotationUse.param(name, v);
                parameterMap.put(name, v);
            }
            return this;
        }

        public Annotate param(String name, Integer value, Integer defaultValue) {
            if (annotationUse != null && !parameterMap.containsKey(name)) {
                Integer v = value == null ? defaultValue : value;
                annotationUse.param(name, v);
                parameterMap.put(name, v.toString());
            }
            return this;
        }

        public void log() {
            if (annotationUse != null) {
                String annotationName = annotationUse.getAnnotationClass().name();
                logger.addAnnotation(annotationName, parameterMap);
            }
        }

        public MultipleAnnotation multipleAnnotationContainer(String paramName) {
            JAnnotationArrayMember array = annotationUse.paramArray(paramName);
            return new MultipleAnnotation(array);
        }

        public class MultipleAnnotation {
            private final JAnnotationArrayMember array;

            public MultipleAnnotation(JAnnotationArrayMember array) {
                this.array = array;
            }

            public Annotate annotate(Class<? extends Annotation> annotationClass) {
                JAnnotationUse annotationUse = array.annotate(annotationClass);
                return new Annotate(annotationUse);
            }

        }
    }

}
