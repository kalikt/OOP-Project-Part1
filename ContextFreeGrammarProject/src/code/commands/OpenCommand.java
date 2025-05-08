package code.commands;

import code.Command;
import code.FileHandler;
import code.Grammar;
import code.GrammarManager;

import java.io.IOException;
import java.util.Map;

public class OpenCommand implements Command {
    private GrammarManager manager;

    public OpenCommand(GrammarManager manager) {
        this.manager = manager;
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: open <file>");
            return;
        }
        String filePath = args[1];
        try {
            Map<String, Grammar> loadedGrammars = FileHandler.loadGrammarsFromFile(filePath);

            for (Grammar grammar : loadedGrammars.values()) {
                manager.addGrammar(grammar);
            }

            manager.setCurrentFilePath(filePath);

            if (loadedGrammars.isEmpty()) {
                System.out.println("No grammars found in the file. If the file did not exist, a new one has been created.");
            } else {
                System.out.println("File " + filePath + " opened successfully. Loaded " + loadedGrammars.size() + " grammar(s).");
            }
        } catch (IOException e) {
            System.out.println("Error opening file: " + e.getMessage());
        }
    }
}
