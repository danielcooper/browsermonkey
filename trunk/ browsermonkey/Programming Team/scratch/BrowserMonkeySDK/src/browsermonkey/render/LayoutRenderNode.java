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
    private int currentLinespaceDistance = 0;
    private boolean hasPreviousComponent = false;
    private boolean centred;

    public LayoutRenderNode(Linkable linker) {
        this(linker, false);
    }

    public LayoutRenderNode(Linkable linker, boolean centred) {
        super(linker);
        this.centred = centred;
        //setBorder(LineBorder.createGrayLineBorder());
        
        layout = new GroupLayout(this);
        this.setLayout(layout);

        horizontalGroup = layout.createParallelGroup(centred ? GroupLayout.Alignment.CENTER : GroupLayout.Alignment.LEADING);
        layout.setHorizontalGroup(horizontalGroup);

        verticalGroup = layout.createSequentialGroup();
        layout.setVerticalGroup(verticalGroup);
        
        if (centred) {
            JComponent spacer = new JComponent() {};
            horizontalGroup.addComponent(spacer, 0, 0, Short.MAX_VALUE);
            verticalGroup.addComponent(spacer, 0, 0, 0);
        }
    }

    @Override
    public void setZoomLevel(float zoomLevel) {
        for (Component component : getComponents()) {
            if (component instanceof RenderNode) {
                ((RenderNode)component).setZoomLevel(zoomLevel);
            }
        }
        revalidate();
        //repaint();
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

    public void addNodePadding(RenderNode leftNode, RenderNode rightNode) {
        if (leftNode == null && rightNode == null)
            return;
        
        GroupLayout.SequentialGroup paddingHorizontalContainer = layout.createSequentialGroup();
        GroupLayout.ParallelGroup verticalOverlapper = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        verticalOverlapper.addGroup(verticalGroup);
        
        if (leftNode != null) {
            paddingHorizontalContainer.addComponent(leftNode);
            verticalOverlapper.addComponent(leftNode);
        }
        
        paddingHorizontalContainer.addGroup(horizontalGroup);

        if (rightNode != null) {
            paddingHorizontalContainer.addComponent(rightNode);
            verticalOverlapper.addComponent(rightNode);
        }
        
        layout.setHorizontalGroup(paddingHorizontalContainer);
        layout.setVerticalGroup(verticalOverlapper);
    }

    public enum WidthBehaviour {
        Minimal,
        Maximal,
        Grow
    }

    public void addNode(RenderNode node, WidthBehaviour widthBehaviour) {
        if (hasPreviousComponent)
            for (int i = 0; i < currentLinespaceDistance; i++)
                addLineSpace(false);
        hasPreviousComponent = true;
        currentLinespaceDistance = 0;

        int widthMax = GroupLayout.DEFAULT_SIZE;
        if (widthBehaviour == WidthBehaviour.Minimal)
            widthMax = GroupLayout.PREFERRED_SIZE;
        else if (widthBehaviour == WidthBehaviour.Grow)
            widthMax = Short.MAX_VALUE;

        verticalGroup.addComponent(node, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE);
        horizontalGroup.addComponent(node, centred ? GroupLayout.Alignment.CENTER : GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, widthMax);
    }

    public TextRenderNode getTextNode() {
        if (currentTextNode == null) {
            currentTextNode = new TextRenderNode(linker, centred);
            addNode(currentTextNode, WidthBehaviour.Maximal);
        }
        return currentTextNode;
    }

    public void ensureLinespaceDistance(int distance) {
        ensureNewLine();
        currentLinespaceDistance = Math.max(currentLinespaceDistance, distance);
    }

    public void addHardLineBreak() {
        if (currentTextNode == null) {
            addLineSpace(true);
            hasPreviousComponent = true;
            currentLinespaceDistance = 0;
        }
        else
            currentTextNode = null;
    }

    private void addLineSpace(boolean addAsNode) {
        TextRenderNode lineSpace = new TextRenderNode(linker);
        lineSpace.addText("&nbsp;", Renderer.DEFAULT_FORMATTING);
        if (addAsNode) {
            addNode(lineSpace, WidthBehaviour.Minimal);
        }
        else {
            verticalGroup.addComponent(lineSpace);
            horizontalGroup.addComponent(lineSpace);
        }
    }

    public void ensureNewLine() {
        currentTextNode = null;
    }
}