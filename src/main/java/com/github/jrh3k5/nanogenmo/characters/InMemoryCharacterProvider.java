package com.github.jrh3k5.nanogenmo.characters;

import java.util.Arrays;
import java.util.Collection;

public class InMemoryCharacterProvider implements CharacterProvider {
    @Override
    public Collection<Character> getCharacters() {
        return Arrays.asList(new Character("Josh", Gender.MALE), new Character("Mary", Gender.FEMALE), new Character("Zee", Gender.NONBINARY));
    }
}
