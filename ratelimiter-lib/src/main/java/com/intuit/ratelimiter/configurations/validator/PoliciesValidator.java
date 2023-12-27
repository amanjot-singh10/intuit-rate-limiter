package com.intuit.ratelimiter.configurations.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.Map;
import com.intuit.ratelimiter.configurations.RateLimiterProperties1.*;
import com.intuit.ratelimiter.constants.RateLimitType;

public class PoliciesValidator implements ConstraintValidator<Policies, Object> {

    @Override
    public void initialize(Policies constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        } else if (value instanceof Collection) {
            return isValidCollection((Collection<?>) value);
        } else if (value instanceof Map) {
            return ((Map<?, ?>) value).values().stream().allMatch(v -> isValid(v, context));
        } else {
            return false;
        }
    }

    private boolean isValidCollection(Collection<?> objects) {
        return objects.isEmpty()
                || objects.stream().allMatch(this::isValidObject);
    }

    private boolean isValidObject(Object o) {
        return (o instanceof Policy) && isValidPolicy((Policy) o);
    }

    private boolean isValidPolicy(Policy policy) {
        return (policy.getLimit() != null) && isValid(policy);
    }

    private boolean isValid(Policy policy) {
        return policy.getType().stream().allMatch(type -> type instanceof RateLimitType);
    }
}