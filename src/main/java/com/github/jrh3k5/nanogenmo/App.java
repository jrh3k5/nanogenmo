package com.github.jrh3k5.nanogenmo;

import com.github.jrh3k5.nanogenmo.ioc.NaNoGenMoModule;
import com.github.jrh3k5.nanogenmo.text.generator.SentenceGenerator;
import com.github.jrh3k5.nanogenmo.text.source.DirectoryTextSource;
import com.github.jrh3k5.nanogenmo.text.source.TextSource;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class App {
    public static void main(String...args) throws IOException {
        final TextSource textSource = new DirectoryTextSource(Path.of("src/main/resources"));
        final Injector injector = Guice.createInjector(new NaNoGenMoModule(), new TextSourceModule(textSource));
        System.out.println(injector.getInstance(SentenceGenerator.class).generateSentence());
    }

    private static class TextSourceModule extends AbstractModule {
        private final TextSource textSource;

        TextSourceModule(TextSource textSource) {
            this.textSource = Objects.requireNonNull(textSource, "Text source cannot be null");
        }

        @Override
        protected void configure() {
            bind(TextSource.class).toInstance(textSource);
        }
    }
}
