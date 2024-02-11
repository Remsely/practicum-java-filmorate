package ru.yandex.practicum.filmorate.model.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class AfterDateValidator implements ConstraintValidator<AfterDate, LocalDate> {
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