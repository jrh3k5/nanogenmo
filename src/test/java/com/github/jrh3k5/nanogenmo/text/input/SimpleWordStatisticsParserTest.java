package com.github.jrh3k5.nanogenmo.text.input;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleWordStatisticsParserTest {
    private final SimpleWordStatisticsParser parser = new SimpleWordStatisticsParser();

    @ParameterizedTest(name = "Testing for {0}")
    @MethodSource("createTestParams")
    public void parseStatistics(TestParams testParams) {
        assertThat(parser.parse(testParams.getLines())).hasSameSizeAs(testParams.getExpected())
                                                       .containsAll(testParams.getExpected());
    }

    private static Stream<TestParams> createTestParams() throws IOException {
        final ObjectReader objectReader = new ObjectMapper().reader();
        try(final InputStream testParamsStream = SimpleWordStatisticsParserTest.class.getResourceAsStream("SimpleWordStatisticsParserTest.testParams.json")) {
            return StreamSupport.stream(objectReader.readTree(testParamsStream).spliterator(), false).map(jsonNode -> {
                final String testName = jsonNode.get("name").asText();
                final Stream<String> lines = StreamSupport.stream(jsonNode.get("lines").spliterator(), false).map(JsonNode::asText);
                final Collection<WordStatistics> wordStatistics = StreamSupport.stream(jsonNode.get("expectedStats").spliterator(), false)
                                                                               .map(n -> {
                                                                                   final String word = n.get("word").asText();
                                                                                   final int count = n.get("count").asInt();
                                                                                   final WordStatistics nStats = new WordStatistics(word, count);

                                                                                   StreamSupport.stream(n.get("childWords").spliterator(), false).forEach(ncw -> {
                                                                                       final WordStatistics.ChildWord childWord = nStats.getChildWord(ncw.get("word").asText());
                                                                                       childWord.increment(ncw.get("count").asInt());
                                                                                   });

                                                                                   return nStats;
                                                                               }).collect(Collectors.toList());
                return new TestParams(testName, lines, wordStatistics);
            });
        }
    }

    @RequiredArgsConstructor
    public static class TestParams {
        @NonNull
        private String testName;
        @NonNull
        @Getter
        private Stream<String> lines;
        @NonNull
        @Getter
        private Collection<WordStatistics> expected;

        @Override
        public String toString() {
            return testName;
        }
    }
}