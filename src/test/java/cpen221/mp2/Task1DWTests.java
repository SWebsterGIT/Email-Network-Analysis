package cpen221.mp2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Task1DWTests {

    private static DWInteractionGraph dwig;
    private static DWInteractionGraph dwig1;
    private static DWInteractionGraph dwig2;

    @BeforeAll
    public static void setupTests() {
        dwig = new DWInteractionGraph("resources/Task1-2Transactions.txt");
        dwig1 = new DWInteractionGraph(dwig, new int[]{3, 9});
        dwig2 = new DWInteractionGraph(dwig, Arrays.asList(2, 3, 4));
    }

    @Test
    public void test1GetUserIDsBase() {
        Set<Integer> expected = new HashSet<>(Arrays.asList(0, 1, 2, 3, 4, 8));
        Assertions.assertEquals(expected, dwig.getUserIDs());
    }

    @Test
    public void test1GetUserIDsGraph1() {
        Set<Integer> expected = new HashSet<>(Arrays.asList(0, 1, 4, 8));
        Assertions.assertEquals(expected, dwig1.getUserIDs());
    }

    @Test
    public void test1GetUserIDsGraph2() {
        Set<Integer> expected = new HashSet<>(Arrays.asList(2, 3, 4, 8));
        Assertions.assertEquals(expected, dwig2.getUserIDs());
    }

    @Test
    public void test1GetEmailCountBase() {
        Assertions.assertEquals(2, dwig.getEmailCount(2, 3));
        Assertions.assertEquals(0, dwig.getEmailCount(8, 4));
    }

    @Test
    public void test1GetEmailCountGraph1() {
        Assertions.assertEquals(1, dwig1.getEmailCount(1, 0));
        Assertions.assertEquals(1, dwig1.getEmailCount(8, 0));

    }

    @Test
    public void test1GetEmailCountGraph2() {
        Assertions.assertEquals(1, dwig2.getEmailCount(4, 8));
        Assertions.assertEquals(2, dwig2.getEmailCount(2, 3));
    }

    @Test
    public void includeAllTimes(){
        Assertions.assertEquals((new DWInteractionGraph(dwig, new int[]{0, 13})).getAdjacencyList(),
                dwig.getAdjacencyList());
    }

    @Test
    public void excludeAllTimes(){
        DWInteractionGraph test = new DWInteractionGraph(dwig, new int[]{14,15});

        Assertions.assertTrue(test.getAdjacencyList().isEmpty());
        Assertions.assertEquals(0, test.getRawData().length);
    }

    @Test
    public void filterAllWithInvalidUsers(){
        DWInteractionGraph test = new DWInteractionGraph(dwig, List.of(31, 15));

        Assertions.assertEquals(0, test.getRawData().length);
    }

    @Test
    public void filterNone(){
        DWInteractionGraph test = new DWInteractionGraph(dwig, List.of(0,1,2,3,4,8));

        Assertions.assertEquals(Arrays.deepToString(test.getRawData()), Arrays.deepToString(dwig.getRawData()));
    }

    @Test
    public void testLastConstructor(){
        DWInteractionGraph dwig4 = new DWInteractionGraph("resources/Task1-2Transactions.txt", new int[]{3, 9});

        Assertions.assertEquals(Arrays.deepToString(dwig4.getRawData()), Arrays.deepToString(dwig1.getRawData()));


    }



}
