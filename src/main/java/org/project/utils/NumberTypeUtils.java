package org.project.utils;

import org.springframework.stereotype.Component;

@Component
@SuppressWarnings({"unchecked"})
public class NumberTypeUtils {
    public <NumberType extends Number> NumberType convertNumberValue(Object value, Class<NumberType> targetType) {
        if (value == null) return null;

        Number numValue = (Number) value;
        if (targetType == Double.class) {
            return (NumberType) Double.valueOf(numValue.doubleValue());
        } else if (targetType == Long.class) {
            return (NumberType) Long.valueOf(numValue.longValue());
        } else if (targetType == Integer.class) {
            return (NumberType) Integer.valueOf(numValue.intValue());
        } else if (targetType == Float.class) {
            return (NumberType) Float.valueOf(numValue.floatValue());
        }

        return (NumberType) numValue;
    }

}
