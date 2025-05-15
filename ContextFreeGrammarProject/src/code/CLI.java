package code;

import code.commands.*;
import code.enums.CommandName;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Command line interface for interacting with the {@link GrammarManager}.
 * Initializes available commands and processes user input in a loop until the user exits the program.
 */
public class CLI {
    private GrammarManager manager;
    private Map<String, Command> commandMap;
    private Scanner scanner =  new Scanner(System.in);

    public CLI() {
        this.manager = new GrammarManager();
        this.commandMap = new HashMap<>();
        initializeCommands();
    }

    /**
     * Maps command names (from {@link CommandName})
     * to their {@link Command} implementations.
     */
    private void initializeCommands() {
        commandMap.put(CommandName.OPEN.getCommand(), new OpenCommand(manager));
        commandMap.put(CommandName.CLOSE.getCommand(), new CloseCommand(manager));
        commandMap.put(CommandName.SAVE.getCommand(), new SaveCommand(manager));
        commandMap.put(CommandName.SAVE_AS.getCommand(), new SaveAsCommand(manager));
        commandMap.put(CommandName.LIST.getCommand(), new ListCommand(manager));
        commandMap.put(CommandName.PRINT.getCommand(), new PrintCommand(manager));
        commandMap.put(CommandName.ADD_RULE.getCommand(), new AddRuleCommand(manager));
        commandMap.put(CommandName.REMOVE_RULE.getCommand(), new RemoveRuleCommand(manager));
        commandMap.put(CommandName.UNION.getCommand(), new UnionCommand(manager));
        commandMap.put(CommandName.CONCAT.getCommand(), new ConcatCommand(manager));
        commandMap.put(CommandName.CHOMSKY.getCommand(), new ChomskyCommand(manager));
        commandMap.put(CommandName.CYK.getCommand(), new CykCommand(manager));
        commandMap.put(CommandName.ITER.getCommand(), new IterCommand(manager));
        commandMap.put(CommandName.EMPTY.getCommand(), new EmptyCommand(manager));
        commandMap.put(CommandName.CHOMSKIFY.getCommand(), new ChomskifyCommand(manager));
        commandMap.put(CommandName.HELP.getCommand(), new HelpCommand());
        commandMap.put(CommandName.EXIT.getCommand(), new ExitCommand());
    }

    /**
     * Starts the command loop.
     * Displays a prompt, reads user input, and executes the appropriate command.
     * Continues until the {@code exit} command is executed.
     */
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
