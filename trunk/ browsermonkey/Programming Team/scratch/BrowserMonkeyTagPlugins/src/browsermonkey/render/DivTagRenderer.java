package browsermonkey.render;

import browsermonkey.document.*;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.*;

/**
 *
 * @author Paul Calcraft
 */
public class DivTagRenderer extends TagRenderer {
    public DivTagRenderer(Linkable linker) {
        super(linker);
    }

    @Override
    public void render(Renderer renderer, TagDocumentNode tag, LayoutRenderNode parent, Map<Attribute, Object> formatting) {
        LayoutRenderNode div = new LayoutRenderNode(linker, false);
        for (DocumentNode child : tag.getChildren())
            renderer.render(child, div, formatting);
        parent.ensureNewLine();
        parent.addNode(div, LayoutRenderNode.WidthBehaviour.Maximal);
    }
}