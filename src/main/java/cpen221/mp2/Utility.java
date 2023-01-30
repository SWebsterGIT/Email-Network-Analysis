package cpen221.mp2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Utility {

    private static final int DATA_LENGTH = 3;
    private static final int SEND_RECEIVE = 2;

    /**
     * Create a 2D where the number of rows is the number of email interactions
     * in a data set represented in the filename.txt file.
     *
     * @param filename a .txt file, where sender, receiver, and time are the
     *                 0th, 1st, and 2nd integers respectively
     * @return a 2D array representing the data stored in the input file.
     * in each row the sender, receiver, and time are the
     * 0th, 1st, and 2nd columns respectively
     */
    public static int[][] fileToIntArray(final String filename) {
        List<int[]> emails = new ArrayList<>();
        try {
            BufferedReader reader =
                new BufferedReader(new FileReader(filename));

            // Read the input file line by line
            for (String fileLine = reader.readLine(); fileLine != null;
                 fileLine = reader.readLine()) {
                String[] strEmailInfo = fileLine.split("\\s+");

                // in the case that data starts with a space
                if (strEmailInfo[0].isEmpty()) {
                    strEmailInfo = Arrays.copyOfRange(strEmailInfo,
                        1, strEmailInfo.length);
                }
                int[] emailInfo = new int[DATA_LENGTH];

                // Assign integers to columns
                for (int i = 0; i < strEmailInfo.length; i++) {
                    emailInfo[i] = Integer.parseInt(strEmailInfo[i]);
                }
                emails.add(emailInfo);
            }
            reader.close();
        } catch (IOException ioe) {
            System.out.println("Problem reading file.");
        }


        int[][] emailInfo = new int[emails.size()][DATA_LENGTH];

        // Copy data in array
        for (int i = 0; i < emails.size(); i++) {
            emailInfo[i] = emails.get(i);
        }
        return emailInfo;
    }

    /**
     * Counts the number of users in an email data 2D array.
     *
     * @param inputData a 2D array where senders and users are represented
     *                  in the 0th and 1st columns.
     * @return the total number of unique users in {@param inputData}
     */
    public static int userCount(final int[][] inputData) {
        Set<Integer> userList = new HashSet<>();

        for (int i = 0; i < inputData.length; i++) {
            for (int j = 0; j < SEND_RECEIVE; j++) {
                userList.add(inputData[i][j]);
            }
        }

        return userList.size();

    }
}
