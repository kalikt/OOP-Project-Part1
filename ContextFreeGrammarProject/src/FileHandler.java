import java.io.*;
import java.util.*;

public class FileHandler {
    private static final String GRAMMAR_SEPARATOR = "====";

    public static Map<String, Grammar> loadGrammarsFromFile(String filePath) throws IOException {
        Map<String, Grammar> grammars = new HashMap<>();
        File file = new File(filePath);

        if (!file.exists()) {
            file.createNewFile();
            return grammars;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            Grammar currentGrammar = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith(GRAMMAR_SEPARATOR)) {
                    if (currentGrammar != null) {
                        grammars.put(currentGrammar.getId(), currentGrammar);
                    }
                    currentGrammar = null;
                }
                else if (line.startsWith("Grammar ID:")) {
                    String id = line.substring("Grammar ID:".length()).trim();
                    currentGrammar = new Grammar(id, 'S');
                }
                else if (currentGrammar != null) {
                    if (line.startsWith("Start Symbol:")) {
                        char startSymbol = line.substring("Start Symbol:".length()).trim().charAt(0);
                        currentGrammar.setStartSymbol(startSymbol);
                    }
                    else if (line.startsWith("Variables:")) {
                        String[] vars = line.substring("Variables:".length()).trim().split("\\s*,\\s*");
                        for (String var : vars) {
                            if (!var.isEmpty()) {
                                currentGrammar.addVariable(var.charAt(0));
                            }
                        }
                    }
                    else if (line.startsWith("Terminals:")) {
                        String[] terms = line.substring("Terminals:".length()).trim().split("\\s*,\\s*");
                        for (String term : terms) {
                            if (!term.isEmpty()) {
                                currentGrammar.addTerminal(term.charAt(0));
                            }
                        }
                    }
                    else if (line.contains(":")) {
                        // format: "ruleId: A -> BC"
                        String[] idAndRule = line.split(":", 2);
                        if (idAndRule.length == 2) {
                            String ruleId = idAndRule[0].trim();
                            String[] ruleParts = idAndRule[1].split("->");
                            if (ruleParts.length == 2) {
                                char leftSide = ruleParts[0].trim().charAt(0);
                                String rightSide = ruleParts[1].trim();
                                currentGrammar.addRule(ruleId, leftSide, rightSide);
                            }
                        }
                    }
                }
            }

            if (currentGrammar != null) {
                grammars.put(currentGrammar.getId(), currentGrammar);
            }
        }
        return grammars;
    }

    public static void saveGrammarsToFile(String filePath, Map<String, Grammar> grammars) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Grammar grammar : grammars.values()) {
                writer.write(GRAMMAR_SEPARATOR + "\n");
                writer.write("Grammar ID: " + grammar.getId() + "\n");
                writer.write("Start Symbol: " + grammar.getStartSymbol() + "\n");

                writer.write("Variables: ");
                StringJoiner varsJoiner = new StringJoiner(", ");
                for (char var : grammar.getVariables()) {
                    varsJoiner.add(String.valueOf(var));
                }
                writer.write(varsJoiner.toString() + "\n");

                writer.write("Terminals: ");
                StringJoiner termsJoiner = new StringJoiner(", ");
                for (char term : grammar.getTerminals()) {
                    termsJoiner.add(String.valueOf(term));
                }
                writer.write(termsJoiner.toString() + "\n");

                for (Rule rule : grammar.getAllRules()) {
                    writer.write(rule + "\n");
                }
            }
        }
    }

    public static void saveGrammarToFile(String filePath, Grammar grammar) throws IOException {
        Map<String, Grammar> single = new HashMap<>();
        single.put(grammar.getId(), grammar);
        saveGrammarsToFile(filePath, single);
    }
}