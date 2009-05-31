package browsermonkey.render;

import browsermonkey.document.*;
import java.text.AttributedCharacterIterator.Attribute;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.ArrayList;

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
        int borderThickness = 1;
        
        String border;
        if ((border = tag.getAttribute("border")) != null) {
            borderThickness = Integer.parseInt(border);
            // TODO: Detect bad value
        }

        TableRenderNode tableNode = new TableRenderNode(linker, borderThickness);

        for (DocumentNode rowNode : tag.getChildren()) {
            // Only render tr nodes as rows
            if (!(rowNode instanceof TagDocumentNode) || !((TagDocumentNode)rowNode).getType().equals("tr"))
                continue;

            tableNode.newRow();

            TagDocumentNode row = (TagDocumentNode)rowNode;
            
            for (DocumentNode cellNode : row.getChildren()) {
                // Only render td nodes as columns
                if (!(cellNode instanceof TagDocumentNode) || !((TagDocumentNode)cellNode).getType().equals("td"))
                    continue;
            
                TagDocumentNode cell = (TagDocumentNode)cellNode;
                LayoutRenderNode cellRender = new LayoutRenderNode(linker);
                cellRender.setPadding(8, 8, 8, 8);

                for (DocumentNode child : cell.getChildren())
                    renderer.render(child, cellRender, formatting);
                
                tableNode.addCell(cellRender);
            }
        }

        parent.ensureNewLine();
        parent.addNode(tableNode, LayoutRenderNode.WidthBehaviour.Maximal);
    }

    /*private static class TableBorder extends JComponent {
        private boolean vertical;
        private int thickness;
        private Color colour;
        private int length;

        public TableBorder(boolean vertical, int thickness) {
            this(vertical, thickness, Color.black);
        }

        public TableBorder (boolean vertical, int thickness, Color colour) {
            this.vertical = vertical;
            this.thickness = thickness;
            this.colour = colour;
            length = 0;

            if (vertical) {
                setMinimumSize(new Dimension(thickness, 0));
                setMaximumSize(new Dimension(thickness, 1));
            }
            else {
                setMinimumSize(new Dimension(0, thickness));
                setMaximumSize(new Dimension(1, thickness));
            }
        }

        public Dimension getDesiredSize() {
            if (vertical)
                return new Dimension(thickness, length);
            else
                return new Dimension(length, thickness);
        }

        public void setLength(int length) {
            this.length = length;
            if (vertical) {
                setMinimumSize(new Dimension(thickness, 0));
                setMaximumSize(new Dimension(thickness, length));
            }
            else {
                setMinimumSize(new Dimension(0, thickness));
                setMaximumSize(new Dimension(length, thickness));
            }
        }

        @Override
        public void setBounds(int x, int y, int width, int height) {
            Dimension maxSize = getMaximumSize();
            super.setBounds(0, 0, maxSize.width, maxSize.height);
        }

        @Override
        public void paint(Graphics g) {
            g.setColor(colour);
            if (!getSize().equals(getDesiredSize())) {
                g.setColor(Color.red);
                setMaximumSize(getDesiredSize());
            }            
            g.fillRect(0, 0, vertical ? thickness : getWidth(), vertical ? getHeight() : thickness);
        }
    }*/

    private static class TableRenderNode extends LayoutRenderNode {
        private GroupLayout.SequentialGroup horizontalSequence;
        private GroupLayout.SequentialGroup verticalSequence;
        private GroupLayout.ParallelGroup horizontalParallelForBorders;
        private GroupLayout.ParallelGroup verticalParallelForBorders;
        private ArrayList<GroupLayout.ParallelGroup> rowGroups;
        private ArrayList<GroupLayout.ParallelGroup> columnGroups;

        private ArrayList<ArrayList<LayoutRenderNode>> tableCells;

        private int currentRowIndex = -1;
        private int currentColumnIndex = -1;
        private GroupLayout layout;
        private int borderThickness;
        //private ArrayList<TableBorder> borders;

        public TableRenderNode(Linkable linker, int borderThickness) {
            super(linker);
            //borders = new ArrayList<TableBorder>();
            this.borderThickness = borderThickness;
            layout = new GroupLayout(this);
            this.setLayout(layout);
            horizontalSequence = layout.createSequentialGroup();
            verticalSequence = layout.createSequentialGroup();
            horizontalParallelForBorders = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
            verticalParallelForBorders = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
            horizontalParallelForBorders.addGroup(horizontalSequence);
            verticalParallelForBorders.addGroup(verticalSequence);
            layout.setHorizontalGroup(horizontalParallelForBorders);
            layout.setVerticalGroup(verticalParallelForBorders);
            rowGroups = new ArrayList<GroupLayout.ParallelGroup>();
            columnGroups = new ArrayList<GroupLayout.ParallelGroup>();

            tableCells = new ArrayList<ArrayList<LayoutRenderNode>>();
        }

        private void addRowBorder() {
            verticalSequence.addGap(borderThickness);
            /*TableBorder border = new TableBorder(false, borderThickness);
            verticalSequence.addComponent(border);
            horizontalParallelForBorders.addComponent(border);
            borders.add(border);*/
        }

        private void addColumnBorder() {
            horizontalSequence.addGap(borderThickness);
            /*TableBorder border = new TableBorder(true, borderThickness);
            horizontalSequence.addComponent(border);
            verticalParallelForBorders.addComponent(border);
            borders.add(border);*/

        }

        public void newRow() {
            if (currentRowIndex == -1)
                addRowBorder();
            
            GroupLayout.ParallelGroup rowLayout = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
            verticalSequence.addGroup(rowLayout);
            rowGroups.add(rowLayout);
            tableCells.add(new ArrayList<LayoutRenderNode>());
            
            currentRowIndex++;
            currentColumnIndex = 0;

            addRowBorder();
        }

        public void addCell(LayoutRenderNode cell) {
            GroupLayout.ParallelGroup columnLayout;
            if (currentColumnIndex >= columnGroups.size()) {
                if (currentColumnIndex == 0)
                    addColumnBorder();
                
                columnLayout = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
                columnGroups.add(columnLayout);
                horizontalSequence.addGroup(columnLayout);

                addColumnBorder();
            }
            else
                columnLayout = columnGroups.get(currentColumnIndex);

            columnLayout.addComponent(cell/*, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE*/);
            rowGroups.get(currentRowIndex).addComponent(cell/*, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE*/);

            tableCells.get(currentRowIndex).add(cell);

            currentColumnIndex++;
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            if (tableCells == null || tableCells.size() == 0 || tableCells.get(0) == null ||  tableCells.get(0).size() == 0)
                return;

            int[] rowHeights = new int[tableCells.size()];
            int[] columnWidths = new int[tableCells.get(0).size()];

            for (int i = 0; i < tableCells.size(); i++) {
                ArrayList<LayoutRenderNode> row = tableCells.get(i);
                for (int j = 0; j < row.size(); j++) {
                    LayoutRenderNode column = row.get(j);
                    rowHeights[i] = Math.max(rowHeights[i], column.getHeight());
                    columnWidths[j] = Math.max(columnWidths[j], column.getWidth());
                }
            }

            int cumulativeY = 0;
            for (int i = 0; i <= rowHeights.length; i++) {
                g.fillRect(0, cumulativeY, getWidth(), borderThickness);
                if (i < rowHeights.length)
                    cumulativeY += rowHeights[i] + borderThickness;
            }

            int cumulativeX = 0;
            for (int j = 0; j <= columnWidths.length; j++) {
                g.fillRect(cumulativeX, 0, borderThickness, getHeight());
                if (j < columnWidths.length)
                    cumulativeX += columnWidths[j] + borderThickness;
            }
        }

        /*@Override
        public void setBounds(int x, int y, int width, int height) {
            
            Dimension size = new Dimension(width, height);

            for (TableBorder border : borders) {
                if (border.vertical) {
                    border.setLength(size.height);
                }
                else
                    border.setLength(size.width);
            }

            super.setBounds(x, y, width, height);
        }*/
    }
}