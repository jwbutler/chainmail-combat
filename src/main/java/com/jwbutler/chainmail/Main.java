package com.jwbutler.chainmail;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

import com.jwbutler.chainmail.model.Unit;
import com.jwbutler.chainmail.model.UnitType;
import org.jspecify.annotations.NonNull;

public final class Main
{
    private static final PrintStream PRINTER = System.out;
    private static final Scanner SCANNER = new Scanner(System.in);
    
    public static void main(@NonNull String[] args)
    {
        var attackerType = _inputUnitType("Enter attacker unit type: " + Arrays.toString(UnitType.values()));
        var attackerCount = _inputInt("Enter attacker count:");
        var defenderType = _inputUnitType("Enter defender unit type: " + Arrays.toString(UnitType.values()));
        var defenderCount = _inputInt("Enter defender count:");
        var attacker = new Unit(attackerType, attackerCount);
        var defender = new Unit(defenderType, defenderCount);
        
        PRINTER.println("===== Melee =====");
        PRINTER.println("Attacker: " + attacker);
        PRINTER.println("Defender: " + defender);
        PRINTER.println();
        var meleeController = new MeleeController();
        var result = meleeController.resolveMeleeDamage(attacker, defender);
        PRINTER.println("Attacker dice rolled: " + result.attackerDiceRolled());
        PRINTER.println("Defender dice rolled: " + result.defenderDiceRolled());
        PRINTER.println("Attacker kills: " + result.attackerKills());
        PRINTER.println("Defender kills: " + result.defenderKills());
        PRINTER.println();
        // it's an error if kills > count, so don't check for it here
        attacker = new Unit(attackerType, attackerCount - result.defenderKills());
        defender = new Unit(defenderType, defenderCount - result.attackerKills());

        PRINTER.println("===== Post-Melee Morale =====");
        PRINTER.println("Attacker: " + attacker);
        PRINTER.println("Defender: " + defender);
        var postMeleeMoraleController = new PostMeleeMoraleController();
        var postMeleeMoraleResult = postMeleeMoraleController.resolvePostMeleeMorale(attacker, defender, result);
        PRINTER.println("Attacker score: " + postMeleeMoraleResult.attackerScore());
        PRINTER.println("Defender score: " + postMeleeMoraleResult.defenderScore());
        PRINTER.println("Outcome: " + postMeleeMoraleResult.outcome());
    }

    @NonNull
    private static UnitType _inputUnitType(@NonNull String prompt)
    {
        while (true)
        {
            PRINTER.println(prompt);
            var value = SCANNER.nextLine();
            try
            {
                return UnitType.valueOf(value.toUpperCase());
            }
            catch (RuntimeException e)
            {
                PRINTER.println("Invalid unit type: " + value);
            }
        }
    }

    private static int _inputInt(@NonNull String prompt)
    {
        while (true)
        {
            PRINTER.println(prompt);
            var value = SCANNER.nextLine();
            try
            {
                var parsed = Integer.parseInt(value);
                if (parsed <= 0)
                {
                    throw new RuntimeException();
                }
                return parsed;
            }
            catch (RuntimeException e)
            {
                PRINTER.println("Invalid count: " + value);
            }
        }
    }
}
