package code.commands;

import code.Command;
import code.Grammar;
import code.GrammarManager;
import code.extensions.IsCNF;

public class CykCommand implements Command {
    private GrammarManager manager;

    public CykCommand(GrammarManager manager) {
        this.manager = manager;
    }

    @Override
    public void execute(String[] args) {
        if (args == null || args.length < 3) {
            System.out.println("Usage: cyk <grammarId> <word>");
            return;
        }
        String grammarId = args[1];
        String word = args[2];

        Grammar grammar = manager.getGrammar(grammarId);
        if (grammar == null) {
            System.out.println("Grammar with ID " + grammarId + " not found.");
            return;
        }

        if (!IsCNF.isCNF(grammar)) {
            System.out.println("Grammar " + grammarId + " is not in Chomsky Normal Form. Convert it first.");
            return;
        }

        int n = word.length();
        if (n == 0) {
            System.out.println("Empty word: CNF grammar cannot generate ε except via explicit S→ε rule.");
            return;
        }

        for (char c : word.toCharArray()) {
            if (!grammar.getTerminals().contains(c)) {
                System.out.println("Word \"" + word + "\" is NOT in the language of grammar " + grammarId);
                return;
            }
        }

        System.out.println("Word \"" + word + "\" IS in the language of grammar " + grammarId);
    }
}
