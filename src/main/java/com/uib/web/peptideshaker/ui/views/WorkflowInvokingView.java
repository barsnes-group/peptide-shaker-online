package com.uib.web.peptideshaker.ui.views;

import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;

import com.uib.web.peptideshaker.ui.abstracts.ViewableFrame;
import com.uib.web.peptideshaker.ui.components.items.SubViewSideBtn;
import com.uib.web.peptideshaker.ui.components.WorkFlowDataInputLayout;
import com.vaadin.event.LayoutEvents;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class represent web tool presenter which is responsible for managing the
 * view and interactivity of the tool
 *
 * @author Yehia Farag
 */
public class WorkflowInvokingView extends AbsoluteLayout implements ViewableFrame, LayoutEvents.LayoutClickListener {

    /**
     * The work-flow input form layout container.
     */
    protected WorkFlowDataInputLayout workflowDataInputLayout;
    /**
     * The work-flow side button container (left side button container).
     */
    protected VerticalLayout leftSideButtonsContainer;

    /**
     * Initialise the web tool main attributes
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
       

        SubViewSideBtn workFlowBtn = new SubViewSideBtn("Work-Flow", 2);
        workFlowBtn.updateIconByResource(new ThemeResource("img/searchguiblue.png"));
        workFlowBtn.addStyleName("searchguiicon");
        workFlowBtn.addStyleName("padding20");
        workFlowBtn.setData("workflow");
        leftSideButtonsContainer.addComponent(workFlowBtn);
        leftSideButtonsContainer.setComponentAlignment(workFlowBtn, Alignment.TOP_LEFT);
        workFlowBtn.setSelected(true);

         workflowDataInputLayout = new WorkFlowDataInputLayout();
        workflowDataInputLayout.initLayout();
        
        AbsoluteLayout toolViewFrame = new AbsoluteLayout();
        toolViewFrame.setSizeFull();
        toolViewFrame.setStyleName("integratedframe");
         this.addComponent(toolViewFrame,"left:100px");

        AbsoluteLayout toolViewFrameContent = new AbsoluteLayout();
        toolViewFrameContent.addStyleName("viewframecontent");
        toolViewFrameContent.setSizeFull();
        toolViewFrame.addComponent(toolViewFrameContent,"left:10px;right:10px;top:10px;bottom:10px;");
        
        toolViewFrameContent.addComponent(workflowDataInputLayout);

        WorkflowInvokingView.this.minimizeView();
    }

//    /**
//     * Update Online PeptideShaker files from Galaxy Server
//     *
//     * @param historyFilesMap List of available files on Galaxy Server
//     */
//    public void updateData(Map<String, GalaxyFileObject> historyFilesMap) {
//        if (historyFilesMap != null) {
//            Map<String, GalaxyTransferableFile> searchSettingFilesMap = new LinkedHashMap<>();
//            Map<String, GalaxyFileObject> fastaFilesMap = new LinkedHashMap<>();
//            Map<String, GalaxyFileObject> mgfFilesMap = new LinkedHashMap<>();
//            Map<String, GalaxyFileObject> rawFilesMap = new LinkedHashMap<>();
//            Map<String, GalaxyFileObject> mzMLFilesMap = new LinkedHashMap<>();
//            Set<String> datasetNames = new HashSet<>();
//            for (String fileKey : historyFilesMap.keySet()) {
//                GalaxyFileObject fileObject = historyFilesMap.get(fileKey);
//                String type = fileObject.getType();
//                switch (type) {
//                    case "MGF":
//                        mgfFilesMap.put(fileKey, fileObject);
//                        break;
//                    case "FASTA":
//                        fastaFilesMap.put(fileKey, fileObject);
//                        break;
//
//                    case "Parameters File (JSON)":
//                        searchSettingFilesMap.put(fileKey, (GalaxyTransferableFile) fileObject);
//                        break;
//
//                    case "Thermo.raw":
//                        rawFilesMap.put(fileKey, fileObject);
//                        break;
//                    case "mzML":
//                        mzMLFilesMap.put(fileKey, fileObject);
//                        break;
//                    case "User uploaded Project":
//                    case "Web Peptide Shaker Dataset":
//                        datasetNames.add(fileObject.getName().trim().toLowerCase());
//                        break;
//                }
//
//            }
////            if (smallPresenterButton.getStyleName().contains("selectedpresenterbtn")) {
////                UI.getCurrent().accessSynchronously(() -> {
////                    workflowDataInputLayout.updateForms(searchSettingFilesMap, fastaFilesMap, mgfFilesMap, rawFilesMap, mzMLFilesMap, datasetNames);
////                    UI.getCurrent().push();
////                });
////
////            } else {
//                
////            }
//
//        }
//    }


    /**
     * Get the presenter id
     *
     * @return unique id for the presenter view
     */
    @Override
    public String getViewId() {
        return WorkflowInvokingView.class.getName();
    }

    /**
     * Hide this presenter visualisation
     */
    @Override
    public void minimizeView() {
//        smallPresenterButton.setSelected(false);
//        mainPresenterBtn.setSelected(false);
        this.addStyleName("hidepanel");
        this.leftSideButtonsContainer.removeStyleName("visible");

    }

    /**
     * Show this presenter visualisation
     */
    @Override
    public void maximizeView() {
        this.leftSideButtonsContainer.addStyleName("visible");
        this.removeStyleName("hidepanel");
    }

    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {

    }

    /**
     * Get sub view buttons container
     *
     * @return layout to contain sub view buttons
     */
//    @Override
//    public VerticalLayout getSubViewButtonsActionContainerLayout() {
//        return leftSideButtonsContainer;
//    }
//    /**
//     * Run Online Peptide-Shaker work-flow
//     *
//     * @param projectName           The project name
//     * @param fastaFileId           FASTA file dataset id
//     * @param searchParameterFileId .par file id
//     * @param mgfIdsList            list of MGF file dataset ids
//     * @param searchEnginesList     List of selected search engine names
//     * @param searchParam           search parameter object
//     * @param quant                 the dataset has raw files (quant data analysis will be
//     *                              invoked)
//     */
//    public abstract void execute_SearchGUI_PeptideShaker_WorkFlow(String projectName, String fastaFileId, String searchParameterFileId, Set<String> mgfIdsList, Set<String> searchEnginesList, IdentificationParameters searchParam, boolean quant);
//
//    /**
//     * Save search settings file into galaxy
//     *
//     * @param searchParameters searchParameters .par file
//     * @param isNew            is new search parameter file
//     * @return updated search parameters file list
//     */
//    public abstract Map<String, GalaxyTransferableFile> saveSearchGUIParameters(IdentificationParameters searchParameters, boolean isNew);
//
//    /**
//     * upload file into galaxy
//     *
//     * @param toUploadFiles files to be uploaded to galaxy
//     * @return updated files map
//     */
//    public abstract boolean uploadToGalaxy(PluploadFile[] toUploadFiles);
    @Override
    public void update() {
        workflowDataInputLayout.updateForms();

    }
}
