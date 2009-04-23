package browsermonkey.render;

import java.util.*;
import javax.swing.*;
import browsermonkey.document.*;

/**
 * Uses the visitor pattern to generate <code>JComponent</code>s for a
 * <code>DocumentNode</code> tree.
 * @author Paul Calcraft
 */
public class RenderVisitor extends browsermonkey.utility.CachedVisitor {
    private List<JComponent> components = new ArrayList<JComponent>();

    public List<JComponent> getComponents() {
        return components;
    }

    /**
     * The visit method for <code>TextDocumentNode</code>s. Creates a
     * corresponding TextRenderComponent.
     * @param textNode
     */
    public void visitSub(TextDocumentNode textNode) {
        components.add(new TextRenderComponent(textNode.getText()));
    }
}
