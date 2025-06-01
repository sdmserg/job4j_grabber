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
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

public class HabrCareerParse implements Parse {
    private static final Logger LOG = Logger.getLogger(HabrCareerParse.class);
    private static final String SOURCE_LINK = "career.habr.com";
    private static final String PREFIX = "vacancies?page=";
    private static final String SUFFIX = "q=Java+developer&type=all";
    private static final int PAGE_NUMBER = 5;

    @Override
    public List<Post> fetch() {
        var result = new ArrayList<Post>();
        DateTimeParser dateTimeParser = new HabrCareerDateTimeParser();
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
                    String vacancyDate = dateTimeElement.attr("datetime");
                    LocalDateTime dateTimeVacancy = dateTimeParser.parse(vacancyDate);
                    Long time = dateTimeVacancy.toInstant(ZoneOffset.UTC).toEpochMilli();
                    Post post = new Post();
                    post.setTitle(vacancyName);
                    post.setLink(link);
                    post.setTime(time);
                    result.add(post);
                });
            }
        } catch (IOException e) {
            LOG.error("When load page", e);
        }
        return result;
    }
}
