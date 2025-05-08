package code.commands;

import code.Command;
import code.Grammar;
import code.GrammarManager;
import code.Rule;

import java.util.HashSet;
import java.util.Set;

public class ConcatCommand implements Command {
    private GrammarManager manager;

    public ConcatCommand(GrammarManager manager) {
        this.manager = manager;
    }

    @Override
    public void execute(String[] args) {
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
}
