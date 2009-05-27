package browsermonkey.render;

import javax.swing.*;

/**
 *
 * @author Paul Calcraft
 */
public class LayoutRenderNode extends RenderNode {
    private GroupLayout layout;
    private GroupLayout.ParallelGroup horizontalGroup;
    private GroupLayout.SequentialGroup verticalGroup;
    private TextRenderNode currentTextNode;

    public LayoutRenderNode(Linkable linker) {
        super(linker);
        layout = new GroupLayout(this);
        this.setLayout(layout);

        horizontalGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        layout.setHorizontalGroup(horizontalGroup);

        verticalGroup = layout.createSequentialGroup();
        layout.setVerticalGroup(verticalGroup);
    }

    public void setPadding(int left, int right, int top, int bottom) {
        if (left > 0 || right > 0) {
            GroupLayout.SequentialGroup paddedContainer = layout.createSequentialGroup();
            if (left > 0)
                paddedContainer.addGap(left);
            paddedContainer.addGroup(horizontalGroup);
            if (right > 0)
                paddedContainer.addGap(right);
            
            layout.setHorizontalGroup(paddedContainer);
        }
        else
            layout.setHorizontalGroup(horizontalGroup);
        if (top > 0 || bottom > 0) {
            GroupLayout.SequentialGroup paddedContainer = layout.createSequentialGroup();
            if (top > 0)
                paddedContainer.addGap(top);
            paddedContainer.addGroup(verticalGroup);
            if (bottom > 0)
                paddedContainer.addGap(bottom);

            layout.setVerticalGroup(paddedContainer);
        }
        else
            layout.setVerticalGroup(verticalGroup);
    }

    public void addNode(RenderNode node) {
        verticalGroup.addComponent(node, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
        horizontalGroup.addComponent(node, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
    }

    public TextRenderNode getTextNode() {
        if (currentTextNode == null) {
            currentTextNode = new TextRenderNode(linker);
            addNode(currentTextNode);
        }
        return currentTextNode;
    }

    public void addLineBreaks(int count) {
        if (count <= 0)
            return;
        if (currentTextNode != null) {
            currentTextNode = null;
            count--;
        }

        for (int i = 0; i < count; i++)
            addLineSpace();
    }

    private void addLineSpace() {
        verticalGroup.addGap(15);
    }

    public void ensureNewLine() {
        currentTextNode = null;
    }
}