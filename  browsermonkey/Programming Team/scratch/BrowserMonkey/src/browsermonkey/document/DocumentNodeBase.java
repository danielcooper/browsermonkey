package browsermonkey.document;

import java.util.*;

/**
 * Provides default implementation for a <code>DocumentNode</code>'s children
 * handling.
 * @author Paul Calcraft
 */
public class DocumentNodeBase implements DocumentNode {
    List<DocumentNode> children;

    /**
     * Constructs a <code>DocumentNodeBase</code> with no children.
     */
    public DocumentNodeBase() {
        children = new ArrayList<DocumentNode>();
    }

    /**
     * Constructs a <code>DocumentNodeBase</code> with the list of children
     * provided as arguments.
     * For example: <code>new DocumentNodeBase(child1, child2);</code>
     * @param children
     */
    public DocumentNodeBase(DocumentNode... children) {
        this.children = new ArrayList<DocumentNode>(Arrays.asList(children));
    }

    public List<DocumentNode> getChildren() {
        return children;
    }
}