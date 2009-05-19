package browsermonkey.render;

import browsermonkey.document.*;
import java.util.ArrayList;
import javax.swing.event.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    private float zoomLevel = 1.0f;
    private RenderNode rootRenderNode;

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

    public void load(String path) throws FileNotFoundException, IOException {
        removeAll();
        document = new Document(path);
        if (path.startsWith("t "))
            document.loadTest(path.substring(2));
        else
            document.load();
        
        Renderer r = new Renderer(new DocumentLinker(this));
        rootRenderNode = r.renderRoot(document.getNodeTree(), zoomLevel);

        verticalGroup.addComponent(rootRenderNode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
        horizontalGroup.addComponent(rootRenderNode, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);

        changed();
        revalidate();
        repaint();
    }

    public String getAddress() {
        return document.getPath();
    }

    private ArrayList<ChangeListener> changeListeners = new ArrayList<ChangeListener>();

    /**
     * Adds a ChangeListener to the panel.
     * @param listener
     */
    public void addChangeListener(ChangeListener listener) {
        changeListeners.add(listener);
    }

    /**
     * Removes a ChangeListener from the panel.
     * @param listener
     */
    public void removeChangeListener(ChangeListener listener) {
        changeListeners.remove(listener);
    }

    /**
     * Alerts the ChangeListeners that the panel has changed.
     */
    protected void changed() {
        for (ChangeListener listener : changeListeners)
            listener.stateChanged(new ChangeEvent(this));
    }
}