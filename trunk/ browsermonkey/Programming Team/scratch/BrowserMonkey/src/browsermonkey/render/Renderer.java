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
    private ArrayList<Integer> headingNumbering;
    public static final Map<Attribute,Object> DEFAULT_FORMATTING;
    public static final Map<Attribute,Object> FIXED_DEFAULT_FORMATTING;

    public String getHeadingString(int headingLevel) {
        if (headingLevel >= headingNumbering.size()) {
            for (int i = headingNumbering.size(); i <= headingLevel; i++)
                headingNumbering.add(1);
        }
        else {
            headingNumbering.set(headingLevel, headingNumbering.get(headingLevel)+1);
            for (int i = headingNumbering.size()-1; i > headingLevel; i--)
                headingNumbering.remove(i);
        }

        StringBuilder headingString = new StringBuilder();

        for (Integer i : headingNumbering) {
            headingString.append(i);
            headingString.append('.');
        }

        return headingString.toString();
    }

    static {
        DEFAULT_FORMATTING = new HashMap<Attribute,Object>();
        DEFAULT_FORMATTING.put(TextAttribute.SIZE, 12f);
        DEFAULT_FORMATTING.put(TextAttribute.FAMILY, "Times New Roman");

        FIXED_DEFAULT_FORMATTING = new HashMap<Attribute,Object>();
        FIXED_DEFAULT_FORMATTING.put(TextAttribute.SIZE, 12f);
        FIXED_DEFAULT_FORMATTING.put(TextAttribute.FAMILY, "Times New Roman");
        FIXED_DEFAULT_FORMATTING.put(TextRenderNode.PRE_ATTRIBUTE, true);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Renderer(Linkable linker) {
        this.linker = linker;

        headingNumbering = new ArrayList<Integer>();
        
        rendererMap = new HashMap<String, TagRenderer>();
        rendererMap.put("b", new BoldTagRenderer(linker));
        rendererMap.put("strong", new BoldTagRenderer(linker));
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
        rendererMap.put("blockquote", new BlockquoteTagRenderer(linker));
        rendererMap.put("tt", new TypewriterTextTagRenderer(linker));
        rendererMap.put("pre", new PreTagRenderer(linker));
        rendererMap.put("h1", new HeadingTagRenderer(linker));
        rendererMap.put("h2", new HeadingTagRenderer(linker));
        rendererMap.put("h3", new HeadingTagRenderer(linker));
        rendererMap.put("h4", new HeadingTagRenderer(linker));
        rendererMap.put("h5", new HeadingTagRenderer(linker));
        rendererMap.put("h6", new HeadingTagRenderer(linker));
        rendererMap.put("u", new UnderlineTagRenderer(linker));
    }

    public LayoutRenderNode renderRoot(DocumentNode root, float zoom) {
        headingNumbering.clear();

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