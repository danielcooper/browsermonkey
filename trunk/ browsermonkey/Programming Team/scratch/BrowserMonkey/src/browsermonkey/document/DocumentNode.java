package browsermonkey.document;

/**
 * The <code>DocumentNode</code> interface represents a node in the document
 * tree.
 * @author Paul Calcraft
 */
public interface DocumentNode {
    /**
     * Returns the children of this <code>DocumentNode</code>. Should not return
     * null - an empty list should indicate no children.
     * @return this instance's children
     */
    public java.util.List<DocumentNode> getChildren();
}