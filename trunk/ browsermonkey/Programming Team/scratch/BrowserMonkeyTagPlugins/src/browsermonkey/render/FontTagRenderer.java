package browsermonkey.render;

import browsermonkey.document.*;
import browsermonkey.utility.BrowserMonkeyLogger;
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
        String color = tag.getAttribute("color");
        String face = tag.getAttribute("face");

        if (color != null) {
            Color colour;
            if (color.charAt(0) == '#') {
                try {
                    int colourValue = Integer.parseInt(color.substring(1), 16);
                    if (colourValue > 0xFFFFFF || colourValue < 0x0)
                        colour = null;
                    else
                        colour = new Color(colourValue);
                } catch (NumberFormatException ex) {
                    colour = null;
                }
            }
            else
                colour = getNamedColour(color);

            if (colour != null) {
                newFormatting.put(TextAttribute.FOREGROUND, colour);
            }
            else {
                BrowserMonkeyLogger.conformance("Invalid color attribute value \""+color+"\" in font tag.");
                renderer.foundConformanceError();
            }
        }

        if (face != null) {
            newFormatting.put(TextAttribute.FAMILY, face);
        }

        for (DocumentNode child : tag.getChildren())
            renderer.render(child, parent, newFormatting);
    }
}