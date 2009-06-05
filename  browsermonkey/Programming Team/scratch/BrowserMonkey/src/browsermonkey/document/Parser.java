/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package browsermonkey.document;

import browsermonkey.utility.BrowserMonkeyLogger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Daniel Cooper, Lawrence Dine
 */
public class Parser {

    private Set<String> ignoredTags;
    private Set<String> headTags;
    private Set<String> structureTags; // Tags that can't contain text nodes.
    private Set<String> tableTags;
    private Set<String> nestableTags;
    private Set<String> singularlyNestableTags;
    private Set<String> leafTags;
    private Set<String> listTags;
    private TagDocumentNode rootNode;
    private TagDocumentNode headNode;
    private ArrayList<TagDocumentNode> openElements;
    private String originalPage;
    private Iterator<Token> tokens;
    private boolean conformant = true;


    private void conformanceError(String error){
        BrowserMonkeyLogger.conformance(error);
        conformant = false;
    }

    public boolean isConformant(){
        return conformant;
    }

    private boolean whitespaceIsPreformatted() {
        for (TagDocumentNode tag : openElements) {
            if (tag.getType().equals("pre")) {
                return true;
            }
        }
        return false;
    }

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
        ignoredTags = new HashSet<String>();
        ignoredTags.add("html");
        ignoredTags.add("body");
        ignoredTags.add("head");
        headTags = new HashSet<String>();
        headTags.add("title");

        structureTags = new HashSet<String>();
        structureTags.add("table");
        structureTags.add("tr");
        structureTags.add("td");

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

        originalPage = page;
        Tokeniser tokeniser = new Tokeniser(page);
        tokeniser.tokenise();
        tokens = tokeniser.getTokens();
        conformant = tokeniser.isConformant();
    }

    public void parse() {
        openElements = new ArrayList<TagDocumentNode>();

        rootNode = new TagDocumentNode("html", null);
        openElements.add(rootNode);

        while (tokens.hasNext()) {
            Token currentToken = tokens.next();

            if (currentToken.getType() == TokenType.TAG) {
                if (currentToken.getTag().equals("th"))
                    currentToken.setTag("td");
                
                if (ignoredTags.contains(currentToken.getTag())) {
                    continue;
                }

                if (currentToken.isStartTag()) {
                    if (headTags.contains(currentToken.getTag())) {
                        if (headNode == null) {
                            headNode = new TagDocumentNode("head", null);
                            rootNode.children.add(0, headNode);
                        }

                        TagDocumentNode headChildNode = new TagDocumentNode(currentToken.getTag(), currentToken.getAttributes());
                        headNode.addChild(headChildNode);
                        openElements.add(headChildNode);
                        continue;
                    }
                    //if it's  table tag or if a row has been opened but not a cell - add the approprate elements
                    if (tableTags.contains(currentToken.getTag())) {
                        doTableElement(currentToken);
                        continue;
                    } else if (openElements.size() >= 1) {
                        if (openElements.get(openElements.size() - 1).getType().equals("tr") || openElements.get(openElements.size() - 1).getType().equals("table")) {
                            conformanceError("Table Error: correcting with new <td> tag.");
                            doTableElement(new Token("<td>", TokenType.TAG));
                        }
                    }

                    //perform listed tag functions
                    if (listTags.contains(currentToken.getTag())) {
                        doListedElement(currentToken);
                    }
                    //For singularly nestable tags, check if the last tag is the same. If it is
                    //fix the nesting, if not - carry on.
                    else if (singularlyNestableTags.contains(currentToken.getTag())) {
                        if (openElements.size() > 1 && openElements.get(openElements.size() - 1).getType().equals(currentToken.getTag())) {
                            conformanceError("Tag Nesting Error: closing " +currentToken.getTag()+".");
                            doEndToken(currentToken);
                        }
                        doStartToken(currentToken);
                    } //basic nestable tag
                    else if (nestableTags.contains(currentToken.getTag())) {
                        doStartToken(currentToken);
                    } //add the leaf tag
                    else if (leafTags.contains(currentToken.getTag())) {
                        doLeafElement(currentToken);
                    } else {
                        // if in doubt...
                        doStartToken(currentToken);
                    }

                } else {
                    doEndToken(currentToken);
                }
            } else {
                String text = currentToken.getTag();
                if (!whitespaceIsPreformatted()) {
                    text = text.replaceAll("\\s+", " ");
                    if (structureTags.contains(openElements.get(openElements.size() - 1).getType()) && text.equals(" ")) {
                        continue;
                    }
                }
                //add a text element - but not without checking the state of the tables.
                if (openElements.size() >= 1) {
                    if (openElements.get(openElements.size() - 1).getType().equals("tr") || openElements.get(openElements.size() - 1).getType().equals("table")) {
                        this.doTableElement(new Token("<td>", TokenType.TAG));
                    }
                }
                doTextElement(text);
            }
        }
    }

    //Does a standard list. If the user chooses to not close li tags then it assumes that the next li
    //signifies the start of the tag. Also, if no list type is given, it defaults to ul
    private void doListedElement(Token token) {
        if (token.getTag().equals("li")) {
            if (openElements.size() >= 1) {
                if (openElements.get(openElements.size() - 1).getType().equals("ol") || openElements.get(openElements.size() - 1).getType().equals("ul")) {
                    doStartToken(token);
                } else if (openElements.get(openElements.size() - 1).getType().equals("li")) {
                    conformanceError("List Error: shorthand list notation - closing <li>.");
                    doEndToken(openElements.get(openElements.size() - 1));
                    doStartToken(token);
                } else {
                    conformanceError("List Error: correcting with new <ul> tag.");
                    doListedElement(new Token("<ul>", TokenType.TAG));
                    doStartToken(token);
                }
            } else {
                //do table token new <ul> token
                conformanceError("List Error: correcting with new <ul> tag.");
                doListedElement(new Token("<ul>", TokenType.TAG));
                doStartToken(token);
            }
        } else if (token.getTag().equals("ol") || token.getTag().equals("ul")) {
            doStartToken(token);
        }
    }

    //Ensures that tables are properly nested
    public void doTableElement(Token token) {
        if (token.getTag().equals("td")) {
            if (openElements.size() >= 1) {
                if (openElements.get(openElements.size() - 1).getType().equals("tr")) {
                    doStartToken(token);
                } else {
                    conformanceError("Table Error: correcting with new <tr> tag.");
                    doTableElement(new Token("<tr>", TokenType.TAG));
                    doStartToken(token);
                }
            } else {
                conformanceError("Table Error: correcting with new <tr> tag.");
                doTableElement(new Token("<tr>", TokenType.TAG));
                doStartToken(token);

            }
        } else if (token.getTag().equals("tr")) {
            if (openElements.size() >= 1) {
                if (openElements.get(openElements.size() - 1).getType().equals("table")) {
                    doStartToken(token);
                } else {
                    conformanceError("Table Error: correcting with new <table> tag.");
                    doTableElement(new Token("<table>", TokenType.TAG));
                    doStartToken(token);
                }
            } else {
                conformanceError("Table Error: correcting with new <table> tag.");
                doTableElement(new Token("<table>", TokenType.TAG));
                doStartToken(token);
            }
        } else if (token.getTag().equals("table")) {
            doStartToken(token);
        }
    }

    private void doLeafElement(Token token) {
        TagDocumentNode tagNode = new TagDocumentNode(token.getTag(), token.getAttributes());
        openElements.get(openElements.size() - 1).addChild(tagNode);
    }

    private void doTextElement(String text) {
        TextDocumentNode textNode = new TextDocumentNode(text);
        openElements.get(openElements.size() - 1).addChild(textNode);
    }

    private void fixNestingError(Token token) {
       conformanceError("Nesting Error: on tag " + token.getTag()+".");
        int errorIndex = -1;
        for (int i = openElements.size() - 1; i >= 0; i--) {
            if (openElements.get(i).getType().equals(token.getTag())) {
                errorIndex = i;
                break;
            }
        }
        if (errorIndex != -1) {
            for (int i = openElements.size() - 1; i >= errorIndex; i--) {
                openElements.remove(openElements.get(i));
            }
        }
        
    }

    private void fixNestingError(TagDocumentNode tagDocNode) {
        conformanceError("Nesting Error: on tag " + tagDocNode.getType()+".");
        int errorIndex = -1;

        for (int i = openElements.size() - 1; i >= 0; i--) {
            if (openElements.get(i).getType().equals(tagDocNode.getType())) {
                errorIndex = i;
                break;
            }
        }

        if (errorIndex != -1) {

            for (int i = openElements.size() - 1; i >= errorIndex; i--) {

                openElements.remove(openElements.get(i));
            }
        }
        


    }

    private void doEndToken(Token token) {
        if (token.getTag().equals(openElements.get(openElements.size() - 1).getType())) {
            openElements.remove(openElements.size() - 1);
        } else {
            fixNestingError(token);
        }
    }

    private void doEndToken(TagDocumentNode tagDocNode) {
        if (tagDocNode.getType().equals(openElements.get(openElements.size() - 1).getType())) {
            openElements.remove(openElements.size() - 1);
        } else {
            fixNestingError(tagDocNode);
        }
    }

    private void doStartToken(Token token) {
        TagDocumentNode newNode = new TagDocumentNode(token.getTag(), token.getAttributes());

        openElements.get(openElements.size() - 1).addChild(newNode);
        openElements.add(newNode);
        if (openElements.size() == 1) {
            rootNode = openElements.get(0);
        }
    }
}