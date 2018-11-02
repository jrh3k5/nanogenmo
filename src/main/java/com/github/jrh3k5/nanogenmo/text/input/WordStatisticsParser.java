package com.github.jrh3k5.nanogenmo.text.input;

import java.util.stream.Stream;

public interface WordStatisticsParser {
    Stream<WordStatistics> parse(Stream<String> lineStream);
}
