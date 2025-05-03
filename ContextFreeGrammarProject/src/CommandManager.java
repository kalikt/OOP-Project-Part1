import java.io.IOException;
import java.util.*;

public class CommandManager {
    private GrammarManager manager;
    private String currentFilePath;

    public CommandManager(GrammarManager manager) {
        this.manager = manager;
    }

    public void handleChomskify(String[] args) {
        if (args == null || args.length < 2) {
            System.out.println("Usage: chomskify <grammarId>");
            return;
        }
        String oldId = args[1];
        Grammar g = manager.getGrammar(oldId);
        if (g == null) {
            System.out.println("Grammar with ID " + oldId + " not found.");
            return;
        }

        if (isCNF(g)) {
            System.out.println("Grammar " + oldId + " is already in Chomsky Normal Form.");
            return;
        }

        int maxNum = 0;
        for (String id : manager.getGrammars().keySet()) {
            if (id.startsWith("G")) {
                try {
                    maxNum = Math.max(maxNum, Integer.parseInt(id.substring(1)));
                } catch (NumberFormatException ignored) {}
            }
        }
        String newId = "G" + (maxNum + 1);

        Grammar cnf = new Grammar(newId, g.getStartSymbol());

        for (char V : g.getVariables()) cnf.addVariable(V);
        for (char t : g.getTerminals())   cnf.addTerminal(t);

        Map<Character,Character> termToVar = new HashMap<>();
        List<Character> freeVars = new ArrayList<>();
        for (char c = 'A'; c <= 'Z'; c++) {
            if (!cnf.getVariables().contains(c)) freeVars.add(c);
        }
        Iterator<Character> freeIt = freeVars.iterator();
        int ruleNum = 1;

        for (char t : g.getTerminals()) {
            if (!termToVar.containsKey(t) && freeIt.hasNext()) {
                char X = freeIt.next();
                termToVar.put(t, X);
                cnf.addVariable(X);
                cnf.addRule("r" + (ruleNum++), X, "" + t);
            }
        }

        for (Rule r : g.getAllRules()) {
            char A = r.getLeftSide();
            String rhs = r.getRightSide();

            List<String> syms = new ArrayList<>();
            for (char c : rhs.toCharArray()) {
                if (g.getTerminals().contains(c) && rhs.length() > 1) {
                    syms.add(termToVar.get(c).toString());
                } else {
                    syms.add(String.valueOf(c));
                }
            }

            if (syms.size() == 1) {
                cnf.addRule("r" + (ruleNum++), A, syms.get(0));
            }
            else if (syms.size() == 2) {
                cnf.addRule("r" + (ruleNum++), A, syms.get(0) + syms.get(1));
            }
            else {
                String X1 = syms.get(0);
                String Y1 = freeIt.hasNext() ? freeIt.next().toString() : null;
                if (Y1 == null) {
                    System.out.println("Out of variable names during chomskify");
                    return;
                }
                cnf.addVariable(Y1.charAt(0));
                cnf.addRule("r" + (ruleNum++), A, X1 + Y1);

                for (int i = 1; i < syms.size() - 2; i++) {
                    String Xi = syms.get(i);
                    String Yi = freeIt.hasNext() ? freeIt.next().toString() : null;
                    if (Yi == null) {
                        System.out.println("Out of variable names during chomskify");
                        return;
                    }
                    cnf.addVariable(Yi.charAt(0));
                    cnf.addRule("r" + (ruleNum++), Y1.charAt(0), Xi + Yi);
                    Y1 = Yi;
                }
                String penult = syms.get(syms.size() - 2);
                String last   = syms.get(syms.size() - 1);
                cnf.addRule("r" + (ruleNum++), Y1.charAt(0), penult + last);
            }
        }

        manager.addGrammar(cnf);
        System.out.println("Created grammar " + newId);
    }


    public void handleChomsky(String[] args) {
        if (args == null || args.length < 2) {
            System.out.println("Usage: chomsky <grammarId>");
            return;
        }
        Grammar g = manager.getGrammar(args[1]);
        if (g == null) {
            System.out.println("Grammar " + args[1] + " not found.");
            return;
        }
        if (isCNF(g)) {
            System.out.println("Grammar " + g.getId() + " is in Chomsky Normal Form.");
        } else {
            System.out.println("Grammar " + g.getId() + " is NOT in Chomsky Normal Form.");
        }
    }

    private boolean isCNF(Grammar g) {
        for (Rule r : g.getAllRules()) {
            String rhs = r.getRightSide();
            if (rhs.length() == 1) {
                // A -> a
                char c = rhs.charAt(0);
                if (!g.getTerminals().contains(c)) return false;
            } else if (rhs.length() == 2) {
                // A -> BC
                char c0 = rhs.charAt(0), c1 = rhs.charAt(1);
                if (!g.getVariables().contains(c0) || !g.getVariables().contains(c1))
                    return false;
            } else {
                return false;
            }
        }
        return true;
    }

    public void handleConcat(String[] args) {
        if (args == null || args.length < 3) {
            System.out.println("Usage: concat <grammarId1> <grammarId2>");
            return;
        }
        String id1 = args[1];
        String id2 = args[2];
        Grammar g1 = manager.getGrammar(id1);
        Grammar g2 = manager.getGrammar(id2);
        if (g1 == null) {
            System.out.println("Grammar with ID " + id1 + " not found.");
            return;
        }
        if (g2 == null) {
            System.out.println("Grammar with ID " + id2 + " not found.");
            return;
        }

        int maxNum = 0;
        for (String existingId : manager.getGrammars().keySet()) {
            if (existingId.startsWith("G")) {
                try {
                    int num = Integer.parseInt(existingId.substring(1));
                    maxNum = Math.max(maxNum, num);
                } catch (NumberFormatException ignored) {}
            }
        }
        String newId = "G" + (maxNum + 1);

        Set<Character> vars = new HashSet<>(g1.getVariables());
        vars.addAll(g2.getVariables());
        Set<Character> terms = new HashSet<>(g1.getTerminals());
        terms.addAll(g2.getTerminals());

        char newStart = 'S';
        for (char c = 'A'; c <= 'Z'; c++) {
            if (!vars.contains(c)) {
                newStart = c;
                break;
            }
        }

        Grammar cat = new Grammar(newId, newStart);
        for (char v : vars)   cat.addVariable(v);
        for (char t : terms)  cat.addTerminal(t);

        int ruleNum = 1;
        for (Rule r : g1.getAllRules()) {
            cat.addRule("r" + ruleNum++, r.getLeftSide(), r.getRightSide());
        }
        for (Rule r : g2.getAllRules()) {
            cat.addRule("r" + ruleNum++, r.getLeftSide(), r.getRightSide());
        }

        char s1 = g1.getStartSymbol();
        char s2 = g2.getStartSymbol();
        cat.addRule("r" + ruleNum++, newStart, "" + s1 + s2);

        manager.addGrammar(cat);
        System.out.println("Created grammar " + newId);
    }

    public void handleUnion(String[] args) {
        if (args == null || args.length < 3) {
            System.out.println("Usage: union <grammarId1> <grammarId2>");
            return;
        }
        String id1 = args[1];
        String id2 = args[2];
        Grammar g1 = manager.getGrammar(id1);
        Grammar g2 = manager.getGrammar(id2);
        if (g1 == null) {
            System.out.println("Grammar with ID " + id1 + " not found.");
            return;
        }
        if (g2 == null) {
            System.out.println("Grammar with ID " + id2 + " not found.");
            return;
        }

        int maxNum = 0;
        for (String existingId : manager.getGrammars().keySet()) {
            if (existingId.startsWith("G")) {
                int num = Integer.parseInt(existingId.substring(1));
                if (num > maxNum) maxNum = num;
            }
        }
        String newId = "G" + (maxNum + 1);

        Set<Character> vars = new HashSet<>(g1.getVariables());
        vars.addAll(g2.getVariables());
        Set<Character> terms = new HashSet<>(g1.getTerminals());
        terms.addAll(g2.getTerminals());

        char newStart = 'S';
        for (char c = 'A'; c <= 'Z'; c++) {
            if (!vars.contains(c)) {
                newStart = c;
                break;
            }
        }

        Grammar unionG = new Grammar(newId, newStart);
        for (char v : vars)   unionG.addVariable(v);
        for (char t : terms)  unionG.addTerminal(t);

        int ruleNum = 1;
        for (Rule r : g1.getAllRules()) {
            String rid = "r" + ruleNum++;
            unionG.addRule(rid, r.getLeftSide(), r.getRightSide());
        }
        for (Rule r : g2.getAllRules()) {
            String rid = "r" + ruleNum++;
            unionG.addRule(rid, r.getLeftSide(), r.getRightSide());
        }

        unionG.addRule("r" + ruleNum++, newStart, Character.toString(g1.getStartSymbol()));
        unionG.addRule("r" + ruleNum++, newStart, Character.toString(g2.getStartSymbol()));

        manager.addGrammar(unionG);
        System.out.println("Created grammar " + newId);
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
        System.out.println("  save                (saves all grammars to the opened file)");
        System.out.println("  save <id> <file>    (saves grammar <id> to <file>)");
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
        System.out.println("save <id> <file> - Saves grammar to file");
        System.out.println("addRule <grammarId> <rule> - Adds a rule to a grammar");
        System.out.println("removeRule <grammarId> <ruleNumber> - Removes a rule from a grammar");
        System.out.println("union <id1> <id2> - Performs union of two grammars and creates a new one");
        System.out.println("concat <id1> <id2> - Performs concatenation of two grammars and creates a new one");
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