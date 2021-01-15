package com.uib.web.peptideshaker.ui.components;

import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.model.FilterUpdatingEvent;
import com.uib.web.peptideshaker.model.Selection;
import com.uib.web.peptideshaker.ui.components.items.FilterButton;
import com.uib.web.peptideshaker.ui.abstracts.RegistrableFilter;
import com.uib.web.peptideshaker.uimanager.SelectionManager;
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
    private final Map<Comparable, Set<Integer>> filteredData;
    private final AbsoluteLayout frame;
    private final SelectionManager selectionManager;
    private final Map<Comparable, Label> chromosomessLabelMap;
    private final AbsoluteLayout mainChartContainer;
    private final LayoutEvents.LayoutClickListener mainClickListener;
    int activeChromosomes;
    private RangeColorGenerator colorGenerator;

    public ChromosomesFilter(String title, String filterId, SelectionManager selectionManager) {

        this.filterId = filterId;
        this.selectionManager = selectionManager;
        ChromosomesFilter.this.setSizeFull();

        frame = new AbsoluteLayout();
        frame.setSizeFull();
        frame.setStyleName("innerborderframe");
        frame.addStyleName("thumbfilterframe");
        frame.addStyleName("reorderlayout");
        frame.addStyleName("chromosomfilter");
        ChromosomesFilter.this.addComponent(frame);
        this.selectionManager.RegistrDatasetsFilter(ChromosomesFilter.this);
        chartTitle = new Label("<font >" + title + "</font>", ContentMode.HTML);
        chartTitle.setStyleName(ValoTheme.LABEL_BOLD);
        chartTitle.setWidth(100, Unit.PERCENTAGE);
        chartTitle.setHeight(78, Unit.PIXELS);
        chartTitle.addStyleName("resizeabletext");
        frame.addComponent(chartTitle, "left:10px; top:10px;");
        resetFilterBtn = new FilterButton() {
            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                reset();

            }
        };
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
        this.mainClickListener = (LayoutEvents.LayoutClickEvent event) -> {
            Component clickedComponent = event.getClickedComponent();
            if (clickedComponent instanceof Label) {
                if (clickedComponent == chartTitle || clickedComponent.getDescription().equalsIgnoreCase("No Proteins")) {
                    return;
                }
                if (clickedComponent.getStyleName().contains("resizableselectedimg")) {
                    unSelectCategory((int) ((Label) clickedComponent).getData());
                } else {
                    selectCategory((int) ((Label) clickedComponent).getData());
                }

            }
        };
        mainChartContainer = initFilterLayout();
        mainFilterPanel.setContent(mainChartContainer);
    }

    private AbsoluteLayout initFilterLayout() {
        chromosomessLabelMap.clear();
        HorizontalLayout filter = new HorizontalLayout();
        filter.addLayoutClickListener(mainClickListener);
        filter.setSpacing(false);
        filter.setSizeFull();

        for (int i = 1; i < 26; i++) {

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
        chromosomessLabelMap.values().stream().map((chromosomImg) -> {
            chromosomImg.removeStyleName("pointer");
            return chromosomImg;
        }).forEachOrdered((chromosomImg) -> {
            int chromosomId = (int) chromosomImg.getData();
            String color = "whitesmoke";
            String desc = "No proteins";
            String cursor = "";
            if (filteredData.containsKey(chromosomId) && !filteredData.get(chromosomId).isEmpty()) {
                activeChromosomes++;
                color = colorGenerator.getColor(filteredData.get(chromosomId).size());
                desc = filteredData.get(chromosomId).size() + " Proteins";
                if (chromosomId == 25) {
                    desc = "Other chromosomes, " + desc;
                }
                cursor = "pointer";
            }
            chromosomImg.setValue("<img style= 'background-color: " + color + ";' src='VAADIN/themes/webpeptideshakertheme/img/chromosoms/" + (chromosomId) + ".png'>");
            chromosomImg.addStyleName(cursor);
            chromosomImg.addStyleName("resizableimg");
            chromosomImg.setDescription(desc);

        });
    }

    private void localResetFilterData(Map<Comparable, Set<Integer>> localMap) {
        filteredData.clear();
        filteredData.putAll(localMap);
        TreeSet<Integer> treeSet = new TreeSet<>();
        localMap.keySet().stream().map((key) -> localMap.get(key)).map((set) -> {
            treeSet.add(set.size());
            return set;
        }).forEachOrdered((set) -> {
        });
        if (colorGenerator != null) {
            ChromosomesFilter.this.frame.removeComponent(colorGenerator.getColorScale());
        }
        colorGenerator = new RangeColorGenerator(treeSet.last());
        frame.addComponent(colorGenerator.getColorScale(), "left:150px;top:12px;right:35px");
        updateChromosomesLabelsColor();

    }

    private void selectCategory(Set<Comparable> categoryIds) {
        unselectAll();
        if (categoryIds != null) {
            categoryIds.stream().map((id) -> chromosomessLabelMap.get(Integer.valueOf(id.toString()))).forEachOrdered((chromosomImg) -> {
                chromosomImg.addStyleName("resizableselectedimg");
            });
        }
    }

    @Override
    public void updateFilterSelection(Set<Comparable> selectedItems, Set<Comparable> selectedCategories, boolean topFilter, boolean singleProteinsFilter, boolean selfAction) {

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

    @Override
    public void updateSelection(FilterUpdatingEvent updateEvent) {
        try {
            Map<Comparable, Set<Integer>> fullData = new LinkedHashMap<>();
            updateEvent.getSelectionMap().keySet().forEach((compKey) -> {
                if ((int) compKey != -1) {
                    if (!fullData.containsKey(compKey)) {
                        fullData.put(compKey, new HashSet<>());
                    }
                    fullData.get(compKey).addAll(updateEvent.getSelectionMap().get(compKey));
                }
            });

            if (updateEvent.getSeletionCategories() != null && updateEvent.getSeletionCategories().containsAll(fullData.keySet())) {
                reset();
                return;
            }

            localResetFilterData(fullData);
            selectCategory(updateEvent.getSeletionCategories());
            setMainAppliedFilter(updateEvent.getSeletionCategories() != null && !updateEvent.getSeletionCategories().isEmpty());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void selectCategory(Comparable category) {
        Set<Comparable> selectionCategories = new LinkedHashSet<>();
        selectionCategories.add(category);
        Selection selection = new Selection(CONSTANT.DATASET_SELECTION, filterId, selectionCategories, true);
        selectionManager.setSelection(selection);
    }

    private void unSelectCategory(Comparable category) {
        Set<Comparable> selectionCategories = new LinkedHashSet<>();
        selectionCategories.add(category);
        Selection selection = new Selection(CONSTANT.DATASET_SELECTION, filterId, selectionCategories, false);
        selectionManager.setSelection(selection);
    }

    private void reset() {
        Selection selection = new Selection(CONSTANT.DATASET_SELECTION, filterId, null, false);
        selectionManager.setSelection(selection);
    }

}
