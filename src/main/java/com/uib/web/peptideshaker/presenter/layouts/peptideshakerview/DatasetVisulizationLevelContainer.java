package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.DatasetVisulizationLevelComponent;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;

import com.uib.web.peptideshaker.presenter.core.PresenterSubViewSideBtn;
import com.uib.web.peptideshaker.presenter.core.FilterButton;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.uib.web.peptideshaker.presenter.layouts.SearchParametersForm;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import java.util.Set;

/**
 * This class represents the layout that contains PeptideShaker datasets
 * overview
 *
 * @author Yehia Farag
 */
public class DatasetVisulizationLevelContainer extends HorizontalLayout {

    private final AbsoluteLayout container;
    private final PopupWindow headerLabel;
    private final DatasetVisulizationLevelComponent datasetVisulizationLevelComponent;

    /**
     * Constructor to initialise the main layout and variables.
     *
     * @param Selection_Manager
     * @param datasetsOverviewBtn
     */
    public DatasetVisulizationLevelContainer(SelectionManager Selection_Manager, PresenterSubViewSideBtn datasetsOverviewBtn) {
        DatasetVisulizationLevelContainer.this.setSizeFull();
        DatasetVisulizationLevelContainer.this.setStyleName("transitionallayout");
        DatasetVisulizationLevelContainer.this.setSpacing(false);
        DatasetVisulizationLevelContainer.this.setMargin(false);
        datasetsOverviewBtn.setDescription("Selected dataset overview and the proteins list");

        datasetsOverviewBtn.updateIconByHTMLCode(VaadinIcons.CLUSTER.getHtml());
        datasetsOverviewBtn.updateIconByResource(new ThemeResource("img/venn_color.png"));//img/vizicon.png
        container = new AbsoluteLayout();
        container.setSizeFull();
        DatasetVisulizationLevelContainer.this.addComponent(container);

        HorizontalLayout topLabelContainer = new HorizontalLayout();
        topLabelContainer.setSizeFull();
        topLabelContainer.addStyleName("minhight30");
        container.addComponent(topLabelContainer);

        HorizontalLayout topLeftLabelContainer = new HorizontalLayout();
        topLeftLabelContainer.setWidthUndefined();
        topLeftLabelContainer.setHeight(100, Unit.PERCENTAGE);
        topLabelContainer.addComponent(topLeftLabelContainer);
        headerLabel = new PopupWindow("Dataset name") {
            @Override
            public void onClosePopup() {
            }

        };
        headerLabel.addStyleName("largetitle");
        headerLabel.setWidthUndefined();
        topLeftLabelContainer.setSpacing(true);
        topLeftLabelContainer.addComponent(headerLabel);

        FilterButton removeFilterIcon = new FilterButton() {
            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                Selection_Manager.resetDatasetSelection();

            }
        };
        topLeftLabelContainer.addComponent(removeFilterIcon);
        Label commentLabel = new Label("<i style='padding-right: 50px;'>* Click in the charts to select and filter data</i>", ContentMode.HTML);
        commentLabel.setWidthUndefined();
        commentLabel.setStyleName("resizeabletext");
        commentLabel.addStyleName("margintop10");
        commentLabel.addStyleName("selectiondescriptionlabel");
        topLabelContainer.addComponent(commentLabel);
        topLabelContainer.setComponentAlignment(commentLabel, Alignment.TOP_RIGHT);
        datasetVisulizationLevelComponent = new DatasetVisulizationLevelComponent(Selection_Manager) {
            @Override
            public void updateFilterSelection(Set<Comparable> selection, Set<Comparable> selectedCategories, boolean topFilter, boolean selectOnly, boolean selfAction) {
                removeFilterIcon.setVisible(Selection_Manager.isDatasetFilterApplied());
                super.updateFilterSelection(selection, selectedCategories, topFilter, selectOnly, selfAction);
            }
        };
        container.addComponent(datasetVisulizationLevelComponent, "left:0px;top:40px;");

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

        final Label filterViewIndex = new Label(" (1/5) ", ContentMode.HTML);
        btnContainer.addComponent(filterViewIndex);

        beforeBtn.addClickListener((Button.ClickEvent event) -> {
            filterViewIndex.setValue(" (" + datasetVisulizationLevelComponent.showBefore() + "/5) ");
        });
        Button nextBtn = new Button(VaadinIcons.CARET_RIGHT);
        nextBtn.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        btnContainer.addComponent(nextBtn);
        nextBtn.addClickListener((Button.ClickEvent event) -> {
            filterViewIndex.setValue(" (" + datasetVisulizationLevelComponent.showNext() + "/5) ");
        });

    }

    public void selectDataset(PeptideShakerVisualizationDataset peptideShakerVisualizationDataset) {

        headerLabel.setLabelValue("Dataset: " + peptideShakerVisualizationDataset.getProjectName().split("___")[0]);
        if (!peptideShakerVisualizationDataset.isUploadedProject()) {
            SearchParametersForm dsOverview = new SearchParametersForm((PeptideShakerVisualizationDataset) peptideShakerVisualizationDataset, true) {
                @Override
                public void saveSearchingFile(IdentificationParameters searchParameters, boolean isNew) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void cancel() {
                    headerLabel.setPopupVisible(false);
                }

            };
            headerLabel.setContent(dsOverview);
        }
        headerLabel.setEnabled(!peptideShakerVisualizationDataset.isUploadedProject());
        datasetVisulizationLevelComponent.updateData(peptideShakerVisualizationDataset);

    }

}
