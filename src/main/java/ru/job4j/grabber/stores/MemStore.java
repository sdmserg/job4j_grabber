package ru.job4j.grabber.stores;

import ru.job4j.grabber.model.Post;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MemStore implements Store {
    private final Map<Long, Post> mem = new HashMap<>();

    @Override
    public void save(Post post) {
        mem.put(post.getId(), post);
    }

    @Override
    public List<Post> getAll() {
        return new ArrayList<>(mem.values());
    }

    @Override
    public Optional<Post> findById(Long id) {
        return Optional.ofNullable(mem.get(id));
    }
}
