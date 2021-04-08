package com.uib.web.peptideshaker.ui.components;

import com.compomics.util.experiment.biology.enzymes.Enzyme;
import com.compomics.util.experiment.biology.enzymes.EnzymeFactory;
import com.compomics.util.experiment.biology.modifications.ModificationFactory;
import com.compomics.util.parameters.identification.IdentificationParameters;
import com.compomics.util.parameters.identification.search.DigestionParameters;
import com.compomics.util.parameters.identification.search.SearchParameters;
import com.uib.web.peptideshaker.ui.components.items.ColorLabel;
import com.uib.web.peptideshaker.ui.components.items.Horizontal2Label;
import com.uib.web.peptideshaker.ui.components.items.HorizontalLabel2DropdownList;
import com.uib.web.peptideshaker.ui.components.items.HorizontalLabel2TextField;
import com.uib.web.peptideshaker.ui.components.items.HorizontalLabelDropDownList;
import com.uib.web.peptideshaker.ui.components.items.HorizontalLabelTextField;
import com.uib.web.peptideshaker.ui.components.items.HorizontalLabelTextFieldDropdownList;
import com.uib.web.peptideshaker.ui.components.items.SparkLine;
import com.uib.web.peptideshaker.ui.components.items.MultiSelectOptionGroup;
import com.uib.web.peptideshaker.ui.views.modal.PopupWindow;
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
import com.vaadin.ui.Label;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;

/**
 * This class represents the search settings input form layout
 *
 * @author Yehia Mokhtar Farag
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
        /**
         * @todo: move it to constants*
         */
        String mod = "Acetylation of K//Acetylation of protein N-term//Carbamidomethylation of C//Oxidation of M//Phosphorylation of S//Phosphorylation of T//Phosphorylation of Y//Arginine 13C6//Lysine 13C6//iTRAQ 4-plex of peptide N-term//iTRAQ 4-plex of K//iTRAQ 4-plex of Y//iTRAQ 8-plex of peptide N-term//iTRAQ 8-plex of K//iTRAQ 8-plex of Y//TMT 6-plex of peptide N-term//TMT 6-plex of K//TMT 10-plex of peptide N-term//TMT 10-plex of K//Pyrolidone from E//Pyrolidone from Q//Pyrolidone from carbamidomethylated C//Deamidation of N//Deamidation of Q";

        commonModificationIds.addAll(Arrays.asList(mod.split("//")));
        upperPanel.addComponent(titleLayout);
        titleLabel = new Label("Search Settings", ContentMode.HTML);
        titleLabel.addStyleName(ValoTheme.LABEL_BOLD);
        titleLayout.addComponent(titleLabel);
        titleLayout.setExpandRatio(titleLabel, 20);

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
            if (!editable) {
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
            if (!editable) {
                return;
            }
            proteaseFragmentationContainer.setPopupVisible(true);
        });
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
        if (!editable) {
            this.searchsettingsContainer.setVisible(false);
            modificationsBtn.setIcon(null);
            proteaseFragmentationBtn.setIcon(null);
            saveBtn.setVisible(false);
            _advSearchEnginesSettings.setVisible(false);
            _advSearchSettings.setVisible(false);
            titleLayout.setHeight(25, Unit.PIXELS);
        }

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
            if (!fixedModificationTable.getItemIds().isEmpty()) {
                for (String tid : fixedModificationTable.getItemIds().toString().replace("[", "").replace("]", "").split(",")) {
                    updateMod = updateMod + " , " + updatedModiList.get(tid.trim());
                }
                updateMod = updateMod.replaceFirst(" , ", "");
            }
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
            Object id = modificationListControl.getValue();
            String cap = modificationListControl.getItemCaption(id).split("\\(")[0] + "(" + selectionTable.getItemIds().size() + ")";
            modificationListControl.setItemCaption(id, cap);
            String updateMod = "";
            if (!fixedModificationTable.getItemIds().isEmpty()) {
                for (String tid : fixedModificationTable.getItemIds().toString().replace("[", "").replace("]", "").split(",")) {
                    updateMod = updateMod + " , " + updatedModiList.get(tid.trim());
                }
                updateMod = updateMod.replaceFirst(" , ", "");
            }
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
                }
            });
            variableModificationTable.sort(new Object[]{"name"}, new boolean[]{true});
            variableModificationTable.setCaption("Variable Modifications (" + variableModificationTable.getItemIds().size() + ")");
            Object id = modificationListControl.getValue();
            String cap = modificationListControl.getItemCaption(id).split("\\(")[0] + " (" + selectionTable.getItemIds().size() + ")";
            modificationListControl.setItemCaption(id, cap);
            String updateMod = "";
            if (!variableModificationTable.getItemIds().isEmpty()) {
                for (String tid : variableModificationTable.getItemIds().toString().replace("[", "").replace("]", "").split(",")) {
                    updateMod = updateMod + " , " + updatedModiList.get(tid.trim());
                }
                updateMod = updateMod.replaceFirst(" , ", "");
            }
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
            Object id = modificationListControl.getValue();
            String cap = modificationListControl.getItemCaption(id).split("\\(")[0] + " (" + selectionTable.getItemIds().size() + ")";
            modificationListControl.setItemCaption(id, cap);
            String updateMod = "";
            if (!variableModificationTable.getItemIds().isEmpty()) {
                for (String tid : variableModificationTable.getItemIds().toString().replace("[", "").replace("]", "").split(",")) {
                    updateMod = updateMod + " , " + updatedModiList.get(tid.trim());
                }

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
        try {
            this.searchParameters = searchParameters;
            isNew = true;
            this.parameterFileId = "New_File";
            searchParametersFileNameInputField.setSelectedValue(null);
            createDecoyDatabaseOptionList.setSelectedValue("create_decoy");
            if (searchParameters.getSearchParameters().getDigestionParameters() != null) {

                digestionList.setSelected(searchParameters.getSearchParameters().getDigestionParameters().getCleavageParameter().name());
                if (searchParameters.getSearchParameters().getDigestionParameters().getEnzymes() != null) {
                    enzymeList.setSelected(searchParameters.getSearchParameters().getDigestionParameters().getEnzymes().get(0).getName());
                    enzyme = enzymeList.getSelectedValue();
                    specificityList.setSelected(searchParameters.getSearchParameters().getDigestionParameters().getSpecificity(enzyme).name());
                    maxMissCleavages.setSelectedValue(searchParameters.getSearchParameters().getDigestionParameters().getnMissedCleavages(enzyme));
                }
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
            String value = "<center>" + digestionList.fullLabelValue() + "</center><center>" + enzymeList.fullLabelValue() + "</center><center>" + specificityList.fullLabelValue() + " </center><br/><center> " + maxMissCleavages.fullLabelValue() + "</center><center>" + fragmentIonTypes.fullLabelValue().replace("_-_", " and ") + "</center><center>" + precursorTolerance.fullLabelValue().replace("_-_", "") + " </center><br/><center> " + fragmentTolerance.fullLabelValue().replace("_-_", "") + "</center><center>" + precursorCharge.fullLabelValue().replace("_-_", "-") + "</center><center>" + isotopes.fullLabelValue().replace("_-_", "-") + "</center>";
            proteaseFragmentationLabels.setValue(value);
            _advSearchSettings.updateAdvancedSearchParamForms(searchParameters);
            _advSearchEnginesSettings.updateAdvancedSearchParamForms(searchParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        try {
            if (searchParameters == null) {
                return;
            }
            searchParameters.getSearchParameters().getModificationParameters().clearVariableModifications();
            searchParameters.getSearchParameters().getModificationParameters().clearFixedModifications();

            if (!fixedModificationTable.getItemIds().isEmpty()) {
                fixedModificationTable.getItemIds().forEach((modificationId) -> {
                    searchParameters.getSearchParameters().getModificationParameters().addFixedModification(ModificationFactory.getInstance().getModification(modificationId.toString()));
                });
            }
            if (!variableModificationTable.getItemIds().isEmpty()) {
                variableModificationTable.getItemIds().forEach((modificationId) -> {
                    searchParameters.getSearchParameters().getModificationParameters().addVariableModification(ModificationFactory.getInstance().getModification(modificationId.toString()));
                });
            }
            ArrayList<Enzyme> enzymes = new ArrayList<>();
            enzymes.add(enzymeFactory.getEnzyme(enzymeList.getSelectedValue()));
            searchParameters.getSearchParameters().getDigestionParameters().getEnzymes().clear();
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
            if (precursorTolerance.getSecondSelectedValue().equalsIgnoreCase("ppm")) {
                searchParameters.getSearchParameters().setPrecursorAccuracyType(SearchParameters.MassAccuracyType.PPM);
            } else {
                searchParameters.getSearchParameters().setPrecursorAccuracyType(SearchParameters.MassAccuracyType.DA);
            }
            searchParameters.getSearchParameters().setFragmentIonAccuracy(Double.valueOf(fragmentTolerance.getFirstSelectedValue()));
            if (fragmentTolerance.getSecondSelectedValue().equalsIgnoreCase("ppm")) {
                searchParameters.getSearchParameters().setFragmentAccuracyType(SearchParameters.MassAccuracyType.PPM);
            } else {
                searchParameters.getSearchParameters().setFragmentAccuracyType(SearchParameters.MassAccuracyType.DA);
            }

            searchParameters.getSearchParameters().setMinChargeSearched(Integer.valueOf(precursorCharge.getFirstSelectedValue()));
            searchParameters.getSearchParameters().setMaxChargeSearched(Integer.valueOf(precursorCharge.getSecondSelectedValue()));

            searchParameters.getSearchParameters().setMinIsotopicCorrection(Integer.valueOf(isotopes.getFirstSelectedValue()));
            searchParameters.getSearchParameters().setMaxIsotopicCorrection(Integer.valueOf(isotopes.getSecondSelectedValue()));
            _advSearchSettings.updateAdvancedSearchParamForms(searchParameters);
            _advSearchEnginesSettings.updateAdvancedSearchParamForms(searchParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
