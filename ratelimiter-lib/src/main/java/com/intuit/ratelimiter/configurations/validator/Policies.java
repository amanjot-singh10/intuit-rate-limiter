package com.intuit.ratelimiter.configurations.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PoliciesValidator.class)
public @interface Policies {

    String message() default "Policy must contain limit";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}