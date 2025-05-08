package code.commands;

import code.Command;
import code.Grammar;
import code.GrammarManager;
import code.Rule;
import code.extensions.IsCNF;

import java.util.*;

public class ChomskifyCommand implements Command {
    private GrammarManager manager;

    public ChomskifyCommand(GrammarManager manager) {
        this.manager = manager;
    }

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
