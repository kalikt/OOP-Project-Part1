package code;

import code.commands.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CLI {
    private GrammarManager manager;
    private Map<String, Command> commandMap;
    private Scanner scanner =  new Scanner(System.in);

    public CLI() {
        this.manager = new GrammarManager();
        this.commandMap = new HashMap<>();
        initializeCommands();
    }

    private void initializeCommands() {
        commandMap.put("open", new OpenCommand(manager));
        commandMap.put("close", new CloseCommand(manager));
        commandMap.put("save", new SaveCommand(manager));
        commandMap.put("saveas", new SaveAsCommand(manager));
        commandMap.put("list", new ListCommand(manager));
        commandMap.put("print", new PrintCommand(manager));
        commandMap.put("addRule", new AddRuleCommand(manager));
        commandMap.put("removeRule", new RemoveRuleCommand(manager));
        commandMap.put("union", new UnionCommand(manager));
        commandMap.put("concat", new ConcatCommand(manager));
        commandMap.put("chomsky", new ChomskyCommand(manager));
        commandMap.put("cyk", new CykCommand(manager));
        commandMap.put("iter", new IterCommand(manager));
        commandMap.put("empty", new EmptyCommand(manager));
        commandMap.put("chomskify", new ChomskifyCommand(manager));
        commandMap.put("help", new HelpCommand());
        commandMap.put("exit", new ExitCommand());
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
