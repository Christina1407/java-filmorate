package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedStorage {

    List<Feed> findUsersFeed(Long userId);

    Feed save(Feed feed);

}
