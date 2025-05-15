package code;

/**
 * Represents the rule of the grammar.
 */
public class Rule {
    private String id;
    private char leftSide;
    private String rightSide;

    /**
     * Constructs a new Rule with the given identifier, left side variable,
     * and right side production.
     *
     * @param id unique identifier for this rule
     * @param leftSide the variable on the left side of the rule
     * @param rightSide the sequence of variables and/or terminals on the right side
     */
    public Rule(String id, char leftSide, String rightSide) {
        this.id = id;
        this.leftSide = leftSide;
        this.rightSide = rightSide;
    }

    public String getId() {
        return id;
    }

    public char getLeftSide() {
        return leftSide;
    }

    public String getRightSide() {
        return rightSide;
    }

    /**
     * Returns the rule in the following format:
     * "id: leftSide -> rightSide".
     *
     * @return formatted string representing the rule
     */
    @Override
    public String toString() {
        return id + ": " + leftSide + " -> " + rightSide;
    }
}
