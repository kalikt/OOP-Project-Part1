import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CLI {
    private GrammarManager manager;
    private CommandManager commandHandler;
    private Map<String, Command> commandMap;
    private Scanner scanner =  new Scanner(System.in);

    public CLI() {
        this.manager = new GrammarManager();
        this.commandHandler = new CommandManager(manager);
        this.commandMap = new HashMap<>();
        initializeCommands();
    }

    private void initializeCommands() {
        commandMap.put("open", commandHandler::handleOpen);
        commandMap.put("close", commandHandler::handleClose);
        commandMap.put("save", commandHandler::handleSave);
        commandMap.put("saveas", commandHandler::handleSaveAs);
        commandMap.put("list", commandHandler::handleList);
        commandMap.put("print", commandHandler::handlePrint);
        commandMap.put("addRule", commandHandler::handleAddRule);
        commandMap.put("removeRule", commandHandler::handleRemoveRule);
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
        System.out.println("Type 'help' to view all commands.");
        while (true) {
            System.out.print("-> ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] tokens = line.split("\\s+");
            String cmd = tokens[0];

            if (commandMap.containsKey(cmd)) {
                commandMap.get(cmd).execute(tokens);
            } else {
                System.out.println("Invalid command. Type 'help' to view all commands.");
            }
        }
    }
}
