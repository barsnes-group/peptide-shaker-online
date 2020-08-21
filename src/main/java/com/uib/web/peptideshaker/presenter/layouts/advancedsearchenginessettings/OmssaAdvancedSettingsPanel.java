package com.uib.web.peptideshaker.presenter.layouts.advancedsearchenginessettings;

import com.compomics.util.experiment.identification.Advocate;
import com.compomics.util.parameters.identification.IdentificationParameters;
import com.compomics.util.parameters.identification.tool_specific.OmssaParameters;
import com.uib.web.peptideshaker.presenter.core.Help;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabel2TextField;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabelDropDownList;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabelTextField;
import com.vaadin.data.Property;
import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author y-mok
 */
public class OmssaAdvancedSettingsPanel extends PopupWindow {

    private final HorizontalLabelTextField lowIntensityCutoff;
    private final HorizontalLabelTextField highIntensityCutoff;
    private final HorizontalLabelTextField intensityCutoffIncrement;
    private final HorizontalLabelTextField minimalNumberofPeaks;
    private final HorizontalLabelDropDownList eliminateChargeReducedPrecursorsinSpectra;
    private final HorizontalLabelDropDownList precursorChargeEstimation;
    private final HorizontalLabelDropDownList plusOneChargeEstimatedAlgorithmically;
    private final HorizontalLabelTextField fractionofPrecursorsMZforChargeOneEstimation;
    private final HorizontalLabelTextField minimalNumberofPrecursorsperSpectrum;
    private final HorizontalLabelDropDownList precursorMassScaling;

    private final HorizontalLabelDropDownList sequencesMappinginMemory;
    private final HorizontalLabelDropDownList cleaveNTerminalMethionine;

    private final HorizontalLabelTextField minimumPrecursorChargeforMultiplyChargedFragments;
    private final HorizontalLabelTextField massThresholdtoConsiderExactNeutronMass;
    private final HorizontalLabelTextField singlyChargedWindowWidth;
    private final HorizontalLabelTextField doublyChargedWindowWidth;
    private final HorizontalLabelTextField numberofPeaksinDoublyChargedWindow;
    private final HorizontalLabelTextField numberofPeaksinSinglyChargedWindow;
    private final HorizontalLabelTextField minimumAnnotatedPeaksAmongtheMostIntenseOnes;
    private final HorizontalLabelTextField minimumNumberofAnnotaedPeaks;
    private final HorizontalLabelTextField maximumMZLadders;
    private final HorizontalLabelTextField maximumFragmentCharge;
    private final HorizontalLabelDropDownList searchPostiveIons;
    private final HorizontalLabelDropDownList searchFirstForwardIon_b1;
    private final HorizontalLabelDropDownList searchRewind_c_terminal_Ions;
    private final HorizontalLabelTextField maximumFragmentsperSeries;
    private final HorizontalLabelDropDownList useCorrelationScore;
    private final HorizontalLabelTextField consectiveIonProbability;
    private final HorizontalLabelTextField numberofHitsPerSpectrumperCharge;

    private final HorizontalLabelTextField evalueCutoffforSequences;
    private final HorizontalLabelTextField evalueCutoffforSpectra;
    private final HorizontalLabelTextField evalueCutofftoReplaceaHit;

    private final HorizontalLabel2TextField peptideLength;

    private final HorizontalLabelTextField evalueCutoff;
    private final HorizontalLabelTextField maximumHitListLength;
    private final HorizontalLabelDropDownList omssaOutputFormat;

    private IdentificationParameters webSearchParameters;

    public OmssaAdvancedSettingsPanel() {
        super(VaadinIcons.COG.getHtml() + " OMSSA Advanced Settings");

        AbsoluteLayout container = new AbsoluteLayout();
        container.setStyleName("popuppanelmaincontainer");
        container.setWidth(500, Unit.PIXELS);
        container.setHeight(600, Unit.PIXELS);
        Label title = new Label("OMSSA");
        container.addComponent(title, "left:10px;top:10px");
        OmssaAdvancedSettingsPanel.this.setContent(container);
        OmssaAdvancedSettingsPanel.this.setClosable(true);

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

        this.lowIntensityCutoff = new HorizontalLabelTextField("Low Intensity Cutoff (percent of most intense peak)", 0.0, new DoubleRangeValidator("Only positive double allowed", 0.0, Double.MAX_VALUE));
        tab1.addComponent(lowIntensityCutoff);
        lowIntensityCutoff.updateExpandingRatio(0.7f, 0.3f);
        this.highIntensityCutoff = new HorizontalLabelTextField("High Intensity Cutoff (percent of most intense peak)", 0.0, new DoubleRangeValidator("Only positive double allowed", 0.0, Double.MAX_VALUE));
        tab1.addComponent(highIntensityCutoff);
        highIntensityCutoff.updateExpandingRatio(0.7f, 0.3f);
        this.intensityCutoffIncrement = new HorizontalLabelTextField("Intensity Cutoff Increment", 0.0, new DoubleRangeValidator("Only postive double value allowed ", 0.0, Double.MAX_VALUE));
        tab1.addComponent(intensityCutoffIncrement);
        intensityCutoffIncrement.updateExpandingRatio(0.7f, 0.3f);

        this.minimalNumberofPeaks = new HorizontalLabelTextField("Minimal Number of Peaks", 0, new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab1.addComponent(minimalNumberofPeaks);
        minimalNumberofPeaks.updateExpandingRatio(0.7f, 0.3f);

        Set<String> values = new LinkedHashSet<>();
        values.add("Yes");
        values.add("No");
        this.eliminateChargeReducedPrecursorsinSpectra = new HorizontalLabelDropDownList("Eliminate Charge Reduced Precursors in Spectra");
        this.eliminateChargeReducedPrecursorsinSpectra.updateData(values);
        eliminateChargeReducedPrecursorsinSpectra.updateExpandingRatio(0.7f, 0.3f);
        tab1.addComponent(this.eliminateChargeReducedPrecursorsinSpectra);
        Set<String> values2 = new LinkedHashSet<>();
        values2.add("Use Range");
        values2.add("Believe Input File");
        precursorChargeEstimation = new HorizontalLabelDropDownList("Precursor Charge Estimation");
        precursorChargeEstimation.updateData(values2);
        precursorChargeEstimation.updateExpandingRatio(0.7f, 0.3f);
        tab1.addComponent(this.precursorChargeEstimation);
        plusOneChargeEstimatedAlgorithmically = new HorizontalLabelDropDownList("Plus One Charge Estimated Algorithmically");
        plusOneChargeEstimatedAlgorithmically.updateData(values);
        plusOneChargeEstimatedAlgorithmically.updateExpandingRatio(0.7f, 0.3f);
        tab1.addComponent(this.plusOneChargeEstimatedAlgorithmically);
        this.fractionofPrecursorsMZforChargeOneEstimation = new HorizontalLabelTextField("Fraction of Precursors m/z for Charge One Estimation", 0.0, new DoubleRangeValidator("Only postive double values allowed", 0.0, Double.MAX_VALUE));
        fractionofPrecursorsMZforChargeOneEstimation.updateExpandingRatio(0.7f, 0.3f);
        tab1.addComponent(this.fractionofPrecursorsMZforChargeOneEstimation);
        this.minimalNumberofPrecursorsperSpectrum = new HorizontalLabelTextField("Minimal Number of Precursors per Spectrum", 0, new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab1.addComponent(minimalNumberofPrecursorsperSpectrum);
        minimalNumberofPrecursorsperSpectrum.updateExpandingRatio(0.7f, 0.3f);
        precursorMassScaling = new HorizontalLabelDropDownList("Precursor Mass Scaling");
        precursorMassScaling.updateData(values);
        precursorMassScaling.updateExpandingRatio(0.7f, 0.3f);
        tab1.addComponent(this.precursorMassScaling);


        /*tab 2*/
        VerticalLayout tab2 = new VerticalLayout();
        subContainer.addTab(tab2, "Database");
        sequencesMappinginMemory = new HorizontalLabelDropDownList("Sequences Mapping in Memory");
        sequencesMappinginMemory.updateData(values);
        sequencesMappinginMemory.updateExpandingRatio(0.7f, 0.3f);
        tab2.addComponent(this.sequencesMappinginMemory);
        cleaveNTerminalMethionine = new HorizontalLabelDropDownList("Cleave N-Terminal Methionine");
        cleaveNTerminalMethionine.updateData(values);
        cleaveNTerminalMethionine.updateExpandingRatio(0.7f, 0.3f);
        tab2.addComponent(this.cleaveNTerminalMethionine);

        /*tab 3*/
        VerticalLayout tab3 = new VerticalLayout();
        subContainer.addTab(tab3, "Search");
        this.minimumPrecursorChargeforMultiplyChargedFragments = new HorizontalLabelTextField("Minimum Precursor Charge for Multiply Charged Fragmentse", 0, new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab3.addComponent(minimumPrecursorChargeforMultiplyChargedFragments);
        minimumPrecursorChargeforMultiplyChargedFragments.updateExpandingRatio(0.7f, 0.3f);
        this.massThresholdtoConsiderExactNeutronMass = new HorizontalLabelTextField("Mass Threshold to Consider Exact Neutron Mass", 0.0, new DoubleRangeValidator("Only postive double values allowed", 0.0, Double.MAX_VALUE));
        tab3.addComponent(massThresholdtoConsiderExactNeutronMass);
        massThresholdtoConsiderExactNeutronMass.updateExpandingRatio(0.7f, 0.3f);

        this.singlyChargedWindowWidth = new HorizontalLabelTextField("Singly Charged Window Width (Da)", 0, new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab3.addComponent(singlyChargedWindowWidth);
        singlyChargedWindowWidth.updateExpandingRatio(0.7f, 0.3f);

        this.doublyChargedWindowWidth = new HorizontalLabelTextField("Doubly Charged Window Width (Da)", 0, new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab3.addComponent(doublyChargedWindowWidth);
        doublyChargedWindowWidth.updateExpandingRatio(0.7f, 0.3f);

        this.numberofPeaksinSinglyChargedWindow = new HorizontalLabelTextField("Number of Peaks in Singly Charged Window", 0, new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab3.addComponent(numberofPeaksinSinglyChargedWindow);
        numberofPeaksinSinglyChargedWindow.updateExpandingRatio(0.7f, 0.3f);

        this.numberofPeaksinDoublyChargedWindow = new HorizontalLabelTextField("Number of Peaks in Doubly Charged Window", 0, new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab3.addComponent(numberofPeaksinDoublyChargedWindow);
        numberofPeaksinDoublyChargedWindow.updateExpandingRatio(0.7f, 0.3f);

        this.minimumAnnotatedPeaksAmongtheMostIntenseOnes = new HorizontalLabelTextField("Minimum Annotated Peaks Among the Most Intense Ones", 0, new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab3.addComponent(minimumAnnotatedPeaksAmongtheMostIntenseOnes);
        minimumAnnotatedPeaksAmongtheMostIntenseOnes.updateExpandingRatio(0.7f, 0.3f);

        this.minimumNumberofAnnotaedPeaks = new HorizontalLabelTextField("Minimum Number of Annotated Peaks", 0, new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab3.addComponent(minimumNumberofAnnotaedPeaks);
        minimumNumberofAnnotaedPeaks.updateExpandingRatio(0.7f, 0.3f);

        this.maximumMZLadders = new HorizontalLabelTextField("Maximum m/z Ladders", 0, new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab3.addComponent(maximumMZLadders);
        maximumMZLadders.updateExpandingRatio(0.7f, 0.3f);

        this.maximumFragmentCharge = new HorizontalLabelTextField("Maximum Fragment Charge", 0, new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab3.addComponent(maximumFragmentCharge);
        maximumFragmentCharge.updateExpandingRatio(0.7f, 0.3f);

        searchPostiveIons = new HorizontalLabelDropDownList("Search Postive Ions");
        searchPostiveIons.updateData(values);
        tab3.addComponent(this.searchPostiveIons);
        searchPostiveIons.updateExpandingRatio(0.7f, 0.3f);

        searchFirstForwardIon_b1 = new HorizontalLabelDropDownList("Search First ForwardIon (b1)");
        searchFirstForwardIon_b1.updateData(values);
        tab3.addComponent(this.searchFirstForwardIon_b1);
        searchFirstForwardIon_b1.updateExpandingRatio(0.7f, 0.3f);

        searchRewind_c_terminal_Ions = new HorizontalLabelDropDownList("Search Rewind (C-Terminal) Ions");
        searchRewind_c_terminal_Ions.updateData(values);
        tab3.addComponent(this.searchRewind_c_terminal_Ions);
        searchRewind_c_terminal_Ions.updateExpandingRatio(0.7f, 0.3f);

        this.maximumFragmentsperSeries = new HorizontalLabelTextField("Maximum Fragments per Series", 0, new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab3.addComponent(maximumFragmentsperSeries);
        maximumFragmentsperSeries.updateExpandingRatio(0.7f, 0.3f);
        this.useCorrelationScore = new HorizontalLabelDropDownList("Use Correlation Score");
        useCorrelationScore.updateData(values);
        useCorrelationScore.updateExpandingRatio(0.7f, 0.3f);
        tab3.addComponent(this.useCorrelationScore);

        this.consectiveIonProbability = new HorizontalLabelTextField("Consective Ion Probability", 0.0, new DoubleRangeValidator("Only postive double values allowed", 0.0, Double.MAX_VALUE));
        tab3.addComponent(this.consectiveIonProbability);
        consectiveIonProbability.updateExpandingRatio(0.7f, 0.3f);
        this.numberofHitsPerSpectrumperCharge = new HorizontalLabelTextField("Number of Hits per Spectrum per Charge", 0, new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab3.addComponent(numberofHitsPerSpectrumperCharge);
        numberofHitsPerSpectrumperCharge.updateExpandingRatio(0.7f, 0.3f);

        /*tab 4*/
        VerticalLayout tab4 = new VerticalLayout();
        subContainer.addTab(tab4, "Iterative Search");

        this.evalueCutoffforSequences = new HorizontalLabelTextField("E-value Cutoff for Sequences (0 means all)", 0.0, new DoubleRangeValidator("Only postive double values allowed", 0.0, Double.MAX_VALUE));
        tab4.addComponent(this.evalueCutoffforSequences);
        evalueCutoffforSequences.updateExpandingRatio(0.7f, 0.3f);
        this.evalueCutoffforSpectra = new HorizontalLabelTextField("E-value Cutoff for Spectra (0 means all", 0.0, new DoubleRangeValidator("Only postive double values allowed", 0.0, Double.MAX_VALUE));
        tab4.addComponent(this.evalueCutoffforSpectra);
        evalueCutoffforSpectra.updateExpandingRatio(0.7f, 0.3f);
        this.evalueCutofftoReplaceaHit = new HorizontalLabelTextField("E-value Cutoff to Replace a Hit (o means keep best)", 0.0, new DoubleRangeValidator("Only postive double values allowed", 0.0, Double.MAX_VALUE));
        tab4.addComponent(this.evalueCutofftoReplaceaHit);
        evalueCutofftoReplaceaHit.updateExpandingRatio(0.7f, 0.3f);
        /*tab 5*/
        VerticalLayout tab5 = new VerticalLayout();
        subContainer.addTab(tab5, "Semi-Enzymatic");
        this.peptideLength = new HorizontalLabel2TextField("Peptide Length (min-max)", 0, 0, new IntegerRangeValidator("Only integer number allowed ", (-1 * Integer.MAX_VALUE), Integer.MAX_VALUE));
        tab5.addComponent(peptideLength);


        /*tab 6*/
        VerticalLayout tab6 = new VerticalLayout();
        subContainer.addTab(tab6, "Output");

        this.evalueCutoff = new HorizontalLabelTextField("E-value Cutoff", 0.0, new DoubleRangeValidator("Only postive double values allowed", 0.0, Double.MAX_VALUE));
        tab6.addComponent(this.evalueCutoff);
        evalueCutoff.updateExpandingRatio(0.7f, 0.3f);

        this.maximumHitListLength = new HorizontalLabelTextField("Maximum HitList Length", 0, new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab6.addComponent(maximumHitListLength);
        maximumHitListLength.updateExpandingRatio(0.7f, 0.3f);
        values2.clear();
        values2.add("OMX");
        values2.add("CSV");
        values2.add("pepXML");
        omssaOutputFormat = new HorizontalLabelDropDownList("OMSSA Output Format");
        omssaOutputFormat.updateData(values2);
//        tab2.addComponent(this.omssaOutputFormat);

        omssaOutputFormat.addValueChangeListener((Property.ValueChangeEvent event) -> {
            String selectedValue = searchFirstForwardIon_b1.getSelectedValue();
            if (!selectedValue.equalsIgnoreCase("OMX")) {
                Notification.show("Note that the OMSSA " + selectedValue + " format is not compatible with PeptideShaker", Notification.Type.TRAY_NOTIFICATION);
            }
        });
        tab6.addComponent(this.omssaOutputFormat);
        omssaOutputFormat.updateExpandingRatio(0.7f, 0.3f);

        Button okBtn = new Button("OK");
        okBtn.setWidth(76, Unit.PIXELS);
        okBtn.setHeight(20, Unit.PIXELS);
        okBtn.setStyleName(ValoTheme.BUTTON_TINY);
        okBtn.addClickListener((Button.ClickEvent event) -> {
            if (lowIntensityCutoff.isValid() && highIntensityCutoff.isValid() && intensityCutoffIncrement.isValid() && minimalNumberofPeaks.isValid() && fractionofPrecursorsMZforChargeOneEstimation.isValid() && minimalNumberofPrecursorsperSpectrum.isValid() && minimumPrecursorChargeforMultiplyChargedFragments.isValid() && singlyChargedWindowWidth.isValid() && massThresholdtoConsiderExactNeutronMass.isValid() && doublyChargedWindowWidth.isValid() && numberofPeaksinSinglyChargedWindow.isValid()
                    && numberofPeaksinDoublyChargedWindow.isValid() && minimumAnnotatedPeaksAmongtheMostIntenseOnes.isValid() && minimumNumberofAnnotaedPeaks.isValid() && maximumMZLadders.isValid() && maximumFragmentCharge.isValid() && searchPostiveIons.isValid()
                    && maximumFragmentsperSeries.isValid() && consectiveIonProbability.isValid() && numberofHitsPerSpectrumperCharge.isValid() && evalueCutoff.isValid() && evalueCutoffforSequences.isValid()
                    && evalueCutoffforSpectra.isValid() && evalueCutofftoReplaceaHit.isValid() && peptideLength.isValid() && maximumHitListLength.isValid()) {
                updateParameters();
                setPopupVisible(false);
            }
        });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyleName(ValoTheme.BUTTON_TINY);
        cancelBtn.setWidth(76, Unit.PIXELS);
        cancelBtn.setHeight(20, Unit.PIXELS);

        cancelBtn.addClickListener((Button.ClickEvent event) -> {
            OmssaAdvancedSettingsPanel.this.setPopupVisible(false);
        });

        String helpText = "<h1>OMSSA Advanced Settings</h1><div style='overflow: auto;  max-height: 452px; padding-right: 19px;width: 467px;'>"
                + "<p align=\"justify\">\n"
                + "            The Open Mass Spectrometry Search Engine (<a href=\"http://www.ncbi.nlm.nih.gov/pubmed/15473683\">OMSSA</a>) comes with many options for \n"
                + "            fine tuning the search settings. Note that these settings aim to be used by advanced users only. It is highly recommended to carefully test any \n"
                + "            changes from the default settings, to document them and report them in any publication/report.\n"
                + "            <br><br>\n"
                + "            Running OMSSA with the -help option will display all details of the available settings. If you are missing a parameter, please contact the developers.\n"
                + "        </p>\n"
                + "\n"
                + "        <H3>Spectrum</H3>\n"
                + "        The <i>Spectrum</i> category groups all settings related to the spectrum preprocessing:\n"
                + "        <ul>\n"
                + "            <li>Low intensity cutoff as fraction of max peak (-cl command line option)</li>\n"
                + "            <li>High intensity cutoff as fraction of max peak (-ch command line option)</li>\n"
                + "            <li>Intensity cutoff increment as fraction of max peak (-ci command line option)</li>\n"
                + "            <li>The minimum number of m/< values a spectrum must have to be searched (-hm command line option)</li>\n"
                + "            <li>Eliminate charge reduced precursors in spectra (-cp command line option)</li>\n"
                + "            <li>How should precursor charges be determined? (-zcc command line option)</li>\n"
                + "            <li>Should charge plus one be determined algorithmically? (-zc command line option)</li>\n"
                + "            <li>Fraction of peaks below precursor used to determine if spectrum is charge 1 (-z1 command line option)</li>\n"
                + "            <li>Minimum number of precursors that match a spectrum (-pc command line option)</li>\n"
                + "            <li>Charge dependency of precursor mass tolerance (-tez command line option)</li>\n"
                + "        </ul>\n"
                + "\n"
                + "        <H3>Database</H3>\n"
                + "        The <i>Database</i> category groups all settings related to the database preprocessing:\n"
                + "        <ul>\n"
                + "            <li>Use memory mapped sequence libraries (-umm command line option)</li>\n"
                + "            <li>N-term methionine should be cleaved (-mnm command line option)</li>\n"
                + "        </ul>\n"
                + "\n"
                + "        <H3>Search</H3>\n"
                + "        The <i>Search</i> category groups all settings related to the search itself:\n"
                + "        <ul>\n"
                + "            <li>Minimum precursor charge to start considering multiply charged products (-zt command line option)</li>\n"
                + "            <li>When doing multi isotope search, the number of isotopic peaks to search (0 equals monoisotopic peak only, -mnm command line option)</li>\n"
                + "            <li>Threshold in Da above which the mass of neutron should be added in exact mass search (-tex command line option)</li>\n"
                + "            <li>Single charge window in Da (-w1 command line option)</li>\n"
                + "            <li>Double charge window in Da (-w2 command line option)</li>\n"
                + "            <li>Number of peaks allowed in single charge window (0 equals number of ion species, -h1 command line option)</li>\n"
                + "            <li>Number of peaks allowed in double charge window (0 equals number of ion species, -h2 command line option)</li>\n"
                + "            <li>Number of m/z values corresponding to the most intense peaks that must include one match to the theoretical peptide (-ht command line option)</li>\n"
                + "            <li>The minimum number of m/z values a spectrum must have to be searched (-hs command line option)</li>\n"
                + "            <li>The maximum number of mass ladders to generate per database peptide (-mm command line option)</li>\n"
                + "            <li>Maximum product charge to search (-zoh command line option)</li>\n"
                + "            <li>Search using negative or positive ions (-zn command line option)</li>\n"
                + "            <li>Should first forward (b1) product ions be in search (-sb1 command line option)</li>\n"
                + "            <li>Should c terminus ions be searched (-sct command line option)</li>\n"
                + "            <li>Max number of ions in search series being searched (-sp command line option)</li>\n"
                + "            <li>Turn off correlation correction score (-scorr command line option)</li>\n"
                + "            <li>Probability of consecutive ion (used in correlation correction, -scorp command line option)</li>\n"
                + "            <li>Number of hits retained per precursor charge state per spectrum during the search (-hl command line option)</li>\n"
                + "        </ul>\n"
                + "\n"
                + "        <H3>Iterative Search</H3>\n"
                + "        The <i>Iterative Search</i> category groups all settings related to the thresholding of the iterative search:\n"
                + "        <ul>\n"
                + "            <li>E-value threshold to include sequences in the iterative search (0 equals all, -is command line option)</li>\n"
                + "            <li>E-value threshold to iteratively search a spectrum again (0 equals always, -ii command line option)</li>\n"
                + "            <li>E-value threshold to replace a hit (0 equals only if better, -ir command line option)</li>\n"
                + "        </ul>\n"
                + "\n"
                + "        <H3>Semi-Enzymatic</H3>\n"
                + "        The <i>Semi-Enzymatic</i> category groups all settings related to the use of OMSSA in no-enzyme and semi-tryptic searches mode:\n"
                + "        <ul>\n"
                + "            <li>Minimum and maximum sizes of peptides for no-enzyme and semi-tryptic searches (0 equals none, -no and -nox command line options)</li>\n"
                + "        </ul>\n"
                + "\n"
                + "        <H3>Output</H3>\n"
                + "        The <i>Output</i> category groups all settings related to the file output:\n"
                + "        <ul>\n"
                + "            <li>The maximum evalue allowed in the hit list (for accurate FNR estimation keep this value as high as possible, -he command line option)</li>\n"
                + "            <li>Maximum number of hits reported per spectrum (0 equals all, for accurate PTM scoring retain all, -hc command line option)</li>\n"
                + "            <li>OMSSA output format (for compatibility with PeptideShaker use omx, -ox or -oc command line option)</li>\n"
                + "        </ul></div>";
        Help help = new Help(helpText, "Note: The advanced settings are for expert use only. See help for details", 100, 20);
        container.addComponent(help, "left:20px;bottom:10px;");
        container.addComponent(cancelBtn, "bottom:10px;right:10px");
        container.addComponent(okBtn, "bottom:10px;right:96px");
    }

    public void updateGUI(IdentificationParameters webSearchParameters) {
        this.webSearchParameters = webSearchParameters;

        OmssaParameters oldOmssaParameters = (OmssaParameters) this.webSearchParameters.getSearchParameters().getIdentificationAlgorithmParameter(Advocate.omssa.getIndex());
        lowIntensityCutoff.setSelectedValue(oldOmssaParameters.getLowIntensityCutOff());
        highIntensityCutoff.setSelectedValue(oldOmssaParameters.getHighIntensityCutOff());
        intensityCutoffIncrement.setSelectedValue(oldOmssaParameters.getIntensityCutOffIncrement());
        if (oldOmssaParameters.isRemovePrecursor()) {
            eliminateChargeReducedPrecursorsinSpectra.setSelected("Yes");
        } else {
            eliminateChargeReducedPrecursorsinSpectra.setSelected("No");
        }
        if (oldOmssaParameters.isEstimateCharge()) {
            precursorChargeEstimation.setSelected("Use Range");
        } else {
            precursorChargeEstimation.setSelected("Believe Input File");
        }
        if (oldOmssaParameters.isDetermineChargePlusOneAlgorithmically()) {
            plusOneChargeEstimatedAlgorithmically.setSelected("Yes");
        } else {
            plusOneChargeEstimatedAlgorithmically.setSelected("No");
        }

        minimalNumberofPrecursorsperSpectrum.setSelectedValue(oldOmssaParameters.getMinPrecPerSpectrum());
        if (oldOmssaParameters.isScalePrecursor()) {
            precursorMassScaling.setSelected("Yes");
        } else {
            precursorMassScaling.setSelected("No");
        }
        fractionofPrecursorsMZforChargeOneEstimation.setSelectedValue(oldOmssaParameters.getFractionOfPeaksForChargeEstimation());
        minimalNumberofPeaks.setSelectedValue(oldOmssaParameters.getMinPeaks());
        intensityCutoffIncrement.setSelectedValue(oldOmssaParameters.getIntensityCutOffIncrement());
        /*tab 2*/
        if (oldOmssaParameters.isMemoryMappedSequenceLibraries()) {
            sequencesMappinginMemory.setSelected("Yes");
        } else {
            sequencesMappinginMemory.setSelected("No");
        }
        if (oldOmssaParameters.isCleaveNterMethionine()) {
            cleaveNTerminalMethionine.setSelected("Yes");
        } else {
            cleaveNTerminalMethionine.setSelected("No");
        }
        /*tab 3*/
        minimumPrecursorChargeforMultiplyChargedFragments.setSelectedValue(oldOmssaParameters.getMinimalChargeForMultipleChargedFragments());
        massThresholdtoConsiderExactNeutronMass.setSelectedValue(oldOmssaParameters.getNeutronThreshold());
        singlyChargedWindowWidth.setSelectedValue(oldOmssaParameters.getSingleChargeWindow());
        doublyChargedWindowWidth.setSelectedValue(oldOmssaParameters.getDoubleChargeWindow());
        numberofPeaksinSinglyChargedWindow.setSelectedValue(oldOmssaParameters.getnPeaksInSingleChargeWindow());

        numberofPeaksinDoublyChargedWindow.setSelectedValue(oldOmssaParameters.getnPeaksInDoubleChargeWindow());
        minimumAnnotatedPeaksAmongtheMostIntenseOnes.setSelectedValue(oldOmssaParameters.getnAnnotatedMostIntensePeaks());
        minimumNumberofAnnotaedPeaks.setSelectedValue(oldOmssaParameters.getMinAnnotatedPeaks());
        maximumMZLadders.setSelectedValue(oldOmssaParameters.getMaxMzLadders());
        maximumFragmentCharge.setSelectedValue(oldOmssaParameters.getMaxFragmentCharge());

        if (oldOmssaParameters.isSearchPositiveIons()) {
            searchPostiveIons.setSelected("Yes");
        } else {
            searchPostiveIons.setSelected("No");
        }
        if (oldOmssaParameters.isSearchForwardFragmentFirst()) {
            searchFirstForwardIon_b1.setSelected("Yes");
        } else {
            searchFirstForwardIon_b1.setSelected("No");
        }
        if (oldOmssaParameters.isSearchRewindFragments()) {
            searchRewind_c_terminal_Ions.setSelected("Yes");
        } else {
            searchRewind_c_terminal_Ions.setSelected("No");
        }

        maximumFragmentsperSeries.setSelectedValue(oldOmssaParameters.getMaxFragmentPerSeries());
        if (oldOmssaParameters.isUseCorrelationCorrectionScore()) {
            useCorrelationScore.setSelected("Yes");
        } else {
            useCorrelationScore.setSelected("No");
        }
        consectiveIonProbability.setSelectedValue(oldOmssaParameters.getConsecutiveIonProbability());
        numberofHitsPerSpectrumperCharge.setSelectedValue(oldOmssaParameters.getMaxHitsPerSpectrumPerCharge());

        /*tab 4*/
        evalueCutoffforSequences.setSelectedValue(oldOmssaParameters.getIterativeSequenceEvalue());
        evalueCutoffforSpectra.setSelectedValue(oldOmssaParameters.getIterativeSpectrumEvalue());
        evalueCutofftoReplaceaHit.setSelectedValue(oldOmssaParameters.getIterativeReplaceEvalue());
        /**
         * tab5
         */

        peptideLength.setFirstSelectedValue(oldOmssaParameters.getMinPeptideLength());
        peptideLength.setSecondSelectedValue(oldOmssaParameters.getMaxPeptideLength());
        /**
         * tab6*
         */
        evalueCutoff.setSelectedValue(oldOmssaParameters.getMaxEValue());
        maximumHitListLength.setSelectedValue(oldOmssaParameters.getHitListLength());
        omssaOutputFormat.setSelected(oldOmssaParameters.getSelectedOutput());

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

        OmssaParameters oldOmssaParameters = (OmssaParameters) this.webSearchParameters.getSearchParameters().getIdentificationAlgorithmParameter(Advocate.omssa.getIndex());
        oldOmssaParameters.setLowIntensityCutOff(Double.valueOf(lowIntensityCutoff.getSelectedValue()));
        oldOmssaParameters.setHighIntensityCutOff(Double.valueOf(highIntensityCutoff.getSelectedValue()));
        oldOmssaParameters.setEstimateCharge((this.precursorChargeEstimation.getSelectedValue().equalsIgnoreCase("Use Range")));
        oldOmssaParameters.setRemovePrecursor((this.eliminateChargeReducedPrecursorsinSpectra.getSelectedValue().equalsIgnoreCase("Yes")));
        oldOmssaParameters.setIntensityCutOffIncrement(Double.valueOf(intensityCutoffIncrement.getSelectedValue()));
        oldOmssaParameters.setDetermineChargePlusOneAlgorithmically(plusOneChargeEstimatedAlgorithmically.getSelectedValue().equalsIgnoreCase("Yes"));
        oldOmssaParameters.setFractionOfPeaksForChargeEstimation(Double.valueOf(fractionofPrecursorsMZforChargeOneEstimation.getSelectedValue()));
        oldOmssaParameters.setMinPrecPerSpectrum(Integer.valueOf(minimalNumberofPrecursorsperSpectrum.getSelectedValue()));
        oldOmssaParameters.setScalePrecursor(precursorMassScaling.getSelectedValue().equalsIgnoreCase("Yes"));
        oldOmssaParameters.setMinPeaks(Integer.valueOf(minimalNumberofPeaks.getSelectedValue()));
        /**
         * tab 2
         */
        oldOmssaParameters.setMemoryMappedSequenceLibraries(sequencesMappinginMemory.getSelectedValue().equalsIgnoreCase("Yes"));
        oldOmssaParameters.setCleaveNterMethionine(cleaveNTerminalMethionine.getSelectedValue().equalsIgnoreCase("Yes"));
        /**
         * tab 3*
         */
        oldOmssaParameters.setMinimalChargeForMultipleChargedFragments(Integer.valueOf(minimumPrecursorChargeforMultiplyChargedFragments.getSelectedValue()));
        oldOmssaParameters.setNeutronThreshold(Double.valueOf(massThresholdtoConsiderExactNeutronMass.getSelectedValue()));
        oldOmssaParameters.setSingleChargeWindow(Integer.valueOf(singlyChargedWindowWidth.getSelectedValue()));
        oldOmssaParameters.setDoubleChargeWindow(Integer.valueOf(doublyChargedWindowWidth.getSelectedValue()));
        oldOmssaParameters.setnPeaksInSingleChargeWindow(Integer.valueOf(numberofPeaksinSinglyChargedWindow.getSelectedValue()));
        oldOmssaParameters.setnPeaksInDoubleChargeWindow(Integer.valueOf(numberofPeaksinDoublyChargedWindow.getSelectedValue()));
        oldOmssaParameters.setnAnnotatedMostIntensePeaks(Integer.valueOf(minimumAnnotatedPeaksAmongtheMostIntenseOnes.getSelectedValue()));
        oldOmssaParameters.setMinAnnotatedPeaks(Integer.valueOf(minimumNumberofAnnotaedPeaks.getSelectedValue()));
        oldOmssaParameters.setMaxMzLadders(Integer.valueOf(maximumMZLadders.getSelectedValue()));
        oldOmssaParameters.setMaxFragmentCharge(Integer.valueOf(maximumFragmentCharge.getSelectedValue()));
        oldOmssaParameters.setSearchPositiveIons(searchPostiveIons.getSelectedValue().equalsIgnoreCase("Yes"));
        oldOmssaParameters.setSearchForwardFragmentFirst(searchFirstForwardIon_b1.getSelectedValue().equalsIgnoreCase("Yes"));
        oldOmssaParameters.setSearchRewindFragments(searchRewind_c_terminal_Ions.getSelectedValue().equalsIgnoreCase("Yes"));
        oldOmssaParameters.setMaxFragmentPerSeries(Integer.valueOf(maximumFragmentsperSeries.getSelectedValue()));
        oldOmssaParameters.setUseCorrelationCorrectionScore(useCorrelationScore.getSelectedValue().equalsIgnoreCase("Yes"));
        oldOmssaParameters.setConsecutiveIonProbability(Double.valueOf(consectiveIonProbability.getSelectedValue()));
        oldOmssaParameters.setMaxHitsPerSpectrumPerCharge(Integer.valueOf(numberofHitsPerSpectrumperCharge.getSelectedValue()));
        /*tab 4*/
        oldOmssaParameters.setIterativeSequenceEvalue(Double.valueOf(evalueCutoffforSequences.getSelectedValue()));
        oldOmssaParameters.setIterativeSpectrumEvalue(Double.valueOf(evalueCutoffforSpectra.getSelectedValue()));
        oldOmssaParameters.setIterativeReplaceEvalue(Double.valueOf(evalueCutofftoReplaceaHit.getSelectedValue()));
        /**
         * tab 5
         */
        oldOmssaParameters.setMinPeptideLength(Integer.valueOf(peptideLength.getFirstSelectedValue()));
        oldOmssaParameters.setMaxPeptideLength(Integer.valueOf(peptideLength.getSecondSelectedValue()));
        /**
         * *tab 6*
         */
        oldOmssaParameters.setMaxEValue(Double.valueOf(evalueCutoff.getSelectedValue()));
        oldOmssaParameters.setHitListLength(Integer.valueOf(maximumHitListLength.getSelectedValue()));
        oldOmssaParameters.setSelectedOutput(omssaOutputFormat.getSelectedValue());

    }

}
