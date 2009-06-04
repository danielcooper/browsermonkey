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
    private Set<String> singleNestableTags;
    private Set<String> tableTags;
    private Set<String> nestableTags;
    private Set<String> singularlyNestableTags;
    private Set<String> leafTags;
    private Set<String> listTags;
    private DocumentNode rootNode;
    private ArrayList<TagDocumentNode> openElements;
    private String originalPage;
    private Iterator<Token> tokens;

    public DocumentNode getRootNode() {
        return rootNode;
    }

    public Parser(String page) {
        /*@single_nestable_tags = ['html','head','body'] #tags that can only be used once
    @table_tags = ['table','tr','td',] #table tags need a special case
    @nestable_tags = ['b','i','strong','em','pre'] #normal, nestable, tags
    @singuarly_nestable_tags = ['p'] #these tags cannot be nested inside themselfs
    @leaf_tags =['br','img'] #can have no children
    @listed_tags =['li','ol','ul'] #list ele*/
        this.singleNestableTags = new HashSet<String>();
        singleNestableTags.add("html");
        singleNestableTags.add("head");
        singleNestableTags.add("body");
        this.tableTags = new HashSet<String>();
        tableTags.add("table");
        tableTags.add("tr");
        tableTags.add("td");
        this.nestableTags = new HashSet<String>();
        nestableTags.add("b");
        nestableTags.add("i");
        nestableTags.add("strong");
        nestableTags.add("em");
        nestableTags.add("pre");
        this.singularlyNestableTags = new HashSet<String>();
        singularlyNestableTags.add("p");
        this.leafTags = new HashSet<String>();
        leafTags.add("br");
        leafTags.add("img");
        leafTags.add("hr");
        this.listTags = new HashSet<String>();
        listTags.add("li");
        listTags.add("ol");
        listTags.add("ul");
        this.openElements = new ArrayList<TagDocumentNode>();
        originalPage = page;
        Tokeniser tokeniser =  new Tokeniser(page);
        tokeniser.tokenise();
        tokens = tokeniser.getTokens();
    }

    public void parse(){
        int i = 0;
        TagDocumentNode newNode = new TagDocumentNode("html", null);
        openElements.add(newNode);
        rootNode = newNode;
        while(tokens.hasNext()){
            Token currentToken = tokens.next();

            if (i == 0 && currentToken.getTag().equals("html"))
                continue;

            if(currentToken.getType() == TokenType.TAG){
                if(currentToken.isStartTag()){
                    //if it's  table tag or if a row has been opened but not a cell - add the approprate elements
                    if(tableTags.contains(currentToken.getTag())){
                        doTableElement(currentToken);
                        continue;
                    }else if(openElements.size() >= 1){
                        if(openElements.get(openElements.size()-1).getType().equals("tr") || openElements.get(openElements.size()-1).getType().equals("table")){
                            doTableElement(new Token("<td>", TokenType.TAG));
                        }
                    }

                    //perform listed tag functions
                    if(listTags.contains(currentToken.getTag())){
                        doListedElement(currentToken);
                    }
                    
                    //TODO Fix pre elements
                    //For singularly nestable tags, check if the last tag is the same. If it is
                    //fix the nesting, if not - carry on.
                    else if(singularlyNestableTags.contains(currentToken.getTag())){
                        if(openElements.size() > 1 && openElements.get(openElements.size()-1).getType().equals(currentToken.getTag())){
                            doEndToken(currentToken);
                        }
                        doStartToken(currentToken);
                    }
                    //basic nestable tag
                    else if(nestableTags.contains(currentToken.getTag())){
                        doStartToken(currentToken);
                    }
                    //add the leaf tag
                    else if(leafTags.contains(currentToken.getTag())){
                        doLeafElement(currentToken);
                    }
                    else {
                        // if in doubt...
                        doStartToken(currentToken);
                    }

                } else {
                    doEndToken(currentToken);
                }
            }
            else {
                //add a text element - but not without checking the state of the tables.
                if (openElements.size() >= 1)
                    if (openElements.get(openElements.size()-1).getType().equals("tr") || openElements.get(openElements.size()-1).getType().equals("table"))
                        this.doTableElement(new Token("<td>", TokenType.TAG));
                doTextElement(currentToken);
            }
            i++;
        }
        postProcess();
    }

    private void postProcess() {
        // Find all p tags that are not inside a pre, and remove if empty.
        ArrayList<TagDocumentNode> nonPreTags = shallowTagSearch(rootNode, "pre", true);
        for (TagDocumentNode nonPreTag : nonPreTags) {
             ArrayList<TextDocumentNode> textNodes = textSearch(nonPreTag, false);
            for (TextDocumentNode textNode : textNodes) {
                String text = textNode.getText().replaceAll("\\s+", " ");
                //int textIndex = nonPreTag.children.indexOf(textNode);
                //if (textIndex == 0)
                //    text = text.trim();
                //else if (textIndex == nonPreTag.children.size()-1)
                //    text.trim();
                if (text.equals(" "))
                    nonPreTag.children.remove(textNode);
                else
                    textNode.setText(text);
            }
        }
            /*
            Iterator<DocumentNode> i = nonPreTag.getChildren().iterator();
            while (i.hasNext()) {
                DocumentNode childDocNode = i.next();
                if (!(childDocNode instanceof TagDocumentNode))
                    continue;
                TagDocumentNode child = (TagDocumentNode)childDocNode;
                if (child.getType().equals("p")) {
                    boolean isEmpty = true;
                    for (DocumentNode node : child.getChildren()) {
                        if (node instanceof TextDocumentNode) {
                            if (!((TextDocumentNode)node).getText().replaceAll("\\s+", "").isEmpty()) {
                                isEmpty = false;
                                break;
                            }
                        }
                        else {
                            isEmpty = false;
                                break;
                        }
                    }
                    if (isEmpty) {
                        //i.remove();
                        child.getChildren().clear();
                    }
                }
            }
        }*/
    }

    private static ArrayList<TextDocumentNode> textSearch(DocumentNode parent, boolean deep) {
        ArrayList<TextDocumentNode> result = new ArrayList<TextDocumentNode>();

        for (DocumentNode child : parent.getChildren()) {
            if (child instanceof TextDocumentNode) {
                TextDocumentNode textNode = (TextDocumentNode)child;
                result.add(textNode);
            }
            else if (deep)
                result.addAll(textSearch(child, true));
        }

        return result;
    }

    private static ArrayList<TagDocumentNode> shallowTagSearch(DocumentNode parent, String type, boolean inverse) {
        ArrayList<TagDocumentNode> result = new ArrayList<TagDocumentNode>();

        if (parent instanceof TagDocumentNode) {
            TagDocumentNode tagNode = (TagDocumentNode)parent;
            if (tagNode.getType().equals(type)) {
                if (inverse)
                    return result;

                result.add(tagNode);
                return result;
            }
            else if (inverse)
                result.add(tagNode);
        }

        for (DocumentNode child : parent.getChildren())
            result.addAll(shallowTagSearch(child, type, inverse));

        return result;
    }

    //Does a standard list. If the user chooses to not close li tags then it assumes that the next li
    //signifies the start of the tag. Also, if no list type is given, it defaults to ul
    private void doListedElement(Token token){
        if(token.tag().equals("li")){
            if(openElements.size() >= 1){
                if(openElements.get(openElements.size()-1).getType().equals("ol") || openElements.get(openElements.size()-1).getType().equals("ul")){
                    doStartToken(token);
                } else if(openElements.get(openElements.size()-1).getType().equals("li")){
                    doEndToken(openElements.get(openElements.size()-1));
                    doStartToken(token);
                } else {
                    doListedElement(new Token("<ul>", TokenType.TAG));
                    doStartToken(token);
                }
            } else {
                //do table token new <ul> token
                doListedElement(new Token("<ul>",TokenType.TAG));
                doStartToken(token);
            }
        } else if(token.getTag().equals("ol") || token.getTag().equals("ul")){
            doStartToken(token);
        }
    }

    //Ensures that tables are properly nested
    public void doTableElement(Token token){
        if(token.getTag().equals("td")){
            if(openElements.size() >= 1){
                if(openElements.get(openElements.size()-1).getType().equals("tr")){
                    doStartToken(token);
                } else {
                    doTableElement(new Token("<tr>", TokenType.TAG));
                    doStartToken(token);
                }
            } else {
                doTableElement(new Token("<tr>", TokenType.TAG));
                doStartToken(token);

            }
        } else if(token.getTag().equals("tr")){
            if(openElements.size() >= 1){
                if(openElements.get(openElements.size()-1).getType().equals("table")){
                    doStartToken(token);
                } else {
                    doTableElement(new Token("<table>", TokenType.TAG));
                    doStartToken(token);
                }
            } else {
                doTableElement(new Token("<table>", TokenType.TAG));
                doStartToken(token);
            }
        } else if(token.getTag().equals("table")){
            doStartToken(token);
        }
    }

    private void doLeafElement(Token token){
        TagDocumentNode tagNode = new TagDocumentNode(token.getTag(), token.getAttributes());
        openElements.get(openElements.size()-1).addChild(tagNode);
    }

    private void doTextElement(Token token) {
        TextDocumentNode textNode = new TextDocumentNode(token.getTag());
        openElements.get(openElements.size()-1).addChild(textNode);
    }

    private void fixNestingError(Token token){
        for(int i = openElements.size()-1;i==0;i--){
            if(openElements.get(i).getType().equals(token.getTag())){
                openElements.remove(openElements.get(i));
                break;
            }
        }
    }

    private void fixNestingError(TagDocumentNode tagDocNode){
        for(int i = openElements.size()-1;i==0;i--){
            if(openElements.get(i).getType().equals(tagDocNode.getType())){
                openElements.remove(openElements.get(i));
                break;
            }
        }
    }

    private void doEndToken(Token token){
        if(token.getTag().equals(openElements.get(openElements.size()-1).getType())){
            openElements.remove(openElements.size()-1);
        } else {
            fixNestingError(token);
        }
    }

    private void doEndToken(TagDocumentNode tagDocNode){
        if(tagDocNode.getType().equals(openElements.get(openElements.size()-1).getType())){
            openElements.remove(openElements.size()-1);
        } else {
            fixNestingError(tagDocNode);
        }
    }

    private void doStartToken(Token token){
        TagDocumentNode newNode = new TagDocumentNode(token.getTag(), token.getAttributes());

        openElements.get(openElements.size()-1).addChild(newNode);
        openElements.add(newNode);
        if (openElements.size() == 1) {
            rootNode = openElements.get(0);
        }
    }
}