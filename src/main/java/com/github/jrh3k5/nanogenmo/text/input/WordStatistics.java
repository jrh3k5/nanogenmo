package com.github.jrh3k5.nanogenmo.text.input;

import lombok.*;

import java.util.Map;
import java.util.TreeMap;

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

    @RequiredArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class ChildWord {
        @NonNull
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
