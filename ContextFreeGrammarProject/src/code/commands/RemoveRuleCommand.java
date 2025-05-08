package code.commands;

import code.Command;
import code.Grammar;
import code.GrammarManager;
import code.Rule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RemoveRuleCommand implements Command {
    private GrammarManager manager;

    public RemoveRuleCommand(GrammarManager manager) {
        this.manager = manager;
    }

    @Override
    public void execute(String[] args) {
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
}
