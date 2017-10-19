package agenda.presentation.command;

import java.util.ArrayList;
import java.util.Scanner;

import agenda.business.User;

/**
 * Subclass of Command used to register a new user.
 */
public class RegisterCommand extends Command {
    private ArrayList<User> users;

    /**
     * Constructor with an ArrayList containing all users in this application.
     * 
     * @param us the ArrayList containing all users in this application
     */
    public RegisterCommand(ArrayList<User> us) {
        users = us;
    }

    /**
     * Execute this command.
     * 
     * @param args arguments passed to this command
     */
    public void exec(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: register [userName][password]");
        } else {
            for (User user : users) {
                if (user.getName().equals(args[1])) {
                    System.out.println("This user name has been registered");
                    return;
                }
            }
            users.add(new User(args[1], args[2]));
            System.out.println("Done");
        }
    }

    public String getName() {
        return "register";
    }

    public String getDesc() {
        return "Register a new user";
    }

    /**
     * Check whether the new user's name has already been registered.
     * 
     * @param name the new user's name
     */
    private boolean isDuplicated(String name) {
        for (User user : users) {
            if (name.equals(user.getName())) {
                return true;
            }
        }
        return false;
    }

}