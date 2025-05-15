package code.commands;

import code.Command;

/**
 * Command that terminates the application.
 */
public class ExitCommand implements Command {
    @Override
    public void execute(String[] args) {
        System.out.println("Exiting application...");
        System.exit(0);
    }
}
