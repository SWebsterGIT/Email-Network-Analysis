package cpen221.mp2;

class Interaction implements Comparable{

    // RI: For each instance of an Interaction, senderID, receiverID, and time are all
    // part of (any) same line in the input file

    // AF: represents an email with sender = senderUserID, receiver = receiverID, and time = time

    // This class is used to create a list of "Interactions" (or
    // emails) that can be sorted by time in order to create a time-ordered list
    // for the purposes of task 4.

    public final int senderUserID;
    public final int receiverUserID;
    public final int time;

    public Interaction(int senderUserID, int receiverUserID, int time){

        this.senderUserID = senderUserID;
        this.receiverUserID = receiverUserID;
        this.time = time;

    }

    // This is a special case where I am not also overriding the equals() method
    // The class is package-private and the only call to this datatype involves sorting with
    // Collections.sort, so we only need to override this method.

    // this compareTo() override compares the times of each interaction.

    @Override
    public int compareTo(Object o){
        if(!(o instanceof Interaction)){
            throw new RuntimeException("cannot compare this object to an Interaction");
        }
        Interaction otherInteraction = (Interaction) o;
        if(this.time > otherInteraction.time){
            return 1;
        }else if(this.time < otherInteraction.time){
            return -1;
        }else{
            return 0;
        }
    }


}
