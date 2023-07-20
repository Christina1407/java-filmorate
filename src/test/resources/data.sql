merge into "rating_mpa" key (rating_id) values (1, 'G'), (2, 'PG'), (3, 'PG_13'), (4, 'R'), (5, 'NC_17');
merge into "genre" key (genre_id) values (1, 'Комедия'), (2, 'Драма'), (3, 'Мультфильм'), (4, 'Триллер'), (5, 'Документальный'), (6, 'Боевик');

insert into "user" (email, login, name, birthday) values ('test1@test.ru', 'test1', 'test1', '1994-08-06')
,('test2@test.ru', 'test2', 'test2', '1995-08-06')
,('test1@test.ru', 'test3', 'test3', '1996-08-06');

insert into "film" (name, description, release_date, duration, rating_id) values ('test1', 'test1', '1994-08-06', 100, 1)
,('test2', 'test2', '2005-10-10', 120, 5)
,('test3', 'test3', '2000-02-20', 90, 3);