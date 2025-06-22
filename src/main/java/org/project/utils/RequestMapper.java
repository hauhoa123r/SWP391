package org.project.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.project.exception.mapping.ErrorMappingException;
import org.project.model.dai.AppointmentDAI;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class RequestMapper {

    public static <T> T mapFromMap(Map<String, Object> sourceMap, Class<T> clazz){
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                JsonProperty annotation = field.getAnnotation(JsonProperty.class);
                if (annotation != null) {
                    fieldName = annotation.value();
                }
                if (!sourceMap.containsKey(fieldName)) continue;

                Object value = sourceMap.get(fieldName);
                if (value == null) continue;
                Class<?> fieldType = field.getType();

                if (fieldType == String.class) {
                    field.set(instance, value.toString());
                } else if (fieldType == boolean.class || fieldType == Boolean.class) {
                    field.set(instance, Boolean.parseBoolean(value.toString()));
                } else if (fieldType == int.class || fieldType == Integer.class) {
                    field.set(instance, Integer.parseInt(value.toString()));
                } else if (fieldType == long.class || fieldType == Long.class) {
                    field.set(instance, Long.parseLong(value.toString()));
                } else if (fieldType == float.class || fieldType == Float.class) {
                    field.set(instance, Float.parseFloat(value.toString()));
                } else if (fieldType == double.class || fieldType == Double.class) {
                    field.set(instance, Double.parseDouble(value.toString()));
                } else if (fieldType == byte.class || fieldType == Byte.class) {
                    field.set(instance, Byte.parseByte(value.toString()));
                } else if (fieldType == Date.class) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        field.set(instance, sdf.parse(value.toString()));
                    } catch (Exception e) {
                        field.set(instance, null);
                    }
                }
            }

            return instance;

        } catch (Exception e) {
            throw new ErrorMappingException(Map.class, clazz);
        }
    }
}