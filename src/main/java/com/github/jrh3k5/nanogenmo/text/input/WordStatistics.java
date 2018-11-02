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
    private int count = 0;
    private final Map<String, ChildWord> childrenWords = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public void increment(int delta) {
        count += delta;
    }

    public ChildWord getChildWord(String word) {
        return childrenWords.computeIfAbsent(word, ChildWord::new);
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
}
