package com.github.jrh3k5.nanogenmo.text.input;

import lombok.*;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class WordStatistics {
    @NonNull
    @Getter
    private String word;
    @Getter
    private int occurrenceCount = 0;
    private final Map<String, ChildWord> childrenWords = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<Character, PostCharacter> postCharacters = new TreeMap<>();
    @Getter
    private int sentenceStartCount;

    public void incrementOccurrenceCount(int delta) {
        occurrenceCount += delta;
    }

    public void incrementSentenceStartCount(int delta) {
        sentenceStartCount += delta;
    }

    public ChildWord getChildWord(String word) {
        return childrenWords.computeIfAbsent(word, ChildWord::new);
    }

    public PostCharacter getPostCharacter(char character) {
        return postCharacters.computeIfAbsent(character, PostCharacter::new);
    }

    public boolean hasChildWord(String word) {
        return childrenWords.containsKey(word);
    }

    public int getChildWordCount() {
        return childrenWords.size();
    }

    // TODO: this is not how this should be done, ultimately
    public Optional<String> getNextChildWord(int probability) {
        // Make sure that the probability is between 0 and 100
        final int actualProbability = Math.max(0, Math.min(probability, 100));
        final int totalChildrenOccurrences = childrenWords.values().stream().map(ChildWord::getCount).reduce(0, (a, b) -> a + b)
        if(totalChildrenOccurrences == 0) {
            return Optional.empty();
        }

        final Map<Integer, ChildWord> childWordDistributions = childrenWords.values().stream().collect(Collectors.toMap(c -> (c.getCount() / totalChildrenOccurrences) * 100, Function.identity()));
        if(childWordDistributions.keySet().stream().noneMatch(i -> i >= actualProbability)) {
            final List<String> wordsList = childWordDistributions.values().stream().map(ChildWord::getWord).collect(Collectors.toList());
            Collections.shuffle(wordsList);
            return Optional.of(wordsList.get(0));
        }

        final List<ChildWord> candidates = childWordDistributions.entrySet().stream().filter(kv -> kv.getKey() >= actualProbability).map(Map.Entry::getValue).collect(Collectors.toList());
        Collections.shuffle(candidates);
        return Optional.of(candidates.get(0).getWord());
    }

    // TODO: is this how we want to do this?
    public boolean hasEndingPunctuation() {
        return postCharacters.keySet().stream().anyMatch(c -> {
            switch(c) {
                case '.':
                case '?':
                case '!':
                    return true;
                default:
                    return false;
            }
        });
    }

    @RequiredArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class ChildWord {
        @NonNull
        @Getter
        private String word;
        @Getter
        private int count = 0;

        public void increment(int delta) {
            count += delta;
        }
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class PostCharacter {
        private char character;
        @Getter
        private int count = 0;

        public PostCharacter(char character) {
            this.character = character;
        }

        public void increment(int delta) {
            count += delta;
        }
    }
}
