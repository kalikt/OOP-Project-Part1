package code;

import java.io.*;
import java.util.*;

/**
 * Class for loading and saving {@link Grammar} instances to and from a text file.
 * Grammars are separated by a delimiter line ("====") and include definitions for
 * ID, start symbol, variables, terminals, and rules.
 */

public class FileHandler {
    private static final String GRAMMAR_SEPARATOR = "====";

    /**
     * Reads grammars from the specified file. If the file does not exist, it is created
     * and an empty map is returned. Each grammar block in the file must begin with the
     * separator line ("===="), followed by lines for Grammar ID, Start Symbol, Variables,
     * Terminals, and production rules in the format "ruleId: A -> BC".
     *
     * @param filePath the path to the file containing serialized grammars
     * @return a Map from grammar IDs to loaded {@link Grammar} instances
     * @throws IOException if an I/O error occurs reading or creating the file
     */
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


    /**
     * Writes all grammars in the provided map to the specified file. Each grammar is
     * serialized in a block prefixed by the separator ("===="), then lines for ID, start
     * symbol, variables, terminals, and each production rule’s {@code toString()} output.
     * Existing file contents will be overwritten.
     *
     * @param filePath the path to the output file
     * @param grammars a Map of grammar IDs to {@link Grammar} instances to save
     * @throws IOException if an I/O error occurs writing to the file
     */
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

    /**
     * Saves a single {@link Grammar} to the specified file.
     *
     * @param filePath the path to the output file
     * @param grammar  the {@link Grammar} instance to save
     * @throws IOException if an I/O error occurs writing to the file
     */
    public static void saveGrammarToFile(String filePath, Grammar grammar) throws IOException {
        Map<String, Grammar> single = new HashMap<>();
        single.put(grammar.getId(), grammar);
        saveGrammarsToFile(filePath, single);
    }
}