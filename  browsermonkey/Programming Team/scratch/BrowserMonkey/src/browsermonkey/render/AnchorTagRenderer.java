package browsermonkey.render;

import browsermonkey.document.*;
import java.text.AttributedCharacterIterator.Attribute;
import java.awt.*;
import java.awt.font.*;
import java.util.*;

/**
 *
 * @author Paul Calcraft
 */
public class AnchorTagRenderer extends TagRenderer {

    public AnchorTagRenderer(Linkable linker) {
        super(linker);
    }
    
    @Override
    public void render(Renderer renderer, TagDocumentNode tag, LayoutRenderNode parent, Map<Attribute, Object> formatting) {
        Map<Attribute, Object> newFormatting = formatting;
        String href = tag.getAttributes().get("href");
        if (href != null) {
            newFormatting = (Map<Attribute, Object>)((Hashtable)formatting).clone();
            newFormatting.put(TextAttribute.FOREGROUND, Color.blue);
            newFormatting.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            newFormatting.put(TextRenderNode.HREF_ATTRIBUTE, href);
        }

        for (DocumentNode child : tag.getChildren())
            renderer.render(child, parent, newFormatting);
    }
}