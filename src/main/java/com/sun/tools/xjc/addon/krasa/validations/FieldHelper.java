package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
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
            return NumericRange.valid(itemTypeName(), value);
        }
        return null;
    }

    /** The type the field holds: its type argument when it is a collection, its type otherwise. */
    private String itemTypeName() {
        JType type = field.type();
        if (type instanceof JClass) {
            List<JClass> typeArguments = ((JClass) type).getTypeParameters();
            if (typeArguments.size() == 1) {
                // the type argument of a List is a reference type, so boxify() is not needed
                // (and is deprecated on JClass)
                return typeArguments.get(0).fullName();
            }
        }
        return type.boxify().fullName();
    }

    /** WARNING a string with enumeration restrictions is converted into an enum */
    public boolean isString() {
        return field.type().name().equals("String");
    }

    public boolean isStringList() {
        return field.type().name().equals("List<String>");
    }

    public boolean isList() {
        return field.type().name().startsWith("List<");
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
