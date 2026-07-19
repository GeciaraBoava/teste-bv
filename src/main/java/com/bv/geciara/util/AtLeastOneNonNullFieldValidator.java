package com.bv.geciara.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

public class AtLeastOneNonNullFieldValidator implements ConstraintValidator<AtLeastOneNonNullField, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        for (Field field : value.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(jakarta.validation.constraints.NotBlank.class)
                    || field.isAnnotationPresent(jakarta.validation.constraints.NotNull.class)) {
                continue;
            }

            field.setAccessible(true);
            try {
                if (field.get(value) != null) {
                    return true;
                }
            } catch (IllegalAccessException e) {
                return false;
            }
        }

        return false;
    }
}
