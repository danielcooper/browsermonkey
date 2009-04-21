package browsermonkey.document.tree;

import java.util.*;

/**
 *
 * @author Paul Calcraft
 */
public class DocumentNodeBase implements DocumentNode {
    List<DocumentNode> children;

    public DocumentNodeBase() {
        children = new ArrayList<DocumentNode>();
    }

    public DocumentNodeBase(DocumentNode... children) {
        this.children = new ArrayList<DocumentNode>(Arrays.asList(children));
    }

    public List<DocumentNode> getChildren() {
        return children;
    }
}