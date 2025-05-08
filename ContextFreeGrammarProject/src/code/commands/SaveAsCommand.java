package code.commands;

import code.Command;
import code.FileHandler;
import code.GrammarManager;

import java.io.IOException;

public class SaveAsCommand implements Command {
    private GrammarManager manager;

    public SaveAsCommand(GrammarManager manager) {
        this.manager = manager;
    }

    @Override
    public void execute(String[] args) {
        String currentFilePath = manager.getCurrentFilePath();

        if (manager.getGrammars().isEmpty()) {
            System.out.println("No file is currently open.");
            return;
        }
        if (args == null || args.length < 2) {
            System.out.println("Usage: saveas <file>");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String newFilePath = sb.toString().trim();

        try {
            FileHandler.saveGrammarsToFile(newFilePath, manager.getGrammars());
            currentFilePath = newFilePath;
            System.out.println("Successfully saved as " + newFilePath);
        } catch (IOException e) {
            System.out.println("Error saving file as: " + e.getMessage());
        }
    }
}
