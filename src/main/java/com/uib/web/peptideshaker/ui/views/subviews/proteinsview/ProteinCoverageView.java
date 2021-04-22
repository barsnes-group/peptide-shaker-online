package com.uib.web.peptideshaker.ui.views.subviews.proteinsview;

import com.ejt.vaadin.sizereporter.ComponentResizeEvent;
import com.ejt.vaadin.sizereporter.SizeReporter;
import com.uib.web.peptideshaker.model.PeptideObject;
import com.uib.web.peptideshaker.model.ProteinGroupObject;
import com.uib.web.peptideshaker.ui.components.items.ActionLabel;
import com.uib.web.peptideshaker.ui.components.items.ColorLabelWithPopupTooltip;
import com.uib.web.peptideshaker.ui.components.RangeColorGenerator;
import com.uib.web.peptideshaker.ui.views.subviews.proteinsview.components.ProteinCoverageComponent;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.LinkedHashMap;

import java.util.Map;

/**
 * This class represents proteins coverage container component
 *
 * @author Yehia Mokhtar Farag
 */
public abstract class ProteinCoverageView extends VerticalLayout {

    private final Table proteinCoverageTable;
    private final Map<String, Object[]> tableData;
    private final AbsoluteLayout chainCoverageLayout;
    private int reIndex;
    private int rowIndex = 1;
    private boolean allowReize = false;
    private double intensityWidth = 120.0;
    private int lastCapturedWidth;
    private Map<String, ProteinGroupObject> proteinNodes;
    private String selectedPeptideId;

    public ProteinCoverageView(AbsoluteLayout chainCoverageLayout) {
        ProteinCoverageView.this.setSizeFull();
        ProteinCoverageView.this.setStyleName("proteincoveragetablecontainer");
        this.chainCoverageLayout = chainCoverageLayout;
        tableData = new LinkedHashMap<>();
        proteinCoverageTable = new Table(" ");
        proteinCoverageTable.setStyleName(ValoTheme.TABLE_COMPACT);
        proteinCoverageTable.addStyleName(ValoTheme.TABLE_SMALL);
        proteinCoverageTable.addStyleName("inframetable");
        proteinCoverageTable.addStyleName("framedpanel");
        proteinCoverageTable.addStyleName(ValoTheme.TABLE_NO_STRIPES);
        proteinCoverageTable.setSelectable(false);
        proteinCoverageTable.setWidth(100, Unit.PERCENTAGE);
        proteinCoverageTable.setHeight(95, Unit.PERCENTAGE);
        proteinCoverageTable.addContainerProperty("index", Integer.class, null, "", null, Table.Align.LEFT);
        proteinCoverageTable.addContainerProperty("acc", Link.class, null, generateCaptionWithTooltio("Accession", "Protein accession"), null, Table.Align.CENTER);
        proteinCoverageTable.addContainerProperty("name", String.class, null, generateCaptionWithTooltio("Name", "Protein name"), null, Table.Align.LEFT);
        proteinCoverageTable.addContainerProperty("proteinIntensity", ColorLabelWithPopupTooltip.class, null, generateCaptionWithTooltio("Intensity", "Protein intinsity"), null, Table.Align.LEFT);

        proteinCoverageTable.addContainerProperty("coverage", ProteinCoverageComponent.class, null, generateCaptionWithTooltio("Coverage", "Protein sequence coverage"), null, Table.Align.LEFT);

        if ((boolean) VaadinSession.getCurrent().getAttribute("mobilescreenstyle")) {
            proteinCoverageTable.setColumnWidth("coverage", 600);
            proteinCoverageTable.setColumnWidth("proteinIntensity", (int) intensityWidth);
        } else {
            proteinCoverageTable.setColumnWidth("index", 37);
            proteinCoverageTable.setColumnWidth("acc", 80);
            proteinCoverageTable.setColumnWidth("name", 300);
            proteinCoverageTable.setColumnWidth("proteinIntensity", (int) intensityWidth);
        }

        proteinCoverageTable.setCacheRate(1);
        proteinCoverageTable.setBuffered(true);
        ProteinCoverageView.this.addComponent(proteinCoverageTable);
        proteinCoverageTable.addColumnResizeListener((Table.ColumnResizeEvent event) -> {
            if (!allowReize) {
                proteinCoverageTable.setColumnWidth(event.getPropertyId(), event.getPreviousWidth());
            }
        });
        SizeReporter tablesizeReporter = new SizeReporter(proteinCoverageTable);
        tablesizeReporter.addResizeListener((ComponentResizeEvent event) -> {
            //disable column resize listener
            allowReize = true;
            lastCapturedWidth = event.getWidth();
            double availableWidth = lastCapturedWidth - 37 - 80 - intensityWidth - 8 - 16;
            double nameWidth = availableWidth * 20.0 / 100.0;
            double coverageWidth = availableWidth - nameWidth;
            proteinCoverageTable.setColumnWidth("name", (int) nameWidth);
            proteinCoverageTable.setColumnWidth("coverage", (int) (coverageWidth));
            allowReize = false;
        });

    }

    public void setSelectedItems(Map<String, ProteinGroupObject> selectedProteinsItems, Map<String, PeptideObject> selectedPeptidesItems) {
        if (tableData.isEmpty() || !tableData.keySet().containsAll(selectedProteinsItems.keySet())) {
            return;
        }
        this.proteinCoverageTable.removeAllItems();
        reIndex = 1;
        selectedProteinsItems.keySet().stream().map((id) -> {
            tableData.get(id)[0] = reIndex++;
            this.proteinCoverageTable.addItem(tableData.get(id), id);
            return id;
        }).map((id) -> ((ProteinCoverageComponent) tableData.get(id)[4])).map((pcov) -> {
            if (selectedPeptidesItems != null) {
                pcov.selectSubComponents(selectedPeptidesItems.keySet());
            }
            return pcov;
        }).filter((pcov) -> (selectedProteinsItems.size() == 1)).map((pcov) -> {
            if (chainCoverageLayout != null && chainCoverageLayout.isAttached()) {
                chainCoverageLayout.setSizeUndefined();
                chainCoverageLayout.detach();
            }
            return pcov;
        }).map((pcov) -> {
            pcov.enable3D(chainCoverageLayout);
            return pcov;
        }).forEachOrdered((_item) -> {
        });

    }

    public void updateProteinsMode(String mode) {
        if (mode.contains("Unique Only")) {
            if (mode.contains("true")) {
                tableData.keySet().forEach((id) -> {
                    ((ColorLabelWithPopupTooltip) tableData.get(id)[3]).updateValues(proteinNodes.get(id).getUniquePeptidesIntensity(), proteinNodes.get(id).getUniquePeptideIintensityColor(), proteinNodes.get(id).getPercentageUniquePeptidesIntensity());
                });
            } else {
                tableData.keySet().forEach((id) -> {
                    ((ColorLabelWithPopupTooltip) tableData.get(id)[3]).updateValues(proteinNodes.get(id).getAllPeptidesIntensity(), proteinNodes.get(id).getAllPeptideIintensityColor(), proteinNodes.get(id).getPercentageAllPeptidesIntensity());
                });
            }
            return;

        }
        tableData.keySet().forEach((id) -> {
            ((ProteinCoverageComponent) tableData.get(id)[4]).updateStylingMode(mode);

        });

    }

    public void updateData(Object[] graphData, boolean quantDataset) {

//        Set<Object> defaultSelectedProteinsItems, Set<Object> defaultSelectedPeptidesItems
//        graphComponent.updateGraphData((ProteinGroupObject) graphData[0], (Map<String, ProteinGroupObject>) graphData[1], (Map<String, PeptideObject>) graphData[2], (HashMap<String, Set<String>>) graphData[3], (RangeColorGenerator) graphData[4], (RangeColorGenerator) graphData[5] == null, (RangeColorGenerator) graphData[5]);
        tableData.clear();
        rowIndex = 1;
        this.proteinNodes = (Map<String, ProteinGroupObject>) graphData[1];
        if (!quantDataset) {
            proteinCoverageTable.setColumnCollapsingAllowed(true);
            proteinCoverageTable.setColumnCollapsible("proteinIntensity", true);
            proteinCoverageTable.setColumnCollapsed("proteinIntensity", true);
            intensityWidth = 0;
            //disable column resize listener
            allowReize = true;
            double availableWidth = lastCapturedWidth - 37 - 80 - intensityWidth - 8 - 16;
            double nameWidth = availableWidth * 20.0 / 100.0;
            double coverageWidth = availableWidth - nameWidth;
            proteinCoverageTable.setColumnWidth("name", (int) nameWidth);
            proteinCoverageTable.setColumnWidth("coverage", (int) (coverageWidth));
            allowReize = false;
        }
        proteinNodes.values().forEach((protein) -> {
            ProteinCoverageComponent proteinLayout = new ProteinCoverageComponent(protein, (Map<String, PeptideObject>) graphData[2], (RangeColorGenerator) graphData[4]) {
                @Override
                public void selectPeptide(Map<String, ProteinGroupObject> selectedItems, Map<String, PeptideObject> selectedChildsItems, boolean isProteform) {
                    ProteinCoverageView.this.selectPeptide(selectedItems, selectedChildsItems, isProteform);
                    selectedPeptideId = null;
                    if (selectedChildsItems != null && selectedChildsItems.size() == 1) {
                        selectedPeptideId = selectedChildsItems.keySet().iterator().next();
                    }
                    tableData.keySet().forEach((id) -> {
                        if (selectedItems.containsKey(id)) {
                            ((ProteinCoverageComponent) tableData.get(id)[4]).selectPeptides(selectedPeptideId);
                        } else {
                            ((ProteinCoverageComponent) tableData.get(id)[4]).selectPeptides("");
                        }
                    });
                }

            };
//            ActionLabel info = new ActionLabel(VaadinIcons.INFO, "Click to view protein information") {
//                @Override
//                public void layoutClick(LayoutEvents.LayoutClickEvent event) {
//                }
//            };
            ColorLabelWithPopupTooltip intinsity = new ColorLabelWithPopupTooltip(protein.getAllPeptidesIntensity(), protein.getAllPeptideIintensityColor(), protein.getPercentageAllPeptidesIntensity());

            Link proteinAccLink = new Link(protein.getAccession(), new ExternalResource("http://www.uniprot.org/uniprot/" + protein.getAccession()));
            proteinAccLink.setTargetName("_blank");
            proteinAccLink.setStyleName("tablelink");
            tableData.put(protein.getAccession(), new Object[]{rowIndex++, proteinAccLink, protein.getDescription(), intinsity, proteinLayout});
        });
//        setSelectedItems(defaultSelectedProteinsItems, defaultSelectedPeptidesItems);
    }

    private String generateCaptionWithTooltio(String caption, String tooltip) {
        return "<div class='tooltip'>" + caption + "<span class='tooltiptext'>" + tooltip + "</span></div>";
    }

    public abstract void selectPeptide(Map<String, ProteinGroupObject> selectedProteins, Map<String, PeptideObject> selectedPeptides, boolean isProteform);

}
