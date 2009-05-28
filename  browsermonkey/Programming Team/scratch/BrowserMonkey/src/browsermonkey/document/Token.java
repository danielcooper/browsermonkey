package browsermonkey.document;

import java.util.HashMap;
import java.util.Map;
import browsermonkey.utility.RegexUtility;

/**
 *
 * @author Lawrence Dine
 */
public class Token {
    String tag;
    boolean endTag;
    String fullTag;
    Map<String, String> attributes;
    TokenType type;

    public String tag() {
        return tag;
    }

    public Token(String fullTag, TokenType type){
        this.fullTag = fullTag;
        this.type = type;

        attributes = null;

        endTag = false;

        if (type == TokenType.TEXT){
            tag = fullTag;
        } else if(type == TokenType.TAG) { //If type is tag
            //Regex to get the a in <a href="b">
            tag = RegexUtility.scan(fullTag, "[\\w:-]+")[0][0].toLowerCase();
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

    public Map<String, String> getAttributes() {
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
        String[][] atts = RegexUtility.scan(fullTag, "<[\\w:-]+\\s+(.*)>");
        if (atts.length > 0) {
            String[][] attributeStrings = RegexUtility.scan(atts[0][0], "\\s*([\\w:-]+)\\s*=\\s*(\"[^\"]*\"|'[^']*'|[^\"'>][^\\s>]*)");

            attributes = new HashMap<String, String>();

            for (String[] attribute : attributeStrings) {
                String value = attribute[1];
                if ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("\'") && value.endsWith("\'")))
                    value = value.substring(1, value.length()-1);
                attributes.put(attribute[0].toLowerCase(), value);
            }
        }

        //Determine if the tag is an end tag by looking for a / before the tag name. (</b>)
        int endTagIndex = fullTag.indexOf('/');
        if(endTagIndex != -1){
            int tagPos = fullTag.indexOf(tag);
            if(endTagIndex < tagPos){
                endTag = true;
            }
        }
    }


}