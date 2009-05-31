package browsermonkey.render;

import browsermonkey.render.*;
import java.util.Map;
import browsermonkey.document.TagDocumentNode;
import java.text.AttributedCharacterIterator.Attribute;

/**
 *
 * @author Paul Calcraft
 */
public abstract class TagRenderer {
    protected Linkable linker;

    public TagRenderer(Linkable linker) {
        this.linker = linker;
    }
    
    public abstract void render(Renderer renderer, TagDocumentNode tag, LayoutRenderNode parent, Map<Attribute,Object> formatting);
}
