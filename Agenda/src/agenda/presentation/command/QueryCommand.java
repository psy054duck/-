package agenda.presentation.command;

import java.util.ArrayList;

import agenda.business.*;

/**
 * Subclass of Command used to implement query.
 */
public class QueryCommand extends Command {
    private final ArrayList<User> users;
    /**
     * Constructor with all users in this program.
     * 
     * @param pUsers ArrayList of users in this program
     */
    public QueryCommand(ArrayList<User> pUsers) {
        users = pUsers;
    }

    /**
     * Execute this command.
     * 
     * @param args arguments passed to this commad
     */
    public void exec(String[] args) {
        if (args.length != 5) {
            System.out.println("Usage: query [userName] [password] [start] [end]");
            return ;
        }

        User user = getUserByName(args[1]);
        if (user == null) {
            System.out.printf("%s is not in this program\n", args[1]);
        } else if (! user.checkPassword(args[2])) {
            System.out.println("Wrong password");
        } else {
            try {
                Date begin = Date.stringToDate(args[3]);
                Date end = Date.stringToDate(args[4]);
                ArrayList<Meeting> meetingList = user.getMeetingsByDate(begin, end);

                for (Meeting meeting : meetingList) {
                    System.out.println(meeting.toString());
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Get the name of this command.
     * 
     * @return the name of this command
     */
    public String getName() {
        return "query";
    }

    /**
     * Get the description of this command.
     * 
     * @return the description of this command
     */
    public String getDesc() {
        return "Get all meetings a user should"
               + " participate in during the given time";
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