package code.commands;

import code.Command;

public class ExitCommand implements Command {
    @Override
    public void execute(String[] args) {
        System.out.println("Exiting program...");
        System.exit(0);
    }
}
