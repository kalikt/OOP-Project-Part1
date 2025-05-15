package code.enums;

/**
 * Enumeration of all supported commands, each mapping to
 * the string used to invoke the corresponding command.
 */
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

    /**
     * Constructs a CommandName enum constant.
     *
     * @param command the string used to invoke this command in the CLI
     */
    CommandName(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
