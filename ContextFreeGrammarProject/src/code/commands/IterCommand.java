package code.commands;

import code.Command;
import code.Grammar;
import code.GrammarManager;
import code.Rule;

import java.util.HashSet;
import java.util.Set;

public class IterCommand implements Command {
    private GrammarManager manager;

    public IterCommand(GrammarManager manager) {
        this.manager = manager;
    }

    @Override
    public void execute(String[] args) {
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
}

