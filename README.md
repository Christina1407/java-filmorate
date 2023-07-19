// Use DBML to define your database structure
// Docs: https://dbml.dbdiagram.io/docs


Table user {
user_id bigint [primary key]
email varchar
login varchar
name varchar
birthday date


}

Table friendship {
friendship_id bigint [primary key]
user_id bigint
friend_id bigint
relation_type enum('friend', 'not_approved_friend')
}

Table film {
film_id bigint [primary key]
name varchar
description varchar
release_date date
duration int  
rating_id int
}

Table genre {
genre_id int [primary key]
name varchar
}

Table film_genre {
film_genre_id bigint [primary key]
film_id bigint
genre_id int
}

Table film_like {
film_like_id bigint [primary key]
film_id bigint
user_id bigint
}
Table  rating_mpa {
rating_id int [primary key]
name enum ('G', 'PG', 'PG-13',
'R', 'NC-17')


}

Ref: user.user_id < friendship.user_id // one-to-many
Ref: user.user_id < friendship.friend_id // one-to-many
Ref: film.film_id < film_genre.film_id // one-to-many
Ref: genre.genre_id < film_genre.genre_id // one-to-many
Ref: film.film_id < film_like.film_id // one-to-many
Ref: user.user_id < film_like.user_id // one-to-many
Ref: film.rating_id - rating_mpa.rating_id // one-to-one




