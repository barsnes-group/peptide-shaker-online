package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components;

import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.ProteinGroupObject;
import com.uib.web.peptideshaker.presenter.core.filtercharts.components.RangeColorGenerator;
import com.uib.web.peptideshaker.presenter.core.graph.GraphComponent;
import com.vaadin.ui.VerticalLayout;
import graphmatcher.NetworkGraphComponent;
import graphmatcher.NetworkGraphEdge;

import java.util.*;

/**
 * this class represents interactive graph layout
 *
 * @author Yehia Farag
 */
public abstract class GraphsContainerComponent extends VerticalLayout {

    private final GraphComponent graphComponent;
    private final Map<String, ProteinGroupObject> proteinNodes;
    private final Map<String, PeptideObject> peptidesNodes;
    private final Set<PeptideObject> peptides;
    private final Map<String, ProteinGroupObject> unrelatedProt = new LinkedHashMap<>();
    private final Map<String, PeptideObject> unrelatedPeptides = new LinkedHashMap<>();
    private final HashMap<String, ArrayList<String>> edges;
    private final NetworkGraphComponent proteinsPathwayNewtorkGraph;
    private PeptideShakerVisualizationDataset peptideShakerVisualizationDataset;
    private String thumbURL;
    private int maxPsms;
    private RangeColorGenerator colorScale;

    public GraphsContainerComponent() {

        GraphsContainerComponent.this.setSizeFull();
        graphComponent = new GraphComponent() {
            @Override
            public void selectedItem(Set<Object> selectedItems, Set<Object> selectedChildsItems, boolean updateProteform) {
                GraphsContainerComponent.this.selectedItem(selectedItems, selectedChildsItems, false);//
                if (updateProteform) {
                    GraphsContainerComponent.this.updateProteoformGraphData(selectedItems);
                }
            }

            @Override
            public void updateProteinsMode(String modeType) {
                GraphsContainerComponent.this.updateProteinsMode(modeType);
            }

        };
        GraphsContainerComponent.this.addComponent(graphComponent);
        proteinNodes = new LinkedHashMap<>();
        peptidesNodes = new LinkedHashMap<>();
        peptides = new LinkedHashSet<>();
        edges = new HashMap<>();

        proteinsPathwayNewtorkGraph = new NetworkGraphComponent() {
            @Override
            public void selectedItem(Set<Object> selectedParentItems, Set<Object> selectedChildItems) {//
                if (proteinsPathwayNewtorkGraph.isVisible()) {
                    GraphsContainerComponent.this.selectedItem(selectedParentItems, selectedChildItems, true);
                    graphComponent.selectParentItem(selectedParentItems);
                }
            }

        };
        proteinsPathwayNewtorkGraph.setSizeFull();
        graphComponent.addProteoformGraphComponent(proteinsPathwayNewtorkGraph);

    }

    public RangeColorGenerator getColorScale() {
        return colorScale;
    }

    public void selectDataset(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
        this.peptideShakerVisualizationDataset = peptideShakerVisualizationDataset;
    }

    public Map<String, ProteinGroupObject> getProteinNodes() {
        return proteinNodes;
    }

    public Map<String, PeptideObject> getPeptidesNodes() {
        return peptidesNodes;
    }

    public void selectPeptide(Object proteinId, Object peptideId) {
        if (peptideId == null) {

            graphComponent.selectParentItem(proteinId);
        } else {
            graphComponent.selectChildItem(proteinId, peptideId);
        }
    }

    public boolean isQuantDataSet() {
        return peptideShakerVisualizationDataset.isQuantDataset();
    }

    public String getProteinName(String selectedProteinId) {
        return peptideShakerVisualizationDataset.getProtein(selectedProteinId).getDescription();
    }

    public String updateGraphData(String selectedProteinId) {
        proteinNodes.clear();
        peptidesNodes.clear();
        peptides.clear();
        unrelatedProt.clear();
        unrelatedPeptides.clear();
        edges.clear();
        if (peptideShakerVisualizationDataset == null || selectedProteinId == null || selectedProteinId.trim().equalsIgnoreCase("null") || peptideShakerVisualizationDataset.getProtein(selectedProteinId) == null) {
            graphComponent.updateGraphData(null, null, null, null, null, peptideShakerVisualizationDataset.isQuantDataset(), null);
            thumbURL = null;
            return thumbURL;
        }
        ProteinGroupObject protein = peptideShakerVisualizationDataset.getProtein(selectedProteinId);
        peptides.addAll(peptideShakerVisualizationDataset.getPeptides(selectedProteinId));
        Set<String> tunrelatedProt = new LinkedHashSet<>();
        maxPsms = 0;
        peptides.stream().map((peptide) -> {
            if (peptide.isModified() && peptide.getVariableModificationsAsString().contains("0") && peptide.getSequence().startsWith("NH2")) {
                peptide.setModifiedSequence(peptide.getModifiedSequence().replaceFirst("NH2", "pyro"));
            }
            peptidesNodes.put(peptide.getModifiedSequence(), peptide);
            maxPsms = Math.max(maxPsms, peptide.getPSMsNumber());
            return peptide;
        }).forEachOrdered((peptide) -> {
            ArrayList<String> tEd = new ArrayList<>();
            peptide.getProteinsSet().stream().map((acc) -> {
                tEd.add(acc);
                return acc;
            }).filter((acc) -> (!proteinNodes.containsKey(acc))).forEachOrdered((acc) -> {
                tunrelatedProt.add(acc);
            });
            edges.put(peptide.getModifiedSequence(), tEd);
        });
        tunrelatedProt.forEach((unrelated) -> {
            fillUnrelatedProteinsAndPeptides(unrelated, peptideShakerVisualizationDataset.getProtein(unrelated));
        });
        proteinNodes.putAll(unrelatedProt);
        peptidesNodes.putAll(unrelatedPeptides);

        Map<String, ProteinGroupObject> tempProteinNodes = new LinkedHashMap<>(proteinNodes);
        tempProteinNodes.keySet().forEach((accession) -> {
            proteinNodes.replace(accession, peptideShakerVisualizationDataset.updateProteinInformation(tempProteinNodes.get(accession), accession));
        });
        colorScale = new RangeColorGenerator(maxPsms);//update pathway information
        graphComponent.updateGraphData(protein, proteinNodes, peptidesNodes, edges, colorScale, peptideShakerVisualizationDataset.isQuantDataset(), peptideShakerVisualizationDataset.getProteinIntensityColorGenerator());
        thumbURL = graphComponent.getThumbImgeUrl();
        return thumbURL;

    }

    private void updateProteoformGraphData(Set<Object> proteinsIds) {

        if (proteinsPathwayNewtorkGraph == null) {
            return;
        }
        Map<String, ProteinGroupObject> proteoformProteinNodes = new LinkedHashMap<>();
        ProteinGroupObject protein;

        for (Object protId : proteinsIds) {
            if (!proteinNodes.containsKey(protId.toString())) {
                protein = peptideShakerVisualizationDataset.getProtein(protId.toString());
            } else {
                protein = proteinNodes.get(protId.toString());
            }

            if (protein != null && protein.getValidation().contains("Confident")) {
                proteoformProteinNodes.put(protein.getAccession(), protein);
            }
        }

        Set<NetworkGraphEdge> pathwayEdges = peptideShakerVisualizationDataset.updateProteinPathwayInformation(proteoformProteinNodes);
        proteinsPathwayNewtorkGraph.updateGraphData(proteoformProteinNodes.keySet(), pathwayEdges);
        graphComponent.setEnablePathway(proteinsPathwayNewtorkGraph.isEnabled());

    }

    private void fillUnrelatedProteinsAndPeptides(String proteinAccession, ProteinGroupObject protein) {
        if (unrelatedProt.containsKey(proteinAccession)) {
            return;
        }
        unrelatedProt.put(proteinAccession, protein);
        Set<PeptideObject> tpeptides = peptideShakerVisualizationDataset.getPeptides(proteinAccession);

        if (tpeptides != null) {
            tpeptides.stream().map((pep) -> {
                if (!edges.containsKey(pep.getModifiedSequence())) {
                    ArrayList<String> tEd = new ArrayList<>();
                    edges.put(pep.getModifiedSequence(), tEd);
                }
                return pep;
            }).map((pep) -> {

                if (pep.getProteinsSet().contains(proteinAccession)) {
                    edges.get(pep.getModifiedSequence()).add(proteinAccession);
                }
                return pep;
            }).filter((pep) -> (!peptidesNodes.containsKey(pep.getModifiedSequence()))).map((pep) -> {
                unrelatedPeptides.put(pep.getModifiedSequence(), pep);
                return pep;
            }).forEachOrdered((pep) -> {
                pep.getProteinsSet().forEach((newAcc) -> {
                    ProteinGroupObject tprot = peptideShakerVisualizationDataset.getProtein(newAcc, pep.getModifiedSequence());
                    fillUnrelatedProteinsAndPeptides(newAcc, tprot);
                });
            });
        }

    }

    public Set<Object> getSelectedProteins() {
        return graphComponent.getSelectedProteins();
    }

    public Set<Object> getSelectedPeptides() {
        return graphComponent.getSelectedPeptides();
    }

    public abstract void selectedItem(Set<Object> selectedItems, Set<Object> selectedChildsItems, boolean isProteform);

    public abstract void updateProteinsMode(String modeType);

    public void updateMode() {
        this.graphComponent.updateMode();
    }

}
