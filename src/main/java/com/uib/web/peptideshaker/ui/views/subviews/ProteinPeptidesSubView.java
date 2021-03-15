package com.uib.web.peptideshaker.ui.views.subviews;

import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.model.PeptideObject;
import com.uib.web.peptideshaker.model.ProteinGroupObject;
import com.uib.web.peptideshaker.model.VisualizationDatasetModel;
import com.uib.web.peptideshaker.ui.views.subviews.proteinsview.ProteinsGraphsContainerView;
import com.uib.web.peptideshaker.ui.views.subviews.proteinsview.Protein3DStructureView;
import com.uib.web.peptideshaker.ui.views.subviews.proteinsview.ProteinCoverageView;
import com.uib.web.peptideshaker.ui.abstracts.ViewableFrame;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the layout that contains selected protein details
 * (peptides/structure/coverage/3d)
 *
 * @author Yehia Mokhtar Farag
 */
public class ProteinPeptidesSubView extends AbsoluteLayout implements ViewableFrame {

    private final AppManagmentBean appManagmentBean;
    private final AbsoluteLayout container;
    private final ProteinsGraphsContainerView graphsContainerComponent;
    private final ProteinCoverageView ProteinCoverageView;
    private final Protein3DStructureView protein3DStructureView;
    private final Label headerLabel;

    private final Map<Integer, Component> filterComponentsMap;

    public ProteinPeptidesSubView() {
        this.appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
        ProteinPeptidesSubView.this.setSizeFull();
        ProteinPeptidesSubView.this.setStyleName("transitionallayout");

        container = new AbsoluteLayout();
        container.setSizeFull();
        ProteinPeptidesSubView.this.addComponent(container);

        HorizontalLayout topLabelContainer = new HorizontalLayout();
        topLabelContainer.setHeight(30, Unit.PIXELS);
        topLabelContainer.setWidth(100, Unit.PERCENTAGE);
        container.addComponent(topLabelContainer);
        HorizontalLayout topLeftLabelContainer = new HorizontalLayout();
        topLeftLabelContainer.setWidthUndefined();
        topLeftLabelContainer.setHeight(100, Unit.PERCENTAGE);
        topLabelContainer.addComponent(topLeftLabelContainer);
        headerLabel = new Label();
        headerLabel.setValue("Protein Overview");
        headerLabel.addStyleName("largetitle");
        headerLabel.setWidthUndefined();
        topLeftLabelContainer.setSpacing(true);
        topLeftLabelContainer.addComponent(headerLabel);

        Label commentLabel = new Label("<i style='padding-right: 50px;top: 3px !important;position: relative;'>* Click in the graph or table to select proteins and peptides</i>", ContentMode.HTML);
        commentLabel.setWidthUndefined();
        commentLabel.setStyleName("resizeabletext");
        commentLabel.addStyleName("margintop10");
        commentLabel.addStyleName("selectiondescriptionlabel");
        topLabelContainer.addComponent(commentLabel);
        topLabelContainer.setComponentAlignment(commentLabel, Alignment.TOP_RIGHT);

        VerticalLayout subContainer = new VerticalLayout();
        subContainer.setSizeFull();
        subContainer.setSpacing(true);
        container.addComponent(subContainer, "left:0px; top:30px; bottom:15px;");

        HorizontalLayout middleContainer = new HorizontalLayout();
        middleContainer.addStyleName("extendwidthstyle");
        middleContainer.setHeight(100, Unit.PERCENTAGE);
        middleContainer.setWidth(100, Unit.PERCENTAGE);

        middleContainer.setSpacing(true);
        subContainer.addComponent(middleContainer);
        graphsContainerComponent = new ProteinsGraphsContainerView() {
            @Override
            public void selectedItem(Map<String, ProteinGroupObject> selectedItems, Map<String, PeptideObject> selectedChildsItems, boolean isProteform) {

                ProteinPeptidesSubView.this.selectPeptide(selectedItems, selectedChildsItems, isProteform);

            }

            @Override
            public void updateProteinsMode(String modeType) {
                ProteinCoverageView.updateProteinsMode(modeType);
                int mode = 1;
                switch (modeType) {
                    case "Validation":
                        mode = 2;
                        break;
                    case "Modifications":
                        mode = 3;
                        break;
                    case "Proteoform":
                        mode = 3;
                        break;
                    case "Intensity":
                        mode = 5;
                        break;
                    case "PSMNumber":
                        mode = 4;
                        break;
                }
                protein3DStructureView.setMode(mode);
            }

        };
        middleContainer.addComponent(graphsContainerComponent);
        middleContainer.setExpandRatio(graphsContainerComponent, 60);
        graphsContainerComponent.addStyleName("graphcontainerstyle");

        protein3DStructureView = new Protein3DStructureView();
        middleContainer.addComponent(protein3DStructureView);
        middleContainer.setExpandRatio(protein3DStructureView, 40);
        protein3DStructureView.addStyleName("protein3dcontainerstyle");
        ProteinCoverageView = new ProteinCoverageView(protein3DStructureView.getChainCoverageLayout()) {
            @Override
            public void selectPeptide(Map<String, ProteinGroupObject> selectedProteins, Map<String, PeptideObject> selectedPeptides, boolean isProteform) {
                ProteinPeptidesSubView.this.selectPeptide(selectedProteins, selectedPeptides, isProteform);
            }

        };
        ProteinCoverageView.setSizeFull();
        subContainer.addComponent(ProteinCoverageView);
        ProteinCoverageView.addStyleName("proteincoveragecontainerstyle");

        this.filterComponentsMap = new HashMap<>();
        this.filterComponentsMap.put(1, this.graphsContainerComponent);
        this.filterComponentsMap.put(2, this.protein3DStructureView);
        this.filterComponentsMap.put(3, this.ProteinCoverageView);

        HorizontalLayout paggingBtnsContainer = new HorizontalLayout();
        paggingBtnsContainer.setWidth(100, Unit.PERCENTAGE);
        paggingBtnsContainer.setHeight(20, Unit.PIXELS);
        paggingBtnsContainer.addStyleName("paggingbtnscontainer");
        container.addComponent(paggingBtnsContainer, "left:0px;bottom:50px");
        HorizontalLayout btnContainer = new HorizontalLayout();
        btnContainer.setHeight(100, Unit.PERCENTAGE);
        btnContainer.setWidthUndefined();
        btnContainer.setSpacing(true);
        paggingBtnsContainer.addComponent(btnContainer);
        paggingBtnsContainer.setComponentAlignment(btnContainer, Alignment.TOP_CENTER);

        Button beforeBtn = new Button(VaadinIcons.CARET_LEFT);
        beforeBtn.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        btnContainer.addComponent(beforeBtn);

        final Label filterViewIndex = new Label(" (1/3) ", ContentMode.HTML);
        btnContainer.addComponent(filterViewIndex);

        beforeBtn.addClickListener((Button.ClickEvent event) -> {
//            filterViewIndex.setValue(" (" + this.showBefore() + "/3) ");
        });
        Button nextBtn = new Button(VaadinIcons.CARET_RIGHT);
        nextBtn.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        btnContainer.addComponent(nextBtn);
        nextBtn.addClickListener((Button.ClickEvent event) -> {
//            filterViewIndex.setValue(" (" + this.showNext() + "/3) ");
        });
//        filterViewIndex.setValue(" (" + ProteinVisulizationLevelContainer.this.showNext() + "/3) ");
    }

    @Override
    public String getViewId() {
        return ProteinPeptidesSubView.class.getName();
    }

    /**
     * Hide current presenter
     */
    @Override
    public void minimizeView() {
        this.addStyleName("hidepanel");
    }

    /**
     * View presenter
     */
    @Override
    public void maximizeView() {
        this.removeStyleName("hidepanel");
    }
    private int lastSelectedProteinIndex;

    @Override
    public void update() {
        if (appManagmentBean.getUI_Manager().getSelectedProteinIndex() == -1) {
            this.minimizeView();
            appManagmentBean.getUI_Manager().setEncodedProteinButtonImage("null");
            return;
        }
        protein3DStructureView.setMode(4);
        if (appManagmentBean.getUI_Manager().getSelectedProteinIndex() != lastSelectedProteinIndex) {
            lastSelectedProteinIndex = appManagmentBean.getUI_Manager().getSelectedProteinIndex();
            String selectedDatasetId = appManagmentBean.getUI_Manager().getSelectedDatasetId();
            VisualizationDatasetModel dataset = appManagmentBean.getUserHandler().getDataset(selectedDatasetId);
            ProteinGroupObject selectedProteinObject = dataset.getProteinsMap().get(lastSelectedProteinIndex);
            if (!dataset.getProteinsGraphDataMap().containsKey(selectedProteinObject.getAccession())) {
                Object[] graphData = appManagmentBean.getDatasetUtils().initialiseProteinGraphData(selectedDatasetId, lastSelectedProteinIndex);
                Map<String, ProteinGroupObject> proteinNodes = (Map<String, ProteinGroupObject>) graphData[1];
                proteinNodes.keySet().forEach((accession) -> {
                    dataset.addProteinGraphData(accession, graphData);
                });
            }
            Object[] graphData = dataset.getProteinsGraphDataMap().get(selectedProteinObject.getAccession());
            ProteinCoverageView.updateData(graphData, dataset.getDatasetType().equals(CONSTANT.QUANT_DATASET));
            appManagmentBean.getUI_Manager().setEncodedProteinButtonImage(graphsContainerComponent.updateGraphData(graphData, dataset.getDatasetType().equals(CONSTANT.QUANT_DATASET)));
        }

    }

    private void selectPeptide(Map<String, ProteinGroupObject> selectedItems, Map<String, PeptideObject> selectedChildsItems, boolean isProteform) {
        ProteinGroupObject protein;
        if (selectedItems.size() == 1) {
            protein = selectedItems.values().iterator().next();
            headerLabel.setValue("Protein Overview (" + protein.getDescription() + ")");
        } else {
            protein = null;
            headerLabel.setValue("Protein Overview ");
        }
        ProteinCoverageView.setSelectedItems(selectedItems, selectedChildsItems);
        if (isProteform) {
            return;
        }
        if (selectedItems.size() == 1 && protein != null) {
            if (selectedChildsItems != null) {
                protein3DStructureView.updatePanel(protein.getAccession(), protein.getSequence(), selectedChildsItems.values());
            } else {
//                protein3DStructureView.updatePanel(protein.getAccession(), protein.getSequence(), null);
            }
        } else {
            protein3DStructureView.reset();
        }
        String peptideId = null;
        if (selectedChildsItems != null && selectedChildsItems.size() == 1) {
            peptideId = selectedChildsItems.keySet().iterator().next();
            protein3DStructureView.selectPeptide(peptideId);
        }
        if (selectedItems.size() == 1) {
            Object proteinId = selectedItems.keySet().toArray()[0];
            graphsContainerComponent.selectPeptide(proteinId, peptideId);
        }
        if (peptideId != null) {
            System.out.println("at peptide id " + peptideId + selectedChildsItems.get(peptideId).getIndex());
            appManagmentBean.getUI_Manager().setSelectedPeptideIndex(selectedChildsItems.get(peptideId).getIndex());
        } else {
            appManagmentBean.getUI_Manager().setSelectedPeptideIndex(-1);
        }

    }

}
