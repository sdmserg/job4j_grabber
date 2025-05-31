package ru.job4j.grabber.service;

import java.util.List;

import ru.job4j.grabber.model.Post;

public interface Parse {
    List<Post> fetch();
}