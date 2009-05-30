package browsermonkey.render;

import java.awt.*;
import java.text.AttributedString;
import java.util.ArrayList;
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
    private boolean centred;

    public LayoutRenderNode(Linkable linker) {
        this(linker, false);
    }

    public LayoutRenderNode(Linkable linker, boolean centred) {
        super(linker);
        this.centred = centred;
        
        layout = new GroupLayout(this);
        this.setLayout(layout);

        horizontalGroup = layout.createParallelGroup(centred ? GroupLayout.Alignment.CENTER : GroupLayout.Alignment.LEADING);
        layout.setHorizontalGroup(horizontalGroup);

        verticalGroup = layout.createSequentialGroup();
        layout.setVerticalGroup(verticalGroup);
    }

    @Override
    public void setZoomLevel(float zoomLevel) {
        for (Component component : getComponents()) {
            if (component instanceof RenderNode) {
                ((RenderNode)component).setZoomLevel(zoomLevel);
            }
        }
    }

    @Override
    public void extractTextInto(ArrayList<AttributedString> text) {
        for (Component component : getComponents()) {
            if (component instanceof RenderNode) {
                ((RenderNode)component).extractTextInto(text);
            }
        }
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

    public void padLeftWithNode(RenderNode node) {
        GroupLayout.SequentialGroup paddedContainer = layout.createSequentialGroup();
        paddedContainer.addComponent(node);
        paddedContainer.addGroup(horizontalGroup);
        layout.setHorizontalGroup(paddedContainer);
        GroupLayout.ParallelGroup verticalOverlapper = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        verticalOverlapper.addGroup(verticalGroup);
        verticalOverlapper.addComponent(node, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
        layout.setVerticalGroup(verticalOverlapper);
    }

    public void addNode(RenderNode node) {
        verticalGroup.addComponent(node, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
        horizontalGroup.addComponent(node, centred ? GroupLayout.Alignment.CENTER : GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
    }

    public TextRenderNode getTextNode() {
        if (currentTextNode == null) {
            currentTextNode = new TextRenderNode(linker, centred);
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
        TextRenderNode lineSpace = new TextRenderNode(linker);
        lineSpace.addText(" ", Renderer.FIXED_DEFAULT_FORMATTING);
        addNode(lineSpace);
    }

    public void ensureNewLine() {
        currentTextNode = null;
    }
}