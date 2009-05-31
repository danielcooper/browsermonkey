package browsermonkey.render;

import browsermonkey.document.*;
import java.text.AttributedCharacterIterator;
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

    public static final AttributedCharacterIterator.Attribute INSIDE_LIST_ATTRIBUTE = new AttributedCharacterIterator.Attribute("insideList") {};

    @Override
    public void render(Renderer renderer, TagDocumentNode tag, LayoutRenderNode parent, Map<Attribute, Object> formatting) {
        Map<Attribute, Object> newFormatting = (Map<Attribute, Object>)((HashMap)formatting).clone();
        newFormatting.put(INSIDE_LIST_ATTRIBUTE, true);

        LayoutRenderNode listNode = new LayoutRenderNode(linker);

        int i = 0;
        for (DocumentNode itemNode : tag.getChildren()) {
            // Only render li nodes as list items
            if (!(itemNode instanceof TagDocumentNode) || !((TagDocumentNode)itemNode).getType().equals("li"))
                continue;

            LayoutRenderNode itemLayoutNode = new LayoutRenderNode(linker);
            TextRenderNode listElementNode = new TextRenderNode(linker);
            listElementNode.addText(getListElementText(i), formatting);
            itemLayoutNode.addNodePadding(listElementNode, null);

            for (DocumentNode child : itemNode.getChildren())
                renderer.render(child, itemLayoutNode, newFormatting);

            listNode.addNode(itemLayoutNode, LayoutRenderNode.WidthBehaviour.Maximal);
            i++;
        }

        Boolean insideListAttribute = (Boolean)formatting.get(INSIDE_LIST_ATTRIBUTE);
        if (insideListAttribute == null || !insideListAttribute) {
            parent.ensureLinespaceDistance(1);
        }
        else
            parent.ensureNewLine();
        
        parent.addNode(listNode, LayoutRenderNode.WidthBehaviour.Maximal);
        parent.ensureLinespaceDistance(1);
    }

    protected abstract String getListElementText(int index);
}