package com.github.jrh3k5.nanogenmo.text.input;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleWordStatisticsParser implements WordStatisticsParser {
    @Override
    public Stream<WordStatistics> parse(Stream<String> lineStream) {
        final Map<String, WordStatistics> wordsStats = new TreeMap<>();
        lineStream.forEach(line -> {
            final List<String> words = Arrays.stream(line.split(" ")).filter(StringUtils::isNotBlank)
                                                                           .map(String::toUpperCase)
                                                                           .collect(Collectors.toList());
            WordStatistics previousWord = null;
            // Tracks whether or not the previous word ended a sentence
            // Start out as true because it's assumed the first word being read is the beginning of sentence
            boolean previousWasEnd = true;
            for(String word : words) {
                final String effectiveWord;
                final char postCharacter;
                if(word.matches(".+[.,?!]$")) {
                    effectiveWord = word.substring(0, word.length() - 1);
                    postCharacter = word.substring(word.length() - 1).charAt(0);
                } else {
                    effectiveWord = word;
                    postCharacter = ' ';
                }

                final WordStatistics wordStats = wordsStats.computeIfAbsent(effectiveWord, WordStatistics::new);
                wordStats.incrementOccurrenceCount(1);
                wordStats.getPostCharacter(postCharacter).increment(1);
                if(previousWasEnd) {
                    wordStats.incrementSentenceStartCount(1);
                }

                if(previousWord != null) {
                    previousWord.getChildWord(wordStats.getWord()).increment(1);
                }
                previousWord = wordStats;
                switch(postCharacter) {
                    case '.':
                    case '?':
                    case '!':
                        previousWasEnd = true;
                        break;
                    default:
                        previousWasEnd = false;
                }
            }
        });
        return wordsStats.values().stream();
    }
}
