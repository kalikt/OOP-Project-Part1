package code.commands;

import code.Command;
import code.Grammar;
import code.GrammarManager;
import code.Rule;

import java.util.HashSet;
import java.util.Set;

public class EmptyCommand implements Command {
    private GrammarManager manager;

    public EmptyCommand(GrammarManager manager) {
        this.manager = manager;
    }

    @Override
    public void execute(String[] args) {
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
}
