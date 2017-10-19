package agenda.presentation.command;

import java.util.ArrayList;

import agenda.business.*;

/**
 * Subclass of Command used to implement clear.
 */
public class ClearCommand extends Command {
    private final ArrayList<User> users;

    /**
     * Constructor with all users in this program.
     * 
     * @param pUsers all users
     */
    public ClearCommand(ArrayList<User> pUsers) {
        users = pUsers;
    }

    /**
     * Execute this command.
     * 
     * @param args arguments passed to this command
     */
    public void exec(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: clear [userName] [password]");
            return ;
        }

        User user = getUserByName(args[1]);

        if (user == null) {
            System.out.printf("%s is not in this program\n", args[1]);
        } else if (! user.checkPassword(args[2])) {
            System.out.println("Wrong password");
        } else {
            user.clearAllMeetings();
            System.out.println("Done");
        }
    }

    /**
     * Get the name of this commnad.
     * 
     * @return the name of this command
     */
    public String getName() {
        return "clear";
    }

    /**
     * Get the description of this command.
     * 
     * @return the description of this command
     */
    public String getDesc() {
        return "Clear all meetings of a user";
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