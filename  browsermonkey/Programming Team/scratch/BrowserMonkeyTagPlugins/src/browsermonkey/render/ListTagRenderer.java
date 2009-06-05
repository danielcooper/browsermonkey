package browsermonkey.render;

import browsermonkey.document.*;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.*;

/**
 * An abstract base class used for ul and ol list rendering.
 * @author Paul Calcraft
 */
public abstract class ListTagRenderer extends TagRenderer {
    public ListTagRenderer(Linkable linker) {
        super(linker);
    }

    // Attribute to indicate we're inside a list - used for line spacing.
    public static final AttributedCharacterIterator.Attribute INSIDE_LIST_ATTRIBUTE = new AttributedCharacterIterator.Attribute("insideList") {};

    @Override
    public void render(Renderer renderer, TagDocumentNode tag, LayoutRenderNode parent, Map<Attribute, Object> formatting) {
        // Add the inside list attribute.
        Map<Attribute, Object> newFormatting = (Map<Attribute, Object>)((HashMap)formatting).clone();
        newFormatting.put(INSIDE_LIST_ATTRIBUTE, true);

        // Create a new layout node for the list as a whole.
        LayoutRenderNode listNode = new LayoutRenderNode(linker);

        int i = 0;
        for (DocumentNode itemNode : tag.getChildren()) {
            // Only render li nodes as list items.
            if (!(itemNode instanceof TagDocumentNode) || !((TagDocumentNode)itemNode).getType().equals("li"))
                continue;

            // Use the abstract method to retrieve the indentation text, and add
            // this as padding to the individual list item.
            LayoutRenderNode itemLayoutNode = new LayoutRenderNode(linker);
            TextRenderNode listElementNode = new TextRenderNode(linker);
            listElementNode.addText(getListElementText(i), formatting);
            itemLayoutNode.addNodePadding(listElementNode, null);

            // Render all children into the list item node using the Renderer.
            for (DocumentNode child : itemNode.getChildren())
                renderer.render(child, itemLayoutNode, newFormatting);

            // Add the item to the list layout node.
            listNode.addNode(itemLayoutNode, LayoutRenderNode.WidthBehaviour.Maximal);

            // Increase the list item counter.
            i++;
        }

        // Only add line space if we're not already inside a list.
        Boolean insideListAttribute = (Boolean)formatting.get(INSIDE_LIST_ATTRIBUTE);
        boolean addWhitespace = insideListAttribute == null || !insideListAttribute;
        
        if (addWhitespace)
            parent.ensureLinespaceDistance(1);

        // Add the whole list.
        parent.addNode(listNode, LayoutRenderNode.WidthBehaviour.Maximal);

        if (addWhitespace)
            parent.ensureLinespaceDistance(1);
    }

    /**
     * Returns the text for the indented element of the list for a given index.
     * @param index the zero-based index of the item in the list
     * @return
     */
    protected abstract String getListElementText(int index);
}