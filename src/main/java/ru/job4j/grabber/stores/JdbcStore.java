package ru.job4j.grabber.stores;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;

import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.service.Config;

public class JdbcStore implements Store {
    private static final Logger LOG = Logger.getLogger(JdbcStore.class);
    private final Connection connection;

    public JdbcStore(Config config) {
        this.connection = initConnection(config);
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO posts(name, text, link, created) VALUES (?, ?, ?, ?) ON CONFLICT (link) DO NOTHING"
        )) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setObject(4, post.getTime());
            statement.execute();
        } catch (SQLException ex) {
            LOG.error(String.format("When save post with title: %s  and link: %s",
                    post.getTitle(),
                    post.getLink()
                    ), ex);
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT id, name, text, link, created FROM posts"
        )) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    posts.add(new Post(
                            resultSet.getLong("id"),
                            resultSet.getString("name"),
                            resultSet.getString("link"),
                            resultSet.getString("text"),
                            resultSet.getLong("created")
                    ));
                }
            }
        } catch (SQLException ex) {
            LOG.error("When load all posts", ex);
        }
        return posts;
    }

    @Override
    public Optional<Post> findById(Long id) {
        Post post = null;
        try (PreparedStatement statement = connection.prepareStatement(
                    "select id, name, text, link, created FROM posts WHERE id = ?"
        )) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    post = new Post(
                            resultSet.getLong("id"),
                            resultSet.getString("name"),
                            resultSet.getString("text"),
                            resultSet.getString("link"),
                            resultSet.getTimestamp("created").getTime()
                    );
                }
            }
        } catch (SQLException ex) {
            LOG.error(String.format(
                    "When find post by id: %s",
                    id), ex);
        }
        return Optional.ofNullable(post);
    }

    private static Connection initConnection(Config config) {
        try {
            Class.forName(config.get("db.driver-class-name"));
            return DriverManager.getConnection(
                    config.get("db.url"),
                    config.get("db.username"),
                    config.get("db.password")
            );
        } catch (Exception ex) {
            LOG.error("Failed to connection DB", ex);
            throw new IllegalStateException("Failed to connect DB", ex);
        }
    }
}
