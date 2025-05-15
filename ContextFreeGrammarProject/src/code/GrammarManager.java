package code;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages a collection of {@link Grammar} instances, allowing grammars to be added,
 * retrieved, and manipulated, and tracks the currently opened file.
 */
public class GrammarManager {
    private Map<String, Grammar> grammars;
    private String currentFilePath;

    /**
     * Constructs a new GrammarManager with an empty set of grammars.
     */
    public GrammarManager() {
        this.grammars = new HashMap<>();
    }

    public String getCurrentFilePath() {
        return currentFilePath;
    }

    public void setCurrentFilePath(String currentFilePath) {
        this.currentFilePath = currentFilePath;
    }

    /**
     * Adds a new {@link Grammar} to this manager.
     * If a grammar with the same ID already exists, it will be replaced.
     *
     * @param grammar the Grammar instance to add
     */
    public void addGrammar(Grammar grammar) {
        grammars.put(grammar.getId(), grammar);
    }

    /**
     * Adds a production rule to the specified grammar.
     *
     * @param grammarId the ID of the grammar to which the rule should be added
     * @param ruleId unique identifier for the new rule
     * @param leftSide the variable on the left side of the rule
     * @param rightSide the sequence of symbols on the right side of the rule
     * @throws IllegalArgumentException if no grammar with the given ID exists, or if the rule parameters are invalid
     */
    public void addRule(String grammarId, String ruleId, char leftSide, String rightSide) {
        Grammar grammar = grammars.get(grammarId);
        if (grammar == null) {
            throw new IllegalArgumentException("Grammar with ID " + grammarId + " not found");
        }
        grammar.addRule(ruleId, leftSide, rightSide);
    }

    /**
     * Removes a production rule from the specified grammar.
     *
     * @param grammarId the ID of the grammar from which to remove the rule
     * @param ruleId the identifier of the rule to remove
     * @throws IllegalArgumentException if no grammar with the given ID exists, or if the rule ID is not found in that grammar
     */
    public void removeRule(String grammarId, String ruleId) {
        Grammar grammar = grammars.get(grammarId);
        if (grammar == null) {
            throw new IllegalArgumentException("Grammar with ID " + grammarId + " not found");
        }
        grammar.removeRule(ruleId);
    }

    /**
     * Checks whether this manager currently has any grammars.
     *
     * @return {@code true} if no grammars have been added, {@code false} otherwise
     */
    public boolean isEmpty() {
        return grammars.isEmpty();
    }

    /**
     * Removes all grammars from this manager.
     */
    public void clearGrammars() {
        grammars.clear();
    }

    /**
     * Returns the map of all grammars.
     *
     * @return a Map from all grammars
     */
    public Map<String, Grammar> getGrammars() {
        return grammars;
    }

    /**
     * Retrieves a grammar by its ID.
     *
     * @param id the ID of the grammar to retrieve
     * @return the Grammar instance, or {@code null} if no grammar with that ID exists
     */
    public Grammar getGrammar(String id) {
        return grammars.get(id);
    }
}
