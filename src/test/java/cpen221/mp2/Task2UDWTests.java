package cpen221.mp2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class Task2UDWTests {

    private static UDWInteractionGraph testGraphBase;
    private static UDWInteractionGraph additionalTesting;
    private static UDWInteractionGraph Edge;
    private static UDWInteractionGraph Edge1;
    private static DWInteractionGraph dwTester;

    @BeforeAll
    public static void setupTests() {
        testGraphBase = new UDWInteractionGraph("resources/Task1-2UDWTransactions.txt");
        additionalTesting = new UDWInteractionGraph("resources/Task1-2Transactions.txt");
        Edge = new UDWInteractionGraph("resources/edgeHelper.txt");
        Edge1 = new UDWInteractionGraph(Edge, new int[] {19, 100});
        dwTester = new DWInteractionGraph("resources/edgeHelper.txt");
    }

    @Test
    public void testReportActivityInTimeWindow() {
        int[] result = testGraphBase.ReportActivityInTimeWindow(new int[] {0, 1});
        Assertions.assertEquals(3, result[0]);
        Assertions.assertEquals(2, result[1]);
    }

    @Test
    public void testReportOnUser() {
        int[] result = testGraphBase.ReportOnUser(0);
        Assertions.assertEquals(6, result[0]);
        Assertions.assertEquals(3, result[1]);
    }


    @Test
    public void testReportOnUser1() {
        List<Integer> userFilter = Arrays.asList(0, 1);
        UDWInteractionGraph t = new UDWInteractionGraph(testGraphBase, userFilter);
        int[] result = t.ReportOnUser(0);
        Assertions.assertEquals(6, result[0]);
        Assertions.assertEquals(3, result[1]);
    }

    @Test
    public void testReportOnUser2() {
        int[] result = testGraphBase.ReportOnUser(4);
        Assertions.assertEquals(0, result[0]);
        Assertions.assertEquals(0, result[1]);
    }

    @Test
    public void testNthActiveUser() {
        UDWInteractionGraph t = new UDWInteractionGraph(testGraphBase, new int[] {0, 2});
        Assertions.assertEquals(0, t.NthMostActiveUser(1));
    }

    @Test
    public void testNthActiveUser1() {
        UDWInteractionGraph t = new UDWInteractionGraph(testGraphBase, new int[] {0, 2});
        Assertions.assertEquals(1, t.NthMostActiveUser(2));
    }

    @Test
    public void testNthActiveUserEdge1() {
        UDWInteractionGraph t = new UDWInteractionGraph(additionalTesting, new int[] {0, 0});
        Assertions.assertEquals(1, t.NthMostActiveUser(2));
    }

    @Test
    public void testNthActiveUserEdge2() {
        Assertions.assertEquals(1, additionalTesting.NthMostActiveUser(2));
    }

    @Test
    public void testReportOnUserNotFound() {
        int[] result = additionalTesting.ReportOnUser(5);
        Assertions.assertEquals(0, result[0]);
        Assertions.assertEquals(0, result[1]);
    }

    @Test
    public void testReportOnUserWithSelfEmail() {
        int[] result = additionalTesting.ReportOnUser(0);
        Assertions.assertEquals(4, result[0]);
        Assertions.assertEquals(3, result[1]);
    }


    @Test
    public void testReportActivityInTimeWindowEdge() {
        int[] result = additionalTesting.ReportActivityInTimeWindow(new int[] {0, 0});
        Assertions.assertEquals(2, result[0]);
        Assertions.assertEquals(1, result[1]);
    }

    @Test
    public void testReportActivityInTimeWindowEdgeNoUsers() {
        int[] result = additionalTesting.ReportActivityInTimeWindow(new int[] {10, 11});
        Assertions.assertEquals(0, result[0]);
        Assertions.assertEquals(0, result[1]);
    }

    @Test
    public void testNthActiveUserWithSelfEmailsAndTieBreak() {
        Assertions.assertEquals(0, Edge.NthMostActiveUser(1));
        Assertions.assertEquals(3, Edge.NthMostActiveUser(2));
    }

    @Test
    public void testNthActiveUserWithFourthConstructor() {

        DWInteractionGraph temp = new DWInteractionGraph("resources/edgeHelper.txt");
        UDWInteractionGraph Edge1 = new UDWInteractionGraph(temp);
        Assertions.assertEquals(0, Edge1.NthMostActiveUser(1));
        Assertions.assertEquals(3, Edge1.NthMostActiveUser(2));
    }

    @Test
    public void testNthActiveUserWithOneEmail() {
        Assertions.assertEquals(1, Edge1.NthMostActiveUser(1));
        Assertions.assertEquals(2, Edge1.NthMostActiveUser(2));
    }

    @Test
    public void testNthActiveUserWithNoEmails() {
        UDWInteractionGraph Edge2 = new UDWInteractionGraph(Edge1, new int[] {1, 4});
        Assertions.assertEquals(-1, Edge2.NthMostActiveUser(1));
        Assertions.assertEquals(-1, Edge2.NthMostActiveUser(2));
    }

    @Test
    public void testNthActiveUserWithNoEmailsFromDW() {

        DWInteractionGraph step1 = new DWInteractionGraph(dwTester, new int[] {21, 100});
        UDWInteractionGraph Edge2 = new UDWInteractionGraph(step1);

        Assertions.assertEquals(-1, Edge2.NthMostActiveUser(1));
        Assertions.assertEquals(-1, Edge2.NthMostActiveUser(2));
    }






    /*DWInteractionGraph temp = new DWInteractionGraph("resources/edgeHelper.txt");
        DWInteractionGraph step1 = new DWInteractionGraph(temp, new int[]{21,100});
        UDWInteractionGraph Edge1 = new UDWInteractionGraph(step1);*/


}
