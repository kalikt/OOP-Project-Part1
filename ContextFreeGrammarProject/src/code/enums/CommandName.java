package code.enums;

public enum CommandName {
    OPEN("open"),
    CLOSE("close"),
    SAVE("save"),
    SAVE_AS("saveas"),
    LIST("list"),
    PRINT("print"),
    ADD_RULE("addRule"),
    REMOVE_RULE("removeRule"),
    UNION("union"),
    CONCAT("concat"),
    CHOMSKY("chomsky"),
    CYK("cyk"),
    ITER("iter"),
    EMPTY("empty"),
    CHOMSKIFY("chomskify"),
    HELP("help"),
    EXIT("exit");

    private String command;

    CommandName(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
