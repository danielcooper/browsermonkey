package browsermonkey.render;

import browsermonkey.document.*;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.*;

/**
 *
 * @author Paul Calcraft
 */
public abstract class ListTagRenderer extends TagRenderer {
    public ListTagRenderer(Linkable linker) {
        super(linker);
    }

    @Override
    public void render(Renderer renderer, TagDocumentNode tag, LayoutRenderNode parent, Map<Attribute, Object> formatting) {

        LayoutRenderNode listNode = new LayoutRenderNode(linker);

        //listNode.setPadding(40, 0, 0, 0);
        int i = 0;
        for (DocumentNode itemNode : tag.getChildren()) {
            LayoutRenderNode itemLayoutNode = new LayoutRenderNode(linker);
            TextRenderNode listElementNode = new TextRenderNode(linker);
            listElementNode.addText(getListElementText(i), formatting);
            itemLayoutNode.padLeftWithNode(listElementNode);

            for (DocumentNode child : itemNode.getChildren())
                renderer.render(child, itemLayoutNode, formatting);

            listNode.addNode(itemLayoutNode);
            i++;
        }

        parent.ensureNewLine();
        parent.addNode(listNode);
    }

    protected abstract String getListElementText(int index);
}