package com.uib.web.peptideshaker.presenter;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;

import com.uib.web.peptideshaker.presenter.layouts.SearchGUIPeptideShakerWorkFlowInputLayout;
import com.uib.web.peptideshaker.presenter.core.PresenterSubViewSideBtn;
import com.uib.web.peptideshaker.presenter.core.ButtonWithLabel;
import com.uib.web.peptideshaker.presenter.core.SmallSideBtn;
import com.vaadin.event.LayoutEvents;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import pl.exsio.plupload.PluploadFile;

/**
 * This class represent web tool presenter which is responsible for managing the
 * view and interactivity of the tool
 *
 * @author Yehia Farag
 */
public abstract class SearchGUIPeptideShakerToolPresenter extends VerticalLayout implements ViewableFrame, LayoutEvents.LayoutClickListener {

    /**
     * The tools layout side button.
     */
    protected SmallSideBtn smallPresenterButton;
    /**
     * The tools layout side button.
     */
    protected ButtonWithLabel mainPresenterBtn;
    /**
     * The work-flow input form layout container.
     */
    protected SearchGUIPeptideShakerWorkFlowInputLayout peptideshakerToolInputForm;
    /**
     * The work-flow side button container (left side button container).
     */
    protected VerticalLayout btnContainer;

    /**
     * Initialise the web tool main attributes
     *
     */
    public SearchGUIPeptideShakerToolPresenter() {
        SearchGUIPeptideShakerToolPresenter.this.setSizeFull();
        SearchGUIPeptideShakerToolPresenter.this.setStyleName("activelayout");
        SearchGUIPeptideShakerToolPresenter.this.addStyleName("integratedframe");
        initLayout();
    }

    private void initLayout() {
        smallPresenterButton = new SmallSideBtn("img/searchguiblue.png");//spectra2.pngimg/searchgui-medium-shadow-2.png
        smallPresenterButton.setData(SearchGUIPeptideShakerToolPresenter.this.getViewId());
        smallPresenterButton.setDescription("Search and process data (SearchGUI and PeptideShaker)");
        smallPresenterButton.addStyleName("smalltoolsbtn");
        smallPresenterButton.addStyleName("searchguiicon");
        mainPresenterBtn = new ButtonWithLabel("Analyze Data</br><font>Search and process data</font>", 1);//spectra2.png
        mainPresenterBtn.setData(SearchGUIPeptideShakerToolPresenter.this.getViewId());
        mainPresenterBtn.updateIconResource(new ThemeResource("img/searchguiblue.png"));//img/workflow3.png
        mainPresenterBtn.addStyleName("searchguiicon");
        btnContainer = new VerticalLayout();
        btnContainer.setWidth(100, Unit.PERCENTAGE);
        btnContainer.setHeightUndefined();
        btnContainer.setSpacing(true);
        btnContainer.setMargin(new MarginInfo(false, false, true, false));
        btnContainer.addStyleName("singlebtn");

        peptideshakerToolInputForm = new SearchGUIPeptideShakerWorkFlowInputLayout() {
            @Override
            public void executeWorkFlow(String projectName, String fastaFileId, String searchParameterFileId, Set<String> inputFilesIdsList, Set<String> searchEnginesList, IdentificationParameters searchParam, boolean quant) {
//                if (quant && inputFilesIdsList.size() > 1) {
//                    Notification.show("Runing work flow with multiple thermo.raw files is not supported", Notification.Type.WARNING_MESSAGE);
//                    return;
//                }

                SearchGUIPeptideShakerToolPresenter.this.execute_SearchGUI_PeptideShaker_WorkFlow(projectName, fastaFileId, searchParameterFileId, inputFilesIdsList, searchEnginesList, searchParam, quant);
                this.reset();
            }

            @Override
            public boolean uploadToGalaxy(PluploadFile[] toUploadFiles) {
                return SearchGUIPeptideShakerToolPresenter.this.uploadToGalaxy(toUploadFiles);
            }

            @Override
            public Map<String, GalaxyTransferableFile> saveSearchGUIParameters(IdentificationParameters searchParameters, boolean isNEw) {
                return SearchGUIPeptideShakerToolPresenter.this.saveSearchGUIParameters(searchParameters, isNEw);
            }

        };
        peptideshakerToolInputForm.initLayout();

        PresenterSubViewSideBtn workFlowBtn = new PresenterSubViewSideBtn("Work-Flow", 2);
        workFlowBtn.updateIconByResource(new ThemeResource("img/searchguiblue.png"));
        workFlowBtn.addStyleName("searchguiicon");
        workFlowBtn.addStyleName("padding20");
        workFlowBtn.setData("workflow");
        btnContainer.addComponent(workFlowBtn);
        btnContainer.setComponentAlignment(workFlowBtn, Alignment.TOP_CENTER);
        workFlowBtn.addLayoutClickListener(SearchGUIPeptideShakerToolPresenter.this);
        workFlowBtn.setSelected(true);

        VerticalLayout toolViewFrame = new VerticalLayout();
        toolViewFrame.setSizeFull();
        toolViewFrame.setStyleName("viewframe");

        this.addComponent(toolViewFrame);
        this.setExpandRatio(toolViewFrame, 100);

        AbsoluteLayout toolViewFrameContent = new AbsoluteLayout();
        toolViewFrameContent.addStyleName("viewframecontent");
        toolViewFrameContent.setSizeFull();
        toolViewFrame.addComponent(toolViewFrameContent);
        toolViewFrameContent.addComponent(peptideshakerToolInputForm);

        SearchGUIPeptideShakerToolPresenter.this.minimizeView();
        this.mainPresenterBtn.setDescription("Search and process data (SearchGUI and PeptideShaker)");
    }

    private Label initBusyUploadPanel() {
        Label busyUploadPanel = new Label("<img src='VAADIN/themes/webpeptideshakertheme/img/globeearthanimation.gif' alt='' style='width: 17px;top: 10px;background-color: white;position: relative!important;z-index: 3!important;'>", ContentMode.HTML);
        busyUploadPanel.setSizeFull();
        busyUploadPanel.setStyleName("busypanel");
        return busyUploadPanel;
    }

    /**
     * Update Online PeptideShaker files from Galaxy Server
     *
     * @param historyFilesMap List of available files on Galaxy Server
     */
    public void updateData(Map<String, GalaxyFileObject> historyFilesMap) {
        if (historyFilesMap != null) {
            Map<String, GalaxyTransferableFile> searchSettingFilesMap = new LinkedHashMap<>();
            Map<String, GalaxyFileObject> fastaFilesMap = new LinkedHashMap<>();
            Map<String, GalaxyFileObject> mgfFilesMap = new LinkedHashMap<>();
            Map<String, GalaxyFileObject> rawFilesMap = new LinkedHashMap<>();
             Map<String, GalaxyFileObject> mzMLFilesMap = new LinkedHashMap<>();
            for (String fileKey : historyFilesMap.keySet()) {
                GalaxyFileObject fileObject = historyFilesMap.get(fileKey);
                String type = fileObject.getType();
                switch (type) {
                    case "MGF":
                        mgfFilesMap.put(fileKey, fileObject);
                        break;
                    case "FASTA":
                        fastaFilesMap.put(fileKey, fileObject);
                        break;

                    case "Parameters File (JSON)":
                        searchSettingFilesMap.put(fileKey, (GalaxyTransferableFile) fileObject);
                        break;

                    case "Thermo.raw":
                        rawFilesMap.put(fileKey, fileObject);
                        break;
                         case "mzML":
                        mzMLFilesMap.put(fileKey, fileObject);
                        break;
                }

            }
            if (smallPresenterButton.getStyleName().contains("selectedpresenterbtn")) {
                UI.getCurrent().accessSynchronously(() -> {
                    peptideshakerToolInputForm.updateForm(searchSettingFilesMap, fastaFilesMap, mgfFilesMap, rawFilesMap,mzMLFilesMap);
                    UI.getCurrent().push();
                });

            } else {
                peptideshakerToolInputForm.updateForm(searchSettingFilesMap, fastaFilesMap, mgfFilesMap, rawFilesMap,mzMLFilesMap);
            }

        }
    }

    /**
     * Get main visualisation presenter
     *
     * @return main layout container for the selected visualisation
     */
    @Override
    public VerticalLayout getMainView() {
        return this;
    }

    /**
     * Get small presenter button for the top left side button container
     *
     * @return small button
     */
    @Override
    public SmallSideBtn getSmallPresenterControlButton() {
        return smallPresenterButton;
    }

    /**
     * Get main presenter button (used in welcome page)
     *
     * @return presenter button
     */
    @Override
    public ButtonWithLabel getMainPresenterButton() {
        return mainPresenterBtn;
    }

    /**
     * Get the presenter id
     *
     * @return unique id for the presenter view
     */
    @Override
    public String getViewId() {
        return SearchGUIPeptideShakerToolPresenter.class.getName();
    }

    /**
     * Hide this presenter visualisation
     */
    @Override
    public void minimizeView() {
        smallPresenterButton.setSelected(false);
        mainPresenterBtn.setSelected(false);
        this.addStyleName("hidepanel");
        this.btnContainer.removeStyleName("visible");

    }

    /**
     * Show this presenter visualisation
     */
    @Override
    public void maximizeView() {

        smallPresenterButton.setSelected(true);
        mainPresenterBtn.setSelected(true);
        this.btnContainer.addStyleName("visible");
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
    @Override
    public VerticalLayout getSubViewButtonsActionContainerLayout() {
        return btnContainer;
    }

    /**
     * Run Online Peptide-Shaker work-flow
     *
     * @param projectName The project name
     * @param fastaFileId FASTA file dataset id
     * @param searchParameterFileId .par file id
     * @param mgfIdsList list of MGF file dataset ids
     * @param searchEnginesList List of selected search engine names
     * @param searchParam search parameter object
     * @param quant the dataset has raw files (quant data analysis will be
     * invoked)
     */
    public abstract void execute_SearchGUI_PeptideShaker_WorkFlow(String projectName, String fastaFileId, String searchParameterFileId, Set<String> mgfIdsList, Set<String> searchEnginesList, IdentificationParameters searchParam, boolean quant);

    /**
     * Save search settings file into galaxy
     *
     *
     * @param searchParameters searchParameters .par file
     * @param isNew is new search parameter file
     * @return updated search parameters file list
     */
    public abstract Map<String, GalaxyTransferableFile> saveSearchGUIParameters(IdentificationParameters searchParameters, boolean isNew);

    /**
     * upload file into galaxy
     *
     *
     * @param toUploadFiles files to be uploaded to galaxy
     * @return updated files map
     */
    public abstract boolean uploadToGalaxy(PluploadFile[] toUploadFiles);

}
