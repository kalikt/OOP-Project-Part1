package code.commands;

import code.Command;
import code.Grammar;
import code.GrammarManager;
import code.Rule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Command that displays the full details of a specified grammar.
 */
public class PrintCommand implements Command {
    private GrammarManager manager;

    public PrintCommand(GrammarManager manager) {
        this.manager = manager;
    }

    /**
     * Executes the "print" command.
     * <p>
     * Parses {@code args} to obtain the grammar ID ( {@code args[1]} ).
     * If the grammar does not exist, prints an error message.
     * Otherwise, prints the grammar’s:
     * <ul>
     *   <li>ID</li>
     *   <li>Start symbol</li>
     *   <li>Variables</li>
     *   <li>Terminals</li>
     *   <li>Rules</li>
     * </ul>
     * </p>
     *
     * @param args the command tokens, where args[0] is "print" and
     *              args[1] is the id of the grammar.
     */
    @Override
    public void execute(String[] args) {
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
}
