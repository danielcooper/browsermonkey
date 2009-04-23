package browsermonkey.render;

import browsermonkey.document.DocumentNode;
import java.awt.*;
import javax.swing.*;

/**
 * A GUI component for laying out the JComponents to render a
 * <code>DocumentNode</code> tree.
 * @author Paul Calcraft
 */
public class RenderPanel extends JPanel {
    private GroupLayout layout;
    private GroupLayout.ParallelGroup horizontalGroup;
    private GroupLayout.SequentialGroup verticalGroup;

    /**
     * Constructs a <code>RenderPanel</code> by initialising the layout groups.
     */
    public RenderPanel() {
        this.setBackground(Color.white);

        layout = new GroupLayout(this);
        this.setLayout(layout);

        GroupLayout.SequentialGroup horizontalIndentLayout = layout.createSequentialGroup();
        horizontalIndentLayout.addGap(10);
        horizontalGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        horizontalIndentLayout.addGroup(horizontalGroup);
        horizontalIndentLayout.addGap(10);
        layout.setHorizontalGroup(horizontalIndentLayout);

        GroupLayout.SequentialGroup verticalIndentLayout = layout.createSequentialGroup();
        verticalIndentLayout.addGap(10);
        verticalGroup = layout.createSequentialGroup();
        verticalIndentLayout.addGroup(verticalGroup);
        verticalIndentLayout.addGap(10);
        layout.setVerticalGroup(verticalIndentLayout);
    }

    /**
     * Sets the <code>DocumentNode</code> tree for this <code>RenderPanel</code>
     * to render.
     * Uses the {@link}RenderVisitor to generate renderable components for the
     * tree and then adds these components to the layout managers.
     * @param root
     */
    public void setDocumentTree(DocumentNode root) {
        removeAll();
        RenderVisitor renderVisitor = new RenderVisitor();
        for (DocumentNode node : root.getChildren())
            renderVisitor.visit(node);
        for (JComponent component : renderVisitor.getComponents()) {
            verticalGroup.addComponent(component, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
            horizontalGroup.addComponent(component, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        }
        revalidate();
        repaint();
    }
}