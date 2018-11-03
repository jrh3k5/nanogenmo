package com.github.jrh3k5.nanogenmo.text.input;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleWordStatisticsParserTest {
    private final SimpleWordStatisticsParser parser = new SimpleWordStatisticsParser();

    @ParameterizedTest(name = "Testing for {0}")
    @MethodSource("createTestParams")
    void parseStatistics(TestParams testParams) {
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
                                                                                   final int occurrenceCount = n.get("count").asInt();
                                                                                   final int sentenceStartCount = n.has("sentenceStartCount") ? n.get("sentenceStartCount").asInt() : 0;
                                                                                   final WordStatistics nStats = new WordStatistics(word, occurrenceCount, sentenceStartCount);

                                                                                   StreamSupport.stream(n.get("childWords").spliterator(), false).forEach(ncw -> {
                                                                                       final WordStatistics.ChildWord childWord = nStats.getChildWord(ncw.get("word").asText());
                                                                                       childWord.increment(ncw.get("count").asInt());
                                                                                   });

                                                                                   // If the post characters aren't present, then just infer that the test should look for spaces equal to the number of instances of this word
                                                                                   if(!n.has("postCharacters")) {
                                                                                       nStats.getPostCharacter(' ').increment(occurrenceCount);
                                                                                   } else {
                                                                                       StreamSupport.stream(n.get("postCharacters").spliterator(), false).forEach(pc -> {
                                                                                           final WordStatistics.PostCharacter postCharacter = nStats.getPostCharacter(pc.get("character").asText().charAt(0));
                                                                                           postCharacter.increment(pc.get("count").asInt());
                                                                                       });
                                                                                   }


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