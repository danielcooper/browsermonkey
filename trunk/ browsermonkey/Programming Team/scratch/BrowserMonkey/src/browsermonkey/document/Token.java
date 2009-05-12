package browsermonkey.document;

import java.util.Map;

/**
 *
 * @author Lawrence Dine
 */
public class Token {
    String tag;
    boolean endTag;
    String fullTag;
    Map attributes;
    TokenType type;

    public String tag() {
        return tag;
    }

    public Token(String fullTag, TokenType type){
        this.fullTag = fullTag;
        this.type = type;

    }

    public boolean isEndTag(){
        return endTag;
    }

    public boolean hasAttributes(){
        return (attributes.size() > 0);
    }

    public String getFullTag(){
        return fullTag;
    }

    public TokenType getType(){
        return type;
    }

    public String getTag(){
        return tag;
    }

    public void classifyTag(){

    }
}