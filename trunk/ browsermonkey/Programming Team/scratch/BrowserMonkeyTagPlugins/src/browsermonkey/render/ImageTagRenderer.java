package browsermonkey.render;

import browsermonkey.document.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.*;
import javax.imageio.*;

/**
 *
 * @author Paul Calcraft
 */
public class ImageTagRenderer extends TagRenderer {
    public ImageTagRenderer(Linkable linker) {
        super(linker);
    }

    @Override
    public void render(Renderer renderer, TagDocumentNode tag, LayoutRenderNode parent, Map<Attribute, Object> formatting) {
        parent.ensureNewLine();
        
        String src = tag.getAttribute("src");
        if (src == null)
            return;

        String hrefValue = (String)formatting.get(TextRenderNode.HREF_ATTRIBUTE);

        byte[] imageResource = renderer.loadResource(src);
        ImageRenderNode img = new ImageRenderNode(linker, imageResource, hrefValue);

        parent.addNode(img, LayoutRenderNode.WidthBehaviour.Maximal);
    }

    private static class ImageRenderNode extends RenderNode {
        private Image image;
        private static Image redX;
        private String link;

        static {
            try {
                InputStream stream = ImageRenderNode.class.getResourceAsStream("/resources/redx.gif");
                if (stream != null) {
                    redX = ImageIO.read(stream);
                    stream.close();
                }
            } catch (IOException ex) {
                redX = null;
            }
        }

        public boolean isImageValid() {
            return image != null;
        }

        public ImageRenderNode(Linkable linker, byte[] imageResource, String link) {
            super(linker);

            this.link = link;

            if (imageResource == null)
                image = null;
            else {
                try {
                    image = ImageIO.read(new ByteArrayInputStream(imageResource));
                } catch (IOException ex) {
                    image = null;
                }
            }

            addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                    click();
                }

                public void mouseEntered(MouseEvent e) {}
                public void mouseExited(MouseEvent e) {}
                public void mousePressed(MouseEvent e) {}
                public void mouseReleased(MouseEvent e) {}
            });
            
            updateSizes(1);
        }
        
        private void updateSizes(float zoom) {
            int width = 0;
            int height = 0;
            
            if (image != null) {
                width = Math.round(image.getWidth(null)*zoom);
                height = Math.round(image.getHeight(null)*zoom);
            }
            else if (redX != null) {
                width = Math.round(redX.getWidth(null));
                height = Math.round(redX.getHeight(null));
            }

            Dimension size = new Dimension(width, height);
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);

            revalidate();
        }

        @Override
        public void setZoomLevel(float zoomLevel) {
            updateSizes(zoomLevel);
        }

        @Override
        public void paint(Graphics g) {
            if (image != null)
                g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
            else if (redX != null)
                g.drawImage(redX, 0, 0, redX.getWidth(null), redX.getHeight(null), null);
        }

        private void click() {
            if (link != null)
                linker.followLink(link);
        }
    }
}