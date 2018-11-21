package com.github.jrh3k5.nanogenmo.text.input;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class WordStatistics {
    private static int calculateFrequency(int occurrence, int total) {
        return (int) Math.ceil((occurrence / (double) total) * 100);
    }

    @NonNull
    @Getter
    private String word;
    @Getter
    private int occurrenceCount = 0;
    private final Map<String, ChildWord> childrenWords = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<Character, PostCharacter> postCharacters = new TreeMap<>();
    @Getter
    private int sentenceStartCount;

    void incrementOccurrenceCount() {
        occurrenceCount++;
    }

    void incrementSentenceStartCount() {
        sentenceStartCount++;
    }

    ChildWord getChildWord(String word) {
        return childrenWords.computeIfAbsent(word, ChildWord::new);
    }

    PostCharacter getPostCharacter(char character) {
        return postCharacters.computeIfAbsent(character, PostCharacter::new);
    }

    public PostCharacter getPostCharacter(int probability) {
        return getElement(postCharacters, Function.identity(), probability).orElseThrow();
    }

    public int getChildWordCount() {
        return childrenWords.size();
    }

    // TODO: this is not how this should be done, ultimately
    public Optional<String> getNextChildWord(int probability) {
        return getElement(childrenWords, ChildWord::getWord, probability);
    }

    private static <K, V extends Countable, O> Optional<O> getElement(Map<K, V> map, Function<V, O> outputExtractor, int probability) {
        // Make sure that the probability is between 0 and 100
        final int actualProbability = Math.max(0, Math.min(probability, 100));
        final int totalChildrenOccurrences = map.values().stream().map(Countable::getCount).reduce(0, (a, b) -> a + b);
        if(totalChildrenOccurrences == 0) {
            return Optional.empty();
        }

        final Supplier<Multimap<Integer, V>> supplier = ArrayListMultimap::create;
        final BiConsumer<Multimap<Integer, V>, V> accumulator = (destination, value) -> destination.put(calculateFrequency(value.getCount(), totalChildrenOccurrences), value);
        final BinaryOperator<Multimap<Integer, V>> combiner = (map1, map2) -> {
            map1.putAll(map2);
            return map1;
        };
        final Multimap<Integer, V> childWordDistributions = map.values().stream().collect(Collector.of(supplier, accumulator, combiner));
        if(childWordDistributions.keySet().stream().noneMatch(i -> i >= actualProbability)) {
            final List<O> wordsList = childWordDistributions.values().stream().map(outputExtractor).collect(Collectors.toList());
            Collections.shuffle(wordsList);
            return Optional.of(wordsList.get(0));
        }

        final List<V> candidates = childWordDistributions.entries().stream().filter(kv -> kv.getKey() >= actualProbability).map(Map.Entry::getValue).collect(Collectors.toList());
        // TODO: make this a generated random index?
        Collections.shuffle(candidates);
        return Optional.of(outputExtractor.apply(candidates.get(0)));
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
    static class ChildWord implements Countable {
        @NonNull
        @Getter
        private String word;
        @Getter
        private int count = 0;

        void increment(int delta) {
            count += delta;
        }
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class PostCharacter implements Countable {
        @Getter
        private char character;
        @Getter
        private int count = 0;

        PostCharacter(char character) {
            this.character = character;
        }

        void increment(int delta) {
            count += delta;
        }

        public boolean isEndingPunctuation() {
            switch(character) {
                case '.':
                case '?':
                case '!':
                case ';':
                case ',':
                    return true;
                default:
                    return false;
            }
        }
    }

    public interface Countable {
        int getCount();
    }
}
