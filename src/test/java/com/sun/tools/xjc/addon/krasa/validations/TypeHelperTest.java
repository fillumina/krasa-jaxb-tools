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

import java.math.BigDecimal;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class TypeHelperTest {

    @Test
    public void testIsNumber() throws Exception {
        assertFalse(FieldHelper.isNumber(String.class));
        assertFalse(FieldHelper.isNumber(IllegalStateException.class));
        assertTrue(FieldHelper.isNumber(BigDecimal.class));
    }

    @Test
    public void isFieldTypeNameNumber() {
        assertTrue(FieldHelper.isFieldTypeNameNumber(BigDecimal.class.getSimpleName()));
        assertTrue(FieldHelper.isFieldTypeNameNumber(Long.class.getSimpleName()));
        assertTrue(FieldHelper.isFieldTypeNameNumber(Short.class.getSimpleName()));
        assertTrue(FieldHelper.isFieldTypeNameNumber(Integer.class.getSimpleName()));

        assertFalse(FieldHelper.isFieldTypeNameNumber(String.class.getSimpleName()));
        assertFalse(FieldHelper.isFieldTypeNameNumber(Boolean.class.getSimpleName()));
    }

    @Test
    public void isFieldTypeFullNameNumber() {
        assertTrue(FieldHelper.isFieldTypeFullNameNumber(BigDecimal.class.getCanonicalName()));
        assertTrue(FieldHelper.isFieldTypeFullNameNumber(Long.class.getCanonicalName()));
        assertTrue(FieldHelper.isFieldTypeFullNameNumber(Short.class.getCanonicalName()));
        assertTrue(FieldHelper.isFieldTypeFullNameNumber(Integer.class.getCanonicalName()));

        assertFalse(FieldHelper.isFieldTypeFullNameNumber(String.class.getCanonicalName()));
        assertFalse(FieldHelper.isFieldTypeFullNameNumber(Boolean.class.getCanonicalName()));
    }

}
