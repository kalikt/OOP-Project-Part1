import java.util.*;

public class Grammar {
    private String id;
    private Set<Character> variables;
    private Set<Character> terminals;
    private Map<String, Rule> rules;
    private char startSymbol;

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

    public void addVariable(char variable) {
        variables.add(variable);
    }

    public void addTerminal(char terminal) {
        terminals.add(terminal);
    }

    public Collection<Rule> getAllRules() {
        return rules.values();
    }

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

    public void removeRule(String ruleId) {
        if (!rules.containsKey(ruleId)) {
            throw new IllegalArgumentException("Rule with ID " + ruleId + " not found");
        }
        rules.remove(ruleId);
    }
}
