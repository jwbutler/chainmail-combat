package com.jwbutler.chainmail;

import java.util.List;

import com.jwbutler.chainmail.model.Unit;
import org.jspecify.annotations.NonNull;

import static com.jwbutler.chainmail.utils.DiceUtils.rollDice;

public final class MeleeController
{
    public record MeleeDamageResult(
        @NonNull List<Integer> attackerDiceRolled,
        @NonNull List<Integer> defenderDiceRolled,
        int attackerKillThreshold,
        int defenderKillThreshold,
        int attackerKills,
        int defenderKills
    )
    {
    }
    
    @NonNull
    public MeleeDamageResult resolveMeleeDamage(@NonNull Unit attacker, @NonNull Unit defender)
    {
        int attackerDiceToRoll = _getNumDiceToRoll(attacker, defender);
        List<Integer> attackerDiceRolled = rollDice(attackerDiceToRoll);
        int defenderDiceToRoll = _getNumDiceToRoll(defender, attacker);
        List<Integer> defenderDiceRolled = rollDice(defenderDiceToRoll);
        int attackerKillThreshold = _getKillThreshold(attacker, defender);
        int defenderKillThreshold = _getKillThreshold(defender, attacker);
        int attackerKills = _countKills(attackerDiceRolled, attackerKillThreshold, defender.count());
        int defenderKills = _countKills(defenderDiceRolled, defenderKillThreshold, attacker.count());

        return new MeleeDamageResult(
            attackerDiceRolled,
            defenderDiceRolled,
            attackerKillThreshold,
            defenderKillThreshold,
            attackerKills,
            defenderKills
        );
    }

    private static int _countKills(@NonNull List<Integer> diceRolled, int killThreshold, int numEnemies)
    {
        int kills = (int) diceRolled.stream()
            .mapToInt(Integer::intValue)
            .filter(value -> value >= killThreshold)
            .count();
        return Math.min(kills, numEnemies);
    }

    private static int _getNumDiceToRoll(@NonNull Unit attacker, @NonNull Unit defender)
    {
        double factor = switch (attacker.type())
        {
            case LIGHT_FOOT -> switch (defender.type())
            {
                case LIGHT_FOOT -> 1.0;
                case HEAVY_FOOT -> 0.5;
                case ARMORED_FOOT -> 1.0 / 3;
                case LIGHT_HORSE -> 0.5;
                case MEDIUM_HORSE -> 1.0 / 3;
                case HEAVY_HORSE -> 0.25;
            };
            case HEAVY_FOOT -> switch (defender.type())
            {
                case LIGHT_FOOT, HEAVY_FOOT -> 1.0;
                case ARMORED_FOOT, LIGHT_HORSE -> 0.5;
                case MEDIUM_HORSE -> 1.0 / 3;
                case HEAVY_HORSE -> 0.25;
            };
            case ARMORED_FOOT -> switch (defender.type())
            {
                case LIGHT_FOOT, HEAVY_FOOT, ARMORED_FOOT, LIGHT_HORSE -> 1.0;
                case MEDIUM_HORSE -> 0.5;
                case HEAVY_HORSE -> 1.0 / 3;
            };
            case LIGHT_HORSE -> switch (defender.type())
            {
                case LIGHT_FOOT, HEAVY_FOOT -> 2.0;
                case ARMORED_FOOT, LIGHT_HORSE -> 1.0;
                case MEDIUM_HORSE -> 0.5;
                case HEAVY_HORSE -> 1.0 / 3;
            };
            case MEDIUM_HORSE -> switch (defender.type())
            {
                case LIGHT_FOOT, HEAVY_FOOT, ARMORED_FOOT -> 2.0;
                case LIGHT_HORSE, MEDIUM_HORSE -> 1.0;
                case HEAVY_HORSE -> 0.5;
            };
            case HEAVY_HORSE -> switch (defender.type())
            {
                case LIGHT_FOOT -> 4.0;
                case HEAVY_FOOT -> 3.0;
                case ARMORED_FOOT, LIGHT_HORSE -> 2.0;
                case MEDIUM_HORSE, HEAVY_HORSE -> 1.0;
            };
        };
        
        return (int) Math.floor(factor * attacker.count());
    }
    
    private static int _getKillThreshold(@NonNull Unit attacker, @NonNull Unit defender)
    {
        return switch (attacker.type())
        {
            case LIGHT_FOOT -> 6;
            case HEAVY_FOOT -> switch (defender.type())
            {
                case LIGHT_FOOT -> 5;
                default -> 6;
            };
            case ARMORED_FOOT -> switch (defender.type())
            {
                case LIGHT_FOOT -> 4;
                case HEAVY_FOOT -> 5;
                default -> 6;
            };
            case LIGHT_HORSE -> switch (defender.type())
            {
                case LIGHT_FOOT -> 5;
                default -> 6;
            };
            case MEDIUM_HORSE -> switch (defender.type())
            {
                case LIGHT_FOOT -> 5;
                case HEAVY_FOOT, LIGHT_HORSE -> 5;
                default -> 6;
            };
            case HEAVY_HORSE -> switch (defender.type())
            {
                case HEAVY_HORSE -> 6;
                default -> 5;
            };
        };
    }
}
