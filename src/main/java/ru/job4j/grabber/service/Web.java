package ru.job4j.grabber.service;

import io.javalin.Javalin;

import ru.job4j.grabber.stores.Store;

public class Web {
    private final Store store;

    public Web(Store store) {
        this.store = store;
    }

    public void start(int port) {
        var app = Javalin.create();
        app.start("0.0.0.0", port);
        var page = new StringBuilder();
        store.getAll().forEach(post -> page.append(post.toString()).append(System.lineSeparator().repeat(3)));
        app.get("/", ctx -> {
            ctx.contentType("text/plain; charset=UTF-8");
            ctx.result(page.toString());
        });
    }
}
