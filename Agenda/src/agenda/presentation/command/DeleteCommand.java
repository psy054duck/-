package agenda.presentation.command;

import java.util.ArrayList;

import agenda.business.*;

/**
 * Subclass of Command used to implement delete.
 */
public class DeleteCommand extends Command {
    private final ArrayList<User> users;

    /**
     * Constructor with all users in this program
     * 
     * @param pUsers all users
     */
    public DeleteCommand(ArrayList<User> pUsers) {
        users = pUsers;
    }

    /**
     * Execute this command.
     * 
     * @param args arguments passed to this command
     */
    public void exec(String[] args) {
        if (args.length != 4) {
            System.out.println("Usage: delete [userName] [password] [meetingId]");
            return ;
        }
        
        User user = getUserByName(args[1]);
        if (user == null) {
            System.out.printf("%s is not in this program\n", args[1]);
        } else if (! user.checkPassword(args[2])) {
            System.out.println("Wrong password");
        } else {
            if (user.deleteMeetingByTitle(args[3])) {
                System.out.println("Done");
            } else {
                System.out.printf("%s doesn't have meeting titled %s\n",
                                  args[1], args[3]);
            }
        }
    }

    /**
     * Get the name of this command.
     * 
     * @return the name of this command
     */
    public String getName() {
        return "delete";
    }

    /**
     * Get the description of this command.
     * 
     * @return the description of this command
     */
    public String getDesc() {
        return "Delete some meeting of a user";
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