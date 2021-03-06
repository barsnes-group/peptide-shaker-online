package com.uib.web.peptideshaker.ui.components.advancedsearchenginessettings;

import com.compomics.util.experiment.identification.Advocate;
import com.compomics.util.parameters.identification.IdentificationParameters;
import com.compomics.util.parameters.identification.tool_specific.MsAmandaParameters;
import com.uib.web.peptideshaker.ui.components.items.HelpPopupButton;
import com.uib.web.peptideshaker.ui.views.modal.PopupWindow;
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
 * @author Yehia Mokhtar Farag
 */
public class MSAmandaAdvancedSettingsPanel extends PopupWindow {

    private final HorizontalLabelDropDownList generateDecoyDatabase;
    private final HorizontalLabelDropDownList fragmentIonTypes;
    private final HorizontalLabelDropDownList monoisotopicList;
    private final HorizontalLabelTextField maxRank;
    private final HorizontalLabelDropDownList performDeisotoping;
    private final HorizontalLabelDropDownList maxPtmDublicatesPerPeptide;
    private final HorizontalLabelDropDownList maxVariablePTMsPerPeptide;
    private final HorizontalLabelDropDownList maxPotintialPtmSitesPerPeptide;
    private final HorizontalLabelDropDownList maxNeutralLossesPerPeptide;
    private final HorizontalLabelDropDownList maxPtmNeutralLossesPerPeptide;
    private final HorizontalLabelTextField minPeptideLength;
    private final HorizontalLabelTextField maxProteinsLoadedIntoMemory;
    private final HorizontalLabelTextField maxSpectraLoadedIntoMemory;
    private final HorizontalLabelDropDownList outputFormat;

    private IdentificationParameters webSearchParameters;

    /**
     *
     */
    public MSAmandaAdvancedSettingsPanel() {
        super(VaadinIcons.COG.getHtml() + " MS Amanda Advanced Settings");
        AbsoluteLayout container = new AbsoluteLayout();
        container.setStyleName("popuppanelmaincontainer");
        container.setWidth(500, Unit.PIXELS);
        container.setHeight(500, Unit.PIXELS);

        Label title = new Label("MS Amandas");
        container.addComponent(title, "left:10px;top:10px");
        VerticalLayout subContainer = new VerticalLayout();
        subContainer.setSizeFull();
        subContainer.setStyleName("subcontainer");
        subContainer.addStyleName("paddingvertical5");
        container.addComponent(subContainer, "left:10px;top:45px;right:10px;bottom:40px");
        MSAmandaAdvancedSettingsPanel.this.setContent(container);
        MSAmandaAdvancedSettingsPanel.this.setClosable(true);

        generateDecoyDatabase = new HorizontalLabelDropDownList("Generate Decoy Database");
        subContainer.addComponent(generateDecoyDatabase);
        Set<String> values = new LinkedHashSet<>();
        values.add("Yes");
        values.add("No");
        generateDecoyDatabase.updateData(values);

        fragmentIonTypes = new HorizontalLabelDropDownList("Fragment Ion Types");
        subContainer.addComponent(fragmentIonTypes);
        Set<String> values2 = new LinkedHashSet<>();
        values2.add("b, y");
        values2.add("b, y, -H2O, -NH3");
        values2.add("a, b, y, -H2O, -NH3, Imm");
        values2.add("a, b, y, -H2O, -NH3");
        values2.add("a, b, y");
        values2.add("a, b, y, Imm");
        values2.add("a, b, y, z, -H2O, -NH3, Imm");
        values2.add("c, y, z+1, z+2");
        values2.add("b, c, y, z+1, z+2");
        values2.add("b, y, INT");
        values2.add("b, y, INT, Imm");
        values2.add("a, b, y, INT");
        values2.add("a, b, y, INT, IMM");
        values2.add("a, b, y, INT, IMM, -H2O");
        values2.add("a, b, y, INT, IMM, -H2O, -NH3");
        values2.add("a, b, y, INT, IMM, -NH3");

        fragmentIonTypes.updateData(values2);
        fragmentIonTypes.setItemCaption(0, "None Required");
        fragmentIonTypes.setItemCaption(1, "At Least One");
        fragmentIonTypes.setItemCaption(2, "Both");

        monoisotopicList = new HorizontalLabelDropDownList("Monoisotopic");
        subContainer.addComponent(monoisotopicList);
        monoisotopicList.updateData(values);
        maxRank = new HorizontalLabelTextField("Max Rank", 0, new IntegerRangeValidator("Postive integer only allowed", 0, Integer.MAX_VALUE));
        subContainer.addComponent(maxRank);

        performDeisotoping = new HorizontalLabelDropDownList("Perform Deisotoping");
        subContainer.addComponent(performDeisotoping);
        performDeisotoping.updateData(values);

        maxPtmDublicatesPerPeptide = new HorizontalLabelDropDownList("Max PTM Duplicates per Peptide");
        subContainer.addComponent(maxPtmDublicatesPerPeptide);
        Set<String> values3 = new LinkedHashSet<>();
        values3.add(0 + "");
        values3.add(1 + "");
        values3.add(2 + "");
        values3.add(3 + "");
        values3.add(4 + "");
        values3.add(5 + "");
        values3.add(6 + "");
        values3.add(7 + "");
        values3.add(8 + "");
        values3.add(9 + "");
        values3.add(10 + "");
        maxPtmDublicatesPerPeptide.updateData(values3);

        maxVariablePTMsPerPeptide = new HorizontalLabelDropDownList("Max Variable PTMs per Peptide");
        subContainer.addComponent(maxVariablePTMsPerPeptide);
        maxVariablePTMsPerPeptide.updateData(values3);

        maxPotintialPtmSitesPerPeptide = new HorizontalLabelDropDownList("Max Potintial PTM sites per PTM");
        subContainer.addComponent(maxPotintialPtmSitesPerPeptide);
        values3.add(11 + "");
        values3.add(12 + "");
        values3.add(13 + "");
        values3.add(14 + "");
        values3.add(15 + "");
        values3.add(16 + "");
        values3.add(17 + "");
        values3.add(18 + "");
        values3.add(19 + "");
        values3.add(20 + "");
        maxPotintialPtmSitesPerPeptide.updateData(values3);

        maxNeutralLossesPerPeptide = new HorizontalLabelDropDownList("Max Neutral Losses per Peptide");
        subContainer.addComponent(maxNeutralLossesPerPeptide);
        values3.clear();
        values3.add(0 + "");
        values3.add(1 + "");
        values3.add(2 + "");
        values3.add(3 + "");
        values3.add(4 + "");
        values3.add(5 + "");
        maxNeutralLossesPerPeptide.updateData(values3);

        maxPtmNeutralLossesPerPeptide = new HorizontalLabelDropDownList("Max PTM Neutral Losses per Peptide");
        subContainer.addComponent(maxPtmNeutralLossesPerPeptide);
        maxPtmNeutralLossesPerPeptide.updateData(values3);

        minPeptideLength = new HorizontalLabelTextField("Min Peptide Length", 0, new IntegerRangeValidator("Postive integer only allowed", 0, Integer.MAX_VALUE));
        subContainer.addComponent(minPeptideLength);

        maxProteinsLoadedIntoMemory = new HorizontalLabelTextField("Max Proteins Loaded into Memory", 0, new IntegerRangeValidator("Postive integer only allowed", 0, Integer.MAX_VALUE));
        subContainer.addComponent(maxProteinsLoadedIntoMemory);

        maxSpectraLoadedIntoMemory = new HorizontalLabelTextField("Max Spectra Loaded into Memory", 0, new IntegerRangeValidator("Postive integer only allowed", 0, Integer.MAX_VALUE));
        subContainer.addComponent(maxSpectraLoadedIntoMemory);

        outputFormat = new HorizontalLabelDropDownList("Output Format");
        subContainer.addComponent(outputFormat);
        values.clear();
        values.add("csv");
        values.add("mzIdentML");
        outputFormat.updateData(values);

        String helpText = "<a href='https://ms.imp.ac.at/?goto=msamanda' target='_blank'>";
        HelpPopupButton help = new HelpPopupButton(helpText, "Click to open the MS Amanda help page.", 100, 20);
        container.addComponent(help, "left:10px;bottom:10px;");

        Button okBtn = new Button("OK");
        okBtn.setWidth(76, Unit.PIXELS);
        okBtn.setHeight(20, Unit.PIXELS);
        okBtn.setStyleName(ValoTheme.BUTTON_TINY);
        okBtn.addClickListener((Button.ClickEvent event) -> {
            if (maxRank.isValid() && minPeptideLength.isValid() && maxProteinsLoadedIntoMemory.isValid() && maxSpectraLoadedIntoMemory.isValid()) {
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
            MSAmandaAdvancedSettingsPanel.this.setPopupVisible(false);
        });

    }

    /**
     *
     * @param webSearchParameters
     */
    public void updateGUI(IdentificationParameters webSearchParameters) {
        this.webSearchParameters = webSearchParameters;
        MsAmandaParameters oldMsAmandaParameters = (MsAmandaParameters) webSearchParameters.getSearchParameters().getIdentificationAlgorithmParameter(Advocate.msAmanda.getIndex());

        minPeptideLength.setSelectedValue(oldMsAmandaParameters.getMinPeptideLength());

        if (oldMsAmandaParameters.generateDecoy()) {
            generateDecoyDatabase.setSelected("Yes");
        } else {
            generateDecoyDatabase.setSelected("No");
        }
        fragmentIonTypes.setSelected(oldMsAmandaParameters.getInstrumentID());
        if (oldMsAmandaParameters.isMonoIsotopic()) {
            monoisotopicList.setSelected("Yes");
        } else {
            monoisotopicList.setSelected("No");
        }
        maxRank.setSelectedValue(oldMsAmandaParameters.getMaxRank());
        if (oldMsAmandaParameters.isPerformDeisotoping()) {
            performDeisotoping.setSelected("Yes");
        } else {
            performDeisotoping.setSelected("No");
        }
        maxPtmDublicatesPerPeptide.setSelected(oldMsAmandaParameters.getMaxModifications() + "");
        maxVariablePTMsPerPeptide.setSelected(oldMsAmandaParameters.getMaxVariableModifications() + "");
        maxPotintialPtmSitesPerPeptide.setSelected(oldMsAmandaParameters.getMaxModificationSites() + "");
        maxNeutralLossesPerPeptide.setSelected(oldMsAmandaParameters.getMaxNeutralLosses() + "");
        maxPtmNeutralLossesPerPeptide.setSelected(oldMsAmandaParameters.getMaxNeutralLossesPerModification() + "");
        minPeptideLength.setSelectedValue(oldMsAmandaParameters.getMinPeptideLength());
        maxProteinsLoadedIntoMemory.setSelectedValue(oldMsAmandaParameters.getMaxLoadedProteins());
        maxSpectraLoadedIntoMemory.setSelectedValue(oldMsAmandaParameters.getMaxLoadedSpectra());
        outputFormat.setSelected(oldMsAmandaParameters.getOutputFormat());

    }

    /**
     *
     */
    @Override
    public void onClosePopup() {
    }

    /**
     *
     * @param visible
     */
    @Override
    public void setPopupVisible(boolean visible) {
        if (visible && webSearchParameters != null) {
            updateGUI(webSearchParameters);
        } else if (webSearchParameters != null) {
            updateParameters();
        }
        super.setPopupVisible(visible); 
    }

    private void updateParameters() {
        MsAmandaParameters oldMsAmandaParameters = (MsAmandaParameters) webSearchParameters.getSearchParameters().getIdentificationAlgorithmParameter(Advocate.msAmanda.getIndex());
        oldMsAmandaParameters.setGenerateDecoyDatabase(generateDecoyDatabase.getSelectedValue().equalsIgnoreCase("Yes"));
        oldMsAmandaParameters.setInstrumentID(fragmentIonTypes.getSelectedValue());
        oldMsAmandaParameters.setMonoIsotopic(monoisotopicList.getSelectedValue().equalsIgnoreCase("Yes"));
        oldMsAmandaParameters.setMaxRank(Integer.valueOf(maxRank.getSelectedValue()));
        oldMsAmandaParameters.setPerformDeisotoping(performDeisotoping.getSelectedValue().equalsIgnoreCase("Yes"));
        oldMsAmandaParameters.setMaxModifications(Integer.valueOf(maxPtmDublicatesPerPeptide.getSelectedValue()));
        oldMsAmandaParameters.setMaxVariableModifications(Integer.valueOf(maxVariablePTMsPerPeptide.getSelectedValue()));
        oldMsAmandaParameters.setMaxModificationSites(Integer.valueOf(maxPotintialPtmSitesPerPeptide.getSelectedValue()));
        oldMsAmandaParameters.setMaxNeutralLosses(Integer.valueOf(maxNeutralLossesPerPeptide.getSelectedValue()));
        oldMsAmandaParameters.setMaxNeutralLossesPerModification(Integer.valueOf(maxPtmNeutralLossesPerPeptide.getSelectedValue()));
        oldMsAmandaParameters.setMinPeptideLength(Integer.valueOf(minPeptideLength.getSelectedValue().replace(",", "")));
        oldMsAmandaParameters.setMaxLoadedProteins(Integer.valueOf(maxProteinsLoadedIntoMemory.getSelectedValue().replace(",", "")));
        oldMsAmandaParameters.setMaxLoadedSpectra(Integer.valueOf(maxSpectraLoadedIntoMemory.getSelectedValue().replace(",", "")));
        oldMsAmandaParameters.setOutputFormat(outputFormat.getSelectedValue());

    }

}
