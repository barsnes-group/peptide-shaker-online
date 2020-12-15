package com.uib.web.peptideshaker.ui.views;


import com.uib.web.peptideshaker.ui.abstracts.ViewableFrame;
import com.uib.web.peptideshaker.presenter.core.Help;
import com.uib.web.peptideshaker.presenter.core.PresenterSubViewSideBtn;
import com.uib.web.peptideshaker.ui.components.FilesTablePanel;
import com.vaadin.event.LayoutEvents;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class represent PeptideShaker view presenter which is responsible for
 * viewing the PeptideShaker results on web
 *
 * @author Yehia Farag
 */
public class FileSystemView extends AbsoluteLayout implements ViewableFrame, LayoutEvents.LayoutClickListener {

//    /**
//     * The small side button (normal size screen).
//     */
//    private final ButtonWithLabel mainPresenterButton;
//    /**
//     * The main left side buttons container in big screen mode.
//     */
//    /**
//     * The small side button (normal size screen).
//     */
//    private final SmallSideBtn smallPresenterButton;
    /**
     * Map of layouts to coordinate left side buttons actions.
     */
    private final Map<PresenterSubViewSideBtn, Component> btnsLayoutMap;
    private VerticalLayout leftSideButtonsContainer;
    /**
     * Main layout that contains the files and datasets table.
     */
    private FilesTablePanel filesTablePanel;
    /**
     * The main left side button (only support in a big screen).
     */
    private PresenterSubViewSideBtn viewDataBtn;
    private boolean jobInProgress;

    /**
     * Constructor to initialise the web tool main attributes.
     */
    public FileSystemView() {
        FileSystemView.this.setSizeFull();
        FileSystemView.this.setStyleName("activelayout");
        FileSystemView.this.addStyleName("hidelowerpanel");
        this.btnsLayoutMap = new LinkedHashMap<>();
        this.initLayout();
        FileSystemView.this.minimizeView();
    }

    /**
     * Initialise the container layout.
     */
    private void initLayout() {
        this.addStyleName("integratedframe");
        leftSideButtonsContainer = new VerticalLayout();
        leftSideButtonsContainer.setWidth(100, Unit.PERCENTAGE);
        leftSideButtonsContainer.setHeightUndefined();
        leftSideButtonsContainer.setSpacing(true);
        leftSideButtonsContainer.setMargin(new MarginInfo(false, false, true, false));
        leftSideButtonsContainer.addStyleName("singlebtn");
        viewDataBtn = new PresenterSubViewSideBtn("Data Overview", 1);
        viewDataBtn.setDescription("Available datasets and files");
        viewDataBtn.updateIconByResource(new ThemeResource("img/globeearthanimation.png"));
        viewDataBtn.addStyleName("glubimg");

        viewDataBtn.setData("datasetoverview");
        leftSideButtonsContainer.addComponent(viewDataBtn);
        leftSideButtonsContainer.setComponentAlignment(viewDataBtn, Alignment.TOP_CENTER);
        viewDataBtn.addLayoutClickListener(FileSystemView.this);

        VerticalLayout dataContainerLayout = initDataViewTableLayout();
        btnsLayoutMap.put(viewDataBtn, dataContainerLayout);

        VerticalLayout dataViewFrame = new VerticalLayout();
        dataViewFrame.setSizeFull();
        dataViewFrame.setStyleName("viewframe");

        this.addComponent(dataViewFrame);
//        this.setExpandRatio(dataViewFrame, 100);
        AbsoluteLayout dataViewFrameContent = new AbsoluteLayout();
        dataViewFrameContent.addStyleName("viewframecontent");
        dataViewFrameContent.setSizeFull();

        dataViewFrame.addComponent(dataViewFrameContent);

        Label titleLabel = new Label("Projects Overview");
        titleLabel.setStyleName("frametitle");
        titleLabel.addStyleName("maintitleheader");
        dataViewFrameContent.addComponent(titleLabel, "left:40px;top:13px");
        Help helpBtn = new Help("<h1>Projects Overview</h1>Users can check the available ready to visualise datasets, get an overview for the processed data, check the dataset processing statues and have access for the dataset sharing links where users can visulize the dataset using dataset link.<br/>Also users can delete datasets and input files.", "", 400, 150);
        dataViewFrameContent.addComponent(helpBtn, "left:178;top:0px");
        dataViewFrameContent.addComponent(dataContainerLayout);
        viewDataBtn.setSelected(true);

    }

    /**
     * Initialise the data view table layout.
     *
     * @return layout
     */
    private VerticalLayout initDataViewTableLayout() {
        VerticalLayout container = new VerticalLayout();
        container.setWidth(100, Unit.PERCENTAGE);
        container.setHeight(100, Unit.PERCENTAGE);
        container.setSpacing(true);
        container.setStyleName("subframe");
        container.addStyleName("padding25");
        filesTablePanel = new FilesTablePanel() ;

        container.addComponent(filesTablePanel);
        container.setComponentAlignment(filesTablePanel, Alignment.MIDDLE_CENTER);

        return container;
    }

    /**
     * Update Online PeptideShaker files from Galaxy Server
     *
     * @param historyFilesMap List of available files on Galaxy Server
     * @param jobInProgress Jobs are running
     */
//    public void updateData(Map<String, GalaxyFileObject> historyFilesMap, boolean jobInProgress,String viewDsId) {
//        this.historyFilesMap = historyFilesMap;
//        this.jobInProgress = jobInProgress;
////        if (smallPresenterButton.getStyleName().contains("selectedpresenterbtn")) {
//        UI.getCurrent().access(() -> {
//            updatelayout(viewDsId);
//            try {
//                UI.getCurrent().push();
//            } catch (Exception e) {
//                Page.getCurrent().reload();
//            }
//        });
//
//    }
//
//    private void updatelayout(String viewDsId) {
//        if (jobInProgress) {
////            smallPresenterButton.updateIconSourceURL("img/globeearthanimation1.gif");
////            mainPresenterButton.updateIconResource(new ThemeResource("img/globeearthanimation1.gif"));
//            viewDataBtn.updateIconByResource(new ThemeResource("img/globeearthanimation1.gif"));
////            smallPresenterButton.addStyleName("nopadding");
////            mainPresenterButton.addStyleName("nopadding");
//            viewDataBtn.addStyleName("nopadding");
//        } else {
////            smallPresenterButton.updateIconSourceURL("img/globeearthanimation.png");
////            mainPresenterButton.updateIconResource(new ThemeResource("img/globeearthanimation.png"));
//            viewDataBtn.updateIconByResource(new ThemeResource("img/globeearthanimation.png"));
////            smallPresenterButton.removeStyleName("nopadding");
////            mainPresenterButton.removeStyleName("nopadding");
//            viewDataBtn.removeStyleName("nopadding");
//        }
//        if (historyFilesMap != null) {
////            this.dataLayout.updateDatasetsTable(historyFilesMap,viewDsId);
//
//        }
//
//    }
   
    /**
     * Check if history is busy
     *
     * @return there is job on galaxy in progress
     */
    public boolean isJobInProgress() {
        return jobInProgress;
    }

    /**
     * Get the main frame layout
     *
     * @return File system presenter layout
     */
//    @Override
//    public VerticalLayout getMainView() {
//        return this;
//    }
    /**
     * Get the small right side button component (represent view control button
     * in large screen mode)
     *
     * @return right view control button
     */
//    @Override
//    public SmallSideBtn getSmallPresenterControlButton() {
//        return smallPresenterButton;
//    }
    /**
     * Get main presenter button
     *
     * @return large presenter button for welcome page
     */
//    @Override
//    public ButtonWithLabel getMainPresenterButton() {
//        return mainPresenterButton;
//    }
    /**
     * Get the current view ID
     *
     * @return view id
     */
    @Override
    public String getViewId() {
        return FileSystemView.class.getName();
    }

    /**
     * Hide the main view for the current component.
     */
    @Override
    public void minimizeView() {
        this.addStyleName("hidepanel");
        this.leftSideButtonsContainer.removeStyleName("visible");

    }

    /**
     * Show the main view for the current component.
     */
    @Override
    public void maximizeView() {
//        mainPresenterButton.setSelected(true);
        filesTablePanel.setEnabled(true);
//        smallPresenterButton.setSelected(true);
        this.leftSideButtonsContainer.addStyleName("visible");
        this.removeStyleName("hidepanel");

    }

    /**
     * Layout click method that is used to coordinate view inside the layout (in
     * case of multiple view under the same presenter).
     *
     * @param event left side button clicked action
     */
    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        PresenterSubViewSideBtn comp = (PresenterSubViewSideBtn) event.getComponent();
        btnsLayoutMap.keySet().forEach((bbt) -> {
            if (comp.getData().toString().equalsIgnoreCase(bbt.getData().toString())) {
                bbt.setSelected(true);
                btnsLayoutMap.get(bbt).removeStyleName("hidepanel");
            } else {
                bbt.setSelected(false);
                btnsLayoutMap.get(bbt).addStyleName("hidepanel");
            }
        });
        if (comp.getData().toString().equalsIgnoreCase("datasetoverview")) {

        }
    }


    
   
    @Override
    public void update() {
            filesTablePanel.updateDatasetsTable();
            filesTablePanel.updateViewDataset();
    }
}
