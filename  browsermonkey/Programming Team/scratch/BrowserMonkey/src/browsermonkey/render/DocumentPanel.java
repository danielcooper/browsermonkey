package browsermonkey.render;

import browsermonkey.document.*;
import java.awt.*;
import javax.swing.*;

/**
 * A GUI component for laying out the JComponents to render a
 * <code>DocumentNode</code> tree.
 * @author Paul Calcraft
 */
public class DocumentPanel extends JPanel {
    private Document document;
    private GroupLayout layout;
    private GroupLayout.ParallelGroup horizontalGroup;
    private GroupLayout.SequentialGroup verticalGroup;

    /**
     * Constructs a <code>DocumentPanel</code> by initialising the layout groups.
     */
    public DocumentPanel() {
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
     * Sets the <code>Document</code> for this <code>DocumentPanel</code> to
     * render.
     * @param document
     */
    public void setDocument(Document document) {
        this.document = document;
        load();
    }

    /**
     * Uses the {@link}RenderVisitor to generate the render nodes
     * and then adds these components to the layout managers.
     */
    public void load() {
        removeAll();
        java.util.List<DocumentNode> rootNodes = document.getNodeTree().getChildren();
        RenderVisitor renderVisitor = new RenderVisitor();
        for (DocumentNode node : rootNodes)
            renderVisitor.visit(node);
        for (JComponent component : renderVisitor.getNodes()) {
            verticalGroup.addComponent(component, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
            horizontalGroup.addComponent(component, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        }
        revalidate();
        repaint();
    }
}