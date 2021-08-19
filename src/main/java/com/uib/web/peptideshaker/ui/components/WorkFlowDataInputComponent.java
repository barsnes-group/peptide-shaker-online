package com.uib.web.peptideshaker.ui.components;

import com.uib.web.peptideshaker.ui.components.items.HelpPopupButton;
import com.uib.web.peptideshaker.ui.views.modal.PopupWindow;
import com.uib.web.peptideshaker.ui.components.items.StatusLabel;
import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.model.GalaxyFileModel;
import com.uib.web.peptideshaker.model.VisualizationDatasetModel;
import com.uib.web.peptideshaker.ui.components.items.DropDownList;
import com.uib.web.peptideshaker.ui.components.items.MultiSelectOptionGroup;
import com.uib.web.peptideshaker.ui.components.items.RadioButton;
import com.uib.web.peptideshaker.ui.views.FileSystemView;
import com.vaadin.data.Property;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;
import pl.exsio.plupload.PluploadFile;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class represents SearchGUI-Peptide-Shaker work-flow which include input
 * form
 *
 * @author Yehia Mokhtar Farag
 */
public class WorkFlowDataInputComponent extends Panel {

    /**
     * MGF file list available for user to select from.
     */
    private final Set<GalaxyFileModel> _mgfFileList;
    /**
     * mzML file list available for user to select from.
     */
    private final Set<GalaxyFileModel> _mzMLFileList;
    /**
     * Raw file list available for user to select from.
     */
    private final Set<GalaxyFileModel> _rawFileList;
    private final float[] expandingRatio = new float[]{5f, 5f, 58f, 25f, 8f};
    /**
     * Search settings .par file drop-down list .
     */
    private DropDownList searchSettingsFileList;
    /**
     * FASTA file drop-down list .
     */
    private DropDownList _fastaFileInputLayout;
    /**
     * layout contain project name field and execute button.
     */
    private TextField _projectNameField;
    /**
     * Pop-up layout content that has search input available options.
     */
    private SearchParametersForm _searchParameterForm;
    /**
     * Pop-up layout container for edit user search input.
     */
    private PopupWindow _searchParameterFormContainer;
    /**
     * Available pre-saved search parameters files .par from previous searching.
     */
    private Map<String, GalaxyFileModel> serachParamFilesMap;
    /**
     * selected search parameters to perform the search at galaxy server.
     */
    private IdentificationParameters _searchParameters;
    /**
     * Map Galaxy if for FASTA file to FAST files name.
     */
    private Map<String, String> fastaFileIdToNameMap;
    /**
     * Search engines file list available for user to select from.
     */
    private MultiSelectOptionGroup _searchEngines;
    private OptionGroup rawMgfController;
    private AbsoluteLayout spectrumFileContainer;
    private AbsoluteLayout fastaFileContainer;
    private Button executeWorkFlowBtn;
    /**
     * Execution button for the work-flow.
     */
    private AbsoluteLayout inputDataFilesContainer;
    private Uploader mgf_raw_dataUploader;
    private VerticalLayout rawDataListLayout;
    private VerticalLayout mzMLDataListLayout;
    private VerticalLayout mgfDataListLayout;
    private LayoutClickListener rawClickListener;
    private LayoutClickListener mgfClickListener;
    private LayoutClickListener mzMLClickListener;
    private Uploader fastaFileUploader;
    private int indexer = 0;
    private boolean spectrumFileUploaderBusy = Boolean.FALSE;
    private boolean fastaFileUploaderBusy = Boolean.FALSE;
    private boolean numberChanged;
    private final AppManagmentBean appManagmentBean;
    private Map<String, GalaxyFileModel> fastaFilesMap;

    /**
     * Constructor to initialise the main attributes.
     */
    @SuppressWarnings("Convert2Lambda")
    public WorkFlowDataInputComponent() {
        fastaFileIdToNameMap = new LinkedHashMap<>();
        _mgfFileList = new LinkedHashSet<>();
        _rawFileList = new LinkedHashSet<>();
        _mzMLFileList = new LinkedHashSet<>();
        appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
    }

    /**
     * Initialise main form layout.
     */
    public void initLayout() {
        
        WorkFlowDataInputComponent.this.setWidth(100, Unit.PERCENTAGE);
        WorkFlowDataInputComponent.this.setHeight(100, Unit.PERCENTAGE);
        /**
         * MAIN LAYOUT CONFIGURATION.
         *
         */
        AbsoluteLayout container = new AbsoluteLayout();
        container.setHeight(100, Unit.PERCENTAGE);
        container.setWidth(100, Unit.PERCENTAGE);
        WorkFlowDataInputComponent.this.setContent(container);
        WorkFlowDataInputComponent.this.setStyleName("subframe");
        WorkFlowDataInputComponent.this.addStyleName("floatrightinyscreen");

        /**
         * CONFIGURING GRAPHICAL COMPONENTS... Title
         */
        Label titleLabel = new Label("Analyze Data");
        titleLabel.setStyleName("frametitle");
        titleLabel.addStyleName("maintitleheader");
        container.addComponent(titleLabel, "left:40px;top:13px");
        HelpPopupButton helpBtn = new HelpPopupButton(appManagmentBean.getAppConfig().getAnalyze_Data_text(), "", 400, 120);
        container.addComponent(helpBtn, "left:143px;top:0px");
        
        AbsoluteLayout mainContainerLayout = new AbsoluteLayout();
        mainContainerLayout.setSizeFull();
        mainContainerLayout.setStyleName("maincontentcontainer");
        container.addComponent(mainContainerLayout, "left:25px;top:25px;bottom:25px;right:25px");
        
        AbsoluteLayout newProjectContainer = new AbsoluteLayout();
        newProjectContainer.setWidth(100, Unit.PERCENTAGE);
        newProjectContainer.setHeight(460, Unit.PIXELS);
        newProjectContainer.addStyleName("searchguiinputcontainer");
        newProjectContainer.addStyleName("datainputsubcontainer");
        mainContainerLayout.addComponent(newProjectContainer, "left: 15px;right: 15px;top:50%");
        /**
         * Search settings layout.
         */
        AbsoluteLayout searchParameterContainer = initialiseSearchSettingsLayout();
        newProjectContainer.addComponent(searchParameterContainer, "left:25px;top:25px;right:50%");

        /**
         * Search parameter form.
         *
         */
        _searchParameterForm = initialiseSearchParametersForm();
        _searchParameterFormContainer = initialiseSearchParameterFormContainer(_searchParameterForm);
        /**
         * FASTA file drop-down list (open select pop-up option).
         *
         */
        fastaFileContainer = new AbsoluteLayout();
        fastaFileContainer.setCaption("Protein Database (FASTA)");
        fastaFileContainer.setWidth(100, Unit.PERCENTAGE);
        fastaFileContainer.setHeight(28, Unit.PERCENTAGE);
        fastaFileContainer.setStyleName("titleinborder");
        newProjectContainer.addComponent(fastaFileContainer, "left:25px;top:46%;right:50%");
        
        _fastaFileInputLayout = initialiseFastaFileDropdownList();
        fastaFileContainer.addComponent(_fastaFileInputLayout, "left:15px;top:15px;right:15px");
        //add decoy layout
        Label addDecoyLable = new Label("<h6 style='font-size: 12px;font-weight: normal;'>FASTA file should not contain decoys</h6>",ContentMode.HTML);
        addDecoyLable.setWidth(100, Unit.PERCENTAGE);
        addDecoyLable.setHeight(20, Unit.PIXELS);
        fastaFileContainer.addComponent(addDecoyLable, "left:17px;top:18px;right:15px");

        /**
         * Initialise Raw/MGF available files controller.
         */
        spectrumFileContainer = new AbsoluteLayout();
        spectrumFileContainer.setCaption("Spectrum");
        spectrumFileContainer.setWidth(100, Unit.PERCENTAGE);
        spectrumFileContainer.setHeight(58, Unit.PERCENTAGE);
        spectrumFileContainer.setStyleName("titleinborder");
        spectrumFileContainer.addStyleName("marginleft25");
        newProjectContainer.addComponent(spectrumFileContainer, "left:50%;top:25px;right:50px");
        
        rawMgfController = new OptionGroup();
        rawMgfController.setStyleName("controllercombobox");
        rawMgfController.addItem("rawFiles");
        rawMgfController.addItem("mgfFiles");
        rawMgfController.addItem("mzMLFiles");
        
        rawMgfController.setHeight(26, Unit.PERCENTAGE);
        rawMgfController.setHtmlContentAllowed(true);
        rawMgfController.setItemCaption("rawFiles", "Raw");
        rawMgfController.setItemCaption("mgfFiles", "MGF");
        rawMgfController.setItemCaption("mzMLFiles", "mzML");
        spectrumFileContainer.addComponent(rawMgfController, "left:15px;top:15px;right:150px");
        rawMgfController.addValueChangeListener((Property.ValueChangeEvent event) -> {
            String selectedValue = rawMgfController.getValue().toString();
            _mgfFileList.clear();
            _rawFileList.clear();
            if (_searchEngines != null) {
                _searchEngines.setItemEnabled("MetaMorpheus", !selectedValue.equals("mgfFiles"));
            }
            switch (selectedValue) {
                case "mgfFiles":
                    _searchEngines.unselect("MetaMorpheus");
                    mzMLDataListLayout.setVisible(false);
                    rawDataListLayout.setVisible(false);
                    mgfDataListLayout.setVisible(true);
                    break;
                case "mzMLFiles":
                    mzMLDataListLayout.setVisible(true);
                    rawDataListLayout.setVisible(false);
                    mgfDataListLayout.setVisible(false);
                    break;
                case "rawFiles":
                    mzMLDataListLayout.setVisible(false);
                    rawDataListLayout.setVisible(true);
                    mgfDataListLayout.setVisible(false);
                    break;
            }
            
            updateMgfFilesTable();
            updateRawFilesTable();
            updateMzMLFilesTable();
        });
        inputDataFilesContainer = new AbsoluteLayout();
        inputDataFilesContainer.setHeight(100, Unit.PERCENTAGE);
        inputDataFilesContainer.setWidth(100, Unit.PERCENTAGE);
        inputDataFilesContainer.setStyleName("inputdatafilecontainer");
        spectrumFileContainer.addComponent(inputDataFilesContainer, "left:15px;top:52px;right:15px;bottom:10px");
        mgf_raw_dataUploader = new Uploader(appManagmentBean.getAppConfig().getUserFolderUri()) {
            @Override
            public void filesUploaded(PluploadFile[] uploadedFiles) {
                
                File file = (File) uploadedFiles[0].getUploadedFile();
                String fileExtension;
                String fileName = uploadedFiles[0].getName();
                
                if (fileName.toLowerCase().endsWith(CONSTANT.MGF_FILE_EXTENSION)) {
                    fileExtension = CONSTANT.MGF_FILE_EXTENSION;
                } else if (fileName.toLowerCase().endsWith(CONSTANT.mzML_FILE_EXTENSION)) {
                    fileExtension = CONSTANT.mzML_FILE_EXTENSION;
                } else if (fileName.toLowerCase().endsWith(".raw")) {
                    fileExtension = CONSTANT.THERMO_RAW_FILE_EXTENSION;
                } else {
                    return;
                }
                GalaxyFileModel spectrumFile = new GalaxyFileModel();
                spectrumFile.setName(fileName);
                spectrumFile.setExtension(fileExtension);
                spectrumFile.setStatus(CONSTANT.RUNNING_STATUS);
                spectrumFile.setId(file.getAbsolutePath());
                spectrumFile.setDeleted(Boolean.FALSE);
                spectrumFile.setCreatedDate(java.util.Calendar.getInstance().getTime());
                spectrumFile.setDownloadUrl(file.getAbsolutePath());
                
                appManagmentBean.getUserHandler().addToFilesMap(spectrumFile);
                appManagmentBean.getUI_Manager().updateAll();
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.submit(() -> {
                    appManagmentBean.getWorkFlowHandler().uploadFile(file, fileName, fileExtension);
                    file.delete();
                    appManagmentBean.getUserHandler().syncAndUpdateUserData();
                    appManagmentBean.getUI_Manager().updateAll();
                    this.setBusy(false);
                });
                
            }
        };
        mgf_raw_dataUploader.addUploaderFilter(CONSTANT.MGF_FILE_EXTENSION);
        mgf_raw_dataUploader.addUploaderFilter("raw");
        mgf_raw_dataUploader.addUploaderFilter(CONSTANT.mzML_FILE_EXTENSION);
        inputDataFilesContainer.addComponent(mgf_raw_dataUploader, "right:5px;top:-40px");
        if (!appManagmentBean.getAppConfig().isEnableUpload()) {
            mgf_raw_dataUploader.setEnabled(false);
            mgf_raw_dataUploader.setDescription("Upload is disabled in the demo version");
            mgf_raw_dataUploader.addStyleName("permanentdisable");
        }
        
        rawDataListLayout = new VerticalLayout();
        rawDataListLayout.setSizeFull();
        rawDataListLayout.setSpacing(true);
        rawDataListLayout.setStyleName("astablelayout");
        inputDataFilesContainer.addComponent(rawDataListLayout, "top:0px;");
        
        mgfDataListLayout = new VerticalLayout();
        mgfDataListLayout.setSizeFull();
        mgfDataListLayout.setStyleName("astablelayout");
        inputDataFilesContainer.addComponent(mgfDataListLayout, "top:0px;");
        mzMLDataListLayout = new VerticalLayout();
        mzMLDataListLayout.setSizeFull();
        mgfDataListLayout.setStyleName("astablelayout");
        inputDataFilesContainer.addComponent(mzMLDataListLayout, "top:0px;");
        rawMgfController.select("rawFiles");
        /**
         * Initialise Search Engines container.
         */
        AbsoluteLayout searchEnginesContainer = new AbsoluteLayout();
        searchEnginesContainer.setCaption("Search Engines");
        searchEnginesContainer.setWidth(100, Unit.PERCENTAGE);
        searchEnginesContainer.setHeight(100, Unit.PERCENTAGE);
        searchEnginesContainer.setStyleName("titleinborder");
        newProjectContainer.addComponent(searchEnginesContainer, "left:25px;top:65%;right:50%;bottom:10px");
        Map<String, String> searchEnginesList = getSearchEnginesList();
        _searchEngines = initialiseSearchEnginesSelectionContainer(searchEnginesList);
        searchEnginesContainer.addComponent(_searchEngines, "left:15px;top:15px;right:15px;");
        _searchEngines.addValueChangeListener((Property.ValueChangeEvent event) -> {
            if (_searchEngines.getSelectedValue() == null) {
                _searchEngines.selectAll();
            }
        });

        /**
         * Project name field.
         */
        AbsoluteLayout projectNameContainer = new AbsoluteLayout();
        projectNameContainer.setCaption("Project Name");
        projectNameContainer.setWidth(100, Unit.PERCENTAGE);
        projectNameContainer.setHeight(45, Unit.PERCENTAGE);
        projectNameContainer.setStyleName("titleinborder");
        projectNameContainer.addStyleName("marginleft25");
        newProjectContainer.addComponent(projectNameContainer, "left:50%;top:65%;right:50px");
        _projectNameField = initialiseProjectNameInputField();
        projectNameContainer.addComponent(_projectNameField, "left:15px;top:15px;right:15px;");

        // BOTTOM LAYOUT
        AbsoluteLayout _executeWorkFlowButtonLayoutContainer = new AbsoluteLayout();
        _executeWorkFlowButtonLayoutContainer.setWidth(100, Unit.PERCENTAGE);
        _executeWorkFlowButtonLayoutContainer.setHeight(100, Unit.PERCENTAGE);
        _executeWorkFlowButtonLayoutContainer.setStyleName("titleinborder");
        _executeWorkFlowButtonLayoutContainer.addStyleName("marginleft25");
        
        HorizontalLayout bottomLayout = new HorizontalLayout();
        bottomLayout.setSizeFull();
        bottomLayout.setSpacing(true);
        _executeWorkFlowButtonLayoutContainer.addComponent(bottomLayout, "top:7.5px;bottom:7.5px");
        newProjectContainer.addComponent(_executeWorkFlowButtonLayoutContainer, "left:50%;top:84%;right:50px;bottom:10px");
        initialiseExecuteWorkFlowBtn(bottomLayout, _searchEngines, searchEnginesList);
        executeWorkFlowBtn.setEnabled(true);
        _projectNameField.addStyleName("focos");
        rawClickListener = (LayoutEvents.LayoutClickEvent event) -> {
            GalaxyFileModel galaxyFile = (GalaxyFileModel) ((HorizontalLayout) event.getComponent()).getData();
            
            if (_rawFileList.contains(galaxyFile)) {
                _rawFileList.remove(galaxyFile);
            } else {
                _rawFileList.add(galaxyFile);
            }
            updateRawFilesTable();
        };
        mgfClickListener = (LayoutEvents.LayoutClickEvent event) -> {
            GalaxyFileModel galaxyFile = (GalaxyFileModel) ((HorizontalLayout) event.getComponent()).getData();
            if (_mgfFileList.contains(galaxyFile)) {
                _mgfFileList.remove(galaxyFile);
            } else {
                _mgfFileList.add(galaxyFile);
            }
            
            updateMgfFilesTable();
        };
        
        mzMLClickListener = (LayoutEvents.LayoutClickEvent event) -> {
            GalaxyFileModel galaxyFile = (GalaxyFileModel) ((HorizontalLayout) event.getComponent()).getData();
            if (_mzMLFileList.contains(galaxyFile)) {
                _mzMLFileList.remove(galaxyFile);
            } else {
                _mzMLFileList.add(galaxyFile);
            }
            
            updateMzMLFilesTable();
        };
    }

    /**
     * Initialise project name input field
     *
     * @return text field
     */
    private TextField initialiseProjectNameInputField() {
        TextField projectNameField = new TextField();
        projectNameField.setInputPrompt("New Project Name");
        projectNameField.setCaptionAsHtml(true);
        projectNameField.setWidth(100, Unit.PERCENTAGE);
        projectNameField.setRequired(false);
        projectNameField.addStyleName("psprojectname");
        return projectNameField;
    }

    /**
     * Initialise search settings file (par file) drop-dowun list
     *
     * @return drop down list
     */
    private AbsoluteLayout initialiseSearchSettingsLayout() {
        AbsoluteLayout searchParameterContainer = new AbsoluteLayout();
        searchParameterContainer.setCaption("Search Settings");
        searchParameterContainer.setWidth(100, Unit.PERCENTAGE);
        searchParameterContainer.setHeight(37, Unit.PERCENTAGE);
        searchParameterContainer.setStyleName("titleinborder");
        searchParameterContainer.addStyleName("searchparametercontainer");

        // Search settings info
        final Label searchSettingInfo = initialiseSearchParameterFormOverviewLayout();
        searchParameterContainer.addComponent(searchSettingInfo, "left:15px;top:50px;right:15px;bottom:10px;");
        searchSettingsFileList = new DropDownList(null) {
            @Override
            public void setEnabled(boolean enable) {
                if (!enable) {
                    this.removeStyleName("focos");
                }
                super.setEnabled(enable);
            }
        };
        searchSettingsFileList.setWidth(100, Unit.PERCENTAGE);
        searchSettingsFileList.addStyleName("nomargintop");
        searchSettingsFileList.setReadOnly(true);
        searchParameterContainer.addComponent(searchSettingsFileList, "left:15px;top:15px;right:15px");
        
        searchSettingsFileList.addValueChangeListener((Property.ValueChangeEvent event) -> {
            if (searchSettingsFileList.getSelectedValue() != null) {
                if (searchSettingsFileList.getSelectedValue().equalsIgnoreCase("Add new")) {
                    _searchParameterForm.updateForms(appManagmentBean.getWorkFlowHandler().getDefaultIdentificationParameters());
                    _searchParameterFormContainer.setPopupVisible(true);
                    return;
                }
                _searchParameters = appManagmentBean.getWorkFlowHandler().retriveIdentificationParametersFileFromGalaxy(serachParamFilesMap.get(searchSettingsFileList.getSelectedValue()));
                String descrip = "Fixed:" + _searchParameters.getSearchParameters().getModificationParameters().getFixedModifications() + "</br>Variable:" + _searchParameters.getSearchParameters().getModificationParameters().getVariableModifications() + "<br/>Fragment Tolerance: " + _searchParameters.getSearchParameters().getFragmentIonAccuracyInDaltons();
                descrip = descrip.replace("[", "").replace("]", "").replace("null", "No modifications");
                for (String mod : _searchParameterForm.getUpdatedModiList().keySet()) {
                    if (descrip.contains(mod)) {
                        descrip = descrip.replace(mod, _searchParameterForm.getUpdatedModiList().get(mod));
                    }
                }
                searchSettingInfo.setValue(descrip);
                
            }
        }
        );
        
        return searchParameterContainer;
    }

    /**
     * Initialise FASTA file drop-down list and FASTA file upload utility.
     *
     * @return drop-down list with upload support
     */
    private DropDownList initialiseFastaFileDropdownList() {
        fastaFileUploader = new Uploader(appManagmentBean.getAppConfig().getUserFolderUri()) {
            @Override
            public void filesUploaded(PluploadFile[] uploadedFiles) {
                File file = (File) uploadedFiles[0].getUploadedFile();
                String fileName = uploadedFiles[0].getName();
                GalaxyFileModel fastaFile = new GalaxyFileModel();
                fastaFile.setName(fileName);
                fastaFile.setExtension(CONSTANT.FASTA_FILE_EXTENSION);
                fastaFile.setStatus(CONSTANT.RUNNING_STATUS);
                fastaFile.setId(file.getAbsolutePath());
                fastaFile.setDeleted(Boolean.FALSE);
                fastaFile.setCreatedDate(java.util.Calendar.getInstance().getTime());
                fastaFile.setDownloadUrl(file.getAbsolutePath());
                appManagmentBean.getUserHandler().addToFilesMap(fastaFile);
                appManagmentBean.getUI_Manager().updateAll();
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.submit(() -> {
                    appManagmentBean.getWorkFlowHandler().uploadFile(file, fileName, CONSTANT.FASTA_FILE_EXTENSION);
                    file.delete();
                    appManagmentBean.getUserHandler().syncAndUpdateUserData();
                    appManagmentBean.getUI_Manager().updateAll();
                    fastaFileUploader.setBusy(false);
                });
                
            }
            
        };
        fastaFileUploader.addUploaderFilter("fasta");
        if (!appManagmentBean.getAppConfig().isEnableUpload()) {
            fastaFileUploader.setEnabled(false);
            fastaFileUploader.setDescription("Upload is disabled in the demo version");
            fastaFileUploader.addStyleName("permanentdisable");
        }
        final DropDownList fastaFileList = new DropDownList(null) {
            @Override
            public boolean isValid() {
                this.setRequired(true, "No FASTA FILE AVAILABLE");
                boolean check = super.isValid();
                this.setRequired(!check, "No FASTA FILE AVAILABLE");
                return check;
                
            }
            
            @Override
            public void setEnabled(boolean enable) {
                
                if (!enable) {
                    this.removeStyleName("focos");
                }
                super.setEnabled(enable);
            }
        };
        
        fastaFileList.addStyleName("v-caption-on-left");
        fastaFileList.setWidth(100, Unit.PERCENTAGE);
        fastaFileList.addUploadBtn(fastaFileUploader);
        fastaFileList.setReadOnly(true);
        return fastaFileList;
    }

    /**
     * Initialise Search parameters form
     *
     * @return searchSettings utility
     */
    private SearchParametersForm initialiseSearchParametersForm() {
        SearchParametersForm searchSettingsLayout = new SearchParametersForm(true) {
            @Override
            public void saveSearchingFile(IdentificationParameters searchParameters, boolean isNew) {
                checkAndSaveSearchSettingsFile(searchParameters);
                _searchParameterFormContainer.setPopupVisible(false);
                searchSettingsFileList.removeStyleName("focos");
            }
            
            @Override
            public void cancel() {
                _searchParameterFormContainer.setPopupVisible(false);
                searchSettingsFileList.defultSelect();
            }
            
            @Override
            public void setEnabled(boolean enable) {
                
                if (!enable) {
                    this.removeStyleName("focos");
                }
                super.setEnabled(enable);
            }
            
        };
        return searchSettingsLayout;
    }

    /**
     * Initialise search parameter form container
     *
     * @param searchParametersForm
     * @return pup-up window
     */
    private PopupWindow initialiseSearchParameterFormContainer(SearchParametersForm searchParametersForm) {
        PopupWindow editSearchOption = new PopupWindow("Edit") {
            @Override
            public void onClosePopup() {
            }
            
        };
        editSearchOption.setContent(searchParametersForm);
        editSearchOption.setSizeFull();
        editSearchOption.addStyleName("centerwindow");
        return editSearchOption;
    }

    /**
     * Initialise search parameter information label
     *
     * @return label container
     */
    private Label initialiseSearchParameterFormOverviewLayout() {
        Label searchSettingInfo = new Label();
        searchSettingInfo.setWidth(100, Unit.PERCENTAGE);
        searchSettingInfo.setHeight(90, Unit.PIXELS);
        searchSettingInfo.setContentMode(ContentMode.HTML);
        searchSettingInfo.setStyleName("subpanelframe");
        searchSettingInfo.addStyleName("bottomformlayout");
        searchSettingInfo.addStyleName("smallfontlongtext");
        return searchSettingInfo;
    }

    /**
     * Initialise search engines selection container
     *
     * @param searchEnginesList list of available search engines
     * @return layout
     */
    private MultiSelectOptionGroup initialiseSearchEnginesSelectionContainer(Map<String, String> searchEnginesList) {
        MultiSelectOptionGroup searchEngines = new MultiSelectOptionGroup(null, false) {
            @Override
            public void setEnabled(boolean enable) {
                if (!enable) {
                    this.removeStyleName("focos");
                }
                super.setEnabled(enable);
            }
        };
        searchEngines.addStyleName("smallscreenfloatright");
        searchEngines.setRequired(false, "Select at least 1 search engine");
        searchEngines.setViewList(true);
        searchEngines.addStyleName("searchenginesstyle");
        searchEngines.setSizeFull();
        searchEngines.updateList(searchEnginesList);
        searchEngines.setSelectedValue("X!Tandem");
//        searchEngines.setSelectedValue("MS-GF+");
//        searchEngines.setSelectedValue("OMSSA");
        searchEngines.addValueChangeListener(new Property.ValueChangeListener() {
            private boolean selfSelection = false;
            
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (selfSelection) {
                    return;
                }
                Object[] objArr = ((Set<Object>) event.getProperty().getValue()).toArray();
                Object lastSelected;
                if (objArr.length > 0) {
                    lastSelected = objArr[objArr.length - 1];
                    if (lastSelected.toString().equalsIgnoreCase("MetaMorpheus")) {
                        Notification.show("MetaMorpheus only produce results when using raw and mzML format", Notification.Type.TRAY_NOTIFICATION);
                    }
                } else {
                    lastSelected = "";
                }
                if (searchEngines.getSelectedValue() != null && searchEngines.getSelectedValue().size() > 3 && appManagmentBean.getAppConfig().isLimitedsearchengine()) {
                    if (searchEngines.getSelectedValue().contains(lastSelected.toString())) {
                        selfSelection = true;
                        searchEngines.unselect(lastSelected);
                        Notification.show("Maximum 3 Search Engines", Notification.Type.TRAY_NOTIFICATION);
                        selfSelection = false;
                    }
                }
            }
        });
        return searchEngines;
    }

    /**
     * Initialise workflow invoking button
     *
     * @param layout button container
     * @param searchEngines search engine input container
     * @param searchEnginesList available search engine list
     * @return button
     */
    private void initialiseExecuteWorkFlowBtn(AbstractOrderedLayout layout, MultiSelectOptionGroup searchEngines, Map<String, String> searchEnginesList) {
        
        executeWorkFlowBtn = new Button("Execute");
        executeWorkFlowBtn.setStyleName(ValoTheme.BUTTON_SMALL);
        executeWorkFlowBtn.addStyleName(ValoTheme.BUTTON_TINY);
        executeWorkFlowBtn.setWidth(200, Unit.PIXELS);
        executeWorkFlowBtn.setHeight(100, Unit.PERCENTAGE);
        layout.addComponent(executeWorkFlowBtn);
        layout.setComponentAlignment(executeWorkFlowBtn, Alignment.TOP_CENTER);
        
        executeWorkFlowBtn.addClickListener((Button.ClickEvent event) -> {
            if (searchSettingsFileList.getSelectedValue() == null || searchSettingsFileList.getSelectedValue().equalsIgnoreCase("null")) {
                searchSettingsFileList.addStyleName("errorstyle");
                return;
            } else {
                searchSettingsFileList.removeStyleName("errorstyle");
            }
            if (_fastaFileInputLayout.getSelectedValue() == null || _fastaFileInputLayout.getSelectedValue().equalsIgnoreCase("null")) {
                _fastaFileInputLayout.addStyleName("errorstyle");
                return;
            } else {
                _fastaFileInputLayout.removeStyleName("errorstyle");
            }
            if (_mgfFileList.isEmpty() && _rawFileList.isEmpty() && _mzMLFileList.isEmpty()) {
                inputDataFilesContainer.addStyleName("errorstyle");
                return;
            } else {
                inputDataFilesContainer.removeStyleName("errorstyle");
            }
            if (searchEngines.getSelectedValue() == null || searchEngines.getSelectedValue().isEmpty()) {
                searchEngines.setRequired(true, "Select at least 1 search engine");
                return;
            } else {
                searchEngines.setRequired(false, "");
            }
            boolean valid = validateProjectName();
            if (!valid) {
                return;
            }
            
            String fastFileId = this.getFastaFileId();
            
            String projectName = _projectNameField.getValue().trim();
            searchEngines.setRequired(false, "Select at least 1 search engine");
            Map<String, Boolean> selectedSearchEngines = new HashMap<>();
            searchEnginesList.keySet().forEach((paramId) -> {
                selectedSearchEngines.put(paramId.replace("-", "").replace("+", "").trim(), searchEngines.getSelectedValue().contains(paramId));
            });
            Set<GalaxyFileModel> usedList;
            if (!_rawFileList.isEmpty()) {
                usedList = _rawFileList;
            } else if (!_mgfFileList.isEmpty()) {
                usedList = _mgfFileList;
            } else {
                usedList = _mzMLFileList;
            }
            try {
                VisualizationDatasetModel dataset = appManagmentBean.getWorkFlowHandler().executeWorkFlow(projectName, fastaFilesMap.get(fastFileId), serachParamFilesMap.get(searchSettingsFileList.getSelectedValue()), usedList, selectedSearchEngines,_searchParameters);
                if (dataset != null) {
                    appManagmentBean.getUserHandler().addToDatasetMap(dataset);
                    appManagmentBean.getUI_Manager().updateAll();
                    appManagmentBean.getUI_Manager().viewLayout(FileSystemView.class.getName());
                    appManagmentBean.getUserHandler().forceBusyHistory();
                    reset();
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("at Error class: " + this.getClass().getName() + "  line: 679 " + e);
            }
        });
    }

    /**
     * Get list of available search engines
     *
     * @return list of available search engines
     */
    private Map<String, String> getSearchEnginesList() {
        Map<String, String> searchEnginesList = new LinkedHashMap<>();
        searchEnginesList.put("X!Tandem", "X! Tandem");
        searchEnginesList.put("MSGF", "MS-GF+");
        searchEnginesList.put("OMSSA", "OMSSA");
        searchEnginesList.put("Comet", "Comet");
        searchEnginesList.put("Tide", "Tide");
        searchEnginesList.put("MyriMatch", "MyriMatch");
        searchEnginesList.put("MetaMorpheus", "MetaMorpheus");
        searchEnginesList.put("MS_Amanda", "MS Amanda");
        searchEnginesList.put("DirecTag", "DirecTag");
        searchEnginesList.put("Novor", "Novor");
        return searchEnginesList;
    }

    /**
     * Update the tools input forms.
     */
    public void updateForms() {
        this._projectNameField.clear();
        this.updateFastaFileList();
        this.updateSearchParamFileList();
        this.updateSpectrumPanel();
        
    }

    /**
     * Validate and save search setting .par file on galaxy server for future
     * use.
     *
     * @param searchParameters selected search parameters
     */
    private void checkAndSaveSearchSettingsFile(IdentificationParameters searchParameters) {
        this._searchParameters = searchParameters;
        GalaxyFileModel paramFile = appManagmentBean.getWorkFlowHandler().saveSearchParametersFile(searchParameters);//saveSearchGUIParameters(searchParameters, isNew);
        appManagmentBean.getUserHandler().addToFilesMap(paramFile);
        appManagmentBean.getUI_Manager().updateAll();
        
    }

    /**
     * Get selected FASTA file galaxy id
     *
     * @return FASTA file Galaxy id
     */
    public String getFastaFileId() {
        return this._fastaFileInputLayout.getSelectedValue();
    }

    /**
     * update search parameters file drop down list.
     *
     */
    public void updateSpectrumPanel() {
        Set<GalaxyFileModel> mgfFileSet = new LinkedHashSet<>();
        Set<GalaxyFileModel> mzMLFileSet = new LinkedHashSet<>();
        Set<GalaxyFileModel> rawFileSet = new LinkedHashSet<>();
        for (GalaxyFileModel galaxyFile : appManagmentBean.getUserHandler().getFilesToViewList()) {
            switch (galaxyFile.getExtension()) {
                case CONSTANT.MGF_FILE_EXTENSION:
                    mgfFileSet.add(galaxyFile);
                    break;
                case CONSTANT.THERMO_RAW_FILE_EXTENSION:
                    rawFileSet.add(galaxyFile);
                    break;
                case CONSTANT.mzML_FILE_EXTENSION:
                    mzMLFileSet.add(galaxyFile);
                
            }
            
        }
        
        Map<String, String> mgfFileIdToNameMap = new LinkedHashMap<>();
        numberChanged = numberChanged || ((mzMLDataListLayout.getComponentCount() < mzMLFileSet.size()) || (mgfDataListLayout.getComponentCount() < mgfFileSet.size()) || (rawDataListLayout.getComponentCount() < rawFileSet.size()));
        indexer = 1;
        mgfDataListLayout.removeAllComponents();
        rawDataListLayout.removeAllComponents();
        mzMLDataListLayout.removeAllComponents();
        spectrumFileUploaderBusy = false;
        
        mzMLFileSet.forEach((galaxyFile) -> {
            mgfFileIdToNameMap.put(galaxyFile.getId(), galaxyFile.getName());
            StatusLabel statusLabel = new StatusLabel();
            statusLabel.setStatus(galaxyFile.getStatus());
            Label nameLabel = new Label(galaxyFile.getName());
            Label type = new Label();
            type.setValue("<b>" + galaxyFile.getExtension() + "</b>");
            type.setContentMode(ContentMode.HTML);
            type.setDescription(galaxyFile.getExtension());
            RadioButton selectionRadioBtn = new RadioButton(galaxyFile) {
                @Override
                public void selectItem(Object itemId) {
                    
                }
            };
            
            HorizontalLayout rowLayout = initializeRowData(new Component[]{new Label(indexer + ""), selectionRadioBtn, nameLabel, type, statusLabel}, false);
            rowLayout.setId(galaxyFile.getId());
            rowLayout.setEnabled(galaxyFile.getStatus().equals(CONSTANT.OK_STATUS));
            rowLayout.setData(galaxyFile);
            rowLayout.addLayoutClickListener(mzMLClickListener);
            mzMLDataListLayout.addComponent(rowLayout);
            indexer++;
            
        });
        indexer = 1;
        
        mgfFileSet.forEach((galaxyFile) -> {
            mgfFileIdToNameMap.put(galaxyFile.getId(), galaxyFile.getName());
            StatusLabel statusLabel = new StatusLabel();
            statusLabel.setStatus(galaxyFile.getStatus());
            Label nameLabel = new Label(galaxyFile.getName());
            Label type = new Label();
            type.setValue("<b>" + galaxyFile.getExtension() + "</b>");
            type.setContentMode(ContentMode.HTML);
            type.setDescription(galaxyFile.getExtension());
            RadioButton selectionRadioBtn = new RadioButton(galaxyFile) {
                @Override
                public void selectItem(Object itemId) {
                    
                }
            };
            
            HorizontalLayout rowLayout = initializeRowData(new Component[]{new Label(indexer + ""), selectionRadioBtn, nameLabel, type, statusLabel}, false);
            rowLayout.setId(galaxyFile.getId());
            rowLayout.addLayoutClickListener(mgfClickListener);
            rowLayout.setEnabled(galaxyFile.getStatus().equals(CONSTANT.OK_STATUS));
            rowLayout.setData(galaxyFile);
            mgfDataListLayout.addComponent(rowLayout);
            indexer++;
            
        });
        indexer = 1;
        rawFileSet.forEach((galaxyFile) -> {
            StatusLabel statusLabel = new StatusLabel();
            statusLabel.setStatus(galaxyFile.getStatus());
            if (!galaxyFile.getStatus().equals(CONSTANT.OK_STATUS)) {
                spectrumFileUploaderBusy = true;
            }
            Label nameLabel = new Label(galaxyFile.getName());
            Label type = new Label();
            type.setValue("<b>" + galaxyFile.getExtension() + "</b>");
            type.setContentMode(ContentMode.HTML);
            type.setDescription(galaxyFile.getExtension());
            RadioButton selectionRadioBtn = new RadioButton(galaxyFile) {
                @Override
                public void selectItem(Object itemId) {
                    
                }
            };
            
            HorizontalLayout rowLayout = initializeRowData(new Component[]{new Label(indexer + ""), selectionRadioBtn, nameLabel, type, statusLabel}, false);
            rowLayout.setId(galaxyFile.getId());
            rowLayout.addLayoutClickListener(rawClickListener);
            rowLayout.setEnabled(galaxyFile.getStatus().equals(CONSTANT.OK_STATUS));
            rowLayout.setData(galaxyFile);
            rawDataListLayout.addComponent(rowLayout);
            indexer++;
        }
        );
        
        UI.getCurrent().access(() -> {
            
            updateMgfFilesTable();
            updateRawFilesTable();
            updateMzMLFilesTable();
            if (mgf_raw_dataUploader.isBusy() && !spectrumFileUploaderBusy && numberChanged) {
                mgf_raw_dataUploader.setBusy(spectrumFileUploaderBusy);
                UI.getCurrent().push();
                numberChanged = false;
            }
            
        });
        if (!rawFileSet.isEmpty()) {
            rawMgfController.select("rawFiles");
        } else if (!mgfFileSet.isEmpty()) {
            rawMgfController.select("mgfFiles");
        } else if (!mzMLFileSet.isEmpty()) {
            rawMgfController.select("mzMLFiles");
        }
    }

    /**
     * update search parameters file drop down list.
     *
     */
    public void updateSearchParamFileList() {
        this.serachParamFilesMap = new LinkedHashMap<>();
        Map<String, String> searchSettingsFileIdToNameMap = new LinkedHashMap<>();
        appManagmentBean.getUserHandler().getFilesToViewList().stream().filter((galaxyFile) -> (galaxyFile.getExtension().equals(CONSTANT.JSON_FILE_EXTENSION) && galaxyFile.getName().contains(" PAR "))).forEachOrdered((galaxyFile) -> {
            serachParamFilesMap.put(galaxyFile.getId(), galaxyFile);
            searchSettingsFileIdToNameMap.put(galaxyFile.getId(), galaxyFile.getName());
            
        });
        searchSettingsFileIdToNameMap.put("Add new", "Add new");
        searchSettingsFileList.updateList(searchSettingsFileIdToNameMap);
        if (searchSettingsFileIdToNameMap.size() > 1) {
            searchSettingsFileList.setSelected(searchSettingsFileIdToNameMap.keySet().iterator().next());
        }
        searchSettingsFileList.setItemIcon("Add new", VaadinIcons.FILE_ADD);
        
    }

    /**
     * Update selection list for FASTA files.
     */
    public void updateFastaFileList() {
        fastaFilesMap = new LinkedHashMap<>();
        fastaFileUploaderBusy = false;
        fastaFileIdToNameMap = new LinkedHashMap<>();
        appManagmentBean.getUserHandler().getFilesToViewList().stream().filter((galaxyFile) -> (galaxyFile.getExtension().equals(CONSTANT.FASTA_FILE_EXTENSION))).forEachOrdered((galaxyFile) -> {
            fastaFilesMap.put(galaxyFile.getId(), galaxyFile);
            fastaFileIdToNameMap.put(galaxyFile.getId(), galaxyFile.getName());
            
        });
        
        this._fastaFileInputLayout.updateList(fastaFileIdToNameMap);
        if (!fastaFileIdToNameMap.isEmpty()) {
            this._fastaFileInputLayout.setSelected(fastaFileIdToNameMap.keySet().iterator().next());
        }
        fastaFileUploader.setBusy(fastaFileUploaderBusy);
        
    }

    /**
     * Get Selected FASTA file name
     *
     * @param fastaFileID FASTA file Galaxy ID
     * @return FASTA file name
     */
    public String getFastaFileName(String fastaFileID) {
        if (fastaFileIdToNameMap != null && fastaFileIdToNameMap.containsKey(fastaFileID)) {
            return fastaFileIdToNameMap.get(fastaFileID);
        } else {
            return "";
        }
    }

    /**
     * upload file into galaxy
     *
     * @return updated files map
     */
    private HorizontalLayout initializeRowData(Component[] data, boolean header) {
        HorizontalLayout row = new HorizontalLayout();
        row.setSpacing(true);
        int i = 0;
        for (Component component : data) {
            component.addStyleName(ValoTheme.LABEL_NO_MARGIN);
            row.addComponent(component);
            row.setComponentAlignment(component, Alignment.MIDDLE_CENTER);
            row.setExpandRatio(component, expandingRatio[i]);
            i++;
        }
        row.setWidth(100, Unit.PERCENTAGE);
        row.setHeight(30, Unit.PIXELS);
        row.setStyleName("row");
        if (header) {
            row.addStyleName("header");
        }
        
        return row;
    }
    
    private void updateMgfFilesTable() {
        Iterator<Component> itr = mgfDataListLayout.iterator();
        while (itr.hasNext()) {
            HorizontalLayout raw = (HorizontalLayout) itr.next();
            if (_mgfFileList.contains((GalaxyFileModel) raw.getData())) {
                raw.addStyleName("selectedraw");
            } else {
                raw.removeStyleName("selectedraw");
            }
        }
        
    }
    
    private void updateMzMLFilesTable() {
        Iterator<Component> itr = mzMLDataListLayout.iterator();
        while (itr.hasNext()) {
            HorizontalLayout raw = (HorizontalLayout) itr.next();
            if (_mzMLFileList.contains((GalaxyFileModel) raw.getData())) {
                raw.addStyleName("selectedraw");
            } else {
                raw.removeStyleName("selectedraw");
            }
        }
        
    }
    
    private void updateRawFilesTable() {
        Iterator<Component> itr = rawDataListLayout.iterator();
        while (itr.hasNext()) {
            HorizontalLayout raw = (HorizontalLayout) itr.next();
            if (_rawFileList.contains((GalaxyFileModel) raw.getData())) {
                raw.addStyleName("selectedraw");
            } else {
                raw.removeStyleName("selectedraw");
            }
        }
    }

    /**
     * Reset all input form to default.
     */
    public void reset() {
        _rawFileList.clear();
        _mgfFileList.clear();
        _mzMLFileList.clear();
        rawMgfController.select("rawFiles");
        updateRawFilesTable();
        this._projectNameField.clear();
    }
    
    private boolean validateProjectName() {
        boolean valid = false;
        if (_projectNameField.getValue().matches("^((?=[A-Za-z0-9_ -])(?![åäö\\\\]).)*$")) {
            _projectNameField.removeStyleName("errorstyle");
            valid = true;
            
        } else {
            _projectNameField.addStyleName("errorstyle");
            Notification.show("Please use alphabets, numbers, '-' and '_' only ", Notification.Type.TRAY_NOTIFICATION);
        }
        if (appManagmentBean.getUserHandler().getDatasetNames().contains(_projectNameField.getValue().trim().toLowerCase()) && valid) {
            valid = false;
            _projectNameField.addStyleName("errorstyle");
            Notification.show("Dataset name exist, please use different name", Notification.Type.TRAY_NOTIFICATION);
            
        }
        
        return valid;
    }
    
}
