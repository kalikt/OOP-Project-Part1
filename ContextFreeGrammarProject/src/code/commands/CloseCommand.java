package code.commands;

import code.Command;
import code.GrammarManager;

public class CloseCommand implements Command {
    private GrammarManager manager;

    public CloseCommand(GrammarManager manager) {
        this.manager = manager;
    }

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
