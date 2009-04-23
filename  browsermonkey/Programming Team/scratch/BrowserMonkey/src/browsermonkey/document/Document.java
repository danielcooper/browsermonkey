package browsermonkey.document;

/**
 *
 * @author Paul Calcraft
 */
public class Document {
    DocumentNode nodeTree;

    /**
     * Constructs a new <code>Document</code> by parsing a source string.
     * @param source the source to parse
     */
    public Document(String source) {
        nodeTree = new DocumentNodeBase(new TextDocumentNode(source), new TextDocumentNode(source));
    }

    public DocumentNode getNodeTree() {
        return nodeTree;
    }
}