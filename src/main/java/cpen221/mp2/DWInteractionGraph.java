package cpen221.mp2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class DWInteractionGraph {

    /*
    DWInteractionGraph:

    Abstraction Function: DWInteractionGraph is a directed, weighted graph which represents all of the different emails
    sent between different users in a data set provided to the constructor. The graph is represented by an
    "adjacency matrix" which is an n x n square matrix, where n is the total number of users who have either sent or
    received an email within a specified time span [if specified], minus a specified list of users to exclude from the
    graph [if specified]. The indexes of the rows and columns of the adjacencyMatrix are translated to userIDs by means
    of either the userToIndexMap or the indexToUserMap. In the userToIndexMap, each key is a userID, each key's
    respective value is the index in the adjacency matrix where that user's data is stored. In the indexToUserMap, each
    key is an index in the adjacency matrix, and each key's respective value is the userID for which that index
    represents. Each entry of the adjacencyMatrix corresponds to the number of times that an email was sent between the
    user [based on indexToUserMap] and the recipient [based on indexToUserMap]. The graph is also represented by the
    adjacencyList, which represents an unweighted version of the graph. The adjacencyList is a list of lists of
    integers, for which each list at index corresponding to a userID [per indexToUserMap], contains all of the userIDs
    of users that the user has sent emails to within the given time span. adjacencyList is built off of the
    adjacencyMatrix, and therefore will exclude all filtered users.

    Lastly, data is stored in two other ways. The original rawData matrix, which is an n x 3 array, stores all of the
    original email interactions, where the first column are the userIDs of the senders, the second column is the
    userIDs of the recipients, and the third column is the time at which the email was sent. Thus, a single email is
    always represented as [sending userID, receiving userID, time of email]. The rawData matrix is stored in the data
    type in the event that it is needed in the future for other methods. The times matrix contains all of the times that
    all the emails were sent in a matrix of length n, where n is the total number of emails sent.

    Representation Invariant(s):

    the Integers in adjacencyMatrix, rawData, times, userToIndexMap, indexToUserMap, adjacencyList are all >= 0

    adjacencyMatrix is a square matrix

    userToIndexMap.size() == indexToUserMap.size() == adjacencyMatrix.length == adjacencyList.size()

    userToIndexMap does not contain filtered users [if specified]

    times does not contain times outside the time interval [if specified]

    userToIndexMap.size() == totalNumber of users in raw data trimmed to time interval, minus the users within the data
    set that are also specified in the list of users to filter out.

    times.length == rawData.length

     */

    /* ------- Task 1 ------- */
    /* Building the Constructors */


    private final int[][] adjacencyMatrix;
    private final int[] times;
    private final int[][] rawData;
    private final Map<Integer, Integer> userToIndexMap = new HashMap<>();
    private final Map<Integer, Integer> indexToUserMap = new HashMap<>();

    private final List<List<Integer>> adjacencyList = new ArrayList<>();

    static final int TIME = 2;
    static final int ATTRIBUTES = 3;
    static final int USERS = 2;
    static final int HOURSTOSECONDS = 3600;
    static final int TAG = -1;

    /**
     * Creates a new DWInteractionGraph using an email interaction file.
     * The email interaction file will be in the resources directory.
     *
     * @param fileName the name of the file in the resources
     *                 directory containing email interactions
     */
    public DWInteractionGraph(String fileName) {

        //Imports the data from the email transactions file to an array representing the data.
        int[][] baseArray = Utility.fileToIntArray(fileName);

        //Helper methods build all of the various fields in the data type, and assign their return values to their
        //respective fields.
        this.adjacencyMatrix = createAdjacencyMatrix(baseArray);
        this.times = createTimeArray(baseArray);
        this.rawData = baseArray;

        //Create an adjacency list.
        createAdjacencyList();

    }

    /**
     * Creates a new DWInteractionGraph from a DWInteractionGraph object
     * and considering a time window filter.
     *
     * @param inputDWIG  a DWInteractionGraph object
     * @param timeFilter an integer array of length 2: [t0, t1]
     *                   where t0 <= t1. The created DWInteractionGraph
     *                   should only include those emails in the input
     *                   DWInteractionGraph with send time t in the
     *                   t0 <= t <= t1 range.
     */
    public DWInteractionGraph(DWInteractionGraph inputDWIG, int[] timeFilter) {

        //Import raw data from the prior instance of DWInteractionGraph.
        int[][] inputRawData = inputDWIG.getRawData();

        //Initializing values
        int startTime = timeFilter[0];
        int endTime = timeFilter[1];
        int currentTime;
        int startIndex = inputRawData.length - 1;
        int endIndex = inputRawData.length - 1;

        //Finds the minimum index for which the email time is greater than or equal to the beginning of the timeFilter
        //interval.
        for (int i = 0; i < inputRawData.length; i++) {
            currentTime = inputRawData[i][TIME];
            if (currentTime >= startTime) {
                startIndex = i;
                break;
            }
        }

        //Finds the minimum index for which the email time is greater than or equal to the end of the timeFilter
        //interval.
        for (int i = startIndex; i < inputRawData.length; i++) {
            currentTime = inputRawData[i][TIME];
            if (currentTime > endTime) {
                endIndex = i - 1;
                break;
            }
        }

        //Finds the length of the new rawData matrix/initializing values.
        int newLength = endIndex - startIndex  + 1;

        List<Integer> timeList = new ArrayList<>();

        for(int i = 0; i < inputRawData.length; i++){
            currentTime = inputRawData[i][TIME];
            timeList.add(currentTime);
        }

        if(Collections.max(timeList) < startTime || Collections.min(timeList) > endTime){
            newLength = 0;
        }

        int i2;

        int[][] newRawData = new int[newLength][ATTRIBUTES];

        //Places values in the new rawData matrix.
        for (int i = 0; i < newLength; i++) {
            for (int j = 0; j < ATTRIBUTES; j++) {
                i2 = i + startIndex;
                newRawData[i][j] = inputRawData[i2][j];
            }
        }

        //Helper methods build all of the various fields in the data type, and assign their return values to their
        //respective fields, based on the new raw data with time filtering applied.
        this.adjacencyMatrix = createAdjacencyMatrix(newRawData);
        this.times = createTimeArray(newRawData);
        this.rawData = newRawData;

        //Creates a new adjacency list based on the new data.
        createAdjacencyList();

    }

    /**
     * Creates a new DWInteractionGraph from a DWInteractionGraph object
     * and considering a list of User IDs.
     *
     * @param inputDWIG  a DWInteractionGraph object
     * @param userFilter a List of User IDs. The created DWInteractionGraph
     *                   should exclude those emails in the input
     *                   DWInteractionGraph for which neither the sender
     *                   nor the receiver exist in userFilter.
     */
    public DWInteractionGraph(DWInteractionGraph inputDWIG, List<Integer> userFilter) {

        //Obtain raw data matrix, initialize values.

        int[][] inputRawData = inputDWIG.getRawData();
        int currentSender;
        int currentReceiver;

        List<Integer> validList = new ArrayList<>();

        //Adds all indexes of interactions that do not contain users in userFilter.

        for (int i = 0; i < inputRawData.length; i++) {
            currentSender = inputRawData[i][0];
            currentReceiver = inputRawData[i][1];

            if ((userFilter.contains(currentSender) || (userFilter.contains(currentReceiver)))) {
                validList.add(i);
            }
        }

        //initializes a new rawData matrix, and adds the interactions without the filtered users to the matrix.

        int[][] newRawData = new int[validList.size()][ATTRIBUTES];

        for (int i = 0; i < validList.size(); i++) {
            for (int j = 0; j < ATTRIBUTES; j++) {
                newRawData[i][j] = inputRawData[validList.get(i)][j];
            }
        }

        //Rep invariant new user count = old user count - filter.length


        //Helper methods build all of the various fields in the data type, and assign their return values to their
        //respective fields, based on the new raw data with time filtering applied.
        this.rawData = newRawData;
        this.adjacencyMatrix = createAdjacencyMatrix(newRawData);
        this.times = createTimeArray(newRawData);

        //Builds an adjacency list for the new instance based on the new adjacencyMatrix.
        createAdjacencyList();

    }


    /**
     * Creates a new DWInteractionGraph from a email data file
     * and considering a time window filter.
     *
     * @param fileName the name of the file in the resources
     *                 directory containing email interactions
     * @param timeFilter an integer array of length 2: [t0, t1]
     *                   where t0 <= t1. The created DWInteractionGraph
     *                   should only include those emails in the input
     *                   DWInteractionGraph with send time t in the
     *                   t0 <= t <= t1 range.
     */
    public DWInteractionGraph(String fileName, int[] timeFilter) {
        this(new DWInteractionGraph(fileName), timeFilter);
    }


    /**
     * @return a Set of Integers, where every element in the set is a User ID
     * in this DWInteractionGraph.
     */
    public Set<Integer> getUserIDs() {

        return new HashSet<>(userToIndexMap.keySet());
    }

    /**
     * @param sender   the User ID of the sender in the email transaction.
     * @param receiver the User ID of the receiver in the email transaction.
     * @return the number of emails sent from the specified sender to the specified
     * receiver in this DWInteractionGraph.
     */
    public int getEmailCount(int sender, int receiver) {

        return this.adjacencyMatrix[userToIndexMap.get(sender)][userToIndexMap.get(receiver)];

    }

    /* ------- Task 2 ------- */

    /**
     * Given an int array, [t0, t1], reports email transaction details.
     * Suppose an email in this graph is sent at time t, then all emails
     * sent where t0 <= t <= t1 are included in this report.
     *
     * @param timeWindow is an int array of size 2 [t0, t1] where t0<=t1.
     * @return an int array of length 3, with the following structure:
     * [NumberOfSenders, NumberOfReceivers, NumberOfEmailTransactions]
     *
     */
    public int[] ReportActivityInTimeWindow(int[] timeWindow) {

        // change the matrix to the filtered version using constructor
        DWInteractionGraph filteredGraph = new DWInteractionGraph(this, timeWindow);

        // used to track uniqueness of senders or receivers.
        Set<Integer> recSet = new HashSet<>();
        Set<Integer> sendSet = new HashSet<>();

        int sendCount = 0;
        int recCount = 0;
        int transCount = 0;

        for (int i = 0; i < filteredGraph.adjacencyMatrix.length; i++) {
            for (int j = 0; j < filteredGraph.adjacencyMatrix[i].length; j++) {
                if (filteredGraph.adjacencyMatrix[i][j] >= 1) {
                    if (!recSet.contains(j)) {
                        recSet.add(j);
                        recCount++;
                    }
                    if (!sendSet.contains(i)) {
                        sendSet.add(i);
                        sendCount++;
                    }
                    transCount += filteredGraph.adjacencyMatrix[i][j];
                }
            }
        }

        return new int[] {sendCount, recCount, transCount};
    }

    /**
     * Given a User ID, reports the specified User's email transaction history.
     *
     * @param userID the User ID of the user for which the report will be
     *               created.
     * @return an int array of length 3 with the following structure:
     * [NumberOfEmailsSent, NumberOfEmailsReceived, UniqueUsersInteractedWith]
     * If the specified User ID does not exist in this instance of a graph,
     * returns [0, 0, 0].
     */
    public int[] ReportOnUser(int userID) {

        // start by finding the index of the user
        // if not found, return [0,0,0]
        int userIndex;
        if (this.userToIndexMap.containsKey(userID)) {
            userIndex = this.userToIndexMap.get(userID);
        } else {

            return new int[] {0, 0, 0};
        }

        // go through the row to count sent emails (use found index)
        // count number of non-zero or filled entries by summing weights
        int sendCount = 0;
        int recCount = 0;
        int interCount = 0;
        Set<Integer> usersDone = new HashSet<>();

        for (int i = 0; i < this.adjacencyMatrix[userIndex].length; i++) {
            sendCount += this.adjacencyMatrix[userIndex][i];
            if (this.adjacencyMatrix[userIndex][i] >= 1 && !usersDone.contains(i)) {
                interCount++;
                usersDone.add(i);
            }
        }

        // same for received emails. The set of interacted users is conserved.
        for (int i = 0; i < this.adjacencyMatrix.length; i++) {
            recCount += this.adjacencyMatrix[i][userIndex];
            if (this.adjacencyMatrix[i][userIndex] >= 1 && !usersDone.contains(i)) {
                interCount++;
                usersDone.add(i);
            }
        }

        return new int[] {sendCount, recCount, interCount};

    }

    /**
     * @param N               a positive number representing rank. N=1 means the most active.
     * @param interactionType Represent the type of interaction to calculate the rank for
     *                        Can be SendOrReceive.Send or SendOrReceive.RECEIVE
     * @return the User ID for the Nth most active user in specified interaction type.
     * Sorts User IDs by their number of sent or received emails first. In the case of a
     * tie, secondarily sorts the tied User IDs in ascending order. If no suitable user is found,
     * or if N exceeds the number of users in the dataset, returns -1 (as per CW and Notion).
     * Users with zero interactions of the specified type will not be considered
     * suitable users, as per CW.
     */
    public int NthMostActiveUser(int N, SendOrReceive interactionType) {

        if(adjacencyMatrix.length == 0){
            return -1;
        }

        int[] emailsPerUserIndex = new int[this.adjacencyMatrix.length];

        for (int i = 0; i < this.adjacencyMatrix.length; i++) {
            emailsPerUserIndex[i] = 0;
        }

        // case 1: receive.
        // go through each receiver column, sum weights in the loop.

        if (interactionType == SendOrReceive.RECEIVE) {

            for (int i = 0; i < this.adjacencyMatrix.length; i++) {

                for (int j = 0; j < this.adjacencyMatrix[i].length; j++) {
                    emailsPerUserIndex[j] += this.adjacencyMatrix[i][j];
                }
            }

        } else if (interactionType == SendOrReceive.SEND) {

            // go through each sender row, sum weights in the loop.
            for (int i = 0; i < this.adjacencyMatrix.length; i++) {

                for (int j = 0; j < this.adjacencyMatrix[i].length; j++) {
                    emailsPerUserIndex[i] += this.adjacencyMatrix[i][j];
                }
            }
        }


        // Then, go through and "remove" (set to MIN_VALUE) the top (N-1) indexes
        for (int i = 0; i < N - 1; i++) {
            int maxVal = -1;
            int indexOfLargest = 0;
            for (int j = 0; j < this.adjacencyMatrix.length; j++) {
                if (emailsPerUserIndex[j] > maxVal) {
                    maxVal = emailsPerUserIndex[j];
                    indexOfLargest = j;
                }
            }
            emailsPerUserIndex[indexOfLargest] = Integer.MIN_VALUE;
        }

        // now, since we removed the N-1 most active users,
        // the highest value will be at the index of the Nth most active user
        int maxVal = -1;
        int indexOfLargest = 0;
        for (int j = 0; j < this.adjacencyMatrix.length; j++) {
            if (emailsPerUserIndex[j] > maxVal) {
                maxVal = emailsPerUserIndex[j];
                indexOfLargest = j;
            }
        }

        // if the Nth most active user is involved in 0 interactions, we return -1, as per
        // CampusWire clarification
        if (emailsPerUserIndex[indexOfLargest] <= 0) {
            return -1;
        }

        return indexToUserMap.get(indexOfLargest);

    }

    /* ------- Task 3 ------- */

    /**
     * performs breadth first search on the DWInteractionGraph object
     * to check path between user with userID1 and user with userID2.
     *
     * @param userID1 the user ID for the first user
     * @param userID2 the user ID for the second user
     * @return if a path exists, returns aa list of user IDs
     * in the order encountered in the search.
     * if no path exists, should return null.
     */
    public List<Integer> BFS(int userID1, int userID2) {


        //Uses checkRelation to see if a linkage exists between userID1 and userID2 exists, if so, uses bSearch to
        //produce the path list, if no linkage exists, returns null.
        if (!this.userToIndexMap.containsKey(userID1) ||
            !(this.userToIndexMap.containsKey(userID2)) ||
            !checkRelation(userID1, userID2)) {
            return null;
        } else {
            List<Integer> initialList = new ArrayList<>(List.of(userID1));
            List<List<Integer>> initialDoubleList =
                new ArrayList<>(List.of(this.adjacencyList.get(userToIndexMap.get(userID1))));

            return bSearch(userID2, initialList, initialDoubleList);
        }
    }


    /**
     * Helper method for BFS. Performs a breadth-first search on the current instance of DWInteractionGraph, returning
     * the path in which the search travelled to reach the destination value.
     *
     * @param endUser        userID which the breadth-first search is searching for. endUser must be reachable
     *                       in the DWAdjacencyGraph from the starting userID.
     * @param searchPath     current path which the function has searched the data from. When using this function,
     *                       searchPath should be a list with only the starting userID that the search should start
     *                       from.
     * @param totalRelations list of lists representing the layers of interactions of the current instance of
     *                       DWInteractionGraph. When using this function, totalRelations should be passed with
     *                       the starting UserID's corresponding recipient list in adjacencyList. The "layers of
     *                       interactions" are characterized by all of the recipients of the starting user, then
     *                       all of the recipients of all of the recipients of the the first user, and so on.
     * @return The path in which the breadth-first search traversed the DWInteractionGraph in order
     * to find endUser.
     */
    private List<Integer> bSearch(int endUser, List<Integer> searchPath,
                                  List<List<Integer>> totalRelations) {

        //Initializes currentUserRelations, which contains the last list in totalRelations.
        List<Integer> currentUserRelations =
            new ArrayList<>(totalRelations.get(totalRelations.size() - 1));

        List<Integer> nextUsers = new ArrayList<>();

        //Adds every recipient of each user in currentUserRelations if not already added previously to the list of the
        //next users.
        for (Integer nextUser : currentUserRelations) {

            //Checks if the any of the users in currentUserRelations are the endUser, breaks the method if so and
            //returns the current search path plus the endUser ID.
            if (nextUser == endUser) {
                searchPath.add(nextUser);
                return searchPath;
            }

            //Adds the all of the recipients of nextUser to the nextUsers list.
            if (!searchPath.contains(nextUser)) {
                searchPath.add(nextUser);
                nextUsers.addAll(this.adjacencyList.get(userToIndexMap.get(nextUser)));

            }
        }

        //Adds the list of next users to the totalRelations list.

        totalRelations.add(nextUsers);

        //Continues adding layers of users to the totalRelations list, and updates which users have been searched.
        return bSearch(endUser, searchPath, totalRelations);

    }

    /**
     * performs depth first search on the DWInteractionGraph object
     * to check path between user with userID1 and user with userID2.
     *
     * @param userID1 the user ID for the first user
     * @param userID2 the user ID for the second user
     * @return if a path exists, returns aa list of user IDs
     * in the order encountered in the search.
     * if no path exists, should return null.
     */
    public List<Integer> DFS(int userID1, int userID2) {

        //Uses checkRelation to see if a linkage exists between userID1 and userID2 exists, if so, uses dSearch to
        //produce the path list, if no linkage exists, returns null.


        if (!this.userToIndexMap.containsKey(userID1) ||
            !(this.userToIndexMap.containsKey(userID2)) ||
            !checkRelation(userID1, userID2)) {
            return null;
        } else {
            List<Integer> startUserList = new ArrayList<>(List.of(userID1));
            List<Integer> emptyList = new ArrayList<>();

            return dSearch(userID1, userID2, startUserList, userID1, emptyList);
        }


    }

    /**
     * Helper method for DFS. Performs a depth-first search on the current instance of DWInteractionGraph, producing
     * the path of users which the search traversed to find the endUser.
     *
     * @param startUser      The user from which to start the search from.
     * @param endUser        The user to search for in the depth-first search. startUser and endUser must be
     *                       connected through an email chain.
     * @param searchList     The current path of userIDs that the function has already searched through. When calling
     *                       this method from an external context, searchList should be passed with only the
     *                       startUser in the list.
     * @param currentSearch  The current userID from which to search the DWInteractionGraph from. When calling this
     *                       method from an external context, currentSearch should be the same as startUser.
     * @param failedSearches The list of userIDs for which a depth-first search could not find endUser as a
     *                       recipient. When calling this method from an external context, failedSearches should
     *                       be an empty list.
     * @return The sequential path of userIDs that the method traversed in order to reach endUser in
     * the DWInteractionGraph, regardless if those searches were successful or not.
     */
    private List<Integer> dSearch(int startUser, int endUser, List<Integer> searchList,
                                  int currentSearch, List<Integer> failedSearches) {

        //Obtains the index of the current user in the adjacency matrix/list.

        int currentUserIndex = userToIndexMap.get(currentSearch);

        //Iterates through the entries of the currentUserIndex row of the adjacencyMatrix. For non-zero entries in the
        //row, Breaks the method and returns searchList if the index corresponds to the userID of the endUser. Adds
        //the userID corresponding to the index and calls new search if the userID corresponding to the index is not
        //already within the searchList.

        for (int i = 0; i < this.adjacencyMatrix.length; i++) {

            if (this.adjacencyMatrix[currentUserIndex][i] > 0) {

                int nextUser = indexToUserMap.get(i);

                if (nextUser == endUser) {
                    searchList.add(nextUser);
                    return searchList;
                }

                if (!searchList.contains(nextUser)) {
                    searchList.add(nextUser);

                    return dSearch(startUser, endUser, searchList, nextUser, failedSearches);
                }
            }
        }

        //The code below finds the last userID whose search did not result in an addition to failedSearches, and
        //searches again based on that userID.

        failedSearches.add(currentSearch);

        List<Integer> tempList = new ArrayList<>(searchList);

        tempList.removeAll(failedSearches);

        int nextSearch;

        if (!tempList.isEmpty()) {
            nextSearch = tempList.get(tempList.size() - 1);
        } else {
            nextSearch = startUser;
        }

        return dSearch(startUser, endUser, searchList, nextSearch, failedSearches);

    }

    /**
     * Checks to see if there is a path of emails that links one user to another in the current instance of
     * DWInteractionGraph.
     *
     * @param startUser The starting userID from which to start the search from. Must be a valid userID within the
     *                  scope of the current instance of DWInteractionGraph [must be a key in userToIndexMap].
     * @param endUser   The userID to see if startUser is connected to. Must be a valid userID within the scope of
     *                  current instance of DWInteractionGraph [must be a key in userToIndexMap].
     * @return true, if a path of emails indeed exists between startUser and endUser, or false if
     * such a path does not exist.
     */
    public boolean checkRelation(int startUser, int endUser) {

        //Uses the result of tagSearch based on startUser and endUser to observe if there is a relationship
        //between startUser and endUser.

        Set<Integer> emptySet = new TreeSet<>();

        List<Integer> resultList =
            new ArrayList<>(tagSearch(startUser, endUser, startUser, emptySet, true));

        if (resultList.contains(TAG)) {
            return false;
        } else {
            return true;
        }

    }

    /**
     * Helper method for checkRelation. Performs a depth-first search to check if there is a path of emails that
     * links one user to another in the current instance of DWInteractionGraph. Returns a list with the path if a
     * relationship exists, returns a list "tagged" with the value (-1) if no relationship exists.
     *
     * @param startUser       The userID from which to start the search from. Must be a valid userID within the
     *                        scope of the current instance of DWInteractionGraph [must be a key in
     *                        userToIndexMap].
     * @param endUser         The userID to search for in the depth-first search. Must be a valid userID within
     *                        the scope of the current instance of DWInteractionGraph
     *                        [must be a key in userToIndexMap].
     * @param currentUser     The current userID from which to search the DWInteractionGraph from. When calling
     *                        tagSearch from an external context, currentUser should always be the same as
     *                        startUser.
     * @param alreadySearched The set of the userIDs that have already been depth-first searched to find endUser
     *                        as one of their email recipients. When calling tagSearch from an external
     *                        context, tagSearch should be an empty set.
     * @param firstSearch     Boolean which tells the method if it is the first search or not. When calling
     *                        tagSearch from an external context, firstSearch should always be true.
     * @return A list the successful search paths if a path of emails exists linking one user to
     * another in the current instance of DWInteractionGraph. Returns a list of -1 only
     * if no such path exists.
     */
    private List<Integer> tagSearch(int startUser, int endUser, int currentUser,
                                    Set<Integer> alreadySearched, boolean firstSearch) {

        //Gets the current user index.

        int currentUserIndex = userToIndexMap.get(currentUser);

        //dList is the list of successful searches that did not hit the startUser, or have no relations.
        List<Integer> dList = new ArrayList<>();

        //checks if it is the first search, if so adds the first user to dList.
        if (firstSearch) {
            dList.add(currentUser);
        }

        //Returns an empty list if the currentUser is the same as the endUser.
        if (currentUser == endUser) {
            return List.of();
        }

        //Returns the "tag" if the currentUser is the starting user, and it is not the first search.
        if (currentUser == startUser && !firstSearch) {
            return List.of(TAG);
        }

        //Iterates through all of the nonzero entries of the current user's row in the adjacency matrix, that are
        //not already in the alreadySearched list. Performs a new search based on the current index (user) of the
        //iteration and adds the results from the new search to dList if the search based on the current index is not
        //tagged with -1.

        for (int i = 0; i < this.adjacencyMatrix.length; i++) {
            if (this.adjacencyMatrix[currentUserIndex][i] > 0) {

                int nextUser = indexToUserMap.get(i);

                if (!alreadySearched.contains(nextUser)) {

                    alreadySearched.add(currentUser);
                    dList.add(nextUser);

                    List<Integer> nextList =
                        tagSearch(startUser, endUser, nextUser, alreadySearched, false);

                    if (!nextList.contains(TAG)) {
                        dList.addAll(nextList);
                        return dList;
                    }
                }
            }
        }
        //If none of the values in the current user's row in the adjacency matrix are not tagged or not in
        //alreadySearched, tag the search as unsuccessful.
        return List.of(TAG);
    }

    /* ------- Task 4 ------- */

    /**
     * Read the MP README file carefully to understand
     * what is required from this method.
     *
     * @param hours - the number of hours after which the firewall triggers. Users cannot be
     *              infected at the time that the firewall triggers, but can be infected at time =0;
     *
     * @return the maximum number of users that can be polluted in N hours
     */
    public int MaxBreachedUserCount(int hours) {

        final int triggerTime = hours * HOURSTOSECONDS;

        int end = times.length;
        Set<Integer> timeSet = new LinkedHashSet<>();

        int[] sortedTimes = Arrays.copyOf(times, times.length);

        Arrays.sort(sortedTimes);

        for (int i = 0; i < end; i++) {
            timeSet.add(sortedTimes[i]);
        }

        ArrayList<Interaction> emailSequence = new ArrayList<>();

        for (int i = 0; i < rawData.length; i++) {
            Interaction email = new Interaction(rawData[i][0],
                rawData[i][1], rawData[i][2]);
            emailSequence.add(email);
        }
        Collections.sort(emailSequence);

        int maxInfected = 0;
        int prevStartIndex = 0;

        // process each time window individually.
        for (int startTime : timeSet) {

            int maxTime = startTime + triggerTime;

            // for each time window, we need only check each email that
            // matches the start of the time window as the start email.
            int numStartsOnTime = 0;
            for (int startEmailIndex = prevStartIndex;
                 startEmailIndex < emailSequence.size(); startEmailIndex++) {


                if (emailSequence.get(startEmailIndex).time > startTime) {
                    break;
                }
                if (emailSequence.get(startEmailIndex).time == startTime) {

                    Set<Integer> corruptedSet = new HashSet<>();
                    // start with one infected user here
                    corruptedSet.add(emailSequence.get(startEmailIndex).senderUserID);

                    for (int checkEmailIndex = startEmailIndex;
                         checkEmailIndex < emailSequence.size(); checkEmailIndex++) {

                        int processTime = emailSequence.get(checkEmailIndex).time;
                        if (processTime > maxTime) {
                            break;
                        }
                        // processList ensures that we can process each time
                        // grouping of emails together to ensure the most
                        // possible interactions
                        ArrayList<Interaction> processList = new ArrayList<>();
                        for (int processIndex = startEmailIndex - numStartsOnTime;
                             processIndex < emailSequence.size(); processIndex++) {

                            if (emailSequence.get(processIndex).time > processTime) {
                                break;
                            } else if (emailSequence.get(processIndex).time == processTime) {
                                processList.add(emailSequence.get(processIndex));
                            }
                        }

                        // processlist requires processing one time for each
                        // element in processList.
                        for (int i = 0; i < processList.size(); i++) {

                            for (int j = 0; j < processList.size(); j++) {

                                if (corruptedSet.contains(processList.get(j).senderUserID)) {
                                    corruptedSet.add(processList.get(j).receiverUserID);
                                }
                            }
                        }


                    }

                    int infectedUsers = corruptedSet.size();
                    if (infectedUsers > maxInfected) {
                        maxInfected = infectedUsers;
                    }

                }

                numStartsOnTime++;
                prevStartIndex = startEmailIndex;
            }

        }


        return maxInfected;
    }


    /**
     * Returns the rawData of the current instance of the DWInteractionGraph.
     *
     * @return the rawData of the current instance of the DWInteractionGraph.
     */
    public int[][] getRawData() {
        return this.rawData.clone();
    }


    /**
     * Creates an adjacency matrix for a weighted and directed graph from raw data.
     *
     * @param rawData the rawData to build the adjacencyMatrix from, which is a nx3 2D matrix, containing email
     *                sender userIDs in its first column, email recipient userIDs in its second column, and send
     *                times in the third column.
     * @return An adjacency matrix for a weighted and directed graph that corresponds to the rawData of
     * email interactions provided to the method.
     */
    private int[][] createAdjacencyMatrix(int[][] rawData) {

        int sender;
        int receiver;

        //Creates a set of the current users in from the provided raw data matrix.

        Set<Integer> userSet = new TreeSet<>();

        for (int i = 0; i < rawData.length; i++) {
            for (int j = 0; j < USERS; j++) {
                userSet.add(rawData[i][j]);
            }
        }

        //Generates both the userToIndexMap and indexToUserMap based on the userSet.

        List<Integer> userList = new ArrayList<>(userSet);

        for (int i = 0; i < userSet.size(); i++) {
            this.userToIndexMap.put(userList.get(i), i);
            this.indexToUserMap.put(i, userList.get(i));
        }

        //Writes data into an adjacency matrix with indexes corresponding to userIDs, per userToIndexMap/indexToUserMap.

        int[][] matrixGen = new int[userSet.size()][userSet.size()];

        for (int i = 0; i < rawData.length; i++) {
            sender = rawData[i][0];
            receiver = rawData[i][1];

            matrixGen[userToIndexMap.get(sender)][userToIndexMap.get(receiver)]++;

        }
        return matrixGen;
    }

    /**
     * Creates an array that contains all of the times for which emails were sent in the inputted raw email data.
     *
     * @param baseArray A matrix of raw email data, which is a nx3 2D matrix, containing email
     *                  sender userIDs in its first column, email recipient userIDs in its second column, and send
     *                  times in the third column.
     * @return An array with all of the times emails were sent in the raw data matrix provided to the
     * method.
     */
    private int[] createTimeArray(int[][] baseArray) {
        int[] result = new int[baseArray.length];

        //Translates the third column of the input raw data into an 1D array.

        for (int i = 0; i < baseArray.length; i++) {
            result[i] = baseArray[i][TIME];
        }
        return result;
    }

    /**
     * Creates an adjacencyList for the current instance of DWInteractionGraph, based on the current adjacencyMatrix.
     * The adjacencyList stores directionality, but not weighting.
     */
    public void createAdjacencyList() {

        //Iterates through all of the non-zero values of each row in the adjacencyMatrix, adding each corresponding
        //UserID [from the index and translate via indexToUserMap], to a temporary list, which is then added to the
        //adjacency list with index corresponding to the userID per indexToUserMap.

        for (int i = 0; i < this.adjacencyMatrix.length; i++) {
            List<Integer> tempList = new ArrayList<>();
            for (int j = 0; j < this.adjacencyMatrix.length; j++) {

                if (this.adjacencyMatrix[i][j] > 0) {
                    tempList.add(indexToUserMap.get(j));
                }
            }
            this.adjacencyList.add((tempList));
        }
    }

    /**
     * Returns the adjacency list of the current instance of DWInteractionGraph.
     *
     * @return the adjacencyList of the current instance of DWInteractionGraph.
     */
    public List<List<Integer>> getAdjacencyList() {
        return List.copyOf(this.adjacencyList);
    }

}
