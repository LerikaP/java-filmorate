package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class FilmReleaseDateValidator implements ConstraintValidator<FilmReleaseDate, LocalDate> {
    private final LocalDate earliestReleaseDate = LocalDate.of(1895, 12, 28);

    @Override
    public void initialize(FilmReleaseDate filmReleaseDate) {
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext context) {
            return localDate.isAfter(earliestReleaseDate);
    }
}
