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
    private String title = null;
    public static final Map<Attribute,Object> DEFAULT_FORMATTING;

    static {
        DEFAULT_FORMATTING = new HashMap<Attribute,Object>();
        DEFAULT_FORMATTING.put(TextAttribute.SIZE, 12f);
        DEFAULT_FORMATTING.put(TextAttribute.FAMILY, "Times New Roman");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Renderer(Linkable linker) {
        this.linker = linker;
        rendererMap = new HashMap<String, TagRenderer>();
        rendererMap.put("b", new BoldTagRenderer(linker));
        rendererMap.put("i", new ItalicsTagRenderer(linker));
        rendererMap.put("table", new TableTagRenderer(linker));
        rendererMap.put("a", new AnchorTagRenderer(linker));
        rendererMap.put("br", new LineBreakTagRenderer(linker));
        rendererMap.put("p", new ParagraphTagRenderer(linker));
        rendererMap.put("font", new FontTagRenderer(linker));
        rendererMap.put("title", new TitleTagRenderer(linker));
        rendererMap.put("ol", new OrderedListRenderer(linker));
        rendererMap.put("ul", new BulletListRenderer(linker));
        rendererMap.put("center", new CenterTagRenderer(linker));
    }

    public LayoutRenderNode renderRoot(DocumentNode root, float zoom) {
        LayoutRenderNode renderRoot = new LayoutRenderNode(linker);

        render(root, renderRoot, DEFAULT_FORMATTING);

        renderRoot.setZoomLevel(zoom);

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