package com.uib.web.peptideshaker.presenter.layouts.advancedsearchenginessettings;

import com.compomics.util.experiment.biology.modifications.ModificationFactory;
import com.compomics.util.experiment.identification.Advocate;
import com.compomics.util.parameters.identification.IdentificationParameters;
import com.compomics.util.parameters.identification.tool_specific.XtandemParameters;
import com.uib.web.peptideshaker.presenter.core.Help;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.uib.web.peptideshaker.presenter.core.form.ColorLabel;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabelDropDounList;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabelTextField;
import com.uib.web.peptideshaker.presenter.core.form.SparkLine;
import com.vaadin.data.Property;
import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author y-mok
 */
public class XTandemAdvancedSettingsPanel extends PopupWindow {

    private final HorizontalLabelTextField spectrumDynamicRange;
    private final HorizontalLabelTextField numberOfPeaks;
    private final HorizontalLabelTextField minFragment;
    private final HorizontalLabelTextField minPeaks;
    private final HorizontalLabelTextField minPrecursorMass;
    private final HorizontalLabelDropDounList noiseSuppressionList;
    private final HorizontalLabelDropDounList parentIsotopExpansionList;

    private final HorizontalLabelDropDounList quickAcetyl;
    private final HorizontalLabelDropDounList quickPyrolidone;
    private final HorizontalLabelDropDounList stPbias;
    private final HorizontalLabelTextField ptmComplexity;
//
    private final HorizontalLabelDropDounList outputResults;
    private final HorizontalLabelTextField eValueCutoff;
    private final HorizontalLabelDropDounList outputProteins;
    private final HorizontalLabelDropDounList outputSequences;
    private final HorizontalLabelDropDounList outputSpectra;
    private final HorizontalLabelDropDounList outputHistograms;
    private final HorizontalLabelTextField skylinePath;

    private final HorizontalLabelDropDounList refinement;
    private final HorizontalLabelTextField maximumValidExpectationValue;
    private final HorizontalLabelDropDounList unanticipatedCleavage;
    private final HorizontalLabelDropDounList semiEnzymaticCleavage;
    private final HorizontalLabelDropDounList potintialModificationsforFullRefinement;
    private final HorizontalLabelDropDounList pointMutations;
    private final HorizontalLabelDropDounList snAPs;
    private final HorizontalLabelDropDounList spectrumSynthesis;

    /**
     * The full modifications list table.
     */
    private final Table allModificationsTable;
    private final DecimalFormat df = new DecimalFormat("0.00E00");//new DecimalFormat("#.##");
    private IdentificationParameters webSearchParameters;
    private final Set<String> refModificationSelection = new LinkedHashSet<>();
    /**
     * The post translational modifications factory.
     */
    private final ModificationFactory PTM = ModificationFactory.getInstance();
    /**
     * The modification items that is used for initialise modifications tables.
     */
    private final Map<Object, Object[]> completeModificationItems = new LinkedHashMap<>();
    /**
     * The post translational modifications factory.
     */
    private ModificationFactory modificationFactory = ModificationFactory.getInstance();

    public XTandemAdvancedSettingsPanel() {
        super(VaadinIcons.COG.getHtml() + " X! Tandem Advanced Settings");

        AbsoluteLayout container = new AbsoluteLayout();
        container.setStyleName("popuppanelmaincontainer");
        container.setWidth(500, Unit.PIXELS);
        container.setHeight(500, Unit.PIXELS);

        XTandemAdvancedSettingsPanel.this.setContent(container);
        XTandemAdvancedSettingsPanel.this.setClosable(true);

        Label title = new Label("X! Tandem ");
        container.addComponent(title, "left:10px;top:10px");

        TabSheet subContainer = new TabSheet();
        subContainer.setSizeFull();
        container.addComponent(subContainer, "left:10px;top:40px;right:10px;bottom:40px");
        subContainer.setStyleName("subcontainertabsheet");
        subContainer.addStyleName(ValoTheme.TABSHEET_COMPACT_TABBAR);
        /**
         * tab 1
         */
        VerticalLayout tab1 = new VerticalLayout();
        subContainer.addTab(tab1, "Spectrum Import");

        this.spectrumDynamicRange = new HorizontalLabelTextField("Spectrum Dynamic Range <a href='https://www.thegpm.org/TANDEM/api/sdr.html' target=\"_blank\"> (?)</a>", 0.0, new DoubleRangeValidator("Only Positive number allowed ", 0.0, Double.MAX_VALUE));
        tab1.addComponent(spectrumDynamicRange);

        this.numberOfPeaks = new HorizontalLabelTextField("Number of Peaks <a href='https://www.thegpm.org/TANDEM/api/stp.html' target=\"_blank\"> (?)</a>", 0, new IntegerRangeValidator("Only Positive Integer allowed", 0, Integer.MAX_VALUE));
        tab1.addComponent(numberOfPeaks);

        this.minFragment = new HorizontalLabelTextField("Minimum Fragment m/z <a href='https://www.thegpm.org/TANDEM/api/smfmz.html' target=\"_blank\"> (?)</a>", 0.0, new DoubleRangeValidator("Only Positive number allowed", 0.0, Double.MAX_VALUE));
        tab1.addComponent(minFragment);

        this.minPeaks = new HorizontalLabelTextField("Minimum Peaks <a href='https://www.thegpm.org/TANDEM/api/smp.html' target=\"_blank\"> (?)</a>", 0, new IntegerRangeValidator("Only Positive Integer allowed", 0, Integer.MAX_VALUE));
        tab1.addComponent(minPeaks);
        Set<String> values = new LinkedHashSet<>();
        values.add("Yes");
        values.add("No");
        this.noiseSuppressionList = new HorizontalLabelDropDounList("Noise Suppression <a href='https://www.thegpm.org/TANDEM/api/suns.html' target=\"_blank\"> (?)</a>");
        this.noiseSuppressionList.updateData(values);
        tab1.addComponent(this.noiseSuppressionList);

        this.minPrecursorMass = new HorizontalLabelTextField("Minimum Precursor Mass <a href='https://www.thegpm.org/TANDEM/api/smpmh.html' target=\"_blank\"> (?)</a>", 0.0, new DoubleRangeValidator("Only Positive number allowed", 0.0, Double.MAX_VALUE));
        tab1.addComponent(this.minPrecursorMass);

        this.noiseSuppressionList.addValueChangeListener((Property.ValueChangeEvent event) -> {
            minPrecursorMass.setEnabled(noiseSuppressionList.getSelectedValue().equalsIgnoreCase("Yes"));
        });
        this.noiseSuppressionList.setSelected("No");

        parentIsotopExpansionList = new HorizontalLabelDropDounList("Parent Isotop Expansion <a href='https://www.thegpm.org/TANDEM/api/spmmie.html' target=\"_blank\"> (?)</a>");
        parentIsotopExpansionList.updateData(values);
        tab1.addComponent(this.parentIsotopExpansionList);

        /*tab 2*/
        VerticalLayout tab2 = new VerticalLayout();
        subContainer.addTab(tab2, "Advanced Search");
        quickAcetyl = new HorizontalLabelDropDounList("Quick Acetyl <a href='https://www.thegpm.org/TANDEM/api/pqa.html' target=\"_blank\"> (?)</a>");
        quickAcetyl.updateData(values);
        tab2.addComponent(this.quickAcetyl);

        quickPyrolidone = new HorizontalLabelDropDounList("Quick Pyrolidone <a href='https://www.thegpm.org/TANDEM/api/pqp.html' target=\"_blank\"> (?)</a>");
        quickPyrolidone.updateData(values);
        tab2.addComponent(this.quickPyrolidone);

        stPbias = new HorizontalLabelDropDounList("stP bias <a href='https://www.thegpm.org/TANDEM/api/pstpb.html' target=\"_blank\"> (?)</a>");
        stPbias.updateData(values);
        tab2.addComponent(this.stPbias);

        this.ptmComplexity = new HorizontalLabelTextField("PTM Complexity <a href='https://www.thegpm.org/TANDEM/release.html' target=\"_blank\"> (?)</a>", 0.0, new DoubleRangeValidator("Select number from 0.0 to 12.00", 0.0, 12.0));
        tab2.addComponent(ptmComplexity);

        /**
         * *
         * tab 3
         *
         */
        VerticalLayout tab3 = new VerticalLayout();
        subContainer.addTab(tab3, "Output");

        Set<String> values2 = new LinkedHashSet<>();
        values2.add("all");
        values2.add("valid");
        values2.add("stochastic");

        outputResults = new HorizontalLabelDropDounList("Output Results <a href='https://www.thegpm.org/TANDEM/api/oresu.html' target=\"_blank\"> (?)</a>");
        outputResults.updateData(values2);
        tab3.addComponent(this.outputResults);

        this.eValueCutoff = new HorizontalLabelTextField("E-value Cutoff<a href='https://www.thegpm.org/TANDEM/api/omvev.html' target=\"_blank\"> (?)</a>", 0.0, new DoubleRangeValidator("Select positive number ", 0.0, Double.MAX_VALUE));
        tab3.addComponent(eValueCutoff);
        outputResults.addValueChangeListener((Property.ValueChangeEvent event) -> {
            eValueCutoff.setEnabled(!outputResults.getSelectedValue().equalsIgnoreCase("all"));
        });
        outputProteins = new HorizontalLabelDropDounList("Output Proteins <a href='https://www.thegpm.org/TANDEM/api/oprot.html' target=\"_blank\"> (?)</a>");
        outputProteins.updateData(values);
        tab3.addComponent(this.outputProteins);

        outputSequences = new HorizontalLabelDropDounList("Output Sequences <a href='https://www.thegpm.org/TANDEM/api/osequ.html' target=\"_blank\"> (?)</a>");
        outputSequences.updateData(values);
        tab3.addComponent(this.outputSequences);

        outputSpectra = new HorizontalLabelDropDounList("Output Spectra <a href='https://www.thegpm.org/TANDEM/api/ospec.html' target=\"_blank\"> (?)</a>");
        outputSpectra.updateData(values);
        tab3.addComponent(this.outputSpectra);

        outputHistograms = new HorizontalLabelDropDounList("Output Histograms <a href='https://www.thegpm.org/TANDEM/api/ohist.html' target=\"_blank\"> (?)</a>");
        outputHistograms.updateData(values);
        tab3.addComponent(this.outputHistograms);

        this.skylinePath = new HorizontalLabelTextField("Skyline Path <a href='https://www.thegpm.org/TANDEM/ssp.html' target=\"_blank\"> (?)</a>", "", null);
        tab3.addComponent(skylinePath);

        /**
         * tab 4*
         */
        VerticalLayout tab4 = new VerticalLayout();
        subContainer.addTab(tab4, "Refinement");

        refinement = new HorizontalLabelDropDounList("Refinement <a href='https://www.thegpm.org/TANDEM/api/refine.html' target=\"_blank\"> (?)</a>");
        refinement.updateData(values);
        tab4.addComponent(this.refinement);
        refinement.updateExpandingRatio(0.6f, 0.4f);

        this.maximumValidExpectationValue = new HorizontalLabelTextField("Maximum Valid Expectation Value<a href='https://www.thegpm.org/TANDEM/api/refmvev.html' target=\"_blank\"> (?)</a>", 0.0, new DoubleRangeValidator("Select positive number ", 0.0, Double.MAX_VALUE));
        tab4.addComponent(maximumValidExpectationValue);
        maximumValidExpectationValue.updateExpandingRatio(0.6f, 0.4f);

        unanticipatedCleavage = new HorizontalLabelDropDounList(" Unanticipated Cleavage <a href='https://www.thegpm.org/TANDEM/api/ruc.html' target=\"_blank\"> (?)</a>");
        unanticipatedCleavage.updateData(values);
        tab4.addComponent(this.unanticipatedCleavage);
        unanticipatedCleavage.updateExpandingRatio(0.6f, 0.4f);

        semiEnzymaticCleavage = new HorizontalLabelDropDounList("Semi-Enzymatic Cleavage <a href='https://www.thegpm.org/TANDEM/api/rcsemi.html' target=\"_blank\"> (?)</a>");
        semiEnzymaticCleavage.updateData(values);
        tab4.addComponent(this.semiEnzymaticCleavage);
        semiEnzymaticCleavage.updateExpandingRatio(0.6f, 0.4f);

        potintialModificationsforFullRefinement = new HorizontalLabelDropDounList("Potintial Modifications for Full Refinement <a href='https://www.thegpm.org/TANDEM/api/rupmffr.html' target=\"_blank\"> (?)</a>");
        potintialModificationsforFullRefinement.updateData(values);
        potintialModificationsforFullRefinement.updateExpandingRatio(0.6f, 0.4f);
        tab4.addComponent(this.potintialModificationsforFullRefinement);

        pointMutations = new HorizontalLabelDropDounList("Point Mutations <a href='https://www.thegpm.org/TANDEM/api/rpm.html' target=\"_blank\"> (?)</a>");
        pointMutations.updateData(values);
        pointMutations.updateExpandingRatio(0.6f, 0.4f);
        tab4.addComponent(this.pointMutations);
        snAPs = new HorizontalLabelDropDounList("snAPs <a href='https://www.thegpm.org/TANDEM/api/rsaps.html' target=\"_blank\"> (?)</a>");
        snAPs.updateData(values);
        snAPs.updateExpandingRatio(0.6f, 0.4f);
        tab4.addComponent(this.snAPs);
        spectrumSynthesis = new HorizontalLabelDropDounList("Spectrum Synthesis <a href='https://www.thegpm.org/TANDEM/api/rss.html' target=\"_blank\"> (?)</a>");
        spectrumSynthesis.updateData(values);
        spectrumSynthesis.updateExpandingRatio(0.6f, 0.4f);
        tab4.addComponent(this.spectrumSynthesis);

        refinement.addValueChangeListener((Property.ValueChangeEvent event) -> {
            maximumValidExpectationValue.setEnabled(outputResults.getSelectedValue().equalsIgnoreCase("Yes"));
            unanticipatedCleavage.setEnabled(outputResults.getSelectedValue().equalsIgnoreCase("Yes"));
            semiEnzymaticCleavage.setEnabled(outputResults.getSelectedValue().equalsIgnoreCase("Yes"));
            potintialModificationsforFullRefinement.setEnabled(outputResults.getSelectedValue().equalsIgnoreCase("Yes"));
            pointMutations.setEnabled(outputResults.getSelectedValue().equalsIgnoreCase("Yes"));
            snAPs.setEnabled(outputResults.getSelectedValue().equalsIgnoreCase("Yes"));
            spectrumSynthesis.setEnabled(outputResults.getSelectedValue().equalsIgnoreCase("Yes"));

        });
        allModificationsTable = initModificationTable("Refinement Modifications <a href='https://www.thegpm.org/TANDEM/api/refpmm.html' target=\"_blank\"> (?)</a>");

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
        tab4.addComponent(this.allModificationsTable);
        for (int x = 0; x < allModiList.size(); x++) {
            if (modificationFactory.getModification(allModiList.get(x)) == null) {
                continue;
            }
            ColorLabel color = new ColorLabel(new Color(PTM.getColor(allModiList.get(x))));
            SparkLine sLine = new SparkLine(PTM.getModification(allModiList.get(x)).getMass(), minMass, maxMass);
            sLine.addStyleName("intablesparkline");
            CheckBox cb1 = new CheckBox();
            String modID = allModiList.get(x);

            cb1.setData("v;" + modID);
            cb1.addValueChangeListener((Property.ValueChangeEvent event) -> {
                if ((boolean) event.getProperty().getValue()) {
                    refModificationSelection.add(cb1.getData().toString());
                } else {
                    refModificationSelection.remove(cb1.getData().toString());
                }
            });
            cb1.setDescription("Variable Refinement Modification");
            CheckBox cb2 = new CheckBox();
            cb2.setData("f;" + modID);
            cb2.addValueChangeListener((Property.ValueChangeEvent event) -> {
                if ((boolean) event.getProperty().getValue()) {
                    refModificationSelection.add(cb2.getData().toString());
                } else {
                    refModificationSelection.remove(cb2.getData().toString());
                }
            });
            cb2.setDescription("Fixed Refinement Modification");
            Object[] modificationArr = new Object[]{color, allModiList.get(x), sLine, cb1, cb2};
//            String updatedId = "<font style='color:" + color.getRGBColorAsString() + ";font-size:10px !important;margin-right:5px'> " + VaadinIcons.CIRCLE.getHtml() + "</font>" + allModiList.get(x);
            completeModificationItems.put(allModiList.get(x), modificationArr);
            allModificationsTable.addItem(modificationArr, allModiList.get(x));

        }

        Button okBtn = new Button("OK");
        okBtn.setWidth(76, Unit.PIXELS);
        okBtn.setHeight(20, Unit.PIXELS);
        okBtn.setStyleName(ValoTheme.BUTTON_TINY);
        okBtn.addClickListener((Button.ClickEvent event) -> {
            if (spectrumDynamicRange.isValid() && numberOfPeaks.isValid() && minFragment.isValid() && minPeaks.isValid() && minPrecursorMass.isValid() && ptmComplexity.isValid() && eValueCutoff.isValid() && maximumValidExpectationValue.isValid()) {
                updateParameters();
                setPopupVisible(false);
            }
        });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyleName(ValoTheme.BUTTON_TINY);
        cancelBtn.setWidth(76, Unit.PIXELS);
        cancelBtn.setHeight(20, Unit.PIXELS);

        cancelBtn.addClickListener((Button.ClickEvent event) -> {
            XTandemAdvancedSettingsPanel.this.setPopupVisible(false);
        });

        String helpText = "<h1>X!Tandem Advanced Settings</h1>"
                + "X!Tandem comes with many options allowing you to fine tune the search settings. Note that these settings aim to be used by advanced users only. It is highly recommended to carefully test any change to the default settings, to document them and report them in any publication/report.  For more help, consult the X!Tandem API. If you are missing a parameter, please contact the developers.";
        Help help = new Help(helpText, "The advanced settings are for expert use only. See help for details", 450, 155);
        container.addComponent(help, "left:10px;bottom:10px;");
        container.addComponent(cancelBtn, "bottom:10px;right:10px");
        container.addComponent(okBtn, "bottom:10px;right:96px");
    }

    public void updateGUI(IdentificationParameters webSearchParameters) {
        this.webSearchParameters = webSearchParameters;
//        this.refModificationSelection.clear();
        XtandemParameters oldXtandemParameters = (XtandemParameters) this.webSearchParameters.getSearchParameters().getIdentificationAlgorithmParameter(Advocate.xtandem.getIndex());
        spectrumDynamicRange.setSelectedValue(oldXtandemParameters.getDynamicRange());
        numberOfPeaks.setSelectedValue(oldXtandemParameters.getnPeaks());
        minFragment.setSelectedValue(oldXtandemParameters.getMinFragmentMz());
        minPeaks.setSelectedValue(oldXtandemParameters.getMinPeaksPerSpectrum());
        if (oldXtandemParameters.isUseNoiseSuppression()) {
            noiseSuppressionList.setSelected("Yes");
        } else {
            noiseSuppressionList.setSelected("No");
        }
        minPrecursorMass.setSelectedValue(oldXtandemParameters.getMinPrecursorMass());
        if (oldXtandemParameters.getParentMonoisotopicMassIsotopeError()) {
            parentIsotopExpansionList.setSelected("Yes");
        } else {
            parentIsotopExpansionList.setSelected("No");
        }

        if (oldXtandemParameters.isProteinQuickAcetyl()) {
            quickAcetyl.setSelected("Yes");
        } else {
            quickAcetyl.setSelected("No");
        }
        if (oldXtandemParameters.isQuickPyrolidone()) {
            quickPyrolidone.setSelected("Yes");
        } else {
            quickPyrolidone.setSelected("No");
        }
        if (oldXtandemParameters.isStpBias()) {
            stPbias.setSelected("Yes");
        } else {
            stPbias.setSelected("No");
        }
        ptmComplexity.setSelectedValue((double) oldXtandemParameters.getProteinPtmComplexity());
        outputResults.setSelected(oldXtandemParameters.getOutputResults());
        eValueCutoff.setSelectedValue(oldXtandemParameters.getMaxEValue());
        if (oldXtandemParameters.isOutputProteins()) {
            outputProteins.setSelected("Yes");
        } else {
            outputProteins.setSelected("No");
        }
        if (oldXtandemParameters.isOutputSequences()) {
            outputSequences.setSelected("Yes");
        } else {
            outputSequences.setSelected("No");
        }
        if (oldXtandemParameters.isOutputSpectra()) {
            outputSpectra.setSelected("Yes");
        } else {
            outputSpectra.setSelected("No");
        }
        if (oldXtandemParameters.isOutputHistograms()) {
            outputHistograms.setSelected("Yes");
        } else {
            outputHistograms.setSelected("No");
        }
        skylinePath.setSelectedValue(oldXtandemParameters.getSkylinePath());

        if (oldXtandemParameters.isRefine()) {
            refinement.setSelected("Yes");
        } else {
            refinement.setSelected("No");
        }
        maximumValidExpectationValue.setSelectedValue((double) oldXtandemParameters.getMaximumExpectationValueRefinement());
        if (oldXtandemParameters.isRefineUnanticipatedCleavages()) {
            unanticipatedCleavage.setSelected("Yes");
        } else {
            unanticipatedCleavage.setSelected("No");
        }
        if (oldXtandemParameters.isRefineSemi()) {
            semiEnzymaticCleavage.setSelected("Yes");
        } else {
            semiEnzymaticCleavage.setSelected("No");
        }
        if (oldXtandemParameters.isPotentialModificationsForFullRefinment()) {
            potintialModificationsforFullRefinement.setSelected("Yes");
        } else {
            potintialModificationsforFullRefinement.setSelected("No");
        }
        if (oldXtandemParameters.isRefinePointMutations()) {
            pointMutations.setSelected("Yes");
        } else {
            pointMutations.setSelected("No");
        }
        if (oldXtandemParameters.isRefineSnaps()) {
            snAPs.setSelected("Yes");
        } else {
            snAPs.setSelected("No");
        }
        if (oldXtandemParameters.isRefineSpectrumSynthesis()) {
            spectrumSynthesis.setSelected("Yes");
        } else {
            spectrumSynthesis.setSelected("No");
        }

        List<String> rVmod = this.webSearchParameters.getSearchParameters().getModificationParameters().getRefinementVariableModifications();
        List<String> rFmod = this.webSearchParameters.getSearchParameters().getModificationParameters().getRefinementFixedModifications();
        rVmod.forEach((key) -> {
            ((CheckBox) allModificationsTable.getItem(key).getItemProperty("variable").getValue()).setValue(Boolean.TRUE);
        });
        rFmod.forEach((key) -> {
            ((CheckBox) allModificationsTable.getItem(key).getItemProperty("fixed").getValue()).setValue(Boolean.TRUE);
        });

    }

    @Override
    public void onClosePopup() {
    }

    @Override
    public void setPopupVisible(boolean visible) {
        if (visible && webSearchParameters != null) {
            updateGUI(webSearchParameters);
        }
        super.setPopupVisible(visible); //To change body of generated methods, choose Tools | Templates.
    }

    private void updateParameters() {

        XtandemParameters oldXtandemParameters = (XtandemParameters) this.webSearchParameters.getSearchParameters().getIdentificationAlgorithmParameter(Advocate.xtandem.getIndex());
        oldXtandemParameters.setDynamicRange(Double.valueOf(spectrumDynamicRange.getSelectedValue()));
        oldXtandemParameters.setnPeaks(Integer.valueOf(numberOfPeaks.getSelectedValue()));
        oldXtandemParameters.setMinFragmentMz(Double.valueOf(minFragment.getSelectedValue()));
        oldXtandemParameters.setMinPeaksPerSpectrum(Integer.valueOf(minPeaks.getSelectedValue()));
        oldXtandemParameters.setMinPrecursorMass(Double.valueOf(minPrecursorMass.getSelectedValue()));
        oldXtandemParameters.setUseNoiseSuppression(noiseSuppressionList.getSelectedValue().equalsIgnoreCase("Yes"));
        oldXtandemParameters.setParentMonoisotopicMassIsotopeError(parentIsotopExpansionList.getSelectedValue().equalsIgnoreCase("Yes"));
        oldXtandemParameters.setProteinQuickAcetyl(quickAcetyl.getSelectedValue().equalsIgnoreCase("Yes"));
        oldXtandemParameters.setQuickPyrolidone(quickPyrolidone.getSelectedValue().equalsIgnoreCase("Yes"));
        oldXtandemParameters.setStpBias(stPbias.getSelectedValue().equalsIgnoreCase("Yes"));
        oldXtandemParameters.setProteinPtmComplexity(Double.valueOf(ptmComplexity.getSelectedValue()));

        oldXtandemParameters.setOutputResults(outputResults.getSelectedValue());
        oldXtandemParameters.setMaxEValue(Double.valueOf(eValueCutoff.getSelectedValue()));
        oldXtandemParameters.setOutputProteins(outputProteins.getSelectedValue().equalsIgnoreCase("Yes"));
        oldXtandemParameters.setOutputSequences(outputSequences.getSelectedValue().equalsIgnoreCase("Yes"));
        oldXtandemParameters.setOutputSpectra(outputSpectra.getSelectedValue().equalsIgnoreCase("Yes"));
        oldXtandemParameters.setOutputHistograms(outputHistograms.getSelectedValue().equalsIgnoreCase("Yes"));
        oldXtandemParameters.setSkylinePath(skylinePath.getSelectedValue());

        oldXtandemParameters.setRefine(refinement.getSelectedValue().equalsIgnoreCase("Yes"));
        oldXtandemParameters.setMaximumExpectationValueRefinement(Double.valueOf(maximumValidExpectationValue.getSelectedValue()));
        oldXtandemParameters.setRefineUnanticipatedCleavages(unanticipatedCleavage.getSelectedValue().equalsIgnoreCase("Yes"));
        oldXtandemParameters.setRefineSemi(semiEnzymaticCleavage.getSelectedValue().equalsIgnoreCase("Yes"));
        oldXtandemParameters.setPotentialModificationsForFullRefinment(potintialModificationsforFullRefinement.getSelectedValue().equalsIgnoreCase("Yes"));
        oldXtandemParameters.setRefinePointMutations(pointMutations.getSelectedValue().equalsIgnoreCase("Yes"));
        oldXtandemParameters.setRefineSnaps(snAPs.getSelectedValue().equalsIgnoreCase("Yes"));
        oldXtandemParameters.setRefineSpectrumSynthesis(spectrumSynthesis.getSelectedValue().equalsIgnoreCase("Yes"));
        List<String> rVmod = new ArrayList<>(this.webSearchParameters.getSearchParameters().getModificationParameters().getRefinementVariableModifications());
        List<String> rFmod = new ArrayList<>(this.webSearchParameters.getSearchParameters().getModificationParameters().getRefinementFixedModifications());
        rVmod.forEach((key) -> {
            this.webSearchParameters.getSearchParameters().getModificationParameters().removeRefinementVariableModification(key);
        });
        rFmod.forEach((key) -> {
            this.webSearchParameters.getSearchParameters().getModificationParameters().removeRefinementFixedModification(key);
        });

        refModificationSelection.stream().map((mod) -> {
            return mod;
        }).forEachOrdered((mod) -> {
            if (mod.contains("v;")) {
                this.webSearchParameters.getSearchParameters().getModificationParameters().addRefinementVariableModification(modificationFactory.getModification(mod.replace("v;", "")));
            } else {
                this.webSearchParameters.getSearchParameters().getModificationParameters().addRefinementFixedModification(modificationFactory.getModification(mod.replace("f;", "")));
            }
        });
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
        modificationsTable.setWidth(100, Unit.PERCENTAGE);
        modificationsTable.setHeight(135, Unit.PIXELS);
        modificationsTable.setCaptionAsHtml(true);
//        modificationsTable.setStyleName(ValoTheme.TABLE_SMALL);
        modificationsTable.addStyleName(ValoTheme.TREETABLE_SMALL);
//        modificationsTable.addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
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
        modificationsTable
                .addContainerProperty("variable", CheckBox.class,
                        null, "V", null, Table.Align.LEFT);
        modificationsTable
                .addContainerProperty("fixed", CheckBox.class,
                        null, "F", null, Table.Align.LEFT);

        modificationsTable.setColumnExpandRatio("color", 10);
        modificationsTable.setColumnExpandRatio("name", 55);
        modificationsTable.setColumnExpandRatio("mass", 35);
        modificationsTable.setColumnWidth("variable", 30);
        modificationsTable.setColumnWidth("fixed", 30);
        modificationsTable.sort(new Object[]{"name"}, new boolean[]{true});
        modificationsTable.setSortEnabled(false);
        modificationsTable.setItemDescriptionGenerator((Component source, Object itemId, Object propertyId) -> PTM.getModification(itemId.toString()).getHtmlTooltip());
        return modificationsTable;
    }

}
