package ru.job4j.grabber.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;

import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.utils.DateTimeParser;

public class HabrCareerParse implements Parse {
    private static final Logger LOG = Logger.getLogger(HabrCareerParse.class);
    private static final String SOURCE_LINK = "career.habr.com";
    private static final String PREFIX = "vacancies?page=";
    private static final String SUFFIX = "q=Java+developer&type=all";
    private static final int PAGE_NUMBER = 5;
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> fetch() {
        var result = new ArrayList<Post>();
        try {
            for (int pageNumber = 1; pageNumber <= PAGE_NUMBER; pageNumber++)  {
                String fullLink = "%s%s%d%s".formatted(SOURCE_LINK, PREFIX, pageNumber, SUFFIX);
                var connection = Jsoup.connect(fullLink);
                var document = connection.get();
                var rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> {
                    var titleElement = row.select(".vacancy-card__title").first();
                    var linkElement = titleElement.child(0);
                    var dateElement = row.select(".vacancy-card__date").first();
                    var dateTimeElement = dateElement.child(0);
                    String vacancyName = titleElement.text();
                    String link = String.format("%s%s", SOURCE_LINK,
                            linkElement.attr("href"));
                    String description = retrieveDescription(link);
                    String vacancyDate = dateTimeElement.attr("datetime");
                    LocalDateTime dateTimeVacancy = dateTimeParser.parse(vacancyDate);
                    Long time = dateTimeVacancy.toInstant(ZoneOffset.UTC).toEpochMilli();
                    Post post = new Post();
                    post.setTitle(vacancyName);
                    post.setLink(link);
                    post.setDescription(description);
                    post.setTime(time);
                    result.add(post);
                });
            }
        } catch (IOException e) {
            LOG.error("When load page", e);
        }
        return result;
    }

    private String retrieveDescription(String link) {
        String description = "";
        try {
            var connection = Jsoup.connect(link);
            var document = connection.get();
            var row = document.select(".vacancy-description__text").first();
            description = row.text();
        } catch (IOException ex) {
            LOG.error("When parse description vacancy", ex);
        }
        return description;
    }
}
