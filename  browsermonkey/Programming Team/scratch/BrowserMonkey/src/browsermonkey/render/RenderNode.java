package browsermonkey.render;

import java.text.AttributedString;
import java.util.ArrayList;
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

    public void extractTextInto(ArrayList<AttributedString> text) {}
}
