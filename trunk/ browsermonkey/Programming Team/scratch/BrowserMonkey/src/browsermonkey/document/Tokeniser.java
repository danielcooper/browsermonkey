package browsermonkey.document;

import java.util.*;

/**
 *
 * @author Paul Calcraft
 */
public class Tokeniser {

    private List<Token> tokens;
    private String page;
    private int currentPos;

    /**
     * Tokenises the text from the input into a list of tokens.
     * @param input a <code>Reader</code> for the input text
     */
    public Tokeniser(String input) {
        tokens = new ArrayList<Token>();
        page = input;
        currentPos = 0;
    // Tokenise...
    }

    public void tokenise() {
        while (currentPos < page.length()) {
            getNextToken();
        }
    }

    public void getNextToken() {
        if (page.charAt(currentPos) == '<') {
            if (page.substring(currentPos + 1, currentPos + 4).equals("!--")) {
                //calculate length of token and move token
                int nextTagOpen = page.indexOf('<', currentPos + 1);
                int tagTokenEnd = page.indexOf("-->", currentPos + 4);

                if(nextTagOpen !=-1 && tagTokenEnd > nextTagOpen){
                    currentPos = nextTagOpen;
                } else {
                    currentPos = tagTokenEnd + 3;
                }
            } else {
                int nextTagOpen = page.indexOf('<', currentPos + 1);
                int tagTokenEnd = page.indexOf('>', currentPos + 1);
                //Malformed html shiz
                String fullTag;
                if (nextTagOpen!= -1 && tagTokenEnd > nextTagOpen) {
                    fullTag = page.substring(currentPos, nextTagOpen) + ">";
                    tagTokenEnd = nextTagOpen;
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