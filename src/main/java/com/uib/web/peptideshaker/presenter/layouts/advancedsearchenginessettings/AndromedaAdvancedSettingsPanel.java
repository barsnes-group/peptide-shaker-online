package com.uib.web.peptideshaker.presenter.layouts.advancedsearchenginessettings;

import com.compomics.util.experiment.identification.Advocate;
import com.compomics.util.experiment.mass_spectrometry.FragmentationMethod;
import com.compomics.util.parameters.identification.IdentificationParameters;
import com.compomics.util.parameters.identification.tool_specific.AndromedaParameters;
import com.uib.web.peptideshaker.presenter.core.Help;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabel2TextField;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabelDropDounList;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabelTextField;
import com.vaadin.data.validator.DoubleRangeValidator;
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
 *
 * @author y-mok
 */
public class AndromedaAdvancedSettingsPanel extends PopupWindow {

    private final HorizontalLabel2TextField peptideLength;
    private final HorizontalLabelTextField maxPeptideMass;
    private final HorizontalLabelTextField numberOfSpectrumMatches;
    private final HorizontalLabelTextField maxVariabalePTMs;

    private final HorizontalLabelDropDounList fragmentaionMethod;
    private final HorizontalLabelDropDounList waterLoss;
    private final HorizontalLabelDropDounList ammoniaLoss;
    private final HorizontalLabelDropDounList sequenceDependentNeutralLoss;
    private final HorizontalLabelDropDounList equalIandL;
    private final HorizontalLabelDropDounList fragmentAll;
    private final HorizontalLabelDropDounList empiricalCorrection;
    private final HorizontalLabelDropDounList higherCharge;

    private final HorizontalLabelTextField maxCombinations;
    private final HorizontalLabelTextField topPeaks;
    private final HorizontalLabelTextField topPeaksWindows;
    private final HorizontalLabelDropDounList decoyMode;

    private IdentificationParameters webSearchParameters;

    public AndromedaAdvancedSettingsPanel() {
        super(VaadinIcons.COG.getHtml() + " Andromeda Advanced Settings");
        AbsoluteLayout container = new AbsoluteLayout();
        container.setStyleName("popuppanelmaincontainer");
        container.setWidth(500, Unit.PIXELS);
        container.setHeight(500, Unit.PIXELS);

        Label title = new Label("Search Settings");
        container.addComponent(title, "left:10px;top:10px");
        VerticalLayout subContainer = new VerticalLayout();
        subContainer.setSizeFull();
        subContainer.setStyleName("subcontainer");
        container.addComponent(subContainer, "left:10px;top:40px;right:10px;bottom:40px");
        AndromedaAdvancedSettingsPanel.this.setContent(container);
        AndromedaAdvancedSettingsPanel.this.setClosable(true);

        peptideLength = new HorizontalLabel2TextField("Peptide Length No Enzyme", 0, 0, new IntegerRangeValidator("Only integer values allowd", (-1* Integer.MAX_VALUE), Integer.MAX_VALUE));
        subContainer.addComponent(peptideLength);

        maxPeptideMass = new HorizontalLabelTextField("Max Peptide Mass", 0.0, new DoubleRangeValidator("Postive double only allowed", 0.0, Double.MAX_VALUE));
        subContainer.addComponent(maxPeptideMass);

        numberOfSpectrumMatches = new HorizontalLabelTextField("Number of Spectrum Matches", 0, new IntegerRangeValidator("Postive integer only allowed", 0, Integer.MAX_VALUE));
        subContainer.addComponent(numberOfSpectrumMatches);

        maxVariabalePTMs = new HorizontalLabelTextField("Max Variable PTMs", 0, new IntegerRangeValidator("Postive integer only allowed", 0, Integer.MAX_VALUE));
        subContainer.addComponent(maxVariabalePTMs);

        fragmentaionMethod = new HorizontalLabelDropDounList("Fragmentation Method");
        subContainer.addComponent(fragmentaionMethod);
        Set<String> values = new LinkedHashSet<>();
        /**
         * The MS-GF+ instrument ID: 0: Low-res LCQ/LTQ (Default), 1:
         * Orbitrap/FTICR, 2: TOF, 3: Q-Exactive.
         */
        values.add(FragmentationMethod.CID.name());
        values.add(FragmentationMethod.HCD.name());
        values.add(FragmentationMethod.ETD.name());
        fragmentaionMethod.updateData(values);

        waterLoss = new HorizontalLabelDropDounList("Water Loss");
        subContainer.addComponent(waterLoss);
        Set<String> values2 = new LinkedHashSet<>();
        values2.add("Yes");
        values2.add("No");
        waterLoss.updateData(values2);

        ammoniaLoss = new HorizontalLabelDropDounList("Ammonia Loss");
        subContainer.addComponent(ammoniaLoss);
        ammoniaLoss.updateData(values2);

        sequenceDependentNeutralLoss = new HorizontalLabelDropDounList("Sequence Dependent Neutral Loss");
        subContainer.addComponent(sequenceDependentNeutralLoss);
        sequenceDependentNeutralLoss.updateData(values2);
        equalIandL = new HorizontalLabelDropDounList("Equal I and L");
        subContainer.addComponent(equalIandL);
        equalIandL.updateData(values2);
        fragmentAll = new HorizontalLabelDropDounList("fragmentAll");
        subContainer.addComponent(fragmentAll);
        fragmentAll.updateData(values2);

        empiricalCorrection = new HorizontalLabelDropDounList("Empirical Correction");
        subContainer.addComponent(empiricalCorrection);
        empiricalCorrection.updateData(values2);

        higherCharge = new HorizontalLabelDropDounList("Higher Charge");
        subContainer.addComponent(higherCharge);
        higherCharge.updateData(values2);

        maxCombinations = new HorizontalLabelTextField("Max Combinations", 0, new IntegerRangeValidator("Postive integer only allowed", 0, Integer.MAX_VALUE));
        subContainer.addComponent(maxCombinations);

        topPeaks = new HorizontalLabelTextField("Top Peaks", 0, new IntegerRangeValidator("Postive integer only allowed", 0, Integer.MAX_VALUE));
        subContainer.addComponent(topPeaks);
        topPeaksWindows = new HorizontalLabelTextField("Top Peaks Windows", 0, new IntegerRangeValidator("Postive integer only allowed", 0, Integer.MAX_VALUE));
        subContainer.addComponent(topPeaksWindows);

        decoyMode = new HorizontalLabelDropDounList("Decoy Mode");
        subContainer.addComponent(decoyMode);
        values2.clear();
        values2.add(AndromedaParameters.AndromedaDecoyMode.none.name());
        values2.add(AndromedaParameters.AndromedaDecoyMode.reverse.name());
        decoyMode.updateData(values2);

        String helpText = "<a href='http://coxdocs.org/doku.php?id=maxquant:andromeda:start' targe='_blank'>";
        Help help = new Help(helpText, "<font style='line-height: 20px;'>Click to open the Andromeda help page.</font>",100,20);
        container.addComponent(help, "left:20px;bottom:10px;");
        Button okBtn = new Button("OK");
        okBtn.setWidth(76, Unit.PIXELS);
        okBtn.setHeight(20, Unit.PIXELS);
        okBtn.setStyleName(ValoTheme.BUTTON_TINY);
        okBtn.addClickListener((Button.ClickEvent event) -> {
            if (peptideLength.isValid() && maxVariabalePTMs.isValid() && numberOfSpectrumMatches.isValid() && maxPeptideMass.isValid() && maxCombinations.isValid() && topPeaks.isValid() && topPeaksWindows.isValid()) {
                updateParameters();
                setPopupVisible(false);
            }
        });
        container.addComponent(okBtn, "bottom:10px;right:10px");
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyleName(ValoTheme.BUTTON_TINY);
        cancelBtn.setWidth(76, Unit.PIXELS);
        cancelBtn.setHeight(20, Unit.PIXELS);
        container.addComponent(cancelBtn, "bottom:10px;right:96px");
        cancelBtn.addClickListener((Button.ClickEvent event) -> {
            AndromedaAdvancedSettingsPanel.this.setPopupVisible(false);
        });

    }

    public void updateGUI(IdentificationParameters webSearchParameters) {
        this.webSearchParameters = webSearchParameters;
        AndromedaParameters andromedaParameters = (AndromedaParameters) webSearchParameters.getSearchParameters().getIdentificationAlgorithmParameter(Advocate.andromeda.getIndex());
        peptideLength.setFirstSelectedValue(andromedaParameters.getMinPeptideLengthNoEnzyme());
        peptideLength.setSecondSelectedValue(andromedaParameters.getMaxPeptideLengthNoEnzyme());
        maxPeptideMass.setSelectedValue(andromedaParameters.getMaxPeptideMass());
        numberOfSpectrumMatches.setSelectedValue(andromedaParameters.getNumberOfCandidates());
        maxVariabalePTMs.setSelectedValue(andromedaParameters.getMaxNumberOfModifications());
        fragmentaionMethod.setSelected(andromedaParameters.getFragmentationMethod().name());
        if (andromedaParameters.isIncludeWater()) {
            waterLoss.setSelected("Yes");
        } else {
            waterLoss.setSelected("No");
        }
        if (andromedaParameters.isIncludeAmmonia()) {
            ammoniaLoss.setSelected("Yes");
        } else {
            ammoniaLoss.setSelected("No");
        }
        if (andromedaParameters.isDependentLosses()) {
            sequenceDependentNeutralLoss.setSelected("Yes");
        } else {
            sequenceDependentNeutralLoss.setSelected("No");
        }
        if (andromedaParameters.isEqualIL()) {
            equalIandL.setSelected("Yes");
        } else {
            equalIandL.setSelected("No");
        }
        if (andromedaParameters.isFragmentAll()) {
            fragmentAll.setSelected("Yes");
        } else {
            fragmentAll.setSelected("No");
        }
        if (andromedaParameters.isEmpiricalCorrection()) {
            empiricalCorrection.setSelected("Yes");
        } else {
            empiricalCorrection.setSelected("No");
        }
        if (andromedaParameters.isHigherCharge()) {
            higherCharge.setSelected("Yes");
        } else {
            higherCharge.setSelected("No");
        }

        maxCombinations.setSelectedValue(andromedaParameters.getMaxCombinations());
        topPeaks.setSelectedValue(andromedaParameters.getTopPeaks());
        topPeaksWindows.setSelectedValue(andromedaParameters.getTopPeaksWindow());
        decoyMode.setSelected(andromedaParameters.getDecoyMode().name());
    }

    @Override
    public void onClosePopup() {
    }

    @Override
    public void setPopupVisible(boolean visible) {
        if (visible && webSearchParameters != null) {
            updateGUI(webSearchParameters);
        }
//else if (webSearchParameters != null) {
//            updateParameters();
//        }
        super.setPopupVisible(visible); //To change body of generated methods, choose Tools | Templates.
    }

    private void updateParameters() {
        AndromedaParameters andromedaParameters = (AndromedaParameters) webSearchParameters.getSearchParameters().getIdentificationAlgorithmParameter(Advocate.andromeda.getIndex());
        andromedaParameters.setMinPeptideLengthNoEnzyme(Integer.valueOf(peptideLength.getFirstSelectedValue()));
        andromedaParameters.setMaxPeptideLengthNoEnzyme(Integer.valueOf(peptideLength.getSecondSelectedValue()));
        andromedaParameters.setMaxPeptideMass(Double.valueOf(maxPeptideMass.getSelectedValue().replace(",", "")));
        andromedaParameters.setNumberOfCandidates(Integer.valueOf(numberOfSpectrumMatches.getSelectedValue()));
        andromedaParameters.setMaxNumberOfModifications(Integer.valueOf(maxVariabalePTMs.getSelectedValue()));
        andromedaParameters.setFragmentationMethod(FragmentationMethod.valueOf(fragmentaionMethod.getSelectedValue()));
        andromedaParameters.setIncludeWater(waterLoss.getSelectedValue().equalsIgnoreCase("Yes"));
        andromedaParameters.setIncludeAmmonia(ammoniaLoss.getSelectedValue().equalsIgnoreCase("Yes"));
        andromedaParameters.setDependentLosses(sequenceDependentNeutralLoss.getSelectedValue().equalsIgnoreCase("Yes"));
        andromedaParameters.setEqualIL(equalIandL.getSelectedValue().equalsIgnoreCase("Yes"));
        andromedaParameters.setFragmentAll(fragmentAll.getSelectedValue().equalsIgnoreCase("Yes"));
        andromedaParameters.setEmpiricalCorrection(empiricalCorrection.getSelectedValue().equalsIgnoreCase("Yes"));
        andromedaParameters.setHigherCharge(higherCharge.getSelectedValue().equalsIgnoreCase("Yes"));

        andromedaParameters.setMaxCombinations(Integer.valueOf(maxCombinations.getSelectedValue()));
        andromedaParameters.setTopPeaks(Integer.valueOf(topPeaks.getSelectedValue()));
        andromedaParameters.setTopPeaksWindow(Integer.valueOf(topPeaksWindows.getSelectedValue()));
        andromedaParameters.setDecoyMode(AndromedaParameters.AndromedaDecoyMode.valueOf(decoyMode.getSelectedValue()));

    }

}
