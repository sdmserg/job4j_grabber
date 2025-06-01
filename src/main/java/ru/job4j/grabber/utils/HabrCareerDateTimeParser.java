package ru.job4j.grabber.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.log4j.Logger;

public class HabrCareerDateTimeParser implements DateTimeParser {
    private static final Logger LOG = Logger.getLogger(HabrCareerDateTimeParser.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public LocalDateTime parse(String parse) {
        try {
            return LocalDateTime.parse(parse, FORMATTER);
        } catch (DateTimeParseException ex) {
            LOG.error("When parse date", ex);
            throw new IllegalArgumentException(String.format(
                    "Invalid date format %s", parse
            ), ex);
        }
    }
}