package com.uib.web.peptideshaker.ui.views.subviews.proteinsview;

import com.uib.web.peptideshaker.model.PeptideObject;
import com.uib.web.peptideshaker.model.ProteinGroupObject;
import com.uib.web.peptideshaker.ui.views.subviews.proteinsview.components.GraphComponent;
import com.uib.web.peptideshaker.ui.components.RangeColorGenerator;
import com.vaadin.ui.VerticalLayout;
import graphmatcher.NetworkGraphComponent;
import graphmatcher.NetworkGraphEdge;

import java.util.*;

/**
 * this class represents interactive graph layout
 *
 * @author Yehia Mokhtar Farag
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
            public void selectedItem(Map<String, ProteinGroupObject> selectedItems, Map<String, PeptideObject> selectedChildsItems, boolean updateProteform) {
                ProteinsGraphsContainerView.this.selectedItem(selectedItems, selectedChildsItems);//
                if (updateProteform) {
                    ProteinsGraphsContainerView.this.updateProteoformGraphData(selectedItems);
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
                    ProteinsGraphsContainerView.this.selectedProteform(selectedParentItems, selectedChildItems);//
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

    public void selectProteoform(String proteoformId) {
        proteinsPathwayNewtorkGraph.selectProteoform(proteoformId);
    }

    public String updateGraphData(Object[] graphData, boolean quantDataset) {
        graphComponent.reset();
        graphComponent.updateGraphData((ProteinGroupObject) graphData[0], (Map<String, ProteinGroupObject>) graphData[1], (Map<String, PeptideObject>) graphData[2], (HashMap<String, Set<String>>) graphData[3], (RangeColorGenerator) graphData[4], quantDataset, (RangeColorGenerator) graphData[5]);
        thumbURL = graphComponent.getThumbImgeUrl();
        return thumbURL;

    }

    private void updateProteoformGraphData(Map<String, ProteinGroupObject> selectedItems) {

        if (proteinsPathwayNewtorkGraph == null) {
            return;
        }
        proteinsPathwayNewtorkGraph.updateGraphData(selectedItems);
        if (!proteinsPathwayNewtorkGraph.isVisible()) {
            graphComponent.setEnablePathway(proteinsPathwayNewtorkGraph.isEnabled());
        }

    }

    public Set<Object> getSelectedProteins() {
        return graphComponent.getSelectedProteins();
    }

    public Set<Object> getSelectedPeptides() {
        return graphComponent.getSelectedPeptides();
    }

    public abstract void selectedProteform(Set<Object> selectedParentItems, Set<Object> selectedChildItems);

    public abstract void selectedItem(Map<String, ProteinGroupObject> selectedItems, Map<String, PeptideObject> selectedChildsItems);

    public abstract void updateProteinsMode(String modeType);

    public void updateMode() {
        this.graphComponent.updateMode();
    }

    public Map<String, PeptideObject> getPeptidesNodes() {
        return graphComponent.getPeptidesNodes();
    }

}
