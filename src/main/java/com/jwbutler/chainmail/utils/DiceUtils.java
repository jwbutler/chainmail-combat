package com.jwbutler.chainmail.utils;

import java.util.List;
import java.util.Random;

import org.jspecify.annotations.NonNull;

public final class DiceUtils
{
    private static final Random RNG = new Random();
    
    private DiceUtils() {}
    
    @NonNull
    public static List<Integer> rollDice(int count)
    {
        return RNG.ints(count, 1, 7)
            .boxed()
            .toList();
    }
    
    public static int rollDie()
    {
        return RNG.nextInt(6) + 1;
    }
}
