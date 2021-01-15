package com.uib.web.peptideshaker.ui.views.subviews.datasetview;

import com.uib.web.peptideshaker.ui.components.items.FilterButton;
import com.uib.web.peptideshaker.ui.components.items.HelpPopupButton;
import com.uib.web.peptideshaker.ui.views.modal.PopupWindow;
import com.uib.web.peptideshaker.ui.views.subviews.datasetview.components.DatasetProteinsSubViewComponent;
import com.uib.web.peptideshaker.uimanager.ResultsViewSelectionManager_old;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.Set;

/**
 * This class represents the layout that contains PeptideShaker datasets
 * overview
 *
 * @author Yehia Mokhtar Farag
 */
public class DatasetProteinsSubViewContent extends HorizontalLayout {

//    private final AbsoluteLayout container;
//    private final PopupWindow headerLabel;
//    private final DatasetProteinsSubViewComponent datasetVisulizationLevelComponent;
//
//    /**
//     * Constructor to initialise the main layout and variables.
//     */
//    public DatasetProteinsSubViewContent() {
//        DatasetProteinsSubViewContent.this.setSizeFull();
//        DatasetProteinsSubViewContent.this.setStyleName("transitionallayout");
//        DatasetProteinsSubViewContent.this.setSpacing(false);
//        DatasetProteinsSubViewContent.this.setMargin(false);
//        
//        container = new AbsoluteLayout();
//        container.setSizeFull();
//        DatasetProteinsSubViewContent.this.addComponent(container);
//
//        HorizontalLayout topLabelContainer = new HorizontalLayout();
//        topLabelContainer.setSizeFull();
//        topLabelContainer.addStyleName("minhight30");
//        container.addComponent(topLabelContainer);
//
//        HorizontalLayout topLeftLabelContainer = new HorizontalLayout();
//        topLeftLabelContainer.setWidthUndefined();
//        topLeftLabelContainer.setHeight(100, Unit.PERCENTAGE);
//        topLabelContainer.addComponent(topLeftLabelContainer);
//        headerLabel = new PopupWindow("Dataset name") {
//            @Override
//            public void onClosePopup() {
//            }
//
//        };
//        headerLabel.addStyleName("largetitle");
//        headerLabel.setWidthUndefined();
//        topLeftLabelContainer.setSpacing(true);
//        topLeftLabelContainer.addComponent(headerLabel);
//
//        HelpPopupButton helpBtn = new HelpPopupButton("<h1>Datset Visualization</h1>Users visualise the selected datasets and interact with it.<br/>The dataset visulization has three main levels<br/>  1.Dataset level: include proteins table and dataset filters.</br>  2.Protein level: visulaisation of protein details and related proteins including the peptide coverage and 3D visulisation.</br>  3.Peptide level: visulization of peptide details include available peptide-to-spectrum matches and spectrum visulizaion chart.", "", 400, 175);
//        topLeftLabelContainer.addComponent(helpBtn);
//
//
//        FilterButton removeFilterIcon = new FilterButton() {
//            @Override
//            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
//                
//
//            }
//        };
//        topLeftLabelContainer.addComponent(removeFilterIcon);
//        Label commentLabel = new Label("<i style='padding-right: 50px;'>* Click in the charts to select and filter data</i>", ContentMode.HTML);
//        commentLabel.setWidthUndefined();
//        commentLabel.setStyleName("resizeabletext");
//        commentLabel.addStyleName("margintop10");
//        commentLabel.addStyleName("selectiondescriptionlabel");
//        topLabelContainer.addComponent(commentLabel);
//        topLabelContainer.setComponentAlignment(commentLabel, Alignment.TOP_RIGHT);
//        datasetVisulizationLevelComponent = new DatasetProteinsSubViewComponent(new ResultsViewSelectionManager_old()) {
//            @Override
//            public void updateFilterSelection(Set<Comparable> selection, Set<Comparable> selectedCategories, boolean topFilter, boolean selectOnly, boolean selfAction) {
////                removeFilterIcon.setVisible(Selection_Manager.isDatasetFilterApplied());
////                super.updateFilterSelection(selection, selectedCategories, topFilter, selectOnly, selfAction);
//            }
//        };
//        container.addComponent(datasetVisulizationLevelComponent, "left:0px;top:40px;");
//        HorizontalLayout paggingBtnsContainer = new HorizontalLayout();
//        paggingBtnsContainer.setWidth(100, Unit.PERCENTAGE);
//        paggingBtnsContainer.setHeight(20, Unit.PIXELS);
//        paggingBtnsContainer.addStyleName("paggingbtnscontainer");
//        container.addComponent(paggingBtnsContainer, "left:0px;bottom:50px");
//        HorizontalLayout btnContainer = new HorizontalLayout();
//        btnContainer.setHeight(100, Unit.PERCENTAGE);
//        btnContainer.setWidthUndefined();
//        btnContainer.setSpacing(true);
//        paggingBtnsContainer.addComponent(btnContainer);
//        paggingBtnsContainer.setComponentAlignment(btnContainer, Alignment.TOP_CENTER);
//
//        Button beforeBtn = new Button(VaadinIcons.CARET_LEFT);
//        beforeBtn.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
//        btnContainer.addComponent(beforeBtn);
//
//        final Label filterViewIndex = new Label(" (1/5) ", ContentMode.HTML);
//        btnContainer.addComponent(filterViewIndex);
//
//        beforeBtn.addClickListener((Button.ClickEvent event) -> {
//            filterViewIndex.setValue(" (" + datasetVisulizationLevelComponent.showBefore() + "/5) ");
//        });
//        Button nextBtn = new Button(VaadinIcons.CARET_RIGHT);
//        nextBtn.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
//        btnContainer.addComponent(nextBtn);
//        nextBtn.addClickListener((Button.ClickEvent event) -> {
//            filterViewIndex.setValue(" (" + datasetVisulizationLevelComponent.showNext() + "/5) ");
//        });
//
//    }
//
////    public void selectDataset(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {
////
////        headerLabel.setLabelValue("Dataset: " + peptideShakerVisualizationDataset.getProjectName());
////        if (!peptideShakerVisualizationDataset.isUploadedProject() && !peptideShakerVisualizationDataset.isToShareDataset()) {
////            SearchParametersForm dsOverview = new SearchParametersForm(false) {
////            //((PeptideShakerVisualizationDataset) peptideShakerVisualizationDataset, true) {
////                @Override
////                public void saveSearchingFile(IdentificationParameters searchParameters, boolean isNew) {
//////                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
////                }
////
////                @Override
////                public void cancel() {
////                    headerLabel.setPopupVisible(false);
////                }
////
////            };
////            headerLabel.setContent(dsOverview);
////        }
////        headerLabel.setEnabled(!peptideShakerVisualizationDataset.isUploadedProject());
////        datasetVisulizationLevelComponent.updateData(peptideShakerVisualizationDataset);
////
////    }

}
