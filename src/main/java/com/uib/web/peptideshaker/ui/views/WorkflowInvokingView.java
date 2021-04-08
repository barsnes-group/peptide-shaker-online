package com.uib.web.peptideshaker.ui.views;

import com.uib.web.peptideshaker.ui.interfaces.ViewableFrame;
import com.uib.web.peptideshaker.ui.components.items.SubViewSideButton;
import com.uib.web.peptideshaker.ui.components.WorkFlowDataInputComponent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;

/**
 * This class represent web tool presenter which is responsible for managing the
 * view and interactivity of the tool
 *
 * @author Yehia Mokhtar Farag
 */
public class WorkflowInvokingView extends AbsoluteLayout implements ViewableFrame {

    /**
     * The work-flow input form layout container.
     */
    protected WorkFlowDataInputComponent workflowDataInputLayout;
    /**
     * The work-flow side button container (left side button container).
     */
    protected VerticalLayout leftSideButtonsContainer;

    /**
     * Initialise the web tool main attributes.
     */
    public WorkflowInvokingView() {
        initLayout();
    }

    private void initLayout() {
        WorkflowInvokingView.this.setSizeFull();
        WorkflowInvokingView.this.setStyleName("activelayout");
        leftSideButtonsContainer = new VerticalLayout();
        leftSideButtonsContainer.setWidth(100, Unit.PIXELS);
        leftSideButtonsContainer.setHeightUndefined();
        leftSideButtonsContainer.setSpacing(true);
        leftSideButtonsContainer.addStyleName("singlebtn");
        this.addComponent(leftSideButtonsContainer, "top:120px;left:3px");

        SubViewSideButton workFlowBtn = new SubViewSideButton("Work-Flow", 2);
        workFlowBtn.updateIconByResource(new ThemeResource("img/searchguiblue.png"));
        workFlowBtn.addStyleName("searchguiicon");
        workFlowBtn.addStyleName("padding20");
        workFlowBtn.setData("workflow");
        leftSideButtonsContainer.addComponent(workFlowBtn);
        leftSideButtonsContainer.setComponentAlignment(workFlowBtn, Alignment.TOP_LEFT);
        workFlowBtn.setSelected(true);

        workflowDataInputLayout = new WorkFlowDataInputComponent();
        workflowDataInputLayout.initLayout();

        AbsoluteLayout toolViewFrame = new AbsoluteLayout();
        toolViewFrame.setSizeFull();
        toolViewFrame.setStyleName("integratedframe");
        this.addComponent(toolViewFrame, "left:100px");

        AbsoluteLayout toolViewFrameContent = new AbsoluteLayout();
        toolViewFrameContent.addStyleName("viewframecontent");
        toolViewFrameContent.setSizeFull();
        toolViewFrame.addComponent(toolViewFrameContent, "left:10px;right:10px;top:10px;bottom:10px;");
        toolViewFrameContent.addComponent(workflowDataInputLayout);
        WorkflowInvokingView.this.minimizeView();
    }

    /**
     * Get the presenter id.
     *
     * @return unique id for the presenter view
     */
    @Override
    public String getViewId() {
        return WorkflowInvokingView.class.getName();
    }

    /**
     * Hide this presenter visualisation.
     */
    @Override
    public void minimizeView() {
        this.addStyleName("hidepanel");
        this.leftSideButtonsContainer.removeStyleName("visible");

    }

    /**
     * Show this presenter visualisation.
     */
    @Override
    public void maximizeView() {
        this.leftSideButtonsContainer.addStyleName("visible");
        this.removeStyleName("hidepanel");
    }

    @Override
    public void update() {
        workflowDataInputLayout.updateForms();

    }
}
