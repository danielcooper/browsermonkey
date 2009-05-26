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

    public void tokenise(){
        while(currentPos < page.length()){
            getNextToken();
        }
    }

    public void getNextToken(){
        if(page.charAt(currentPos) == '<'){
            if(page.substring(currentPos+1, currentPos+3).equals("!--")){
                //calculate length of token and move token
                int tagTokenEnd = page.indexOf("-->", currentPos+1);
                //TODO Malformed html shiz
                currentPos = currentPos + (tagTokenEnd - currentPos);
            } else {
                int tagTokenEnd = page.indexOf('>', currentPos + 1);
                //Malformed html shiz
                String tag = page.substring(currentPos, tagTokenEnd);
                tokens.add(new Token(tag, TokenType.valueOf("Tag")));
                currentPos = currentPos + tag.length();
            }
        } else {
            int textTokenEnd = page.indexOf('<', currentPos);
            String text = new String();
            if(textTokenEnd != 0){
                text = page.substring(currentPos, textTokenEnd);
            } else {
                text = page.substring(currentPos, page.length());
            }
            tokens.add(new Token(text, TokenType.valueOf("Text")));
            if(text.equals("")){
                currentPos = page.length();
            } else {
                currentPos = currentPos + text.length();
            }
        }
    }
    
    public Iterator<Token> getTokens() {
        return tokens.iterator();
    }
}