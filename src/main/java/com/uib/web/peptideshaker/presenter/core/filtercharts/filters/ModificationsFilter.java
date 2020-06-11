package com.uib.web.peptideshaker.presenter.core.filtercharts.filters;

import com.uib.web.peptideshaker.model.core.ModificationMatrix;
import com.uib.web.peptideshaker.presenter.core.FilterButton;
import com.uib.web.peptideshaker.presenter.core.filtercharts.RegistrableFilter;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.SelectionManager;
import com.vaadin.event.LayoutEvents;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import java.awt.Color;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class represents Modifications filter theat support both interactive
 * Venn diagram and matrix diagram the visualisation is switching between the 2
 * diagrams based on the number of intersections
 *
 * @author Yehia Farag
 */
public class ModificationsFilter extends AbsoluteLayout implements RegistrableFilter {

    /**
     * The filter id.
     */
    private final String filterId;
    /**
     * The filter title label component.
     */
    private final Label chartTitle;
    /**
     * The central selection manager.
     */
    private final SelectionManager Selection_Manager;
    /**
     * Reset filter button.
     */
    private final FilterButton resetFilterbtn;
    /**
     * Matrix diagram to represent multiple data intersections .
     */
    private final MatrixDiagramRedraw matrixDiagram;
    /**
     * Venn diagram to represent limited number of data intersections .
     */
    private final VennDiagram vennDiagram;
    
    public ModificationsFilter(String title, String filterId, SelectionManager Selection_Manager) {
        this.filterId = filterId;
        this.Selection_Manager = Selection_Manager;
        this.Selection_Manager.RegistrDatasetsFilter(ModificationsFilter.this);
        ModificationsFilter.this.setSizeFull();
        ModificationsFilter.this.setStyleName("thumbfilterframe");
        chartTitle = new Label("<font>" + title + "</font>", ContentMode.HTML);
        chartTitle.setStyleName(ValoTheme.LABEL_BOLD);
        chartTitle.setWidth(80, Unit.PIXELS);
        chartTitle.setHeight(20, Unit.PIXELS);
        chartTitle.addStyleName("resizeabletext");
        ModificationsFilter.this.addComponent(chartTitle, "left:10px;top:10px;");
        resetFilterbtn = new FilterButton() {
            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                applyFilter(null);
            }
        };
        resetFilterbtn.setWidth(15, Unit.PIXELS);
        resetFilterbtn.setHeight(15, Unit.PIXELS);
        resetFilterbtn.setVisible(false);
        resetFilterbtn.addStyleName("btninframe");
        resetFilterbtn.addStyleName("modificationfilterreset");
        ModificationsFilter.this.addComponent(resetFilterbtn, "right:14px;top:-1px;");
        
        this.matrixDiagram = new MatrixDiagramRedraw() {
            @Override
            public void setMainAppliedFilter(boolean mainAppliedFilter) {
                ModificationsFilter.this.setMainAppliedFilter(mainAppliedFilter);
            }
            
            @Override
            public void applyFilter(Set<Integer> columnIndexs) {
                ModificationsFilter.this.applyFilter(columnIndexs);
            }
            
        };
        ModificationsFilter.this.addComponent(matrixDiagram, "left:10px;top:30px;right:10px;bottom:10px;");
        matrixDiagram.setVisible(false);
       
        this.vennDiagram = new VennDiagram() {
            @Override
            public void compleateLoading(boolean done) {
                vennDiagram.setVisible(done);
                matrixDiagram.setVisible(!done);
                
            }
            
            @Override
            public void applyFilter(Set<Integer> columnIndexs) {
                ModificationsFilter.this.applyFilter(columnIndexs);
            }
            
        };
        ModificationsFilter.this.addComponent(vennDiagram, "left:0px;top:0px;");
        
    }
    
    public void initializeFilterData(ModificationMatrix modificationMatrix, Map<String, Color> dataColors, Set<Object> selectedCategories, int totalNumber) {
        matrixDiagram.initializeFilterData(modificationMatrix, dataColors, selectedCategories, totalNumber);
        vennDiagram.initializeFilterData(modificationMatrix, dataColors, selectedCategories, totalNumber);
        
    }
    
    @Override
    public void suspendFilter(boolean suspend) {
    }
    
    @Override
    public void redrawChart() {
//        matrixDiagram.redrawChart();
    }
    
    @Override
    public String getFilterId() {
        return filterId;
    }
    
    @Override
    public void updateFilterSelection(Set<Comparable> selectedItems, Set<Comparable> selectedCategories, boolean topFilter, boolean singleProteinsFilter, boolean selfAction) {
        
        matrixDiagram.updateFilterSelection(selectedItems, selectedCategories, topFilter, singleProteinsFilter, selfAction);
        vennDiagram.updateFilterSelection(selectedItems, selectedCategories, topFilter, singleProteinsFilter, selfAction);
        
    }
    
    @Override
    public void selectionChange(String type) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void applyFilter(Set<Integer> columnIndexs) {
        
        Set<Comparable> appliedFilter = matrixDiagram.filterAction(columnIndexs);
        if (columnIndexs == null || columnIndexs.isEmpty() || columnIndexs.size() == vennDiagram.getModificationMatrix().getCalculatedColumns().size()) {
            vennDiagram.resetFilter();
            matrixDiagram.resetFilter();
            Selection_Manager.setSelection("dataset_filter_selection", new LinkedHashSet<>(), null, filterId);
            return;
        }
        
        Selection_Manager.setSelection("dataset_filter_selection", appliedFilter, null, filterId);
    }
    
    private void setMainAppliedFilter(boolean mainAppliedFilter) {
        resetFilterbtn.setVisible(mainAppliedFilter);
        if (mainAppliedFilter) {
            this.addStyleName("highlightfilter");
        } else {
            this.removeStyleName("highlightfilter");
        }
        
    }
}
