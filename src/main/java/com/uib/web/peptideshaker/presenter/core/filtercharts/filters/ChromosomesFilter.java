package com.uib.web.peptideshaker.presenter.core.filtercharts.filters;

import com.google.common.collect.Sets;
import com.uib.web.peptideshaker.presenter.core.FilterButton;
import com.uib.web.peptideshaker.presenter.core.filtercharts.RegistrableFilter;
import com.uib.web.peptideshaker.presenter.core.filtercharts.components.RangeColorGenerator;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.SelectionManager;
import com.vaadin.event.LayoutEvents;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.*;

/**
 * This class represents matrix layout filter
 *
 * @author Yehia Farag
 */
public abstract class ChromosomesFilter extends AbsoluteLayout implements RegistrableFilter {

    private final String filterId;
    private final Panel mainFilterPanel;
    private final Label chartTitle;
    private final FilterButton resetFilterBtn;
    private final Set<Object> selectedCategories;
    private final Set<Comparable> appliedFilters;
    private final Map<Integer, Set<Comparable>> filteredData;
    private final AbsoluteLayout frame;
    private final SelectionManager Selection_Manager;
    private final Map<Comparable, Label> chromosomessLabelMap;
    private final Set<Comparable> selectedData;
    private final AbsoluteLayout mainChartContainer;
    private final LayoutEvents.LayoutClickListener mainClickListener;
    int activeChromosomes;
    private RangeColorGenerator colorGenerator;
    private Map<Integer, Set<Comparable>> fullData;
    private int totalItemsNumber;

    public ChromosomesFilter(String title, String filterId, SelectionManager Selection_Manager) {

        this.filterId = filterId;
        this.Selection_Manager = Selection_Manager;
        selectedData = new LinkedHashSet<>();
        this.appliedFilters = new LinkedHashSet<>();
        ChromosomesFilter.this.setSizeFull();

        frame = new AbsoluteLayout();
        frame.setSizeFull();
        frame.setStyleName("innerborderframe");
        frame.addStyleName("thumbfilterframe");
        frame.addStyleName("reorderlayout");
        frame.addStyleName("chromosomfilter");
        ChromosomesFilter.this.addComponent(frame);


        this.selectedCategories = new LinkedHashSet<>();
        this.Selection_Manager.RegistrDatasetsFilter(ChromosomesFilter.this);

        chartTitle = new Label("<font >" + title + "</font>", ContentMode.HTML);
        chartTitle.setStyleName(ValoTheme.LABEL_BOLD);
        chartTitle.setWidth(100, Unit.PERCENTAGE);
        chartTitle.setHeight(78, Unit.PIXELS);
        chartTitle.addStyleName("resizeabletext");
        frame.addComponent(chartTitle, "left:10px; top:10px;");
        resetFilterBtn = new FilterButton() {
            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                applyFilter(null);

            }
        };
//        resetFilterBtn.setWidth(10, Unit.PERCENTAGE);
//        resetFilterBtn.setHeight(10, Unit.PERCENTAGE);
        resetFilterBtn.setVisible(false);
        resetFilterBtn.addStyleName("btninframe");
        resetFilterBtn.addStyleName("chromosomefilterrest");
        ChromosomesFilter.this.addComponent(resetFilterBtn, "top:5px;right:0px;");


        mainFilterPanel = new Panel();
        mainFilterPanel.setHeight(100, Unit.PERCENTAGE);
        mainFilterPanel.setWidth(100, Unit.PERCENTAGE);
        mainFilterPanel.setStyleName(ValoTheme.PANEL_BORDERLESS);
        mainFilterPanel.addStyleName("floatbottom");
        mainFilterPanel.addStyleName("chromosomimgecontainer");
        frame.addComponent(mainFilterPanel, "top: 51px;left: 10px;right: 10px;bottom: 0px;");

        chromosomessLabelMap = new LinkedHashMap<>();
        this.filteredData = new LinkedHashMap<>();
        ;

        this.mainClickListener = (LayoutEvents.LayoutClickEvent event) -> {
            Component clickedComponent = event.getClickedComponent();
            if (clickedComponent instanceof Label) {
                if (clickedComponent == chartTitle) {
                    applyFilter(null);
                    return;
                }
                if (clickedComponent.getDescription().equalsIgnoreCase("No Proteins")) {
                    return;
                }
                applyFilter(((Label) clickedComponent).getData() + "");

            } else {
                applyFilter(null);
            }
        };
        mainChartContainer = initFilterLayout();
        mainFilterPanel.setContent(mainChartContainer);
    }

    public boolean isAppliedFilter() {
        return !(selectedData.isEmpty() || selectedData.size() == totalItemsNumber);
    }

    private AbsoluteLayout initFilterLayout() {
        chromosomessLabelMap.clear();
        HorizontalLayout filter = new HorizontalLayout();
        filter.addLayoutClickListener(mainClickListener);
        filter.setSpacing(false);
        filter.setSizeFull();

        for (int i = 1; i < 25; i++) {

            Label img = new Label("", ContentMode.HTML);
            img.setHeight(95, Unit.PERCENTAGE);
            img.setWidth(85, Unit.PERCENTAGE);
            img.setData(i);
            filter.addComponent(img);
            chromosomessLabelMap.put((i), img);

        }
        AbsoluteLayout filterContainer = new AbsoluteLayout();
        filterContainer.addStyleName("chromosomefiltercontainerstyle");
        filterContainer.setSizeFull();
        filterContainer.addComponent(filter);
        return filterContainer;
    }

    private void updateChromosomesLabelsColor() {
        activeChromosomes = 0;
        for (Label chromosomImg : chromosomessLabelMap.values()) {
            chromosomImg.removeStyleName("pointer");
            int chromosomId = (int) chromosomImg.getData();
            String color = "whitesmoke";
            String desc = "No proteins";
            String cursor = "";
            if (filteredData.containsKey(chromosomId) && !filteredData.get(chromosomId).isEmpty()) {
                activeChromosomes++;
                color = colorGenerator.getColor(filteredData.get(chromosomId).size());
                desc = filteredData.get(chromosomId).size() + " Proteins";
                cursor = "pointer";
            }
            chromosomImg.setValue("<img style= 'background-color: " + color + ";' src='VAADIN/themes/webpeptideshakertheme/img/chromosoms/" + (chromosomId) + ".png'>");

            chromosomImg.addStyleName(cursor);
            chromosomImg.addStyleName("resizableimg");
            chromosomImg.setDescription(desc);

        }
    }

    public void initializeFilterData(Map<Integer, Set<Comparable>> data) {
        this.fullData = data;
        filteredData.clear();
        filteredData.putAll(data);
        appliedFilters.clear();
        selectedCategories.clear();
        TreeSet<Integer> treeSet = new TreeSet<>();
        data.keySet().stream().map((key) -> data.get(key)).map((set) -> {
            treeSet.add(set.size());
            return set;
        }).forEachOrdered((set) -> {
            totalItemsNumber += set.size();
        });
        if (colorGenerator != null) {
            ChromosomesFilter.this.removeComponent(colorGenerator.getColorScale());
        }
        colorGenerator = new RangeColorGenerator(treeSet.last());
        frame.addComponent(colorGenerator.getColorScale(), "left:150px;top:12;right:35px");
        updateChromosomesLabelsColor();

    }

    private void selectCategory(Set<Comparable> categoryIds) {
        unselectAll();
        categoryIds.stream().map((id) -> chromosomessLabelMap.get(Integer.valueOf(id.toString()))).forEachOrdered((chromosomImg) -> {
            chromosomImg.addStyleName("resizableselectedimg");
        });
    }

    @Override
    public void updateFilterSelection(Set<Comparable> selectedItems, Set<Comparable> selectedCategories, boolean topFilter, boolean singleProteinsFilter, boolean selfAction) {
        if (!selfAction) {
            if (singleProteinsFilter && !selfAction && !selectedCategories.isEmpty()) {
                //reset filter value to oreginal 
                initializeFilterData(fullData);
            } else {
                filteredData.clear();
                TreeSet<Integer> treeSet = new TreeSet<>();
                for (int key : fullData.keySet()) {
                    Set<Comparable> set = fullData.get(key);
                    Set<Comparable> tSet = new LinkedHashSet<>(Sets.intersection(set, selectedItems));
                    treeSet.add(tSet.size());
                    filteredData.put(key, tSet);

                }
                if (colorGenerator != null) {
                    frame.removeComponent(colorGenerator.getColorScale());
                }
                colorGenerator = new RangeColorGenerator(treeSet.last());
                frame.addComponent(colorGenerator.getColorScale(), "left:150px;top:12;right:35px");
                updateChromosomesLabelsColor();

            }
        }
        selectCategory(selectedCategories);
        setMainAppliedFilter(topFilter && !selectedCategories.isEmpty());
    }

    private void unselectAll() {
        chromosomessLabelMap.values().forEach((chromosomImg) -> {
            chromosomImg.removeStyleName("resizableselectedimg");
        });
    }

    @Override
    public void redrawChart() {
    }

    @Override
    public String getFilterId() {
        return filterId;
    }

    public void resetFilter() {
        filteredData.clear();
        filteredData.putAll(fullData);
        selectedCategories.clear();
        appliedFilters.clear();
        List<Double> barChartData = new ArrayList<>();
        for (int key : fullData.keySet()) {
            int size = fullData.get(key).size();
            barChartData.add((double) size);
        }
        TreeSet<Double> barChartset = new TreeSet<>(barChartData);
        double counter = 0;
        List<Double> updatedBarChartData = new ArrayList<>();
        for (double linar : barChartData) {
            double sv = 0;
            if (linar > 0.0) {
                sv = scaleValues(linar, barChartset.last(), barChartset.first());
            }
            counter += sv;
            updatedBarChartData.add(sv);
        }
        barChartData.clear();
        for (double log : updatedBarChartData) {
            barChartData.add((double) ((int) (log * 100.0 / counter)));

        }
        unselectAll();

    }

    public void applyFilter(String chromosome) {
        if (activeChromosomes == 1) {
            return;
        }

        if (chromosome != null) {
            if (appliedFilters.contains(chromosome)) {
                appliedFilters.remove(chromosome);
            } else {
                appliedFilters.add(chromosome);
            }
        } else {
            appliedFilters.clear();
        }

        Selection_Manager.setSelection("dataset_filter_selection", appliedFilters, null, filterId);

    }

    public Set<Comparable> getSelectedData() {
        if (selectedData.isEmpty()) {
            selectedData.addAll(filteredData.keySet());
        }
        return selectedData;
    }

    /**
     * Converts the value from linear scale to log scale. The log scale numbers
     * are limited by the range of the type float. The linear scale numbers can
     * be any double value.
     *
     * @param linearValue the value to be converted to log scale
     * @param max         The upper limit number for the input numbers
     * @param lowerLimit  the lower limit for the input numbers
     * @return the value in log scale
     */
    private double scaleValues(double linearValue, double max, double lowerLimit) {
        double logMax = (Math.log(max) / Math.log(2));
        double logValue = (Math.log(linearValue + 1) / Math.log(2));
        logValue = (logValue * 2 / logMax) + lowerLimit;
        return Math.min(logValue, max);
    }

    @Override
    public void suspendFilter(boolean suspend) {
    }

    private void setMainAppliedFilter(boolean mainAppliedFilter) {
        resetFilterBtn.setVisible(mainAppliedFilter);
        if (mainAppliedFilter) {
            this.addStyleName("highlightfilter");
        } else {
            this.removeStyleName("highlightfilter");
        }

    }
}
