package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class UserControllerTest {
    private static User user;
    private Validator validator;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("kinokritik999@yandex.ru")
                .login("Kinokritik")
                .name("Todd")
                .birthday(LocalDate.of(1969, 7, 25))
                .build();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldNotAddUserWithIncorrectEmail() {
        user.setEmail(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.stream().anyMatch(t -> t.getMessage().equals("Email не может отсутствовать")));

        user.setEmail("");
        violations = validator.validate(user);

        assertTrue(violations.stream().anyMatch(t -> t.getMessage().equals("Email не может быть пустым")));

        user.setEmail("lol");
        violations = validator.validate(user);

        assertEquals(violations.iterator().next().getMessage(), "Должен быть введен корректный email");
    }

    @Test
    void shouldNotSaveUserWithIncorrectLogin() {
        user.setLogin(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.stream().anyMatch(t -> t.getMessage().equals("Логин не может отсутствовать")));

        user.setLogin("");
        violations = validator.validate(user);

        assertEquals(violations.iterator().next().getMessage(), "Логин не может быть пустым");

        user.setLogin("l o l");
        violations = validator.validate(user);

        assertEquals(violations.iterator().next().getMessage(), "Строка не должна содержать пробелы");
    }

    @Test
    void shouldNotAddUserWithIncorrectBirthdayDate() {
        user.setBirthday(LocalDate.now().plusDays(10));
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(violations.iterator().next().getMessage(), "Дата рождения не может быть в будущем");
    }
}
