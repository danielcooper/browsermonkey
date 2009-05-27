package browsermonkey.render;

import browsermonkey.document.*;
import java.text.AttributedCharacterIterator.Attribute;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;

/**
 *
 * @author Paul Calcraft
 */
public class TableTagRenderer extends TagRenderer {

    public TableTagRenderer(Linkable linker) {
        super(linker);
    }
    
    @Override
    public void render(Renderer renderer, TagDocumentNode tag, LayoutRenderNode parent, Map<Attribute, Object> formatting) {
        TableRenderNode tableNode = new TableRenderNode(linker);

        boolean firstRow = true;
        for (DocumentNode rowNode : tag.getChildren()) {
            tableNode.newRow();

            TagDocumentNode row = (TagDocumentNode)rowNode;
            
            boolean firstColumn = true;
            for (DocumentNode cellNode : row.getChildren()) {
                TagDocumentNode cell = (TagDocumentNode)cellNode;
                CellRenderNode cellRender = new CellRenderNode(linker, !firstColumn, !firstRow);
                for (DocumentNode child : cell.getChildren()) {
                    renderer.render(child, cellRender, formatting);
                }
                tableNode.addCell(cellRender);
                firstColumn = false;
            }
            firstRow = false;
        }

        parent.ensureNewLine();
        parent.addNode(tableNode);
    }

    private static class TableRenderNode extends LayoutRenderNode {
        private GroupLayout.SequentialGroup horizontalSequence;
        private GroupLayout.SequentialGroup verticalSequence;
        private ArrayList<GroupLayout.ParallelGroup> rows;
        private ArrayList<GroupLayout.ParallelGroup> columns;
        private int currentRowIndex = -1;
        private int currentColumnIndex = -1;
        private GroupLayout layout;

        public TableRenderNode(Linkable linker) {
            super(linker);
            this.setBorder(new LineBorder(Color.BLACK));
            layout = new GroupLayout(this);
            this.setLayout(layout);
            horizontalSequence = layout.createSequentialGroup();
            verticalSequence = layout.createSequentialGroup();
            layout.setHorizontalGroup(horizontalSequence);
            layout.setVerticalGroup(verticalSequence);
            rows = new ArrayList<GroupLayout.ParallelGroup>();
            columns = new ArrayList<GroupLayout.ParallelGroup>();
        }

        public void newRow() {
            GroupLayout.ParallelGroup rowLayout = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
            verticalSequence.addGroup(rowLayout);
            rows.add(rowLayout);
            currentRowIndex++;
            currentColumnIndex = 0;
        }

        public void addCell(CellRenderNode cell) {
            GroupLayout.ParallelGroup columnLayout;
            if (currentColumnIndex >= columns.size()) {
                columnLayout = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
                columns.add(columnLayout);
                horizontalSequence.addGroup(columnLayout);
            }
            else
                columnLayout = columns.get(currentColumnIndex);
            
            columnLayout.addComponent(cell, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
            rows.get(currentRowIndex).addComponent(cell, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
            currentColumnIndex++;
        }
    }

    private static class CellRenderNode extends LayoutRenderNode {
        private boolean drawLeftBorder;
        private boolean drawTopBorder;

        public CellRenderNode(Linkable linker, boolean drawLeftBorder, boolean drawTopBorder) {
            super(linker);
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