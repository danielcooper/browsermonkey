package browsermonkey.render.components;

import java.awt.*;
import java.awt.font.*;
import javax.swing.*;
import java.text.*;

/**
 *
 * @author Paul Calcraft
 */
public class TextRenderComponent extends JComponent {
    private String text;
    private boolean widthChanged = true;
    
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
            setPreferredSize(new Dimension(0, coord.y));
            widthChanged = false;
            revalidate();
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        if (this.getBounds().width != width) {
            widthChanged = true;
        }
        super.setBounds(x, y, width, height);
    }
}