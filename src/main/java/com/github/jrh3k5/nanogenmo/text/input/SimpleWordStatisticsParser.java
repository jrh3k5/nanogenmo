package com.github.jrh3k5.nanogenmo.text.input;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleWordStatisticsParser implements WordStatisticsParser {
    @Override
    public Stream<WordStatistics> parse(Stream<String> lineStream) {
        final Map<String, WordStatistics> wordsStats = new TreeMap<>();
        // Tracks whether or not the previous word ended a sentence
        // Start out as true because it's assumed the first word being read is the beginning of sentence.
        final MutableBoolean previousWasEnd = new MutableBoolean(true);
        final MutableObject<WordStatistics> previousWord = new MutableObject<>();
        lineStream.forEach(line -> {
            final List<String> words = Arrays.stream(line.split(" ")).filter(StringUtils::isNotBlank)
                                                                           .map(String::toUpperCase)
                                                                           .collect(Collectors.toList());
            for(String word : words) {
                final String effectiveWord;
                final char postCharacter;
                if(word.matches(".+[.,?!;]$")) {
                    effectiveWord = word.substring(0, word.length() - 1);
                    postCharacter = word.substring(word.length() - 1).charAt(0);
                } else {
                    effectiveWord = word;
                    postCharacter = ' ';
                }

                final WordStatistics wordStats = wordsStats.computeIfAbsent(effectiveWord, WordStatistics::new);
                wordStats.incrementOccurrenceCount();
                wordStats.getPostCharacter(postCharacter).increment(1);
                if(previousWasEnd.booleanValue()) {
                    wordStats.incrementSentenceStartCount();
                }

                if(previousWord.getValue() != null) {
                    previousWord.getValue().getChildWord(wordStats.getWord()).increment(1);
                }
                previousWord.setValue(wordStats);
                switch(postCharacter) {
                    case '.':
                    case '?':
                    case '!':
                        previousWasEnd.setTrue();
                        // Stop tracking the previous word since the start of the
                        // next sentence shouldn't predicate based on the last word
                        // of the previous sentence
                        previousWord.setValue(null);
                        break;
                    default:
                        previousWasEnd.setFalse();
                }
            }
        });
        return wordsStats.values().stream();
    }
}
