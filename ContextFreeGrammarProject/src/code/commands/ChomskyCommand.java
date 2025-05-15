package code.commands;

import code.Command;
import code.Grammar;
import code.GrammarManager;
import code.extensions.IsCNF;

/**
 * Command that checks whether a specified grammar is in Chomsky Normal Form (CNF).
 */
public class ChomskyCommand implements Command {
    private GrammarManager manager;

    public ChomskyCommand(GrammarManager manager) {
        this.manager = manager;
    }

    /**
     * Executes the "chomsky" command, which checks whether the specified grammar
     * is in Chomsky Normal Form and prints the result.
     * @param args the command arguments, where args[1] is the ID of the grammar to check
     */
    @Override
    public void execute(String[] args) {
        if (args == null || args.length < 2) {
            System.out.println("Usage: chomsky <grammarId>");
            return;
        }
        Grammar grammar = manager.getGrammar(args[1]);
        if (grammar == null) {
            System.out.println("Grammar " + args[1] + " not found.");
            return;
        }
        if (IsCNF.isCNF(grammar)) {
            System.out.println("Grammar " + grammar.getId() + " is in Chomsky Normal Form.");
        } else {
            System.out.println("Grammar " + grammar.getId() + " is NOT in Chomsky Normal Form.");
        }
    }
}
