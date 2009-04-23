package browsermonkey.document;

import java.util.*;
import java.io.*;

/**
 *
 * @author Paul Calcraft
 */
public class Tokeniser {
    private List<Token> tokens;

    /**
     * Tokenises the text from the input into a list of tokens.
     * @param input a <code>Reader</code> for the input text
     */
    public Tokeniser(Reader input) {
        tokens = new ArrayList<Token>();
        // Tokenise...
    }
    
    public Iterator<Token> getTokens() {
        return tokens.iterator();
    }
}