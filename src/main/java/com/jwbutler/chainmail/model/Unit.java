package com.jwbutler.chainmail.model;

import org.jspecify.annotations.NonNull;

public record Unit(
    @NonNull UnitType type,
    int count
)
{
}
