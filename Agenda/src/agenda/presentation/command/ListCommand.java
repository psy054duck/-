package agenda.presentation.command;

import java.util.ArrayList;

import agenda.business.*;

/**
 * Subclass of Command used to implement ls
 */
public class ListCommand extends Command {
    private final ArrayList<User> users;
    private final ArrayList<Meeting> meetings;

    /**
     * Constructor with users and meetings in this program.
     * 
     * @param pUsers user list
     * @param pMeetings meeting list
     */
    public ListCommand(ArrayList<User> pUsers, ArrayList<Meeting> pMeetings) {
        users = pUsers;
        meetings = pMeetings;
    }

    /**
     * Execute this command.
     * 
     * @param args arguments passed to this command
     */
    public void exec(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: ls [-u][-m]");
        } else if (args[1].equals("-u")) {
            for (User user : users) {
                System.out.println(user.getName());
            }
        } else if (args[1].equals("-m")) {
            for (Meeting meeting : meetings) {
                System.out.println(meeting.toString());
            }
        }
    }

    /**
     * Get the name of this command.
     * 
     * @return the name of this command
     */
    public String getName() {
        return "ls";
    }

    /**
     * Get the description of this command.
     * 
     * @return the description of this command
     */
    public String getDesc() {
        return "List users or meetings in this program";
    }
}