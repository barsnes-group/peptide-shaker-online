package com.uib.web.peptideshaker.ui.components;

import com.compomics.util.experiment.biology.modifications.ModificationFactory;
import com.itextpdf.text.pdf.codec.Base64;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.model.FilterUpdatingEvent;
import com.uib.web.peptideshaker.model.Selection;
import com.uib.web.peptideshaker.ui.components.items.FilterButton;
import com.uib.web.peptideshaker.ui.interfaces.RegistrableFilter;
import com.uib.web.peptideshaker.uimanager.SelectionManager;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.MouseEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.jfree.chart.encoders.ImageEncoder;
import org.jfree.chart.encoders.ImageEncoderFactory;
import org.jfree.chart.encoders.ImageFormat;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class represents Modifications filter theat support both interactive
 * Venn diagram and matrix diagram the visualization is switching between the 2
 * diagrams based on the number of intersections
 *
 * @author Yehia Mokhtar Farag
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
    private final SelectionManager selectionManager;
    /**
     * Reset filter button.
     */
    private final FilterButton resetFilterbtn;
    /**
     * Matrix diagram to represent multiple data intersections .
     */
    private final MatrixDiagramFilter matrixDiagram;
    /**
     * Venn diagram to represent limited number of data intersections .
     */
    private final VennDiagram vennDiagram;
    /**
     * The post translational modifications factory.
     */
    private final ModificationFactory PTM = ModificationFactory.getInstance();
    private final Panel modificationFilterPanel;
    private final OptionGroup modificationOptionGroup;

    public ModificationsFilter(String title, String filterId, SelectionManager selectionManager) {
        this.filterId = filterId;
        this.selectionManager = selectionManager;
        this.selectionManager.RegistrDatasetsFilter(ModificationsFilter.this);
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
               reset();
            }
        };
        resetFilterbtn.setWidth(15, Unit.PIXELS);
        resetFilterbtn.setHeight(15, Unit.PIXELS);
        resetFilterbtn.setVisible(false);
        resetFilterbtn.addStyleName("btninframe");
        resetFilterbtn.addStyleName("modificationfilterreset");
        ModificationsFilter.this.addComponent(resetFilterbtn, "right:14px;top:-1px;");

        this.matrixDiagram = new MatrixDiagramFilter(selectionManager) {
//            @Override
//            public void setMainAppliedFilter(boolean mainAppliedFilter) {
//                ModificationsFilter.this.setMainAppliedFilter(mainAppliedFilter);
//            }

//            @Override
//            public void applyFilter(Set<Integer> columnIndexs) {
//                ModificationsFilter.this.applyFilter(columnIndexs);
//            }

            @Override
            public void setVisibleScrollbar(boolean visible) {
                modificationFilterPanel.setVisible(visible);
                if (visible) {
                    if (matrixDiagram.getBarChartX() > 0) {
                        modificationFilterPanel.setWidth((matrixDiagram.getBarChartX()), Unit.PIXELS);
                    }
                    modificationFilterPanel.setHeight((matrixDiagram.getBarEndY()), Unit.PIXELS);
                }

            }

        };
        ModificationsFilter.this.addComponent(matrixDiagram, "left:10px;top:30px;right:10px;bottom:10px;");
        matrixDiagram.setVisible(false);

        this.vennDiagram = new VennDiagram(selectionManager) {
            @Override
            public void compleateLoading(boolean done) {
                vennDiagram.setVisible(done);
                matrixDiagram.setVisible(!done);
                modificationFilterPanel.setVisible(!done);

            }

           
        };
//        ModificationsFilter.this.addComponent(vennDiagram, "left:0px;top:0px;");

        Button minimiseExpandBtn = new Button();
        minimiseExpandBtn.setStyleName(ValoTheme.BUTTON_ICON_ONLY);

        modificationOptionGroup = initModificationSelectionList();
        modificationFilterPanel = new Panel(modificationOptionGroup) {
            @Override
            public void setVisible(boolean visible) {
                minimiseExpandBtn.setVisible(visible);
                super.setVisible(visible);

            }

        };
        modificationFilterPanel.setSizeUndefined();
        modificationFilterPanel.setStyleName("modificationlistpanel");
        ModificationsFilter.this.addComponent(modificationFilterPanel, "left:10px;top:30px;");
        modificationFilterPanel.addClickListener((MouseEvents.ClickEvent event) -> {
            int y = event.getRelativeY();
            if (modificationFilterPanel.getStyleName().contains("compressmodpanel")) {
                modificationFilterPanel.removeStyleName("compressmodpanel");
                modificationFilterPanel.setIcon(VaadinIcons.ANGLE_DOUBLE_UP);
            } else if (y >= 11 && y <= 33) {
                int x = event.getRelativeX();
                int startX = (int) modificationFilterPanel.getWidth() - x;
                if (startX >= 17 && startX <= 39) {
                    modificationFilterPanel.addStyleName("compressmodpanel");
                    modificationFilterPanel.setIcon(VaadinIcons.FILTER);
                }

            }
        });
        modificationFilterPanel.setIcon(VaadinIcons.FILTER);
        modificationFilterPanel.addStyleName("compressmodpanel");
        modificationFilterPanel.setDescription("Select modifications");
    }

    private OptionGroup initModificationSelectionList() {

        OptionGroup modificationOptionGroup = new OptionGroup("Select an option");
        modificationOptionGroup.setSizeFull();
        modificationOptionGroup.setNullSelectionAllowed(true);
        modificationOptionGroup.setImmediate(true);
        modificationOptionGroup.setMultiSelect(true);
        modificationOptionGroup.addValueChangeListener((event) -> {
            Set<String> selection = (Set<String>) modificationOptionGroup.getValue();
            if (selection == null || selection.isEmpty()) {
                selection = new LinkedHashSet<>();
                for (Object Id : modificationOptionGroup.getItemIds()) {
                    selection.add(Id.toString());
                }
            }
             matrixDiagram.updateViewedModifications(selection);

        });

        return modificationOptionGroup;
    }

    private void populateModificationOptionGroup(Map<Comparable, Color> modificationsColorMap) {
        modificationOptionGroup.removeAllItems();
        for (Comparable modification : modificationsColorMap.keySet()) {
            modificationOptionGroup.addItem(modification);
            modificationOptionGroup.setItemIcon(modification, new ExternalResource(colorToIconResource(modificationsColorMap.get(modification))));

        }

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

    }

    @Override
    public void selectionChange(String type) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   

    private void setMainAppliedFilter(boolean mainAppliedFilter) {
        resetFilterbtn.setVisible(mainAppliedFilter);
        if (mainAppliedFilter) {
            this.addStyleName("highlightfilter");
        } else {
            this.removeStyleName("highlightfilter");
        }

    }

    private String colorToIconResource(Color c) {

        BufferedImage image = new BufferedImage(12, 12, BufferedImage.TYPE_INT_RGB);
        Graphics g2d = image.createGraphics();
        //draw sequence line

        g2d.setColor(c);
        g2d.fillRect(0, 0, 11, 11);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, 11, 11);
        byte[] imageData = null;
        try {
            ImageEncoder in = ImageEncoderFactory.newInstance(ImageFormat.PNG, 1);
            imageData = in.encode(image);
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
        String base64 = Base64.encodeBytes(imageData);
        base64 = "data:image/png;base64," + base64;
        //total chain coverage     
        return base64;
    }

    @Override
    public void updateSelection(FilterUpdatingEvent event) {
        Map<Comparable, Color> modificationColorMap = new LinkedHashMap<>();
        Map<Comparable, Set<Integer>> columns = new LinkedHashMap<>();
        Map<Comparable, Integer> rows = new LinkedHashMap<>();
        for (Comparable compKey : event.getSelectionMap().keySet()) {
            if (compKey.toString().contains("[")) {
                columns.put(compKey, new LinkedHashSet<>(event.getSelectionMap().get(compKey)));
            } else {
                rows.put(compKey, event.getSelectionMap().get(compKey).size());
            }
        }
        rows.keySet().forEach((mod) -> {
            if (PTM.containsModification(mod.toString())) {
                modificationColorMap.put(mod.toString(), new Color(ModificationFactory.getDefaultColor(mod.toString())));
            } else {
                modificationColorMap.put(mod.toString(), Color.LIGHT_GRAY);
            }
        });
        matrixDiagram.updateFilter(columns, rows, modificationColorMap, event.getSelectionCategories());
//        vennDiagram.updateFilter(columns, rows, modificationColorMap, event.getSelectionCategories());
        populateModificationOptionGroup(modificationColorMap);

        matrixDiagram.setVisible(true);
        modificationFilterPanel.setVisible(true);
        setMainAppliedFilter(event.getSelectionCategories() != null && !event.getSelectionCategories().isEmpty());
    }
    
    private void reset() {
        Selection selection = new Selection(CONSTANT.DATASET_SELECTION, CONSTANT.MODIFICATIONS_FILTER_ID, null, false);
        selectionManager.setSelection(selection);
    }
}
