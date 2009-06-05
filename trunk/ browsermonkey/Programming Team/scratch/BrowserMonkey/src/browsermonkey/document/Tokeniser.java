package browsermonkey.document;

import browsermonkey.utility.BrowserMonkeyLogger;
import java.util.*;

/**
 * Tokeniser class takes the raw text input and produces a useful tree of Tokens
 * for the Parser to use. It does this mainly by looking for <> characters.
 * @author Paul Calcraft, Daniel Cooper, Lawrence Dine
 */
public class Tokeniser {
    private List<Token> tokens;
    private String page;
    private int currentPos;
    private boolean conformant;

    /**
     * Returns true if the tokenisation didn't have to compensate for any
     * conformance errors.
     * @return True if conformant
     */
    public boolean isConformant() {
        return conformant;
    }
    
    private void conformanceError(String error){
        BrowserMonkeyLogger.conformance(error);
        conformant = false;
    }

    /**
     * Tokenises the text from the input into a list of tokens.
     * @param input a <code>Reader</code> for the input text
     */
    public Tokeniser(String input) {
        tokens = new ArrayList<Token>();
        page = input;
        currentPos = 0;
        conformant = true;
    }

    /**
     * Tokenise method is used to call the method that does the actual tokenisation
     * over and over again until the tokenising is complete.
     */
    public void tokenise() {
        while (currentPos < page.length()) {
            getNextToken();
        }
    }

    /**
     * This method does all the clever work for tokenising the text. See source
     * for explanatory comments.
     */
    public void getNextToken() {
        if (page.charAt(currentPos) == '<') {   //TODO Detailed in-line comments
            if (page.substring(currentPos + 1, currentPos + 4).equals("!--")) {
                int tagTokenEnd = page.indexOf("-->", currentPos + 4);

                if(tagTokenEnd == -1){
                    currentPos = page.length();
                    conformanceError("Comment tag does not end, treating rest of the document as a comment.");
                } else {
                    currentPos = tagTokenEnd + 3;
                }
            } else {
                int nextTagOpen = page.indexOf('<', currentPos + 1);
                int tagTokenEnd = page.indexOf('>', currentPos + 1);

                if (nextTagOpen == -1) {
                    nextTagOpen = page.length();
                }

                String fullTag;
                if (tagTokenEnd == -1 || tagTokenEnd > nextTagOpen) {
                    fullTag = page.substring(currentPos, nextTagOpen) + ">";
                    tagTokenEnd = nextTagOpen;
                    if (nextTagOpen == page.length())
                        conformanceError("Tag does not end with '>' throughout the document, closing at end of document: "+fullTag);
                    else
                        conformanceError("Tag does not close with '>' before another is opened with '<', forcing close: "+fullTag);
                } else {
                    fullTag = page.substring(currentPos, tagTokenEnd + 1);
                    tagTokenEnd++;
                }

                Token token = new Token(fullTag, TokenType.TAG);
                tokens.add(token);
                currentPos = tagTokenEnd;

                if (token.getTag().equals("title")) {
                    int endTitle = page.indexOf("</title>", tagTokenEnd);
                    String text;
                    if (endTitle != -1) {
                        text = page.substring(currentPos, endTitle);
                        currentPos = endTitle + 8;
                    } else {
                        conformanceError("Title tag does not end, treating rest of document as title.");
                        text = page.substring(currentPos, page.length());
                        currentPos = page.length();
                    }
                    tokens.add(new Token(text, TokenType.TEXT));
                    tokens.add(new Token("</title>", TokenType.TAG));
                }
            }
        } else {
            int textTokenEnd = page.indexOf('<', currentPos);
            String text;
            if (textTokenEnd != -1) {
                text = page.substring(currentPos, textTokenEnd);
            } else {
                text = page.substring(currentPos, page.length());
            }
            currentPos = currentPos + text.length();
            tokens.add(new Token(text, TokenType.TEXT));
        }
    }

    /**
     * After the tokenisation is complete this is used to get an iterator
     * containing the tokens. This method is used by the <code>Parser</code>.
     * @return Iterator of Tokens
     */
    public Iterator<Token> getTokens() {
        return tokens.iterator();
    }
}