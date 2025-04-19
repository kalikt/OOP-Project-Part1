import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CLI {
    private GrammarManager manager;
    private CommandManager commandHandler;
    private Map<String, Runnable> commandMap;
    private Scanner scanner =  new Scanner(System.in);

    public CLI() {
        this.manager = new GrammarManager();
        this.commandHandler = new CommandManager(manager);
        this.commandMap = new HashMap<>();
        initializeCommands();
    }

    private void initializeCommands() {
        commandMap.put("open", () -> {
            System.out.print("Enter file path: ");
            String filePath = scanner.nextLine().trim();
            String[] args = {"open", filePath};
            commandHandler.handleOpen(args);
        });
        commandMap.put("close", () -> {
            commandHandler.handleClose(null);
        });
        commandMap.put("save", () -> {
            commandHandler.handleSave(null);
        });
        //commandMap.put("saveas", commandHandler::handleSaveAs);
        //commandMap.put("list", commandHandler::handleList);
        commandMap.put("print", () -> {
            System.out.print("Enter grammar ID to print: ");
            String grammarId = scanner.nextLine().trim();
            String[] args = {"print", grammarId};
            commandHandler.handlePrint(args);
        });
        commandMap.put("addRule", () -> {
            System.out.print("Enter rule in format <grammarId> <leftSide> -> <rightSide> (e.g., G1 A -> aB): ");
            String input = scanner.nextLine().trim();
            String[] args = input.split("\\s+");
            commandHandler.handleAddRule(args);
        });
        //commandMap.put("removeRule", commandHandler::handleRemoveRule);
        //commandMap.put("union", commandHandler::handleUnion);
        //commandMap.put("concat", commandHandler::handleConcat);
        //commandMap.put("chomsky", commandHandler::handleChomsky);
        //commandMap.put("cyk", commandHandler::handleCyk);
        //commandMap.put("iter", commandHandler::handleIter);
        //commandMap.put("empty", commandHandler::handleEmpty);
        //commandMap.put("chomskify", commandHandler::handleChomskify);
        commandMap.put("help", commandHandler::handleHelp);
        commandMap.put("exit", commandHandler::handleExit);
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Type 'help' if you want to view all commands.");
        while (true) {
            System.out.print("-> ");
            String input = scanner.nextLine();
            String[] tokens = input.split(" ");
            String command = tokens[0];

            if (commandMap.containsKey(command)) {
                commandMap.get(command).run();
            } else {
                System.out.println("Invalid command. Type 'help' to view all commands.");
            }
        }
    }
}
