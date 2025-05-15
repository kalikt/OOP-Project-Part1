package code.commands;

import code.Command;
import code.Grammar;
import code.GrammarManager;

import java.util.Map;

/**
 * Command that lists all grammars currently loaded in the manager.
 */
public class ListCommand implements Command {
    private GrammarManager manager;

    public ListCommand(GrammarManager manager) {
        this.manager = manager;
    }

    /**
     * Executes the "list" command.
     * <p>
     * Retrieves the map of grammars from the manager and prints each grammar ID.
     * If no grammars are loaded, prints "No grammars loaded."
     * </p>
     *
     * @param args the command arguments (none)
     */
    @Override
    public void execute(String[] args) {
        Map<String, Grammar> grammars = manager.getGrammars();
        if (grammars.isEmpty()) {
            System.out.println("No grammars loaded.");
            return;
        }
        System.out.println("Loaded grammars:");
        for (String id : grammars.keySet()) {
            System.out.println("- " + id);
        }
    }
}
