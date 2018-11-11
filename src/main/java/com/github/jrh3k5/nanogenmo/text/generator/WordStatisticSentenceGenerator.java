package com.github.jrh3k5.nanogenmo.text.generator;

import com.github.jrh3k5.nanogenmo.text.input.WordStatistics;
import com.github.jrh3k5.nanogenmo.text.input.WordStatisticsParser;
import com.github.jrh3k5.nanogenmo.text.source.TextSource;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class WordStatisticSentenceGenerator implements SentenceGenerator {
    @NonNull
    private TextSource textSource;
    @NonNull
    private WordStatisticsParser wordStatisticsParser;

    @Override
    public String generateSentence() throws IOException {
        final List<WordStatistics> statistics = wordStatisticsParser.parse(textSource.getLines()).collect(Collectors.toList());
        final Map<String, WordStatistics> statisticsMap = statistics.stream().collect(Collectors.toMap(WordStatistics::getWord, Function.identity()));
        final StringBuilder sentenceBuilder = new StringBuilder();
        final WordStatistics startingWord = statistics.stream().filter(w -> w.getSentenceStartCount() > 0 && w.getChildWordCount() > 0).findAny().get();
        sentenceBuilder.append(startingWord.getWord());

        final Supplier<Integer> probabilityGenerator = () -> RandomUtils.nextInt(0, 101);
        Optional<String> nextChildWord = startingWord.getNextChildWord(probabilityGenerator.get());
        if(nextChildWord.isEmpty()) {
            // TODO: make this choose an actual ending character
            sentenceBuilder.append(".");
        }
        WordStatistics nextWord = statisticsMap.get(nextChildWord.get());
        while(sentenceBuilder.length() < 200) {
            sentenceBuilder.append(nextWord.getWord()).append(nextWord.getPostCharacter(probabilityGenerator.get()));
            nextChildWord = nextWord.getNextChildWord(probabilityGenerator.get());
            if(nextChildWord.isEmpty()) {
                break;
            }
            nextWord = statisticsMap.get(nextChildWord.get());
        }

        return sentenceBuilder.toString();
    }
}
