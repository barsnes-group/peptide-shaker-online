package com.uib.web.peptideshaker.ui.views;

import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.ui.interfaces.ViewableFrame;
import com.uib.web.peptideshaker.ui.components.items.HelpPopupButton;
import com.uib.web.peptideshaker.ui.components.items.SubViewSideButton;
import com.uib.web.peptideshaker.ui.components.FilesTablePanel;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;

/**
 * This class represent PeptideShaker view presenter which is responsible for
 * viewing the PeptideShaker results on web
 *
 * @author Yehia Mokhtar Farag
 */
public class FileSystemView extends AbsoluteLayout implements ViewableFrame {

    private VerticalLayout leftSideButtonsContainer;
    /**
     * Main layout that contains the files and datasets table.
     */
    private FilesTablePanel filesTablePanel;
    /**
     * The main left side button (only support in a big screen).
     */
    private SubViewSideButton viewDataBtn;
    private boolean jobInProgress;
    private final AppManagmentBean appManagmentBean;

    /**
     * Constructor to initialise the web tool main attributes.
     */
    public FileSystemView() {
        appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
        FileSystemView.this.setSizeFull();
        FileSystemView.this.setStyleName("activelayout");
        FileSystemView.this.addStyleName("hidelowerpanel");
        this.initLayout();
        FileSystemView.this.minimizeView();
    }

    /**
     * Initialise the container layout.
     */
    private void initLayout() {
        leftSideButtonsContainer = new VerticalLayout();
        leftSideButtonsContainer.setWidth(100, Unit.PIXELS);
        leftSideButtonsContainer.setHeightUndefined();
        leftSideButtonsContainer.setSpacing(true);
        leftSideButtonsContainer.addStyleName("singlebtn");
        this.addComponent(leftSideButtonsContainer, "top:120px;left:3px");

        viewDataBtn = new SubViewSideButton("Data Overview", 1);
        viewDataBtn.setDescription("Available datasets and files");
        viewDataBtn.updateIconByResource(new ThemeResource("img/globeearthanimation.png"));
        viewDataBtn.addStyleName("glubimg");

        viewDataBtn.setData("datasetoverview");
        leftSideButtonsContainer.addComponent(viewDataBtn);
        leftSideButtonsContainer.setComponentAlignment(viewDataBtn, Alignment.TOP_CENTER);

        
        AbsoluteLayout dataViewFrame = new AbsoluteLayout();
        dataViewFrame.setSizeFull();
        dataViewFrame.setStyleName("integratedframe");
        this.addComponent(dataViewFrame, "left:100px");
        
        AbsoluteLayout dataViewFrameContent = new AbsoluteLayout();
        dataViewFrameContent.addStyleName("viewframecontent");
        dataViewFrameContent.setSizeFull();

        dataViewFrame.addComponent(dataViewFrameContent, "left:10px;right:10px;top:10px;bottom:10px;");

        Label titleLabel = new Label("Projects Overview");
        titleLabel.setStyleName("frametitle");
        titleLabel.addStyleName("maintitleheader");
        dataViewFrameContent.addComponent(titleLabel, "left:40px;top:13px");
        HelpPopupButton helpBtn = new HelpPopupButton(appManagmentBean.getAppConfig().getProjects_Overview_text(), "", 400, 110);
        dataViewFrameContent.addComponent(helpBtn, "left:178;top:0px");
        
        
        VerticalLayout dataContainerLayout = initDataViewTableLayout();
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
        filesTablePanel = new FilesTablePanel();

        container.addComponent(filesTablePanel);
        container.setComponentAlignment(filesTablePanel, Alignment.MIDDLE_CENTER);

        return container;
    }

    /**
     * Check if history is busy
     *
     * @return there is job on galaxy in progress
     */
    public boolean isJobInProgress() {
        return jobInProgress;
    }

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
        filesTablePanel.setEnabled(true);
        this.leftSideButtonsContainer.addStyleName("visible");
        this.removeStyleName("hidepanel");

    }

    @Override
    public void update() {
        UI.getCurrent().access(() -> {
            UI.getCurrent().markAsDirtyRecursive();
            filesTablePanel.updateDatasetsTable();
            filesTablePanel.updateViewDataset();
            UI.getCurrent().push();
        });

    }
}
