package com.jwbutler.chainmail;

import com.jwbutler.chainmail.MeleeController.MeleeDamageResult;
import com.jwbutler.chainmail.model.Unit;
import com.jwbutler.chainmail.model.UnitType;
import org.jspecify.annotations.NonNull;

import static com.jwbutler.chainmail.utils.DiceUtils.rollDie;

public final class PostMeleeMoraleController
{
    public enum Outcome
    {
        MELEE_CONTINUES,
        ATTACKER_BACK_2_MOVES_GOOD_ORDER,
        ATTACKER_BACK_1_MOVE_GOOD_ORDER,
        ATTACKER_RETREATS_1_MOVE,
        ATTACKER_ROUTS_1_5_MOVES,
        ATTACKER_SURRENDERS,
        DEFENDER_BACK_2_MOVES_GOOD_ORDER,
        DEFENDER_BACK_1_MOVE_GOOD_ORDER,
        DEFENDER_RETREATS_1_MOVE,
        DEFENDER_ROUTS_1_5_MOVES,
        DEFENDER_SURRENDERS
    }
    
    public record PostMeleeMoraleResult(
        int attackerScore,
        int defenderScore,
        int dieRoll,
        @NonNull Outcome outcome
    )
    {
    }

    /**
     * @param attacker The attacking unit *after* taking melee damage
     * @param defender The defending unit *after* taking melee damage
     */
    @NonNull
    public PostMeleeMoraleResult resolvePostMeleeMorale(
        @NonNull Unit attacker,
        @NonNull Unit defender,
        @NonNull MeleeDamageResult meleeResult
    )
    {
        // 1. The side with the fewer casualties determines the positive difference between
        // their losses and those suffered by the enemy. This number is then multiplied
        // by the score of a die roll and the total noted.
        int attackerLosses = meleeResult.defenderKills();
        int defenderLosses = meleeResult.attackerKills();
        int lossesDifference = Math.abs(attackerLosses - defenderLosses);
        var dieRoll = rollDie();
        int lossDifferenceTimesDiceRoll = lossesDifference * dieRoll;
        
        // 2. The side with the greater number of surviving troops which were involved in
        // the melee determines the positive difference between the number of his troops
        // and those of the enemy. This number is noted.
        
        int survivingTroopsDifference = Math.abs(attacker.count() - defender.count());
        
        // 3. Each side now multiplies their surviving figures, separating them by type
        // if more than one type is involved, by the following "Morale Rating" factors:
        // <snip, see below>
        
        int attackerScore = attacker.count() * _getMoraleRatingFactor(attacker.type());
        int defenderScore = defender.count() * _getMoraleRatingFactor(defender.type());
        
        // Add results from step 1 now.
        if (attackerLosses < defenderLosses)
        {
            attackerScore += lossDifferenceTimesDiceRoll; 
        }
        else if (defenderLosses < attackerLosses)
        {
            defenderScore += lossDifferenceTimesDiceRoll;
        }
        else
        {
            // In typical fashion, the manual does not specify what to do if the losses are equal.
            // Let's do nothing.   
        }
        
        // Add results from step 2 now.
        if (attacker.count() > defender.count())
        {
            attackerScore += survivingTroopsDifference;
        }
        else if (defender.count() > attacker.count())
        {
            defenderScore += survivingTroopsDifference;
        }
        else
        {
            // 0, no-op anyway
        }

        var scoreDiff = Math.abs(attackerScore - defenderScore);
        final Outcome outcome;
        if (attackerScore > defenderScore)
        {
            if (scoreDiff <= 19)
            {
                outcome = Outcome.MELEE_CONTINUES;
            }
            else if (scoreDiff <= 39)
            {
                outcome = Outcome.DEFENDER_BACK_2_MOVES_GOOD_ORDER;
            }
            else if (scoreDiff <= 59)
            {
                outcome = Outcome.DEFENDER_BACK_1_MOVE_GOOD_ORDER;
            }
            else if (scoreDiff <= 79)
            {
                outcome = Outcome.DEFENDER_RETREATS_1_MOVE;
            }
            else if (scoreDiff <= 99)
            {
                outcome = Outcome.DEFENDER_ROUTS_1_5_MOVES;
            }
            else
            {
                outcome = Outcome.DEFENDER_SURRENDERS;
            }
        }
        else
        {
            if (scoreDiff <= 19)
            {
                outcome = Outcome.MELEE_CONTINUES;
            }
            else if (scoreDiff <= 39)
            {
                outcome = Outcome.ATTACKER_BACK_2_MOVES_GOOD_ORDER;
            }
            else if (scoreDiff <= 59)
            {
                outcome = Outcome.ATTACKER_BACK_1_MOVE_GOOD_ORDER;
            }
            else if (scoreDiff <= 79)
            {
                outcome = Outcome.ATTACKER_RETREATS_1_MOVE;
            }
            else if (scoreDiff <= 99)
            {
                outcome = Outcome.ATTACKER_ROUTS_1_5_MOVES;
            }
            else
            {
                outcome = Outcome.ATTACKER_SURRENDERS;
            }
        }
        
        return new PostMeleeMoraleResult(attackerScore, defenderScore, dieRoll, outcome);
    }
    
    private static int _getMoraleRatingFactor(@NonNull UnitType unitType)
    {
        return switch (unitType)
        {
            case LIGHT_FOOT -> 4;
            case HEAVY_FOOT -> 5;
            case ARMORED_FOOT -> 7;
            case LIGHT_HORSE -> 6;
            case MEDIUM_HORSE -> 8;
            case HEAVY_HORSE -> 9;
        };
    }
}
