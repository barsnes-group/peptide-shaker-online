package com.uib.web.peptideshaker.presenter.core.filtercharts.filters;

import com.ejt.vaadin.sizereporter.ComponentResizeEvent;
import com.ejt.vaadin.sizereporter.SizeReporter;
import com.google.common.collect.Sets;

import com.uib.web.peptideshaker.model.core.ModificationMatrix;
import com.uib.web.peptideshaker.presenter.core.HBarWithLabel;
import com.uib.web.peptideshaker.presenter.core.filtercharts.components.SelectableNode;
import com.uib.web.peptideshaker.presenter.core.form.SparkLine;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * This class represents matrix layout filter
 *
 * @author Yehia Farag
 */
public abstract class MatrixDiagramRedraw extends VerticalLayout {
    
    private final AbsoluteLayout container;
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
    private Map<String, Set<Comparable>> columns;
    private final Map<String, Integer> rows = new LinkedHashMap<>();
    private final Set<Comparable> fullItemsSet;
    private final Map<Integer, Double> barChartValues;
    private final Map<Comparable, List<SelectableNode>> nodesTable;
    
    private final LayoutEvents.LayoutClickListener barListener;
    private final Map<String, HBarWithLabel> columnMap;
    private final Map<String, SparkLine> rowLabelsMap;
    private Map<String, Color> dataColors;
    private final Set<Integer> selectedIndexes;
    private int rowLabelsWidth;
    private int minWidth;
    private boolean allowRedraw = false;
    private VerticalLayout spacer;
    
    public MatrixDiagramRedraw() {
        this.container = new AbsoluteLayout();
        MatrixDiagramRedraw.this.addStyleName("autooverflow");
        this.container.setSizeFull();
        MatrixDiagramRedraw.this.setSizeFull();
        MatrixDiagramRedraw.this.addComponent(container);
        MatrixDiagramRedraw.this.setComponentAlignment(container, Alignment.MIDDLE_CENTER);
        this.mainWidth = 300;
        this.mainHeight = 300;
        this.rowLabelsMap = new LinkedHashMap<>();
        this.nodesTable = new LinkedHashMap<>();
        this.columnMap = new LinkedHashMap<>();
        this.barChartValues = new LinkedHashMap<>();
        this.fullItemsSet = new LinkedHashSet<>();
        this.selectedIndexes = new LinkedHashSet<>();
        MatrixDiagramRedraw.this.addStyleName("slowredraw");
        this.barListener = (LayoutEvents.LayoutClickEvent event) -> {
            Component barLayout = event.getComponent();
            if (barLayout == null) {
                return;
            }
            int columnIndx = (int) ((HBarWithLabel) barLayout).getData();
            if (selectedIndexes.contains(columnIndx)) {
                selectedIndexes.remove(columnIndx);
            } else {
                selectedIndexes.add(columnIndx);
            }
            
            applyFilter(selectedIndexes);
        };
        this.initlayout();
        
    }
    
    private void initlayout() {
        
        SizeReporter mainSizeReporter = new SizeReporter(MatrixDiagramRedraw.this);
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
            mainHeight = tChartHeight;
            reDrawLayout();
        });
    }
    
    public void initializeFilterData(ModificationMatrix modificationMatrix, Map<String, Color> dataColors, Set<Object> selectedCategories, int totalNumber) {
        
        rows.clear();
        rowLabelsMap.clear();
        this.dataColors = dataColors;
        rows.putAll(modificationMatrix.getRows());
        columns = modificationMatrix.getCalculatedColumns();
        barChartValues.clear();
        fullItemsSet.clear();
        int coulmnIndx = 0;
        for (String key : columns.keySet()) {
            fullItemsSet.addAll(columns.get(key));
            barChartValues.put(coulmnIndx++, (double) columns.get(key).size());
        }
        drawLayout();
    }
    
    private void reDrawLayout() {
        
        if (!allowRedraw) {
            return;
        }
        if (mainWidth < minWidth) {
            mainWidth = minWidth;
        }
        int step;
        if (columnMap.size() > 1) {
            step = (mainWidth - rowLabelsWidth - 20) / (columnMap.size());
        } else {
            step = (mainWidth - rowLabelsWidth - 20);
        }
        int steps;
        if ((columnMap.size() % 2) == 0) {
            steps = columnMap.size() - 1;
        } else {
            steps = columnMap.size();
        }
        int corrector = (mainWidth - rowLabelsWidth) - (((columnMap.size() - 1) * step) + 20);
        int x = rowLabelsWidth + corrector;
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
        spacer.setWidth(((x - step + 15) - (rowLabelsWidth + corrector)), Unit.PIXELS);
        container.setWidth(mainWidth, Unit.PIXELS);
        
    }
    
    private void drawLayout() {
        allowRedraw = false;
        container.removeAllComponents();
        rowLabelsMap.clear();
        columnMap.clear();
        nodesTable.clear();
        
        int currentX = 0;
        int currentY = 0;
        int max = (-1* Integer.MAX_VALUE);
        int min = Integer.MAX_VALUE;
        rowLabelsWidth = 0;
        
        Map<String, Integer> effictiveRowMap = new LinkedHashMap<>();
        rows.keySet().forEach((rowKey) -> {
            int numInRow = 0;
            for (int columnIndex : barChartValues.keySet()) {
                int columnValue = barChartValues.get(columnIndex).intValue();
                if (columnValue == 0) {
                    continue;
                }
                String columnKey = columns.keySet().toArray()[columnIndex] + "";
                if (columnKey.contains(rowKey)) {
                    numInRow += barChartValues.get(columnIndex);
                }
            }
            if (numInRow > 0) {
                effictiveRowMap.put(rowKey, numInRow);
            }
        });
        ;
        
        for (String row : effictiveRowMap.keySet()) {
            int i = effictiveRowMap.get(row);
            rowLabelsWidth = Math.min(Math.max(rowLabelsWidth, (row).length()),200);
            if (max < i) {
                max = i;
            }
            if (min > i) {
                min = i;
            }
            
        }
        rowLabelsWidth = Math.min((rowLabelsWidth * 5) + 5 + 70,200);
        int barchartHeight = 130;//Math.min(Math.max(100, mainHeight - (effictiveRows * 25)), 200); //start drawing columns
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
            int bheight = (int) Math.max((Math.log(columnValue) * 10.0) * factor, 4) + 30;
            String columnKey = columns.keySet().toArray()[columnIndex] + "";
            HBarWithLabel bar = new HBarWithLabel(columnValue + "", barChartValues.get(columnIndex).intValue());
            bar.setWidth(15, Unit.PIXELS);
            bar.setHeight(bheight, Unit.PIXELS);
            bar.addLayoutClickListener(barListener);
            container.addComponent(bar, "left:" + currentX + "px; top:" + (barchartHeight - bheight) + "px;");
            currentX += 20;
            bar.setData(columnIndex);
            String mod = columns.keySet().toArray()[columnIndex++].toString().replace("[", "").replace("]", "").replace(",", "<br/>") + "<font style='font-size:10px !important;margin-right:5px'><br/>" + VaadinIcons.HASH.getHtml() + "</font>Proteins" + " (" + ((int) (double) barChartValues.get(columnIndex - 1)) + ")";
            for (String key : dataColors.keySet()) {
                if (mod.contains(key)) {
                    Color c = dataColors.get(key);
                    mod = mod.replace(key, "<font style='color:rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ");font-size:10px !important;margin-right:5px'> " + VaadinIcons.CIRCLE.getHtml() + "</font>" + key);
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
            sl.setData(rowIndex);
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
                    @Override
                    public void selectNode(int columnIndex) {//                           
                        if (selectedIndexes.contains(columnIndex)) {
                            selectedIndexes.remove(columnIndex);
                        } else {
                            selectedIndexes.add(columnIndex);
                        }
                        applyFilter(selectedIndexes);
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
        minWidth = finalwidth + 20;
        container.setWidth(minWidth, Unit.PIXELS);
        container.setHeight(finalheight + 30, Unit.PIXELS);
        allowRedraw = true;
        
    }
    
    private void selectColumn(Set<Comparable> columnIds) {
        unselectAll();
        if (columnIds.isEmpty()) {
            return;
        }
//        String columnId = columnIds.iterator().next() + "";
        columnIds.stream().map((columnId) -> {
            //            if (nodesTable.get(columnId).get(0).isSelected()) {
//                continue;
//            }
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
    
    public void resetFilter() {
        selectedIndexes.clear();
        unselectAll();
        
    }
    
    public void updateFilterSelection(Set<Comparable> selectedItems, Set<Comparable> selectedCategories, boolean topFilter, boolean singleProteinsFilter, boolean selfAction) {
        if (!selfAction) {
            barChartValues.clear();
            if (singleProteinsFilter && !selfAction && !selectedCategories.isEmpty()) {
                int coulmnIndex = 0;
                for (String key : columns.keySet()) {
                    double d = (double) columns.get(key).size();
                    barChartValues.put(coulmnIndex, d);
                    coulmnIndex++;
                }
            } else {
                Map<Integer, Double> tbarChartValues = new LinkedHashMap<>();
                int coulmnIndex = 0;
                for (String key : columns.keySet()) {
                    double d = (double) Sets.intersection(columns.get(key), selectedItems).size();
                    if (d > 0) {
                        tbarChartValues.put(coulmnIndex, d);
                    }
                    coulmnIndex++;
                }
                barChartValues.putAll(tbarChartValues);
                
            }
            drawLayout();
            reDrawLayout();
        }
        setMainAppliedFilter(topFilter && !selectedCategories.isEmpty());
        selectColumn(selectedCategories);
    }
    
    public Set<Comparable> filterAction(Set<Integer> columnIndexs) {
        if (columnMap.size() == 1 || columnIndexs == null || columnIndexs.isEmpty()) {
            return null;
        }
        Set<Comparable> appliedFilter = new LinkedHashSet<>();
        columnIndexs.forEach((columnIndex) -> {
            appliedFilter.add((columns.keySet().toArray()[columnIndex] + ""));
        });
        return appliedFilter;
    }
    
    public abstract void setMainAppliedFilter(boolean mainAppliedFilter);
    
    public abstract void applyFilter(Set<Integer> columnIndexs);
    
}
