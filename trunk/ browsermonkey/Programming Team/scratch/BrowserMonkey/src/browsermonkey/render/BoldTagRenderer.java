package browsermonkey.render;

import browsermonkey.document.*;
import java.text.AttributedCharacterIterator.Attribute;
import java.awt.font.*;
import java.util.*;

/**
 *
 * @author prtc20
 */
public class BoldTagRenderer extends TagRenderer {

    @Override
    public void render(Renderer renderer, TagDocumentNode tag, LayoutRenderNode parent, Map<Attribute, Object> formatting) {
        Map<Attribute, Object> newFormatting = (Map<Attribute, Object>)((Hashtable)formatting).clone();
        newFormatting.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        for (DocumentNode child : tag.getChildren())
            renderer.render(child, parent, newFormatting);
    }
}