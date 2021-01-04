package com.uib.web.peptideshaker.ui.components.items;

import com.vaadin.event.LayoutEvents;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import java.awt.*;

/**
 * This class represents selectable node that used mainly in
 * MatrixLayoutChartFilter
 *
 * @author Yehia Farag
 */
public abstract class SelectableNode extends AbsoluteLayout implements LayoutEvents.LayoutClickListener {

    private final boolean disables;
    private final VerticalLayout upperLine;
    private final VerticalLayout lowerLine;
    private final String nodeId;
    private final int columnIndex;
    private final VerticalLayout nodeContainer;
    private final Color nodeColor;
    private final Label nodeCircle;
    private boolean selecatble;
    private boolean selected;

    /**
     * Initialise selectable node
     *
     * @param nodeId      id
     * @param columnIndex represented column
     * @param disables    disabled node
     * @param nodeColor   node colour-AWT
     */
    public SelectableNode(String nodeId, int columnIndex, boolean disables, Color nodeColor) {
        this.nodeId = nodeId;
        this.columnIndex = columnIndex;
        this.nodeColor = nodeColor;
        SelectableNode.this.setWidth(15, Unit.PIXELS);
        SelectableNode.this.setHeight(25, Unit.PIXELS);
        SelectableNode.this.setStyleName("selectablenode");
        VerticalLayout lineContainers = new VerticalLayout();
        lineContainers.setWidth(100, Unit.PERCENTAGE);
        lineContainers.setHeight(100, Unit.PERCENTAGE);
        SelectableNode.this.addComponent(lineContainers);

        nodeContainer = new VerticalLayout();
        nodeContainer.setSizeFull();
        SelectableNode.this.addComponent(nodeContainer);

        nodeCircle = new Label();
        nodeCircle.setWidth(100, Unit.PERCENTAGE);
        nodeCircle.setHeight(100, Unit.PERCENTAGE);
        nodeCircle.setData(columnIndex);

        nodeContainer.addComponent(nodeCircle);
        nodeContainer.setComponentAlignment(nodeCircle, Alignment.TOP_CENTER);

        this.disables = disables;

        SelectableNode.this.addLayoutClickListener(SelectableNode.this);

        nodeCircle.setContentMode(ContentMode.HTML);

        upperLine = new VerticalLayout();
        upperLine.setHeight(100, Unit.PERCENTAGE);
        lineContainers.addComponent(upperLine);
        lineContainers.setComponentAlignment(upperLine, Alignment.TOP_CENTER);

        lowerLine = new VerticalLayout();
        lowerLine.setHeight(100, Unit.PERCENTAGE);
        lineContainers.addComponent(lowerLine);
        lineContainers.setComponentAlignment(lowerLine, Alignment.TOP_CENTER);

    }

    /**
     * Get node id
     *
     * @return id
     */
    public String getNodeId() {
        return nodeId;
    }

    /**
     * Get node colour
     *
     * @return AWT colour
     */
    public Color getNodeColor() {
        return nodeColor;
    }

    /**
     * Check if node is disabled
     *
     * @return disabled node
     */
    public boolean isDisables() {
        return disables;
    }

    /**
     * Set upper node selected
     *
     * @param upperSelected upper node is selected
     */
    public void setUpperSelected(boolean upperSelected) {
        if (disables) {
            return;
        }
        if (upperSelected) {
            upperLine.setStyleName("selectednodeline");
        } else {
            upperLine.setStyleName("unselectednodeline");
        }
    }

    /**
     * Set lower node selected
     *
     * @param lowerSelected node selected
     */
    public void setLowerSelected(boolean lowerSelected) {
        if (disables) {
            return;
        }
        if (lowerSelected) {
            lowerLine.addStyleName("selectednodeline");
        } else {
            lowerLine.addStyleName("unselectednodeline");
        }
    }

    /**
     * Check if node is active
     *
     * @return active node
     */
    public boolean isSelecatble() {
        if (disables) {
            return !disables;
        }
        return selecatble;
    }

    /**
     * Set node active
     *
     * @param selecatble active node
     */
    public void setSelecatble(boolean selecatble) {
        if (disables) {
            return;
        }
        this.selecatble = selecatble;
        if (selecatble) {
            nodeCircle.setValue("<center style='color:rgb(" + nodeColor.getRed() + "," + nodeColor.getGreen() + "," + nodeColor.getBlue() + "); width:100% !important; height:100% !important'>" + FontAwesome.CIRCLE.getHtml() + "</center>");

        } else {
            nodeCircle.setValue("<center style='z-index: 1 !important;; color:rgb(" + Color.WHITE.getRed() + "," + Color.WHITE.getGreen() + "," + Color.WHITE.getBlue() + "); width:100% !important; height:100% !important'>" + FontAwesome.CIRCLE.getHtml() + "</center>");

        }
    }

    /**
     * Check if node is selected
     *
     * @return node is selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Set node selected
     *
     * @param selected
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            SelectableNode.this.addStyleName("selectedbubble");
        } else {
            SelectableNode.this.removeStyleName("selectedbubble");
        }

    }

    /**
     * Get related column index
     *
     * @return int column index
     */
    public int getColumnIndex() {
        return columnIndex;
    }

    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        if (selected) {
            selectNode(-1);
        } else {
            selectNode(columnIndex);
        }
    }

    /**
     * Action on select node
     *
     * @param columnIndex related column index
     */
    public abstract void selectNode(int columnIndex);

}
