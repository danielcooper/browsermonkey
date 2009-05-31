package browsermonkey.render;

import browsermonkey.document.*;
import java.text.AttributedCharacterIterator.Attribute;
import java.awt.font.*;
import java.util.*;

/**
 *
 * @author Daniel Cooper dc92
 */
public class ItalicsTagRenderer extends TagRenderer {

    public ItalicsTagRenderer(Linkable linker) {
        super(linker);
    }
    
    @Override
    public void render(Renderer renderer, TagDocumentNode tag, LayoutRenderNode parent, Map<Attribute, Object> formatting) {
        Map<Attribute, Object> newFormatting = (Map<Attribute, Object>)((HashMap)formatting).clone();
        newFormatting.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
        for (DocumentNode child : tag.getChildren())
            renderer.render(child, parent, newFormatting);
    }
}