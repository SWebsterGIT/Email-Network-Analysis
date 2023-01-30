package cpen221.mp2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class Task4DWTests {

    private static DWInteractionGraph dwig1;
    private static DWInteractionGraph dwig2;
    private static DWInteractionGraph dwig3;
    private static DWInteractionGraph testAssist1;
    private static DWInteractionGraph timeSeparatedChains;
    private static DWInteractionGraph massiveFile;

    @BeforeAll
    public static void setupTests() {
        dwig1 = new DWInteractionGraph("resources/Task4Transactions1.txt");
        dwig2 = new DWInteractionGraph("resources/Task4Transactions2.txt");
        dwig3 = new DWInteractionGraph("resources/Task4Transactions3.txt");
        testAssist1 = new DWInteractionGraph("resources/task4testingAssist");
        timeSeparatedChains = new DWInteractionGraph("resources/task4timeSep.txt");
        massiveFile = new DWInteractionGraph("resources/email-Eu-core-temporal-Dept1.txt");
    }


    @Test
    public void testSimultaneousSends() {
        Assertions.assertEquals(6, testAssist1.MaxBreachedUserCount(2));
    }

    @Test
    public void testMassiveInputForTimingOnly() {
        // 41 is a reasonable answer, but this cannot be reasoned properly
        // since the dataset is so large. Runs in around 10s on SW's machine.
        Assertions.assertEquals(41, massiveFile.MaxBreachedUserCount(10));
    }


    @Test
    public void testTimeSepChains() {
        // see task4timeSep.txt for SW created data. Connections are blocked or included
        // depending on the firewall
        Assertions.assertEquals(9, timeSeparatedChains.MaxBreachedUserCount(1));
        Assertions.assertEquals(11, timeSeparatedChains.MaxBreachedUserCount(2));
        Assertions.assertEquals(17, timeSeparatedChains.MaxBreachedUserCount(3));
        Assertions.assertEquals(18, timeSeparatedChains.MaxBreachedUserCount(30));
    }

    @Test
    public void testMaxedBreachedUserCount1() {
        // Attacking user 7 any time in the window [0,120] will pollute 8 users in a 2 hour window.
        Assertions.assertEquals(8, dwig1.MaxBreachedUserCount(2));
    }

    @Test
    public void testMaxedBreachedUserCount2() {
        // Attacking user 3 at t=0, or attacking user 5 any time in the window [0,60] will pollute
        // 10 users in a 4 hour window.
        Assertions.assertEquals(10, dwig2.MaxBreachedUserCount(4));
    }

    @Test
    public void testMaxedBreachedUserCount3() {
        // Attacking user 4 at t=3600 will lead to users 4, 5, 6, 3, and 1 (5 users) to be polluted
        // in a 6-hour-long window after the attack starts.
        Assertions.assertEquals(5, dwig3.MaxBreachedUserCount(6));
    }
}
