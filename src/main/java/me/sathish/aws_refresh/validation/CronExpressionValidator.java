package me.sathish.aws_refresh.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import me.sathish.aws_refresh.util.CronValidator;

public class CronExpressionValidator implements ConstraintValidator<ValidCron, String> {

    private boolean allowNull;

    @Override
    public void initialize(ValidCron constraintAnnotation) {
        this.allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return allowNull;
        }
        
        boolean isValid = CronValidator.isValid(value);
        
        if (!isValid) {
            String message = CronValidator.getValidationMessage(value);
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                   .addConstraintViolation();
        }
        
        return isValid;
    }
}
