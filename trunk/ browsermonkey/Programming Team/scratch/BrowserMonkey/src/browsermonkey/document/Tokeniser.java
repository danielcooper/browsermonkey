package browsermonkey.document;

import browsermonkey.utility.BrowserMonkeyLogger;
import java.util.*;

/**
 *
 * @author Paul Calcraft
 */
public class Tokeniser {
    private List<Token> tokens;
    private String page;
    private int currentPos;
    private boolean conformant;

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

    public void tokenise() {
        while (currentPos < page.length()) {
            getNextToken();
        }
    }

    public void getNextToken() {
        if (page.charAt(currentPos) == '<') {
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

    public Iterator<Token> getTokens() {
        return tokens.iterator();
    }
}