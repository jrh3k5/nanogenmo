package com.github.jrh3k5.nanogenmo;

import com.github.jrh3k5.nanogenmo.characters.CharacterProvider;
import com.github.jrh3k5.nanogenmo.ioc.NaNoGenMoModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class App {
    public static void main(String...args) {
        final Injector injector = Guice.createInjector(new NaNoGenMoModule());
        final CharacterProvider characterProvider = injector.getInstance(CharacterProvider.class);
        characterProvider.getCharacters().forEach(System.out::println);
    }
}
