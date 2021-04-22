package com.uib.web.peptideshaker.ui.components;

import com.ejt.vaadin.sizereporter.ComponentResizeEvent;
import com.ejt.vaadin.sizereporter.SizeReporter;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.model.Selection;
import com.uib.web.peptideshaker.uimanager.SelectionManager;
import com.vaadin.event.LayoutEvents;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import d3diagrams.VennDiagramComponent;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.awt.*;
import java.util.*;

/**
 * @author Yehia Mokhtar Farag
 */
public abstract class VennDiagram extends AbsoluteLayout {

    private final VennDiagramComponent vennDiagramComponent;
    private final HorizontalLayout legendContainer;
    private final Map<String, String> indexMap;
    private final SizeReporter legendSizeReported;
    private final Set<Integer> selectedColumns;
    private final String vennDiagramfilter = "vennDiagramfilter";
    private final Map<String, JSONObject> tempDataset;
    private final String alphabet = "abcdefghijklmnopqrstuvwxyz".toUpperCase();
    private final Map<Comparable, String> nameToCharMap;
    private Map<Comparable, Color> dataColors;
    private JSONArray dataset;
    private JSONArray selectedDatasetColors;
    private JSONArray unselectedDatasetColors;
    /**
     * The width of the chart.
     */
    private int mainWidth;
    /**
     * The height of the chart.
     */
    private int mainHeight;
    private final SelectionManager selectionManager;

    /**
     *
     * @param selectionManager
     */
    public VennDiagram(SelectionManager selectionManager) {
        this.selectionManager = selectionManager;
        VennDiagram.this.setSizeFull();
        this.indexMap = new HashMap<>();
        this.nameToCharMap = new HashMap<>();
        this.selectedColumns = new LinkedHashSet<>();
        this.tempDataset = new LinkedHashMap<>();
        dataset = new JSONArray();
        selectedDatasetColors = new JSONArray();
        unselectedDatasetColors = new JSONArray();

        vennDiagramComponent = new VennDiagramComponent() {
            @Override
            public void SelectionPerformed(String value) {

                if (value.contains("error_in_loading")) {
                    compleateLoading(false);

                } else if (value.contains("loading_is_done")) {
                    compleateLoading(true);
                } else {

                    if (value.trim().equalsIgnoreCase("")) {
                        //unselectall
                        selectedColumns.clear();
                    } else {
                        String[] valueArr = value.split(",");
                        selectedColumns.clear();
                        for (String valueArr1 : valueArr) {
                             System.out.println("at select category "+((Label)legendContainer.getComponent(Integer.parseInt(valueArr1.trim()))).getData());
                            selectedColumns.add(Integer.parseInt(valueArr1.trim()));
                        }
                    }
                    updateLegendSelectionStyle();
                }

            }
        };
        vennDiagramComponent.addStyleName("vinndiagramcomponent");
        legendContainer = new HorizontalLayout();
        legendContainer.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
        legendContainer.setHeightUndefined();
        legendContainer.addStyleName("vennlegendcontainer");
        VennDiagram.this.addComponent(legendContainer, "bottom: 5px; left: 20px;");
        legendSizeReported = new SizeReporter(legendContainer);
        legendSizeReported.addResizeListener((ComponentResizeEvent event) -> {
            if (vennDiagramComponent != null) {
                VennDiagram.this.removeComponent(vennDiagramComponent);
                VennDiagram.this.addComponent(vennDiagramComponent, "left:20px; bottom:" + (event.getHeight() + 5) + "px; right:20px; top:30px;");
            }
        });
        legendContainer.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
            Label selection = (Label) event.getClickedComponent();
            int index = legendContainer.getComponentIndex(selection);
            if (index == -1) {
                return;
            }
            if (selectedColumns.contains(index)) {
                System.out.println("at remove category "+((Label)legendContainer.getComponent(index)).getData());
            } else {
                 System.out.println("at select category "+((Label)legendContainer.getComponent(index)).getData());
            }
        });

        SizeReporter reporter = new SizeReporter(vennDiagramComponent);
        reporter.addResizeListener((ComponentResizeEvent event) -> {
            int width = event.getWidth();
            int height = event.getHeight();
            if (mainWidth == width && mainHeight == height) {
                return;
            }
            sizeChanged(width, height);
        });
        VennDiagram.this.setId(vennDiagramfilter);

    }

    private void sizeChanged(int tChartWidth, int tChartHeight) {
        if (tChartWidth > 0 && tChartHeight > 0) {
            mainWidth = tChartWidth;
            mainHeight = tChartHeight;
            vennDiagramComponent.setSize(mainWidth - 40, mainHeight - 60);
            VaadinSession.getCurrent().setAttribute("modificationLayoutSize", new int[]{mainWidth, mainHeight});
        }

    }

    /**
     *
     */
    public void resetFilter() {
        selectedColumns.clear();
        updateLegendSelectionStyle();
        updateVennDiagramSelectionStyle();

    }

    private void updateDiagramData(Map<Comparable, Set<Integer>> columns, Map<Comparable, Integer> rows) {
        dataset = new JSONArray();
        selectedDatasetColors = new JSONArray();
        unselectedDatasetColors = new JSONArray();
        indexMap.clear();
        tempDataset.clear();
        legendContainer.removeAllComponents();
        Map<String, Integer> intersectionCategories = new HashMap<>();
        Map<String, Integer> mainCategories = new HashMap<>();
        int index = 0;
        int dsIndex = 0;
        for (Comparable key : columns.keySet()) {
            if (columns.get(key).isEmpty()) {
                continue;
            }
            if (!key.toString().contains(",")) {
                String rowKey = key.toString().replace("[", "").replace("]", "");
                if (!nameToCharMap.containsKey(rowKey)) {
                    nameToCharMap.put(rowKey, alphabet.charAt(index++) + "");
                }
                int logSize = rows.get(rowKey);
                dataset.put(initDatasetObject(new String[]{nameToCharMap.get(rowKey)}, logSize));
                indexMap.put((dsIndex++) + "", rowKey);
                Color c = dataColors.get(rowKey);
                String colorStr = "rgba(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ",1.0)";
                selectedDatasetColors.put(colorStr);
                legendContainer.addComponent(initLegendItem(rowKey, nameToCharMap.get(rowKey), colorStr, columns.get(key).size(), key));
                colorStr = "rgba(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ",0.3)";
                unselectedDatasetColors.put(colorStr);
                mainCategories.put(rowKey, logSize);
            } else {
                String[] intersections = key.toString().replace("[", "").replace("]", "").split(",");
                String[] updatedIntersection = new String[intersections.length];
                Map<String, String> charToColorMap = new LinkedHashMap<>();
                int z = 0;
                int logSize = columns.get(key).size();
                Color[] colorsToMix = new Color[intersections.length];
                for (String cer : intersections) {
                    cer = cer.trim();
                    if (!nameToCharMap.containsKey(cer)) {
                        nameToCharMap.put(cer, alphabet.charAt(index++) + "");
                    }
                    Color c = dataColors.get(cer);
                    colorsToMix[z] = c;
                    updatedIntersection[z++] = nameToCharMap.get(cer);
                    String colorStr = "rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")";
                    charToColorMap.put(nameToCharMap.get(cer), colorStr);

                }
                intersectionCategories.put(key.toString(), logSize);
                tempDataset.put(key.toString(), initDatasetObject(updatedIntersection, logSize));
                indexMap.put((dsIndex++) + "", key.toString());
                Color c = mixColors(colorsToMix).darker();
                String colorStr = "rgba(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ",1.0)";
                selectedDatasetColors.put(colorStr);
                c = c.brighter();
                colorStr = "rgba(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ",1)";
                unselectedDatasetColors.put(colorStr);
                legendContainer.addComponent(initLegendItem(charToColorMap, columns.get(key).size(), key));
            }
        }
        for (String key : intersectionCategories.keySet()) {
            String[] intersections = key.replace("[", "").replace("]", "").split(",");
            boolean allUnAvailable = true;
            for (String inter : intersections) {
                inter = inter.trim();
                if (mainCategories.containsKey(inter)) {
                    allUnAvailable = false;
                    break;
                }

            }
            if (allUnAvailable) {
                if (!nameToCharMap.containsKey(key)) {
                    nameToCharMap.put(key, alphabet.charAt(index++) + "");
                }
                dataset.put(initDatasetObject(new String[]{nameToCharMap.get(key)}, intersectionCategories.get(key)));
            } else {
                for (String inter : intersections) {
                    inter = inter.trim();
                    if (!mainCategories.containsKey(inter)) {
                        if (!nameToCharMap.containsKey(inter)) {
                            nameToCharMap.put(inter, alphabet.charAt(index++) + "");
                        }
                        int logSize = intersectionCategories.get(key);//((int) (Math.log(rows.get(key)) * 10)) + 1;
                        dataset.put(initDatasetObject(new String[]{nameToCharMap.get(inter)}, logSize));
                        indexMap.put((dsIndex++) + "", inter);
                        Color c = dataColors.get(inter);
                        String colorStr = "rgba(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ",1.0)";
                        selectedDatasetColors.put(colorStr);
                        legendContainer.addComponent(initLegendItem(inter, nameToCharMap.get(inter), colorStr, 0, "[" + inter + "]"));
                        colorStr = "rgba(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ",0.3)";
                        unselectedDatasetColors.put(colorStr);
                    }

                }
                dataset.put(tempDataset.get(key));
            }
        }
        if (legendContainer.getComponentCount() > 5) {
            compleateLoading(false);
        } else {

            if (mainWidth <= 0 || mainHeight <= 0) {
                int[] size = (int[]) VaadinSession.getCurrent().getAttribute("modificationLayoutSize");
                mainWidth = size[0];
                mainHeight = size[1];
            }
            vennDiagramComponent.setValue(dataset.toString() + ";" + selectedDatasetColors.toString() + ";" + unselectedDatasetColors.toString(), mainWidth - 40, mainHeight - 60);

        }

    }

    private void updateLegendSelectionStyle() {
        if (selectedColumns.size() == legendContainer.getComponentCount()) {
            selectedColumns.clear();
        }
        for (int i = 0; i < legendContainer.getComponentCount(); i++) {
            legendContainer.getComponent(i).removeStyleName("selectedvennlegend");
        }
        if (selectedColumns.isEmpty()) {
            return;
        }
        selectedColumns.forEach((i) -> {
            legendContainer.getComponent(i).addStyleName("selectedvennlegend");
        });

    }

    private void updateVennDiagramSelectionStyle() {
        JSONArray selection = new JSONArray();
        selectedColumns.forEach((i) -> {
            selection.put(i);
        });
        if (mainWidth <= 0 || mainHeight <= 0) {
            int[] size = (int[]) VaadinSession.getCurrent().getAttribute("modificationLayoutSize");
            mainWidth = size[0];
            mainHeight = size[1];
        }
        vennDiagramComponent.setValue(":selection:" + selection.toString(), (mainWidth - 40), (mainHeight - 60));
    }

    private JSONObject initDatasetObject(String[] ids, int size) {
        try {
            JSONObject dataObject = new JSONObject();
            JSONArray sublist = new JSONArray();
            for (String id : ids) {
                sublist.put(id.trim());
            }
            dataObject.put("sets", sublist);
            dataObject.put("size", size);

            return dataObject;
        } catch (JSONException ex) {
            System.out.println("at Error class: "+this.getClass().getName()+"  line: 308 "+ex);
        }
        return null;

    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            this.removeStyleName("hidevenn");
        } else {
            this.addStyleName("hidevenn");
        }
    }

    private Component initLegendItem(String title, String charRep, String color, int size, Comparable columnId) {
        Label legendItem = new Label("<div style='background:" + color + ";'>" + charRep + "</div>" + title + "<font>(" + size + ")</font>");
        legendItem.setWidth(100, Unit.PERCENTAGE);
        legendItem.setContentMode(ContentMode.HTML);
        legendItem.setStyleName("venndiagramlegend");
        legendItem.setData(columnId);
        return legendItem;

    }

    private Component initLegendItem(Map<String, String> charToColorMap, int size, Comparable columnId) {
        String labelContent = "";
        labelContent = charToColorMap.keySet().stream().map((charKey) -> "<div style='background:" + charToColorMap.get(charKey) + ";    margin-right: 0px !important;'>" + charKey + "</div>").reduce(labelContent, String::concat);
        labelContent += "<font>(" + size + ")</font>";
        Label legendItem = new Label(labelContent);
        legendItem.setWidth(100, Unit.PERCENTAGE);
        legendItem.setContentMode(ContentMode.HTML);
        legendItem.setStyleName("venndiagramlegend");
        legendItem.setData(columnId);
        return legendItem;

    }

    private Color mixColors(Color[] colors) {
        int r = 0;
        for (Color c : colors) {
            r += c.getRed();
        }
        r = r / colors.length;
        int g = 0;
        for (Color c : colors) {
            g += c.getGreen();
        }
        g = g / colors.length;

        int b = 0;
        for (Color c : colors) {
            b += c.getBlue();
        }
        b = b / colors.length;

        return new Color(r, g, b);

    }

    /**
     *
     * @param done
     */
    public abstract void compleateLoading(boolean done);

    /**
     *
     * @param columns
     * @param rows
     * @param dataColors
     * @param selectedCategories
     */
    public void updateFilter(Map<Comparable, Set<Integer>> columns, Map<Comparable, Integer> rows, Map<Comparable, Color> dataColors, Set<Comparable> selectedCategories) {
        nameToCharMap.clear();
        this.dataColors = dataColors;
        updateDiagramData(columns, rows);
        updateLegendSelectionStyle();
        //updatevenn diagram
        updateVennDiagramSelectionStyle();
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

}
