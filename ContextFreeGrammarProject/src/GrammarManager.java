import java.util.HashMap;
import java.util.Map;

public class GrammarManager {
    private Map<String, Grammar> grammars;

    public GrammarManager() {
        this.grammars = new HashMap<>();
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
