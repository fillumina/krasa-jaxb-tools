package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JFieldVar;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
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
    /** When not null the annotations are collected here instead of being written to the field. */
    private final List<Annotate> collector;
    private final Set<Class<? extends Annotation>> annotationSet = new HashSet<>();

    public XjcAnnotator(JFieldVar field, ValidationsLogger logger) {
        this(field, logger, null);
    }

    public XjcAnnotator(JFieldVar field, ValidationsLogger logger, List<Annotate> collector) {
        this.field = field;
        this.logger = logger;
        this.collector = collector;
    }

    Annotate annotate(Class<? extends Annotation> annotation) {
        return new Annotate(annotation);
    }

    public class Annotate {
        private Class<? extends Annotation> annotationClass;
        private final JAnnotationUse annotationUse;
        /** False when the annotation is a duplicate and is written nowhere. */
        private final boolean active;
        private final Map<String,String> parameterMap = new LinkedHashMap<>();
        private final List<Annotate> nested = new ArrayList<>();

        private Annotate(Class<? extends Annotation> type, JAnnotationUse use) {
            this.annotationClass = type;
            this.annotationUse = use;
            this.active = true;
        }

        public Annotate(JAnnotationUse annotationUse) {
            this.annotationUse = annotationUse;
            this.active = annotationUse != null;
        }

        public Annotate(Class<? extends Annotation> annotation) {
            this.annotationClass = annotation;
            // @Pattern is allowed multiple times
            boolean used = annotationSet.add(annotation)
                    || annotation.equals(Pattern.class)
                    || annotation.equals(javax.validation.constraints.Pattern.class);
            this.active = used;
            if (!used) {
                this.annotationUse = null;
            } else if (collector == null) {
                this.annotationUse = field.annotate(annotation);
            } else {
                this.annotationUse = null;
                collector.add(this);
            }
        }

        /** @return the annotation that would have been written, with the parameters it was given. */
        Class<? extends Annotation> getAnnotationClass() {
            return annotationClass;
        }

        Map<String, String> getParameters() {
            return parameterMap;
        }

        List<Annotate> getNested() {
            return nested;
        }

        /**
         * Records a parameter: it is always remembered, and written only when there is an
         * annotation to write it into — a collected annotation has none.
         *
         * @return true when the parameter also has to be written
         */
        private boolean record(String name, String value) {
            if (!active || value == null || parameterMap.containsKey(name)) {
                return false;
            }
            parameterMap.put(name, value);
            return annotationUse != null;
        }

        public Annotate paramIf(boolean condition, String name, Integer value) {
            return condition ? param(name, value) : this;
        }

        public Annotate param(String name, Integer value) {
            if (value != null && record(name, value.toString())) {
                annotationUse.param(name, value);
            }
            return this;
        }

        public Annotate param(String name, Boolean value) {
            if (value != null && record(name, value.toString())) {
                annotationUse.param(name, value);
            }
            return this;
        }

        public Annotate param(String name, BigDecimal value) {
            if (value != null && record(name, value.toString())) {
                annotationUse.param(name, value.toString());
            }
            return this;
        }

        public Annotate param(String name, String value) {
            if (record(name, value)) {
                annotationUse.param(name, value);
            }
            return this;
        }

        public Annotate param(String name, String value, String defaultValue) {
            String v = value == null ? defaultValue : value;
            if (record(name, v)) {
                annotationUse.param(name, v);
            }
            return this;
        }

        public Annotate param(String name, Integer value, Integer defaultValue) {
            Integer v = value == null ? defaultValue : value;
            if (v != null && record(name, v.toString())) {
                annotationUse.param(name, v);
            }
            return this;
        }

        /** Only an annotation that was written is logged: a collected one was not. */
        public void log() {
            if (annotationUse != null) {
                String annotationName = annotationUse.getAnnotationClass().name();
                logger.addAnnotation(annotationName, parameterMap);
            }
        }

        public MultipleAnnotation multipleAnnotationContainer(String paramName) {
            JAnnotationArrayMember array = annotationUse == null ? null : annotationUse.paramArray(paramName);
            return new MultipleAnnotation(array);
        }

        public class MultipleAnnotation {
            private final JAnnotationArrayMember array;

            public MultipleAnnotation(JAnnotationArrayMember array) {
                this.array = array;
            }

            public Annotate annotate(Class<? extends Annotation> annotationClass) {
                if (array == null) {
                    Annotate child = new Annotate(annotationClass, null);
                    nested.add(child);
                    return child;
                }
                return new Annotate(array.annotate(annotationClass));
            }

        }
    }

}
