package com.uib.web.peptideshaker.ui.components;

import com.ejt.vaadin.sizereporter.ComponentResizeEvent;
import com.ejt.vaadin.sizereporter.SizeReporter;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.model.Selection;
import com.uib.web.peptideshaker.ui.components.items.HBarWithLabel;
import com.uib.web.peptideshaker.ui.components.items.SelectableNode;
import com.uib.web.peptideshaker.ui.components.items.SparkLine;
import com.uib.web.peptideshaker.uimanager.SelectionManager;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * This class represents matrix layout filter
 *
 * @author Yehia Mokhtar Farag
 */
public abstract class MatrixDiagramFilter extends VerticalLayout {

    private final AbsoluteLayout container;
    private final Map<Comparable, Integer> rows = new LinkedHashMap<>();
    private final Set<Comparable> fullItemsSet;
    private final Map<Integer, Double> barChartValues;
    private final Map<Comparable, List<SelectableNode>> nodesTable;
    private final LayoutEvents.LayoutClickListener barListener;
    private final Map<String, HBarWithLabel> columnMap;
    private final Map<String, SparkLine> rowLabelsMap;
    private final Set<Comparable> selectedColumns;
    /**
     * The width of the filter container.
     */
    private int mainWidth;
    /**
     * The height of the filter container.
     */
    private int mainHeight;
    /**
     * The title text of the filter.
     */
    private Map<Comparable, Set<Integer>> columns;
    private Map<Comparable, Color> dataColors;
    private int rowLabelsWidth;
    private int minimumWidth;
    private boolean allowRedraw = false;
    private VerticalLayout spacer;
    private int frameWidth;
    private int frameHeight;
    private boolean visibleScrollbar;
    private final SelectionManager selectionManager;

    /**
     *
     * @param selectionManager
     */
    public MatrixDiagramFilter(SelectionManager selectionManager) {
        this.selectionManager = selectionManager;
        this.container = new AbsoluteLayout();
        MatrixDiagramFilter.this.addStyleName("autooverflow");
        this.container.setSizeFull();
        MatrixDiagramFilter.this.setSizeFull();
        MatrixDiagramFilter.this.addComponent(container);
        MatrixDiagramFilter.this.setComponentAlignment(container, Alignment.MIDDLE_CENTER);
        this.mainWidth = 300;
        this.mainHeight = 300;
        this.rowLabelsMap = new LinkedHashMap<>();
        this.nodesTable = new LinkedHashMap<>();
        this.columnMap = new LinkedHashMap<>();
        this.barChartValues = new LinkedHashMap<>();
        this.fullItemsSet = new LinkedHashSet<>();
        this.selectedColumns = new LinkedHashSet<>();
        MatrixDiagramFilter.this.addStyleName("slowredraw");
        this.barListener = (LayoutEvents.LayoutClickEvent event) -> {
            Component barLayout = event.getComponent();
            if (barLayout == null) {
                return;
            }
            Comparable columnId = (Comparable) ((HBarWithLabel) barLayout).getData();
            if (selectedColumns.contains(columnId)) {
                selectedColumns.remove(columnId);
                unSelectCategory(columnId);
            } else {
                selectCategory(columnId);
            }
        };
        this.initlayout();

    }

    private void initlayout() {

        SizeReporter mainSizeReporter = new SizeReporter(MatrixDiagramFilter.this);
        mainSizeReporter.addResizeListener((ComponentResizeEvent event) -> {
            int tChartWidth = event.getWidth();
            int tChartHeight = event.getHeight();
            if (tChartWidth <= 0 || tChartHeight <= 0) {
                return;
            }

            if ((tChartWidth == mainWidth || Math.abs(tChartWidth - mainWidth) < 10) && (mainHeight == tChartHeight || Math.abs(tChartHeight - mainHeight) < 10)) {
                return;
            }
            mainWidth = tChartWidth - 2;
            mainHeight = tChartHeight-2;
            frameHeight = mainHeight;
            frameWidth = mainWidth;
            reDrawLayout(false);
        });
    }

    private void reDrawLayout(boolean local) {
        if (!allowRedraw) {
            return;
        }
        int step;
        if (columnMap.size() > 1) {
            step = (frameWidth - 30 - rowLabelsWidth - 20) / (columnMap.size() - 1);
            if (step < 25) {
                step = 25;
            }
        } else {
            step = (frameWidth - rowLabelsWidth - 20 - 20);
        }
        int x = rowLabelsWidth + 20;
        AbsoluteLayout.ComponentPosition position = container.getPosition(spacer);
        position.setLeftValue((float) x);
        container.setPosition(spacer, position);
        for (String columnKey : columnMap.keySet()) {
            position = container.getPosition(columnMap.get(columnKey));
            position.setLeftValue((float) x);
            container.setPosition(columnMap.get(columnKey), position);
            List<SelectableNode> nodeList = nodesTable.get(columnKey);
            for (SelectableNode node : nodeList) {
                position = container.getPosition(node);
                position.setLeftValue((float) x);
                container.setPosition(node, position);
            }
            x += step;
        }
        x -= step;
        spacer.setWidth(((x + 20) - (rowLabelsWidth + 20)), Unit.PIXELS);
        if (columnMap.size() == 1) {
            container.setWidth(rowLabelsWidth + 40, Unit.PIXELS);
        } else {
            container.setWidth((x + 25), Unit.PIXELS);
        }
        visibleScrollbar = container.getWidth() > frameWidth || container.getHeight() > frameHeight;
        this.setVisibleScrollbar(visibleScrollbar || local);
    }

    /**
     *
     * @param visible
     */
    public abstract void setVisibleScrollbar(boolean visible);

    /**
     *
     * @return
     */
    public int getBarChartX() {
        if (columnMap.size() == 1) {
            return -1;
        }
        return rowLabelsWidth;
    }

    /**
     *
     * @return
     */
    public Float getBarEndY() {
        return container.getPosition(spacer).getTopValue();
    }

    private void drawLayout() {
        allowRedraw = false;
        container.removeAllComponents();
        rowLabelsMap.clear();
        columnMap.clear();
        nodesTable.clear();

        int currentX ;
        int currentY ;
        int max = (-1 * Integer.MAX_VALUE);
        int min = Integer.MAX_VALUE;
        int labelCharNumber = 0;

        Map<String, Integer> effictiveRowMap = new LinkedHashMap<>();
        rows.keySet().forEach((rowKey) -> {
            int numInRow = 0;
            for (int columnIndex : barChartValues.keySet()) {
                int columnValue = barChartValues.get(columnIndex).intValue();
                if (columnValue == 0) {
                    continue;
                }
                String columnKey = columns.keySet().toArray()[columnIndex] + "";
                if (columnKey.contains(rowKey.toString())) {
                    numInRow += barChartValues.get(columnIndex);
                }
            }
            if (numInRow > 0) {
                effictiveRowMap.put(rowKey.toString(), numInRow);
            }
        });
        ;

        for (String row : effictiveRowMap.keySet()) {
            int i = effictiveRowMap.get(row);
            labelCharNumber = Math.max(labelCharNumber, (row).length());
            if (max < i) {
                max = i;
            }
            if (min > i) {
                min = i;
            }

        }
        rowLabelsWidth = Math.min((labelCharNumber * 5) + 5 + 70, 200);
        int barchartHeight = 130;
        double factor = -1;
        currentX = rowLabelsWidth;
        List<String> sortedKeysList = new ArrayList<>(effictiveRowMap.keySet());
        for (int columnIndex : barChartValues.keySet()) {
            int columnValue = barChartValues.get(columnIndex).intValue();
            if (columnValue == 0) {
                continue;
            }
            if (factor == -1) {
                factor = (double) (barchartHeight - 30) / (Math.log(columnValue) * 10.0);
            }
            int bheight = (int) ((Math.log(columnValue) * 10.0) * factor);
            if (bheight < 4) {
                bheight = 4;
            }
            bheight = bheight + 30;
            int y = (barchartHeight - bheight);
            if (y < 0) {
                bheight = 34 + columnValue;
                y = barchartHeight - bheight;

            }

            String columnKey = columns.keySet().toArray()[columnIndex] + "";
            HBarWithLabel bar = new HBarWithLabel(columnValue + "", barChartValues.get(columnIndex).intValue());
            bar.setWidth(15, Unit.PIXELS);
            bar.setHeight(bheight, Unit.PIXELS);
            bar.addLayoutClickListener(barListener);

            container.addComponent(bar, "left:" + currentX + "px; top:" + y + "px;");

            bar.setData(columnKey);
            String mod = columns.keySet().toArray()[columnIndex++].toString().replace("[", "").replace("]", "").replace(",", "<br/>") + "<font style='font-size:10px !important;margin-right:5px'><br/>" + VaadinIcons.HASH.getHtml() + "</font>Proteins" + " (" + ((int) (double) barChartValues.get(columnIndex - 1)) + ")";
            for (Comparable key : dataColors.keySet()) {
                if (mod.contains(key.toString())) {
                    Color c = dataColors.get(key);
                    mod = mod.replace(key.toString(), "<font style='color:rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ");font-size:10px !important;margin-right:5px'> " + VaadinIcons.CIRCLE.getHtml() + "</font>" + key);
                }

            }
            bar.setDescription(mod);
            columnMap.put(columnKey, bar);
            if (!nodesTable.containsKey(columnKey)) {
                nodesTable.put(columnKey, new ArrayList<>());
            }
        }
        spacer = new VerticalLayout();
        spacer.setHeight(1, Unit.PIXELS);
        spacer.setStyleName("lightgraylayout");
        spacer.setWidth((currentX - rowLabelsWidth), Unit.PIXELS);
        this.container.addComponent(spacer, "left:" + rowLabelsWidth + "px;top:" + (barchartHeight + 2) + "px");

        currentX = 0;
        currentY = barchartHeight + 7;
        int rowIndex = 0;
        for (String rowKey : effictiveRowMap.keySet()) {

            SparkLine sl = new SparkLine(rowKey, effictiveRowMap.get(rowKey), 0, max, dataColors.get(rowKey));
            sl.setWidth(rowLabelsWidth, Unit.PIXELS);
            sl.setData(rowKey);
            sl.setDescription(rowKey);
            this.container.addComponent(sl, "left:" + currentX + "px;top:" + currentY + "px");
            rowLabelsMap.put(rowKey, sl);
            currentY += 25;
            rowIndex++;
        }
        int finalwidth = 0;
        int finalheight = 0;
        for (String rowKey : rowLabelsMap.keySet()) {
            int columnIndex = 0;
            for (String columnKey : columnMap.keySet()) {
                SelectableNode node = new SelectableNode(rowKey, columnIndex, columns.get(columnKey).isEmpty(), dataColors.get(rowKey)) {
                    private final Comparable columnId = columnKey;

                    @Override
                    public void selectNode(int columnIndex) {//
                        if (selectedColumns.contains(columnIndex)) {
                            unSelectCategory(columnId);
                        } else {
                            selectCategory(columnId);
                        }
                    }
                };
                node.setData(columns.get(columnKey).size());
                node.setDescription(columnMap.get(columnKey).getDescription());
                nodesTable.get(columnKey).add(node);
                int left = container.getPosition(columnMap.get(columnKey)).getLeftValue().intValue();
                finalwidth = Math.max(finalwidth, left);
                int top = container.getPosition(rowLabelsMap.get(rowKey)).getTopValue().intValue();
                finalheight = Math.max(finalheight, top);
                this.container.addComponent(node, "left:" + left + "px;top:" + top + "px");

                node.setSelecatble(false);
                if (columnKey.contains(rowKey)) {
                    node.setSelecatble(true);
                    node.setUpperSelected(true);
                    node.setLowerSelected(true);
                }
                if (columnKey.split(",").length == 1) {
                    node.setUpperSelected(false);
                    node.setLowerSelected(false);
                } else {
                    int nodeIndex = sortedKeysList.indexOf(node.getNodeId());
                    String[] subArr = columnKey.replace("]", "").replace("[", "").trim().split(",");
                    int startLineRange = sortedKeysList.indexOf(subArr[0]);
                    int endLineRange = sortedKeysList.indexOf(subArr[subArr.length - 1].trim());
                    if (nodeIndex == startLineRange) {
                        node.setUpperSelected(false);
                        node.setLowerSelected(true);
                    } else if (nodeIndex > startLineRange && nodeIndex < endLineRange) {
                        node.setUpperSelected(true);
                        node.setLowerSelected(true);
                    } else if (nodeIndex == endLineRange) {
                        node.setUpperSelected(true);
                        node.setLowerSelected(false);
                    } else {
                        node.setUpperSelected(false);
                        node.setLowerSelected(false);
                    }
                }

                columnIndex++;
            }

        }
        minimumWidth = finalwidth + 0;
        container.setWidth(minimumWidth, Unit.PIXELS);
        container.setHeight(finalheight + 30, Unit.PIXELS);
        allowRedraw = true;

    }

    private void selectColumn(Set<Comparable> columnIds) {
        unselectAll();
        if (columnIds == null || columnIds.isEmpty()) {
            return;
        }
        this.selectedColumns.addAll(columnIds);
        columnIds.stream().map((columnId) -> {
            rowLabelsMap.values().forEach((sL) -> {
                if (columnId.toString().contains(sL.getDescription())) {
                    sL.setSelected(true);
                }
            });
            return columnId;
        }).map((columnId) -> {
            nodesTable.get(columnId).forEach((sN) -> {
                sN.setSelected(true);
            });
            return columnId;
        }).forEachOrdered((columnId) -> {
            if (columnMap.containsKey(columnId.toString())) {
                columnMap.get(columnId.toString()).addStyleName("selectedbarlayout");
            }
        });

    }

    /**
     *
     */
    public void unselectAll() {

        rowLabelsMap.values().forEach((sL) -> {
            sL.setSelected(false);
        });
        columnMap.values().forEach((bar) -> {
            bar.removeStyleName("selectedbarlayout");
        });
        nodesTable.values().forEach((lSN) -> {
            lSN.forEach((sN) -> {
                sN.setSelected(false);
            });
        });
    }


    /**
     *
     * @param modifications
     */
    public void updateViewedModifications(Set<String> modifications) {
        Set<Comparable> rowSet = new HashSet<>();
        Set<Comparable> columnSet = new HashSet<>();

        columns.keySet().forEach((columnKey) -> {
            modifications.stream().filter((modKey) -> (columnKey.toString().contains(modKey))).map((_item) -> {
                columnSet.add(columnKey.toString());
                return _item;
            }).map((_item) -> columnKey.toString().replace("[", "").replace("]", "").split(",")).forEachOrdered((mods) -> {
                for (String mod : mods) {
                    rowSet.add(mod.trim());
                }
            });
        });
        localDrawLayout(rowSet, columnSet);
        reDrawLayout(true);
    }

    private void localDrawLayout(Set<Comparable> rowSet, Set<Comparable> columnSet) {
        allowRedraw = false;
        container.removeAllComponents();
        rowLabelsMap.clear();
        columnMap.clear();
        nodesTable.clear();
        int currentX = 0;
        int currentY = 0;
        int max = (-1 * Integer.MAX_VALUE);
        int min = Integer.MAX_VALUE;
        rowLabelsWidth = 0;
        Map<Comparable, Integer> effictiveRowMap = new LinkedHashMap<>();
        rows.keySet().forEach((rowKey) -> {
            if (!rowSet.isEmpty() && !rowSet.contains(rowKey)) {
            } else {
                int numInRow = 0;
                for (int columnIndex : barChartValues.keySet()) {
                    int columnValue = barChartValues.get(columnIndex).intValue();
                    if (columnValue == 0) {
                        continue;
                    }
                    String columnKey = columns.keySet().toArray()[columnIndex] + "";
                    if (columnKey.contains(rowKey.toString())) {
                        numInRow += barChartValues.get(columnIndex);
                    }
                }
                if (numInRow > 0) {
                    effictiveRowMap.put(rowKey.toString(), numInRow);
                }
            }
        });
        for (Comparable row : effictiveRowMap.keySet()) {
            int i = effictiveRowMap.get(row);
            rowLabelsWidth = Math.min(Math.max(rowLabelsWidth, (row).toString().length()), 200);
            if (max < i) {
                max = i;
            }
            if (min > i) {
                min = i;
            }

        }
        rowLabelsWidth = Math.min((rowLabelsWidth * 5) + 5 + 70, 200);
        int barchartHeight = 130;
        double factor = -1;
        currentX = rowLabelsWidth;
        List<Comparable> sortedKeysList = new ArrayList<>(effictiveRowMap.keySet());
        for (int columnIndex : barChartValues.keySet()) {
            int columnValue = barChartValues.get(columnIndex).intValue();
            String columnKey = columns.keySet().toArray()[columnIndex] + "";

            if (columnValue == 0 || (!columnSet.isEmpty() && !columnSet.contains(columnKey))) {
                continue;
            }
            if (factor == -1) {
                factor = (double) (barchartHeight - 30) / (Math.log(columnValue) * 10.0);
            }
            int bheight = (int) Math.max((Math.log(columnValue) * 10.0) * factor, 4) + 30;
            HBarWithLabel bar = new HBarWithLabel(columnValue + "", barChartValues.get(columnIndex).intValue());
            bar.setWidth(15, Unit.PIXELS);
            bar.setHeight(bheight, Unit.PIXELS);
            bar.addLayoutClickListener(barListener);
            container.addComponent(bar, "left:" + currentX + "px; top:" + (barchartHeight - bheight) + "px;");
            bar.setData(columnKey);
            String mod = columns.keySet().toArray()[columnIndex++].toString().replace("[", "").replace("]", "").replace(",", "<br/>") + "<font style='font-size:10px !important;margin-right:5px'><br/>" + VaadinIcons.HASH.getHtml() + "</font>Proteins" + " (" + ((int) (double) barChartValues.get(columnIndex - 1)) + ")";
            for (Comparable key : dataColors.keySet()) {
                if (mod.contains(key.toString())) {
                    Color c = dataColors.get(key);
                    mod = mod.replace(key.toString(), "<font style='color:rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ");font-size:10px !important;margin-right:5px'> " + VaadinIcons.CIRCLE.getHtml() + "</font>" + key);
                }

            }
            bar.setDescription(mod);
            columnMap.put(columnKey, bar);
            if (!nodesTable.containsKey(columnKey)) {
                nodesTable.put(columnKey, new ArrayList<>());
            }
        }
        spacer = new VerticalLayout();
        spacer.setHeight(1, Unit.PIXELS);
        spacer.setStyleName("lightgraylayout");
        spacer.setWidth((currentX - rowLabelsWidth), Unit.PIXELS);
        this.container.addComponent(spacer, "left:" + rowLabelsWidth + "px;top:" + (barchartHeight + 2) + "px");
        currentX = 0;
        currentY = barchartHeight + 7;
        int rowIndex = 0;
        for (Comparable rowKey : effictiveRowMap.keySet()) {
            SparkLine sl = new SparkLine(rowKey.toString(), effictiveRowMap.get(rowKey), 0, max, dataColors.get(rowKey));
            sl.setWidth(rowLabelsWidth, Unit.PIXELS);
            sl.setData(rowKey);
            sl.setDescription(rowKey.toString());
            this.container.addComponent(sl, "left:" + currentX + "px;top:" + currentY + "px");
            rowLabelsMap.put(rowKey.toString(), sl);
            currentY += 25;
            rowIndex++;
        }
        int finalwidth = 0;
        int finalheight = 0;
        for (String rowKey : rowLabelsMap.keySet()) {
            int columnIndex = 0;
            for (String columnKey : columnMap.keySet()) {
                SelectableNode node = new SelectableNode(rowKey, columnIndex, columns.get(columnKey).isEmpty(), dataColors.get(rowKey)) {
                    private final Comparable columnId = columnKey;
                    @Override
                    public void selectNode(int columnIndex) {//
                        if (selectedColumns.contains(columnIndex)) {
                            unSelectCategory(columnId);
                        } else {
                            selectCategory(columnId);
                        }
                    }

                };
                node.setData(columns.get(columnKey).size());
                node.setDescription(columnMap.get(columnKey).getDescription());
                nodesTable.get(columnKey).add(node);
                int left = container.getPosition(columnMap.get(columnKey)).getLeftValue().intValue();

                finalwidth = Math.max(finalwidth, left);
                int top = container.getPosition(rowLabelsMap.get(rowKey)).getTopValue().intValue();
                finalheight = Math.max(finalheight, top);
                this.container.addComponent(node, "left:" + left + "px;top:" + top + "px");
                node.setSelecatble(false);
                if (columnKey.contains(rowKey)) {
                    node.setSelecatble(true);
                    node.setUpperSelected(true);
                    node.setLowerSelected(true);
                }
                if (columnKey.split(",").length == 1) {
                    node.setUpperSelected(false);
                    node.setLowerSelected(false);
                } else {
                    int nodeIndex = sortedKeysList.indexOf(node.getNodeId());
                    String[] subArr = columnKey.replace("]", "").replace("[", "").trim().split(",");
                    int startLineRange = sortedKeysList.indexOf(subArr[0]);
                    int endLineRange = sortedKeysList.indexOf(subArr[subArr.length - 1].trim());
                    if (nodeIndex == startLineRange) {
                        node.setUpperSelected(false);
                        node.setLowerSelected(true);
                    } else if (nodeIndex > startLineRange && nodeIndex < endLineRange) {
                        node.setUpperSelected(true);
                        node.setLowerSelected(true);
                    } else if (nodeIndex == endLineRange) {
                        node.setUpperSelected(true);
                        node.setLowerSelected(false);
                    } else {
                        node.setUpperSelected(false);
                        node.setLowerSelected(false);
                    }
                }

                columnIndex++;
            }

        }
        minimumWidth = finalwidth;
        container.setWidth(minimumWidth, Unit.PIXELS);
        container.setHeight(finalheight + 30, Unit.PIXELS);
        allowRedraw = true;

    }

    /**
     *
     * @param columns
     * @param rows
     * @param dataColors
     * @param selectedCategories
     */
    public void updateFilter(Map<Comparable, Set<Integer>> columns, Map<Comparable, Integer> rows, Map<Comparable, Color> dataColors, Set<Comparable> selectedCategories) {
       
        if (selectedCategories != null && selectedCategories.containsAll(columns.keySet())) {
            reset();
            return;
        }
        boolean sameData = true;
        if (this.rows.size() == rows.size() && this.rows.keySet().containsAll(rows.keySet())) {
            for (Comparable key : rows.keySet()) {
                if (rows.get(key).intValue() != this.rows.get(key).intValue()) {
                    sameData = false;
                    break;
                }
            }
        } else {
            sameData = false;
        }
        if (sameData && this.columns.size() == columns.size() && this.columns.keySet().containsAll(columns.keySet())) {
            for (Comparable key : columns.keySet()) {
                if (columns.get(key).size() != this.columns.get(key).size()) {
                    sameData = false;
                    break;
                }
            }
        } else {
            sameData = false;
        }
        if (sameData) {
            selectColumn(selectedCategories);
            return;
        }
        this.rows.clear();
        rowLabelsMap.clear();
        this.selectedColumns.clear();
        this.dataColors = dataColors;
        this.rows.putAll(rows);
        this.columns = columns;
        barChartValues.clear();
        fullItemsSet.clear();
        int coulmnIndx = 0;
        for (Comparable key : columns.keySet()) {
            fullItemsSet.addAll(columns.get(key));
            barChartValues.put(coulmnIndx++, (double) columns.get(key).size());
        }
        drawLayout();
        reDrawLayout(false);
        selectColumn(selectedCategories);
    }

    private void selectCategory(Comparable category) {
        Set<Comparable> selectionCategories = new LinkedHashSet<>();
        selectionCategories.add(category);
        Selection selection = new Selection(CONSTANT.DATASET_SELECTION, CONSTANT.MODIFICATIONS_FILTER_ID, selectionCategories, true);
        selectionManager.setSelection(selection);
    }

    private void unSelectCategory(Comparable category) {
        Set<Comparable> selectionCategories = new LinkedHashSet<>();
        selectionCategories.add(category);
        Selection selection = new Selection(CONSTANT.DATASET_SELECTION, CONSTANT.MODIFICATIONS_FILTER_ID, selectionCategories, false);
        selectionManager.setSelection(selection);
    }

    private void reset() {
        Selection selection = new Selection(CONSTANT.DATASET_SELECTION, CONSTANT.MODIFICATIONS_FILTER_ID, null, false);
        selectionManager.setSelection(selection);
    }

}
