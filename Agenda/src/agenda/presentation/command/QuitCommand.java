package agenda.presentation.command;

/**
 * Subclass of Command used to implement exit.
 */
public class QuitCommand extends Command {

    /**
     * Execute command.
     * 
     * @param args arguments passed to this command
     */
    public void exec(String[] args) {
        Runtime.getRuntime().exit(0);
    }

    /**
     * Get the name of this command.
     * 
     * @return the name of this command
     */
    public String getName() {
        return "quit";
    }

    /**
     * Get the description of this command.
     * 
     * @return the description of this command
     */
    public String getDesc() {
        return "Exit this program";
    }
}