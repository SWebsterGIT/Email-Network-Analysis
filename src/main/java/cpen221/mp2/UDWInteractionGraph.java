package cpen221.mp2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UDWInteractionGraph {

    /*
    * Representation Invariant:
    * - Each user in given data, after filter, is represented by some
    index in the adjacency matrix
    * - emailAdjMatrix is symmetrical, and is square
    * - numUsers == userToIndexMap.size() == emailAdjMatrix.length
    * == indexToUserMap.size()
    * - indices of the adjacency matrix are represented by
    * a user id in the map userToIndex Map
    * - The ith and jth column in the adjacency matrix represent the same user
    * - index (the index of a user in an adjacency matrix) ==
    * underToIndexMap.get(user)
    * - user (the user represented by an index) == indexToUserMap.get(index)
    * - total users in components == numUsers
    * - the Integers in emailAdjMatrix, emailData, userToIndexMap,
    * indexToUserMap, adjacencyList are all >= 0
    * - userToIndexMap does not contain filtered users [if specified]
    * - times does not contain times outside the time interval [if specified]
    */

    /*
    * Abstraction Function:
    * Represents email interactions as an undirected weighted graph where the
    * weights of the edges are given by the number of interactions between two
    * users which are represented as nodes.
    *
    * Graphs are represented with an adjacency matrix. Indices of the adj. matrix
    * are mapped users, removing the need to zero rows.
    * */

    /** location of time in row of data. */
    private static final int TIME = 2;


    /* ------- Task 1 ------- */
    /* Building the Constructors */
    /** number of users in a graph. */
    private final int numUsers;
    /** time window of a graph. */
    private final int[] timeWindow = new int[2];
    /** data of the emails where 0th, 1st, and 2nd are
     * sender, receiver, and time. */
    private final int[][] emailData;
    /** Adjacency matrix that represents the graph, rows represent senders,
     * columns receivers. Indices of senders and receivers represent users in
     * given by the indexToUserMap
     */
    private final int[][] emailAdjMatrix;
    /** Maps users to their respective indices in the adjacency matrix. */
    private final Map<Integer, Integer> userToIndexMap = new HashMap<>();
    /** Maps indices of the adjacency matrix to their respective users. */
    private final Map<Integer, Integer> indexToUserMap = new HashMap<>();
    /** Represents the components of an undirected graph represented as Sets. */
    private final List<Set<Integer>> components;
    /** The number of users in the graph. */
    private int userCounter = 0;

    /**
     * Creates a new UDWInteractionGraph using an email interaction file.
     * The email interaction file will be in the resources directory.
     *
     * @param fileName the name of the file in the resources
     *                 directory containing email interactions
     */
    public UDWInteractionGraph(String fileName) {
        int[][] emailData = Utility.fileToIntArray(fileName);
        //get weights based on number of email interactions
        int numUsers = Utility.userCount(emailData);
        int[][] emailAdjMatrix = getEmailAdjMatrix(emailData, numUsers);

        this.numUsers = numUsers;
        this.emailAdjMatrix = emailAdjMatrix;
        this.emailData = emailData;
        this.components = this.groupComponents();
    }

    /**
     * Creates a new UDWInteractionGraph from a file
     * and considering a time window filter.
     *
     * @param filename   the name of the file in the resources
     *                   directory containing email interactions
     * @param timeFilter an integer array of length 2: [t0, t1]
     *                   where t0 <= t1. The created UDWInteractionGraph
     *                   should only include those emails in the input
     *                   UDWInteractionGraph with send time t in the
     *                   t0 <= t <= t1 range.
     */
    public UDWInteractionGraph(String filename, int[] timeFilter) {
        this(new UDWInteractionGraph(filename), timeFilter);
    }


    /**
     * Creates a new UDWInteractionGraph from a UDWInteractionGraph object
     * and considering a time window filter.
     *
     * @param inputUDWIG a UDWInteractionGraph object
     * @param timeFilter an integer array of length 2: [t0, t1]
     *                   where t0 <= t1. The created UDWInteractionGraph
     *                   should only include those emails in the input
     *                   UDWInteractionGraph with send time t in the
     *                   t0 <= t <= t1 range.
     */
    public UDWInteractionGraph(UDWInteractionGraph inputUDWIG, int[] timeFilter) {

        int[] timeWindow = new int[2];
        int startIndex = 0;
        int endIndex = 0;
        int[][] inputData = inputUDWIG.getRawData();

        // if the file is empty no time filter
        if (inputData.length > 0
            && timeFilter[0] <= inputData[inputData.length - 1][TIME]) {
            // Find the start of the time window

            for (int i = 0; i < inputData.length; i++) {
                if (inputData[i][TIME] >= timeFilter[0]) {
                    timeWindow[0] = inputData[i][TIME];
                    startIndex = i;
                    break;
                }
            }

            // Check if the time filter is larger than the time window
            if (timeFilter[1] < inputData[inputData.length - 1][TIME]) {
                // Find the end of the time window
                for (int i = startIndex; i < inputData.length; i++) {
                    if (inputData[i][TIME] > timeFilter[1]) {
                        timeWindow[1] = inputData[i][TIME];
                        endIndex = i + 1;
                        break;
                    } else if (inputData[i][TIME] == timeFilter[1]) {
                        timeWindow[1] = inputData[i][TIME];
                        endIndex = i + 1;
                        break;
                    }
                }
            } else {
                endIndex = inputData.length;
                timeWindow[1] = inputData[inputData.length - 1][TIME];
            }
        }

        // checks that the time window is actually whithin the time filter
        // based on the largest index in the time window
        if (timeWindow[1] > timeFilter[1]) {
            endIndex--;
        }


        int[][] emailData = Arrays.copyOfRange(inputData, startIndex, endIndex);
        int numUsers = Utility.userCount(emailData);
        int[][] emailAdjMatrix = this.getEmailAdjMatrix(emailData, numUsers);

        this.timeWindow[0] = timeWindow[0];
        this.timeWindow[1] = timeWindow[1];
        this.numUsers = numUsers;
        this.emailAdjMatrix = emailAdjMatrix;
        this.emailData = emailData;
        this.components = this.groupComponents();
    }


    /**
     * Creates a new UDWInteractionGraph from a UDWInteractionGraph object
     * and considering a list of User IDs.
     *
     * @param inputUDWIG a UDWInteractionGraph object
     * @param userFilter a List of User IDs. The created UDWInteractionGraph
     *                   should exclude those emails in the input
     *                   UDWInteractionGraph for which neither the sender
     *                   nor the receiver exist in userFilter.
     */
    public UDWInteractionGraph(UDWInteractionGraph inputUDWIG, List<Integer> userFilter) {
        int[][] inputData = inputUDWIG.getRawData();
        List<int[]> newEmailData = new ArrayList<>();

        // Filters users contained in userFilter
        for (int[] inputDatum : inputData) {
            if (userFilter.contains(inputDatum[0])
                || userFilter.contains(inputDatum[1])) {
                newEmailData.add(inputDatum);
            }
        }

        int[][] emailData = new int[newEmailData.size()][3];

        // Create new data based on the emails
        for (int i = 0; i < newEmailData.size(); i++) {
            emailData[i] = newEmailData.get(i);
        }

        int numUsers = Utility.userCount(emailData);
        int[][] emailAdjMatrix = this.getEmailAdjMatrix(emailData, numUsers);

        this.numUsers = numUsers;
        this.emailData = emailData;
        this.emailAdjMatrix = emailAdjMatrix;
        this.components = this.groupComponents();
    }

    /**
     * Creates a new UDWInteractionGraph from a DWInteractionGraph object.
     *
     * @param inputDWIG a DWInteractionGraph object
     */
    public UDWInteractionGraph(DWInteractionGraph inputDWIG) {
        // Copies data and makes and its own respective adjacency matrix
        int[][] inputData = inputDWIG.getRawData();
        int numUsers = Utility.userCount(inputData);

        this.emailAdjMatrix = this.getEmailAdjMatrix(inputData, numUsers);
        this.emailData = inputData;
        this.numUsers = numUsers;
        this.components = this.groupComponents();
    }

    /**
     * @return a Set of Integers, where every element in the set is a User ID
     * in this UDWInteractionGraph.
     */
    public Set<Integer> getUserIDs() {
        return new HashSet<>(userToIndexMap.keySet());
    }

    /**
     * @param user1 the User ID of the first user.
     * @param user2 the User ID of the second user.
     * @return the number of email interactions (send/receive) between user1 and user2
     */
    public int getEmailCount(int user1, int user2) {
        if (!userToIndexMap.containsKey(user1) || !userToIndexMap.containsKey(user2)) {
            return 0;
        }
        return emailAdjMatrix[this.userToIndexMap.get(user1)][this.userToIndexMap.get(user2)];
    }

    /* ------- Task 2 ------- */

    /**
     * @param timeWindow is an int array of size 2 [t0, t1]
     *                   where t0<=t1
     * @return an int array of length 2, with the following structure:
     * [NumberOfUsers, NumberOfEmailTransactions]
     */
    public int[] ReportActivityInTimeWindow(int[] timeWindow) {

        // change the matrix to the filtered version using constructor
        UDWInteractionGraph filteredGraph = new UDWInteractionGraph(this, timeWindow);


        // for number of emails total, go through upper diagonal of matrix and sum weights
        int sum = 0;
        for (int i = 0; i < filteredGraph.emailAdjMatrix.length; i++) {
            for (int j = i; j < filteredGraph.emailAdjMatrix.length; j++) {
                sum += filteredGraph.emailAdjMatrix[i][j];
            }

        }
        int transCount = sum;

        // numUsers is already the number of users that send or receive an email.
        return new int[] {filteredGraph.numUsers, transCount};

    }

    /**
     * @param userID the User ID of the user for which
     *               the report will be created
     * @return an int array of length 2 with the following structure:
     * [NumberOfEmails, UniqueUsersInteractedWith]
     * If the specified User ID does not exist in this instance of a graph,
     * returns [0, 0].
     */
    public int[] ReportOnUser(int userID) {

        int userIndex;
        // start by finding the index of the user
        if (this.userToIndexMap.containsKey(userID)) {
            userIndex = this.userToIndexMap.get(userID);
        } else {
            // if not found, return [0,0]
            return new int[] {0, 0};
        }

        // go through the row or column of the array that shows emails (use found index)
        // count number of non-zero or filled entries by summing weights
        int sum = 0;
        int interCount = 0;
        for (int i = 0; i < this.emailAdjMatrix.length; i++) {
            sum += this.emailAdjMatrix[userIndex][i];
            if (this.emailAdjMatrix[userIndex][i] >= 1) {
                interCount++;
            }
        }

        return new int[] {sum, interCount};
    }

    /**
     * @param N a positive number representing rank. N=1 means the most active.
     * @return the User ID for the Nth most active user, if there is a tie, returns the user with
     * that had the earliest interaction of all the tied users. If N exceeds the number of users,
     * returns -1.
     */

    // requires N is not negative, and is not more than the number of users in the network.
    public int NthMostActiveUser(int N) {


        if (N > numUsers) {
            return -1;
        }

        // make a new int arraylist with indexes that represent each user index.
        int[] emailsPerUser = new int[numUsers];
        for (int i = 0; i < numUsers; i++) {
            emailsPerUser[i] = 0;
        }

        // Go through top diagonal of the matrix and sum interactions for each user.
        for (int i = 0; i < numUsers; i++) {
            for (int j = i; j < numUsers; j++) {
                emailsPerUser[i] += this.emailAdjMatrix[i][j];
                if (i != j) {
                    emailsPerUser[j] += this.emailAdjMatrix[i][j];
                }
            }
        }

        // Then, go through and remove (set to -1) the top (N-1) indexes
        for (int i = 0; i < N - 1; i++) {
            int maxVal = -1;
            int indexOfLargest = 0;
            for (int j = 0; j < numUsers; j++) {
                if (emailsPerUser[j] > maxVal) {
                    maxVal = emailsPerUser[j];
                    indexOfLargest = j;
                }
            }
            emailsPerUser[indexOfLargest] = Integer.MIN_VALUE;
        }

        // now, the highest value will be the index of the Nth most active user
        int maxVal = -1;
        int indexOfLargest = 0;
        for (int j = 0; j < numUsers; j++) {
            if (emailsPerUser[j] > maxVal) {
                maxVal = emailsPerUser[j];
                indexOfLargest = j;
            }
        }

        if (emailsPerUser[indexOfLargest] <= 0) {
            return -1;
        }

        return indexToUserMap.get(indexOfLargest);
    }

    /* ------- Task 3 ------- */

    /**
     * @return the number of completely disjoint graph
     * components in the UDWInteractionGraph object.
     */
    public int NumberOfComponents() {
        return components.size();
    }

    /**
     * Identifies the components of a graph and returns the set of unique components
     *
     * @return A list of the unique components, where each component is represented by a Set
     * No two sets contain duplicates.
     */
    private List<Set<Integer>> groupComponents() {
        List<Set<Integer>> components = new ArrayList<>();

        // Adds every connection that a user has as its own set in components
        // If a user self sends, the user is added twice
        for (int i = 0; i < this.numUsers; i++) {
            Set<Integer> currComponent = new HashSet<>();
            int currSender = indexToUserMap.get(i);
            for (int j = 0; j < this.numUsers; j++) {
                int currReceiver = indexToUserMap.get(j);
                // Determines if there was any interaction
                if (emailAdjMatrix[i][j] > 0) {
                    if (currReceiver == currSender) {
                        components.add(new HashSet<>(List.of(currReceiver)));
                    }
                    currComponent.add(currSender);
                    currComponent.add(currReceiver);
                }
            }
            components.add(currComponent);
        }

        // Combine's sets with common users to create proper components
        // Removes a set that is added to another set
        // That is, removes a component that is already part of another component
        for (int i = 0; i < components.size(); i++) {
            for (int j = 0; j < components.size(); j++) {
                Set<Integer> compComponent = components.get(j);
                // Ensures that the loop is not checking values
                // out of the array bounds
                if (i < components.size() &&
                    components.get(i).stream().anyMatch(compComponent::contains)) {
                    components.get(i).addAll(compComponent);
                    components.remove(compComponent);
                }
            }
        }

        return new ArrayList<>(components);
    }

    /**
     * @param userID1 the user ID for the first user
     * @param userID2 the user ID for the second user
     * @return whether a path exists between the two users
     */
    public boolean PathExists(int userID1, int userID2) {
        // If two exist in the same component,
        // there is a path that exists between them
        for (Set<Integer> component : components) {
            if (component.contains(userID1) && component.contains(userID2)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Produces an adjacency matrix for an Undirected graph.
     * The ith, jth element is the number of
     * interactions between the ith and jth user.
     *
     * @param inputData Email data, where each row is represented by sender, receiver, time,
     *                  not null
     * @param numUsers  the number of users in a this instance of and Undirected graph,
     *                  is non-negative
     * @return the adjacency matrix to represent the graph of sender and receiver of emails.
     * Each row represents a sender, and each column the receiver.
     * The element at the ith sender and
     * jth receiver is the number of interactions between the ith and jth users.
     */
    private int[][] getEmailAdjMatrix(int[][] inputData, int numUsers) {
        int sender;
        int receiver;

        int[][] emailAdjMatrix = new int[numUsers][numUsers];

        // Fill the adjacency matrix
        for (int[] inputDatum : inputData) {
            sender = inputDatum[0];
            receiver = inputDatum[1];

            // Ensures that users aren't counter more than once
            if (!this.userToIndexMap.containsKey(sender)) {
                this.userToIndexMap.put(sender, userCounter);
                this.indexToUserMap.put(userCounter, sender);
                this.userCounter++;
            }
            if (!this.userToIndexMap.containsKey(receiver)) {
                this.userToIndexMap.put(receiver, userCounter);
                this.indexToUserMap.put(userCounter, receiver);
                this.userCounter++;
            }

            // Accounts for self sends
            if (sender != receiver) {
                emailAdjMatrix[this.userToIndexMap.get(receiver)][this.userToIndexMap.get(
                    sender)]++;
            }
            emailAdjMatrix[this.userToIndexMap.get(sender)]
                [this.userToIndexMap.get(receiver)]++;
        }

        return emailAdjMatrix;
    }

    /**
     * Get the data of this instance of the graph.
     *
     * @return the data belonging to this instance of the undirected graph
     */
    public int[][] getRawData() {
        return this.emailData.clone();
    }
}
