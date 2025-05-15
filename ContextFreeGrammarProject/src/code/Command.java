package code;

/**
 * Represents a command that can be executed with a set of string arguments.
 */
public interface Command {
    /**
     * Executes this command using the given arguments.
     *
     * @param args an array of String arguments for the command
     */
    void execute(String[] args);
}
