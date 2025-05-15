package code.commands;

import code.Command;
import code.FileHandler;
import code.GrammarManager;

import java.io.IOException;

/**
 * Command that saves all currently loaded grammars to a new file path.
 */
public class SaveAsCommand implements Command {
    private GrammarManager manager;

    public SaveAsCommand(GrammarManager manager) {
        this.manager = manager;
    }

    /**
     * Executes the "saveas" command.
     * <p>
     * Parses {@code args} to build a new file path from {@code args[1]}.
     * If no grammars are loaded, prints "No file is currently open."
     * If the user writes invalid file path, prints usage instructions.
     * Otherwise, attempts to save all grammars to the new file via {@link FileHandler}.
     * </p>
     *
     * @param args the command tokens, where args[0] is "saveas" and
     *              args[1] is the path of the file.
     */
    @Override
    public void execute(String[] args) {
        String currentFilePath = manager.getCurrentFilePath();

        if (manager.getGrammars().isEmpty()) {
            System.out.println("No file is currently open.");
            return;
        }
        if (args == null || args.length < 2) {
            System.out.println("Usage: saveas <file>");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String newFilePath = sb.toString().trim();

        try {
            FileHandler.saveGrammarsToFile(newFilePath, manager.getGrammars());
            currentFilePath = newFilePath;
            System.out.println("Successfully saved as " + newFilePath);
        } catch (IOException e) {
            System.out.println("Error saving file as: " + e.getMessage());
        }
    }
}
