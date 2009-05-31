package browsermonkey.render;

import browsermonkey.document.*;
import browsermonkey.utility.BrowserMonkeyLogger;
import java.util.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.font.TextAttribute;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.*;
import java.util.ArrayList;
import javax.swing.*;

/**
 * A GUI component for laying out the JComponents to render a
 * <code>DocumentNode</code> tree.
 * @author Paul Calcraft
 */
public class DocumentPanel extends JPanel {
    private Document document;
    private String title;
    private GroupLayout layout;
    private GroupLayout.ParallelGroup horizontalGroup;
    private GroupLayout.SequentialGroup verticalGroup;
    private float zoomLevel = 1.0f;
    private RenderNode rootRenderNode;

    public String getTitle() {
        return title;
    }

    /**
     * Constructs a <code>DocumentPanel</code> by initialising the layout groups.
     */
    public DocumentPanel() {
        this.setBackground(Color.white);

        layout = new GroupLayout(this);
        this.setLayout(layout);

        GroupLayout.SequentialGroup horizontalIndentLayout = layout.createSequentialGroup();
        horizontalIndentLayout.addGap(8);
        horizontalGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        horizontalIndentLayout.addGroup(horizontalGroup);
        horizontalIndentLayout.addGap(8);
        layout.setHorizontalGroup(horizontalIndentLayout);

        GroupLayout.SequentialGroup verticalIndentLayout = layout.createSequentialGroup();
        verticalIndentLayout.addGap(8);
        verticalGroup = layout.createSequentialGroup();
        verticalIndentLayout.addGroup(verticalGroup);
        verticalIndentLayout.addGap(8);
        layout.setVerticalGroup(verticalIndentLayout);
    }

    private String getCurrentFolderPath() {
        if (document == null)
            return "";
        return document.getPath().replaceFirst("[^/\\\\]*$", "");
    }

    public void load(String path) {
        load(path, false);
    }

    public void load(String path, boolean relative) {
        removeAll();
        if (relative) {
            path = getCurrentFolderPath()+path;
        }
        document = new Document(path);
        if (path.startsWith("t "))
            document.loadTest(path.substring(2));
        else {
            try {
                document.load();
            } catch (FileNotFoundException ex) {
                BrowserMonkeyLogger.warning("File not found: "+path);
                load("404.html");
            } catch (IOException ex) {
                BrowserMonkeyLogger.warning("File read error: "+path);
                // TODO: Make alternative error page for file read errors.
                //load("404.html");
            }
        }
        
        Renderer r = new Renderer(new DocumentLinker(this));
        rootRenderNode = r.renderRoot(document.getNodeTree(), zoomLevel);

        verticalGroup.addComponent(rootRenderNode);
        horizontalGroup.addComponent(rootRenderNode);

        title = r.getTitle();
        
        // Store the html output into the clipboard for debug.
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable transferableText = new StringSelection(document.getNodeTree().toDebugString());
		systemClipboard.setContents(transferableText, null);

        changed();
        revalidate();
        repaint();
    }

    public String getAddress() {
        return document.getPath();
    }

    public void setZoomLevel(float zoomLevel) {
        rootRenderNode.setZoomLevel(zoomLevel);
        this.zoomLevel = zoomLevel;
        revalidate();
        repaint();
    }

    public void setSearch(String term) {
        ArrayList<AttributedString> textRanges = new ArrayList<AttributedString>();
        Map<AttributedCharacterIterator.Attribute, Object> highlightAttributes = new HashMap<AttributedCharacterIterator.Attribute, Object>();
        //highlightAttributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        highlightAttributes.put(TextAttribute.BACKGROUND, new Color(0x38D878));
        rootRenderNode.extractTextInto(textRanges);
        Searcher.highlightSearchTerm(textRanges.toArray(new AttributedString[textRanges.size()]), term, highlightAttributes);
        revalidate();
        repaint();
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