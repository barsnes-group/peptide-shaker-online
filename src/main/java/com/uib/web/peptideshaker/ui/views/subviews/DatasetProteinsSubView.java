package com.uib.web.peptideshaker.ui.views.subviews;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.model.VisualizationDatasetModel;
import com.uib.web.peptideshaker.ui.interfaces.ViewableFrame;
import com.uib.web.peptideshaker.ui.components.SearchParametersForm;
import com.uib.web.peptideshaker.ui.components.items.FilterButton;
import com.uib.web.peptideshaker.ui.components.items.HelpPopupButton;
import com.uib.web.peptideshaker.ui.views.ResultsView;
import com.uib.web.peptideshaker.ui.views.modal.PopupWindow;
import com.uib.web.peptideshaker.ui.views.subviews.datasetview.components.DatasetProteinsSubViewComponent;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

/**
 *
 * @author Yehia Mokhtar Farag
 */
public class DatasetProteinsSubView extends AbsoluteLayout implements ViewableFrame {

    private boolean inactive = true;
    private final AppManagmentBean appManagmentBean;
    private final AbsoluteLayout container;
    private final PopupWindow headerLabel;
    private final DatasetProteinsSubViewComponent datasetProteinsSubViewComponent;

    public DatasetProteinsSubView() {
        this.appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
        DatasetProteinsSubView.this.setSizeFull();

        container = new AbsoluteLayout();
        container.setSizeFull();
        DatasetProteinsSubView.this.addComponent(container);

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
        HelpPopupButton helpBtn = new HelpPopupButton("<h1>Datset Visualization</h1>Users visualise the selected datasets and interact with it.<br/>Dataset level: include proteins table and dataset filters..", "", 350, 90);
        topLeftLabelContainer.addComponent(helpBtn);
        FilterButton removeFilterIcon = new FilterButton() {
            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent event) {

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

        datasetProteinsSubViewComponent = new DatasetProteinsSubViewComponent();
        container.addComponent(datasetProteinsSubViewComponent, "left:0px;top:40px;");
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
            filterViewIndex.setValue(" (" + datasetProteinsSubViewComponent.showBefore() + "/5) ");
        });
        Button nextBtn = new Button(VaadinIcons.CARET_RIGHT);
        nextBtn.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        btnContainer.addComponent(nextBtn);
        nextBtn.addClickListener((Button.ClickEvent event) -> {
            filterViewIndex.setValue(" (" + datasetProteinsSubViewComponent.showNext() + "/5) ");
        });

    }

    @Override
    public String getViewId() {
        return DatasetProteinsSubView.class.getName();
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
        if (inactive && !appManagmentBean.isSharingDatasetMode()) {
            appManagmentBean.getNotificationFacade().showInfoNotification("You need to select Project or upload your own files to visualize data");
            appManagmentBean.getUI_Manager().viewSubLayout(ResultsView.class.getName(), UserUploadDataSubView.class.getName());
        } else {
            this.removeStyleName("hidepanel");
        }
    }
    private String lastSelectedDatasetId;

    @Override
    public void update() {
        VisualizationDatasetModel dataset = appManagmentBean.getUserHandler().getDataset(appManagmentBean.getUI_Manager().getSelectedDatasetId());
        if (dataset == null) {
            this.minimizeView();
            return;
        }
        if (!dataset.getId().equals(lastSelectedDatasetId)) {
            lastSelectedDatasetId = dataset.getId();
            inactive = false;
            headerLabel.setLabelValue("Dataset: " + dataset.getName());
           if (dataset.getDatasetSource().equals(CONSTANT.GALAXY_SOURCE)) {
                SearchParametersForm dsOverview = new SearchParametersForm(false) {
                    @Override
                    public void saveSearchingFile(IdentificationParameters searchParameters, boolean isNew) {
                    }

                    @Override
                    public void cancel() {
                        headerLabel.setPopupVisible(false);
                    }

                };
                dsOverview.updateForms(dataset.getIdentificationParametersObject());
                headerLabel.setContent(dsOverview);
            }
            headerLabel.setEnabled(!dataset.getDatasetSource().equals(CONSTANT.USER_UPLOAD_SOURCE));
            datasetProteinsSubViewComponent.updateData(dataset);
        }

    }

}
