/*
 * Copyright 2024 Francesco Illuminati .
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sun.tools.xjc.addon.krasa.validations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class ValidationsOptionsTest {

    @Test
    public void shouldGetDafaults() {
        ValidationsOptions options = ValidationsOptions.builder().build();
        assertFalse(options.isNotNullCustomMessage());
        assertFalse(options.isNotNullPrefixClassName());
        assertFalse(options.isNotNullPrefixFieldName());
        assertTrue(options.isNotNullAnnotations());
        assertFalse(options.isMultiPattern());
        assertFalse(options.isVerbose());
        assertFalse(options.isValidationCollection());
        assertEquals(ValidationsAnnotation.JAVAX, options.getAnnotationFactory());
    }

    @Test
    public void shouldSetTargetNamespace() {
        ValidationsOptions options = ValidationsOptions.builder()
                .targetNamespace("something.there")
                .build();

        assertEquals("something.there", options.getTargetNamespace());
    }

    @Test
    public void shouldSetMultiPatternTrue() {
        ValidationsOptions options = ValidationsOptions.builder()
                .multiPattern(true)
                .build();

        assertTrue(options.isMultiPattern());
    }

    @Test
    public void shouldSetMultiPatternFalse() {
        ValidationsOptions options = ValidationsOptions.builder()
                .multiPattern(false)
                .build();

        assertFalse(options.isMultiPattern());
    }

    @Test
    public void shouldSetVerboseTrue() {
        ValidationsOptions options = ValidationsOptions.builder()
                .verbose(true)
                .build();

        assertTrue(options.isVerbose());
    }

    @Test
    public void shouldSetVerboseFalse() {
        ValidationsOptions options = ValidationsOptions.builder()
                .verbose(false)
                .build();

        assertFalse(options.isVerbose());
    }

    @Test
    public void shouldSetGenerateStringListAnnotationsTrue() {
        ValidationsOptions options = ValidationsOptions.builder()
                .validationCollection(true)
                .build();

        assertTrue(options.isValidationCollection());
    }

    @Test
    public void shouldSetGenerateStringListAnnotationsFalse() {
        ValidationsOptions options = ValidationsOptions.builder()
                .validationCollection(false)
                .build();

        assertFalse(options.isValidationCollection());
    }

    @Test
    public void shouldSetAnnotationFactoryTrue() {
        ValidationsOptions options = ValidationsOptions.builder()
                .annotationFactory(ValidationsAnnotation.JAVAX)
                .build();

        assertEquals(ValidationsAnnotation.JAVAX, options.getAnnotationFactory());
    }

    @Test
    public void shouldSetAnnotationFactoryFalse() {
        ValidationsOptions options = ValidationsOptions.builder()
                .annotationFactory(ValidationsAnnotation.JAKARTA)
                .build();

        assertEquals(ValidationsAnnotation.JAKARTA, options.getAnnotationFactory());
    }

}
