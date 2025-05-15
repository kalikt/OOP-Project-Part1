package code.commands;

import code.Command;
import code.GrammarManager;

/**
 * Command that closes the currently open grammar file by clearing all loaded grammars
 * and resetting the manager’s file path.
 */
public class CloseCommand implements Command {
    private GrammarManager manager;

    public CloseCommand(GrammarManager manager) {
        this.manager = manager;
    }

    /**
     * Executes the "close" command.
     * <p>
     * If no file is open, prints "No file is currently open."
     * Otherwise, clears all grammars and prints a success message.
     * </p>
     *
     * @param args the command arguments (none)
     */
    @Override
    public void execute(String[] args) {
        String currentFilePath = manager.getCurrentFilePath();
        if (manager.isEmpty()) {
            System.out.println("No file is currently open.");
            return;
        }
        manager.clearGrammars();
        currentFilePath = null;
        System.out.println("The file has been closed successfully.");
    }
}
