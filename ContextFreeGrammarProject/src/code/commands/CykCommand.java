package code.commands;

import code.Command;
import code.Grammar;
import code.GrammarManager;
import code.Rule;
import code.extensions.IsCNF;

import java.util.*;

public class CykCommand implements Command {
    private GrammarManager manager;

    public CykCommand(GrammarManager manager) {
        this.manager = manager;
    }

    @Override
    public void execute(String[] args) {
        if (args == null || args.length < 3) {
            System.out.println("Usage: cyk <grammarId> <word>");
            return;
        }
        String grammarId = args[1];
        String word = args[2];

        Grammar grammar = manager.getGrammar(grammarId);
        if (grammar == null) {
            System.out.println("Grammar with ID " + grammarId + " not found.");
            return;
        }

        if (!IsCNF.isCNF(grammar)) {
            System.out.println("Grammar " + grammarId + " is not in Chomsky Normal Form. Convert it first.");
            return;
        }

        int n = word.length();
        if (n == 0) {
            System.out.println("Empty word: CNF grammar cannot generate ε except via explicit S→ε rule.");
            return;
        }

        List<String> w = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            w.add(String.valueOf(word.charAt(i)));
        }

        Map<Integer, Map<Integer, Set<String>>> table = new HashMap<>();

        // length = 1 cases
        for (int j = 0; j < n; j++) {
            String terminal = w.get(j);
            for (Rule r : grammar.getAllRules()) {
                String lhs = String.valueOf(r.getLeftSide());
                String rhs = r.getRightSide();
                if (rhs.length() == 1 && rhs.equals(terminal)) {
                    if (!table.containsKey(j)) {
                        table.put(j, new HashMap<>());
                    }
                    Map<Integer, Set<String>> row = table.get(j);
                    if (!row.containsKey(j)) {
                        row.put(j, new HashSet<>());
                    }
                    row.get(j).add(lhs);
                }
            }
        }

        // length = 2 cases
        for (int j = 0; j < n; j++) {
            for (int i = j; i >= 0; i--) {
                for (int k = i; k < j; k++) {
                    for (Rule r : grammar.getAllRules()) {
                        String lhs = String.valueOf(r.getLeftSide());
                        String rhs = r.getRightSide();
                        if (rhs.length() == 2) {
                            String B = String.valueOf(rhs.charAt(0));
                            String C = String.valueOf(rhs.charAt(1));
                            boolean leftOk = table.containsKey(i)
                                    && table.get(i).get(k) != null
                                    && table.get(i).get(k).contains(B);
                            boolean rightOk = table.containsKey(k + 1)
                                    && table.get(k + 1).get(j) != null
                                    && table.get(k + 1).get(j).contains(C);
                            if (leftOk && rightOk) {
                                if (!table.containsKey(i)) {
                                    table.put(i, new HashMap<>());
                                }
                                Map<Integer, Set<String>> row = table.get(i);
                                if (!row.containsKey(j)) {
                                    row.put(j, new HashSet<>());
                                }
                                row.get(j).add(lhs);
                            }
                        }
                    }
                }
            }
        }

        boolean accepted = false;
        if (table.containsKey(0) && table.get(0).get(n - 1) != null
                && table.get(0).get(n - 1).contains(String.valueOf(grammar.getStartSymbol()))) {
            accepted = true;
        }
        if (accepted) {
            System.out.println("Word \"" + word + "\" IS in the language of grammar " + grammarId);
        } else {
            System.out.println("Word \"" + word + "\" is NOT in the language of grammar " + grammarId);
        }
    }
}
