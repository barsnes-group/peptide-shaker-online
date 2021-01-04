package com.uib.web.peptideshaker.ui.views.subviews.datasetview.components;

import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.model.VisualizationDatasetModel;
import com.uib.web.peptideshaker.ui.components.ChromosomesFilter;
import com.uib.web.peptideshaker.ui.components.DivaPieChartFilter;
import com.uib.web.peptideshaker.ui.components.DivaRangeFilter;
import com.uib.web.peptideshaker.ui.components.ModificationsFilter;
import com.uib.web.peptideshaker.uimanager.ResultsViewSelectionManager;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * This class represents main container for pie-chart filters the class
 * responsible for maintaining pie-charts interactivity
 *
 * @author Yehia Farag
 */
public class DatasetFiltersComponent extends HorizontalLayout {

    private final ModificationsFilter modificationFilter;

    private final DivaPieChartFilter validationFilter;

    private final DivaPieChartFilter ProteinInferenceFilter;

    private final ChromosomesFilter chromosomeFilter;

    private final DivaRangeFilter peptidesNumberFilter;
    private final DivaRangeFilter coverageFilter;
    private final DivaRangeFilter psmNumberFilter;

    private final AbsoluteLayout intinsityContainer;
    private final DivaRangeFilter intensityAllPeptidesRange;
    private final DivaRangeFilter intensityUniquePeptidesRange;

    private final VerticalLayout filterRightPanelContainer;
    private final VerticalLayout filterLeftPanelContainer;
    private final Map<String, Color> proteinInferenceColourMap;
    private final List<Color> colorList;
    /**
     * Array of default slice colours.
     */
    private final ResultsViewSelectionManager Selection_Manager;

    private int piWidth = -1;
    private int valWidth = -1;
    private boolean disableresize;

    /**
     * Initialise filter container
     *
     * @param Selection_Manager main selection manager
     */
    public DatasetFiltersComponent(ResultsViewSelectionManager Selection_Manager) {
        DatasetFiltersComponent.this.setSizeFull();
        DatasetFiltersComponent.this.setSpacing(true);
        DatasetFiltersComponent.this.setStyleName("datasetfilterstyle");
        proteinInferenceColourMap = new HashMap<>();
        proteinInferenceColourMap.put("Single", new Color(4, 180, 95));
        proteinInferenceColourMap.put("Related", new Color(245, 226, 80));
        proteinInferenceColourMap.put("Related & Unrelated", Color.ORANGE);
        proteinInferenceColourMap.put("Unrelated", new Color(213, 8, 8));
        this.Selection_Manager = Selection_Manager;
        colorList = new ArrayList<>(Arrays.asList(CONSTANT.DEFAULT_CHARTS_COLOURS));
        for (String str : CONSTANT.EXTRA_COLOURS) {
            Color c = hex2Rgb("#" + str);
            if (c.getRGB() == Color.WHITE.getRGB() || c.getRGB() == Color.BLACK.getRGB() || Arrays.asList(CONSTANT.DEFAULT_CHARTS_COLOURS).contains(c)) {
                continue;
            }
            colorList.add(c);
        }
        ProteinInferenceFilter = new DivaPieChartFilter("Protein Inference", "pi_filter", Selection_Manager) {
            @Override
            public void selectionChange(String type) {
                if (type.equalsIgnoreCase("dataset_filter_selection")) {

                }
            }

            @Override
            public void filterSizeChanged(int w, int h) {
            }

            @Override
            public void updateLegendWidth(int legWidth) {
                piWidth = legWidth;
                resetLegendSize(piWidth, valWidth);
            }

        };
        ProteinInferenceFilter.addStyleName("pifilter");
        filterLeftPanelContainer = new VerticalLayout();
        filterLeftPanelContainer.setHeight(100, Unit.PERCENTAGE);
        filterLeftPanelContainer.setWidth(100, Unit.PERCENTAGE);
        filterLeftPanelContainer.setSpacing(false);
        filterLeftPanelContainer.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
        filterLeftPanelContainer.addStyleName("cornerfiltercontainerstyle");
        DatasetFiltersComponent.this.addComponent(filterLeftPanelContainer);
        DatasetFiltersComponent.this.setExpandRatio(filterLeftPanelContainer, 1);
        filterLeftPanelContainer.addComponent(ProteinInferenceFilter);
        filterLeftPanelContainer.setComponentAlignment(ProteinInferenceFilter, Alignment.TOP_LEFT);

        validationFilter = new DivaPieChartFilter("Protein Validation", "validation_filter", Selection_Manager) {
            @Override
            public void selectionChange(String type) {
                if (type.equalsIgnoreCase("dataset_filter_selection")) {

                }
            }

            @Override
            public void filterSizeChanged(int w, int h) {
                ProteinInferenceFilter.sizeChanged(w, h);
            }

            @Override
            public void updateLegendWidth(int legWidth) {
                valWidth = legWidth;
                resetLegendSize(piWidth, valWidth);
            }

        };
        validationFilter.addStyleName("validationfilter");
        filterLeftPanelContainer.addComponent(validationFilter);
        filterLeftPanelContainer.setComponentAlignment(validationFilter, Alignment.TOP_CENTER);
        chromosomeFilter = new ChromosomesFilter("Chromosome", "chromosome_filter", Selection_Manager) {
            @Override
            public void selectionChange(String type) {
                if (type.equalsIgnoreCase("dataset_filter_selection")) {
                }
            }
        };
        chromosomeFilter.addStyleName("chromfilter");
        filterLeftPanelContainer.addComponent(chromosomeFilter);
        filterLeftPanelContainer.setComponentAlignment(chromosomeFilter, Alignment.TOP_RIGHT);
        chromosomeFilter.addStyleName("bottomfilter");

        VerticalLayout filterMiddlePanelContainer = new VerticalLayout();
        filterMiddlePanelContainer.setHeight(100, Unit.PERCENTAGE);
        filterMiddlePanelContainer.setWidth(100, Unit.PERCENTAGE);
        filterMiddlePanelContainer.setSpacing(true);
        filterMiddlePanelContainer.addStyleName("cornerfiltercontainerstyle");
        filterMiddlePanelContainer.addStyleName("middlefiltercontainerstyle");
        DatasetFiltersComponent.this.addComponent(filterMiddlePanelContainer);
        DatasetFiltersComponent.this.setComponentAlignment(filterMiddlePanelContainer, Alignment.TOP_LEFT);
        DatasetFiltersComponent.this.setExpandRatio(filterMiddlePanelContainer, 2);

        modificationFilter = new ModificationsFilter("Modifications", "modifications_filter", Selection_Manager) {

            @Override
            public void selectionChange(String type) {
//                if (type.equalsIgnoreCase("dataset_filter_selection")) {
////                    updateFilterSelection(Selection_Manager.getActiveProteinsSet(), Selection_Manager.getAppliedFilterCategories("modifications_filter"), Selection_Manager.isSingleProteinsFilter());
//                }
            }

        };
        modificationFilter.addStyleName("middlefiltercontainerstyle");
        filterMiddlePanelContainer.addComponent(modificationFilter);
        modificationFilter.setSizeFull();

        filterRightPanelContainer = new VerticalLayout();
        filterRightPanelContainer.setHeight(100, Unit.PERCENTAGE);
        filterRightPanelContainer.setWidth(100, Unit.PERCENTAGE);
        filterRightPanelContainer.setSpacing(false);
        filterRightPanelContainer.addStyleName("cornerfiltercontainerstyle");
        filterRightPanelContainer.addStyleName("rightpanelfilter");
        filterRightPanelContainer.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
        DatasetFiltersComponent.this.addComponent(filterRightPanelContainer);
        DatasetFiltersComponent.this.setComponentAlignment(filterRightPanelContainer, Alignment.TOP_LEFT);
        DatasetFiltersComponent.this.setExpandRatio(filterRightPanelContainer, 1);

        //add range filter
        intensityAllPeptidesRange = new DivaRangeFilter("Intensity (% - All peptides)", "intensityAllPep_filter", this.Selection_Manager) {
            @Override
            public void selectionChange(String type) {

            }

            @Override
            public void setVisible(boolean visible) {
                intensityUniquePeptidesRange.setVisible(!visible);
                super.setVisible(visible);
            }

        };
        intensityAllPeptidesRange.addStyleName("intallpepfilter");
        intensityUniquePeptidesRange = new DivaRangeFilter("Intensity (% - Unique peptides)", "intensityUniquePep_filter", this.Selection_Manager) {
            @Override
            public void selectionChange(String type) {

            }
        };
        intensityUniquePeptidesRange.addStyleName("intuniquepepfilter");
        intinsityContainer = new AbsoluteLayout();
        intinsityContainer.setSizeFull();
        intinsityContainer.addComponent(intensityAllPeptidesRange, "left:0px;top:5px;bottom:0px !important;right:0px;");
        intensityAllPeptidesRange.setVisible(true);
        intinsityContainer.addComponent(intensityUniquePeptidesRange, "left:0px;top:5px;bottom:0px;right:0px;");
        intinsityContainer.addStyleName("intinsfilter");
        peptidesNumberFilter = new DivaRangeFilter("#Peptides", "peptidesNum_filter", this.Selection_Manager) {
            @Override
            public void selectionChange(String type) {

            }

        };
        peptidesNumberFilter.addStyleName("pepNumfilter");
        filterRightPanelContainer.addComponent(peptidesNumberFilter);
        filterRightPanelContainer.setComponentAlignment(peptidesNumberFilter, Alignment.TOP_LEFT);

        psmNumberFilter = new DivaRangeFilter("#PSMs", "psmNum_filter", this.Selection_Manager) {
            @Override
            public void selectionChange(String type) {
            }

        };
        psmNumberFilter.addStyleName("psmnumfilter");
        filterRightPanelContainer.addComponent(psmNumberFilter);
        filterRightPanelContainer.setComponentAlignment(psmNumberFilter, Alignment.TOP_CENTER);

        coverageFilter = new DivaRangeFilter("Coverage (%)", "possibleCoverage_filter", this.Selection_Manager) {
            @Override
            public void selectionChange(String type) {
            }

        };
        coverageFilter.addStyleName("coveragefilter");
        filterRightPanelContainer.addComponent(coverageFilter);
        coverageFilter.addStyleName("bottomfilter");
        coverageFilter.addStyleName("correctresetbtn");
        filterRightPanelContainer.setComponentAlignment(coverageFilter, Alignment.TOP_CENTER);

        filterRightPanelContainer.addComponent(intinsityContainer);
        filterRightPanelContainer.setComponentAlignment(intinsityContainer, Alignment.BOTTOM_RIGHT);

    }

    /**
     * Update filters data
     *
     * @param toShareDataset dataset is shared by user
     * @param modificationMatrix main modification matrix
     * @param modificationsColorMap main modification colour map
     * @param chromosomeMap chromosomes map for the dataset
     * @param piMap protein inference map for the dataset
     * @param proteinValidationMap protein validation map for the dataset
     * @param proteinPeptidesNumberMap protein to peptides number map
     * @param proteinPSMNumberMap proteins to PSM number map
     * @param proteinCoverageMap protein coverage map
     * @param proteinIntinsityAllPepMap protein intensity map (based on all
     * peptides intensity)
     * @param proteinIntinsityUniquePepMap protein intensity map (based on
     * unique peptides intensity)
     */
    public void updateFiltersData(VisualizationDatasetModel dataset) {
      
//        Selection_Manager.reset();
//        Selection_Manager.setModificationsMap(modificationMatrix);
        modificationFilter.initializeFilterData(dataset.getModificationMatrixModel(), new HashSet<>(), Selection_Manager.getFullProteinSet().size());
        chromosomeFilter.initializeFilterData(dataset.getChromosomeMap());
//        Selection_Manager.setChromosomeMap(chromosomeMap);
        ProteinInferenceFilter.initializeFilterData(dataset.getProteinInferenceMap(), proteinInferenceColourMap);
//        Selection_Manager.setPiMap(piMap);
//        Selection_Manager.setProteinValidationMap(proteinValidationMap);
        validationFilter.initializeFilterData(dataset.getProteinValidationMap(), new ArrayList<>(Arrays.asList(CONSTANT.PROTEIN_VALIDATION_COLOURS)));
//        Selection_Manager.setProteinCoverageMap(proteinCoverageMap);
//        Selection_Manager.setProteinIntinsityAllPepMap(proteinIntinsityAllPepMap);
//        Selection_Manager.setProteinIntinsityUniquePepMap(proteinIntinsityUniquePepMap);
//        Selection_Manager.setProteinPSMNumberMap(proteinPSMNumberMap);
//        Selection_Manager.setProteinPeptidesNumberMap(proteinPeptidesNumberMap);
        peptidesNumberFilter.initializeFilterData(dataset.getValidatedPetideMap());
        psmNumberFilter.initializeFilterData(dataset.getValidatedPsmsMap());
        coverageFilter.initializeFilterData(dataset.getValidatedCoverageMap());
        if (dataset.getDatasetType().equals(CONSTANT.ID_DATASET)) {
            Label noquant = new Label("<center> No quant data available </center>", ContentMode.HTML);
            noquant.setSizeFull();
            noquant.setStyleName("noquantlabel");
            intensityAllPeptidesRange.addComponent(noquant);
            intensityUniquePeptidesRange.suspendFilter(true);
            intensityAllPeptidesRange.suspendFilter(true);

        } else {
//            intensityAllPeptidesRange.initializeFilterData(dataset.getAllPeptideIntensityMap());
//            intensityUniquePeptidesRange.initializeFilterData(dataset.getUniquePeptideIntensityMap());

        }
//        if (toShareDataset) {
//            peptidesNumberFilter.redrawChart();
//        }

    }

    /**
     * Convert hashed colour to AWT colour object
     *
     * @param colorStr e.g. "#FFFFFF"
     * @return AWT colour object
     */
    private Color hex2Rgb(String colorStr) {
        return new Color(
                Integer.valueOf(colorStr.substring(1, 3), 16),
                Integer.valueOf(colorStr.substring(3, 5), 16),
                Integer.valueOf(colorStr.substring(5, 7), 16));
    }

    /**
     * Update intensity filter (based on all peptides) data
     *
     * @param allPeptideInteinsity intensity (based on all peptides) data
     */
    public void updateQuantFilter(boolean allPeptideInteinsity) {
        this.intensityAllPeptidesRange.setVisible(allPeptideInteinsity);

    }

    /**
     * Get protein validation filter
     *
     * @return pie-chart filter
     */
    public DivaPieChartFilter getValidationFilter() {
        return validationFilter;
    }

    /**
     * Get protein inference filter
     *
     * @return pie-chart filter
     */
    public DivaPieChartFilter getProteinInferenceFilter() {
        return ProteinInferenceFilter;
    }

    /**
     * Get chromosomes filter
     *
     * @return Chromosomes Filter
     */
    public ChromosomesFilter getChromosomeFilter() {
        return chromosomeFilter;
    }

    /**
     * *Get modifications filter
     *
     * @return modifications filter
     */
    public ModificationsFilter getModificationFilter() {
        return modificationFilter;
    }

    /**
     * Get main right panel container layout for right side filters
     *
     * @return layout
     */
    public VerticalLayout getFilterRightPanelContainer() {
        return filterRightPanelContainer;
    }

    /**
     * Get main left panel container layout for left side filters
     *
     * @return layout
     */
    public VerticalLayout getFilterLeftPanelContainer() {
        return filterLeftPanelContainer;
    }

    /**
     * Get peptides number filter
     *
     * @return range filter
     */
    public DivaRangeFilter getPeptidesNumberFilter() {
        return peptidesNumberFilter;
    }

    /**
     * Get PSM number filter
     *
     * @return range filter
     */
    public DivaRangeFilter getPsmNumberFilter() {
        return psmNumberFilter;
    }

    /**
     * Get intensity filter (based on all peptides)
     *
     * @return range filter
     */
    public DivaRangeFilter getIntensityAllPeptidesRange() {
        return intensityAllPeptidesRange;
    }

    /**
     * Get intensity filter (based on unique peptides)
     *
     * @return range filter
     */
    public DivaRangeFilter getIntensityUniquePeptidesRange() {
        return intensityUniquePeptidesRange;
    }

    public void resetLegendSize(int pIWidth, int valWidth) {

        if (pIWidth == valWidth || pIWidth == -1 || valWidth == -1 || pIWidth == -0 || valWidth == 10 || disableresize) {
            return;
        }
        disableresize = true;
        int maxWidth = Math.max(valWidth, pIWidth) + 2;
        ProteinInferenceFilter.setLegendWidth(maxWidth);
        validationFilter.setLegendWidth(maxWidth);
    }

}
