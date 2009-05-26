/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package browsermonkey.document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Daniel Cooper, Lawrence Dine
 */
public class Parser {

    Set<String> singleNestableTags;
    Set<String> tableTags;
    Set<String> nestableTags;
    Set<String> singularlyNestableTags;
    Set<String> leafTags;
    Set<String> listTags;
    DocumentNode rootNode;
    ArrayList<TagDocumentNode> openElements;
    String originalPage;
    Iterator<Token> tokens;

    public Parser(String page) {
        this.singleNestableTags = new HashSet<String>();
        this.tableTags = new HashSet<String>();
        this.nestableTags = new HashSet<String>();
        this.singularlyNestableTags = new HashSet<String>();
        this.leafTags = new HashSet<String>();
        this.listTags = new HashSet<String>();
        this.rootNode = new DocumentNode() {}; //TODO FIX THIS
        this.openElements = new ArrayList<TagDocumentNode>();
        originalPage = page;
        tokens = new Tokeniser(page).getTokens();
    }

    public void parse(){
        int i = 0;
        while(tokens.hasNext()){
            Token currentToken = tokens.next();

            //add html if needed
            if(i == 0 && !currentToken.tag().equals("html")){
                TagDocumentNode newNode = new TagDocumentNode("html", null);
                openElements.add(newNode);
            }

            if(currentToken.getType().equals("Tag")){
                if(currentToken.isStartTag()){
                    if(tableTags.contains(currentToken.getTag())){

                    }
                }

            }
        }
    }

    //Does a standard list. If the user chooses to not close li tags then it assumes that the next li
    //signifies the start of the tag. Also, if no list type is given, it defaults to ul
    private void doListedElement(Token token){
        if(token.tag().equals("li")){
            if(openElements.size() >= 1){
                if(openElements.get(openElements.size()-1).getType().equals("ol") || openElements.get(openElements.size()-1).getType().equals("ul")){
                    //do start token current token
                } else if(openElements.get(openElements.size()-1).getType().equals("li")){
                    //do end token on previous token
                    //do start token current token
                } else {
                    doListedElement(new Token("<ul>", TokenType.valueOf("Tag")));
                    //do start token current token
                }
            } else {
                //do table token new <ul> token
                //do start token current token
            }
        } else if(token.getTag().equals("ol") || token.getTag().equals("ul")){
            //do start token current token
        }
    }

    //Ensures that tables are properly nested
    public void doTableElement(Token token){
        if(token.getTag().equals("td")){
            if(openElements.size() >= 1){
                if(openElements.get(openElements.size()-1).getType().equals("tr")){
                    //do start token current token
                } else {
                    doTableElement(new Token("<tr>", TokenType.valueOf("Tag")));
                    //do start token current token
                }
            } else {
                //do table token new <tr> token
                //do start token current token

            }
        } else if(token.getTag().equals("tr")){
            if(openElements.size() >= 1){
                if(openElements.get(openElements.size()-1).getType().equals("table")){
                    //do start token current token
                } else {
                    doTableElement(new Token("<table>", TokenType.valueOf("Tag")));
                    //do start token current token
                }
            } else {
                doTableElement(new Token("<table>", TokenType.valueOf("Tag")));
                //do start token current token
            }
        } else if(token.getTag().equals("table")){
            //do start token current token
        }
    }
}
