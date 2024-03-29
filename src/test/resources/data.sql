merge into "rating_mpa" key (rating_id) values (1, 'G'), (2, 'PG'), (3, 'PG_13'), (4, 'R'), (5, 'NC_17');
merge into "genre" key (genre_id) values (1, 'Комедия'), (2, 'Драма'), (3, 'Мультфильм'), (4, 'Триллер'), (5, 'Документальный'), (6, 'Боевик');

insert into "user" (email, login, name, birthday) values ('test1@test.ru', 'test1', 'test1', '1994-08-06')
,('test2@test.ru', 'test2', 'test2', '1995-08-06')
,('test1@test.ru', 'test3', 'test3', '1996-08-06');

insert into "film" (name, description, release_date, duration, rating_id) values ('test1', 'test1', '1994-08-06', 100, 1)
,('test2', 'test2', '2005-10-10', 120, 5)
,('test3', 'test3', '2000-02-20', 90, 3);

insert into "friendship" (user_id, friend_id, relation_type) values (1, 2, 'FRIEND')
,(2, 1, 'FRIEND')
,(3, 2, 'NOT_APPROVED_FRIEND'); --второй пользователь отправил заявку третьему. Третьему пользователю добавляется в друзья второй.У третьего в друзьях есть второй, у второго третьего нет


insert into "film_like" (film_id, user_id) values (1, 2)
,(1, 1)
,(2, 1);

insert into "film_genre" (film_id, genre_id) values (1, 2)
,(2, 1)
,(2, 3);
