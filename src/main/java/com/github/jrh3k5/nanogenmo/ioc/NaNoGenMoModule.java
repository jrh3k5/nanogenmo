package com.github.jrh3k5.nanogenmo.ioc;

import com.github.jrh3k5.nanogenmo.text.generator.SentenceGenerator;
import com.github.jrh3k5.nanogenmo.text.generator.WordStatisticSentenceGenerator;
import com.github.jrh3k5.nanogenmo.text.input.SimpleWordStatisticsParser;
import com.github.jrh3k5.nanogenmo.text.input.WordStatisticsParser;
import com.google.inject.AbstractModule;

public class NaNoGenMoModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(WordStatisticsParser.class).to(SimpleWordStatisticsParser.class);
        bind(SentenceGenerator.class).to(WordStatisticSentenceGenerator.class);
    }
}
