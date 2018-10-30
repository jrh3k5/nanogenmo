package com.github.jrh3k5.nanogenmo.ioc;

import com.github.jrh3k5.nanogenmo.characters.CharacterProvider;
import com.github.jrh3k5.nanogenmo.characters.InMemoryCharacterProvider;
import com.google.inject.AbstractModule;

public class NaNoGenMoModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(CharacterProvider.class).to(InMemoryCharacterProvider.class);
    }
}
