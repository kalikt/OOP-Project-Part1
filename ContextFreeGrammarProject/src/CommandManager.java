public class CommandManager {
    private GrammarManager manager;

    public CommandManager(GrammarManager manager) {
        this.manager = manager;
    }

    public void handleHelp() {
        System.out.println("The following commands are supported:");
        System.out.println("open <file> - Opens a file");
        System.out.println("close - Closes the currently opened file");
        System.out.println("save - Saves the currently open file");
        System.out.println("saveas <file> - Saves the currently open file with a new name");
        System.out.println("list - Lists all grammars");
        System.out.println("print <id> - Prints a grammar");
        System.out.println("addRule <id> <rule> - Adds a rule to a grammar");
        System.out.println("removeRule <id> <n> - Removes a rule from a grammar");
        System.out.println("union <id1> <id2> - Performs union of two grammars");
        System.out.println("concat <id1> <id2> - Performs concatenation of two grammars");
        System.out.println("chomsky <id> - Checks if a grammar is in Chomsky normal form");
        System.out.println("cyk <id> - Checks if a word is in the language of a grammar (CYK algorithm)");
        System.out.println("iter <id> - Performs Kleene star operation on a grammar");
        System.out.println("empty <id> - Checks if a grammar's language is empty");
        System.out.println("chomskify <id> - Converts a grammar to Chomsky normal form");
        System.out.println("help - Prints this information");
        System.out.println("exit - Exits the program");
    }

    public void handleExit() {
        System.out.println("Exiting program...");
        System.exit(0);
    }
}
