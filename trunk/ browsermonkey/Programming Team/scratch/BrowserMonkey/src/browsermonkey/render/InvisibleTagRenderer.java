package browsermonkey.render;

import browsermonkey.document.*;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Map;

/**
 *
 * @author prtc20
 */
public class InvisibleTagRenderer extends TagRenderer {

    @Override
    public void render(Renderer renderer, TagDocumentNode tag, LayoutRenderNode parent, Map<Attribute, Object> formatting) {
        for (DocumentNode child : tag.getChildren())
            renderer.render(child, parent, formatting);
    }
}