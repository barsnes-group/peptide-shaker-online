package com.uib.web.peptideshaker.presenter.layouts.advancedsearchenginessettings;

import com.compomics.util.experiment.identification.Advocate;
import com.compomics.util.parameters.identification.IdentificationParameters;
import com.compomics.util.parameters.identification.tool_specific.TideParameters;
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
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.text.DecimalFormat;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author y-mok
 */
public class TideAdvancedSettingsPanel extends PopupWindow {

    private final HorizontalLabel2TextField peptideLength;
    private final HorizontalLabel2TextField precursorMass;
    private final HorizontalLabelDropDounList monoisotopicPrecursor;
    private final HorizontalLabelDropDounList removeStartingMethionine;
    private final HorizontalLabelTextField maxVariablePTMsPerPeptide;
    private final HorizontalLabelTextField maxVariablePTMsPerType;

    private final HorizontalLabelDropDounList enzymeType;
    private final HorizontalLabelDropDounList peptideList;
    private final HorizontalLabelDropDounList decoyFormat;
    private final HorizontalLabelDropDounList keepTerminalAAs;
    private final HorizontalLabelTextField decoySeed;
    private final HorizontalLabelDropDounList removeTempFolder;
    //tab 2
    private final HorizontalLabelDropDounList calculateExactPValue;
    private final HorizontalLabelDropDounList calculateSPScore;
    private final HorizontalLabel2TextField spectrumMZ;
    private final HorizontalLabelTextField minimumNumberofPeaks;

    private final HorizontalLabelDropDounList charges;
    private final HorizontalLabelDropDounList removePrecursorPeakPP;
    private final HorizontalLabelTextField removePPTolerance;
    private final HorizontalLabelDropDounList useFlankingPeaks;
    private final HorizontalLabelDropDounList useNeutralLossPeaks;
    private final HorizontalLabelTextField MZBinWidth;
    private final HorizontalLabelTextField MZBinOffset;
    private final HorizontalLabelTextField numberOfSpectrumMatches;
    private final HorizontalLabelDropDounList outputFormat;
//

    private IdentificationParameters webSearchParameters;

    public TideAdvancedSettingsPanel() {
        super(VaadinIcons.COG.getHtml() + " Tide Advanced Settings");

        AbsoluteLayout container = new AbsoluteLayout();
        container.setStyleName("popuppanelmaincontainer");
        container.setWidth(500, Unit.PIXELS);
        container.setHeight(415, Unit.PIXELS);

        TideAdvancedSettingsPanel.this.setContent(container);
        TideAdvancedSettingsPanel.this.setClosable(true);

        TabSheet subContainer = new TabSheet();
        subContainer.setSizeFull();
        container.addComponent(subContainer, "left:10px;top:10px;right:10px;bottom:40px");
        subContainer.setStyleName("subcontainertabsheet");
        subContainer.addStyleName(ValoTheme.TABSHEET_COMPACT_TABBAR);
        /**
         * tab 1
         */
        VerticalLayout tab1 = new VerticalLayout();
        subContainer.addTab(tab1, "Index");

        this.peptideLength = new HorizontalLabel2TextField("Peptide Length (min-max)", 0, 0, new IntegerRangeValidator("Only integer number allowed ", (-1* Integer.MAX_VALUE), Integer.MAX_VALUE));
        tab1.addComponent(peptideLength);

        this.precursorMass = new HorizontalLabel2TextField("Precursor Mass (min-max)", 0.0, 0.0, new DoubleRangeValidator("Only double values allowed", (-1* Double.MAX_VALUE), Double.MAX_VALUE));
        tab1.addComponent(precursorMass);
        Set<String> values = new LinkedHashSet<>();
        values.add("Yes");
        values.add("No");
        this.monoisotopicPrecursor = new HorizontalLabelDropDounList("Monoisotopic Precursor");
        this.monoisotopicPrecursor.updateData(values);
        tab1.addComponent(this.monoisotopicPrecursor);

        removeStartingMethionine = new HorizontalLabelDropDounList("Remove Starting Methionine");
        removeStartingMethionine.updateData(values);
        tab1.addComponent(this.removeStartingMethionine);

        this.maxVariablePTMsPerPeptide = new HorizontalLabelTextField("Max Variable PTMs per Peptide", "", new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab1.addComponent(maxVariablePTMsPerPeptide);
        maxVariablePTMsPerPeptide.setRequired(false);

        this.maxVariablePTMsPerType = new HorizontalLabelTextField("Max Variable PTMs per Type", 0, new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab1.addComponent(maxVariablePTMsPerType);

        Set<String> values2 = new LinkedHashSet<>();
        values2.add("full-digest");
        values2.add("partial-digest");
        enzymeType = new HorizontalLabelDropDounList("Enzyme Type");
        enzymeType.updateData(values2);
        tab1.addComponent(this.enzymeType);
        peptideList = new HorizontalLabelDropDounList("Peptide List");
        peptideList.updateData(values);
        tab1.addComponent(this.peptideList);

        values2.clear();
        decoyFormat = new HorizontalLabelDropDounList("Decoy Format");
        values2.add("none");
        values2.add("shuffle");
        values2.add("peptide-reverse");
        values2.add("protein-reverse");
        decoyFormat.updateData(values2);
        tab1.addComponent(this.decoyFormat);

        values2.clear();
        values2.add("N");
        values2.add("C");
        values2.add("NC");
        values2.add("none");
        keepTerminalAAs = new HorizontalLabelDropDounList("Keep Terminal AAs");
        keepTerminalAAs.updateData(values2);
        tab1.addComponent(this.keepTerminalAAs);

        this.decoySeed = new HorizontalLabelTextField("Decoy Seed", 0, new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab1.addComponent(this.decoySeed);
        removeTempFolder = new HorizontalLabelDropDounList("Remove Temp Folder");
        removeTempFolder.updateData(values);
        tab1.addComponent(this.removeTempFolder);

        /*tab 2*/
        VerticalLayout tab2 = new VerticalLayout();
        subContainer.addTab(tab2, "Search");
        calculateExactPValue = new HorizontalLabelDropDounList("Calculate Exact p-value");
        calculateExactPValue.updateData(values);
        tab2.addComponent(this.calculateExactPValue);

        calculateSPScore = new HorizontalLabelDropDounList("Calculate SP Score");
        calculateSPScore.updateData(values);
        tab2.addComponent(this.calculateSPScore);

        this.spectrumMZ = new HorizontalLabel2TextField("Spectrum m/z (min-max)", 0.0, 0.0, new DoubleRangeValidator("Only double values allowed", (-1* Double.MAX_VALUE), Double.MAX_VALUE));
        tab2.addComponent(spectrumMZ);
        spectrumMZ.setRequired(false);

        this.minimumNumberofPeaks = new HorizontalLabelTextField("Minimum Number of Peaks", 0, new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab2.addComponent(minimumNumberofPeaks);

        values2.clear();
        values2.add("1");
        values2.add("2");
        values2.add("3");
        values2.add("all");
        charges = new HorizontalLabelDropDounList("Charges");
        charges.updateData(values2);
        tab2.addComponent(this.charges);

        removePrecursorPeakPP = new HorizontalLabelDropDounList("Remove Precursor Peak (PP)");
        removePrecursorPeakPP.updateData(values);
        tab2.addComponent(this.removePrecursorPeakPP);

        this.removePPTolerance = new HorizontalLabelTextField("Remove PP Tolerance (in Da)", 0.0, new DoubleRangeValidator("Only postive double value allowed ", 0.0, Double.MAX_VALUE));
        tab2.addComponent(removePPTolerance);

        useFlankingPeaks = new HorizontalLabelDropDounList("Use Flanking Peaks");
        useFlankingPeaks.updateData(values);
        tab2.addComponent(this.useFlankingPeaks);

        useNeutralLossPeaks = new HorizontalLabelDropDounList("Use Neutral Loss Peaks");
        useNeutralLossPeaks.updateData(values);
        tab2.addComponent(this.useNeutralLossPeaks);

        this.MZBinWidth = new HorizontalLabelTextField("m/z Bin Width", 0.0, new DoubleRangeValidator("Only postive double value allowed ", 0.0, Double.MAX_VALUE));
        tab2.addComponent(MZBinWidth);

        this.MZBinOffset = new HorizontalLabelTextField("m/z Bin Offset", 0.0, new DoubleRangeValidator("Only postive double value allowed ", 0.0, Double.MAX_VALUE));
        tab2.addComponent(MZBinOffset);

        this.numberOfSpectrumMatches = new HorizontalLabelTextField("Number of Spectrum Matches", 0, new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab2.addComponent(numberOfSpectrumMatches);

        values2.clear();
        values2.add("Text");
        values2.add("SQT");
        values2.add("pepxml");
        values2.add("mzIdentML");
        values2.add("Percolator input file");
        outputFormat = new HorizontalLabelDropDounList("Output Format");
        outputFormat.updateData(values2);
        tab2.addComponent(this.outputFormat);
        outputFormat.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                String selectedValue = outputFormat.getSelectedValue();
                if (!selectedValue.equalsIgnoreCase("Text")) {
                    Notification.show("Note that the Tide " + selectedValue + " format is not compatible with PeptideShaker", Notification.Type.TRAY_NOTIFICATION);
                }
            }
        });

        String helpText = "<a href='http://crux.ms/' targe='_blank'>";
        Help help = new Help(helpText, "<font style='line-height: 20px;'>Click to open the Tide help page.</font>",100,20);
        container.addComponent(help, "left:20px;bottom:10px;");
        Button okBtn = new Button("OK");
        okBtn.setWidth(76, Unit.PIXELS);
        okBtn.setHeight(20, Unit.PIXELS);
        okBtn.setStyleName(ValoTheme.BUTTON_TINY);
        okBtn.addClickListener((Button.ClickEvent event) -> {
            if (peptideLength.isValid() && precursorMass.isValid() && maxVariablePTMsPerPeptide.isValid() && decoySeed.isValid() && spectrumMZ.isValid() && removePPTolerance.isValid() && minimumNumberofPeaks.isValid() && MZBinWidth.isValid() && MZBinOffset.isValid() && numberOfSpectrumMatches.isValid()) {
                updateParameters();
                setPopupVisible(false);
            }
        });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyleName(ValoTheme.BUTTON_TINY);
        cancelBtn.setWidth(76, Unit.PIXELS);
        cancelBtn.setHeight(20, Unit.PIXELS);

        cancelBtn.addClickListener((Button.ClickEvent event) -> {
            TideAdvancedSettingsPanel.this.setPopupVisible(false);
        });

        container.addComponent(okBtn, "bottom:10px;right:10px");
        container.addComponent(cancelBtn, "bottom:10px;right:96px");
    }

    public void updateGUI(IdentificationParameters webSearchParameters) {
        this.webSearchParameters = webSearchParameters;
//        this.refModificationSelection.clear();
        TideParameters oldTideParameters = (TideParameters) this.webSearchParameters.getSearchParameters().getIdentificationAlgorithmParameter(Advocate.tide.getIndex());
        peptideLength.setFirstSelectedValue(oldTideParameters.getMinPeptideLength());
        peptideLength.setSecondSelectedValue(oldTideParameters.getMaxPeptideLength());
        precursorMass.setFirstSelectedValue(oldTideParameters.getMinPrecursorMass());
        precursorMass.setSecondSelectedValue(oldTideParameters.getMaxPrecursorMass());

        if (oldTideParameters.getMonoisotopicPrecursor()) {
            monoisotopicPrecursor.setSelected("Yes");
        } else {
            monoisotopicPrecursor.setSelected("No");
        }
        if (oldTideParameters.getClipNtermMethionine()) {
            removeStartingMethionine.setSelected("Yes");
        } else {
            removeStartingMethionine.setSelected("No");
        }

        maxVariablePTMsPerPeptide.setSelectedValue(oldTideParameters.getMaxVariableModificationsPerPeptide());
        maxVariablePTMsPerType.setSelectedValue(oldTideParameters.getMaxVariableModificationsPerTypePerPeptide());

        enzymeType.setSelected(oldTideParameters.getDigestionType());
        if (oldTideParameters.getPrintPeptides()) {
            peptideList.setSelected("Yes");
        } else {
            peptideList.setSelected("No");
        }
        decoyFormat.setSelected(oldTideParameters.getDecoyFormat());
        keepTerminalAAs.setSelected(oldTideParameters.getKeepTerminalAminoAcids());
        decoySeed.setSelectedValue(oldTideParameters.getDecoySeed());
        if (oldTideParameters.getRemoveTempFolders()) {
            removeTempFolder.setSelected("Yes");
        } else {
            removeTempFolder.setSelected("No");
        }

        if (oldTideParameters.getComputeExactPValues()) {
            calculateExactPValue.setSelected("Yes");
        } else {
            calculateExactPValue.setSelected("No");
        }
        if (oldTideParameters.getComputeSpScore()) {
            calculateSPScore.setSelected("Yes");
        } else {
            calculateSPScore.setSelected("No");
        }
        spectrumMZ.setFirstSelectedValue(oldTideParameters.getMinSpectrumMz());
        spectrumMZ.setSecondSelectedValue(oldTideParameters.getMaxSpectrumMz());
        minimumNumberofPeaks.setSelectedValue(oldTideParameters.getMinSpectrumPeaks());
        charges.setSelected(oldTideParameters.getSpectrumCharges());
        if (oldTideParameters.getRemovePrecursor()) {
            removePrecursorPeakPP.setSelected("Yes");
        } else {
            removePrecursorPeakPP.setSelected("No");
        }
        removePPTolerance.setSelectedValue(oldTideParameters.getRemovePrecursorTolerance());

        if (oldTideParameters.getUseFlankingPeaks()) {
            useFlankingPeaks.setSelected("Yes");
        } else {
            useFlankingPeaks.setSelected("No");
        }
        if (oldTideParameters.getUseNeutralLossPeaks()) {
            useNeutralLossPeaks.setSelected("Yes");
        } else {
            useNeutralLossPeaks.setSelected("No");
        }
        MZBinWidth.setSelectedValue(oldTideParameters.getMzBinWidth());
        MZBinOffset.setSelectedValue(oldTideParameters.getMzBinOffset());

        numberOfSpectrumMatches.setSelectedValue(oldTideParameters.getNumberOfSpectrumMatches());

        if (oldTideParameters.getTextOutput()) {
            outputFormat.setSelected("text");
        } else if (oldTideParameters.getSqtOutput()) {
            outputFormat.setSelected("SQT");
        } else if (oldTideParameters.getPepXmlOutput()) {
            outputFormat.setSelected("pepxml");
        } else if (oldTideParameters.getMzidOutput()) {
            outputFormat.setSelected("mzIdentML");
        } else if (oldTideParameters.getPinOutput()) {
            outputFormat.setSelected("Percolator input file");
        }
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

        TideParameters oldTideParameters = (TideParameters) this.webSearchParameters.getSearchParameters().getIdentificationAlgorithmParameter(Advocate.tide.getIndex());
        oldTideParameters.setMinPeptideLength(Integer.valueOf(peptideLength.getFirstSelectedValue()));
        oldTideParameters.setMaxPeptideLength(Integer.valueOf(peptideLength.getSecondSelectedValue()));
        oldTideParameters.setMinPrecursorMass(Double.valueOf(precursorMass.getFirstSelectedValue()));
        oldTideParameters.setMaxPrecursorMass(Double.valueOf(precursorMass.getSecondSelectedValue()));
        oldTideParameters.setMonoisotopicPrecursor(monoisotopicPrecursor.getSelectedValue().equalsIgnoreCase("Yes"));
        oldTideParameters.setClipNtermMethionine(removeStartingMethionine.getSelectedValue().equalsIgnoreCase("Yes"));
        oldTideParameters.setMaxVariableModificationsPerPeptide(Integer.valueOf(maxVariablePTMsPerPeptide.getSelectedValue()));
        oldTideParameters.setMaxVariableModificationsPerTypePerPeptide(Integer.valueOf(maxVariablePTMsPerType.getSelectedValue()));
        oldTideParameters.setDigestionType(enzymeType.getSelectedValue());
        oldTideParameters.setPrintPeptides(peptideList.getSelectedValue().equalsIgnoreCase("Yes"));
        oldTideParameters.setDecoyFormat(decoyFormat.getSelectedValue());
        oldTideParameters.setKeepTerminalAminoAcids(keepTerminalAAs.getSelectedValue());
        oldTideParameters.setDecoySeed(Integer.valueOf(decoySeed.getSelectedValue()));
        oldTideParameters.setRemoveTempFolders(removeTempFolder.getSelectedValue().equalsIgnoreCase("Yes"));
        oldTideParameters.setComputeExactPValues(calculateExactPValue.getSelectedValue().equalsIgnoreCase("Yes"));
        oldTideParameters.setComputeSpScore(calculateSPScore.getSelectedValue().equalsIgnoreCase("Yes"));

        oldTideParameters.setMinSpectrumMz(Double.valueOf(spectrumMZ.getFirstSelectedValue()));
        oldTideParameters.setMaxSpectrumMz(Double.valueOf(spectrumMZ.getSecondSelectedValue()));
        oldTideParameters.setMinSpectrumPeaks(Integer.valueOf(minimumNumberofPeaks.getSelectedValue()));
        oldTideParameters.setSpectrumCharges(charges.getSelectedValue());
        oldTideParameters.setRemovePrecursor(removePrecursorPeakPP.getSelectedValue().equalsIgnoreCase("Yes"));
        oldTideParameters.setRemovePrecursorTolerance(Double.valueOf(removePPTolerance.getSelectedValue()));
        oldTideParameters.setUseFlankingPeaks(useFlankingPeaks.getSelectedValue().equalsIgnoreCase("Yes"));
        oldTideParameters.setUseNeutralLossPeaks(useNeutralLossPeaks.getSelectedValue().equalsIgnoreCase("Yes"));
        oldTideParameters.setMzBinWidth(Double.valueOf(MZBinWidth.getSelectedValue()));
        oldTideParameters.setMzBinOffset(Double.valueOf(MZBinOffset.getSelectedValue()));
        oldTideParameters.setNumberOfSpectrumMatches(Integer.valueOf(numberOfSpectrumMatches.getSelectedValue()));
        String textOutput = outputFormat.getSelectedValue();

        oldTideParameters.setTextOutput(textOutput.equalsIgnoreCase("text"));
        oldTideParameters.setSqtOutput(textOutput.equalsIgnoreCase("SQT"));
        oldTideParameters.setPepXmlOutput(textOutput.equalsIgnoreCase("pepxml"));
        oldTideParameters.setMzidOutput(textOutput.equalsIgnoreCase("mzIdentML"));
        oldTideParameters.setPinOutput(textOutput.equalsIgnoreCase("Percolator input file"));

    }

}
