package browsermonkey.render;

import javax.swing.*;

/**
 *
 * @author prtc20
 */
public class LayoutRenderNode extends RenderNode {
    private GroupLayout layout;
    private GroupLayout.ParallelGroup horizontalGroup;
    private GroupLayout.SequentialGroup verticalGroup;
    private TextRenderNode currentTextNode;

    public LayoutRenderNode() {
        layout = new GroupLayout(this);
        this.setLayout(layout);

        horizontalGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        layout.setHorizontalGroup(horizontalGroup);

        verticalGroup = layout.createSequentialGroup();
        layout.setVerticalGroup(verticalGroup);
    }

    public void addNode(RenderNode node) {
        verticalGroup.addComponent(node, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
        horizontalGroup.addComponent(node, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
    }

    public TextRenderNode getTextNode() {
        if (currentTextNode == null) {
            currentTextNode = new TextRenderNode();
            addNode(currentTextNode);
        }
        return currentTextNode;
    }

    public void breakTextNode() {
        currentTextNode = null;
    }
}