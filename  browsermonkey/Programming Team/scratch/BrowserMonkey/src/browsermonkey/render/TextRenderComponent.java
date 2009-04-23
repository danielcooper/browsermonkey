package browsermonkey.render;

import java.awt.*;
import java.awt.font.*;
import javax.swing.*;
import java.text.*;

/**
 * Renders unformatted text with word wrap.
 * @author Paul Calcraft
 */
public class TextRenderComponent extends JComponent {
    private String text;
    private boolean widthChanged = true;
    
    /**
     * Constructs a <code>TextRenderComponent</code> with the specified text.
     * @param text
     */
    public TextRenderComponent(String text) {
        this.text = text;
    }

    @Override
    public void paint(Graphics g) {
        AttributedCharacterIterator it = new AttributedString(text).getIterator();
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