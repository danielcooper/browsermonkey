package browsermonkey.render;

import browsermonkey.document.*;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.*;

/**
 *
 * @author Paul Calcraft
 */
public class CenterTagRenderer extends TagRenderer {

    public CenterTagRenderer(Linkable linker) {
        super(linker);
    }

    @Override
    public void render(Renderer renderer, TagDocumentNode tag, LayoutRenderNode parent, Map<Attribute, Object> formatting) {
        LayoutRenderNode div = new LayoutRenderNode(linker, true);
        for (DocumentNode child : tag.getChildren())
            renderer.render(child, div, formatting);
        parent.addNode(div);
    }
}