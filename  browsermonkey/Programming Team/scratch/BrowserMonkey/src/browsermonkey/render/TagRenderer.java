package browsermonkey.render;

import java.util.Map;
import browsermonkey.document.TagDocumentNode;
import java.text.AttributedCharacterIterator.Attribute;

/**
 *
 * @author prtc20
 */
public abstract class TagRenderer {
    public abstract void render(Renderer renderer, TagDocumentNode tag, LayoutRenderNode parent, Map<Attribute,Object> formatting);
}
