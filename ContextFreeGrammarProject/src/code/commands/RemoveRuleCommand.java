package code.commands;

import code.Command;
import code.Grammar;
import code.GrammarManager;
import code.Rule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Command that removes a production rule from a specified grammar by its index.
 */
public class RemoveRuleCommand implements Command {
    private GrammarManager manager;

    public RemoveRuleCommand(GrammarManager manager) {
        this.manager = manager;
    }

    /**
     * Parses the provided arguments and attempts to remove the specified rule from the grammar.
     * <ul>
     *   <li>If fewer than 3 arguments are provided, prints usage instructions.</li>
     *   <li>Parses the rule number as a 1-based index into the sorted rule list.</li>
     *   <li>Validates that the target grammar exists; if not, prints an error.</li>
     *   <li>On success, removes the rule and prints confirmation; on failure, prints an error.</li>
     * </ul>
     *
     * @param args the command tokens, where
     *             args[1] is the grammar ID and args[2] is the rule number
     */
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
