package code;

import java.util.HashMap;
import java.util.Map;

public class GrammarManager {
    private Map<String, Grammar> grammars;
    private String currentFilePath;

    public GrammarManager() {
        this.grammars = new HashMap<>();
    }

    public String getCurrentFilePath() {
        return currentFilePath;
    }

    public void setCurrentFilePath(String currentFilePath) {
        this.currentFilePath = currentFilePath;
    }

    public void addGrammar(Grammar grammar) {
        grammars.put(grammar.getId(), grammar);
    }

    public void addRule(String grammarId, String ruleId, char leftSide, String rightSide) {
        Grammar grammar = grammars.get(grammarId);
        if (grammar == null) {
            throw new IllegalArgumentException("Grammar with ID " + grammarId + " not found");
        }
        grammar.addRule(ruleId, leftSide, rightSide);
    }

    public void removeRule(String grammarId, String ruleId) {
        Grammar grammar = grammars.get(grammarId);
        if (grammar == null) {
            throw new IllegalArgumentException("Grammar with ID " + grammarId + " not found");
        }
        grammar.removeRule(ruleId);
    }

    public boolean isEmpty() {
        return grammars.isEmpty();
    }

    public void clearGrammars() {
        grammars.clear();
    }

    public Map<String, Grammar> getGrammars() {
        return grammars;
    }

    public Grammar getGrammar(String id) {
        return grammars.get(id);
    }
}
