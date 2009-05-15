package browsermonkey.render;

import browsermonkey.document.*;
import java.text.AttributedCharacterIterator.Attribute;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Map;

/**
 *
 * @author Paul Calcraft
 */
public class TableTagRenderer extends TagRenderer {
    @Override
    public void render(Renderer renderer, TagDocumentNode tag, LayoutRenderNode parent, Map<Attribute, Object> formatting) {
        LayoutRenderNode tableNode = new TableRenderNode();

        boolean firstRow = true;
        for (DocumentNode rowNode : tag.getChildren()) {
            TagDocumentNode row = (TagDocumentNode)rowNode;
            RowRenderNode rowRender = new RowRenderNode();

            boolean firstColumn = true;
            for (DocumentNode cellNode : row.getChildren()) {
                TagDocumentNode cell = (TagDocumentNode)cellNode;
                CellRenderNode cellRender = new CellRenderNode(!firstColumn, !firstRow);
                for (DocumentNode child : cell.getChildren()) {
                    renderer.render(child, cellRender, formatting);
                }
                rowRender.addNode(cellRender);
                firstColumn = false;
            }

            tableNode.addNode(rowRender);
            firstRow = false;
        }

        parent.addLineBreaks(1);

        parent.addNode(tableNode);

        parent.addLineBreaks(2);
    }

    private static class TableRenderNode extends LayoutRenderNode {
        public TableRenderNode() {
            this.setBorder(new LineBorder(Color.BLACK));
        }
    }

    private static class RowRenderNode extends LayoutRenderNode {
        private GroupLayout.SequentialGroup horizontalGroup;
        private GroupLayout.ParallelGroup verticalGroup;
        
        public RowRenderNode() {            
            GroupLayout layout = new GroupLayout(this);
            this.setLayout(layout);

            horizontalGroup = layout.createSequentialGroup();
            layout.setHorizontalGroup(horizontalGroup);

            verticalGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
            layout.setVerticalGroup(verticalGroup);
        }

        @Override
        public void addNode(RenderNode node) {
            verticalGroup.addComponent(node, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
            horizontalGroup.addComponent(node, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE);
        }
    }

    private static class CellRenderNode extends LayoutRenderNode {
        private boolean drawLeftBorder;
        private boolean drawTopBorder;

        public CellRenderNode(boolean drawLeftBorder, boolean drawTopBorder) {
            this.drawLeftBorder = drawLeftBorder;
            this.drawTopBorder = drawTopBorder;
            int pad = 5;
            this.setPadding(drawLeftBorder ? 1+pad : pad, pad, pad, pad);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Dimension size = this.getSize();
            if (drawLeftBorder)
                g.drawLine(0, 0, 0, size.height-1);
            if (drawTopBorder)
                g.drawLine(0, 0, size.width-1, 0);
        }
    }
}