package browsermonkey.render;

import java.util.*;
import browsermonkey.document.*;

/**
 * Uses the visitor pattern to generate <code>RenderNode</code>s for a
 * <code>DocumentNode</code> tree.
 * @author Paul Calcraft
 */
public class RenderVisitor extends browsermonkey.utility.CachedVisitor {
    private List<RenderNode> nodes = new ArrayList<RenderNode>();

    public List<RenderNode> getNodes() {
        return nodes;
    }

    /**
     * The visit method for <code>TextDocumentNode</code>s. Creates a
     * corresponding TextRenderComponent.
     * @param textNode
     */
    public void visitSub(TextDocumentNode textNode) {
        nodes.add(new TextRenderComponent(textNode.getText()));
    }
}
