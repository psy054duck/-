package agenda.presentation;

import java.util.ArrayList;
import java.util.Scanner;

import agenda.business.*;
import agenda.presentation.command.*;

/**
 * Class for ui.
 * Singleton pattern is applied to this class.
 */
public class UI {
    private static UI instance = null;

    private ArrayList<User> users = new ArrayList<User>();
    private ArrayList<Meeting> meetings = new ArrayList<Meeting>();
    private ArrayList<Command> commands = new ArrayList<Command>();

    /**
     * Get the singleton of UI.
     * 
     * @return the singleton of UI
     */
    public static UI getInstance() {
        if (instance == null) {
            instance = new UI();
        }
        return instance;
    }

    /**
     * Constructor with a sequence of operations to add commands to this program.
     */
    private UI() {
        commands.add(new HelpCommand(commands));
        commands.add(new AddCommand(users, meetings));
        commands.add(new RegisterCommand(users));
        commands.add(new QueryCommand(users));
        commands.add(new DeleteCommand(users));
        commands.add(new ClearCommand(users));
        commands.add(new ListCommand(users, meetings));
        commands.add(new BatchCommand());
        commands.add(new QuitCommand());
        sayHello();
    }

    /**
     * Get commands from command line.
     * 
     * @return commands from command line
     */
    public String getCmd() {
        System.out.print(getPrompt());
        Scanner in = new Scanner(System.in);
        return in.nextLine().trim();
    }

    /**
     * Get prompt.
     * 
     * @return prompt
     */
    public String getPrompt() {
        return "$ ";
    }

    /**
     * Execute commands
     * 
     * @param cmd commands to be executed
     */
    public void execCmd(String cmd) {
        String[] args = cmd.split("\\s+");
        for (Command command: commands) {
            if (command.getName().toUpperCase().equals(args[0].toUpperCase())) {
                command.exec(args);
                return;
            }
        }
        System.out.printf("Can't not find command '%s'\n", cmd);
    }

    /**
     * Say hello to users.
     */
    private void sayHello() {
        commands.get(0).exec(null);
    }
}
