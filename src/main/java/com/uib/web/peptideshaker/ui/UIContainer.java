package com.uib.web.peptideshaker.ui;

import com.uib.web.peptideshaker.ui.views.FileSystemView;
import com.uib.web.peptideshaker.ui.views.ResultsView;
import com.uib.web.peptideshaker.ui.views.WorkflowInvokingView;
import com.uib.web.peptideshaker.ui.components.ViewActionButtonsComponent;
import com.uib.web.peptideshaker.ui.views.WelcomePageView;
import com.vaadin.ui.*;

/**
 * This class represents container layout that contain all UI sub components
 *
 * @author Yehia Mokhtar Farag
 */
public class UIContainer extends AbsoluteLayout {

    /**
     * Initialise welcome page .
     */
    private WelcomePageView welcomePageView;
    /**
     * Container to view selected PeptideShaker projects.
     */
    private ResultsView resultsView;
    /**
     * The SearchGUI & PeptideShaker tools view component (frame to start
     * analysis).
     */
    private WorkflowInvokingView workflowInvokingView;
    /**
     * Container to view the main available data-sets and files on galaxy server
     * or in other databases.
     */
    private FileSystemView fileSystemView;

    /**
     * Initialise container layout.
     *
     */
    public UIContainer() {
        UIContainer.this.setSizeFull();
        UIContainer.this.addStyleName("mainapplicationframe");
        UIContainer.this.addStyleName("frame");

        ViewActionButtonsComponent actionButtonsComponent = new ViewActionButtonsComponent();
        UIContainer.this.addComponent(actionButtonsComponent);
        /**
         * landing page initialization.
         */
        welcomePageView = new WelcomePageView();
        UIContainer.this.addComponent(welcomePageView, "left:0px");

        workflowInvokingView = new WorkflowInvokingView();
        UIContainer.this.addComponent(workflowInvokingView, "left:0px");

        fileSystemView = new FileSystemView();
        UIContainer.this.addComponent(fileSystemView, "left:0px");
//
        resultsView = new ResultsView(false);
        UIContainer.this.addComponent(resultsView, "left:0px");

    }

    /**
     * Get dataset visualization view
     *
     * @return layout
     */
    public ResultsView getResultsView() {
        return resultsView;
    }

    /**
     *Get search input view
     * @return layout
     */
    public WorkflowInvokingView getWorkflowInvokingView() {
        return workflowInvokingView;
    }

    /**
     *Get files view (the available files and datasets)
     * @return layout
     */
    public FileSystemView getFileSystemView() {
        return fileSystemView;
    }

    /**
     *Get home page view
     * @return layout
     */
    public WelcomePageView getWelcomePageView() {
        return welcomePageView;
    }

}
