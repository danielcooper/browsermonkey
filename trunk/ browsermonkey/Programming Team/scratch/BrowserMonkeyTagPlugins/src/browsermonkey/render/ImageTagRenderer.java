package browsermonkey.render;

import browsermonkey.document.*;
import java.awt.*;
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

        byte[] imageResource = renderer.loadResource(src);
        if (imageResource == null)
            return;

        ImageRenderNode img = new ImageRenderNode(linker, imageResource);

        if (img.isImageValid())
            parent.addNode(img, LayoutRenderNode.WidthBehaviour.Maximal);
    }

    private static class ImageRenderNode extends RenderNode {
        private Image image;

        public boolean isImageValid() {
            return image != null;
        }

        public ImageRenderNode(Linkable linker, byte[] imageResource) {
            super(linker);
            try {
                image = ImageIO.read(new ByteArrayInputStream(imageResource));

                updateSizes(1);
            } catch (IOException ex) {
                image = null;
            }
        }
        
        private void updateSizes(float zoom) {
            int width = Math.round(image.getWidth(null)*zoom);
            int height = Math.round(image.getHeight(null)*zoom);
            Dimension size = new Dimension(width, height);
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);
            revalidate();
            //repaint();
        }

        @Override
        public void setZoomLevel(float zoomLevel) {
            if (image == null)
                return;
            updateSizes(zoomLevel);
        }

        @Override
        public void paint(Graphics g) {
            if (image != null)
                g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        }
    }
}