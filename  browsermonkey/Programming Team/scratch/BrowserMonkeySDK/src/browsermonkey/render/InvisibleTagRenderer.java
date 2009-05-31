package browsermonkey.render;

import browsermonkey.document.*;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Map;

/**
 *
 * @author Paul Calcraft
 */
public class InvisibleTagRenderer extends TagRenderer {
    public InvisibleTagRenderer(Linkable linker) {
        super(linker);
    }

    @Override
    public void render(Renderer renderer, TagDocumentNode tag, LayoutRenderNode parent, Map<Attribute, Object> formatting) {
        for (DocumentNode child : tag.getChildren())
            renderer.render(child, parent, formatting);
    }
}