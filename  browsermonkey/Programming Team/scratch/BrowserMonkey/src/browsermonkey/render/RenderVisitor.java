package browsermonkey.render;

import java.util.*;
import javax.swing.*;
import browsermonkey.document.tree.*;
import browsermonkey.render.components.*;

/**
 *
 * @author Paul Calcraft
 */
public class RenderVisitor extends browsermonkey.utility.CachedVisitor {
    private List<JComponent> components = new ArrayList<JComponent>();

    public List<JComponent> getComponents() {
        return components;
    }

    public void visitSub(TextDocumentNode text) {
        components.add(new TextRenderComponent(text.getText()));
    }
}
