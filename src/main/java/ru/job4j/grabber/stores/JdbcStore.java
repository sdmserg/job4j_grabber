package ru.job4j.grabber.stores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;

import ru.job4j.grabber.model.Post;

public class JdbcStore implements Store {
    private static final Logger LOG = Logger.getLogger(JdbcStore.class);
    private final Connection connection;

    public JdbcStore(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO posts(name, text, link, created) VALUES (?, ?, ?, ?)"
        )) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setObject(4, LocalDateTime.now());
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
                            resultSet.getString("text"),
                            resultSet.getString("link"),
                            resultSet.getTimestamp("created").getTime()
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
}
