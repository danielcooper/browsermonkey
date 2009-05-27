package browsermonkey.render;

import browsermonkey.document.*;
import java.text.AttributedCharacterIterator.Attribute;
import java.awt.*;
import java.awt.font.*;
import java.util.*;
import java.lang.reflect.*;

/**
 *
 * @author Paul Calcraft
 */
public class FontTagRenderer extends TagRenderer {

    public FontTagRenderer(Linkable linker) {
        super(linker);
    }

    private Color getNamedColour(String colourName) {
        try {
            Field field = Color.class.getField(colourName.toLowerCase());
            return (Color)field.get(null);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void render(Renderer renderer, TagDocumentNode tag, LayoutRenderNode parent, Map<Attribute, Object> formatting) {
        Map<Attribute, Object> newFormatting = formatting;
        String color = tag.getAttributes().get("color");

        if (color != null) {
            Color colour = getNamedColour(color);
            if (colour != null) {
                newFormatting = (Map<Attribute, Object>)((Hashtable)formatting).clone();
                newFormatting.put(TextAttribute.FOREGROUND, colour);
            }
        }

        for (DocumentNode child : tag.getChildren())
            renderer.render(child, parent, newFormatting);
    }
}