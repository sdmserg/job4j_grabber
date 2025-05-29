package ru.job4j;

import java.sql.Connection;
import java.sql.DriverManager;

import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.service.Config;
import ru.job4j.grabber.service.SchedulerManager;
import ru.job4j.grabber.service.SuperJobGrab;
import ru.job4j.grabber.stores.JdbcStore;

public class Main {
    public static void main(String[] args) {
        var config = new Config();
        config.load("application.properties");
        Connection connection = initConnection(config);
        var store = new JdbcStore(connection);
        var post = new Post();
        post.setTitle("Java Developer");
        post.setDescription("Приглашаем разработчика Java");
        post.setLink("https://career.habr.com/vacancies/1000155112");
        store.save(post);
        var scheduler = new SchedulerManager();
        scheduler.init();
        scheduler.load(
                Integer.parseInt(config.get("rabbit.interval")),
                SuperJobGrab.class,
                store
        );
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
            throw new IllegalStateException("Failed to connect DB", ex);
        }
    }
}
