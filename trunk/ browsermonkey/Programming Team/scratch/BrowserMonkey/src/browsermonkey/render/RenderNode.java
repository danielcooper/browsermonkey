package browsermonkey.render;

import javax.swing.*;

/**
 * Provides a base for the nodes used to render the document.
 * @author Paul Calcraft
 */
public abstract class RenderNode extends JComponent {
    protected Linkable linker;

    public RenderNode(Linkable linker) {
        this.linker = linker;
    }

    public void setZoomLevel(float zoomLevel) {}
}
