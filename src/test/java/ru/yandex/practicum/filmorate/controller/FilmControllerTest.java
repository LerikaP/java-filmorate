package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmControllerTest {
    private static Film film;
    private Validator validator;
    private static final String TOO_LONG_DESCRIPTION = "Двое бандитов Винсент Вега и Джулс Винфилд ведут философские " +
            "беседы в перерывах между разборками и решением проблем с должниками криминального босса Марселласа " +
            "Уоллеса. В первой истории Винсент проводит незабываемый вечер с женой Марселласа Мией. Во второй " +
            "рассказывается о боксёре Бутче Кулидже, купленном Уоллесом, чтобы сдать бой. В третьей истории Винсент " +
            "и Джулс по нелепой случайности попадают в неприятности.";

    @BeforeEach
    void setUp() {
        film = Film.builder()
                .name("Криминальное чтиво")
                .description("Двое бандитов Винсент Вега и Джулс Винфилд ведут философские беседы " +
                        "в перерывах между разборками.")
                .releaseDate(LocalDate.of(1994, 5, 21))
                .duration(154)
                .build();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldNotAddFilmWithEmptyName() {
        film.setName(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.stream().anyMatch(t -> t.getMessage().equals("Название фильма не может отсутствовать")));

        film.setName("");
        violations = validator.validate(film);

        assertEquals(violations.iterator().next().getMessage(), "Название фильма не может быть пустым");
    }

    @Test
    void shouldNotAddFilmWithDescriptionMoreThan200Chars() {
        film.setDescription(TOO_LONG_DESCRIPTION);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(violations.iterator().next().getMessage(),
                "Описание не должно превышать размер в 200 символов");
    }

    @Test
    void shouldNotAddFilmWithReleaseDateEarlierThanIndicated() {
        film.setReleaseDate(LocalDate.of(1850, 10, 16));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(violations.iterator().next().getMessage(),
                "Дата релиза не может быть раньше 28 декабря 1895 года");
    }

    @Test
    void shouldNotAddFilmWithNegativeDuration() {
        film.setDuration(-100);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(violations.iterator().next().getMessage(),
                "Продолжительность фильма не может быть отрицательной");
    }
}
