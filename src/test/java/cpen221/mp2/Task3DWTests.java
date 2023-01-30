package cpen221.mp2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class Task3DWTests {

    private static DWInteractionGraph dwig1;
    private static DWInteractionGraph dwig2;
    private static DWInteractionGraph dwig3;
    private static DWInteractionGraph dwig4;
    private static DWInteractionGraph dwig5;
    private static DWInteractionGraph dwig6;

    @BeforeAll
    public static void setupTests() {
        dwig1 = new DWInteractionGraph("resources/Task3Transactions1.txt");
        dwig2 = new DWInteractionGraph("resources/Task3Transactions2.txt");
        dwig3 = new DWInteractionGraph("resources/Task3Transactions3.txt");
        dwig4 = new DWInteractionGraph("resources/Task3Transactions4.txt");
        dwig5 = new DWInteractionGraph("resources/Task3Transactions5.txt");
        dwig6 = new DWInteractionGraph("resources/email-Eu-core-temporal.txt");
    }

    @Test
    public void testBFSGraph1() {
        List<Integer> expected = Arrays.asList(1, 2, 3, 4, 6);
        Assertions.assertEquals(expected, dwig1.BFS(1, 6));
    }

    @Test
    public void testDFSGraph1() {
        List<Integer> expected = Arrays.asList(1, 2, 3, 4, 6);
        Assertions.assertEquals(expected, dwig1.DFS(1, 6));
    }

    @Test
    public void testBFSGraph2() {
        List<Integer> expected = Arrays.asList(1, 3, 5, 6, 4, 8, 7, 2, 9, 10);
        Assertions.assertEquals(expected, dwig2.BFS(1, 10));
    }

    @Test
    public void testDFSGraph2() {
        List<Integer> expected = Arrays.asList(1, 3, 4, 8, 5, 7, 2, 9, 10);
        Assertions.assertEquals(expected, dwig2.DFS(1, 10));
    }

    @Test
    public void noRelationTest() {
        Assertions.assertNull(dwig3.BFS(1, 10));
        Assertions.assertNull(dwig3.DFS(1, 10));
    }

    @Test
    public void invalidIDTest() {
        Assertions.assertNull(dwig2.BFS(-1, 11));
        Assertions.assertNull(dwig2.DFS(-1, 11));
    }

    @Test
    public void nonLinearIDBFS() {
        List<Integer> expected = Arrays.asList(11, 36, 121, 321, 100, 8088, 1738, 21, 8099, 9000);
        Assertions.assertEquals(expected, dwig5.BFS(11, 9000));
    }

    @Test
    public void nonLinearIDDFS() {
        List<Integer> expected = Arrays.asList(11, 36, 100, 8088, 121, 1738, 21, 8099, 9000);
        Assertions.assertEquals(expected, dwig5.DFS(11, 9000));
    }

    @Test
    public void searchSelf() {
        Assertions.assertEquals(List.of(0, 0), dwig4.DFS(0, 0));
        Assertions.assertEquals(List.of(0, 0), dwig4.BFS(0, 0));
    }

    @Test
    public void largeFile() {
        dwig6.DFS(1, 545);
        dwig6.BFS(1, 545);
    }


}
