package browsermonkey.render;

import browsermonkey.document.*;
import browsermonkey.utility.BrowserMonkeyLogger;
import java.util.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
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
    private Renderer renderer;
    private URL context;
    private LoaderThread currentLoaderThread = null;

    public String getTitle() {
        return title;
    }

    /**
     * Constructs a <code>DocumentPanel</code> by initialising the layout groups.
     */
    public DocumentPanel() {
        this.setBackground(Color.white);

        try {
            context = new File(System.getProperty("user.dir")).toURI().toURL();
        } catch (MalformedURLException ex) {
            context = null;
        }

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

        renderer = new Renderer(new DocumentLinker(this));
    }
    
    private class LoaderThread extends SwingWorker<Void, Integer> {
        private String path;
        private boolean absolute;

        public LoaderThread(String path, boolean absolute) {
            this.path = path;
            this.absolute = absolute;
        }
        
        @Override
        protected Void doInBackground() {
            if (absolute) {
                try {
                    context = new File(System.getProperty("user.dir")).toURI().toURL();
                } catch (MalformedURLException ex) {
                    context = null;
                }
                context = null;
            }

            document = new Document(path, context);
            if (path.startsWith("t "))
                document.loadTest(path.substring(2));
            else {
                try {
                    document.load();
                    context = document.getURL();
                } catch (FileNotFoundException ex) {
                    BrowserMonkeyLogger.warning("File not found: "+path);
                    // TODO: load 404 document
                } catch (IOException ex) {
                    BrowserMonkeyLogger.warning("File read error: "+path);
                }
            }

            // Store the html output into the clipboard for debug.
            try {
                Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                Transferable transferableText = new StringSelection(document.getNodeTree().toDebugString());
                systemClipboard.setContents(transferableText, null);
            } catch (IllegalStateException ex) {
                BrowserMonkeyLogger.warning("Couldn't write debug parse information to clipboard.");
            }

            rootRenderNode = renderer.renderRoot(document.getNodeTree(), zoomLevel, context);

            removeAll();
            verticalGroup.addComponent(rootRenderNode);
            horizontalGroup.addComponent(rootRenderNode);

            title = renderer.getTitle();

            changed();
            revalidate();
            repaint();

            Thread.yield();
            
            if (document.isIsConformant() && renderer.isConformant())
                BrowserMonkeyLogger.status("Done, page appears to conform to the specification.");
            else
                BrowserMonkeyLogger.status("Done, page does not conform to the specification. See log file for details.");

            currentLoaderThread = null;

            return null;
        }
    }

    public void load(String path) {
        load(path, false);
    }

    public void load(String path, boolean absolute) {
        if (currentLoaderThread != null)
            currentLoaderThread.cancel(true);
        currentLoaderThread = new LoaderThread(path, absolute);
        currentLoaderThread.execute();
    }

    public String getAddress() {
        URL url = document.getURL();
        if (url == null)
            return "Null address.";
        return url.toString();
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
        highlightAttributes.put(TextAttribute.BACKGROUND, new Color(0x38D878));
        rootRenderNode.extractTextInto(textRanges);
        int resultCount = Searcher.highlightSearchTerm(textRanges.toArray(new AttributedString[textRanges.size()]), term, highlightAttributes);
        String foundStatus;
        switch (resultCount) {
            case 0:
                foundStatus = "Could not find \""+term+"\" in the document.";
                break;
            case 1:
                foundStatus = "Found \""+term+"\" once in the document.";
                break;
            default:
                foundStatus = "Found \""+term+"\" "+resultCount+" times in the document.";
        }
        BrowserMonkeyLogger.status(foundStatus);
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