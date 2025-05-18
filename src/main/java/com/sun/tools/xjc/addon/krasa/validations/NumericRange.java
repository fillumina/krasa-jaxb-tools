package com.sun.tools.xjc.addon.krasa.validations;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Francesco Illuminati
 */
class NumericRange {
    private static final Map<String, NumericRange> MAP = new HashMap<>();

    static {
        MAP.put(Byte.class.getCanonicalName(), new NumericRange(Byte.MIN_VALUE, Byte.MAX_VALUE));
        MAP.put(Short.class.getCanonicalName(), new NumericRange(Short.MIN_VALUE, Short.MAX_VALUE));
        MAP.put(Integer.class.getCanonicalName(), new NumericRange(Integer.MIN_VALUE, Integer.MAX_VALUE));
        MAP.put(Long.class.getCanonicalName(), new NumericRange(Long.MIN_VALUE, Long.MAX_VALUE));
    }

    final BigDecimal min;
    final BigDecimal max;

    public NumericRange(Number min, Number max) {
        this.min = parse(min);
        this.max = parse(max);
    }

    private static BigDecimal parse(Number num) {
        if (num != null) {
            return new BigDecimal(Objects.toString(num));
        }
        return null;
    }

    public static BigDecimal valid(String typeName, BigDecimal value) {
        NumericRange range = MAP.get(typeName);
        if (range != null && (value.equals(range.min) || value.equals(range.max)) ) {
            return null;
        }
        return value;
    }
}
