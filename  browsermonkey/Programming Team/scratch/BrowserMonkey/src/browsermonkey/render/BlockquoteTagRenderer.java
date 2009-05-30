package browsermonkey.render;

import browsermonkey.document.*;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.*;

/**
 *
 * @author Paul Calcraft
 */
public class BlockquoteTagRenderer extends TagRenderer {
    public BlockquoteTagRenderer(Linkable linker) {
        super(linker);
    }

    @Override
    public void render(Renderer renderer, TagDocumentNode tag, LayoutRenderNode parent, Map<Attribute, Object> formatting) {
        LayoutRenderNode div = new LayoutRenderNode(linker);
        div.setPadding(40, 40, 20, 20);
        for (DocumentNode child : tag.getChildren())
            renderer.render(child, div, formatting);
        parent.ensureNewLine();
        parent.addNode(div);
    }
}