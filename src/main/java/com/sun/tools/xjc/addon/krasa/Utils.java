package com.sun.tools.xjc.addon.krasa;

import com.sun.codemodel.JFieldVar;
import java.lang.reflect.Field;
import java.math.BigInteger;

/**
 * @author Vojtech Krasa
 * @author Viserius
 */
class Utils {

    public static final String[] NUMBERS = new String[] {
        "BigDecimal", "BigInteger", "String", "byte", "short", "int", "long"
    };

    public static int toInt(Object value) {
        if (value instanceof BigInteger) {
			// xjc
            return ((BigInteger) value).intValue();
        } else if (value instanceof Integer) {
			// cxf-codegen
            return (Integer) value;
		} else {
            throw new IllegalArgumentException(
                    "unknown type " + value.getClass());
		}
	}

	private static Field getSimpleField(String fieldName, Class<?> clazz) {
		Class<?> tmpClass = clazz;
		try {
			do {
				for (Field field : tmpClass.getDeclaredFields()) {
					String candidateName = field.getName();
					if (!candidateName.equals(fieldName)) {
						continue;
					}
					field.setAccessible(true);
					return field;
				}
				tmpClass = tmpClass.getSuperclass();
			} while (tmpClass != null);
		} catch (Exception e) {
			System.err.println("krasa-jaxb-tools - Field '" + fieldName +
                    "' not found on class " + clazz);
		}
		return null;
	}

    // TODO this
    public static Object getField(String path, Object obj) {
		try {
            int idx = path.indexOf(".");
            if (idx != -1) {
                String field = path.substring(0, idx);
                Field declaredField = obj.getClass().getDeclaredField(field);
				declaredField.setAccessible(true);
                Object result = declaredField.get(obj);

                String nextField = path.substring(path.indexOf(".") + 1);
                return getField(nextField, result);
			} else {
                Field simpleField = getSimpleField(path, obj.getClass());
				simpleField.setAccessible(true);
                return simpleField.get(obj);
			}
		} catch (Exception e) {
			String className = null;
			if(obj != null && obj.getClass() != null) {
				className = obj.getClass().getName();
			}
			System.err.println("krasa-jaxb-tools - Field " + path + " not found on " + className );
		}
		return null;
	}

	public static boolean isMin(String value) {
        return "-9223372036854775808".equals(value) || "-2147483648".equals(value);
	}

	public static boolean isMax(String value) {
        return "9223372036854775807".equals(value) || "2147483647".equals(value);
	}

	public static boolean isNumber(JFieldVar field) {
        final String fieldTypeName = field.type().name();
		for (String type : NUMBERS) {
            if (type.equalsIgnoreCase(fieldTypeName)) {
				return true;
			}
		}
		try {
            final String fieldTypeFullName = field.type().fullName();
            if (isNumber(Class.forName(fieldTypeFullName))) {
                return true;
            }
		} catch (Exception e) {
			// whatever
		}
		return false;
	}

	public static boolean isNumber(Class<?> aClass) {
		return Number.class.isAssignableFrom(aClass);
	}

	public static boolean isCustomType(JFieldVar var) {
		if(var == null) {
			return false;
		}
        return "JDirectClass".equals(var.type().getClass().getSimpleName())
				|| "JNarrowedClass".equals(var.type().getClass().getSimpleName());
    }
}
