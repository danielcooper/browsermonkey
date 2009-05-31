package browsermonkey.render;

import browsermonkey.document.*;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.*;

/**
 *
 * @author Paul Calcraft
 */
public class BlockquoteTagRenderer extends TagRenderer {
    public BlockquoteTagRenderer(Linkable linker) {
        super(linker);
    }

    @Override
    public void render(Renderer renderer, TagDocumentNode tag, LayoutRenderNode parent, Map<Attribute, Object> formatting) {
        LayoutRenderNode div = new LayoutRenderNode(linker);
        TextRenderNode leftNode = renderer.constructIndentTextNode(formatting);
        TextRenderNode rightNode = renderer.constructIndentTextNode(formatting);
        div.addNodePadding(leftNode, rightNode);
        for (DocumentNode child : tag.getChildren())
            renderer.render(child, div, formatting);
        parent.ensureLinespaceDistance(1);
        parent.addNode(div, LayoutRenderNode.WidthBehaviour.Maximal);
        parent.ensureLinespaceDistance(1);
    }
}