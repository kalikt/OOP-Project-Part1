public class Rule {
    private String id;
    private char leftSide;
    private String rightSide;

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

    @Override
    public String toString() {
        return id + ": " + leftSide + " -> " + rightSide;
    }
}
