package agenda.presentation.command;

/**
 * Abstract class used to represent command
 */
public abstract class Command {

    /**
     * Execute this command
     * 
     * @param args arguments passed to this command
     */
    public abstract void exec(String[] args);

    /**
     * Get the name of this command.
     * 
     * @return the name of this command
     */
    public abstract String getName();

    /**
     * Get the description of this command.
     * 
     * @return the description of this command
     */
    public abstract String getDesc();
}
