package com.uib.web.peptideshaker.ui.views.subviews.proteinsview;

import com.uib.web.peptideshaker.model.PeptideObject;
import com.uib.web.peptideshaker.model.ProteinGroupObject;
import com.uib.web.peptideshaker.ui.views.subviews.proteinsview.components.GraphComponent;
import com.uib.web.peptideshaker.ui.components.RangeColorGenerator;
import com.vaadin.ui.VerticalLayout;
import graphmatcher.NetworkGraphComponent;

import java.util.*;

/**
 * this class represents interactive graph layout
 *
 * @author Yehia Farag
 */
public abstract class ProteinsGraphsContainerView extends VerticalLayout {

    private final GraphComponent graphComponent;
    private RangeColorGenerator psmsColorScale;
    private final NetworkGraphComponent proteinsPathwayNewtorkGraph;
    private String thumbURL;

    public ProteinsGraphsContainerView() {
        ProteinsGraphsContainerView.this.setSizeFull();
        graphComponent = new GraphComponent() {
            @Override
            public void selectedItem(Map<String,ProteinGroupObject> selectedItems, Map<String,PeptideObject> selectedChildsItems, boolean updateProteform) {
                ProteinsGraphsContainerView.this.selectedItem(selectedItems, selectedChildsItems, false);//
                if (updateProteform) {
//                    ProteinsGraphsContainerView.this.updateProteoformGraphData(selectedItems);
                }
            }

            @Override
            public void updateProteinsMode(String modeType) {
                ProteinsGraphsContainerView.this.updateProteinsMode(modeType);
            }

        };
        ProteinsGraphsContainerView.this.addComponent(graphComponent);
        proteinsPathwayNewtorkGraph = new NetworkGraphComponent() {
            @Override
            public void selectedItem(Set<Object> selectedParentItems, Set<Object> selectedChildItems) {//
                if (proteinsPathwayNewtorkGraph.isVisible()) {
//                    ProteinsGraphsContainerView.this.selectedItem(selectedParentItems, selectedChildItems, true);
                    graphComponent.selectParentItem(selectedParentItems);
                }
            }

        };
        proteinsPathwayNewtorkGraph.setSizeFull();
        graphComponent.addProteoformGraphComponent(proteinsPathwayNewtorkGraph);

    }

    public RangeColorGenerator getPsmsColorScale() {
        return psmsColorScale;
    }
  

    public void selectPeptide(Object proteinId, Object peptideId) {
        if (peptideId == null) {

            graphComponent.selectParentItem(proteinId);
        } else {
            graphComponent.selectChildItem(proteinId, peptideId);
        }
    }

    public String updateGraphData( Object[] graphData,boolean quantDataset) {
        graphComponent.updateGraphData((ProteinGroupObject) graphData[0], (Map<String, ProteinGroupObject>) graphData[1], (Map<String, PeptideObject>) graphData[2], (HashMap<String, Set<String>>) graphData[3], (RangeColorGenerator) graphData[4], quantDataset, (RangeColorGenerator) graphData[5]);
        thumbURL = graphComponent.getThumbImgeUrl();
        return thumbURL;

    }

    private void updateProteoformGraphData(Set<Object> proteinsIds) {
//
//        if (proteinsPathwayNewtorkGraph == null) {
//            return;
//        }
//        Map<String, ProteinGroupObject> proteoformProteinNodes = new LinkedHashMap<>();
//        ProteinGroupObject protein;
//
//        for (Object protId : proteinsIds) {
//            if (!proteinNodes.containsKey(protId.toString())) {
//                protein = peptideShakerVisualizationDataset.getProtein(protId.toString());
//            } else {
//                protein = proteinNodes.get(protId.toString());
//            }
//
//            if (protein != null && protein.getValidation().contains("Confident")) {
//                proteoformProteinNodes.put(protein.getAccession(), protein);
//            }
//        }
//
//        Set<NetworkGraphEdge> pathwayEdges = peptideShakerVisualizationDataset.updateProteinPathwayInformation(proteoformProteinNodes);
//        proteinsPathwayNewtorkGraph.updateGraphData(proteoformProteinNodes.keySet(), pathwayEdges);
//        graphComponent.setEnablePathway(proteinsPathwayNewtorkGraph.isEnabled());

    }

    public Set<Object> getSelectedProteins() {
        return graphComponent.getSelectedProteins();
    }

    public Set<Object> getSelectedPeptides() {
        return graphComponent.getSelectedPeptides();
    }

    public abstract void selectedItem(Map<String,ProteinGroupObject> selectedItems, Map<String,PeptideObject> selectedChildsItems, boolean isProteform);
    public abstract void updateProteinsMode(String modeType);

    public void updateMode() {
        this.graphComponent.updateMode();
    }

}
