package code.extensions;

import code.Grammar;
import code.Rule;

/**
 * Class for checking whether a {@link Grammar} is in Chomsky Normal Form (CNF).
 * <p>
 * In CNF, every production rule must be one of:
 * <ul>
 *   <li>A single terminal on the right-hand side (A → a)</li>
 *   <li>Two variables on the right-hand side (A → BC)</li>
 * </ul>
 * </p>
 */
public class IsCNF {
    public static boolean isCNF(Grammar g) {
        for (Rule r : g.getAllRules()) {
            String rightSide = r.getRightSide();
            if (rightSide.length() == 1) {
                // A -> a
                char c = rightSide.charAt(0);
                if (!g.getTerminals().contains(c))
                    return false;
            } else if (rightSide.length() == 2) {
                // A -> BC
                char c0 = rightSide.charAt(0);
                char c1 = rightSide.charAt(1);
                if (!g.getVariables().contains(c0) || !g.getVariables().contains(c1))
                    return false;
            } else {
                return false;
            }
        }
        return true;
    }
}
