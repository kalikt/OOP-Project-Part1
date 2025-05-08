package code.commands;

import code.Command;
import code.Grammar;
import code.GrammarManager;
import code.Rule;

import java.util.HashSet;
import java.util.Set;

public class UnionCommand implements Command {
    private GrammarManager manager;

    public UnionCommand(GrammarManager manager) {
        this.manager = manager;
    }

    @Override
    public void execute(String[] args) {
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
}
