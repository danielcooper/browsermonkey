package browsermonkey.render;

import browsermonkey.document.*;
import java.text.AttributedCharacterIterator.Attribute;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;

/**
 *
 * @author Paul Calcraft
 */
public class HrTagRenderer extends TagRenderer {

    public HrTagRenderer(Linkable linker) {
        super(linker);
    }
    
    @Override
    public void render(Renderer renderer, TagDocumentNode tag, LayoutRenderNode parent, Map<Attribute, Object> formatting) {
        HrRenderNode hrNode = new HrRenderNode(linker);

        parent.ensureNewLine();
        parent.addNode(hrNode, LayoutRenderNode.WidthBehaviour.Grow);
    }

    private static class HrRenderNode extends LayoutRenderNode {
        public HrRenderNode(Linkable linker) {
            super(linker);
            this.setBorder(new LineBorder(Color.BLACK));
            this.setMinimumSize(new Dimension(0, 15));
            this.setMaximumSize(new Dimension(Short.MAX_VALUE, 15));
        }

        @Override
        protected void paintBorder(Graphics g) {
            Dimension size = this.getSize();
                g.drawLine(0, size.height/2, size.width, size.height/2);
        }        
    }
}