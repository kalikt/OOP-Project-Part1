import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class CommandManager {
    private GrammarManager manager;
    private String currentFilePath;

    public CommandManager(GrammarManager manager) {
        this.manager = manager;
    }

    public void handleRemoveRule(String[] args) {
        if (args == null || args.length < 3) {
            System.out.println("Usage: removeRule <grammarId> <ruleNumber>");
            return;
        }
        String grammarId = args[1];
        int index;
        try {
            index = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid rule number: " + args[2]);
            return;
        }
        Grammar grammar = manager.getGrammar(grammarId);
        if (grammar == null) {
            System.out.println("Grammar with ID " + grammarId + " not found.");
            return;
        }

        List<Rule> rules = new ArrayList<>(grammar.getAllRules());
        rules.sort(Comparator.comparingInt(r -> Integer.parseInt(r.getId().substring(1))));

        if (index < 1 || index > rules.size()) {
            System.out.println("Rule number out of range. There are " + rules.size() + " rule(s).");
            return;
        }

        String ruleId = rules.get(index - 1).getId();
        try {
            manager.removeRule(grammarId, ruleId);
            System.out.println("Removed rule " + ruleId + " from grammar " + grammarId);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void handleAddRule(String[] args) {
        if (args == null || args.length < 4) {
            System.out.println("Usage: addRule <grammarId> <leftSide> -> <rightSide>");
            return;
        }

        String grammarId = args[1];

        StringBuilder sb = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String ruleDefinition = sb.toString().trim();

        String[] parts = ruleDefinition.split("->", 2);
        if (parts.length != 2) {
            System.out.println("Invalid rule format. Expected format: <leftSide> -> <rightSide>");
            return;
        }

        String leftPart = parts[0].trim();
        String rightPart = parts[1].trim();

        if (leftPart.isEmpty() || rightPart.isEmpty()) {
            System.out.println("Invalid rule format. Left or right side is empty.");
            return;
        }

        char leftSide = leftPart.charAt(0);

        Grammar grammar = manager.getGrammar(grammarId);
        if (grammar == null) {
            System.out.println("Grammar with ID " + grammarId + " not found.");
            return;
        }

        int ruleNumber = grammar.getAllRules().size() + 1;
        String ruleId = "r" + ruleNumber;

        try {
            manager.addRule(grammarId, ruleId, leftSide, rightPart);
            System.out.println("Added rule " + ruleId + " to grammar " + grammarId);
        } catch(Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void handlePrint(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: print <grammarId>");
            return;
        }
        String grammarId = args[1];
        Grammar grammar = manager.getGrammar(grammarId);
        if (grammar == null) {
            System.out.println("Grammar with ID " + grammarId + " not found.");
            return;
        }

        System.out.println("Grammar ID: " + grammar.getId());
        System.out.println("Start Symbol: " + grammar.getStartSymbol());

        System.out.print("Variables: ");
        for (char var : grammar.getVariables()) {
            System.out.print(var + " ");
        }
        System.out.println();

        System.out.print("Terminals: ");
        for (char term : grammar.getTerminals()) {
            System.out.print(term + " ");
        }
        System.out.println();

        System.out.println("Rules:");

        List<Rule> rules = new ArrayList<>(grammar.getAllRules());
        rules.sort(Comparator.comparingInt(r -> Integer.parseInt(r.getId().substring(1))));
        int index = 1;
        for (Rule rule : rules) {
            System.out.println(index + ". " + rule);
            index++;
        }
    }

    public void handleList(String[] args) {
        Map<String, Grammar> all = manager.getGrammars();
        if (all.isEmpty()) {
            System.out.println("No grammars loaded.");
            return;
        }
        System.out.println("Loaded grammars:");
        for (String id : all.keySet()) {
            System.out.println("- " + id);
        }
    }

    public void handleClose(String[] args) {
        if (manager.isEmpty()) {
            System.out.println("No file is currently open.");
            return;
        }
        manager.clearGrammars();
        currentFilePath = null;
        System.out.println("The file has been closed successfully.");
    }

    public void handleSaveAs(String[] args) {
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

    public void handleSave(String[] args) {
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
    }

    public void handleOpen(String[] args) {
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

            currentFilePath = filePath;

            if (loadedGrammars.isEmpty()) {
                System.out.println("No grammars found in the file. If the file did not exist, a new one has been created.");
            } else {
                System.out.println("File " + filePath + " opened successfully. Loaded " + loadedGrammars.size() + " grammar(s).");
            }
        } catch (IOException e) {
            System.out.println("Error opening file: " + e.getMessage());
        }
    }

    public void handleHelp(String[] args) {
        System.out.println("The following commands are supported:");
        System.out.println("open <file> - Opens a file");
        System.out.println("close - Closes the currently opened file");
        System.out.println("save - Saves the currently open file");
        System.out.println("saveas <file> - Saves the currently open file with a new name");
        System.out.println("list - Lists all grammars");
        System.out.println("print <id> - Prints a grammar");
        System.out.println("addRule <grammarId> <rule> - Adds a rule to a grammar");
        System.out.println("removeRule <grammarId> <ruleNumber> - Removes a rule from a grammar");
        System.out.println("union <id1> <id2> - Performs union of two grammars");
        System.out.println("concat <id1> <id2> - Performs concatenation of two grammars");
        System.out.println("chomsky <id> - Checks if a grammar is in Chomsky normal form");
        System.out.println("cyk <id> - Checks if a word is in the language of a grammar (CYK algorithm)");
        System.out.println("iter <id> - Performs Kleene star operation on a grammar");
        System.out.println("empty <id> - Checks if a grammar's language is empty");
        System.out.println("chomskify <id> - Converts a grammar to Chomsky normal form");
        System.out.println("help - Prints this information");
        System.out.println("exit - Exits the program");
    }

    public void handleExit(String[] args) {
        System.out.println("Exiting program...");
        System.exit(0);
    }
}