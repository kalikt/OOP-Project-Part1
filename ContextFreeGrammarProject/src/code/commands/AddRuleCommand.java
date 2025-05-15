package code.commands;

import code.Command;
import code.Grammar;
import code.GrammarManager;

/**
 * Command that adds a new production rule to a specified grammar.
 */
public class AddRuleCommand implements Command {
    private GrammarManager manager;

    public AddRuleCommand(GrammarManager manager) {
        this.manager = manager;
    }

    /**
     * Parses the provided arguments and attempts to add a new rule to the specified grammar.
     * <ul>
     *   <li>If fewer than 4 arguments are provided, prints usage instructions.</li>
     *   <li>Splits the rule definition on "->" to obtain left and right sides.</li>
     *   <li>Validates that the target grammar exists; if not, prints an error.</li>
     *   <li>Generates a rule ID ("rN") based on existing rules and adds the rule.</li>
     *   <li>On success, prints confirmation; on failure, prints the exception message.</li>
     * </ul>
     *
     * @param args the command tokens, where
     *             args[1] is the grammar ID and the remainder form "<leftSide> -> <rightSide>"
     */
    @Override
    public void execute(String[] args) {
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
}
