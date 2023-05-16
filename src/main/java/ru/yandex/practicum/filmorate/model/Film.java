package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.FilmReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class Film {
    private long id;
    @NotNull(message = "Название фильма не может отсутствовать")
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;
    @Size(max = 200, message = "Описание не должно превышать размер в 200 символов")
    private String description;
    @FilmReleaseDate
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма не может быть отрицательной")
    private int duration;
    private Set<Long> likes;
}
