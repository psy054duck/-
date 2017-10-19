package agenda.presentation.command;

import java.util.ArrayList;

/**
 * Subclass of Command used to implement help.
 */
public class HelpCommand extends Command {
    private ArrayList<Command> commands;

    /**
     * Constructor with an ArrayList containing all commands in this program.
     * 
     * @param cmds ArrayList containing all commands in this program
     */
    public HelpCommand(ArrayList<Command> cmds) {
        commands = cmds;
    }

    /**
     * Execute this command.
     * 
     * @param args arguments passed to this command
     */
    public void exec(String[] args) {
        System.out.printf("%22s\n", "Menu");
        printMinus(40);
        for (Command cmd : commands) {
            System.out.printf("%-8s%15s%-20s\n", cmd.getName(), "--", cmd.getDesc());
        }
    }

    /**
     * Get the name of this command.
     * 
     * @return the name of this command
     */
    public String getName() {
        return "help";
    }

    /**
     * Get the description of this command.
     * 
     * @return the description of this command
     */
    public String getDesc() {
        return "Display command menu";
    }

    /**
     * Print the given number of minus.
     * 
     * @param num the number of minus
     */
    private void printMinus(int num) {
        for (int i = 0; i < num; ++i) {
            System.out.print('-');
        }
        System.out.print("\n");
    }
}