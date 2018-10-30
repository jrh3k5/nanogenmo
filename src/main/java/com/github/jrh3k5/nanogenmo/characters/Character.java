package com.github.jrh3k5.nanogenmo.characters;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@Getter
public class Character {
    @NonNull
    private String name;
    @NonNull
    private Gender gender;
}
