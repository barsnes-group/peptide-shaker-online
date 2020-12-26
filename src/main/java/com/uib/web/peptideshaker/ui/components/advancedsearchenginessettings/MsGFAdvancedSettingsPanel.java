package com.uib.web.peptideshaker.ui.components.advancedsearchenginessettings;

import com.compomics.util.experiment.identification.Advocate;
import com.compomics.util.parameters.identification.IdentificationParameters;
import com.compomics.util.parameters.identification.tool_specific.MsgfParameters;
import com.uib.web.peptideshaker.ui.components.items.HelpPopupButton;
import com.uib.web.peptideshaker.ui.views.modal.PopupWindow;
import com.uib.web.peptideshaker.ui.components.items.HorizontalLabel2TextField;
import com.uib.web.peptideshaker.ui.components.items.HorizontalLabelDropDownList;
import com.uib.web.peptideshaker.ui.components.items.HorizontalLabelTextField;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author y-mok
 */
public class MsGFAdvancedSettingsPanel extends PopupWindow {

    private final HorizontalLabelDropDownList searchDecoyDatabase;
    private final HorizontalLabelDropDownList ms_msDetector;
    private final HorizontalLabelDropDownList fragmentaionMethod;
    private final HorizontalLabelDropDownList protocol;
    private final HorizontalLabelDropDownList enzymaticTerminal;
    private final HorizontalLabel2TextField peptideLength;
    private final HorizontalLabelTextField maxVariabalePTMperPeptide;
    private final HorizontalLabelTextField numberOfSpectrumMatches;
    private final HorizontalLabelDropDownList additionalOutput;
    private final HorizontalLabelTextField numberofTasks;

    private IdentificationParameters webSearchParameters;

    public MsGFAdvancedSettingsPanel() {
        super(VaadinIcons.COG.getHtml() + " MS-GF+ Advanced Settings");
        AbsoluteLayout container = new AbsoluteLayout();
        container.setStyleName("popuppanelmaincontainer");
        container.setWidth(500, Unit.PIXELS);
        container.setHeight(410, Unit.PIXELS);

        Label title = new Label("MS-GF+");
        container.addComponent(title, "left:10px;top:10px");
        VerticalLayout subContainer = new VerticalLayout();
        subContainer.setSizeFull();
        subContainer.setStyleName("subcontainer");
        subContainer.addStyleName("paddingvertical5");
        container.addComponent(subContainer, "left:10px;top:45px;right:10px;bottom:40px");
        MsGFAdvancedSettingsPanel.this.setContent(container);
        MsGFAdvancedSettingsPanel.this.setClosable(true);

        searchDecoyDatabase = new HorizontalLabelDropDownList("Search Decoy Database");
        subContainer.addComponent(searchDecoyDatabase);
        Set<String> values = new LinkedHashSet<>();
        values.add("Yes");
        values.add("No");
        searchDecoyDatabase.updateData(values);

        ms_msDetector = new HorizontalLabelDropDownList("MS/MS Detector");
        subContainer.addComponent(ms_msDetector);
        Set<String> values2 = new LinkedHashSet<>();
        /**
         * The MS-GF+ instrument ID: 0: Low-res LCQ/LTQ (Default), 1:
         * Orbitrap/FTICR, 2: TOF, 3: Q-Exactive.
         */
        values2.add(0 + "");
        values2.add(1 + "");
        values2.add(2 + "");
        values2.add(3 + "");
        ms_msDetector.updateData(values2);
        ms_msDetector.setItemCaption(0 + "", "Low-res LCQ/LTQ (Default)");
        ms_msDetector.setItemCaption(1 + "", "Orbitrap/FTICR");
        ms_msDetector.setItemCaption(2 + "", "TOF");
        ms_msDetector.setItemCaption(3 + "", "Q-Exactive");

        fragmentaionMethod = new HorizontalLabelDropDownList("Fragmentation Method");
        subContainer.addComponent(fragmentaionMethod);
        /**
         * The MS-GF+ fragmentation type ID: 0: As written in the spectrum or
         * CID if no info, 1: CID, 2: ETD, 3: HCD, 4: UVPD.
         */
        values2.add(4 + "");
        fragmentaionMethod.updateData(values2);
        fragmentaionMethod.setItemCaption(0 + "", "Automatic");
        fragmentaionMethod.setItemCaption(1 + "", "CID");
        fragmentaionMethod.setItemCaption(2 + "", "ETD");
        fragmentaionMethod.setItemCaption(3 + "", "HCD");
        fragmentaionMethod.setItemCaption(4 + "", "UVPD");

        protocol = new HorizontalLabelDropDownList("Protocol");
        subContainer.addComponent(protocol);
        /**
         * The MS-GF+ protocol ID: 0: Automatic, 1: Phosphorylation, 2: iTRAQ,
         * 3: iTRAQPhospho, 4: TMT, 5: Standard.
         */
        ;
        values2.add(5 + "");
        protocol.updateData(values2);
        protocol.setItemCaption(0 + "", "Automatic");
        protocol.setItemCaption(1 + "", "Phosphorylation");
        protocol.setItemCaption(2 + "", "iTRAQ");
        protocol.setItemCaption(3 + "", "iTRAQPhospho");
        protocol.setItemCaption(4 + "", "TMT");
        protocol.setItemCaption(5 + "", "Standard");

        enzymaticTerminal = new HorizontalLabelDropDownList("Enzymatic Terminals");
        subContainer.addComponent(enzymaticTerminal);
        /**
         * The number of tolerable termini. E.g. For trypsin, 0: non-tryptic, 1:
         * semi-tryptic, 2: fully-tryptic peptides only.
         */
        values2.clear();
        values2.add(0 + "");
        values2.add(1 + "");
        values2.add(2 + "");
        enzymaticTerminal.updateData(values2);
        enzymaticTerminal.setItemCaption(0 + "", "None Required");
        enzymaticTerminal.setItemCaption(1 + "", "At Least One");
        enzymaticTerminal.setItemCaption(2 + "", "Both");

        peptideLength = new HorizontalLabel2TextField("Peptide Length (min-max)", 0, 0, new IntegerRangeValidator("Only double values allowd", (-1 * Integer.MAX_VALUE), Integer.MAX_VALUE));
        peptideLength.setSpacing(true);
        subContainer.addComponent(peptideLength);

        maxVariabalePTMperPeptide = new HorizontalLabelTextField("MAx Variable PTMs per Peptid", 0, new IntegerRangeValidator("Postive integer only allowed", 0, Integer.MAX_VALUE));
        subContainer.addComponent(maxVariabalePTMperPeptide);

        numberOfSpectrumMatches = new HorizontalLabelTextField("Number of Spectrum Matches", 0, new IntegerRangeValidator("Postive integer only allowed", 0, Integer.MAX_VALUE));
        subContainer.addComponent(numberOfSpectrumMatches);
        additionalOutput = new HorizontalLabelDropDownList("Additional Output");
        subContainer.addComponent(additionalOutput);
        additionalOutput.updateData(values);
        numberofTasks = new HorizontalLabelTextField("Number of tasks", "default", new IntegerRangeValidator("Only Integer value allowed", (-1 * Integer.MAX_VALUE), Integer.MAX_VALUE));
        subContainer.addComponent(numberofTasks);

        String helpText = "<a href='https://msgfplus.github.io/msgfplus/MSGFPlus.html' target='_blank'>";
        HelpPopupButton help = new HelpPopupButton(helpText, "<font style='line-height: 20px;'>Click to open the MS-GF+ help page.</font>", 100, 20);
        container.addComponent(help, "left:10px;bottom:10px;");
        Button okBtn = new Button("OK");
        okBtn.setWidth(76, Unit.PIXELS);
        okBtn.setHeight(20, Unit.PIXELS);
        okBtn.setStyleName(ValoTheme.BUTTON_TINY);
        okBtn.addClickListener((Button.ClickEvent event) -> {
            if (peptideLength.isValid() && maxVariabalePTMperPeptide.isValid() && numberOfSpectrumMatches.isValid() && numberofTasks.isValid()) {
                setPopupVisible(false);
            }
        });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyleName(ValoTheme.BUTTON_TINY);
        cancelBtn.setWidth(76, Unit.PIXELS);
        cancelBtn.setHeight(20, Unit.PIXELS);
        container.addComponent(okBtn, "bottom:10px;right:96px");
        container.addComponent(cancelBtn, "bottom:10px;right:10px");
        cancelBtn.addClickListener((Button.ClickEvent event) -> {
            MsGFAdvancedSettingsPanel.this.setPopupVisible(false);
        });

    }

    public void updateGUI(IdentificationParameters webSearchParameters) {
        this.webSearchParameters = webSearchParameters;
        MsgfParameters msgfParameters = (MsgfParameters) webSearchParameters.getSearchParameters().getIdentificationAlgorithmParameter(Advocate.msgf.getIndex());
        if (msgfParameters.searchDecoyDatabase()) {
            searchDecoyDatabase.setSelected("Yes");
        } else {
            searchDecoyDatabase.setSelected("No");
        }

        ms_msDetector.setSelected(msgfParameters.getInstrumentID() + "");
        fragmentaionMethod.setSelected(msgfParameters.getFragmentationType() + "");
        protocol.setSelected(msgfParameters.getProtocol() + "");
        enzymaticTerminal.setSelected(msgfParameters.getNumberTolerableTermini() + "");
        peptideLength.setFirstSelectedValue(msgfParameters.getMinPeptideLength());
        peptideLength.setSecondSelectedValue(msgfParameters.getMaxPeptideLength());
        maxVariabalePTMperPeptide.setSelectedValue(msgfParameters.getNumberOfModificationsPerPeptide());

        numberOfSpectrumMatches.setSelectedValue(msgfParameters.getNumberOfSpectrumMatches());
        if (msgfParameters.isAdditionalOutput()) {
            additionalOutput.setSelected("Yes");
        } else {
            additionalOutput.setSelected("No");
        }
        numberofTasks.setSelectedValue(msgfParameters.getNumberOfTasks());

    }

    @Override
    public void onClosePopup() {
    }

    @Override
    public void setPopupVisible(boolean visible) {
        if (visible && webSearchParameters != null) {
            updateGUI(webSearchParameters);
        } else if (webSearchParameters != null) {
            updateParameters();
        }
        super.setPopupVisible(visible); //To change body of generated methods, choose Tools | Templates.
    }

    private void updateParameters() {
        MsgfParameters msgfParameters = (MsgfParameters) webSearchParameters.getSearchParameters().getIdentificationAlgorithmParameter(Advocate.msgf.getIndex());
        msgfParameters.setSearchDecoyDatabase(searchDecoyDatabase.getSelectedValue().equalsIgnoreCase("Yes"));
        msgfParameters.setInstrumentID(Integer.valueOf(ms_msDetector.getSelectedValue()));
        msgfParameters.setProtocol(Integer.valueOf(protocol.getSelectedValue()));
        msgfParameters.setFragmentationType(Integer.valueOf(fragmentaionMethod.getSelectedValue()));
        msgfParameters.setNumberTolerableTermini(Integer.valueOf(enzymaticTerminal.getSelectedValue()));
        msgfParameters.setMinPeptideLength(Integer.valueOf(peptideLength.getFirstSelectedValue()));
        msgfParameters.setMaxPeptideLength(Integer.valueOf(peptideLength.getSecondSelectedValue()));
        msgfParameters.setNumberOfModificationsPerPeptide(Integer.valueOf(maxVariabalePTMperPeptide.getSelectedValue()));
        msgfParameters.setNumberOfSpectrumMarches(Integer.valueOf(numberOfSpectrumMatches.getSelectedValue()));
        msgfParameters.setAdditionalOutput(additionalOutput.getSelectedValue().equalsIgnoreCase("Yes"));
        try {
            msgfParameters.setNumberOfTasks(Integer.valueOf(numberofTasks.getSelectedValue()));
        } catch (NumberFormatException exp) {
            msgfParameters.setNumberOfTasks(null);
        }

    }

}
