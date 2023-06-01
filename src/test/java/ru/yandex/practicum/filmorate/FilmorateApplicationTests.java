package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class FilmorateApplicationTests {

	private final UserStorage userStorage;
	private final FilmStorage filmStorage;
	private final MpaStorage mpaStorage;
	private final GenreStorage genreStorage;
	private final LikesStorage likesStorage;
	private final FriendsStorage friendsStorage;

	private static User user;
	private static User otherUser;
	private static Film film;
	private static Film otherFilm;

	@BeforeEach
	void setUP() {
		user = User.builder()
				.email("kinokritik999@yandex.ru")
				.login("Kinokritik")
				.name("Todd")
				.birthday(LocalDate.of(1969, 7, 25))
				.build();
		otherUser = User.builder()
				.email("otherKinokritik@yandex.ru")
				.login("Kinokritik999")
				.name("Gage")
				.birthday(LocalDate.of(1984, 2, 17))
				.build();
		film = Film.builder()
				.name("Криминальное чтиво")
				.description("Не очень подробное описание")
				.releaseDate(LocalDate.of(1994, 5, 21))
				.duration(154)
				.mpa(new Mpa(5, "NC-17"))
				.build();
		otherFilm = Film.builder()
				.name("Зеленая Миля")
				.description("Очень подробное описание")
				.releaseDate(LocalDate.of(1999, 12, 6))
				.duration(189)
				.mpa(new Mpa(4, "R"))
				.build();
	}

	@Test
	void testGetAllFilms() {
		Film filmInDb = filmStorage.addFilm(film);
		filmStorage.addFilm(otherFilm);
		List<Film> films = filmStorage.getAllFilms();

		assertEquals(films.size(), 2);
		assertTrue(films.contains(filmInDb));
	}

	@Test
	void testAddFilm() {
		filmStorage.addFilm(film);
		Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));

		assertThat(filmOptional).
				isPresent().
				hasValueSatisfying(film -> {
					assertThat(film).hasFieldOrPropertyWithValue("id", 1L);
					assertThat(film).hasFieldOrPropertyWithValue("name", "Криминальное чтиво");
				}
				);
	}

	@Test
	void testUpdateFilm() {
		filmStorage.addFilm(film);
		film.setName("Новое имя");
		filmStorage.updateFilm(film);
		Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));

		assertThat(filmOptional).
				isPresent().
				hasValueSatisfying(film -> {
					assertThat(film).hasFieldOrPropertyWithValue("id", 1L);
					assertThat(film).hasFieldOrPropertyWithValue("name", "Новое имя");
				}
				);
	}

	@Test
	void testGetFilmById() {
		filmStorage.addFilm(film);
		Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));

		assertThat(filmOptional).
				isPresent().
				hasValueSatisfying(film -> {
							assertThat(film).hasFieldOrPropertyWithValue("id", 1L);
							assertThat(film).hasFieldOrPropertyWithValue("name", "Криминальное чтиво");
				}
				);
	}

	@Test
	void testGetAllGenres() {
		assertEquals(genreStorage.getAllGenres().size(), 6);
	}

	@Test
	void testGetGenreById() {
		assertEquals(genreStorage.getGenreById(1).getName(), "Комедия");
		assertEquals(genreStorage.getGenreById(6).getName(), "Боевик");
	}

	@Test
	void testAddGenresToFilmInDb() {
		filmStorage.addFilm(film);
		genreStorage.addGenresToFilmInDb(film.getId(), 3);

		assertEquals(genreStorage.getGenresForFilmFromDb(1).size(), 1);

		genreStorage.addGenresToFilmInDb(film.getId(), 5);

		assertEquals(genreStorage.getGenresForFilmFromDb(1).size(), 2);
	}

	@Test
	void testDeleteFilmGenres() {
		filmStorage.addFilm(film);
		genreStorage.addGenresToFilmInDb(film.getId(), 3);

		assertEquals(genreStorage.getGenresForFilmFromDb(1).size(), 1);

		genreStorage.deleteFilmGenres(1);

		assertEquals(genreStorage.getGenresForFilmFromDb(1).size(), 0);
	}

	@Test
	void testGetAllMpa() {
		assertEquals(mpaStorage.getAllMpa().size(), 5);
	}

	@Test
	void testGetAllUsers() {
		User userInDB = userStorage.addUser(user);
		userStorage.addUser(otherUser);
		List<User> users = userStorage.getAllUsers();

		assertEquals(users.size(), 2);
		assertTrue(users.contains(userInDB));
	}

	@Test
	void testAddUser() {
		userStorage.addUser(user);
		Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));

		assertThat(userOptional).
				isPresent().
				hasValueSatisfying(user -> {
							assertThat(user).hasFieldOrPropertyWithValue("id", 1L);
							assertThat(user).hasFieldOrPropertyWithValue("login", "Kinokritik");
				}
				);
	}

	@Test
	void testUpdateUser() {
		userStorage.addUser(user);
		user.setName("Новое имя");
		userStorage.updateUser(user);
		Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));

		assertThat(userOptional).
				isPresent().
				hasValueSatisfying(user -> {
							assertThat(user).hasFieldOrPropertyWithValue("id", 1L);
							assertThat(user).hasFieldOrPropertyWithValue("name", "Новое имя");
				}
				);
	}

	@Test
	void testGetUserById() {
		userStorage.addUser(user);
		Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));

		assertThat(userOptional).
				isPresent().
				hasValueSatisfying(user -> {
							assertThat(user).hasFieldOrPropertyWithValue("id", 1L);
							assertThat(user).hasFieldOrPropertyWithValue("name", "Todd");
				}
				);
	}

	@Test
	void testAddLikeToFilm() {
		filmStorage.addFilm(film);
		userStorage.addUser(user);
		likesStorage.addLikeToFilm(1, 1);

		assertEquals(likesStorage.getLikesForFilm(1).size(), 1);

		userStorage.addUser(otherUser);
		likesStorage.addLikeToFilm(1, 2);

		assertEquals(likesStorage.getLikesForFilm(1).size(), 2);

		likesStorage.deleteLikeFromFilm(1, 1);

		assertEquals(likesStorage.getLikesForFilm(1).size(), 1);
	}

	@Test
	void testAddFriend() {
		userStorage.addUser(user);
		userStorage.addUser(otherUser);
		friendsStorage.addFriend(1, 2);

		assertEquals(friendsStorage.getFriendsIds(1).size(), 1);

		friendsStorage.deleteFriend(1, 2);

		assertEquals(friendsStorage.getFriendsIds(1).size(), 0);
	}
}
