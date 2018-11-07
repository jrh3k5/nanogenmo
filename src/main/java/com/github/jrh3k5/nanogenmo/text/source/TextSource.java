package com.github.jrh3k5.nanogenmo.text.source;

import java.io.IOException;
import java.util.stream.Stream;

public interface TextSource {
    Stream<String> getLines() throws IOException;
}
