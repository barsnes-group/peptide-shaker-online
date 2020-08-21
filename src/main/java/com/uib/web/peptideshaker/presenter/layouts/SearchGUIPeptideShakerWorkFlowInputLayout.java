package com.uib.web.peptideshaker.presenter.layouts;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyTransferableFile;
import com.uib.web.peptideshaker.presenter.core.*;
import com.vaadin.data.Property;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import pl.exsio.plupload.PluploadFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class represents SearchGUI-Peptide-Shaker work-flow which include input
 * form
 *
 * @author Yehia Farag
 */
public abstract class SearchGUIPeptideShakerWorkFlowInputLayout extends Panel {

    /**
     * MGF file list available for user to select from.
     */
    private final Set<String> _mgfFileList;
    /**
     * mzML file list available for user to select from.
     */
    private final Set<String> _mzMLFileList;
    /**
     * Raw file list available for user to select from.
     */
    private final Set<String> _rawFileList;
    private final float[] expandingRatio = new float[]{5f, 5f, 58f, 25f, 8f};
    /**
     * Search settings .par file drop-down list .
     */
    protected DropDownList _searchSettingsFileList;
    /**
     * FASTA file drop-down list .
     */
    protected DropDownList _fastaFileInputLayout;
    /**
     * layout contain project name field and execute button.
     */
    protected TextField _projectNameField;
    /**
     * Pop-up layout content that has search input available options.
     */
    protected SearchParametersForm _searchParameterForm;
    /**
     * Pop-up layout container for edit user search input.
     */
    protected PopupWindow _searchParameterFormContainer;
    /**
     * Available pre-saved search parameters files .par from previous searching.
     */
    protected Map<String, GalaxyTransferableFile> _searchSettingsMap;
    /**
     * updated search parameters to perform the search at galaxy server.
     */
//    protected SearchParameters _updatedSearchParameters;  
    /**
     * selected search parameters to perform the search at galaxy server.
     */
    protected IdentificationParameters _searchParameters;
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
     * Execution button for the work-flow
     */
    private Button _executeWorkFlowBtn;
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
    private Set<String> datasetNames;

    /**
     * Constructor to initialise the main attributes.
     */
    @SuppressWarnings("Convert2Lambda")
    public SearchGUIPeptideShakerWorkFlowInputLayout() {
        fastaFileIdToNameMap = new LinkedHashMap<>();
        _mgfFileList = new LinkedHashSet<>();
        _rawFileList = new LinkedHashSet<>();
        _mzMLFileList = new LinkedHashSet<>();
    }

    /**
     * Initialise main form layout.
     */
    public void initLayout() {

        SearchGUIPeptideShakerWorkFlowInputLayout.this.setWidth(100, Unit.PERCENTAGE);
        SearchGUIPeptideShakerWorkFlowInputLayout.this.setHeight(100, Unit.PERCENTAGE);
        /**
         * MAIN LAYOUT CONFIGURATION
         *
         */
        AbsoluteLayout container = new AbsoluteLayout();
        container.setHeight(100, Unit.PERCENTAGE);
        container.setWidth(100, Unit.PERCENTAGE);
        SearchGUIPeptideShakerWorkFlowInputLayout.this.setContent(container);
        SearchGUIPeptideShakerWorkFlowInputLayout.this.setStyleName("subframe");
        SearchGUIPeptideShakerWorkFlowInputLayout.this.addStyleName("floatrightinyscreen");

        /**
         * CONFIGURING GRAPHICAL COMPONENTS... Title
         */
        Label titleLabel = new Label("Analyze Data");
        titleLabel.setStyleName("frametitle");
        titleLabel.addStyleName("maintitleheader");
        container.addComponent(titleLabel, "left:40px;top:13px");
        Help helpBtn = new Help("<h1>Analyze Data (create new projects)</h1>Users can upload their data files (FASTA files, Spectrum input files) and process their data using SearchGUI and visualize the result dataset.</br>Currently the supported Spectrum input files are (MGF and mzML) for identification datastes and (therm.raw) files for quantification datasets.", "", 400, 155);
        container.addComponent(helpBtn, "left:143px;top:0px");

        AbsoluteLayout mainContainerLayout = new AbsoluteLayout();
        mainContainerLayout.setSizeFull();
        mainContainerLayout.setStyleName("maincontentcontainer");
        container.addComponent(mainContainerLayout, "left:25px;top:25px;bottom:25px;right:25px");

        AbsoluteLayout newProjectContainer = new AbsoluteLayout();
        newProjectContainer.setWidth(100, Unit.PERCENTAGE);
        newProjectContainer.setHeight(426, Unit.PIXELS);
        newProjectContainer.addStyleName("searchguiinputcontainer");
        newProjectContainer.addStyleName("datainputsubcontainer");
        mainContainerLayout.addComponent(newProjectContainer, "left: 15px;right: 15px;top:50%");

        /**
         * Search settings layout.
         */
        AbsoluteLayout searchParameterContainer = new AbsoluteLayout();
        searchParameterContainer.setCaption("Search Settings (Search Parameter File)");
        searchParameterContainer.setWidth(100, Unit.PERCENTAGE);
        searchParameterContainer.setHeight(37, Unit.PERCENTAGE);
        searchParameterContainer.setStyleName("titleinborder");
        searchParameterContainer.addStyleName("searchparametercontainer");
        newProjectContainer.addComponent(searchParameterContainer, "left:25px;top:25px;right:50%");
        _searchSettingsFileList = initialiseSearchSettingsFileDropdownList();
        searchParameterContainer.addComponent(_searchSettingsFileList, "left:15px;top:15px;right:15px");

        // Search settings info
        Label searchSettingInfo = initialiseSearchParameterFormOverviewLayout();
        searchParameterContainer.addComponent(searchSettingInfo, "left:15px;top:50px;right:15px;bottom:10px;");
        /**
         * Search parameter form*
         */
        _searchParameterForm = initialiseSearchParametersForm();
        // Edit search option
        _searchParameterFormContainer = initialiseSearchParameterFormContainer(_searchParameterForm);
        /**
         * FASTA file drop-down list (open select pop-up option).
         *
         */
        fastaFileContainer = new AbsoluteLayout();
        fastaFileContainer.setCaption("Protein Database (FASTA File)");
        fastaFileContainer.setWidth(100, Unit.PERCENTAGE);
        fastaFileContainer.setHeight(28, Unit.PERCENTAGE);
        fastaFileContainer.setStyleName("titleinborder");
        newProjectContainer.addComponent(fastaFileContainer, "left:25px;top:46%;right:50%");

        _fastaFileInputLayout = initialiseFastaFileDropdownList();
        fastaFileContainer.addComponent(_fastaFileInputLayout, "left:15px;top:15px;right:15px");

        /**
         * Initialise Raw/MGF available files controller.
         */
        spectrumFileContainer = new AbsoluteLayout();
        spectrumFileContainer.setCaption("Spectrum Files (Raw/MGF Files)");
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
        rawMgfController.setItemCaption("rawFiles", "Raw File(s)");
        rawMgfController.setItemCaption("mgfFiles", "MGF File(s)");
        rawMgfController.setItemCaption("mzMLFiles", "mzML File(s)");
        spectrumFileContainer.addComponent(rawMgfController, "left:15px;top:15px;right:150px");
        rawMgfController.addValueChangeListener((Property.ValueChangeEvent event) -> {
            String selectedValue = rawMgfController.getValue().toString();
            _mgfFileList.clear();
            _rawFileList.clear();
            switch (selectedValue) {
                case "mgfFiles":
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
        mgf_raw_dataUploader = new Uploader() {
            @Override
            public void filesUploaded(PluploadFile[] uploadedFiles) {
                uploadToGalaxy(uploadedFiles);
            }
        };
        mgf_raw_dataUploader.addUploaderFilter("mgf");
        mgf_raw_dataUploader.addUploaderFilter("thermo.raw");
        mgf_raw_dataUploader.addUploaderFilter("mzml");
        inputDataFilesContainer.addComponent(mgf_raw_dataUploader, "right:5px;top:-40px");

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
        _executeWorkFlowBtn = initialiseExecuteWorkFlowBtn(bottomLayout, _searchEngines, searchEnginesList);
        _executeWorkFlowBtn.setEnabled(true);
        initialiseInputComponentsListener(_searchSettingsFileList, searchSettingInfo, _searchEngines, _executeWorkFlowBtn);
        _projectNameField.addStyleName("focos");
        rawClickListener = (LayoutEvents.LayoutClickEvent event) -> {
            String id = event.getComponent().getId();

            if (_rawFileList.contains(id)) {
                _rawFileList.remove(id);
            } else {
                _rawFileList.add(id);
            }
            updateRawFilesTable();
        };
        mgfClickListener = (LayoutEvents.LayoutClickEvent event) -> {
            String id = event.getComponent().getId();
            if (_mgfFileList.contains(id)) {
                _mgfFileList.remove(id);
            } else {
                _mgfFileList.add(id);
            }

            updateMgfFilesTable();
        };

        mzMLClickListener = (LayoutEvents.LayoutClickEvent event) -> {
            String id = event.getComponent().getId();
            if (_mzMLFileList.contains(id)) {
                _mzMLFileList.remove(id);
            } else {
                _mzMLFileList.add(id);
            }

            updateMzMLFilesTable();
        };
    }

    /**
     * Initialise project name input field
     *
     * @return text field
     */
    protected TextField initialiseProjectNameInputField() {
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
    protected DropDownList initialiseSearchSettingsFileDropdownList() {
        DropDownList searchSettingsFileList = new DropDownList(null) {
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
        return searchSettingsFileList;
    }

    /**
     * Initialise fasta file dropdown list and FASTA file upload utility.
     *
     * @return drop-down list with uploader support
     */
    protected DropDownList initialiseFastaFileDropdownList() {
        fastaFileUploader = new Uploader() {
            @Override
            public void filesUploaded(PluploadFile[] uploadedFiles) {
                uploadToGalaxy(uploadedFiles);
            }

        };
        fastaFileUploader.addUploaderFilter("fasta");
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
    protected SearchParametersForm initialiseSearchParametersForm() {
        SearchParametersForm searchSettingsLayout = new SearchParametersForm(false) {
            @Override
            public void saveSearchingFile(IdentificationParameters searchParameters, boolean isNew) {
                checkAndSaveSearchSettingsFile(searchParameters, isNew);
                _searchParameterFormContainer.setPopupVisible(false);
                _searchSettingsFileList.removeStyleName("focos");
            }

            @Override
            public void cancel() {
                _searchParameterFormContainer.setPopupVisible(false);
                _searchSettingsFileList.defultSelect();
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
    protected PopupWindow initialiseSearchParameterFormContainer(SearchParametersForm searchParametersForm) {
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
    protected Label initialiseSearchParameterFormOverviewLayout() {
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
    protected MultiSelectOptionGroup initialiseSearchEnginesSelectionContainer(Map<String, String> searchEnginesList) {
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
        searchEngines.setSelectedValue("MS-GF+");
        searchEngines.setSelectedValue("OMSSA");
        return searchEngines;
    }

    /**
     * Initialise workflow invoking button
     *
     * @param layout            button container
     * @param searchEngines     search engine input container
     * @param searchEnginesList available search engine list
     * @return button
     */
    protected Button initialiseExecuteWorkFlowBtn(AbstractOrderedLayout layout, MultiSelectOptionGroup searchEngines, Map<String, String> searchEnginesList) {

        executeWorkFlowBtn = new Button("Execute");
        executeWorkFlowBtn.setStyleName(ValoTheme.BUTTON_SMALL);
        executeWorkFlowBtn.addStyleName(ValoTheme.BUTTON_TINY);
        executeWorkFlowBtn.setWidth(200, Unit.PIXELS);
        executeWorkFlowBtn.setHeight(100, Unit.PERCENTAGE);
        layout.addComponent(executeWorkFlowBtn);
        layout.setComponentAlignment(executeWorkFlowBtn, Alignment.TOP_CENTER);

        executeWorkFlowBtn.addClickListener((Button.ClickEvent event) -> {
            if (_searchSettingsFileList.getSelectedValue() == null || _searchSettingsFileList.getSelectedValue().equalsIgnoreCase("null")) {
                _searchSettingsFileList.addStyleName("errorstyle");
                return;
            } else {
                _searchSettingsFileList.removeStyleName("errorstyle");
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
            Set<String> spectrumIds = _mgfFileList;
            Set<String> rawIds = _rawFileList;
            Set<String> mzmlIds = _mzMLFileList;
            Set<String> searchEnginesIds = searchEngines.getSelectedValue();
            String projectName = _projectNameField.getValue().trim();//.replace(" ", "_").replace("-", "_") + "___" + (new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss").format(new Timestamp(System.currentTimeMillis()))).replace(":", "_-_").replace("-original-input", "-input");

            searchEngines.setRequired(false, "Select at least 1 search engine");
            Map<String, Boolean> selectedSearchEngines = new HashMap<>();
            searchEnginesList.keySet().forEach((paramId) -> {
                selectedSearchEngines.put(paramId, searchEngines.getSelectedValue().contains(paramId));
            });
            String searchParamerterId = _searchSettingsMap.get(_searchSettingsFileList.getSelectedValue()).getGalaxyId();
            if (!_rawFileList.isEmpty()) {
                executeWorkFlow(projectName, fastFileId, searchParamerterId, rawIds, searchEnginesIds, _searchParameterForm.getSearchParameters(), true);
            } else if (!_mgfFileList.isEmpty()) {

                executeWorkFlow(projectName, fastFileId, searchParamerterId, spectrumIds, searchEnginesIds, _searchParameterForm.getSearchParameters(), false);
            } else {
                executeWorkFlow(projectName, fastFileId, searchParamerterId, mzmlIds, searchEnginesIds, _searchParameterForm.getSearchParameters(), false);
            }
        });
        return executeWorkFlowBtn;
    }

    /**
     * Initialise listeners to validate input fields
     *
     * @param searchSettingsFileList parameter files drop-down list .par files
     * @param searchSettingInfo      search parameter overview label
     * @param searchEngines          search engines selection container
     * @param executeWorkFlowBtn     work flow invoking button
     */
    protected void initialiseInputComponentsListener(DropDownList searchSettingsFileList, Label searchSettingInfo, MultiSelectOptionGroup searchEngines, Button executeWorkFlowBtn) {

        _searchSettingsFileList.addValueChangeListener((Property.ValueChangeEvent event) -> {
                    if (_searchSettingsFileList.getSelectedValue() != null) {
                        if (_searchSettingsFileList.getSelectedValue().equalsIgnoreCase("Add new")) {
                            String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
                            try {
                                File file = new File(basepath + "/VAADIN/SEARCHGUI_IdentificationParameters.json");
                                IdentificationParameters searchParamUtil = IdentificationParameters.getIdentificationParameters(file);
                                _searchParameterForm.updateForms(searchParamUtil);
                                _searchParameterFormContainer.setPopupVisible(true);
                            } catch (IOException ex) {
                                System.err.println("Error: IOException at line : 655 " + ex);
                            }
                            return;
                        }

                        try {
                            File file = _searchSettingsMap.get(_searchSettingsFileList.getSelectedValue()).getFile();
                            if (IdentificationParameters.supportedVersion(file)) {
                                _searchParameters = IdentificationParameters.getIdentificationParameters(file);
                            }
                            String descrip = "Fixed:" + _searchParameters.getSearchParameters().getModificationParameters().getFixedModifications() + "</br>Variable:" + _searchParameters.getSearchParameters().getModificationParameters().getVariableModifications() + "<br/>Fragment Tolerance:" + _searchParameters.getSearchParameters().getFragmentIonAccuracyInDaltons();
                            descrip = descrip.replace("[", "").replace("]", "").replace("null", "No modifications");
                            for (String mod : _searchParameterForm.getUpdatedModiList().keySet()) {
                                if (descrip.contains(mod)) {
                                    descrip = descrip.replace(mod, _searchParameterForm.getUpdatedModiList().get(mod));
                                }

                            }
                            searchSettingInfo.setValue(descrip);
                        } catch (IOException ex) {
                            System.err.println("Error: IOException at line : 666 " + ex);
                        }
                    }
                }
        );
    }

    /**
     * Get list of available search engines
     *
     * @return list of available search engines
     */
    protected Map<String, String> getSearchEnginesList() {
        Map<String, String> searchEnginesList = new LinkedHashMap<>();
        searchEnginesList.put("X!Tandem", "X! Tandem");
        searchEnginesList.put("MS-GF+", "MS-GF+");
        searchEnginesList.put("OMSSA", "OMSSA");
        searchEnginesList.put("Comet", "Comet");
        searchEnginesList.put("Tide", "Tide");
        searchEnginesList.put("MyriMatch", "MyriMatch");
        searchEnginesList.put("MS_Amanda", "MS Amanda");
        searchEnginesList.put("DirecTag", "DirecTag");
        searchEnginesList.put("Novor (Select for non-commercial use only)", "Novor");
        return searchEnginesList;
    }

    /**
     * Update the tools input forms
     *
     * @param searchSettingsMap search settings .par files map
     * @param fastaFilesMap     FASTA files map
     * @param mgfFilesMap       MGF file map
     * @param rawFilesMap       Raw file map
     * @param mzMLFilesMap      mzML files map
     * @param datasetNames      set of user dataset names
     */
    public void updateForm(Map<String, GalaxyTransferableFile> searchSettingsMap, Map<String, GalaxyFileObject> fastaFilesMap, Map<String, GalaxyFileObject> mgfFilesMap, Map<String, GalaxyFileObject> rawFilesMap, Map<String, GalaxyFileObject> mzMLFilesMap, Set<String> datasetNames) {
        this.datasetNames = datasetNames;
        this._searchSettingsMap = searchSettingsMap;
        this.updateFastaFileList(fastaFilesMap);
        Map<String, String> searchSettingsFileIdToNameMap = new LinkedHashMap<>();
        Object selectedId = "";
        for (String id : searchSettingsMap.keySet()) {
            searchSettingsFileIdToNameMap.put(id, searchSettingsMap.get(id).getGalaxyFileObject().getName().replace(".json", ""));
            selectedId = id;
        }
        searchSettingsFileIdToNameMap.put("Add new", "Add new");
        _searchSettingsFileList.updateList(searchSettingsFileIdToNameMap);
        _searchSettingsFileList.setSelected(selectedId);
        _searchSettingsFileList.setItemIcon("Add new", VaadinIcons.FILE_ADD);
        Map<String, String> mgfFileIdToNameMap = new LinkedHashMap<>();
        numberChanged = numberChanged || ((mzMLDataListLayout.getComponentCount() < mzMLFilesMap.size()) || (mgfDataListLayout.getComponentCount() < mgfFilesMap.size()) || (rawDataListLayout.getComponentCount() < rawFilesMap.size()));
        indexer = 1;
        mgfDataListLayout.removeAllComponents();
        rawDataListLayout.removeAllComponents();
        mzMLDataListLayout.removeAllComponents();
        spectrumFileUploaderBusy = false;

        mzMLFilesMap.keySet().forEach((id) -> {

            GalaxyFileObject ds = mzMLFilesMap.get(id);
            mgfFileIdToNameMap.put(id, ds.getName());
            StatusLabel statusLabel = new StatusLabel();
            statusLabel.setStatus("ok");
            Label nameLabel = new Label(ds.getName());
            Label type = new Label();
            type.setValue("<b>" + ds.getType() + "</b>");
            type.setContentMode(ContentMode.HTML);
            type.setDescription(ds.getType());
            RadioButton selectionRadioBtn = new RadioButton(id) {
                @Override
                public void selectItem(Object itemId) {

                }
            };

            HorizontalLayout rowLayout = initializeRowData(new Component[]{new Label(indexer + ""), selectionRadioBtn, nameLabel, type, statusLabel}, false);
            rowLayout.setId(id);
            rowLayout.addLayoutClickListener(mzMLClickListener);
            mzMLDataListLayout.addComponent(rowLayout);
            indexer++;

        });
        indexer = 1;

        mgfFilesMap.keySet().forEach((id) -> {

            GalaxyFileObject ds = mgfFilesMap.get(id);
            mgfFileIdToNameMap.put(id, ds.getName());
            StatusLabel statusLabel = new StatusLabel();
            statusLabel.setStatus("ok");
            Label nameLabel = new Label(ds.getName());
            Label type = new Label();
            type.setValue("<b>" + ds.getType() + "</b>");
            type.setContentMode(ContentMode.HTML);
            type.setDescription(ds.getType());
            RadioButton selectionRadioBtn = new RadioButton(id) {
                @Override
                public void selectItem(Object itemId) {

                }
            };

            HorizontalLayout rowLayout = initializeRowData(new Component[]{new Label(indexer + ""), selectionRadioBtn, nameLabel, type, statusLabel}, false);
            rowLayout.setId(id);
            rowLayout.addLayoutClickListener(mgfClickListener);
            mgfDataListLayout.addComponent(rowLayout);
            indexer++;

        });
        indexer = 1;
        rawFilesMap.keySet().forEach((id) -> {
                    GalaxyFileObject ds = rawFilesMap.get(id);
                    StatusLabel statusLabel = new StatusLabel();
                    statusLabel.setStatus(ds.getStatus());
                    if (!ds.getStatus().equalsIgnoreCase("ok")) {
                        spectrumFileUploaderBusy = true;
                    }
                    Label nameLabel = new Label(ds.getName());
                    Label type = new Label();
                    type.setValue("<b>" + ds.getType() + "</b>");
                    type.setContentMode(ContentMode.HTML);
                    type.setDescription(ds.getType());
                    RadioButton selectionRadioBtn = new RadioButton(id) {
                        @Override
                        public void selectItem(Object itemId) {

                        }
                    };

                    HorizontalLayout rowLayout = initializeRowData(new Component[]{new Label(indexer + ""), selectionRadioBtn, nameLabel, type, statusLabel}, false);
                    rowLayout.setId(id);
                    rowLayout.addLayoutClickListener(rawClickListener);
                    rawDataListLayout.addComponent(rowLayout);
                    indexer++;
                }
        );

        UI.getCurrent().accessSynchronously(() -> {

            updateMgfFilesTable();
            updateRawFilesTable();
            updateMzMLFilesTable();
            if (mgf_raw_dataUploader.isBusy() && !spectrumFileUploaderBusy && numberChanged) {
                mgf_raw_dataUploader.setBusy(spectrumFileUploaderBusy);
                UI.getCurrent().push();
                numberChanged = false;
            }

        });
    }

    /**
     * Run Online Peptide-Shaker work-flow
     *
     * @param projectName           name of the project to store
     * @param fastaFileId           FASTA file dataset id
     * @param searchParameterFileId .par file id
     * @param inputIdsList          list of MGF file dataset ids
     * @param searchEnginesList     List of selected search engine names
     * @param searchParameters      searching parameters // * @param searchEngines
     *                              search engines
     * @param quant                 the dataset is quantification dataset
     */
    public abstract void executeWorkFlow(String projectName, String fastaFileId, String searchParameterFileId, Set<String> inputIdsList, Set<String> searchEnginesList, IdentificationParameters searchParameters, boolean quant);

    /**
     * Validate and save search setting .par file on galaxy server for future
     * use.
     *
     * @param searchParameters selected search parameters
     * @param isNew            create new file or edit exist file
     */
    private void checkAndSaveSearchSettingsFile(IdentificationParameters searchParameters, boolean isNew) {
        this._searchParameters = searchParameters;
        _searchSettingsMap = saveSearchGUIParameters(searchParameters, isNew);
        Map<String, String> searchSettingsFileIdToNameMap = new LinkedHashMap<>();
        String objectId = "";
        for (String id : _searchSettingsMap.keySet()) {
            searchSettingsFileIdToNameMap.put(id, _searchSettingsMap.get(id).getGalaxyFileObject().getName().replace(".json", ""));
            objectId = id;
        }
        searchSettingsFileIdToNameMap.put("Add new", "Add new");
        _searchSettingsFileList.setItemIcon("Add new", VaadinIcons.FILE_ADD);
        _searchSettingsFileList.updateList(searchSettingsFileIdToNameMap);
        _searchSettingsFileList.setSelected(objectId);

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
     * Update selection list for FASTA files
     *
     * @param fastaFilesMap Map of FASTA files galaxy ID and FASTA Files dataset
     */
    public void updateFastaFileList(Map<String, GalaxyFileObject> fastaFilesMap) {

        fastaFileUploaderBusy = false;
        fastaFileIdToNameMap = new LinkedHashMap<>();
        String selectedId = "";
        for (String id : fastaFilesMap.keySet()) {
            fastaFileIdToNameMap.put(id, fastaFilesMap.get(id).getName());
            if (selectedId.equals("")) {
                selectedId = id;
            }
        }
        this._fastaFileInputLayout.updateList(fastaFileIdToNameMap);
        this._fastaFileInputLayout.setSelected(selectedId);
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
     * Save search settings file into galaxy
     *
     * @param searchParameters searchParameters .par file
     * @param isNew            create new file or edit exist file
     * @return updated search parameters files map
     */
    public abstract Map<String, GalaxyTransferableFile> saveSearchGUIParameters(IdentificationParameters searchParameters, boolean isNew);

    /**
     * upload file into galaxy
     *
     * @param toUploadFiles files to be uploaded to galaxy
     * @return updated files map
     */
    public abstract boolean uploadToGalaxy(PluploadFile[] toUploadFiles);

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
            if (_mgfFileList.contains(raw.getId() + "")) {
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
            if (_mzMLFileList.contains(raw.getId() + "")) {
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
            if (_rawFileList.contains(raw.getId() + "")) {
                raw.addStyleName("selectedraw");
            } else {
                raw.removeStyleName("selectedraw");
            }
        }
    }

    /**
     * Reset all input form to default
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
        if (_projectNameField.getValue().matches("^((?=[A-Za-z0-9_])(?![åäö\\\\-]).)*$")) {
            _projectNameField.removeStyleName("errorstyle");
            valid = true;

        } else {
            _projectNameField.addStyleName("errorstyle");
            Notification.show("Please use alphabets, numbers and '_' only ", Notification.Type.TRAY_NOTIFICATION);
        }
        if (valid && datasetNames.contains(_projectNameField.getValue().trim().toLowerCase())) {
            valid = false;
            _projectNameField.addStyleName("errorstyle");
            Notification.show("Dataset name exist, please use different name", Notification.Type.TRAY_NOTIFICATION);


        }

        return valid;
    }

}
