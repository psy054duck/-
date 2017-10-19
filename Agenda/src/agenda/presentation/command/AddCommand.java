package agenda.presentation.command;

import java.util.ArrayList;

import agenda.business.*;

/**
 * Subclass of Command used to implement add.
 */
public class AddCommand extends Command {
    private final ArrayList<User> users;
    private final ArrayList<Meeting> meetings;

    /**
     * Constructor with users and meetings in this program.
     * 
     * @param pUsers users in this program
     * @param pMeetings meetings in this program
     */
    public AddCommand(ArrayList<User> pUsers, ArrayList<Meeting> pMeetings) {
        users = pUsers;
        meetings = pMeetings;
    }

    /**
     * Execute this command
     * 
     * @param args arguments passed to this command
     */
    public void exec(String[] args) {
        if (args.length != 7) {
            System.out.println("Usage: add [userName] [password] [other]"
                               + " [start] [end] [title]");
            return;
        }

        User sponsor = getUserByName(args[1]);
        User participator = getUserByName(args[3]);
        if (sponsor == null) {
            System.out.println(sponsor.getName() + " is not in this program");
        } else if (participator == null) {
            System.out.println(participator.getName() + " is not in this program");
        } else if (! sponsor.getPassword().equals(args[2])) {
            System.out.println("Wrong password");
        } else {
            Date begin = Date.stringToDate(args[4]);
            Date end = Date.stringToDate(args[5]);
            Meeting meeting = new Meeting(args[6], sponsor, participator,
                                          begin, end);
            meetings.add(meeting);
            System.out.println("Done");
        }
    }

    /**
     * Get the name of this command
     * 
     * @return the name of this command
     */
    public String getName() {
        return "add";
    }

    /**
     * Get the description of this command.
     * 
     * @return the description of this command
     */
    public String getDesc() {
        return "Add a meeting";
    }

    /**
     * Auxiliary function to get a User specified with user name.
     * 
     * @param user the user name
     * 
     * @return null if the user is not found
     */
    private User getUserByName(String user) {
        for (User u : users) {
            if (u.getName().equals(user)) {
                return u;
            }
        }
        return null;
    }
}