package browsermonkey.render;

import browsermonkey.document.*;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.*;

/**
 * Renders definition list tags from a dl TagDocumentNode and its children.
 * @author Paul Calcraft
 */
public class DefinitionListRenderer extends TagRenderer {
    public DefinitionListRenderer(Linkable linker) {
        super(linker);
    }

    @Override
    public void render(Renderer renderer, TagDocumentNode tag, LayoutRenderNode parent, Map<Attribute, Object> formatting) {
        // Make space for the list above.
        parent.ensureLinespaceDistance(1);

        // For each child...
        for (DocumentNode itemNode : tag.getChildren()) {
            // Only render dd or dt nodes as list items
            if (!(itemNode instanceof TagDocumentNode))
                continue;

            TagDocumentNode itemTagNode = (TagDocumentNode)itemNode;

            // If dt, render as normal
            if (itemTagNode.getType().equals("dt")) {
                for (DocumentNode child : itemNode.getChildren())
                    renderer.render(child, parent, formatting);
            }
            // If dd, create a layout node and pad with an indent text node to
            // the left.
            else if (itemTagNode.getType().equals("dd")) {
                LayoutRenderNode itemLayoutNode = new LayoutRenderNode(linker);
                itemLayoutNode.addNodePadding(renderer.constructIndentTextNode(formatting), null);

                for (DocumentNode child : itemNode.getChildren())
                    renderer.render(child, itemLayoutNode, formatting);

                parent.addNode(itemLayoutNode, LayoutRenderNode.WidthBehaviour.Maximal);
            }
        }

        // Make space for the list below.
        parent.ensureLinespaceDistance(1);
    }
}