package browsermonkey.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        attributes = new HashMap();

        endTag = false;

        if(){ //If type is text
            tag = fullTag;
        } else if(){ //If type is tag
            //Regex to get the a in <a href="b">
            tag = ;//funky regex!
            classifyTag();
        }
    }

    public boolean isEndTag(){
        return endTag;
    }

    public boolean isStartTag(){
        return !endTag;
    }

    public boolean hasAttributes(){
        return (attributes.size() > 0);
    }

    public Map getAttributes() {
        return attributes;
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
        ArrayList atts = new ArrayList<String>();
//        atts = fullTag.
//        Pattern attPattern = Pattern.compile("[\w:-]+");
//        Matcher attMatcher = attPattern.matcher(fullTag);
//        attMatcher.
        //TODO Regex Stuff, HALP

        //for each attribute in attributes MAP
        //lowercase them

        //Determine if the tag is an end tag by looking for a / before the tag name. (<\b>)
        int endTagIndex = fullTag.indexOf('/');
        if(endTagIndex != -1){
            int tagPos = fullTag.indexOf(tag);
            if(endTagIndex < tagPos){
                endTag = true;
            }
        }
    }


}