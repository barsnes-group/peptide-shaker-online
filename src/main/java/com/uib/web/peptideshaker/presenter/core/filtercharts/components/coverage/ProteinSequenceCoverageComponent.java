package com.uib.web.peptideshaker.presenter.core.filtercharts.components.coverage;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * This class represents protein sequence coverage component.
 *
 * @author Yehia Farag
 */
public class ProteinSequenceCoverageComponent extends VerticalLayout {

    /**
     * Layout that contains peptides components with significant and not
     * significant fold change.
     */
    private VerticalLayout allPeptidesLayout;
    /**
     * Layout that contains peptides components with significant fold change
     * only.
     */
    private VerticalLayout significantPeptidesLayout;
    /**
     * List of peptide layout components.
     */
    private List< StackedBarPeptideComponent> stackedPeptides;
    /**
     * The protein sequence container that shows the protein coverage and
     * available quantified peptides with its information.
     */
    private PeptideSequenceContainer allPeptidesComponent;
    /**
     * The protein sequence container that shows the protein coverage and
     * available significantly quantified peptides with its information.
     */
    private PeptideSequenceContainer significantPeptidesComponent;
    /**
     * The protein sequence container width.
     */
    private double componentWidth = 0;
    /**
     * The protein sequence.
     */
    private final String sequence;
    /**
     * The protein name.
     */
    private final String proteinName;
    /**
     * Set of peptide objects that has all peptide information.
     */
    private final Set<QuantPeptide> quantPeptidesSet;
    /**
     * The index of the dataset that contain this component.
     */
    private final int datasetIndex;

    /**
     * Get protein sequence
     *
     * @return sequence of protein (imported from UniProt)
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * Get set of peptide objects that has all peptide information.
     *
     * @return quantPeptidesSet set of peptide objects
     */
    public Set<QuantPeptide> getQuantPeptidesSet() {
        return quantPeptidesSet;
    }

    /**
     * Get dataset index for this component (dataset contains this protein)
     *
     * @return datasetIndex dataset index in the database
     */
    public int getDatasetIndex() {
        return datasetIndex;
    }

    /**
     * Get the main protein name.
     *
     * @return proteinName.
     */
    public String getProteinName() {
        return proteinName;
    }

    /**
     * Constructor to initialize the main attributes.
     *
     * @param sequence protein sequence
     * @param quantPepSet set of quantitative peptides object that has the
     * peptides information
     * @param proteinSequenceContainerWidth the column with for generating
     * protein sequence coverage component
     * @param datasetIndex the dataset index for the protein row
     * @param proteinName the main protein name.
     */
    public ProteinSequenceCoverageComponent(String sequence, Set<QuantPeptide> quantPepSet, int proteinSequenceContainerWidth, final int datasetIndex, String proteinName) {
        this.setWidth(100, Unit.PERCENTAGE);
        this.addStyleName("roundedborder");
        this.addStyleName("padding20");
        this.addStyleName("whitelayout");
        this.quantPeptidesSet = quantPepSet;
        this.proteinName = proteinName;
        this.sequence = sequence;
        this.datasetIndex = datasetIndex;
        Label noPeptideAvailable = new Label("Peptide information not available");
        noPeptideAvailable.addStyleName(ValoTheme.LABEL_TINY);
        Set<QuantPeptide> filteredQuantPepSet = new LinkedHashSet<>();
        if (sequence == null || sequence.trim().equalsIgnoreCase("") || quantPepSet == null || quantPepSet.isEmpty()) {  //no peptides available
            this.addComponent(noPeptideAvailable);
        } else {
            quantPepSet.stream().filter((peptide) -> (peptide.getQuantDatasetIndex() == datasetIndex)).forEach((peptide) -> {
                filteredQuantPepSet.add(peptide);
            });
            if (filteredQuantPepSet.isEmpty()) {
                this.addComponent(noPeptideAvailable);
                return;
            }
            allPeptidesLayout = new VerticalLayout();
            this.addComponent(allPeptidesLayout);

            significantPeptidesLayout = new VerticalLayout();
            this.addComponent(significantPeptidesLayout);

            stackedPeptides = new ArrayList<>();
            final LinkedHashSet<StackedBarPeptideComponent> allPeptidesStackedBarComponentsMap = this.initAllBarChartComponents(false, proteinSequenceContainerWidth - 160, sequence, filteredQuantPepSet);
            stackedPeptides.addAll(allPeptidesStackedBarComponentsMap);
//            allPeptidesComponent = new PeptideSequenceContainer((int) componentWidth + 40, allPeptidesStackedBarComponentsMap, sequence);
            allPeptidesComponent = new PeptideSequenceContainer((proteinSequenceContainerWidth-110), allPeptidesStackedBarComponentsMap, sequence);
            allPeptidesLayout.addComponent(allPeptidesComponent);
            allPeptidesLayout.setComponentAlignment(allPeptidesComponent, Alignment.MIDDLE_CENTER);
            final LinkedHashSet<StackedBarPeptideComponent> significantPeptidesStackedBarComponentsMap = this.initAllBarChartComponents(true, proteinSequenceContainerWidth - 160, sequence, filteredQuantPepSet);
//            significantPeptidesComponent = new PeptideSequenceContainer((int) componentWidth + 40, significantPeptidesStackedBarComponentsMap, sequence);
            significantPeptidesComponent = new PeptideSequenceContainer((proteinSequenceContainerWidth-110), significantPeptidesStackedBarComponentsMap, sequence);
            significantPeptidesLayout.addComponent(significantPeptidesComponent);
            significantPeptidesLayout.setComponentAlignment(significantPeptidesComponent, Alignment.MIDDLE_CENTER);
            significantPeptidesLayout.setVisible(false);
            
        }
    }

    /**
     * Initialize the bar chart components for the protein sequence coverage.
     *
     * @param significatOnly Show peptides with significant fold change only.
     * @param width The column width for generating protein sequence coverage
     * component.
     * @param sequence The main protein sequence..
     * @param quantPeptidesSet Set of peptide objects that has all peptide
     * information.
     */
    private LinkedHashSet<StackedBarPeptideComponent> initAllBarChartComponents(boolean significatOnly, int width, String sequence, Set<QuantPeptide> quantPeptidesSet) {
        final LinkedHashSet<StackedBarPeptideComponent> barComponentMap = new LinkedHashSet<>();
        componentWidth = width;
        double charW = (double) ((double) width / (double) sequence.length());
        quantPeptidesSet.stream().filter((quantPeptide) -> !(quantPeptide.getPeptideSequence().equalsIgnoreCase("not available"))).map((quantPeptide) -> {
            return quantPeptide;
        }).forEach((quantPeptide) -> {
            String peptideSequence = quantPeptide.getPeptideSequence().trim();
            if (peptideSequence.contains(".")) {
                peptideSequence = quantPeptide.getPeptideSequence().replace(".", "").trim().substring(1, quantPeptide.getPeptideSequence().length() - 2);
            }
            double peptideLayoutWidth = Math.round(peptideSequence.length() * charW);
            int start = sequence.split(peptideSequence)[0].length() + 1;
            int end = sequence.split(peptideSequence)[0].length() + peptideSequence.length();
            int x0 = (int) Math.round((start * charW));
            if ((x0 + peptideLayoutWidth) > componentWidth) {
                componentWidth = componentWidth + ((x0 + peptideLayoutWidth) - componentWidth);
            }
            if (!significatOnly) {
                StackedBarPeptideComponent peptideStackedBarComponent = new StackedBarPeptideComponent(x0, (int) (peptideLayoutWidth),  quantPeptide.getPeptideModification(), quantPeptide, proteinName);
                peptideStackedBarComponent.setWidth((int) peptideLayoutWidth, Unit.PIXELS);
                if (sequence.startsWith("ZZZZZZZZ")) {
                    peptideStackedBarComponent.setDescription("" + 1 + "~" + quantPeptide.getPeptideSequence() + "~" + peptideSequence.length() + "");
                } else {
                    peptideStackedBarComponent.setDescription("" + start + "~" + quantPeptide.getPeptideSequence() + "~" + end + "");
                }
                peptideStackedBarComponent.setParam("peptide", quantPeptide);
                peptideStackedBarComponent.setParam("sequence", quantPeptide.getPeptideSequence());
                peptideStackedBarComponent.setParam("start", start);
                peptideStackedBarComponent.setParam("end", end);
                if (quantPeptide.getString_p_value().trim().equalsIgnoreCase("")) {
                    peptideStackedBarComponent.setSignificant(false);
                    peptideStackedBarComponent.setDefaultStyleShowAllMode("graystackedlayout");
                    peptideStackedBarComponent.setParam("trend", "noquant");
                } else if (quantPeptide.getString_p_value().equalsIgnoreCase("Significant")) {
                    peptideStackedBarComponent.setSignificant(true);
                    if (quantPeptide.getString_fc_value().equalsIgnoreCase("Increased") || quantPeptide.getString_fc_value().equalsIgnoreCase("Increase") || quantPeptide.getString_fc_value().equalsIgnoreCase("Up")) {
                        peptideStackedBarComponent.setDefaultStyleShowAllMode("redstackedlayout");
                        peptideStackedBarComponent.setParam("trend", "increased");
                    } else if (quantPeptide.getString_fc_value() == null || quantPeptide.getString_fc_value().equalsIgnoreCase("") || quantPeptide.getString_fc_value().trim().equalsIgnoreCase("Not Provided")) {
                        peptideStackedBarComponent.setDefaultStyleShowAllMode("lightbluestackedlayout");
                        peptideStackedBarComponent.setParam("trend", "equal");
                    } else {
                        peptideStackedBarComponent.setDefaultStyleShowAllMode("greenstackedlayout");
                        peptideStackedBarComponent.setParam("trend", "decreased");
                    }
                } else {
                    peptideStackedBarComponent.setSignificant(false);
                    if (quantPeptide.getString_fc_value().equalsIgnoreCase("Increased") || quantPeptide.getString_fc_value().equalsIgnoreCase("Increase") || quantPeptide.getString_fc_value().equalsIgnoreCase("Up")) {
                        peptideStackedBarComponent.setDefaultStyleShowAllMode("midredstackedlayout");
                        peptideStackedBarComponent.setParam("trend", "increased");
                    } else if (quantPeptide.getString_fc_value() == null || quantPeptide.getString_fc_value().equalsIgnoreCase("") || quantPeptide.getString_fc_value().trim().equalsIgnoreCase("Not Provided")) {
                        peptideStackedBarComponent.setDefaultStyleShowAllMode("lightbluestackedlayout");
                        peptideStackedBarComponent.setParam("trend", "equal");
                    } else {
                        peptideStackedBarComponent.setDefaultStyleShowAllMode("midgreenstackedlayout");
                        peptideStackedBarComponent.setParam("trend", "decreased");
                    }
                }
                barComponentMap.add(peptideStackedBarComponent);
            } else {
                if (quantPeptide.getString_p_value().equalsIgnoreCase("Significant")) {
                    StackedBarPeptideComponent peptideStackedBarComponent = new StackedBarPeptideComponent(x0, (int) (peptideLayoutWidth), quantPeptide.getPeptideModification(), quantPeptide, proteinName);
                    peptideStackedBarComponent.setSignificant(true);
                    peptideStackedBarComponent.setWidth((int) peptideLayoutWidth, Unit.PIXELS);
                    if (sequence.startsWith("ZZZZZZZZ")) {
                        peptideStackedBarComponent.setDescription("" + 0 + "~" + quantPeptide.getPeptideSequence() + "~" + peptideSequence.length() + "");
                    } else {
                        peptideStackedBarComponent.setDescription("" + start + "~" + quantPeptide.getPeptideSequence() + "~" + end + "");
                    }
                    peptideStackedBarComponent.setParam("peptide", quantPeptide);
                    peptideStackedBarComponent.setParam("sequence", quantPeptide.getPeptideSequence());
                    peptideStackedBarComponent.setParam("start", start);
                    peptideStackedBarComponent.setParam("end", end);

                    if (quantPeptide.getString_fc_value().equalsIgnoreCase("Increased") || quantPeptide.getString_fc_value().equalsIgnoreCase("Up")) {
                        peptideStackedBarComponent.setDefaultStyleShowAllMode("redstackedlayout");
                        peptideStackedBarComponent.setParam("trend", "increased");
                    } else if (quantPeptide.getString_fc_value() == null || quantPeptide.getString_fc_value().equalsIgnoreCase("") || quantPeptide.getString_fc_value().trim().equalsIgnoreCase("Not Provided")) {
                        peptideStackedBarComponent.setDefaultStyleShowAllMode("lightbluestackedlayout");
                        peptideStackedBarComponent.setParam("trend", "equal");
                    } else {
                        peptideStackedBarComponent.setDefaultStyleShowAllMode("greenstackedlayout");
                        peptideStackedBarComponent.setParam("trend", "decreased");

                    }

                    barComponentMap.add(peptideStackedBarComponent);
                }
            }
        });
        final LinkedHashSet<StackedBarPeptideComponent> updatedBarComponentMap = new LinkedHashSet<>();
        barComponentMap.stream().map((sbar) -> {
            return sbar;
        }).forEach((sbar) -> {
            updatedBarComponentMap.add(sbar);
        });
        return updatedBarComponentMap;
    }

    /**
     * Set show peptides layout with significant or not significant fold change
     *
     * @param showNotSignificantpeptide show peptides layout with significant or
     * not significant fold change
     */
    public void setShowNotSignificantPeptides(boolean showNotSignificantpeptide) {
        if (allPeptidesLayout == null) {
            return;
        }
        allPeptidesLayout.setVisible(showNotSignificantpeptide);
        significantPeptidesLayout.setVisible(!showNotSignificantpeptide);
    }

}
