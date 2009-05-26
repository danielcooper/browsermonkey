package browsermonkey.render;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
    //private ArrayList<TextLayout> textLayouts;
    private Map<Rectangle, TextLayout> textLayouts;

    public TextRenderNode(Linkable linker) {
        this(linker, "");
    }

    /**
     * Constructs a <code>TextRenderComponent</code> with the specified text.
     * @param linker
     * @param text
     */
    public TextRenderNode(Linkable linker, String text) {
        super(linker);
        this.text = new AttributedString(text);
        this.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                click(new Point(e.getX(), e.getY()));
            }

            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
        });
    }

    public static final AttributedCharacterIterator.Attribute HREF_ATTRIBUTE = new AttributedCharacterIterator.Attribute("href") {};

    private void click(Point hitPoint) {
        int cumulativeCharacterCount = 0;
        Set<Map.Entry<Rectangle, TextLayout>> textLines = textLayouts.entrySet();
        for (Map.Entry<Rectangle, TextLayout> line : textLines) {
            Rectangle lineRect = line.getKey();
            TextLayout lineLayout = line.getValue();
            if (lineRect.contains(hitPoint)) {
                TextHitInfo hitInfo = lineLayout.hitTestChar(hitPoint.x-lineRect.x, hitPoint.y-lineRect.y);
                if (hitInfo != null) {
                    AttributedCharacterIterator aci = text.getIterator();
                    aci.setIndex(cumulativeCharacterCount + hitInfo.getCharIndex());
                    Object hrefValue = aci.getAttribute(HREF_ATTRIBUTE);
                    if (hrefValue != null) {
                        linker.followLink((String)hrefValue);
                    }
                }
                
            }
            cumulativeCharacterCount += lineLayout.getCharacterCount();
        }
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

        textLayouts = new LinkedHashMap<Rectangle, TextLayout>();

        Point coord = new Point(0, 0);
        float wrappingWidth = getWidth();
        while (lineBreaker.getPosition() < it.getEndIndex()) {
            TextLayout layout = lineBreaker.nextLayout(wrappingWidth);
            coord.y += (layout.getAscent());
            float dx = layout.isLeftToRight() ? 0 : (wrappingWidth - layout.getAdvance());
            layout.draw((Graphics2D)g, coord.x + dx, coord.y);
            textLayouts.put(layout.getPixelBounds(null, coord.x + dx, coord.y), layout);
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