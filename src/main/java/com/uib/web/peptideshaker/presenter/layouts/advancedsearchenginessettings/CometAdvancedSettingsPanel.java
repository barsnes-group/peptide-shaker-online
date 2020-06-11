package com.uib.web.peptideshaker.presenter.layouts.advancedsearchenginessettings;

import com.compomics.util.experiment.identification.Advocate;
import com.compomics.util.parameters.identification.IdentificationParameters;
import com.compomics.util.parameters.identification.tool_specific.CometParameters;
import com.compomics.util.parameters.identification.tool_specific.CometParameters.CometOutputFormat;
import com.uib.web.peptideshaker.presenter.core.Help;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabel2TextField;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabelDropDounList;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabelTextField;
import com.vaadin.data.Property;
import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author y-mok
 */
public class CometAdvancedSettingsPanel extends PopupWindow {

    private final HorizontalLabelTextField minimumNumberofPeaks;
    private final HorizontalLabelTextField minimalPeakIntensity;
    private final HorizontalLabelDropDounList removePrecursorPeak;
    private final HorizontalLabelTextField removePrecursorPeakTolerance;
    private final HorizontalLabel2TextField clearMZRange;

    private final HorizontalLabelDropDounList enzymeType;
    private final HorizontalLabelDropDounList isotopeCorrection;
    private final HorizontalLabel2TextField precursorMass;
    private final HorizontalLabelTextField maxFragmentCharge;
    private final HorizontalLabelDropDounList removeStartingMethionine;
    private final HorizontalLabelTextField spectrumBatchSize;
    private final HorizontalLabelTextField maxVariablePTMsPerPeptide;
    private final HorizontalLabelDropDounList requiredVariablePTM;
    private final HorizontalLabelDropDounList correlationScoreType;
    private final HorizontalLabelTextField fragmentBinOffset;
    private final HorizontalLabelTextField numberOfSpectrumMatches;
    private final HorizontalLabelDropDounList outputFormat;
    private final HorizontalLabelDropDounList printExpectScore;

    private IdentificationParameters webSearchParameters;

    public CometAdvancedSettingsPanel() {
        super(VaadinIcons.COG.getHtml() + " Comet Advanced Settings");

        AbsoluteLayout container = new AbsoluteLayout();
        container.setStyleName("popuppanelmaincontainer");
        container.setWidth(500, Unit.PIXELS);
        container.setHeight(415, Unit.PIXELS);

        CometAdvancedSettingsPanel.this.setContent(container);
        CometAdvancedSettingsPanel.this.setClosable(true);
        
         Label title = new Label("Comet");
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
        subContainer.addTab(tab1, "Spectrum");

        this.minimumNumberofPeaks = new HorizontalLabelTextField("Minimum Number of Peaks", 0, new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab1.addComponent(minimumNumberofPeaks);
        this.minimalPeakIntensity = new HorizontalLabelTextField("Minimal Peak Intensity", 0.0, new DoubleRangeValidator("Only positive double allowed", 0.0, Double.MAX_VALUE));
        tab1.addComponent(minimalPeakIntensity);
        Set<String> values = new LinkedHashSet<>();
        values.add(0 + "");
        values.add(1 + "");
        values.add(2 + "");
        values.add(3 + "");

        this.removePrecursorPeak = new HorizontalLabelDropDounList("Monoisotopic Precursor");
        this.removePrecursorPeak.updateData(values);
        tab1.addComponent(this.removePrecursorPeak);
        removePrecursorPeak.setItemCaption(0 + "", "No");
        removePrecursorPeak.setItemCaption(1 + "", "Yes");
        removePrecursorPeak.setItemCaption(2 + "", "Yes + Charge Reduced");
        removePrecursorPeak.setItemCaption(3 + "", "Yes + Phosphate Neutral");

        values.clear();
        values.add("Yes");
        values.add("No");
        this.removePrecursorPeakTolerance = new HorizontalLabelTextField("Remove Precursor Peak Tolerance (Da)", 0.0, new DoubleRangeValidator("Only postive double value allowed ", 0.0, Double.MAX_VALUE));
        tab1.addComponent(removePrecursorPeakTolerance);

        this.clearMZRange = new HorizontalLabel2TextField("Clear m/z Range", 0.0,0.0, new DoubleRangeValidator("Only double number allowed ",(-1* Double.MAX_VALUE), Double.MAX_VALUE));
        tab1.addComponent(clearMZRange);

        /*tab 2*/
        VerticalLayout tab2 = new VerticalLayout();
        subContainer.addTab(tab2, "Search");
        Set<String> values2 = new LinkedHashSet<>();
        values2.add(2 + "");
        values2.add(1 + "");
        values2.add(2 + "");
        values2.add(3 + "");

        enzymeType = new HorizontalLabelDropDounList("Enzyme Type");
        enzymeType.updateData(values2);
        tab2.addComponent(this.enzymeType);
        /**
         * The enzyme type: 1 for a semi-enzyme search, 2 for a full-enzyme
         * search, 8 for a semi-enzyme search, unspecific cleavage on peptide's
         * C-terminus and 9 for a semi-enzyme search, unspecific cleavage on
         * peptide's N-terminus.
         */
        enzymeType.setItemCaption(2 + "", "Full-enzyme");
        enzymeType.setItemCaption(1 + "", "Semi-specific");
        enzymeType.setItemCaption(8 + "", "Unspecific Peptide's C-term");
        enzymeType.setItemCaption(9 + "", "Unspecific Peptide's N-term");

        /**
         * Isotope correction setting. 0: analyzes no isotope offsets, just the
         * given precursor mass, 1: searches 0, +1 isotope offsets, 2: searches
         * 0, +1, +2 isotope offsets, 3: searches 0, +1, +2, +3 isotope offsets,
         * 4: searches -8, -4, 0, +4, +8 isotope offsets (for +4/+8 stable
         * isotope labeling).
         */
        values2.clear();
        isotopeCorrection = new HorizontalLabelDropDounList("isotopeCorrection");
        values2.add(0 + "");
        values2.add(1 + "");
        values2.add(2 + "");
        values2.add(3 + "");
        values2.add(4 + "");
        isotopeCorrection.updateData(values2);
        tab2.addComponent(this.isotopeCorrection);
        isotopeCorrection.setItemCaption(0 + "", "No Correction");
        isotopeCorrection.setItemCaption(1 + "", "0, +1");
        isotopeCorrection.setItemCaption(2 + "", "0, +1, +2");
        isotopeCorrection.setItemCaption(3 + "", "0, +1, +2, +3");
        isotopeCorrection.setItemCaption(4 + "", " -8, -4, 0, +4, +8");

        this.precursorMass = new HorizontalLabel2TextField("Precursor Mass (min-max)", 0.0, 0.0, new DoubleRangeValidator("Only double values allowed", (-1.0*Double.MAX_VALUE), Double.MAX_VALUE));
        tab2.addComponent(precursorMass);
        this.maxFragmentCharge = new HorizontalLabelTextField("Max Fragment Charge", 0, new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab2.addComponent(maxFragmentCharge);
        removeStartingMethionine = new HorizontalLabelDropDounList("Remove Starting Methionine");
        removeStartingMethionine.updateData(values);
        tab2.addComponent(this.removeStartingMethionine);
        this.spectrumBatchSize = new HorizontalLabelTextField("spectrum Batch Size", 0, new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab2.addComponent(spectrumBatchSize);
        spectrumBatchSize.setRequired(false);
        this.maxVariablePTMsPerPeptide = new HorizontalLabelTextField("Max Variable PTMs per Type", 0, new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab2.addComponent(maxVariablePTMsPerPeptide);
        requiredVariablePTM = new HorizontalLabelDropDounList("Required Variable PTM");
        requiredVariablePTM.updateData(values);
        tab2.addComponent(this.requiredVariablePTM);

        //tab 3
        /*tab 2*/
        VerticalLayout tab3 = new VerticalLayout();
        subContainer.addTab(tab3, "Fragment Ions");

        values2.clear();
        values2.add(Boolean.FALSE.toString());
        values2.add(Boolean.TRUE.toString());

        correlationScoreType = new HorizontalLabelDropDounList("Correlation Score Type");
        correlationScoreType.updateData(values2);
        tab3.addComponent(this.correlationScoreType);
        correlationScoreType.setItemCaption(Boolean.FALSE.toString(), "Summed Intensities + Flanking");//false
        correlationScoreType.setItemCaption(Boolean.TRUE.toString(), "Summed Intensities");//true value

        this.fragmentBinOffset = new HorizontalLabelTextField("Fragment Bin Offset", 0.0, new DoubleRangeValidator("Only postive double values allowed", 0.0, Double.MAX_VALUE));
        tab3.addComponent(this.fragmentBinOffset);
        //tab 4
        VerticalLayout tab4 = new VerticalLayout();
        subContainer.addTab(tab4, "Output");

        this.numberOfSpectrumMatches = new HorizontalLabelTextField("Number of Spectrum Matches", 0, new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab4.addComponent(numberOfSpectrumMatches);

        values2.clear();
        values2.add(CometParameters.CometOutputFormat.PepXML.name());
        values2.add(CometParameters.CometOutputFormat.SQT.name());
        values2.add(CometParameters.CometOutputFormat.TXT.name());
        values2.add(CometParameters.CometOutputFormat.Percolator.name());

        outputFormat = new HorizontalLabelDropDounList("Output Format");
        outputFormat.updateData(values2);

        outputFormat.setItemCaption(CometParameters.CometOutputFormat.PepXML.name(), "PepXML");
        outputFormat.setItemCaption(CometParameters.CometOutputFormat.SQT.name(), "SQT");
        outputFormat.setItemCaption(CometParameters.CometOutputFormat.TXT.name(), "TXT");
        outputFormat.setItemCaption(CometParameters.CometOutputFormat.Percolator.name(), "Percolator");

        tab4.addComponent(this.outputFormat);
        printExpectScore = new HorizontalLabelDropDounList("Print Expect Score");
        outputFormat.addValueChangeListener((Property.ValueChangeEvent event) -> {
            String selectedValue = outputFormat.getSelectedValue();
            if (!selectedValue.equalsIgnoreCase(CometParameters.CometOutputFormat.PepXML.name())) {
                Notification.show("Note that the Comet " + selectedValue + " format is not compatible with PeptideShaker", Notification.Type.TRAY_NOTIFICATION);
            }
            printExpectScore.setEnabled(selectedValue.equalsIgnoreCase(CometOutputFormat.SQT.name()));
        });

        printExpectScore.setSelected("Yes");
        printExpectScore.setEnabled(false);
        printExpectScore.updateData(values);
        tab4.addComponent(this.printExpectScore);

        String helpText = "<a href='http://comet-ms.sourceforge.net/' target='_blank'>";
        Help help = new Help(helpText, "<font style='line-height: 20px;'>Click to open the Comet help page.</font>",100,20);
        container.addComponent(help, "left:10px;bottom:10px;");
        Button okBtn = new Button("OK");
        okBtn.setWidth(76, Unit.PIXELS);
        okBtn.setHeight(20, Unit.PIXELS);
        okBtn.setStyleName(ValoTheme.BUTTON_TINY);
        okBtn.addClickListener((Button.ClickEvent event) -> {
            if (clearMZRange.isValid() && precursorMass.isValid() && minimalPeakIntensity.isValid() && fragmentBinOffset.isValid() && spectrumBatchSize.isValid() && removePrecursorPeakTolerance.isValid() && minimumNumberofPeaks.isValid() && maxFragmentCharge.isValid() && maxVariablePTMsPerPeptide.isValid() && numberOfSpectrumMatches.isValid()) {
                updateParameters();
                setPopupVisible(false);
            }
        });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyleName(ValoTheme.BUTTON_TINY);
        cancelBtn.setWidth(76, Unit.PIXELS);
        cancelBtn.setHeight(20, Unit.PIXELS);

        cancelBtn.addClickListener((Button.ClickEvent event) -> {
            CometAdvancedSettingsPanel.this.setPopupVisible(false);
        });

        container.addComponent(cancelBtn, "bottom:10px;right:10px");
        container.addComponent(okBtn, "bottom:10px;right:96px");
    }

    public void updateGUI(IdentificationParameters webSearchParameters) {
        this.webSearchParameters = webSearchParameters;

        CometParameters oldCometParameters = (CometParameters) this.webSearchParameters.getSearchParameters().getIdentificationAlgorithmParameter(Advocate.comet.getIndex());
        minimumNumberofPeaks.setSelectedValue(oldCometParameters.getMinPeaks());
        minimalPeakIntensity.setSelectedValue(oldCometParameters.getMinPeakIntensity());
        removePrecursorPeak.setSelected(oldCometParameters.getRemovePrecursor() + "");
        removePrecursorPeakTolerance.setSelectedValue(oldCometParameters.getRemovePrecursorTolerance());

        clearMZRange.setFirstSelectedValue(oldCometParameters.getLowerClearMzRange().doubleValue());
        clearMZRange.setSecondSelectedValue(oldCometParameters.getUpperClearMzRange().doubleValue());
        enzymeType.setSelected(oldCometParameters.getEnzymeType());
        isotopeCorrection.setSelected(oldCometParameters.getIsotopeCorrection());
        precursorMass.setFirstSelectedValue(oldCometParameters.getMinPrecursorMass());
        precursorMass.setSecondSelectedValue(oldCometParameters.getMaxPrecursorMass());
        maxFragmentCharge.setSelectedValue(oldCometParameters.getMaxFragmentCharge());

        if (oldCometParameters.getRemoveMethionine()) {
            removeStartingMethionine.setSelected("Yes");
        } else {
            removeStartingMethionine.setSelected("No");
        }
        spectrumBatchSize.setSelectedValue(oldCometParameters.getBatchSize());
        maxVariablePTMsPerPeptide.setSelectedValue(oldCometParameters.getMaxVariableMods());

        if (oldCometParameters.getRequireVariableMods()) {
            requiredVariablePTM.setSelected("Yes");
        } else {
            requiredVariablePTM.setSelected("No");
        }
        correlationScoreType.setSelected(oldCometParameters.getTheoreticalFragmentIonsSumOnly().toString());
        fragmentBinOffset.setSelectedValue(oldCometParameters.getFragmentBinOffset());
        numberOfSpectrumMatches.setSelectedValue(oldCometParameters.getNumberOfSpectrumMatches());
        outputFormat.setSelected(oldCometParameters.getSelectedOutputFormat().name());

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

        CometParameters oldCometParameters = (CometParameters) this.webSearchParameters.getSearchParameters().getIdentificationAlgorithmParameter(Advocate.comet.getIndex());
        oldCometParameters.setMinPeaks(Integer.valueOf(minimumNumberofPeaks.getSelectedValue()));
        oldCometParameters.setMinPeakIntensity(Double.valueOf(minimalPeakIntensity.getSelectedValue()));
        oldCometParameters.setRemovePrecursor(Integer.valueOf(removePrecursorPeak.getSelectedValue()));
        oldCometParameters.setRemovePrecursorTolerance(Double.valueOf(removePrecursorPeakTolerance.getSelectedValue()));
        oldCometParameters.setLowerClearMzRange(Double.valueOf(clearMZRange.getFirstSelectedValue()));
        oldCometParameters.setUpperClearMzRange(Double.valueOf(clearMZRange.getSecondSelectedValue()));
        oldCometParameters.setEnzymeType(Integer.valueOf(enzymeType.getSelectedValue()));
        oldCometParameters.setIsotopeCorrection(Integer.valueOf(isotopeCorrection.getSelectedValue()));
        oldCometParameters.setMinPrecursorMass(Double.valueOf(precursorMass.getFirstSelectedValue()));
        oldCometParameters.setMaxPrecursorMass(Double.valueOf(precursorMass.getSecondSelectedValue()));

        oldCometParameters.setMaxFragmentCharge(Integer.valueOf(maxFragmentCharge.getSelectedValue()));
        oldCometParameters.setRemoveMethionine(removeStartingMethionine.getSelectedValue().equalsIgnoreCase("Yes"));
        oldCometParameters.setBatchSize(Integer.valueOf(spectrumBatchSize.getSelectedValue()));
        oldCometParameters.setMaxVariableMods(Integer.valueOf(maxVariablePTMsPerPeptide.getSelectedValue()));
        oldCometParameters.setRequireVariableMods(requiredVariablePTM.getSelectedValue().equalsIgnoreCase("Yes"));

        oldCometParameters.setTheoreticalFragmentIonsSumOnly(Boolean.valueOf(correlationScoreType.getSelectedValue()));
        oldCometParameters.setFragmentBinOffset(Double.valueOf(fragmentBinOffset.getSelectedValue()));
        oldCometParameters.setNumberOfSpectrumMatches(Integer.valueOf(numberOfSpectrumMatches.getSelectedValue()));
        oldCometParameters.setSelectedOutputFormat(CometParameters.CometOutputFormat.valueOf(outputFormat.getSelectedValue()));
        oldCometParameters.setPrintExpectScore(printExpectScore.getSelectedValue().equalsIgnoreCase("Yes"));

    }

}
