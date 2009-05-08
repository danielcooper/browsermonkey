package browsermonkey.render;

import java.awt.*;
import java.awt.font.*;
import java.text.*;
import java.util.*;
import java.text.AttributedCharacterIterator.Attribute;

/**
 * Renders formatted text with word wrap.
 * @author Paul Calcraft
 */
public class TextRenderNode extends RenderNode {
    private AttributedString text;
    private boolean widthChanged = true;

    public TextRenderNode() {
        this("");
    }

    /**
     * Constructs a <code>TextRenderComponent</code> with the specified text.
     * @param text
     */
    public TextRenderNode(String text) {
        this.text = new AttributedString(text);
    }

    public void addText(String text, Map<Attribute,Object> formatting) {
        ArrayList<Integer> endIndices = new ArrayList<Integer>();
        ArrayList<Map<Attribute,Object>> formats = new ArrayList<Map<Attribute,Object>>();
        AttributedCharacterIterator it = this.text.getIterator();
        StringBuilder builder = new StringBuilder();

        int previousEndIndex = 0;
        for (char c = it.first(); c != CharacterIterator.DONE; c = it.next()) {
            builder.append(c);
            if (it.getRunLimit() > previousEndIndex) {
                previousEndIndex = it.getRunLimit();
                endIndices.add(previousEndIndex);
                formats.add(it.getAttributes());
            }
        }

        builder.append(text);
        endIndices.add(previousEndIndex + text.length());
        formats.add(formatting);

        this.text = new AttributedString(builder.toString());
        
        previousEndIndex = 0;
        for (int i = 0; i < endIndices.size(); i++) {
            this.text.addAttributes(formats.get(i), previousEndIndex, endIndices.get(i));
            previousEndIndex = endIndices.get(i);
        }
    }

    @Override
    public void paint(Graphics g) {
        AttributedCharacterIterator it = text.getIterator();
        LineBreakMeasurer lineBreaker = new LineBreakMeasurer(it, g.getFontMetrics().getFontRenderContext());

        Point coord = new Point(0, 0);
        float wrappingWidth = getWidth();
        while (lineBreaker.getPosition() < it.getEndIndex()) {
            TextLayout layout = lineBreaker.nextLayout(wrappingWidth);
            coord.y += (layout.getAscent());
            float dx = layout.isLeftToRight() ? 0 : (wrappingWidth - layout.getAdvance());
            layout.draw((Graphics2D)g, coord.x + dx, coord.y);
            coord.y += layout.getDescent() + layout.getLeading();
        }
        if (widthChanged) {
            // If the width has changed since the last render, the height needs
            // to be updated to fit the wrapped text. We set 0 as the preferred
            // width so it can defer width control to its parent/layout manager.
            setPreferredSize(new Dimension(0, coord.y));
            widthChanged = false;
            revalidate();
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        // If the bounding width changes, we need to set widthChanged so the
        // paint method knows to reset the height when it word wraps.
        if (this.getBounds().width != width) {
            widthChanged = true;
        }
        super.setBounds(x, y, width, height);
    }
}