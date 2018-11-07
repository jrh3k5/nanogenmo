package com.github.jrh3k5.nanogenmo.text.source;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class DirectoryTextSource implements TextSource {
    private final Path directory;

    public DirectoryTextSource(Path directory) {
        this.directory = Objects.requireNonNull(directory, "Directory cannot be null");
    }

    @Override
    public Stream<String> getLines() throws IOException {
        final List<Path> textFiles = Files.find(directory, 1, (path, basicFileAttributes) -> path.getFileName().toString().endsWith(".txt")).collect(Collectors.toList());
        if(textFiles.isEmpty()) {
            return Stream.empty();
        }

        if(textFiles.size() == 1) {
            return Files.lines(textFiles.get(0));
        }

        final String firstPath = textFiles.get(0).toAbsolutePath().toString();
        final String[] otherPaths = textFiles.subList(1, textFiles.size()).stream()
                                                                          .map(Path::toAbsolutePath)
                                                                          .map(Path::toString)
                                                                          .collect(Collectors.toList())
                                                                          .toArray(new String[0]);
        return Files.lines(Paths.get(firstPath, otherPaths));
    }
}
