package code.commands;

import code.Command;
import code.FileHandler;
import code.Grammar;
import code.GrammarManager;

import java.io.IOException;

public class SaveCommand implements Command {
    private GrammarManager manager;

    public SaveCommand(GrammarManager manager) {
        this.manager = manager;
    }

    @Override
    public void execute(String[] args) {
        String currentFilePath = manager.getCurrentFilePath();
        //save
        if (args == null || args.length == 1) {
            if (currentFilePath == null) {
                System.out.println("No file is currently open.");
                return;
            }
            try {
                FileHandler.saveGrammarsToFile(currentFilePath, manager.getGrammars());
                System.out.println("Successfully saved " + currentFilePath);
            } catch (IOException e) {
                System.out.println("Error saving file: " + e.getMessage());
            }
            return;
        }

        //save <grammarId> <filename>
        if (args.length >= 3) {
            String grammarId = args[1];
            Grammar grammar = manager.getGrammar(grammarId);
            if (grammar == null) {
                System.out.println("Grammar with ID " + grammarId + " not found.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }
            String filePath = sb.toString().trim();

            try {
                FileHandler.saveGrammarToFile(filePath, grammar);
                System.out.println("Grammar " + grammarId + " saved to " + filePath);
            } catch (IOException e) {
                System.out.println("Error saving grammar: " + e.getMessage());
            }
            return;
        }

        System.out.println("Usage:");
        System.out.println("save (saves all grammars to the opened file)");
        System.out.println("save <id> <file> (saves grammar <id> to <file>)");
    }
}
