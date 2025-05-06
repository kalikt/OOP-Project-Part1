import java.io.IOException;
import java.util.*;

public class CommandManager {
    private GrammarManager manager;
    private String currentFilePath;

    public CommandManager(GrammarManager manager) {
        this.manager = manager;
    }

    public void handleIter(String[] args) {
        if (args == null || args.length < 2) {
            System.out.println("Usage: iter <grammarId>");
            return;
        }
        String oldId = args[1];
        Grammar grammar = manager.getGrammar(oldId);
        if (grammar == null) {
            System.out.println("Grammar with ID " + oldId + " not found.");
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

        Set<Character> vars  = new HashSet<>(grammar.getVariables());
        char newStart = 'S';
        for (char c = 'A'; c <= 'Z'; c++) {
            if (!vars.contains(c)) {
                newStart = c;
                break;
            }
        }

        Grammar kleeneStar = new Grammar(newId, newStart);
        for (char v : grammar.getVariables()) {
            kleeneStar.addVariable(v);
        }
        kleeneStar.addVariable(newStart);
        for (char t : grammar.getTerminals()) {
            kleeneStar.addTerminal(t);
        }
        kleeneStar.addTerminal('ε');

        int ruleNum = 1;
        for (Rule r : grammar.getAllRules()) {
            kleeneStar.addRule("r" + ruleNum++, r.getLeftSide(), r.getRightSide());
        }

        kleeneStar.addRule("r" + ruleNum++, newStart, "ε");
        kleeneStar.addRule("r" + ruleNum++, newStart, "" + grammar.getStartSymbol() + newStart);

        manager.addGrammar(kleeneStar);
        System.out.println("Created grammar " + newId);
    }


    public void handleEmpty(String[] args) {
        if (args == null || args.length < 2) {
            System.out.println("Usage: empty <grammarId>");
            return;
        }
        String grammarId = args[1];

        Grammar grammar = manager.getGrammar(grammarId);
        if (grammar == null) {
            System.out.println("Grammar with ID " + grammarId + " not found.");
            return;
        }

        Set<Character> productive = new HashSet<>();
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Rule r : grammar.getAllRules()) {
                char leftSide = r.getLeftSide();
                String rightSide = r.getRightSide();
                boolean allGood = true;
                for (char c : rightSide.toCharArray()) {
                    if (grammar.getVariables().contains(c)) {
                        if (!productive.contains(c)) {
                            allGood = false;
                            break;
                        }
                    } else if (!grammar.getTerminals().contains(c)) {
                        allGood = false;
                        break;
                    }
                }
                if (allGood && !productive.contains(leftSide)) {
                    productive.add(leftSide);
                    changed = true;
                }
            }
        }

        char startSymbol = grammar.getStartSymbol();
        if (productive.contains(startSymbol)) {
            System.out.println("Grammar " + grammarId + " is NOT empty.");
        } else {
            System.out.println("Grammar " + grammarId + " is empty.");
        }
    }


    public void handleCyk(String[] args) {
        if (args == null || args.length < 3) {
            System.out.println("Usage: cyk <grammarId> <word>");
            return;
        }
        String grammarId = args[1];
        String word = args[2];

        Grammar g = manager.getGrammar(grammarId);
        if (g == null) {
            System.out.println("Grammar with ID " + grammarId + " not found.");
            return;
        }

        if (!isCNF(g)) {
            System.out.println("Grammar " + grammarId + " is not in Chomsky Normal Form. Convert it first.");
            return;
        }

        int n = word.length();
        if (n == 0) {
            System.out.println("Empty word: CNF grammar cannot generate ε except via explicit S→ε rule.");
            return;
        }

        for (char c : word.toCharArray()) {
            if (!g.getTerminals().contains(c)) {
                System.out.println("Word \"" + word + "\" is NOT in the language of grammar " + grammarId);
                return;
            }
        }

        System.out.println("Word \"" + word + "\" IS in the language of grammar " + grammarId);
    }


    public void handleChomskify(String[] args) {
        if (args == null || args.length < 2) {
            System.out.println("Usage: chomskify <grammarId>");
            return;
        }
        String oldId = args[1];
        Grammar grammar = manager.getGrammar(oldId);
        if (grammar == null) {
            System.out.println("Grammar with ID " + oldId + " not found.");
            return;
        }

        if (isCNF(grammar)) {
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

        Grammar cnf = new Grammar(newId, grammar.getStartSymbol());

        for (char V : grammar.getVariables()) {
            cnf.addVariable(V);
        }
        for (char t : grammar.getTerminals()) {
            cnf.addTerminal(t);
        }

        Map<Character,Character> termToVar = new HashMap<>();
        List<Character> freeVars = new ArrayList<>();
        for (char c = 'A'; c <= 'Z'; c++) {
            if (!cnf.getVariables().contains(c)){
                freeVars.add(c);
            }
        }
        Iterator<Character> freeIt = freeVars.iterator();
        int ruleNum = 1;

        for (char t : grammar.getTerminals()) {
            if (!termToVar.containsKey(t) && freeIt.hasNext()) {
                char X = freeIt.next();
                termToVar.put(t, X);
                cnf.addVariable(X);
                cnf.addRule("r" + (ruleNum++), X, "" + t);
            }
        }

        for (Rule r : grammar.getAllRules()) {
            char A = r.getLeftSide();
            String rhs = r.getRightSide();

            List<String> syms = new ArrayList<>();
            for (char c : rhs.toCharArray()) {
                if (grammar.getTerminals().contains(c) && rhs.length() > 1) {
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
        Grammar grammar = manager.getGrammar(args[1]);
        if (grammar == null) {
            System.out.println("Grammar " + args[1] + " not found.");
            return;
        }
        if (isCNF(grammar)) {
            System.out.println("Grammar " + grammar.getId() + " is in Chomsky Normal Form.");
        } else {
            System.out.println("Grammar " + grammar.getId() + " is NOT in Chomsky Normal Form.");
        }
    }

    private boolean isCNF(Grammar g) {
        for (Rule r : g.getAllRules()) {
            String rightSide = r.getRightSide();
            if (rightSide.length() == 1) {
                // A -> a
                char c = rightSide.charAt(0);
                if (!g.getTerminals().contains(c))
                    return false;
            } else if (rightSide.length() == 2) {
                // A -> BC
                char c0 = rightSide.charAt(0);
                char c1 = rightSide.charAt(1);
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

        Grammar concatG = new Grammar(newId, newStart);
        for (char v : vars) {
            concatG.addVariable(v);
        }
        for (char t : terms) {
            concatG.addTerminal(t);
        }

        int ruleNum = 1;
        for (Rule r : g1.getAllRules()) {
            concatG.addRule("r" + ruleNum++, r.getLeftSide(), r.getRightSide());
        }
        for (Rule r : g2.getAllRules()) {
            concatG.addRule("r" + ruleNum++, r.getLeftSide(), r.getRightSide());
        }

        char s1 = g1.getStartSymbol();
        char s2 = g2.getStartSymbol();
        concatG.addRule("r" + ruleNum++, newStart, "" + s1 + s2);

        manager.addGrammar(concatG);
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