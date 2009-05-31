package browsermonkey.render;

import browsermonkey.document.*;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.*;

/**
 *
 * @author Paul Calcraft
 */
public class HeadingTagRenderer extends TagRenderer {
    public HeadingTagRenderer(Linkable linker) {
        super(linker);
    }

    @Override
    public void render(Renderer renderer, TagDocumentNode tag, LayoutRenderNode parent, Map<Attribute, Object> formatting) {
        Map<Attribute, Object> newFormatting = (Map<Attribute, Object>)((HashMap)formatting).clone();
        newFormatting.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);

        parent.ensureLinespaceDistance(1);

        int headingLevel = Integer.parseInt(tag.getType().substring(1)) - 1;

        LayoutRenderNode headingTextLayoutNode = new LayoutRenderNode(linker);
        TextRenderNode headingNumberNode = new TextRenderNode(linker);
        headingNumberNode.addText(renderer.getHeadingString(headingLevel)+"&nbsp;", newFormatting);
        headingTextLayoutNode.addNodePadding(headingNumberNode, null);

        for (DocumentNode child : tag.getChildren())
            renderer.render(child, headingTextLayoutNode, newFormatting);

        parent.addNode(headingTextLayoutNode, LayoutRenderNode.WidthBehaviour.Maximal);

        parent.ensureLinespaceDistance(1);
    }
}