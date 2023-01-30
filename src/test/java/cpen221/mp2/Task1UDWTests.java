package cpen221.mp2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Task1UDWTests {
    private static UDWInteractionGraph testGraphBase;
    private static UDWInteractionGraph testGraphBase1;
    private static UDWInteractionGraph testGraph1;
    private static UDWInteractionGraph testGraph2;
    private static UDWInteractionGraph testGraph3;
    private static UDWInteractionGraph testGraph4;
    private static UDWInteractionGraph testGraph5;
    private static UDWInteractionGraph testGraph6;
    private static UDWInteractionGraph testGraph7;
    private static UDWInteractionGraph testEmptyGraph;
    private static UDWInteractionGraph testEmptyGraph1;
    private static UDWInteractionGraph testEmptyGraph2;
    private static UDWInteractionGraph testGraphMultipleLines;

    @BeforeAll
    public static void setupTests() {
        testGraphBase = new UDWInteractionGraph("resources/Task1-2UDWTransactions.txt");
        testGraph7 = new UDWInteractionGraph(testGraphBase, new int[] {100, 101});
        testGraphBase1 = new UDWInteractionGraph("resources/Task1-2Transactions.txt");
        testGraphMultipleLines = new UDWInteractionGraph("resources/multiple_spaces.txt");
        testGraph1 = new UDWInteractionGraph(testGraphBase, new int[] {0, 9});
        testGraph2 = new UDWInteractionGraph(testGraphBase, new int[] {10, 11});
        testGraph3 = new UDWInteractionGraph("resources/Task1-2Transactions.txt",
            new int[] {7, 7});
        testGraph4 = new UDWInteractionGraph("resources/Task1-2Transactions.txt",
            new int[] {0, 50000});
        testGraph5 = new UDWInteractionGraph("resources/Task1-2Transactions.txt",
            new int[] {10, 11});
        testGraph6 = new UDWInteractionGraph(testGraphBase1,
            new ArrayList<>(Arrays.asList(4, 8)));
        testEmptyGraph = new UDWInteractionGraph("resources/empty.txt");
        testEmptyGraph1 = new UDWInteractionGraph("resources/empty.txt", new int[] {3, 9});
        testEmptyGraph2 = new UDWInteractionGraph(testEmptyGraph,
            new ArrayList<>(Arrays.asList(1, 2)));
    }

    @Test
    public void test_DWTimeFilter() {
        DWInteractionGraph d = new DWInteractionGraph("resources/multiple_spaces.txt");
        DWInteractionGraph d1 = new DWInteractionGraph(d, new int[] {2, 3});
        UDWInteractionGraph t = new UDWInteractionGraph(d1);

        Assertions.assertEquals(new HashSet<>(Arrays.asList(0, 1, 2)), t.getUserIDs());
    }

    @Test
    public void test_timeWindowOutside() {
        Assertions.assertEquals(new HashSet<>(Arrays.asList()), testGraph7.getUserIDs());
    }

    @Test
    public void test_multipleSpaces() {
        Assertions.assertEquals(new HashSet<>(Arrays.asList(0, 1, 2, 3)),
            testGraphMultipleLines.getUserIDs());
    }

    @Test
    public void test_users() {
        Assertions.assertEquals(new HashSet<>(Arrays.asList(4, 8, 0, 1)), testGraph6.getUserIDs());
    }

    @Test
    public void test_noUsersInTimeWindow() {
        Assertions.assertEquals(new HashSet<>(List.of()), testGraph5.getUserIDs());
        Assertions.assertEquals(0, testGraph5.getEmailCount(0, 0));
    }

    @Test
    public void test_fileTime() {
        Assertions.assertEquals(new HashSet<>(Arrays.asList(4, 8)), testGraph3.getUserIDs());
        Assertions.assertEquals(1, testGraph4.getEmailCount(0, 0));
    }

    @Test
    public void test_fileTimeOutside() {
        Assertions.assertEquals(new HashSet<>(Arrays.asList(0, 1, 2, 3, 4, 8)),
            testGraph4.getUserIDs());
        Assertions.assertEquals(2, testGraph4.getEmailCount(2, 3));
    }

    @Test
    public void test_empty() {
        Assertions.assertEquals(new HashSet<>(List.of()), testEmptyGraph.getUserIDs());
    }

    @Test
    public void test_emptyTimeWindow() {
        Assertions.assertEquals(new HashSet<>(List.of()), testEmptyGraph1.getUserIDs());
    }

    @Test
    public void test_userNotExist() {
        Assertions.assertEquals(new HashSet<>(List.of()), testEmptyGraph2.getUserIDs());
        Assertions.assertEquals(0, testEmptyGraph2.getEmailCount(3, 5));
    }

    @Test
    public void testGetUserIds() {
        Assertions.assertEquals(new HashSet<>(Arrays.asList(0, 1, 2, 3)),
            testGraphBase.getUserIDs());
    }

    @Test
    public void testGetUserIds1() {
        Assertions.assertEquals(new HashSet<>(Arrays.asList(1, 2, 3)), testGraph2.getUserIDs());
    }

    @Test
    public void testGetEmailCount() {
        Assertions.assertEquals(2, testGraphBase.getEmailCount(1, 0));
        Assertions.assertEquals(2, testGraphBase.getEmailCount(0, 1));
    }

    @Test
    public void testGetEmailCount1() {
        Assertions.assertEquals(2, testGraph1.getEmailCount(1, 0));
        Assertions.assertEquals(2, testGraph1.getEmailCount(0, 3));
    }

    @Test
    public void testGetEmailCount2() {
        Assertions.assertEquals(0, testGraph2.getEmailCount(1, 0));
        Assertions.assertEquals(1, testGraph2.getEmailCount(1, 3));
    }

    @Test
    public void testUserConstructor() {
        List<Integer> userFilter = Arrays.asList(0, 1);
        UDWInteractionGraph t = new UDWInteractionGraph(testGraphBase, userFilter);
        //this doesn't make sense, if we want to include
        Assertions.assertEquals(new HashSet<>(Arrays.asList(0, 1, 2, 3)), t.getUserIDs());
        Assertions.assertEquals(2, t.getEmailCount(0, 1));
        Assertions.assertEquals(2, t.getEmailCount(0, 3));
    }

    @Test
    public void testConstructionFromDW() {
        DWInteractionGraph dwig = new DWInteractionGraph("resources/Task1-2UDWTransactions.txt");
        UDWInteractionGraph udwig = new UDWInteractionGraph(dwig);
        Assertions.assertEquals(new HashSet<>(Arrays.asList(0, 1, 2, 3)), udwig.getUserIDs());
        Assertions.assertEquals(2, udwig.getEmailCount(2, 3));
    }

    @Test
    public void testConstructionFromDW1() {
        DWInteractionGraph dwig = new DWInteractionGraph("resources/Task1-2Transactions.txt");
        UDWInteractionGraph udwig = new UDWInteractionGraph(dwig);
        Assertions.assertEquals(new HashSet<>(Arrays.asList(0, 1, 2, 3, 4, 8)), udwig.getUserIDs());
        Assertions.assertEquals(2, udwig.getEmailCount(2, 3));
    }

}
