DROP TABLE IF EXISTS film_genre;
DROP TABLE IF EXISTS likes;
DROP TABLE IF EXISTS friendships;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS mpa;

 CREATE TABLE IF NOT EXISTS mpa (
            id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
            name VARCHAR(50)
         );
 CREATE TABLE IF NOT EXISTS genres (
             genre_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
             genre_name VARCHAR(50)
           );
 CREATE TABLE IF NOT EXISTS users (
             user_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
             email VARCHAR(255),
             login VARCHAR(100),
             name VARCHAR(40),
             birthday DATE
           );
 CREATE TABLE IF NOT EXISTS films (
            film_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
            name VARCHAR(40) NOT NULL,
            description TEXT,
            release_date DATE NOT NULL,
            duration INTEGER,
            mpa BIGINT,
            FOREIGN KEY (mpa) REFERENCES mpa(id)
          );
CREATE TABLE IF NOT EXISTS film_genre (
            film_id BIGINT,
            genre_id BIGINT,
            FOREIGN KEY (film_id) REFERENCES films(film_id),
            FOREIGN KEY (genre_id) REFERENCES genres(genre_id)
          );
CREATE TABLE IF NOT EXISTS likes (
          film_id BIGINT,
          user_id BIGINT,
           FOREIGN KEY (film_id) REFERENCES films(film_id),
           FOREIGN KEY (user_id) REFERENCES users(user_id)
         );
CREATE TABLE IF NOT EXISTS friendships (
          friendship_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
          first_user_id BIGINT NOT NULL,
          second_user_id BIGINT NOT NULL,
          status VARCHAR(25),
          FOREIGN KEY (first_user_id) REFERENCES users(user_id),
          FOREIGN KEY (second_user_id) REFERENCES users(user_id)
         );


