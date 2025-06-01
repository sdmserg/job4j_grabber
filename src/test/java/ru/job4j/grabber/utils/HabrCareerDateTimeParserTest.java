package ru.job4j.grabber.utils;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HabrCareerDateTimeParserTest {

    @Test
    public void whenDateIsEmptyThenThrownIllegalArgumentException() {
        var habrDateParser = new HabrCareerDateTimeParser();
        String vacancyDate = "";
        assertThrows(IllegalArgumentException.class,
                () -> habrDateParser.parse(vacancyDate)
        );
    }

    @Test
    public void whenDataIsBasicIsoDateThenThrownIllegalArgumentException() {
        var habrDateParser = new HabrCareerDateTimeParser();
        String vacancyDate = "20250601";
        assertThrows(IllegalArgumentException.class,
                () -> habrDateParser.parse(vacancyDate)
        );
    }

    @Test
    public void whenDataIsIsoLocalDateThenThrownIllegalArgumentException() {
        var habrDateParser = new HabrCareerDateTimeParser();
        String vacancyDate = "2025-06-01";
        assertThrows(IllegalArgumentException.class,
                () -> habrDateParser.parse(vacancyDate)
        );
    }

    @Test
    public void whenDataIsIsoLocalDateTimeThenThrownIllegalArgumentException() {
        var habrDateParser = new HabrCareerDateTimeParser();
        String vacancyDate = "2025-06-01T10:15:30";
        assertThrows(IllegalArgumentException.class,
                () -> habrDateParser.parse(vacancyDate)
        );
    }

    @Test
    public void whenDataIsLocalizedDateTimeThenThrownIllegalArgumentException() {
        var habrDateParser = new HabrCareerDateTimeParser();
        String vacancyDate = "1 июня 2025 11:05:30";
        assertThrows(IllegalArgumentException.class,
                () -> habrDateParser.parse(vacancyDate)
        );
    }

    @Test
    public void whenDataIsIsoOffsetDateTimeThenParseSuccess() {
        var habrDateParser = new HabrCareerDateTimeParser();
        String vacancyDate = "2025-06-01T10:15:30+01:00";
        LocalDateTime expected = LocalDateTime.of(2025, 6, 1, 10, 15, 30);
        assertEquals(expected, habrDateParser.parse(vacancyDate));
    }

    @Test
    public void whenDataIsHabrCareerFormatThenParseSuccess() {
        var habrDateParser = new HabrCareerDateTimeParser();
        String vacancyDate = "2025-06-01T00:00:00+00:00";
        LocalDateTime expected = LocalDateTime.of(2025, 6, 1, 0, 0, 0);
        assertEquals(expected, habrDateParser.parse(vacancyDate));
    }
}