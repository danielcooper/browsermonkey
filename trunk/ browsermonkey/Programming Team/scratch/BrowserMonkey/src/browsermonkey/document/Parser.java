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
    ArrayList<DocumentNode> openElements;
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
        this.openElements = new ArrayList<DocumentNode>();
        originalPage = page;
        tokens = new Tokeniser(page).getTokens();
    }

    public void parse(){
        int i = 0;
        while(tokens.hasNext()){
            Token currentToken = tokens.next();

            //add html if needed
            if(i == 0 && !currentToken.tag().equals("html")){
                

            }

        }
    }


}
