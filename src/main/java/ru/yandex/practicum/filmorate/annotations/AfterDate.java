package ru.yandex.practicum.filmorate.annotations;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDate;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AfterDateValidator.class)
public @interface AfterDate {
    String message() default "The date can't be before this";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String lowerBound();
}

class AfterDateValidator implements ConstraintValidator<AfterDate, LocalDate> {
    private LocalDate lowerBound;

    @Override
    public void initialize(AfterDate constraintAnnotation) {
        lowerBound = LocalDate.parse(constraintAnnotation.lowerBound());
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (localDate == null) {
            return true;
        }
        return localDate.isAfter(lowerBound);
    }
}