package browsermonkey.render;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.ArrayList;
import java.util.regex.*;

/**
 * Renders formatted text with word wrap.
 * @author Paul Calcraft
 */
public class TextRenderNode extends RenderNode {
    private AttributedString text;
    private String textString;
    private boolean centred;
    private boolean dimensionsChanged = true;
    private Map<Rectangle, TextLayout> textLayouts;

    private static Map<String, Character> characterEntities;
    static {
        characterEntities = new HashMap<String, Character>();
        characterEntities.put("nbsp", '\u00A0');
        characterEntities.put("pound", '£');
        characterEntities.put("copy", '©');
        characterEntities.put("reg", '®');
        characterEntities.put("trade", '™');
        characterEntities.put("quot", '"');
        characterEntities.put("apos", '\'');
        characterEntities.put("amp", '&');
        characterEntities.put("lt", '<');
        characterEntities.put("gt", '>');
        characterEntities.put("bull", '•');
        characterEntities.put("raquo", '▼');
        characterEntities.put("para", '¶');
        characterEntities.put("frac14", '¼');
        characterEntities.put("frac12", '½');
        characterEntities.put("frac34", '¾');
        characterEntities.put("ntilde", 'ñ');
        characterEntities.put("hellip", '…');
        //characterEntities.put("", '');
    }

    public TextRenderNode(Linkable linker) {
        this(linker, false);
    }

    /**
     * Constructs a <code>TextRenderComponent</code> with the specified text.
     * @param linker
     * @param centred
     */
    public TextRenderNode(Linkable linker, boolean centred) {
        super(linker);

        this.centred = centred;
        textString = "";
        text = new AttributedString(textString);
        
        addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                click(new Point(e.getX(), e.getY()));
            }

            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
        });
    }

    @Override
    public void setZoomLevel(float zoomLevel) {
        if (isEmpty())
            return;

        int screenResolution = Toolkit.getDefaultToolkit().getScreenResolution();
        double dpiCorrection = screenResolution/72d;
        text.addAttribute(TextAttribute.TRANSFORM, new TransformAttribute(AffineTransform.getScaleInstance(zoomLevel*dpiCorrection, zoomLevel*dpiCorrection)));
        dimensionsChanged = true;
    }

    @Override
    public void extractTextInto(ArrayList<AttributedString> text) {
        if (!isEmpty())
            text.add(this.text);
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
                        int hrefStart = aci.getRunStart(HREF_ATTRIBUTE);
                        int hrefEnd = aci.getRunLimit(HREF_ATTRIBUTE);
                        text.addAttribute(TextAttribute.FOREGROUND, Color.red, hrefStart, hrefEnd);
                        repaint();
                        linker.followLink((String)hrefValue);
                    }
                    return;
                }
                
            }
            cumulativeCharacterCount += lineLayout.getCharacterCount();
        }
    }

    public boolean isEmpty() {
        return textString.isEmpty();
    }

    public void addText(String newText, Map<Attribute,Object> formatting) {
        if (newText.isEmpty())
            return;
        
        ArrayList<Integer> endIndices = new ArrayList<Integer>();
        ArrayList<Map<Attribute,Object>> formats = new ArrayList<Map<Attribute,Object>>();
        AttributedCharacterIterator it = text.getIterator();
        StringBuilder builder = new StringBuilder();

        int previousEndIndex = 0;
        for (char c = it.first(); c != CharacterIterator.DONE; c = it.next()) {
            if (it.getRunLimit() > previousEndIndex) {
                previousEndIndex = it.getRunLimit();
                endIndices.add(previousEndIndex);
                formats.add(it.getAttributes());
            }
        }

        builder.append(textString);

        if (newText.startsWith(" ") && (isEmpty() || textString.endsWith(" ")))
            newText = newText.substring(1);
        
        if (newText.isEmpty())
            return;

        int currentPos = 0;
        int findPos;
        while ((findPos = newText.indexOf('&', currentPos)) != -1) {
            int endEntityIndex = newText.indexOf(';', findPos+1);
            if (endEntityIndex == -1)
                break;
            builder.append(newText.substring(currentPos, findPos));
            boolean entityReplaced = false;
            String entityText = newText.substring(findPos+1, endEntityIndex).toLowerCase();
            if (entityText.charAt(0) == '#') {
                try {
                    int value;
                    if (entityText.charAt(1) == 'x')
                        value = Integer.parseInt(entityText.substring(2), 16);
                    else
                        value = Integer.parseInt(entityText.substring(1));
                    builder.append((char)value);
                    entityReplaced = true;
                } catch (NumberFormatException ex) {
                    entityReplaced = false;
                }
            }
            else {
                Character character = characterEntities.get(entityText);
                if (character != null) {
                    builder.append(character);
                    entityReplaced = true;
                }
            }

            if (entityReplaced)
                currentPos = endEntityIndex+1;
            else {
                currentPos = findPos+1;
                builder.append('&');
            }
        }
        
        builder.append(newText.substring(currentPos));
        endIndices.add(builder.length());
       
        formats.add(formatting);

        textString = builder.toString();
        text = new AttributedString(textString);
        
        previousEndIndex = 0;
        for (int i = 0; i < endIndices.size(); i++) {
            text.addAttributes(formats.get(i), previousEndIndex, endIndices.get(i));
            previousEndIndex = endIndices.get(i);
        }
    }

    private static Pattern newLinePattern = Pattern.compile("\\r\\n|\\n\\r|\\r|\\n");
    private static Pattern breakableStringPattern = Pattern.compile("\\s+");

    @Override
    public void paint(Graphics g) {
        if (isEmpty()) {
            if (dimensionsChanged) {
                Dimension newDimension = new Dimension(0, 0);
                setMinimumSize(newDimension);
                setPreferredSize(newDimension);
                setMaximumSize(newDimension);
            }
            return;
        }

        // If possible, enable text antialiasing according to system settings
        if (g instanceof Graphics2D) {
            Toolkit tk = Toolkit.getDefaultToolkit();
            Map map = (Map)(tk.getDesktopProperty("awt.font.desktophints"));
            if (map != null) {
                ((Graphics2D)g).addRenderingHints(map);
            }
            else
                ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }

        AttributedCharacterIterator it = text.getIterator();
        LineBreakMeasurer lineBreaker = new LineBreakMeasurer(it, g.getFontMetrics().getFontRenderContext());

        textLayouts = new LinkedHashMap<Rectangle, TextLayout>();

        Matcher hardLineBreakFinder = newLinePattern.matcher(textString);

        ArrayList<Integer> hardLineBreaks = new ArrayList<Integer>();

        while (!hardLineBreakFinder.hitEnd()) {
            if (!hardLineBreakFinder.find())
                break;
            hardLineBreaks.add(hardLineBreakFinder.start());
        }
        hardLineBreaks.add(textString.length());

        int hardLineIndex = 0;
        Point coord = new Point(0, 0);
        float wrappingWidth = getWidth();
        TextLayout layout = null;
        boolean failedToWrap = false;
        while (lineBreaker.getPosition() < it.getEndIndex()) {
            if (lineBreaker.getPosition() > hardLineBreaks.get(hardLineIndex))
                hardLineIndex++;
            layout = lineBreaker.nextLayout(wrappingWidth, hardLineBreaks.get(hardLineIndex)+1, true);
            if (layout == null) {
                failedToWrap = true;
                break;
            }
            coord.y += layout.getAscent() ;
            float dx;
            if (centred)
                dx = (wrappingWidth-layout.getAdvance())/2f;
            else
                dx = layout.isLeftToRight() ? 0 : (wrappingWidth - layout.getAdvance());
            layout.draw((Graphics2D)g, coord.x + dx, coord.y);
            textLayouts.put(layout.getPixelBounds(null, coord.x + dx, coord.y), layout);
            coord.y += layout.getDescent() + layout.getLeading();
        }
        //if (layout != null)
        //    coord.y -= layout.getLeading();
        //g.clearRect(0, 30, 300, 30);
        //g.drawString("Preferred: "+getPreferredSize().toString(), 0, 50);
        if (dimensionsChanged) {
            // If the width has changed since the last render, the height needs
            // to be updated to fit the wrapped text. We set 0 as the preferred
            // width so it can defer width control to its parent/layout manager.

            TextMeasurer measurer = new TextMeasurer(it, g.getFontMetrics().getFontRenderContext());

            int longestSingleWord = 0;

            Matcher softLineBreakFinder = breakableStringPattern.matcher(textString);

            int previousBreak = 0;
            while (previousBreak < textString.length()) {
                int currentBreak = textString.length();
                int currentBreakEnd = textString.length();

                if (softLineBreakFinder.find()) {
                    currentBreak = softLineBreakFinder.start();
                    currentBreakEnd = softLineBreakFinder.end();
                }

                if (currentBreak > previousBreak) {
                    int pixelLength = (int)Math.ceil(measurer.getAdvanceBetween(previousBreak, currentBreak));
                    longestSingleWord = Math.max(longestSingleWord, pixelLength);
                }
                previousBreak = currentBreakEnd;
            }

            int maximumWidth = 0;
            int minimumHeight = 0;
            int previousLineBreakIndex = 0;
            for (Integer lineBreakIndex : hardLineBreaks) {
                if (lineBreakIndex > previousLineBreakIndex) {
                    TextLayout singleLineLayout = measurer.getLayout(previousLineBreakIndex, lineBreakIndex);
                    maximumWidth = Math.max(maximumWidth, (int)Math.ceil(singleLineLayout.getAdvance()));
                    minimumHeight += (int)Math.ceil(singleLineLayout.getAscent());
                }
                previousLineBreakIndex = lineBreakIndex;
            }
            
            int maximumHeight = Short.MAX_VALUE;

            if (!failedToWrap) {
                minimumHeight = coord.y;
                maximumHeight = coord.y;
            }

            setMinimumSize(new Dimension(longestSingleWord, minimumHeight));
            setMaximumSize(new Dimension(maximumWidth, maximumHeight));

            dimensionsChanged = false;

            revalidate();
            repaint();
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        // If the bounding width changes, we need to set widthChanged so the
        // paint method knows to reset the height when it word wraps.
        if (this.getBounds().width != width) {
            dimensionsChanged = true;
        }
        super.setBounds(x, y, width, height);
    }
}