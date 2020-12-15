package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components;

import com.ejt.vaadin.sizereporter.ComponentResizeEvent;
import com.ejt.vaadin.sizereporter.SizeReporter;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.ProteinGroupObject;
import com.uib.web.peptideshaker.ui.components.items.ActionLabel;
import com.uib.web.peptideshaker.presenter.core.ColorLabelWithPopupTooltip;
import com.uib.web.peptideshaker.presenter.core.filtercharts.components.RangeColorGenerator;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.coverage.ProteinCoverageComponent;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.collections15.map.LinkedMap;

import java.util.Map;
import java.util.Set;

/**
 * This class represents proteins coverage container component
 *
 * @author Yehia Farag
 */
public abstract class ProteinCoverageTable extends VerticalLayout {

    private final Table proteinCoverageTable;
    private final Map<Object, Object[]> tableData;
    private final AbsoluteLayout chainCoverageLayout;
    private int reIndex;
    private int rowIndex = 1;
    private boolean allowReize = false;
    private double intensityWidth = 120.0;
    private int lastCapturedWidth;
    private Map<String, ProteinGroupObject> proteinNodes;

    public ProteinCoverageTable(AbsoluteLayout chainCoverageLayout) {
        ProteinCoverageTable.this.setSizeFull();
        ProteinCoverageTable.this.setStyleName("proteincoveragetablecontainer");
        this.chainCoverageLayout = chainCoverageLayout;
        tableData = new LinkedMap<>();
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
        ProteinCoverageTable.this.addComponent(proteinCoverageTable);
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

    public void setSelectedItems(Set<Object> selectedProteinsItems, Set<Object> selectedPeptidesItems) {
        if (tableData.isEmpty() || !tableData.keySet().containsAll(selectedProteinsItems)) {
            return;
        }
        this.proteinCoverageTable.removeAllItems();
        reIndex = 1;
        selectedProteinsItems.stream().map((id) -> {
            tableData.get(id)[0] = reIndex++;
            this.proteinCoverageTable.addItem(tableData.get(id), id);
            return id;
        }).map((id) -> ((ProteinCoverageComponent) tableData.get(id)[4])).map((pcov) -> {
            pcov.selectSubComponents(selectedPeptidesItems);
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

    public Map<Object, Object[]> getTableData() {
        return tableData;
    }

    public void selectDataset(Map<String, ProteinGroupObject> proteinNodes, Map<String, PeptideObject> peptidesNodes, Set<Object> defaultSelectedProteinsItems, Set<Object> defaultSelectedPeptidesItems, RangeColorGenerator colorScale, boolean quantDataset) {
        tableData.clear();
        rowIndex = 1;
        this.proteinNodes = proteinNodes;
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
            ProteinCoverageComponent proteinLayout = new ProteinCoverageComponent(protein, peptidesNodes, colorScale) {
                @Override
                public void selectPeptide(Object proteinId, Object peptideId) {
                    ProteinCoverageTable.this.selectPeptide(proteinId, peptideId);
                    tableData.keySet().forEach((id) -> {
                        if (id.equals(proteinId)) {
                            ((ProteinCoverageComponent) tableData.get(id)[4]).selectPeptides(peptideId);
                        } else {
                            ((ProteinCoverageComponent) tableData.get(id)[4]).selectPeptides("");
                        }
                    });
                }

            };
            ActionLabel info = new ActionLabel(VaadinIcons.INFO, "Click to view protein information") {
                @Override
                public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                }
            };
            ColorLabelWithPopupTooltip intinsity = new ColorLabelWithPopupTooltip(protein.getAllPeptidesIntensity(), protein.getAllPeptideIintensityColor(), protein.getPercentageAllPeptidesIntensity());

            Link proteinAccLink = new Link(protein.getAccession(), new ExternalResource("http://www.uniprot.org/uniprot/" + protein.getAccession()));
            proteinAccLink.setTargetName("_blank");
            proteinAccLink.setStyleName("tablelink");

            tableData.put(protein.getAccession(), new Object[]{rowIndex++, proteinAccLink, protein.getDescription(), intinsity, proteinLayout});
        });
        setSelectedItems(defaultSelectedProteinsItems, defaultSelectedPeptidesItems);
    }

    private String generateCaptionWithTooltio(String caption, String tooltip) {
        return "<div class='tooltip'>" + caption + "<span class='tooltiptext'>" + tooltip + "</span></div>";
    }

    public abstract void selectPeptide(Object proteinId, Object peptideId);

}
