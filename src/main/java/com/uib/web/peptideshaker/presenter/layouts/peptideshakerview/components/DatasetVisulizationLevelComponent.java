package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components;

import com.compomics.util.experiment.biology.modifications.ModificationFactory;
import com.ejt.vaadin.sizereporter.ComponentResizeEvent;
import com.ejt.vaadin.sizereporter.ComponentResizeListener;
import com.ejt.vaadin.sizereporter.SizeReporter;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.ProteinGroupObject;
import com.uib.web.peptideshaker.model.core.AlphanumComparator;
import com.uib.web.peptideshaker.model.core.ModificationMatrix;
import com.uib.web.peptideshaker.presenter.core.CSFPRLabel;
import com.uib.web.peptideshaker.presenter.core.ClosablePopup;
import com.uib.web.peptideshaker.presenter.core.ColorLabel;
import com.uib.web.peptideshaker.presenter.core.ColorLabelWithPopupTooltip;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.SelectionManager;
import com.uib.web.peptideshaker.presenter.core.SearchableTable;
import com.uib.web.peptideshaker.presenter.core.SparkLineLabel;
import com.uib.web.peptideshaker.presenter.core.TableColumnHeader;
import com.uib.web.peptideshaker.presenter.core.ValidationLabel;
import com.uib.web.peptideshaker.presenter.core.filtercharts.FiltersContainer;
import com.uib.web.peptideshaker.presenter.core.filtercharts.RegistrableFilter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.Link;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Table;
import com.vaadin.ui.themes.ValoTheme;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class represents filtered Table with graph view
 *
 * @author Yehia Farag
 */
public class DatasetVisulizationLevelComponent extends AbsoluteLayout implements RegistrableFilter {

    private final SearchableTable proteinTableContainer;
    private final FiltersContainer datasetFiltersContainer;
    private final Map<String, Integer> inferenceMap;
    private final LayoutEvents.LayoutClickListener selectionListener;
    private PeptideShakerVisualizationDataset peptideShakerVisualizationDataset;
    private Map<String, ProteinGroupObject> proteinTableMap;
    private double intensityColumnWidth = 120.0;
    private final Map<Integer, Component> filterComponentsMap;
    private int currentFilterView = 0;
    private final DecimalFormat df = new DecimalFormat("#.##");
    private final DecimalFormat df1 = new DecimalFormat("0.00E00");// new DecimalFormat("#.##");

    /**
     * The post translational modifications factory.
     */
    private final ModificationFactory PTM = ModificationFactory.getInstance();

    public DatasetVisulizationLevelComponent(SelectionManager Selection_Manager) {
        DatasetVisulizationLevelComponent.this.setSizeFull();
        DatasetVisulizationLevelComponent.this.addStyleName("scrollinsideframe");

        this.datasetFiltersContainer = new FiltersContainer(Selection_Manager);
        DatasetVisulizationLevelComponent.this.addComponent(datasetFiltersContainer);

        this.inferenceMap = new HashMap<>();
        this.inferenceMap.put("Single", 1);
        this.inferenceMap.put("Related", 2);
        this.inferenceMap.put("Related & Unrelated", 3);
        this.inferenceMap.put("Unrelated", 4);
        TableColumnHeader header1 = new TableColumnHeader("index", Integer.class, null, "", null, Table.Align.RIGHT);
        TableColumnHeader header2 = new TableColumnHeader("proteinInference", ColorLabel.class, null, generateCaptionWithTooltio("PI", "Protein inference"), null, Table.Align.CENTER);
        TableColumnHeader header4 = new TableColumnHeader("Accession", Link.class, null, generateCaptionWithTooltio("Accession", "Protein accession"), null, Table.Align.CENTER);
        TableColumnHeader header5 = new TableColumnHeader("csf", CSFPRLabel.class, null, generateCaptionWithTooltio("CSF", "View in CSF-PR"), null, Table.Align.CENTER);
        TableColumnHeader header6 = new TableColumnHeader("Name", String.class, null, generateCaptionWithTooltio("Name", "Protein name"), null, Table.Align.LEFT);
        TableColumnHeader header3 = new TableColumnHeader("proteinIntensity", ColorLabelWithPopupTooltip.class, null, generateCaptionWithTooltio("Intensity", "Protein intinsity"), null, Table.Align.LEFT);

        TableColumnHeader header7 = new TableColumnHeader("protein_group", String.class, null, generateCaptionWithTooltio("Protein Group", "Proteins accessions in the same group"), null, Table.Align.LEFT);
        TableColumnHeader header8 = new TableColumnHeader("geneName", String.class, null, generateCaptionWithTooltio("Gene", "Gene Name"), null, Table.Align.CENTER);
        TableColumnHeader header9 = new TableColumnHeader("chromosom", AlphanumComparator.class, null, generateCaptionWithTooltio("Chr", "Chromosome"), null, Table.Align.CENTER);
        TableColumnHeader header10 = new TableColumnHeader("coverage", SparkLineLabel.class, null, generateCaptionWithTooltio("Coverage", "Protein sequence coverage"), null, Table.Align.LEFT);
        TableColumnHeader header11 = new TableColumnHeader("peptides_number", SparkLineLabel.class, null, generateCaptionWithTooltio("#Peptides", "Number of validated peptides"), null, Table.Align.LEFT);
        TableColumnHeader header12 = new TableColumnHeader("psm_number", SparkLineLabel.class, null, generateCaptionWithTooltio("#PSMs", "Number of Peptide-Spectrum Matches"), null, Table.Align.LEFT);
        TableColumnHeader header13 = new TableColumnHeader("ms2Quant", SparkLineLabel.class, null, generateCaptionWithTooltio("MS2 Quant", "MS2 for protein quantitation"), null, Table.Align.LEFT);
        TableColumnHeader header14 = new TableColumnHeader("mwkDa", SparkLineLabel.class, null, generateCaptionWithTooltio("MW (kDa)", "molecular weight in kilodalton"), null, Table.Align.LEFT);
        TableColumnHeader header15 = new TableColumnHeader("confidence", SparkLineLabel.class, null, generateCaptionWithTooltio("Confidence", "Confidence level"), null, Table.Align.LEFT);
        TableColumnHeader header16 = new TableColumnHeader("validation", ValidationLabel.class, null, generateCaptionWithTooltio("", "Protein validation"), null, Table.Align.CENTER);

        TableColumnHeader[] tableHeaders = new TableColumnHeader[]{header1, header2, header4, header5, header6, header3, header7, header8, header9, header10, header11, header12, header13, header14, header15, header16};
        this.proteinTableContainer = new SearchableTable("Proteins", "Accession or protein name", tableHeaders) {
            private final Set<Comparable> selectedIds = new LinkedHashSet<>();

            @Override
            public void itemSelected(Object itemId) {
                selectedIds.clear();
                selectedIds.add(itemId + "");
                peptideShakerVisualizationDataset.selectUpdateProteins(selectedIds);
//                peptideShakerVisualizationDataset.processPSMFile();
                Selection_Manager.setSelection("protein_selection", selectedIds, null, getFilterId());

            }

        };
        this.proteinTableContainer.setStyleName("datasetproteinstablestyle");
        DatasetVisulizationLevelComponent.this.addComponent(proteinTableContainer);
        Selection_Manager.RegistrDatasetsFilter(DatasetVisulizationLevelComponent.this);
        selectionListener = (event) -> {
            proteinTableContainer.getMainTable().setValue(((AbstractOrderedLayout) event.getComponent()).getData());
        };
        SizeReporter reporter = new SizeReporter(proteinTableContainer.getMainTable());
        reporter.addResizeListener(new ComponentResizeListener() {
            private int lastWidth = -1;

            @Override
            public void sizeChanged(ComponentResizeEvent event) {
                Table mainTable = proteinTableContainer.getMainTable();
                if (mainTable.getItemIds().isEmpty() || event.getWidth() == lastWidth) {
                    return;
                }

                lastWidth = event.getWidth();
                double corrector = 1;
                if (lastWidth < (664 + intensityColumnWidth)) {
                    corrector = (double) lastWidth / (664.0 + intensityColumnWidth);
                }
                proteinTableContainer.suspendColumnResizeListener();
                mainTable.setColumnWidth("index", Math.max(50, (int) (50 * corrector)));
               
                mainTable.setColumnWidth("proteinInference", Math.max(37, (int) (37 * corrector)));
                mainTable.setColumnWidth("Accession", Math.max(60, (int) (60 * corrector)));
                mainTable.setColumnWidth("csf", Math.max(50, (int) (50 * corrector)));
                mainTable.setColumnWidth("coverage", Math.max(90, (int) (90 * corrector)));
                mainTable.setColumnWidth("peptides_number", Math.max(90, (int) (90 * corrector)));
                mainTable.setColumnWidth("psm_number", Math.max(
                        0, (int) (80 * corrector)));
//                mainTable.setColumnWidth("ms2Quant", Math.max(120, (int) (120 * corrector)));
//                mainTable.setColumnWidth("mwkDa", Math.max(120, (int) (120 * corrector)));
                mainTable.setColumnWidth("chromosom", Math.max(50, (int) (50 * corrector)));
                mainTable.setColumnWidth("validation", Math.max(32, (int) (32 * corrector)));
                mainTable.setColumnWidth("confidence", Math.max(90, (int) (90 * corrector)));
                mainTable.setColumnWidth("chromosom", Math.max(37, (int) (37 * corrector))); 
                
                mainTable.setColumnWidth("proteinIntensity", Math.max((int)intensityColumnWidth, (int) (intensityColumnWidth * corrector)));
                int nameColWidth = Math.max(120, lastWidth - ((int) ((664 + intensityColumnWidth) * corrector))+16);
                mainTable.setColumnWidth("Name", nameColWidth);
                proteinTableContainer.activateColumnResizeListener();
            }
        });

        this.filterComponentsMap = new HashMap<>();
        this.filterComponentsMap.put(1, this.datasetFiltersContainer.getProteinInferenceFilter());
        this.filterComponentsMap.put(2, this.datasetFiltersContainer.getValidationFilter());
        this.filterComponentsMap.put(3, this.datasetFiltersContainer.getChromosomeFilter());
        this.filterComponentsMap.put(4, this.datasetFiltersContainer.getModificationFilter());
        this.filterComponentsMap.put(5, this.datasetFiltersContainer.getFilterRightPanelContainer());
        this.filterComponentsMap.put(6, this.proteinTableContainer);

        DatasetVisulizationLevelComponent.this.showNext();

    }

    public void updateData(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
        this.peptideShakerVisualizationDataset = peptideShakerVisualizationDataset;
        if (!peptideShakerVisualizationDataset.isUploadedProject()) {
            peptideShakerVisualizationDataset.processDataFiles();
        }
        proteinTableMap = peptideShakerVisualizationDataset.getProteinsMap();
        final Table mainTable = proteinTableContainer.getMainTable();
        mainTable.sort(new Object[]{"peptides_number", "psm_number"}, new boolean[]{false, false});
        mainTable.setColumnCollapsible("protein_group", true);
        mainTable.setColumnCollapsed("protein_group", true);
        mainTable.setColumnCollapsible("geneName", true);
        mainTable.setColumnCollapsed("geneName", true);
        boolean smallScreen = (boolean) VaadinSession.getCurrent().getAttribute("smallscreenstyle");
        mainTable.setColumnCollapsible("mwkDa", true);
        mainTable.setColumnCollapsible("ms2Quant", true);
        mainTable.setColumnCollapsible("chromosom", smallScreen);
        mainTable.setColumnCollapsed("mwkDa", true);
        mainTable.setColumnCollapsed("ms2Quant", true);
        mainTable.setColumnCollapsed("chromosom", smallScreen);
        if (!peptideShakerVisualizationDataset.isQuantDataset()) {
            mainTable.setColumnCollapsible("proteinIntensity", true);
            mainTable.setColumnCollapsed("proteinIntensity", true);
            intensityColumnWidth = 0;
        }

        if (smallScreen) {
//            mainTable.setColumnWidth("index", 30);
//            mainTable.setColumnWidth("proteinInference", 20);
//            mainTable.setColumnWidth("Accession", 50);
//            mainTable.setColumnWidth("coverage", 70);
//            mainTable.setColumnWidth("peptides_number", 50);
//            mainTable.setColumnWidth("psm_number", 50);
//            mainTable.setColumnWidth("validation", 30);
//            mainTable.setColumnWidth("confidence", 50);

        } else {

//            mainTable.setColumnWidth("index", 50);
//            mainTable.setColumnWidth("proteinInference", 37);
//            mainTable.setColumnWidth("proteinIntensity", (int) intensityColumnWidth);
//            mainTable.setColumnWidth("Accession", 60);
//            mainTable.setColumnWidth("csf", 50);
//            mainTable.setColumnWidth("coverage", 100);
//            mainTable.setColumnWidth("peptides_number", 100);
//            mainTable.setColumnWidth("psm_number", 100);
//            mainTable.setColumnWidth("ms2Quant", 120);
//            mainTable.setColumnWidth("mwkDa", 120);
//            mainTable.setColumnWidth("chromosom", 50);
//            mainTable.setColumnWidth("validation", 32);
//            mainTable.setColumnWidth("confidence", 120);
//            mainTable.setColumnWidth("chromosom", 37);
        }

        proteinTableContainer.resetTable();
        Map<String, Object[]> tempTableDataMap = new LinkedHashMap<>();
        proteinTableMap.values().forEach((protein) -> {
            try {
                ColorLabel piLabel = new ColorLabel(inferenceMap.get(protein.getProteinInference().trim()), protein.getProteinInference(), protein.getProteinGroupKey(), selectionListener);
                Link proteinAccLink = new Link(protein.getAccession(), new ExternalResource("http://www.uniprot.org/uniprot/" + protein.getAccession().toUpperCase())) {
                    @Override
                    public String toString() {
                        return protein.getAccession();
                    }

                };
                proteinAccLink.setTargetName("_blank");
                proteinAccLink.setStyleName("tablelink");

                Map<String, Number> coverageValues = new LinkedHashMap<>();
                coverageValues.put("greenlayout", (float) protein.getCoverage() / 100f);
                SparkLineLabel coverageLabel = new SparkLineLabel(df.format(protein.getCoverage()), coverageValues, protein.getProteinGroupKey()) {
                    @Override
                    public void selected(Object itemId) {
                        mainTable.setValue(itemId);
                    }
                };

                Map<String, Number> peptidesNumValues = new LinkedHashMap<>();
                peptidesNumValues.put("greenlayout", (float) protein.getValidatedPeptidesNumber() / (float) peptideShakerVisualizationDataset.getMaxPeptideNumber());
                SparkLineLabel peptidesNumberLabelLabel = new SparkLineLabel(df.format(protein.getValidatedPeptidesNumber()), peptidesNumValues, protein.getProteinGroupKey()) {
                    @Override
                    public void selected(Object itemId) {
                        mainTable.setValue(itemId);
                    }
                };

                Map<String, Number> psmNumValues = new LinkedHashMap<>();
                psmNumValues.put("greenlayout", (float) protein.getValidatedPSMsNumber() / (float) peptideShakerVisualizationDataset.getMaxPsmNumber());
                SparkLineLabel psmNumberLabelLabel = new SparkLineLabel(df.format(protein.getValidatedPSMsNumber()), psmNumValues, protein.getProteinGroupKey()) {
                    @Override
                    public void selected(Object itemId) {
                        mainTable.setValue(itemId);
                    }
                };
                Map<String, Number> ms2QuantValues = new LinkedHashMap<>();
                ms2QuantValues.put("greenlayout", (float) protein.getSpectrumCounting() / (float) peptideShakerVisualizationDataset.getMaxMS2Quant());
                SparkLineLabel ms2QuantLabelLabel = new SparkLineLabel(df1.format(protein.getSpectrumCounting()), ms2QuantValues, protein.getProteinGroupKey()) {
                    @Override
                    public void selected(Object itemId) {
                        mainTable.setValue(itemId);
                    }
                };
                Map<String, Number> mwValues = new LinkedHashMap<>();
                mwValues.put("greenlayout", (float) protein.getMW() / (float) peptideShakerVisualizationDataset.getMaxMW());
                SparkLineLabel mwLabel = new SparkLineLabel(df1.format(protein.getMW()), mwValues, protein.getProteinGroupKey()) {
                    @Override
                    public void selected(Object itemId) {
                        mainTable.setValue(itemId);
                    }
                };

                Map<String, Number> confidentValues = new LinkedHashMap<>();
                confidentValues.put("greenlayout", (float) protein.getConfidence() / 100f);
                SparkLineLabel confidentLabel = new SparkLineLabel(df.format(protein.getConfidence()), confidentValues, protein.getProteinGroupKey()) {
                    @Override
                    public void selected(Object itemId) {
                        mainTable.setValue(itemId);
                    }
                };
                ValidationLabel validation = new ValidationLabel(protein.getValidation());
                validation.setData(protein.getProteinGroupKey());
                validation.addLayoutClickListener(selectionListener);

                CSFPRLabel csfprLink = new CSFPRLabel(protein.getAccession(), protein.isAvailableOn_CSF_PR());
                String searchKey = protein.getAccession() + "_" + protein.getDescription() + "_" + protein.getProteinGroup().replace(",", "_");

                ColorLabelWithPopupTooltip intinsity = new ColorLabelWithPopupTooltip(protein.getAllPeptidesIntensity(), protein.getAllPeptideIintensityColor(), protein.getPercentageAllPeptidesIntensity());
                tempTableDataMap.put(protein.getProteinGroupKey() + "--_--_--" + searchKey, new Object[]{protein.getIndex(), piLabel, proteinAccLink, csfprLink, protein.getDescription(), intinsity, protein.getProteinGroup(), protein.getGeneName(), new AlphanumComparator(protein.getChromosome()), coverageLabel, peptidesNumberLabelLabel, psmNumberLabelLabel, ms2QuantLabelLabel, mwLabel, confidentLabel, validation});
//                proteinTableContainer.addTableItem(protein.getProteinGroupKey(), new Object[]{protein.getIndex(), piLabel, proteinAccLink, csfprLink, protein.getDescription(), intinsity, protein.getProteinGroup(), protein.getGeneName(), new AlphanumComparator(protein.getChromosome()), coverageLabel, peptidesNumberLabelLabel, psmNumberLabelLabel, ms2QuantLabelLabel, mwLabel, confidentLabel, validation}, searchKey);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("at prot acc error " + protein.getAccession() + "  " + protein.getProteinInference() + "--" + protein.getProteinGroupKey() + " -- " + inferenceMap.containsKey(protein.getProteinInference()));

            }
        });
        for (String str : tempTableDataMap.keySet()) {
            proteinTableContainer.addTableItem(str.split("--_--_--")[0], tempTableDataMap.get(str), str.split("--_--_--")[1]);
        }
        this.proteinTableContainer.activateValueChangeListener();
        this.proteinTableContainer.updateLabel();
        Map<String, Color> ModificationColorMap = new LinkedHashMap<>();
        mainTable.sort();
        int index = 1;
        for (Object key : mainTable.getItemIds()) {
            mainTable.getItem(key).getItemProperty("index").setValue(index++);
        }
        mainTable.setSortEnabled(false);
        ModificationMatrix modificationMatrix = peptideShakerVisualizationDataset.getModificationMatrix();
        if (modificationMatrix == null || modificationMatrix.getRows() == null) {
            return;
        }
        modificationMatrix.getRows().keySet().forEach((mod) -> {
            if (PTM.containsModification(mod)) {
                ModificationColorMap.put(mod, new Color(ModificationFactory.getDefaultColor(mod)));
            } else {
                ModificationColorMap.put(mod, Color.LIGHT_GRAY);
            }
        });
        datasetFiltersContainer.updateFiltersData(peptideShakerVisualizationDataset.isToShareDataset()||peptideShakerVisualizationDataset.isUploadedProject(),modificationMatrix, ModificationColorMap, peptideShakerVisualizationDataset.getChromosomeMap(), peptideShakerVisualizationDataset.getProteinInferenceMap(), peptideShakerVisualizationDataset.getProteinValidationMap(), peptideShakerVisualizationDataset.getProteinPeptidesNumberMap(), peptideShakerVisualizationDataset.getProteinPSMNumberMap(), peptideShakerVisualizationDataset.getProteinCoverageMap(), peptideShakerVisualizationDataset.getProteinIntensityAllPeptideMap(), peptideShakerVisualizationDataset.getProteinIntensityUniquePeptideMap());

        if (peptideShakerVisualizationDataset.isQuantDataset()) {
            OptionGroup quantOptions = new OptionGroup();
            ClosablePopup service = new ClosablePopup("Protein Quantification", quantOptions, VaadinIcons.COG_O.getHtml() + " Quant settings");
            quantOptions.setHeight(60, Unit.PIXELS);
            quantOptions.setWidth(100, Unit.PERCENTAGE);
            quantOptions.setMultiSelect(false);
            quantOptions.setStyleName(ValoTheme.OPTIONGROUP_SMALL);
            quantOptions.addItem("All Peptides");
            quantOptions.addItem("Unique Peptides");
            quantOptions.setValue("All Peptides");
            quantOptions.addValueChangeListener((Property.ValueChangeEvent event) -> {
                updateQuantValues((quantOptions.getValue().toString().equals("All Peptides")));
            });

            proteinTableContainer.addServiceButton(service);
        }

    }

    private void updateQuantValues(boolean useAllPeptideAvg) {
        for (Object id : proteinTableContainer.getMainTable().getItemIds()) {
            ProteinGroupObject protein = proteinTableMap.get(id.toString());
            double updatedValue;
            String color;
            double per;
            if (useAllPeptideAvg) {
                updatedValue = protein.getAllPeptidesIntensity();
                color = protein.getAllPeptideIintensityColor();
                per = protein.getPercentageAllPeptidesIntensity();
            } else {
                updatedValue = protein.getUniquePeptidesIntensity();
                color = protein.getUniquePeptideIintensityColor();
                per = protein.getPercentageUniquePeptidesIntensity();
            }
            Item item = proteinTableContainer.getMainTable().getItem(id);
            ((ColorLabelWithPopupTooltip) item.getItemProperty("proteinIntensity").getValue()).updateValues(updatedValue, color, per);
            this.datasetFiltersContainer.updateQuantFilter(useAllPeptideAvg);

        }
    }

    @Override
    public void selectionChange(String type) {

//        if (type.equalsIgnoreCase("protein_selection")) {
//
//        }
//        if (type.equalsIgnoreCase("dataset_filter_selection")) {
////            
//        }
    }

    @Override
    public String getFilterId() {
        return "proteins_table_filter";
    }

    @Override
    public void updateFilterSelection(Set<Comparable> selection, Set<Comparable> selectedCategories, boolean topFilter, boolean selectOnly, boolean selfAction) {
        this.proteinTableContainer.filterTable(selection);
        proteinTableContainer.getMainTable().sort();
        int index = 1;
        for (Object key : proteinTableContainer.getMainTable().getItemIds()) {
            proteinTableContainer.getMainTable().getItem(key).getItemProperty("index").setValue(index++);
        }
        proteinTableContainer.getMainTable().setSortEnabled(false);
    }

    @Override
    public void redrawChart() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void suspendFilter(boolean suspend) {
    }

    private String generateCaptionWithTooltio(String caption, String tooltip) {
        return "<div class='tooltip'>" + caption + "<span class='tooltiptext'>" + tooltip + "</span></div>";

    }

    public int showNext() {
        filterComponentsMap.values().stream().map((view) -> {
            view.addStyleName("hidedsfilter");
            return view;
        }).forEachOrdered((view) -> {
            view.removeStyleName("viewdsfilter");
        });
        currentFilterView++;
        if (currentFilterView > 6) {
            currentFilterView = 1;
        }
        if (currentFilterView < 3) {
            filterComponentsMap.get(1).addStyleName("viewdsfilter");
            filterComponentsMap.get(1).removeStyleName("hidedsfilter");
            currentFilterView = 2;
        }
        filterComponentsMap.get(currentFilterView).addStyleName("viewdsfilter");
        filterComponentsMap.get(currentFilterView).removeStyleName("hidedsfilter");
        if (currentFilterView == 6) {
            datasetFiltersContainer.addStyleName("hidedsfilter");
        } else {
            datasetFiltersContainer.removeStyleName("hidedsfilter");
        }
        if (currentFilterView <= 2) {
            return 1;
        }
        return currentFilterView - 1;
    }

    public int showBefore() {
        filterComponentsMap.values().stream().map((view) -> {
            view.addStyleName("hidedsfilter");
            return view;
        }).forEachOrdered((view) -> {
            view.removeStyleName("viewdsfilter");
        });
        currentFilterView--;
        if (currentFilterView < 2) {
            currentFilterView = 6;
        }
        if (currentFilterView < 3) {
            filterComponentsMap.get(1).addStyleName("viewdsfilter");
            filterComponentsMap.get(1).removeStyleName("hidedsfilter");
            currentFilterView = 2;
        }
        filterComponentsMap.get(currentFilterView).addStyleName("viewdsfilter");
        filterComponentsMap.get(currentFilterView).removeStyleName("hidedsfilter");
        if (currentFilterView <= 2) {
            return 1;
        }
        return currentFilterView - 1;
    }

    /**
     * Returns the color object corresponding to the given rgb representation.
     *
     * @param colorRGB the color in rgb representation
     *
     * @return the color object
     */
    private Color getColor(int colorRGB) {
        return new Color((colorRGB >> 16) & 0xFF, (colorRGB >> 8) & 0xFF, colorRGB & 0xFF);
    }

}
