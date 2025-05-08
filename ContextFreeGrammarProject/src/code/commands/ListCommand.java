package code.commands;

import code.Command;
import code.Grammar;
import code.GrammarManager;

import java.util.Map;

public class ListCommand implements Command {
    private GrammarManager manager;

    public ListCommand(GrammarManager manager) {
        this.manager = manager;
    }

    @Override
    public void execute(String[] args) {
        Map<String, Grammar> grammars = manager.getGrammars();
        if (grammars.isEmpty()) {
            System.out.println("No grammars loaded.");
            return;
        }
        System.out.println("Loaded grammars:");
        for (String id : grammars.keySet()) {
            System.out.println("- " + id);
        }
    }
}
