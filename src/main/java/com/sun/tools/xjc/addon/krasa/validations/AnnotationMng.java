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
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AnnotationMng {
    private final JFieldVar field;
    private final ValidationsLogger logger;
    private final Set<Class<? extends Annotation>> annotationSet = new HashSet<>();

    public AnnotationMng(ValidationsLogger logger, JFieldVar field) {
        this.field = field;
        this.logger = logger;
    }

    Annotate annotate(Class<? extends Annotation> annotation) {
        return new Annotate(annotation);
    }

    public class Annotate {
        private final JAnnotationUse annotationUse;
        private final Map<String,String> parameterMap = new LinkedHashMap<>();

        public Annotate(Class<? extends Annotation> annotation) {
            if (!annotationSet.contains(annotation)) {
                annotationUse = field.annotate(annotation);
                annotationSet.add(annotation);
            } else {
                this.annotationUse = null;
            }
        }

        private Annotate(JAnnotationUse annotationUse) {
            this.annotationUse = annotationUse;
        }

        public Annotate paramIf(boolean condition, String name, Integer value) {
            if (condition && annotationUse != null && value != null) {
                annotationUse.param(name, value);
                parameterMap.put(name, value.toString());
            }
            return this;
        }

        public Annotate param(String name, Integer value) {
            if (annotationUse != null && value != null) {
                annotationUse.param(name, value);
                parameterMap.put(name, value.toString());
            }
            return this;
        }

        public Annotate param(String name, Boolean value) {
            if (annotationUse != null && value != null) {
                annotationUse.param(name, value);
                parameterMap.put(name, value.toString());
            }
            return this;
        }

        public Annotate param(String name, BigDecimal value) {
            if (annotationUse != null && value != null) {
                annotationUse.param(name, value.toString());
                parameterMap.put(name, value.toString());
            }
            return this;
        }

        public Annotate param(String name, String value) {
            if (annotationUse != null && value != null) {
                annotationUse.param(name, value.toString());
                parameterMap.put(name, value.toString());
            }
            return this;
        }

        public Annotate param(String name, String value, String defaultValue) {
            if (annotationUse != null) {
                String v = value == null ? defaultValue : value;
                annotationUse.param(name, v);
                parameterMap.put(name, v.toString());
            }
            return this;
        }

        public Annotate param(String name, Integer value, Integer defaultValue) {
            if (annotationUse != null) {
                Integer v = value == null ? defaultValue : value;
                annotationUse.param(name, v);
                parameterMap.put(name, v.toString());
            }
            return this;
        }

        public void log() {
            String annotationName = annotationUse.getAnnotationClass().name();
            logger.addAnnotation(annotationName, parameterMap);
        }

        public MultipleAnnotation multiple(String paramName) {
            JAnnotationArrayMember array = annotationUse.paramArray(paramName);
            return new MultipleAnnotation(array);
        }

        public class MultipleAnnotation {
            private final JAnnotationArrayMember array;

            public MultipleAnnotation(JAnnotationArrayMember array) {
                this.array = array;
            }

            public Annotate annotate(Class<? extends Annotation> annotation) {
                JAnnotationUse annotationUse = array.annotate(annotation);
                return new Annotate(annotationUse);
            }

        }
    }

}
