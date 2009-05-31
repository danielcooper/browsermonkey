package browsermonkey.render;

import browsermonkey.document.*;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.*;

/**
 *
 * @author Paul Calcraft
 */
public class DefinitionListRenderer extends TagRenderer {
    public DefinitionListRenderer(Linkable linker) {
        super(linker);
    }

    @Override
    public void render(Renderer renderer, TagDocumentNode tag, LayoutRenderNode parent, Map<Attribute, Object> formatting) {
        parent.ensureLinespaceDistance(1);

        for (DocumentNode itemNode : tag.getChildren()) {
            // Only render dd or dt nodes as list items
            if (!(itemNode instanceof TagDocumentNode))
                continue;

            TagDocumentNode itemTagNode = (TagDocumentNode)itemNode;

            if (itemTagNode.getType().equals("dt")) {
                for (DocumentNode child : itemNode.getChildren())
                    renderer.render(child, parent, formatting);
            }
            else if (itemTagNode.getType().equals("dd")) {
                LayoutRenderNode itemLayoutNode = new LayoutRenderNode(linker);
                TextRenderNode paddingNode = new TextRenderNode(linker);
                paddingNode.addText("&nbsp;&nbsp;&nbsp;&nbsp;", formatting);
                itemLayoutNode.addNodePadding(paddingNode, null);

                for (DocumentNode child : itemNode.getChildren())
                    renderer.render(child, itemLayoutNode, formatting);

                parent.ensureNewLine();
                parent.addNode(itemLayoutNode, LayoutRenderNode.WidthBehaviour.Maximal);
            }
        }

        parent.ensureLinespaceDistance(1);
    }
}