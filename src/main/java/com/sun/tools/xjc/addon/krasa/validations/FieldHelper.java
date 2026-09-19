package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class FieldHelper {
    private final JFieldVar field;

    public FieldHelper(JFieldVar field) {
        this.field = field;
    }

    public BigDecimal validValue(BigDecimal value) {
        if (value != null) {
            String typeName = field.type().boxify().fullName();
            return NumericRange.valid(typeName, value);
        }
        return null;
    }

    /**
     * The same as {@link #validValue(BigDecimal)}, for the elements of a collection: a bound of a
     * {@code List<Integer>} has to be compared with {@code Integer}, while the field itself is a
     * {@code List}, which {@code NumericRange} does not know.
     */
    public BigDecimal validItemValue(BigDecimal value) {
        if (value != null) {
            String itemType = itemTypeName();
            String typeName = itemType != null ? itemType : field.type().boxify().fullName();
            return NumericRange.valid(typeName, value);
        }
        return null;
    }

    /**
     * @return true when the field is exactly of the given type, primitives boxed. The erasure is
     * compared with the canonical name of the class: codemodel defines no equality for its types,
     * and the full name of a generic type carries its arguments.
     */
    private boolean isType(Class<?> expected) {
        return field.type().boxify().erasure().fullName().equals(expected.getCanonicalName());
    }

    /** @return the canonical name of the type argument of the field, null when it has none. */
    private String itemTypeName() {
        List<JClass> typeArguments = typeArguments();
        // the type argument of a List is a reference type, so boxify() is not needed
        // (and is deprecated on JClass)
        return typeArguments.size() == 1 ? typeArguments.get(0).fullName() : null;
    }

    /** @return the type arguments of the field: empty unless it is a generic type. */
    private List<JClass> typeArguments() {
        JType type = field.type();
        return type instanceof JClass
                ? ((JClass) type).getTypeParameters()
                : Collections.emptyList();
    }

    /** WARNING a string with enumeration restrictions is converted into an enum */
    public boolean isString() {
        return isType(String.class);
    }

    public boolean isStringList() {
        return isList() && String.class.getCanonicalName().equals(itemTypeName());
    }

    public boolean isList() {
        return isType(List.class) && itemTypeName() != null;
    }

    public boolean isArray() {
        return field.type().isArray();
    }

    public boolean isCustomType() {
        return "JDirectClass".equals(field.type().getClass().getSimpleName());
    }

    private static final Set<String> NUMBERS = Arrays.stream(new Class<?>[]{
        BigDecimal.class,
        BigInteger.class,
        Byte.class,
        Short.class,
        Integer.class,
        Double.class,
        Float.class,
        Long.class})
            .map(c -> c.getSimpleName().toUpperCase())
            .collect(Collectors.toSet());

    public boolean isNumber() {
        return isFieldTypeNameNumber(field.type().boxify().name()) ||
                isFieldTypeFullNameNumber(field.type().fullName());
    }

    static boolean isFieldTypeNameNumber(String fieldTypeName) {
        return NUMBERS.contains(fieldTypeName.toUpperCase());
    }

    static boolean isFieldTypeFullNameNumber(String fieldTypeFullName) {
        try {
            if (isNumber(Class.forName(fieldTypeFullName))) {
                return true;
            }
        } catch (ClassNotFoundException ex) {
            // ignore
        }
        return false;
    }

    static boolean isNumber(Class<?> aClass) {
        return Number.class.isAssignableFrom(aClass);
    }

}
