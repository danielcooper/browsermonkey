package browsermonkey.document;

import browsermonkey.document.tree.*;

/**
 *
 * @author Paul Calcraft
 */
public class Document {
    DocumentNode nodeTree;

    public Document(String text) {
        nodeTree = new DocumentNodeBase(new TextDocumentNode(text), new TextDocumentNode(text));
    }

    public DocumentNode getNodeTree() {
        return nodeTree;
    }
}