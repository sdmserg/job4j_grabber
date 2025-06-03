package ru.job4j;

import org.apache.log4j.Logger;

import ru.job4j.grabber.service.*;
import ru.job4j.grabber.stores.JdbcStore;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

public class Main {
    private static final Logger LOG = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            var config = new Config();
            config.load("application.properties");
            var store = new JdbcStore(config);
            var parse = new HabrCareerParse(new HabrCareerDateTimeParser());
            var scheduler = new SchedulerManager();
            scheduler.init();
            scheduler.load(
                    Integer.parseInt(config.get("rabbit.interval")),
                    HabrCareerGrab.class,
                    store,
                    parse
            );
            new Web(store).start(Integer.parseInt(config.get("server.port")));
        } catch (NumberFormatException ex) {
            throw new IllegalStateException(
                    "Property 'rabbit.interval' and 'server.port' must be integer", ex);
        } catch (Exception ex) {
            LOG.error("Unexpected error occurred", ex);
        }
    }
}
