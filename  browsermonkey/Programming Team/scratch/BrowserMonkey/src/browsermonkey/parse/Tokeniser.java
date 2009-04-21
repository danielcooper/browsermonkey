package browsermonkey.parse;

import java.util.*;
import java.io.*;

/**
 *
 * @author Paul Calcraft
 */
public class Tokeniser {
    private List<Token> tokens;

    public Tokeniser(Reader input) {
        tokens = new ArrayList<Token>();
        // Tokenise...
    }

    public Iterator<Token> getTokens() {
        return tokens.iterator();
    }
}