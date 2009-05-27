package browsermonkey.render;

import java.util.*;
import browsermonkey.document.*;
import java.text.AttributedCharacterIterator.Attribute;
import java.awt.font.*;

/**
 *
 * @author Paul Calcraft
 */
public class Renderer {
    private Map<String, TagRenderer> rendererMap;
    private Linkable linker;

    public Renderer(Linkable linker) {
        this.linker = linker;
        rendererMap = new Hashtable<String, TagRenderer>();
        rendererMap.put("b", new BoldTagRenderer(linker));
        rendererMap.put("i", new ItalicsTagRenderer(linker));
        rendererMap.put("table", new TableTagRenderer(linker));
        rendererMap.put("a", new AnchorTagRenderer(linker));
        rendererMap.put("br", new LineBreakTagRenderer(linker));
        rendererMap.put("p", new ParagraphTagRenderer(linker));
    }

    public LayoutRenderNode renderRoot(DocumentNode root, float zoom) {
        LayoutRenderNode renderRoot = new LayoutRenderNode(linker);

        Map<Attribute,Object> formatting = new Hashtable<Attribute,Object>();
        formatting.put(TextAttribute.SIZE, Math.round(12 * zoom));

        render(root, renderRoot, formatting);

        return renderRoot;
    }

    public void render(DocumentNode node, LayoutRenderNode parent, Map<Attribute,Object> formatting) {
        if (node instanceof TextDocumentNode) {
            TextDocumentNode textNode = (TextDocumentNode)node;
            parent.getTextNode().addText(textNode.getText(), formatting);
        }
        else {
            TagDocumentNode tagNode = (TagDocumentNode)node;
            getTagRenderer(tagNode).render(this, tagNode, parent, formatting);
        }
    }

    private TagRenderer getTagRenderer(TagDocumentNode tagNode) {
        TagRenderer renderer = rendererMap.get(tagNode.getType());
        if (renderer == null)
            renderer = new InvisibleTagRenderer(linker);
        return renderer;
    }
}