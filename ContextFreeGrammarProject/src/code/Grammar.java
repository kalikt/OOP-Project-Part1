package code;

import java.util.*;

/**
 * Represents a context-free grammar with variables, terminals, and rules.
 */
public class Grammar {
    private String id;
    private Set<Character> variables;
    private Set<Character> terminals;
    private Map<String, Rule> rules;
    private char startSymbol;

    /**
     * Creates a new Grammar.
     *
     * @param id unique identifier for this grammar
     * @param startSymbol  the start variable of the grammar
     */
    public Grammar(String id, char startSymbol) {
        this.id = id;
        this.startSymbol = startSymbol;
        this.variables = new HashSet<>();
        this.terminals = new HashSet<>();
        this.rules = new HashMap<>();
        this.variables.add(startSymbol);
    }

    public String getId() {
        return id;
    }

    public Set<Character> getVariables() {
        return variables;
    }


    public Set<Character> getTerminals() {
        return terminals;
    }

    public char getStartSymbol() {
        return startSymbol;
    }


    public void setStartSymbol(char startSymbol) {
        this.startSymbol = startSymbol;
        this.variables.add(startSymbol);
    }

    /**
     * Adds a new variable (non-terminal) to this grammar.
     *
     * @param variable the variable character to add
     */
    public void addVariable(char variable) {
        variables.add(variable);
    }

    /**
     * Adds a new terminal symbol to this grammar.
     *
     * @param terminal the terminal character to add
     */
    public void addTerminal(char terminal) {
        terminals.add(terminal);
    }

    /**
     * Returns all rules defined in this grammar.
     *
     * @return a Collection of Rule objects
     */
    public Collection<Rule> getAllRules() {
        return rules.values();
    }

    /**
     * Adds a new production rule to this grammar.
     *
     * @param ruleId unique identifier for the rule
     * @param leftSide the variable on the left side of the rule
     * @param rightSide the sequence of variables and/or terminals on the right side
     * @throws IllegalArgumentException if the ruleId already exists,
     *                                  if leftSide is not a defined variable,
     *                                  or if rightSide contains undefined symbols
     */
    public void addRule(String ruleId, char leftSide, String rightSide) {
        if (rules.containsKey(ruleId)) {
            throw new IllegalArgumentException("Rule with ID " + ruleId + " already exists");
        }

        if (!variables.contains(leftSide)) {
            throw new IllegalArgumentException("Left side '" + leftSide + "' is not a defined variable");
        }

        for (char c : rightSide.toCharArray()) {
            if (!variables.contains(c) && !terminals.contains(c)) {
                throw new IllegalArgumentException("Symbol '" + c + "' is not defined");
            }
        }
        rules.put(ruleId, new Rule(ruleId, leftSide, rightSide));
    }

    /**
     * Removes an existing rule from this grammar.
     *
     * @param ruleId the identifier of the rule to remove
     * @throws IllegalArgumentException if no rule with the given ID exists
     */
    public void removeRule(String ruleId) {
        if (!rules.containsKey(ruleId)) {
            throw new IllegalArgumentException("Rule with ID " + ruleId + " not found");
        }
        rules.remove(ruleId);
    }
}
