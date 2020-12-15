package com.uib.web.peptideshaker.ui.components;

import com.compomics.util.experiment.biology.enzymes.Enzyme;
import com.compomics.util.experiment.biology.enzymes.EnzymeFactory;
import com.compomics.util.experiment.biology.modifications.ModificationFactory;
import com.compomics.util.parameters.identification.IdentificationParameters;
import com.compomics.util.parameters.identification.search.DigestionParameters;
import com.compomics.util.parameters.identification.search.SearchParameters;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.ui.components.items.MultiSelectOptionGroup;
import com.uib.web.peptideshaker.ui.views.modal.PopupWindow;
import com.uib.web.peptideshaker.presenter.core.form.*;
import com.uib.web.peptideshaker.presenter.layouts.AdvancedSearchEnginesSettings;
import com.uib.web.peptideshaker.presenter.layouts.AdvancedSearchSettings;
import com.vaadin.data.Property;
import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;

//need to be documeneted
/**
 * This class represents the search settings input form layout
 *
 * @author Yehia Farag
 */
public abstract class SearchParametersForm extends VerticalLayout {

    /**
     * The enzyme factory.
     */
    private final EnzymeFactory enzymeFactory = EnzymeFactory.getInstance();
    /**
     * Convenience array for ion type selection.
     */
    private final List<String> ions = new ArrayList(Arrays.asList(new String[]{"a", "b", "c", "x", "y", "z"}));
    /**
     * The post translational modifications factory.
     */
    private final ModificationFactory PTM = ModificationFactory.getInstance();
    /**
     * The common modifications list.
     */
    private final Set<String> commonModificationIds;
    /**
     * The modification items that is used for initialise modifications tables.
     */
    private final Map<Object, Object[]> completeModificationItems = new LinkedHashMap<>();
    private final Map<String, String> updatedModiList = new HashMap<>();
    private final MultiSelectOptionGroup createDecoyDatabaseOptionList;
    /**
     * Search parameter file name input field.
     */
    private final HorizontalLabelTextField searchParametersFileNameInputField;
    private final HorizontalLayout searchsettingsContainer;
    /**
     * Coordinate view of sub panels in mobile and small screen mode.
     */
    private final LayoutEvents.LayoutClickListener viewCoordinatorListener;
    /**
     * Modification container layout.
     */
    private final PopupWindow modificationContainer;
    private final VerticalLayout modificationLabelsContainer;
    private final VerticalLayout proteaseFragmentationLabelsContainer;
    /**
     * Protease fragmentation container layout.
     */
    private final PopupWindow proteaseFragmentationContainer;
    private final DecimalFormat df = new DecimalFormat("0.00E00");//new DecimalFormat("#.##");
    private final Label titleLabel;
    private final Button modificationsBtn;
    private final Button proteaseFragmentationBtn;
    private final Button saveBtn;
    private final boolean editable;
    /**
     * The selected fixed modifications table.
     */
    private Table fixedModificationTable;
    /**
     * The selected variable modifications table.
     */
    private Table variableModificationTable;
    /**
     * The full modifications list table.
     */
    private Table allModificationsTable;
    /**
     * The most selected modifications list table.
     */
    private Table mostUsedModificationsTable;
    /**
     * The add to fixed modification table button.
     */
    private Button addToFixedModificationTableBtn;
    /**
     * The add to fixed modification table button.
     */
    private Button addToVariableModificationTableBtn;
    /**
     * The remove from fixed modification table button.
     */
    private Button removeFromVariableModificationTableBtn;
    /**
     * The remove from fixed modification table button.
     */
    private Button removeFromFixedModificationTableBtn;
    /**
     * Selected parameter file .par file galaxy id.
     */
    private String parameterFileId;
    /**
     * Protein digestion options list.
     */
    private HorizontalLabelDropDownList digestionList;
    /**
     * Protein enzymes options list.
     */
    private HorizontalLabelDropDownList enzymeList;
    /**
     * Protein digestion specificity options list.
     */
    private HorizontalLabelDropDownList specificityList;
    /**
     * Maximum number of miss cleavages input field.
     */
    private HorizontalLabelTextField maxMissCleavages;
    /**
     * Fragment ions types selection (forward and rewind) input selection.
     */
    private HorizontalLabel2DropdownList fragmentIonTypes;
    /**
     * Precursor m/z Tolerance value and type input selection.
     */
    private HorizontalLabelTextFieldDropdownList precursorTolerance;
    /**
     * Fragment m/z Tolerance value and type input selection.
     */
    private HorizontalLabelTextFieldDropdownList fragmentTolerance;
    /**
     * Precursor Charge (minimum to maximum) value input fields.
     */
    private HorizontalLabel2TextField precursorCharge;
    /**
     * Isotopes (minimum to maximum) value input fields.
     */
    private HorizontalLabel2TextField isotopes;
    /**
     * Search Parameters object that is used to initialise parameter file
     * (.par).
     */
    private IdentificationParameters searchParameters;
    /**
     * Search Parameters file (.par) is new or modified.
     */
    private boolean isNew = true;
    private Label proteaseFragmentationLabels;
    private String enzyme;
    /**
     * Advanced search settings layout
     */
    private AdvancedSearchSettings _advSearchSettings;
    /**
     * Advanced search engines settings layout
     */
    private AdvancedSearchEnginesSettings _advSearchEnginesSettings;

    /**
     * Constructor to initialise the main setting parameters.
     *
     * @param editable editable layout
     */
    public SearchParametersForm(boolean editable) {
        this.editable = editable;
        SearchParametersForm.this.setMargin(true);
        SearchParametersForm.this.setHeightUndefined();
        SearchParametersForm.this.setWidth(630, Unit.PIXELS);
        SearchParametersForm.this.setSpacing(true);
        VerticalLayout upperPanel = new VerticalLayout();
        upperPanel.setWidth(100, Unit.PERCENTAGE);
        upperPanel.addStyleName("subpanelframe");
        SearchParametersForm.this.addComponent(upperPanel);
        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setSizeFull();
        titleLayout.setHeight(40, Unit.PIXELS);
        
        this.commonModificationIds = new HashSet<>();
        String mod = "Acetylation of K//Acetylation of protein N-term//Carbamidomethylation of C//Oxidation of M//Phosphorylation of S//Phosphorylation of T//Phosphorylation of Y//Arginine 13C6//Lysine 13C6//iTRAQ 4-plex of peptide N-term//iTRAQ 4-plex of K//iTRAQ 4-plex of Y//iTRAQ 8-plex of peptide N-term//iTRAQ 8-plex of K//iTRAQ 8-plex of Y//TMT 6-plex of peptide N-term//TMT 6-plex of K//TMT 10-plex of peptide N-term//TMT 10-plex of K//Pyrolidone from E//Pyrolidone from Q//Pyrolidone from carbamidomethylated C//Deamidation of N//Deamidation of Q";
        commonModificationIds.addAll(Arrays.asList(mod.split("//")));
        upperPanel.addComponent(titleLayout);
        titleLabel = new Label("Search Settings", ContentMode.HTML);
        titleLabel.addStyleName(ValoTheme.LABEL_BOLD);
        titleLayout.addComponent(titleLabel);
        titleLayout.setExpandRatio(titleLabel, 20);

//        PopupWindow advancedSearchOption = new PopupWindow("(Advanced Settings)");
//        advancedSearchOption.setContent(initAdvancedSearchOption());
//        advancedSearchOption.setDescription("Not supported yet!");
//        advancedSearchOption.setSizeFull();
//        advancedSearchOption.addStyleName("centerwindow");
//        titleLayout.addComponent(advancedSearchOption);
//        titleLayout.setComponentAlignment(advancedSearchOption, Alignment.MIDDLE_LEFT);
//        titleLayout.setExpandRatio(advancedSearchOption, 80);
//        advancedSearchOption.setEnabled(false);
        Button closeIconBtn = new Button("Close");
        closeIconBtn.setIcon(VaadinIcons.CLOSE_SMALL, "Close window");
        closeIconBtn.setStyleName(ValoTheme.BUTTON_SMALL);
        closeIconBtn.addStyleName(ValoTheme.BUTTON_TINY);
        closeIconBtn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        closeIconBtn.addStyleName("centerbackground");
        closeIconBtn.setHeight(25, Unit.PIXELS);
        closeIconBtn.setWidth(25, Unit.PIXELS);
        
        closeIconBtn.addClickListener((Button.ClickEvent event) -> {
            cancel();
        });
        titleLayout.addComponent(closeIconBtn);
        titleLayout.setComponentAlignment(closeIconBtn, Alignment.TOP_RIGHT);
        
        searchsettingsContainer = new HorizontalLayout();
        searchsettingsContainer.setWidth(100, Unit.PERCENTAGE);
        searchsettingsContainer.setHeight(25, Unit.PIXELS);
        searchsettingsContainer.setSpacing(true);
        searchParametersFileNameInputField = new HorizontalLabelTextField("<b>Search Settings Name</b>", "Search Settings Name", null);
        searchParametersFileNameInputField.setWidth(100, Unit.PERCENTAGE);
        searchParametersFileNameInputField.updateExpandingRatio(0.271f, 0.525f);
        searchParametersFileNameInputField.setRequired(false);
        searchParametersFileNameInputField.addTextChangeListener((FieldEvents.TextChangeEvent event) -> {
            searchParameters.setName(searchParametersFileNameInputField.getSelectedValue());
        });
        VerticalLayout spacer = new VerticalLayout();
        searchParametersFileNameInputField.addComponent(spacer);
        searchParametersFileNameInputField.setExpandRatio(spacer, 0.204f);
        searchsettingsContainer.addComponent(searchParametersFileNameInputField);
        
        upperPanel.addComponent(searchsettingsContainer);
        HorizontalLayout protDatabaseContainer = new HorizontalLayout();
        protDatabaseContainer.setHeight(40, Unit.PIXELS);
        protDatabaseContainer.setWidth(100, Unit.PERCENTAGE);
        
        createDecoyDatabaseOptionList = new MultiSelectOptionGroup(null, false);
        protDatabaseContainer.addComponent(createDecoyDatabaseOptionList);
        protDatabaseContainer.setComponentAlignment(createDecoyDatabaseOptionList, Alignment.TOP_LEFT);
        createDecoyDatabaseOptionList.addStyleName("nopaddingmiddlealign");
        protDatabaseContainer.setExpandRatio(createDecoyDatabaseOptionList, 0.18f);
        Map<String, String> paramMap = new LinkedHashMap<>();
        paramMap.put("create_decoy", "Add Decoy");
        createDecoyDatabaseOptionList.updateList(paramMap);
        createDecoyDatabaseOptionList.setSelectedValue("create_decoy");
        createDecoyDatabaseOptionList.setViewList(true);
        
        modificationsBtn = new Button("Modifications");
        modificationsBtn.setIcon(VaadinIcons.PENCIL);
        modificationsBtn.setStyleName(ValoTheme.BUTTON_LINK);
        modificationsBtn.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        upperPanel.addComponent(modificationsBtn);
        
        modificationLabelsContainer = new VerticalLayout();
        modificationLabelsContainer.setWidth(100, Unit.PERCENTAGE);
        modificationLabelsContainer.setHeightUndefined();
        modificationLabelsContainer.setSpacing(false);
        modificationLabelsContainer.setMargin(new MarginInfo(false, false, false, true));
        upperPanel.addComponent(modificationLabelsContainer);
        modificationContainer = inititModificationLayout();
        modificationsBtn.addClickListener((Button.ClickEvent event) -> {
            if (editable) {
                return;
            }
            modificationContainer.setPopupVisible(true);
        });
        
        proteaseFragmentationBtn = new Button("Protease & Fragmentation");
        proteaseFragmentationBtn.setIcon(VaadinIcons.PENCIL);
        proteaseFragmentationBtn.setStyleName(ValoTheme.BUTTON_LINK);
        proteaseFragmentationBtn.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        upperPanel.addComponent(proteaseFragmentationBtn);
        
        proteaseFragmentationLabelsContainer = new VerticalLayout();
        proteaseFragmentationLabelsContainer.setWidth(100, Unit.PERCENTAGE);
        proteaseFragmentationLabelsContainer.setHeightUndefined();
        proteaseFragmentationLabelsContainer.setSpacing(false);
        proteaseFragmentationLabelsContainer.setMargin(new MarginInfo(false, false, false, true));
        upperPanel.addComponent(proteaseFragmentationLabelsContainer);
        
        _advSearchSettings = new AdvancedSearchSettings();
//        if (editable) {
        upperPanel.addComponent(_advSearchSettings);
        _advSearchSettings.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
            _advSearchSettings.updateAdvancedSearchParamForms(searchParameters);
        });
        
        _advSearchEnginesSettings = new AdvancedSearchEnginesSettings();
        upperPanel.addComponent(_advSearchEnginesSettings);
        _advSearchEnginesSettings.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
            _advSearchEnginesSettings.updateAdvancedSearchParamForms(searchParameters);
        });
        proteaseFragmentationContainer = inititProteaseFragmentationLayout();
        proteaseFragmentationBtn.addClickListener((Button.ClickEvent event) -> {
            if (editable) {
                return;
            }
            proteaseFragmentationContainer.setPopupVisible(true);
        });

//        HorizontalLayout actionButtonsLayout = new HorizontalLayout();
//        actionButtonsLayout.setSizeFull();
//        actionButtonsLayout.addStyleName("subpanelframe");
//        SearchParametersForm.this.addComponent(actionButtonsLayout);
//        HorizontalLayout btnsContainer = new HorizontalLayout();
//        btnsContainer.setHeight(25, Unit.PIXELS);
//        btnsContainer.setWidth(55, Unit.PERCENTAGE);
//        actionButtonsLayout.addComponent(btnsContainer);
//        actionButtonsLayout.setComponentAlignment(btnsContainer, Alignment.MIDDLE_CENTER);
        saveBtn = new Button("Save");
        saveBtn.setIcon(FontAwesome.SAVE);
        saveBtn.setStyleName(ValoTheme.BUTTON_SMALL);
        saveBtn.addStyleName(ValoTheme.BUTTON_TINY);
        saveBtn.addStyleName("bordertopseparator");
        saveBtn.setHeight(20, Unit.PIXELS);
        saveBtn.setWidth(40, Unit.PERCENTAGE);
        saveBtn.addClickListener((Button.ClickEvent event) -> {
            if (this.isValidForm()) {
                if (this.isModifiedForm()) {
                    searchParameters.setName(searchParametersFileNameInputField.getSelectedValue());
                    saveSearchingFile(searchParameters, isNew);
                } else {
                    cancel();
                }
            }
        });
        upperPanel.addComponent(saveBtn);
        upperPanel.setComponentAlignment(saveBtn, Alignment.TOP_CENTER);
//        Button closeBtn = new Button("Close");
//        closeBtn.setStyleName(ValoTheme.BUTTON_SMALL);
//        closeBtn.addStyleName(ValoTheme.BUTTON_TINY);
//        closeBtn.setHeight(25, Unit.PIXELS);
//        closeBtn.setWidth(100, Unit.PERCENTAGE);
//        closeBtn.addClickListener((Button.ClickEvent event) -> {
//            cancel();
//        });
//        btnsContainer.addComponent(closeBtn);

        this.viewCoordinatorListener = (LayoutEvents.LayoutClickEvent event) -> {
            Component comp = event.getClickedComponent();
            if ((comp instanceof Label)) {
                Label modificationLabel = (Label) ((VerticalLayout) modificationContainer.getComponent(0)).getComponent(0);
                Label protFragLabel = (Label) (((GridLayout) proteaseFragmentationContainer.getContent()).getComponent(0, 0));
                Label l = (Label) comp;
                if (l.getData() != null) {
                    Integer i = new Integer(l.getData().toString());
                    if (i == 1) { //action on top label
                        if (l.getValue().contains(VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml())) { //hide top and show bottom
                            modificationLabel.setValue(modificationLabel.getValue().replace(VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml(), VaadinIcons.ANGLE_DOUBLE_RIGHT.getHtml()));
                            protFragLabel.setValue(protFragLabel.getValue().replace(VaadinIcons.ANGLE_DOUBLE_RIGHT.getHtml(), VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml()));
                            modificationContainer.addStyleName("minimizelayout");
                            proteaseFragmentationContainer.removeStyleName("minimizelayout");
                        } else { //show top and hide bottom
                            protFragLabel.setValue(protFragLabel.getValue().replace(VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml(), VaadinIcons.ANGLE_DOUBLE_RIGHT.getHtml()));
                            modificationLabel.setValue(modificationLabel.getValue().replace(VaadinIcons.ANGLE_DOUBLE_RIGHT.getHtml(), VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml()));
                            modificationContainer.removeStyleName("minimizelayout");
                            proteaseFragmentationContainer.addStyleName("minimizelayout");
                        }
                    } else {//action on bottom label                        
                        if (l.getValue().contains(VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml())) { //hide bottom and show top
                            protFragLabel.setValue(protFragLabel.getValue().replace(VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml(), VaadinIcons.ANGLE_DOUBLE_RIGHT.getHtml()));
                            modificationLabel.setValue(modificationLabel.getValue().replace(VaadinIcons.ANGLE_DOUBLE_RIGHT.getHtml(), VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml()));
                            modificationContainer.removeStyleName("minimizelayout");
                            proteaseFragmentationContainer.addStyleName("minimizelayout");
                        } else { //show bottom and hide top
                            modificationLabel.setValue(modificationLabel.getValue().replace(VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml(), VaadinIcons.ANGLE_DOUBLE_RIGHT.getHtml()));
                            protFragLabel.setValue(protFragLabel.getValue().replace(VaadinIcons.ANGLE_DOUBLE_RIGHT.getHtml(), VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml()));
                            modificationContainer.addStyleName("minimizelayout");
                            proteaseFragmentationContainer.removeStyleName("minimizelayout");
                        }
                    }
                }
            }
        };
        
    }

    /**
     * Constructor to initialise the main setting parameters.
     *
     * @param dataset PeptideShakerVisulization dataset object
     * @param visibleByDefault the layout will be visible by default(for paining
     * error handling)
     */
    public SearchParametersForm(PeptideShakerVisualizationDataset dataset, boolean visibleByDefault) {
        this(true);
        if (!dataset.getStatus().equalsIgnoreCase("ok")) {
            return;
        }
        dataset.getFixedModification();
        dataset.getVariableModification();
        SearchParametersForm.this.setVisible(visibleByDefault);
        SearchParametersForm.this.addStyleName("dsoverview");
        SearchParametersForm.this.updateForms(dataset.getSearchingParameters());
        titleLabel.setValue(dataset.getName() + (" <i style='color: gray;font-size: 12px;'>(" + dataset.getCreateTime() + ")</i>").replace("(null)", ""));
        searchsettingsContainer.setVisible(false);

//        fastaFileList.setEnabled(false);
        if (createDecoyDatabaseOptionList.getSelectedValue() != null && !createDecoyDatabaseOptionList.getSelectedValue().isEmpty()) {
            createDecoyDatabaseOptionList.setEnabled(false);
        } else {
            createDecoyDatabaseOptionList.setVisible(false);
        }
        modificationsBtn.setResponsive(false);
        proteaseFragmentationBtn.setResponsive(false);
        saveBtn.setVisible(false);
        
        dataset.getFixedModification();
        dataset.getVariableModification();
        _advSearchSettings.setVisible(false);
        _advSearchEnginesSettings.setVisible(false);
        
    }

    /**
     * Initialise the layout for search advanced setting
     *
     * @return initialised advanced search setting layout
     * @todo: to be implemented
     */
    private VerticalLayout initAdvancedSearchOption() {
        return new VerticalLayout();
        
    }
    
    public String getEnzyme() {
        return enzyme;
    }
    
    public Map<String, String> getUpdatedModiList() {
        return updatedModiList;
    }

    /**
     * Initialise the modifications layout
     *
     * @return initialised modification layout
     */
    private PopupWindow inititModificationLayout() {
        PopupWindow modificationWindow = new PopupWindow("Edit Modifications") {
            @Override
            public void onClosePopup() {
                updateSearchingFile();
            }
            
        };
        modificationWindow.setClosable(true);
        
        HorizontalLayout popupModificationContainer = new HorizontalLayout();
        popupModificationContainer.setStyleName("panelframe");
        popupModificationContainer.setMargin(new MarginInfo(true, true, true, true));
        popupModificationContainer.setWidth(700, Unit.PIXELS);
        popupModificationContainer.setHeight(500, Unit.PIXELS);
        
        VerticalLayout leftSideLayout = new VerticalLayout();
        leftSideLayout.setSizeFull();
        leftSideLayout.setSpacing(true);
        leftSideLayout.setMargin(new MarginInfo(false, false, false, false));
        popupModificationContainer.addComponent(leftSideLayout);
        popupModificationContainer.setExpandRatio(leftSideLayout, 45);
        
        Label modificationLabel = new Label("Edit  Modifications", ContentMode.HTML);
        modificationLabel.setSizeFull();
        leftSideLayout.addComponent(modificationLabel);
        leftSideLayout.setExpandRatio(modificationLabel, 4);
        modificationLabel.setData("1");
        
        HorizontalLayout leftTopLayout = new HorizontalLayout();
        leftTopLayout.setSizeFull();
        leftTopLayout.setSpacing(true);
        leftSideLayout.addComponent(leftTopLayout);
        leftSideLayout.setExpandRatio(leftTopLayout, 48);
        
        fixedModificationTable = initModificationTable("Fixed Modifications");
        leftTopLayout.addComponent(fixedModificationTable);
        HorizontalLayout leftBottomLayout = new HorizontalLayout();
        leftBottomLayout.setSizeFull();
        leftBottomLayout.setSpacing(true);
        leftSideLayout.addComponent(leftBottomLayout);
        leftSideLayout.setExpandRatio(leftBottomLayout, 48);
        variableModificationTable = initModificationTable("Variable Modifications");
        leftBottomLayout.addComponent(variableModificationTable);
        leftBottomLayout.setExpandRatio(variableModificationTable, 80);
        
        VerticalLayout middleSideLayout = new VerticalLayout();
        middleSideLayout.setSizeFull();
        popupModificationContainer.addComponent(middleSideLayout);
        popupModificationContainer.setExpandRatio(middleSideLayout, 10);
        
        VerticalLayout spacer = new VerticalLayout();
        spacer.setSizeFull();
        middleSideLayout.addComponent(spacer);
        middleSideLayout.setExpandRatio(spacer, 4);
        
        VerticalLayout sideTopButtons = new VerticalLayout();
        sideTopButtons.setSizeUndefined();
        middleSideLayout.addComponent(sideTopButtons);
        middleSideLayout.setComponentAlignment(sideTopButtons, Alignment.MIDDLE_CENTER);
        middleSideLayout.setExpandRatio(sideTopButtons, 48);
        
        addToFixedModificationTableBtn = new Button(VaadinIcons.ARROW_LEFT);
        addToFixedModificationTableBtn.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        sideTopButtons.addComponent(addToFixedModificationTableBtn);
        sideTopButtons.setComponentAlignment(addToFixedModificationTableBtn, Alignment.TOP_CENTER);
        addToFixedModificationTableBtn.addStyleName("arrowbtn");
        
        removeFromFixedModificationTableBtn = new Button(VaadinIcons.ARROW_RIGHT);
        removeFromFixedModificationTableBtn.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        removeFromFixedModificationTableBtn.addStyleName("arrowbtn");
        sideTopButtons.addComponent(removeFromFixedModificationTableBtn);
        sideTopButtons.setComponentAlignment(removeFromFixedModificationTableBtn, Alignment.BOTTOM_CENTER);
        
        VerticalLayout sideBottomButtons = new VerticalLayout();
        middleSideLayout.addComponent(sideBottomButtons);
        middleSideLayout.setComponentAlignment(sideBottomButtons, Alignment.MIDDLE_CENTER);
        middleSideLayout.setExpandRatio(sideBottomButtons, 48);
        
        addToVariableModificationTableBtn = new Button(VaadinIcons.ARROW_LEFT);
        addToVariableModificationTableBtn.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        addToVariableModificationTableBtn.addStyleName("arrowbtn");
        sideBottomButtons.addComponent(addToVariableModificationTableBtn);
        sideBottomButtons.setComponentAlignment(addToVariableModificationTableBtn, Alignment.TOP_CENTER);
        
        removeFromVariableModificationTableBtn = new Button(VaadinIcons.ARROW_RIGHT);
        removeFromVariableModificationTableBtn.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        removeFromVariableModificationTableBtn.addStyleName("arrowbtn");
        sideBottomButtons.addComponent(removeFromVariableModificationTableBtn);
        sideBottomButtons.setComponentAlignment(removeFromVariableModificationTableBtn, Alignment.BOTTOM_CENTER);
        
        VerticalLayout rightSideLayout = new VerticalLayout();
        rightSideLayout.setSizeFull();
        rightSideLayout.setSpacing(true);
        popupModificationContainer.addComponent(rightSideLayout);
        popupModificationContainer.setExpandRatio(rightSideLayout, 45);
        
        ComboBox modificationListControl = new ComboBox();
        modificationListControl.setTextInputAllowed(false);
        modificationListControl.setWidth(100, Unit.PERCENTAGE);
        modificationListControl.setHeight(30, Unit.PIXELS);
        modificationListControl.setStyleName(ValoTheme.COMBOBOX_SMALL);
        modificationListControl.addStyleName(ValoTheme.COMBOBOX_TINY);
        modificationListControl.setNullSelectionAllowed(false);
        modificationListControl.addItem("Most Used Modifications");
        modificationListControl.addItem("All Modifications");
        modificationListControl.setValue("Most Used Modifications");
        rightSideLayout.addComponent(modificationListControl);
        rightSideLayout.setExpandRatio(modificationListControl, 4);
        rightSideLayout.setComponentAlignment(modificationListControl, Alignment.MIDDLE_CENTER);
        
        mostUsedModificationsTable = initModificationTable("");
        
        List<String> allModiList = PTM.getDefaultModifications();
        // get the min and max values for the mass sparklines
        double maxMass = (-1.0 * Double.MAX_VALUE);
        double minMass = Double.MAX_VALUE;
        
        for (String ptm : PTM.getModifications()) {
            if (PTM.getModification(ptm).getMass() > maxMass) {
                maxMass = PTM.getModification(ptm).getMass();
            }
            if (PTM.getModification(ptm).getMass() < minMass) {
                minMass = PTM.getModification(ptm).getMass();
            }
        }
        updatedModiList.clear();
        
        for (int x = 0; x < allModiList.size(); x++) {
            ColorLabel color = new ColorLabel(new Color(PTM.getColor(allModiList.get(x))));
            SparkLine sLine = new SparkLine(PTM.getModification(allModiList.get(x)).getMass(), minMass, maxMass);
            Object[] modificationArr = new Object[]{color, allModiList.get(x), sLine};
            String updatedId = "<font style='color:" + color.getRGBColorAsString() + ";font-size:10px !important;margin-right:5px'> " + VaadinIcons.CIRCLE.getHtml() + "</font>" + allModiList.get(x);
            updatedModiList.put(allModiList.get(x), updatedId);
            completeModificationItems.put(allModiList.get(x), modificationArr);
            
        }
        rightSideLayout.addComponent(mostUsedModificationsTable);
        completeModificationItems.keySet().stream().filter((id) -> (commonModificationIds.contains(id.toString()))).forEachOrdered((id) -> {
            mostUsedModificationsTable.addItem(completeModificationItems.get(id), id);
        });
        mostUsedModificationsTable.setCaption("(" + mostUsedModificationsTable.getItemIds().size() + ")");
        mostUsedModificationsTable.setVisible(false);
        rightSideLayout.setExpandRatio(mostUsedModificationsTable, 96);
        allModificationsTable = initModificationTable("");
        rightSideLayout.addComponent(allModificationsTable);
        rightSideLayout.setExpandRatio(allModificationsTable, 96);
        allModificationsTable.setVisible(false);
        
        Horizontal2Label fixedModificationLabel = new Horizontal2Label("<b>Fixed:</b>", "");
        fixedModificationLabel.addStyleName("breakline");
        Horizontal2Label variableModificationLabel = new Horizontal2Label("<b>Variable:</b>", "");
        variableModificationLabel.addStyleName("breakline");
        modificationLabelsContainer.addComponent(fixedModificationLabel);
        modificationLabelsContainer.addComponent(variableModificationLabel);
        modificationListControl.addValueChangeListener((Property.ValueChangeEvent event) -> {
            allModificationsTable.removeAllItems();
            mostUsedModificationsTable.removeAllItems();
            if (modificationListControl.getValue().toString().equalsIgnoreCase("All Modifications")) {
                completeModificationItems.keySet().stream().filter((id) -> !(fixedModificationTable.containsId(id) || variableModificationTable.containsId(id))).forEachOrdered((id) -> {
                    allModificationsTable.addItem(completeModificationItems.get(id), id);
                });
                allModificationsTable.setVisible(true);
                mostUsedModificationsTable.setVisible(false);
                
            } else {
                completeModificationItems.keySet().stream().filter((id) -> !(fixedModificationTable.containsId(id) || variableModificationTable.containsId(id))).filter((id) -> (commonModificationIds.contains(id.toString()))).forEachOrdered((id) -> {
                    mostUsedModificationsTable.addItem(completeModificationItems.get(id), id);
                });
                
                allModificationsTable.setVisible(false);
                mostUsedModificationsTable.setVisible(true);
                
            }
            allModificationsTable.sort(new Object[]{"name"}, new boolean[]{true});
            mostUsedModificationsTable.sort(new Object[]{"name"}, new boolean[]{true});
            mostUsedModificationsTable.setCaption("(" + mostUsedModificationsTable.getItemIds().size() + ")");
            allModificationsTable.setCaption("(" + allModificationsTable.getItemIds().size() + ")");
        });
        addToFixedModificationTableBtn.addClickListener((Button.ClickEvent event) -> {
            Table selectionTable;
            if (allModificationsTable.isVisible()) {
                selectionTable = allModificationsTable;
            } else {
                selectionTable = mostUsedModificationsTable;
            }
            Set<Object> selection = ((Set<Object>) selectionTable.getValue());
            selection.stream().map((id) -> {
                selectionTable.removeItem(id);
                return id;
            }).forEachOrdered((id) -> {
                fixedModificationTable.addItem(completeModificationItems.get(id), id);
            });
            fixedModificationTable.sort(new Object[]{"name"}, new boolean[]{true});
            fixedModificationTable.setCaption("Fixed Modifications (" + fixedModificationTable.getItemIds().size() + ")");
            Object id = modificationListControl.getValue();
            String cap = modificationListControl.getItemCaption(id).split("\\(")[0] + " (" + selectionTable.getItemIds().size() + ")";
            modificationListControl.setItemCaption(id, cap);
            String updateMod = "";
            for (String tid : fixedModificationTable.getItemIds().toString().replace("[", "").replace("]", "").split(",")) {
                updateMod = updateMod + " , " + updatedModiList.get(tid.trim());
            }
            updateMod = updateMod.replaceFirst(" , ", "");
            fixedModificationLabel.updateValue(updateMod);
            
        });
        removeFromFixedModificationTableBtn.addClickListener((Button.ClickEvent event) -> {
            Table selectionTable;
            if (allModificationsTable.isVisible()) {
                selectionTable = allModificationsTable;
            } else {
                selectionTable = mostUsedModificationsTable;
            }
            Set<Object> selection = ((Set<Object>) fixedModificationTable.getValue());
            selection.stream().map((id) -> {
                fixedModificationTable.removeItem(id);
                return id;
            }).forEachOrdered((id) -> {
                selectionTable.addItem(completeModificationItems.get(id), id);
            });
            selectionTable.sort(new Object[]{"name"}, new boolean[]{true});
            fixedModificationTable.setCaption("Fixed Modifications (" + fixedModificationTable.getItemIds().size() + ")");
//            selectionTable.setCaption("(" + selectionTable.getItemIds().size() + ")");
            Object id = modificationListControl.getValue();
            String cap = modificationListControl.getItemCaption(id).split("\\(")[0] + "(" + selectionTable.getItemIds().size() + ")";
            modificationListControl.setItemCaption(id, cap);
            String updateMod = "";
            for (String tid : fixedModificationTable.getItemIds().toString().replace("[", "").replace("]", "").split(",")) {
                updateMod = updateMod + " , " + updatedModiList.get(tid.trim());
            }
            updateMod = updateMod.replaceFirst(" , ", "");
            fixedModificationLabel.updateValue(updateMod);
        });
        addToVariableModificationTableBtn.addClickListener((Button.ClickEvent event) -> {
            Table selectionTable;
            if (allModificationsTable.isVisible()) {
                selectionTable = allModificationsTable;
            } else {
                selectionTable = mostUsedModificationsTable;
            }
            Set<Object> selection = ((Set<Object>) selectionTable.getValue());
            selection.stream().map((id) -> {
                selectionTable.removeItem(id);
                return id;
            }).forEachOrdered((id) -> {
                if (variableModificationTable.getItemIds().size() < 3) {
                    variableModificationTable.addItem(completeModificationItems.get(id), id);
                } else {
                    Notification.show("Maximum 3 Variable Modifications", Notification.Type.TRAY_NOTIFICATION);
                    return;
                }
            });
            variableModificationTable.sort(new Object[]{"name"}, new boolean[]{true});
            variableModificationTable.setCaption("Variable Modifications (" + variableModificationTable.getItemIds().size() + ")");
//            selectionTable.setCaption("(" + selectionTable.getItemIds().size() + ")");
            Object id = modificationListControl.getValue();
            String cap = modificationListControl.getItemCaption(id).split("\\(")[0] + " (" + selectionTable.getItemIds().size() + ")";
            modificationListControl.setItemCaption(id, cap);
            String updateMod = "";
            for (String tid : variableModificationTable.getItemIds().toString().replace("[", "").replace("]", "").split(",")) {
                updateMod = updateMod + " , " + updatedModiList.get(tid.trim());
            }
            updateMod = updateMod.replaceFirst(" , ", "");
            variableModificationLabel.updateValue(updateMod);
            
        });
        removeFromVariableModificationTableBtn.addClickListener((Button.ClickEvent event) -> {
            Table selectionTable;
            if (allModificationsTable.isVisible()) {
                selectionTable = allModificationsTable;
            } else {
                selectionTable = mostUsedModificationsTable;
            }
            Set<Object> selection = ((Set<Object>) variableModificationTable.getValue());
            selection.stream().map((id) -> {
                variableModificationTable.removeItem(id);
                return id;
            }).forEachOrdered((id) -> {
                selectionTable.addItem(completeModificationItems.get(id), id);
            });
            selectionTable.sort(new Object[]{"name"}, new boolean[]{true});
            variableModificationTable.setCaption("Variable Modifications (" + variableModificationTable.getItemIds().size() + ")");
//            selectionTable.setCaption("(" + selectionTable.getItemIds().size() + ")");
            Object id = modificationListControl.getValue();
            String cap = modificationListControl.getItemCaption(id).split("\\(")[0] + " (" + selectionTable.getItemIds().size() + ")";
            modificationListControl.setItemCaption(id, cap);
            String updateMod = "";
            for (String tid : variableModificationTable.getItemIds().toString().replace("[", "").replace("]", "").split(",")) {
                updateMod = updateMod + " , " + updatedModiList.get(tid.trim());
            }
            updateMod = updateMod.replaceFirst(" , ", "");
            variableModificationLabel.updateValue(updateMod);
            
        });
        mostUsedModificationsTable.setVisible(true);
        Object id = modificationListControl.getValue();
        String cap = mostUsedModificationsTable.getItemCaption(id).split("\\(")[0] + " (" + mostUsedModificationsTable.getItemIds().size() + ")";
        modificationListControl.setItemCaption(id, cap);
        modificationWindow.setContent(popupModificationContainer);
        fixedModificationLabel.updateValue(fixedModificationTable.getItemIds().toString().replace("[", "").replace("]", ""));
        variableModificationLabel.updateValue(variableModificationTable.getItemIds().toString().replace("[", "").replace("]", ""));
        return modificationWindow;
        
    }

    /**
     * Initialise the modifications tables
     *
     * @return initialised modification table
     */
    private Table initModificationTable(String cap) {
        Table modificationsTable = new Table(cap) {
            
            @Override
            protected String formatPropertyValue(Object rowId, Object colId, Property property) {
                Object v = property.getValue();
                if (v instanceof Double) {
                    return df.format(v);
                }
                return super.formatPropertyValue(rowId, colId, property);
            }
            
        };
        Set<Object> idSet = new HashSet<>();
        modificationsTable.setData(idSet);
        modificationsTable.setWidth(99, Unit.PERCENTAGE);
        modificationsTable.setHeight(99, Unit.PERCENTAGE);
        
        modificationsTable.setStyleName(ValoTheme.TABLE_SMALL);
        modificationsTable.addStyleName(ValoTheme.TABLE_COMPACT);
        modificationsTable.addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
        modificationsTable.addStyleName("smalltable");
        modificationsTable.setMultiSelect(true);
        modificationsTable.setSelectable(true);
        modificationsTable
                .addContainerProperty("color", ColorLabel.class,
                        null, "", null, Table.Align.CENTER);
        modificationsTable
                .addContainerProperty("name", String.class,
                        null, "Name", null, Table.Align.LEFT);
        modificationsTable
                .addContainerProperty("mass", SparkLine.class,
                        null, "Mass", null, Table.Align.LEFT);
        modificationsTable.setColumnExpandRatio("color", 10);
        modificationsTable.setColumnExpandRatio("name", 55);
        modificationsTable.setColumnExpandRatio("mass", 35);
        modificationsTable.sort(new Object[]{"name"}, new boolean[]{true});
        modificationsTable.setSortEnabled(false);
        modificationsTable.setItemDescriptionGenerator((Component source, Object itemId, Object propertyId) -> PTM.getModification(itemId.toString()).getHtmlTooltip());
        return modificationsTable;
    }

    /**
     * Initialise the Protease and Fragmentation layout
     *
     * @return initialised layout
     */
    private PopupWindow inititProteaseFragmentationLayout() {
        
        HorizontalLayout popupproteaseFragmentationContainer = new HorizontalLayout();
        popupproteaseFragmentationContainer.setStyleName("panelframe");
        popupproteaseFragmentationContainer.setSpacing(true);
        popupproteaseFragmentationContainer.setMargin(new MarginInfo(true, true, true, true));
        popupproteaseFragmentationContainer.setWidth(680, Unit.PIXELS);
        popupproteaseFragmentationContainer.setHeightUndefined();
        
        VerticalLayout leftSideLayout = new VerticalLayout();
        leftSideLayout.setWidth(100, Unit.PERCENTAGE);
        leftSideLayout.setHeightUndefined();
        leftSideLayout.setSpacing(true);
        leftSideLayout.setMargin(new MarginInfo(false, true, false, false));
        popupproteaseFragmentationContainer.addComponent(leftSideLayout);
        
        Label proteaseFragmentationLabel = new Label("Edit Protease & Fragmentation", ContentMode.HTML);
        proteaseFragmentationLabel.setSizeFull();
        leftSideLayout.addComponent(proteaseFragmentationLabel);
        leftSideLayout.setExpandRatio(proteaseFragmentationLabel, 4);
        proteaseFragmentationLabel.setData(2);
        leftSideLayout.setStyleName("nomargin");

//        GridLayout proteaseFragmentationContainer = new GridLayout(2, 6);
//        proteaseFragmentationContainer.setStyleName("panelframe");
//        proteaseFragmentationContainer.setColumnExpandRatio(0, 55);
//        proteaseFragmentationContainer.setColumnExpandRatio(1, 45);
//        proteaseFragmentationContainer.setMargin(new MarginInfo(false, false, true, false));
//        proteaseFragmentationContainer.setWidth(700, Unit.PIXELS);
//        proteaseFragmentationContainer.setHeight(205, Unit.PIXELS);
//        proteaseFragmentationContainer.setSpacing(true);
//
//        Label label = new Label(VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml() + "  Protease & Fragmentation", ContentMode.HTML);
//        label.setSizeFull();
//        label.setData(2);
//        proteaseFragmentationContainer.addComponent(label, 0, 0);
        Set<String> digestionOptionList = new LinkedHashSet<>();
        digestionOptionList.add("Enzyme");
        digestionOptionList.add("Unspecific");
        digestionOptionList.add("Whole Protein");
        
        digestionList = new HorizontalLabelDropDownList("Digestion");
        digestionList.setHeight(25, Unit.PIXELS);
        digestionList.updateData(digestionOptionList);
        digestionList.addValueChangeListener((Property.ValueChangeEvent event) -> {
            if (digestionList.getSelectedValue().equalsIgnoreCase("Enzyme")) {
                enzymeList.setEnabled(true);
                specificityList.setEnabled(true);
                maxMissCleavages.setEnabled(true);
            } else {
                maxMissCleavages.setEnabled(false);
                enzymeList.setEnabled(false);
                specificityList.setEnabled(false);
            }
            
        });
        
        enzymeList = new HorizontalLabelDropDownList("Enzyme");
        Set<String> enzList = new TreeSet<>();
        List<Enzyme> enzObjList = enzymeFactory.getEnzymes();
        enzObjList.forEach((enz) -> {
            enzList.add(enz.getName());
        });
        enzymeList.updateData(enzList);
        Set<String> specificityOptionList = new LinkedHashSet<>();
        specificityOptionList.add("Specific");
        specificityOptionList.add("Semi-Specific");
        specificityOptionList.add("N-term Specific");
        specificityOptionList.add("C-term Specific");
        
        specificityList = new HorizontalLabelDropDownList("Specificity");
        specificityList.updateData(specificityOptionList);
        maxMissCleavages = new HorizontalLabelTextField("Max Missed Cleavages", 2, new IntegerRangeValidator("Error", (-1 * Integer.MAX_VALUE), Integer.MAX_VALUE));
        
        Set<String> ionListI = new LinkedHashSet<>();
        ionListI.add("a");
        ionListI.add("b");
        ionListI.add("c");
        Set<String> ionListII = new LinkedHashSet<>();
        ionListII.add("x");
        ionListII.add("y");
        ionListII.add("z");
        fragmentIonTypes = new HorizontalLabel2DropdownList("Fragment Ion Types", ionListI, ionListII);
        
        leftSideLayout.addComponent(digestionList);
        leftSideLayout.addComponent(enzymeList);
        leftSideLayout.addComponent(specificityList);
        leftSideLayout.addComponent(maxMissCleavages);
        leftSideLayout.addComponent(fragmentIonTypes);
        
        VerticalLayout rightSideLayout = new VerticalLayout();
        rightSideLayout.setWidth(100, Unit.PERCENTAGE);
        rightSideLayout.setHeightUndefined();
        rightSideLayout.setSpacing(true);
        rightSideLayout.setMargin(new MarginInfo(true, false, false, false));
        popupproteaseFragmentationContainer.addComponent(rightSideLayout);
        rightSideLayout.setStyleName("nomargin");
        
        Set<String> mzToleranceList = new LinkedHashSet<>();
        mzToleranceList.add("ppm");
        mzToleranceList.add("Da");
        precursorTolerance = new HorizontalLabelTextFieldDropdownList("Precursor m/z Tolerance", 10.0, mzToleranceList, new DoubleRangeValidator("Error ", (-1 * Double.MAX_VALUE), Double.MAX_VALUE));
        fragmentTolerance = new HorizontalLabelTextFieldDropdownList("Fragment m/z Tolerance", 0.5, mzToleranceList, new DoubleRangeValidator("Error ", (-1 * Double.MAX_VALUE), Double.MAX_VALUE));
        precursorTolerance.setSelected("ppm");
        precursorTolerance.setTextValue(10);
        
        fragmentTolerance.setSelected("Da");
        fragmentTolerance.setTextValue(0.5);
        rightSideLayout.addComponent(precursorTolerance);
        rightSideLayout.addComponent(fragmentTolerance);
        precursorCharge = new HorizontalLabel2TextField("Precursor Charge", 2, 4, new IntegerRangeValidator("Error ", (-1 * Integer.MAX_VALUE), Integer.MAX_VALUE));
        rightSideLayout.addComponent(precursorCharge);
        isotopes = new HorizontalLabel2TextField("Isotopes", 0, 1, new IntegerRangeValidator("Error", (-1 * Integer.MAX_VALUE), Integer.MAX_VALUE));
        rightSideLayout.addComponent(isotopes);
        
        proteaseFragmentationLabels = new Label("", ContentMode.HTML);
        proteaseFragmentationLabels.setSizeFull();
        proteaseFragmentationLabels.addStyleName("breakline");
        proteaseFragmentationLabels.addStyleName("multiheaders");
        proteaseFragmentationLabelsContainer.addComponent(proteaseFragmentationLabels);
        
        PopupWindow proteaseFragmentationWindow = new PopupWindow("Edit Protease & Fragmentation") {
            @Override
            public void onClosePopup() {
                
                String value = "<center>" + digestionList.fullLabelValue() + "</center><center>" + enzymeList.fullLabelValue() + "</center><center>" + specificityList.fullLabelValue() + " </center><br/><center> " + maxMissCleavages.fullLabelValue() + "</center><center>" + fragmentIonTypes.fullLabelValue().replace("_-_", " and ") + "</center><center>" + precursorTolerance.fullLabelValue().replace("_-_", "") + " </center><br/><center> " + fragmentTolerance.fullLabelValue().replace("_-_", "") + "</center><center>" + precursorCharge.fullLabelValue().replace("_-_", "-") + "</center><center>" + isotopes.fullLabelValue().replace("_-_", "-") + "</center>";
                proteaseFragmentationLabels.setValue(value);
                updateSearchingFile();
                
            }
            
        };
        
        proteaseFragmentationWindow.setClosable(true);
        
        proteaseFragmentationWindow.setContent(popupproteaseFragmentationContainer);
        return proteaseFragmentationWindow;
        
    }

    /**
     * Update search input forms based on user selection (add/edit) from search
     * files drop-down list
     *
     * @param searchParameters search parameter object from selected parameter
     * file
     */
    public void updateForms(IdentificationParameters searchParameters) {
        this.searchParameters = searchParameters;
        isNew = true;
        this.parameterFileId = "New_File";
        searchParametersFileNameInputField.setSelectedValue(null);
        createDecoyDatabaseOptionList.setSelectedValue("create_decoy");
        
        if (searchParameters.getSearchParameters().getDigestionParameters() != null) {
            digestionList.setSelected(searchParameters.getSearchParameters().getDigestionParameters().getCleavageParameter().name());
            enzymeList.setSelected(searchParameters.getSearchParameters().getDigestionParameters().getEnzymes().get(0).getName());
            enzyme = enzymeList.getSelectedValue();
            specificityList.setSelected(searchParameters.getSearchParameters().getDigestionParameters().getSpecificity(enzyme).name());
            maxMissCleavages.setSelectedValue(searchParameters.getSearchParameters().getDigestionParameters().getnMissedCleavages(enzyme));
            fragmentIonTypes.setSelectedI(ions.get(searchParameters.getSearchParameters().getForwardIons().get(0)));
            fragmentIonTypes.setSelectedII(ions.get(searchParameters.getSearchParameters().getRewindIons().get(0)));
            precursorTolerance.setTextValue(searchParameters.getSearchParameters().getPrecursorAccuracy());
            precursorTolerance.setSelected(searchParameters.getSearchParameters().getPrecursorAccuracyType());
            fragmentTolerance.setTextValue(searchParameters.getSearchParameters().getFragmentIonAccuracy());
            fragmentTolerance.setSelected(searchParameters.getSearchParameters().getFragmentAccuracyType());
            precursorCharge.setFirstSelectedValue(searchParameters.getSearchParameters().getMinChargeSearched());
            precursorCharge.setSecondSelectedValue(searchParameters.getSearchParameters().getMaxChargeSearched());
            isotopes.setFirstSelectedValue(searchParameters.getSearchParameters().getMinIsotopicCorrection());
            isotopes.setSecondSelectedValue(searchParameters.getSearchParameters().getMaxIsotopicCorrection());
            mostUsedModificationsTable.removeAllItems();
            variableModificationTable.removeAllItems();
            fixedModificationTable.removeAllItems();
            completeModificationItems.keySet().stream().filter((id) -> (commonModificationIds.contains(id.toString()))).forEachOrdered((id) -> {
                mostUsedModificationsTable.addItem(completeModificationItems.get(id), id);
            });
            
            ArrayList<String> vm = searchParameters.getSearchParameters().getModificationParameters().getVariableModifications();
            mostUsedModificationsTable.setValue(vm);
            addToVariableModificationTableBtn.click();
            ArrayList<String> fm = searchParameters.getSearchParameters().getModificationParameters().getFixedModifications();
            mostUsedModificationsTable.setValue(fm);
            addToFixedModificationTableBtn.click();
            
        }

//        else {
//            variableModificationTable.removeAllItems();
//            fixedModificationTable.removeAllItems();
//            mostUsedModificationsTable.removeAllItems();
//            completeModificationItems.keySet().stream().filter((id) -> (commonModificationIds.contains(id.toString()))).forEachOrdered((id) -> {
//                mostUsedModificationsTable.addItem(completeModificationItems.get(id), id);
//            });
//            enzymeList.setSelected("Trypsin");
//            enzyme = enzymeList.getSelectedValue();
//            digestionList.setSelected("Enzyme");
//            specificityList.setSelected("Specific");
//            fragmentIonTypes.setSelectedI("b");
//            fragmentIonTypes.setSelectedII("y");
//            precursorTolerance.setSelected("ppm");
//            fragmentTolerance.setSelected("Da");
//
//        }
//
//        Set<Object> idSet = (Set<Object>) variableModificationTable.getData();
//        idSet.addAll(variableModificationTable.getItemIds());
//        variableModificationTable.setData(idSet);
//
//        Set<Object> idSet2 = (Set<Object>) fixedModificationTable.getData();
//        idSet2.addAll(fixedModificationTable.getItemIds());
//        fixedModificationTable.setData(idSet2);
//
//        mostUsedModificationsTable.setCaption("(" + mostUsedModificationsTable.getItemIds().size() + ")");
//        allModificationsTable.setCaption("(" + allModificationsTable.getItemIds().size() + ")");
//        if (((boolean) VaadinSession.getCurrent().getAttribute("mobilescreenstyle")) && !editable) {
////            modificationContainer.setHeight(270, Unit.PIXELS);
////            modificationContainer.addLayoutClickListener(viewCoordinatorListener);
////            proteaseFragmentationContainer.addLayoutClickListener(viewCoordinatorListener);
////            Label modificationLabel = (Label) ((VerticalLayout) modificationContainer.getComponent(0)).getComponent(0);
////            Label protFragLabel = (Label) (((GridLayout) proteaseFragmentationContainer.getContent()).getComponent(0, 0));
////            protFragLabel.setValue(protFragLabel.getValue().replace(VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml(), VaadinIcons.ANGLE_DOUBLE_RIGHT.getHtml()));
////            modificationLabel.setValue(modificationLabel.getValue().replace(VaadinIcons.ANGLE_DOUBLE_RIGHT.getHtml(), VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml()));
////            modificationContainer.removeStyleName("minimizelayout");
////            proteaseFragmentationContainer.addStyleName("minimizelayout");
//            System.out.println("init mobile search layout 1 --------------------------------------------------");
//        } else if (((boolean) VaadinSession.getCurrent().getAttribute("smallscreenstyle")) && !editable) {
////            modificationContainer.setHeight(270, Unit.PIXELS);
////            modificationContainer.addLayoutClickListener(viewCoordinatorListener);
////            proteaseFragmentationContainer.addLayoutClickListener(viewCoordinatorListener);
////            Label modificationLabel = (Label) ((VerticalLayout) modificationContainer.getComponent(0)).getComponent(0);
////            Label protFragLabel = (Label) (((GridLayout) proteaseFragmentationContainer.getContent()).getComponent(0, 0));
////            protFragLabel.setValue(protFragLabel.getValue().replace(VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml(), VaadinIcons.ANGLE_DOUBLE_RIGHT.getHtml()));
////            modificationLabel.setValue(modificationLabel.getValue().replace(VaadinIcons.ANGLE_DOUBLE_RIGHT.getHtml(), VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml()));
////            modificationContainer.removeStyleName("minimizelayout");
////            proteaseFragmentationContainer.addStyleName("minimizelayout");
//        }
        String value = "<center>" + digestionList.fullLabelValue() + "</center><center>" + enzymeList.fullLabelValue() + "</center><center>" + specificityList.fullLabelValue() + " </center><br/><center> " + maxMissCleavages.fullLabelValue() + "</center><center>" + fragmentIonTypes.fullLabelValue().replace("_-_", " and ") + "</center><center>" + precursorTolerance.fullLabelValue().replace("_-_", "") + " </center><br/><center> " + fragmentTolerance.fullLabelValue().replace("_-_", "") + "</center><center>" + precursorCharge.fullLabelValue().replace("_-_", "-") + "</center><center>" + isotopes.fullLabelValue().replace("_-_", "-") + "</center>";
        proteaseFragmentationLabels.setValue(value);
        _advSearchSettings.updateAdvancedSearchParamForms(searchParameters);
        _advSearchEnginesSettings.updateAdvancedSearchParamForms(searchParameters);
    }

    /**
     * Validate input forms before save the results on Galaxy server
     *
     * @return is valid data user inputs
     */
    public boolean isValidForm() {
        return (digestionList.isValid())
                && enzymeList.isValid()
                && specificityList.isValid()
                && maxMissCleavages.isValid()
                && fragmentIonTypes.isValid()
                && precursorTolerance.isValid()
                && fragmentTolerance.isValid()
                && precursorCharge.isValid() && isotopes.isValid() && searchParametersFileNameInputField.isValid();
    }

    /**
     * The search forms is new or just modified forms for pre exist parameter
     * file
     *
     * @return is file new or modified
     */
    public boolean isModifiedForm() {
        
        Set<Object> idSet = (Set<Object>) variableModificationTable.getData();
        boolean testVarMod = false;
        if (variableModificationTable.getItemIds().size() != idSet.size() || !variableModificationTable.getItemIds().containsAll(idSet)) {
            testVarMod = true;
        }
        
        Set<Object> idSet2 = (Set<Object>) fixedModificationTable.getData();
        boolean testFixedMod = false;
        if (fixedModificationTable.getItemIds().size() != idSet2.size() || !fixedModificationTable.getItemIds().containsAll(idSet2)) {
            testFixedMod = true;
        }
        
        return (testFixedMod || testVarMod || digestionList.isModified())
                || enzymeList.isModified()
                || specificityList.isModified()
                || maxMissCleavages.isModified()
                || fragmentIonTypes.isModified()
                || precursorTolerance.isModified()
                || fragmentTolerance.isModified()
                || precursorCharge.isModified()
                || isotopes.isModified()
                || searchParametersFileNameInputField.isModified()
                || createDecoyDatabaseOptionList.isModified();
        
    }

    /**
     * Updating search parameters object from user input selection before save
     * it into .par file and store it on Galaxy server
     *
     * @return updates search parameters object that will be use for creating
     * .par file
     */
    private void updateSearchingFile() {
        
        if (searchParameters == null) {
            return;
        }
//        PtmSettings ptmSettings = new PtmSettings();
        searchParameters.getSearchParameters().getModificationParameters().clearVariableModifications();
        searchParameters.getSearchParameters().getModificationParameters().clearFixedModifications();
        
        fixedModificationTable.getItemIds().forEach((modificationId) -> {
            searchParameters.getSearchParameters().getModificationParameters().addFixedModification(ModificationFactory.getInstance().getModification(modificationId.toString()));
        });
        variableModificationTable.getItemIds().forEach((modificationId) -> {
            searchParameters.getSearchParameters().getModificationParameters().addVariableModification(ModificationFactory.getInstance().getModification(modificationId.toString()));
        });
//        searchParameters.setPtmSettings(ptmSettings);
//        DigestionPreferences digPref = new DigestionPreferences();
        ArrayList<Enzyme> enzymes = new ArrayList<>();
        enzymes.add(enzymeFactory.getEnzyme(enzymeList.getSelectedValue()));
        searchParameters.getSearchParameters().getDigestionParameters().clearEnzymes();
        searchParameters.getSearchParameters().getDigestionParameters().getEnzymes().addAll(enzymes);
        searchParameters.getSearchParameters().getDigestionParameters().setSpecificity(enzymeList.getSelectedValue(), DigestionParameters.Specificity.valueOf(specificityList.getSelectedValue().toLowerCase()));
        searchParameters.getSearchParameters().getDigestionParameters().setnMissedCleavages(enzymeList.getSelectedValue(), Integer.valueOf(maxMissCleavages.getSelectedValue()));
        searchParameters.getSearchParameters().getDigestionParameters().setCleavageParameter(DigestionParameters.CleavageParameter.valueOf(digestionList.getSelectedValue().toLowerCase()));
        ArrayList<Integer> forwardIonsv = new ArrayList<>();
        forwardIonsv.add(ions.indexOf(fragmentIonTypes.getFirstSelectedValue()));
        searchParameters.getSearchParameters().setForwardIons(forwardIonsv);
        ArrayList<Integer> rewindIonsv = new ArrayList<>();
        rewindIonsv.add(ions.indexOf(fragmentIonTypes.getSecondSelectedValue()));
        searchParameters.getSearchParameters().setRewindIons(rewindIonsv);
        searchParameters.getSearchParameters().setPrecursorAccuracy(Double.valueOf(precursorTolerance.getFirstSelectedValue()));
        searchParameters.getSearchParameters().setPrecursorAccuracyType(SearchParameters.MassAccuracyType.valueOf(precursorTolerance.getSecondSelectedValue()));
        searchParameters.getSearchParameters().setFragmentIonAccuracy(Double.valueOf(fragmentTolerance.getFirstSelectedValue()));
        searchParameters.getSearchParameters().setFragmentAccuracyType(SearchParameters.MassAccuracyType.valueOf(fragmentTolerance.getSecondSelectedValue()));
        
        searchParameters.getSearchParameters().setMinChargeSearched(Integer.valueOf(precursorCharge.getFirstSelectedValue()));
        searchParameters.getSearchParameters().setMaxChargeSearched(Integer.valueOf(precursorCharge.getSecondSelectedValue()));
        
        searchParameters.getSearchParameters().setMinIsotopicCorrection(Integer.valueOf(isotopes.getFirstSelectedValue()));
        searchParameters.getSearchParameters().setMaxIsotopicCorrection(Integer.valueOf(isotopes.getSecondSelectedValue()));
        _advSearchSettings.updateAdvancedSearchParamForms(searchParameters);
        _advSearchEnginesSettings.updateAdvancedSearchParamForms(searchParameters);
    }

    /**
     * Get updated Search parameters object
     *
     * @return updated search parameters object
     */
    public IdentificationParameters getSearchParameters() {
        return searchParameters;
    }

    /**
     * Save user search input parameters into .par file and store it on Galaxy
     * server
     *
     * @param searchParameters updated search parameters object that is used to
     * create .par file
     * @param isNew store the search parameters object in new file or over write
     * existing .par file
     */
    public abstract void saveSearchingFile(IdentificationParameters searchParameters, boolean isNew);

    /**
     * cancel add/edit search parameters input process*
     */
    public abstract void cancel();
    
}
