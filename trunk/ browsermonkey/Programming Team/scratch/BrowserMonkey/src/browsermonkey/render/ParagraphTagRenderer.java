package browsermonkey.render;

import browsermonkey.document.*;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.*;

/**
 *
 * @author Paul Calcraft
 */
public class ParagraphTagRenderer extends TagRenderer {

    public ParagraphTagRenderer(Linkable linker) {
        super(linker);
    }

    @Override
    public void render(Renderer renderer, TagDocumentNode tag, LayoutRenderNode parent, Map<Attribute, Object> formatting) {
        parent.addLineBreaks(2);
        for (DocumentNode child : tag.getChildren())
            renderer.render(child, parent, formatting);
    }
}