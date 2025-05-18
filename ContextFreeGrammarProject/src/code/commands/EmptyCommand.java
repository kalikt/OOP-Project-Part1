package code.commands;

import code.Command;
import code.Grammar;
import code.GrammarManager;

/**
 * Command that determines whether a given grammar’s language is empty
 * (i.e., the grammar cannot derive any terminal string).
 */
public class EmptyCommand implements Command {
    private GrammarManager manager;

    public EmptyCommand(GrammarManager manager) {
        this.manager = manager;
    }

    /**
     * Executes the "empty" command.
     * <p>
     * Checks if either of the terminals or the variables of the language
     * of the grammar are empty. That means the language of the grammar is
     * empty
     * </p>
     *
     * @param args the command tokens, where args[0] is "empty" and
     *      *          args[1] is the ID of the grammar
     */
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

        if(grammar.getVariables().isEmpty() || grammar.getTerminals().isEmpty()){
            System.out.println("Grammar " + grammarId + " is empty.");
        }else{
            System.out.println("Grammar " + grammarId + " is NOT empty.");
        }
    }
}
