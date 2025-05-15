package code.commands;

import code.Command;
import code.Grammar;
import code.GrammarManager;
import code.Rule;
import code.extensions.IsCNF;

import java.util.*;

/**
 * Command that transforms a grammar into Chomsky Normal Form (CNF) by creating
 * a new grammar with a unique ID.
 * <p>
 * If the specified grammar is already in CNF, no new grammar is created.
 * Otherwise, a new grammar is created.
 * </p>
 * <p>
 * The algorithm:
 * <ol>
 *   <li>Copy all variables and terminals from the original grammar.</li>
 *   <li>Create new variables for each terminal which is in a rule with more than one terminal,
 *       mapping each to a new rule of the form V → t.</li>
 *   <li>For each original rule:
 *     <ul>
 *       <li>If it is already of length 1 or 2, add it (substituting terminals as needed).</li>
 *       <li>If longer, iteratively break it into binary productions by introducing fresh variables.</li>
 *     </ul>
 *   </li>
 *   <li>Add the resulting CNF grammar to the manager and print its new ID.</li>
 * </ol>
 * </p>
 */
public class ChomskifyCommand implements Command {
    private GrammarManager manager;

    public ChomskifyCommand(GrammarManager manager) {
        this.manager = manager;
    }

    /**
     * Executes the "chomskify" command.
     * <p>
     * Parses {@code args} to find the grammar ID ( {@code args[1]} ).
     * If the grammar does not exist, prints an error. If it is already in CNF,
     * notifies the user. Otherwise, constructs a new grammar in CNF form,
     * assigns it a new ID, and adds it to the manager.
     * </p>
     *
     * @param args the command tokens, where args[0] is "chomskify" and
     *             args[1] is the ID of the grammar to convert
     */
    @Override
    public void execute(String[] args) {
        if (args == null || args.length < 2) {
            System.out.println("Usage: chomskify <grammarId>");
            return;
        }
        String oldId = args[1];
        Grammar grammar = manager.getGrammar(oldId);
        if (grammar == null) {
            System.out.println("Grammar with ID " + oldId + " not found.");
            return;
        }

        if (IsCNF.isCNF(grammar)) {
            System.out.println("Grammar " + oldId + " is already in Chomsky Normal Form.");
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

        Grammar cnf = new Grammar(newId, grammar.getStartSymbol());

        for (char V : grammar.getVariables()) {
            cnf.addVariable(V);
        }
        for (char t : grammar.getTerminals()) {
            cnf.addTerminal(t);
        }

        Map<Character,Character> termToVar = new HashMap<>();
        List<Character> availableVariables  = new ArrayList<>();
        for (char c = 'A'; c <= 'Z'; c++) {
            if (!cnf.getVariables().contains(c)){
                availableVariables .add(c);
            }
        }
        Iterator<Character> freeIt = availableVariables .iterator();

        int ruleNum = 1;

        for (char t : grammar.getTerminals()) {
            if (!termToVar.containsKey(t) && freeIt.hasNext()) {
                char newVar  = freeIt.next();
                termToVar.put(t, newVar);
                cnf.addVariable(newVar);
                cnf.addRule("r" + (ruleNum++), newVar, "" + t);
            }
        }

        for (Rule r : grammar.getAllRules()) {
            char leftVar = r.getLeftSide();
            String rightSide  = r.getRightSide();

            List<String> symbols = new ArrayList<>();
            for (char c : rightSide.toCharArray()) {
                if (grammar.getTerminals().contains(c) && rightSide.length() > 1) {
                    symbols.add(termToVar.get(c).toString());
                } else {
                    symbols.add(String.valueOf(c));
                }
            }

            if (symbols.size() == 1) {
                cnf.addRule("r" + (ruleNum++), leftVar , symbols.get(0));
            }
            else if (symbols.size() == 2) {
                cnf.addRule("r" + (ruleNum++), leftVar , symbols.get(0) + symbols.get(1));
            }
            else {
                String firstSymbol = symbols.get(0);
                String freeVar = freeIt.hasNext() ? freeIt.next().toString() : null;
                if (freeVar == null) {
                    System.out.println("Out of variable names during chomskify");
                    return;
                }
                cnf.addVariable(freeVar.charAt(0));
                cnf.addRule("r" + (ruleNum++), leftVar , firstSymbol + freeVar);

                for (int i = 1; i < symbols.size() - 2; i++) {
                    String current = symbols.get(i);
                    String nextVar = freeIt.hasNext() ? freeIt.next().toString() : null;
                    if (nextVar == null) {
                        System.out.println("Out of variable names during chomskify");
                        return;
                    }
                    cnf.addVariable(nextVar.charAt(0));
                    cnf.addRule("r" + (ruleNum++), freeVar.charAt(0), current + nextVar);
                    freeVar = nextVar;
                }
                String secondLast = symbols.get(symbols.size() - 2);
                String lastSymbol = symbols.get(symbols.size() - 1);
                cnf.addRule("r" + (ruleNum++), freeVar.charAt(0), secondLast  + lastSymbol);
            }
        }
        manager.addGrammar(cnf);
        System.out.println("Created grammar " + newId);
    }
}
