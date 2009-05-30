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
        Map<Attribute, Object> newFormatting = (Map<Attribute, Object>)((HashMap)formatting).clone();
        String color = tag.getAttributes().get("color");
        String face = tag.getAttributes().get("face");

        if (color != null) {
            Color colour;
            if (color.charAt(0) == '#') {
                colour = new Color(Integer.parseInt(color.substring(1), 16));
                // TODO: Detect bad number formatting
            }
            else
                colour = getNamedColour(color);

            if (colour != null) {
                newFormatting.put(TextAttribute.FOREGROUND, colour);
            }
        }

        if (face != null) {
            newFormatting.put(TextAttribute.FAMILY, face);
        }

        for (DocumentNode child : tag.getChildren())
            renderer.render(child, parent, newFormatting);
    }
}