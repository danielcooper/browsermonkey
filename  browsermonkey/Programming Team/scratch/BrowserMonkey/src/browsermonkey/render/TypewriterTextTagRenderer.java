package browsermonkey.render;

import browsermonkey.document.*;
import java.text.AttributedCharacterIterator.Attribute;
import java.awt.font.*;
import java.util.*;

/**
 *
 * @author Paul Calcraft
 */
public class TypewriterTextTagRenderer extends TagRenderer {
    public TypewriterTextTagRenderer(Linkable linker) {
        super(linker);
    }

    @Override
    public void render(Renderer renderer, TagDocumentNode tag, LayoutRenderNode parent, Map<Attribute, Object> formatting) {
        Map<Attribute, Object> newFormatting = (Map<Attribute, Object>)((HashMap)formatting).clone();
        newFormatting.put(TextAttribute.FAMILY, "Courier New");
        newFormatting.put(TextAttribute.SIZE, 10);

        for (DocumentNode child : tag.getChildren())
            renderer.render(child, parent, newFormatting);
    }
}