package com.bv.geciara.util;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AtLeastOneNonNullFieldValidator.class)
public @interface AtLeastOneNonNullField {

    String message() default "Ao menos um campo deve ser preenchido para atualização";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
