package com.uib.web.peptideshaker.ui.components.advancedsearchenginessettings;

import com.compomics.util.experiment.biology.modifications.ModificationCategory;
import com.compomics.util.experiment.identification.Advocate;
import com.compomics.util.parameters.identification.IdentificationParameters;
import com.compomics.util.parameters.identification.tool_specific.CometParameters;
import com.compomics.util.parameters.identification.tool_specific.MetaMorpheusParameters;
import com.compomics.util.parameters.identification.tool_specific.MetaMorpheusParameters.MetaMorpheusDecoyType;
import com.compomics.util.parameters.identification.tool_specific.MetaMorpheusParameters.MetaMorpheusDissociationType;
import com.compomics.util.parameters.identification.tool_specific.MetaMorpheusParameters.MetaMorpheusFragmentationTerminusType;
import com.compomics.util.parameters.identification.tool_specific.MetaMorpheusParameters.MetaMorpheusInitiatorMethionineBehaviorType;
import com.compomics.util.parameters.identification.tool_specific.MetaMorpheusParameters.MetaMorpheusMassDiffAcceptorType;
import com.compomics.util.parameters.identification.tool_specific.MetaMorpheusParameters.MetaMorpheusSearchType;
import com.uib.web.peptideshaker.ui.components.items.ColorLabel;
import com.uib.web.peptideshaker.ui.components.items.HelpPopupButton;
import com.uib.web.peptideshaker.ui.views.modal.PopupWindow;
import com.uib.web.peptideshaker.ui.components.items.HorizontalLabel2TextField;
import com.uib.web.peptideshaker.ui.components.items.HorizontalLabelDropDownList;
import com.uib.web.peptideshaker.ui.components.items.HorizontalLabelTextField;
import com.uib.web.peptideshaker.ui.components.items.SparkLine;
import com.vaadin.data.Property;
import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.swing.table.DefaultTableModel;

/**
 * @author Yehia Mokhtar Farag
 */
public class MetaMorpheusAdvancedSettingsPanel extends PopupWindow {

    private final HorizontalLabelTextField numberofPartitions;
    private final HorizontalLabelTextField deconIntensityRatio;
    private final HorizontalLabelDropDownList searchType;
    private final HorizontalLabelTextField scoreCutOff;
    private final HorizontalLabel2TextField peptideLength;

    private final HorizontalLabelDropDownList dissociationType;
    private final HorizontalLabelDropDownList initiatorMethionineBehaviorType;
    private final HorizontalLabelDropDownList USE_DELTA_SCORE;
    private final HorizontalLabelDropDownList massDiffAcceptorType;
    private final HorizontalLabelDropDownList useProvidedPrecInfo;
    private final HorizontalLabelDropDownList trimMS1Peaks;
    private final HorizontalLabelDropDownList trimMSMSPeaks;
    private final HorizontalLabelTextField maxNumberofModificationsperPeptide;
    private final HorizontalLabelTextField numberofPeakstoKeepperWindow;
    private final HorizontalLabelDropDownList doPrecDeco;
    private final HorizontalLabelDropDownList deconMassToleranceType;
    private final HorizontalLabelTextField minimumAllowedIntensityRatiotoBasePeak;
    private final HorizontalLabelTextField numberofWindows;
    private final HorizontalLabelTextField maxModificationIsoforms;
    private final HorizontalLabelTextField minimumVariantDepth;
    private final HorizontalLabelTextField maximumHeterozygousVariants;
    private final HorizontalLabelDropDownList normalizePeaksAccrossAllWindows;
    private final HorizontalLabelDropDownList modifiedPeptidesAreDifferent;
    private final HorizontalLabelDropDownList fragmentationTerminus;
    private final HorizontalLabelDropDownList searchTarget;
    private final HorizontalLabelDropDownList runGPTMSearch;
    private final HorizontalLabelDropDownList decoyType;
    private final HorizontalLabelDropDownList exludeOneHitWonders;
    private final HorizontalLabelTextField windowWidthinThomson;
    private final HorizontalLabelTextField maxFragmentSize;
    private final HorizontalLabelTextField deconvolutionMassTolerance;
    private final HorizontalLabelDropDownList writemzIdentML;
    private final HorizontalLabelDropDownList pepXML;
    private final Table categoryTable;

    private IdentificationParameters webSearchParameters;

    /**
     *
     */
    public MetaMorpheusAdvancedSettingsPanel() {
        super(VaadinIcons.COG.getHtml() + " MetaMorpheus Advanced Settings");

        AbsoluteLayout container = new AbsoluteLayout();
        container.setStyleName("popuppanelmaincontainer");
        container.setWidth(564, Unit.PIXELS);
        container.setHeight(415, Unit.PIXELS);

        MetaMorpheusAdvancedSettingsPanel.this.setContent(container);
        MetaMorpheusAdvancedSettingsPanel.this.setClosable(true);

        Label title = new Label("MetaMorpheus");
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
        subContainer.addTab(tab1, "Search");
        Set<String> values = new LinkedHashSet<>();
        values.add(MetaMorpheusSearchType.Classic.name());
        values.add(MetaMorpheusSearchType.Modern.name());
        values.add(MetaMorpheusSearchType.NonSpecific.name());

        this.searchType = new HorizontalLabelDropDownList("Search Type");
        this.searchType.updateData(values);
        tab1.addComponent(this.searchType);

        this.numberofPartitions = new HorizontalLabelTextField("Number of Partitions", 1, new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab1.addComponent(numberofPartitions);
        values.clear();
        values.add(MetaMorpheusDissociationType.HCD.name());
        values.add(MetaMorpheusDissociationType.CID.name());
        values.add(MetaMorpheusDissociationType.ECD.name());
        values.add(MetaMorpheusDissociationType.ETD.name());
        dissociationType = new HorizontalLabelDropDownList("Dissociation Type");
        dissociationType.updateData(values);
        tab1.addComponent(this.dissociationType);
        this.maxNumberofModificationsperPeptide = new HorizontalLabelTextField("Max Number of Modifications/Peptide", 2, new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab1.addComponent(maxNumberofModificationsperPeptide);

        values.clear();
        values.add(MetaMorpheusInitiatorMethionineBehaviorType.Undefined.name());
        values.add(MetaMorpheusInitiatorMethionineBehaviorType.Retain.name());
        values.add(MetaMorpheusInitiatorMethionineBehaviorType.Cleave.name());
        values.add(MetaMorpheusInitiatorMethionineBehaviorType.Variable.name());

        initiatorMethionineBehaviorType = new HorizontalLabelDropDownList("Initiator Methionine Behavior");
        initiatorMethionineBehaviorType.updateData(values);
        tab1.addComponent(this.initiatorMethionineBehaviorType);
        initiatorMethionineBehaviorType.setSelected(MetaMorpheusInitiatorMethionineBehaviorType.Variable.name());

        this.scoreCutOff = new HorizontalLabelTextField("Score Cut-Off", 5.0, new DoubleRangeValidator("Only postive double value allowed ", 0.0, Double.MAX_VALUE));
        tab1.addComponent(scoreCutOff);

        values.clear();
        values.add(1 + "");
        values.add(0 + "");
       

        USE_DELTA_SCORE = new HorizontalLabelDropDownList("Use Delta Score");
        USE_DELTA_SCORE.updateData(values);
        tab1.addComponent(this.USE_DELTA_SCORE);
        USE_DELTA_SCORE.setItemCaption(0 + "", "No");
        USE_DELTA_SCORE.setItemCaption(1 + "", "Yes");

        values.clear();
        values.add(MetaMorpheusMassDiffAcceptorType.Exact.name());
        values.add(MetaMorpheusMassDiffAcceptorType.OneMM.name());
        values.add(MetaMorpheusMassDiffAcceptorType.TwoMM.name());
        values.add(MetaMorpheusMassDiffAcceptorType.ThreeMM.name());
        values.add(MetaMorpheusMassDiffAcceptorType.PlusOrMinusThreeMM.name());
        values.add(MetaMorpheusMassDiffAcceptorType.ModOpen.name());
        values.add(MetaMorpheusMassDiffAcceptorType.Open.name());
        massDiffAcceptorType = new HorizontalLabelDropDownList("Mass Difference Acceptor Type");
        massDiffAcceptorType.updateData(values);
        tab1.addComponent(this.massDiffAcceptorType);
        massDiffAcceptorType.setSelected(MetaMorpheusMassDiffAcceptorType.OneMM.name());

        this.peptideLength = new HorizontalLabel2TextField("Peptide Length(min-max)", 8, 30, new IntegerRangeValidator("Only integer number allowed ", 0, Integer.MAX_VALUE));
        tab1.addComponent(peptideLength);

        //tab 4
        values.clear();
        values.add(1 + "");
        values.add(0 + "");
        

        VerticalLayout tab2 = new VerticalLayout();
        subContainer.addTab(tab2, "Output");
        writemzIdentML = new HorizontalLabelDropDownList("Write mzIdentML");
        writemzIdentML.updateData(values);
        tab2.addComponent(this.writemzIdentML);
        writemzIdentML.setItemCaption(1 + "","Yes" );
        writemzIdentML.setItemCaption(0 + "", "No");
//        writemzIdentML.setSelected(0 + "");

        pepXML = new HorizontalLabelDropDownList("pepXML");
        pepXML.updateData(values);
        pepXML.setItemCaption(0 + "", "No");
        pepXML.setItemCaption(1 + "", "Yes");
        pepXML.setSelected(1 + "");
        tab2.addComponent(this.pepXML);
        /*tab 2*/
        VerticalLayout tab3 = new VerticalLayout();
        subContainer.addTab(tab3, "Deisotoping");
        useProvidedPrecInfo = new HorizontalLabelDropDownList("Use Provided Precursor Info");
        useProvidedPrecInfo.updateData(values);
        tab3.addComponent(this.useProvidedPrecInfo);
        useProvidedPrecInfo.setItemCaption(0 + "", "No");
        useProvidedPrecInfo.setItemCaption(1 + "", "Yes");
//        useProvidedPrecInfo.setSelected(0 + "");

        doPrecDeco = new HorizontalLabelDropDownList("Do Precursor Deconvolution");
        doPrecDeco.updateData(values);
        tab3.addComponent(this.doPrecDeco);
        doPrecDeco.setItemCaption(0 + "", "No");
        doPrecDeco.setItemCaption(1 + "", "Yes");
//        doPrecDeco.setSelected(0 + "");

        this.deconIntensityRatio = new HorizontalLabelTextField("Deconvolution Intensity Ratio", 3.0, new DoubleRangeValidator("Only positive double allowed", 0.0, Double.MAX_VALUE));
        tab3.addComponent(deconIntensityRatio);
        this.deconvolutionMassTolerance = new HorizontalLabelTextField("Deconvolution Mass Tolerance", 4.0, new DoubleRangeValidator("Only positive double allowed", 0.0, Double.MAX_VALUE));
        tab3.addComponent(deconvolutionMassTolerance);

        deconMassToleranceType = new HorizontalLabelDropDownList("Deconvolution Mass Tolerance Type");
        values.clear();
        values.add(MetaMorpheusParameters.MetaMorpheusToleranceType.PPM.name());
        values.add(MetaMorpheusParameters.MetaMorpheusToleranceType.Absolute.name());
        deconMassToleranceType.updateData(values);
        tab3.addComponent(this.deconMassToleranceType);

        /*tab 4*/
        VerticalLayout tab4 = new VerticalLayout();
        subContainer.addTab(tab4, "Peak Trimming");
        values.clear();
        values.add(1 + "");
        values.add(0 + "");
        

        trimMS1Peaks = new HorizontalLabelDropDownList("Trim MS1 Peaks");
        trimMS1Peaks.updateData(values);
        tab4.addComponent(this.trimMS1Peaks);
        trimMS1Peaks.setItemCaption(1 + "", "Yes");
        trimMS1Peaks.setItemCaption(0 + "", "No");
//        trimMS1Peaks.setSelected(1 + "");

        trimMSMSPeaks = new HorizontalLabelDropDownList("Trim MSMS Peaks");
        trimMSMSPeaks.updateData(values);
        tab4.addComponent(this.trimMSMSPeaks);
        trimMSMSPeaks.setItemCaption(0 + "", "No");
        trimMSMSPeaks.setItemCaption(1 + "", "Yes");
//        trimMSMSPeaks.setSelected(0 + "");

        this.numberofPeakstoKeepperWindow = new HorizontalLabelTextField("Number of Peaks to Keep per Window", 200, new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab4.addComponent(numberofPeakstoKeepperWindow);
        this.minimumAllowedIntensityRatiotoBasePeak = new HorizontalLabelTextField("Minimum Allowed Intensity Ratio to Base Peak", 0.01, new DoubleRangeValidator("Only positive double allowed", 0.0, Double.MAX_VALUE));
        tab4.addComponent(minimumAllowedIntensityRatiotoBasePeak);

        this.windowWidthinThomson = new HorizontalLabelTextField("Window Width in Thomson", "", new DoubleRangeValidator("Only postive double values allowed", 0.0, Double.MAX_VALUE));
        tab4.addComponent(this.windowWidthinThomson);

        this.numberofWindows = new HorizontalLabelTextField("Number of Windows", "", new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab4.addComponent(numberofWindows);

        normalizePeaksAccrossAllWindows = new HorizontalLabelDropDownList("Normalize Peaks Accross All Windows");
        normalizePeaksAccrossAllWindows.updateData(values);
        tab4.addComponent(this.normalizePeaksAccrossAllWindows);
        normalizePeaksAccrossAllWindows.setItemCaption(0 + "", "No");
        normalizePeaksAccrossAllWindows.setItemCaption(1 + "", "Yes");
//        normalizePeaksAccrossAllWindows.setSelected(1 + "");

        /*tab 4*/
        VerticalLayout tab5 = new VerticalLayout();
        subContainer.addTab(tab5, "Protein Grouping");

        modifiedPeptidesAreDifferent = new HorizontalLabelDropDownList("Modified Peptides Are Different");
        modifiedPeptidesAreDifferent.updateData(values);
        tab5.addComponent(this.modifiedPeptidesAreDifferent);
        modifiedPeptidesAreDifferent.setItemCaption(0 + "", "No");
        modifiedPeptidesAreDifferent.setItemCaption(1 + "", "Yes");
//        modifiedPeptidesAreDifferent.setSelected(1 + "");

        exludeOneHitWonders = new HorizontalLabelDropDownList("Exlude One Hit Wonders");
        exludeOneHitWonders.updateData(values);
        tab5.addComponent(this.exludeOneHitWonders);
        exludeOneHitWonders.setItemCaption(0 + "", "No");
        exludeOneHitWonders.setItemCaption(1 + "", "Yes");
//        exludeOneHitWonders.setSelected(1 + "");

        /*tab 6*/
        VerticalLayout tab6 = new VerticalLayout();
        subContainer.addTab(tab6, "In Silico Digestion");
        searchTarget = new HorizontalLabelDropDownList("Search Target");
        searchTarget.updateData(values);
        searchTarget.setItemCaption(0 + "", "No");
        searchTarget.setItemCaption(1 + "", "Yes");
//        searchTarget.setSelected(0 + "");

        runGPTMSearch = new HorizontalLabelDropDownList("Run G-PTM Search");
        runGPTMSearch.updateData(values);
        runGPTMSearch.setItemCaption(0 + "", "No");
        runGPTMSearch.setItemCaption(1 + "", "Yes");
//        runGPTMSearch.setSelected(1 + "");

        values.clear();
        values.add(MetaMorpheusFragmentationTerminusType.Both.name());
        values.add(MetaMorpheusFragmentationTerminusType.N.name());
        values.add(MetaMorpheusFragmentationTerminusType.C.name());

        fragmentationTerminus = new HorizontalLabelDropDownList("Fragmentation Terminus");
        fragmentationTerminus.updateData(values);
        tab6.addComponent(this.fragmentationTerminus);
        this.maxFragmentSize = new HorizontalLabelTextField("Max Fragment Size", 30000.0, new DoubleRangeValidator("Only postive double values allowed", 0.0, Double.MAX_VALUE));
        tab6.addComponent(this.maxFragmentSize);
        tab6.addComponent(this.searchTarget);

        values.clear();
        values.add(MetaMorpheusDecoyType.None.name());
        values.add(MetaMorpheusDecoyType.Reverse.name());
        values.add(MetaMorpheusDecoyType.Slide.name());
        decoyType = new HorizontalLabelDropDownList("Decoy Type");
        decoyType.updateData(values);
        tab6.addComponent(this.decoyType);

        this.maxModificationIsoforms = new HorizontalLabelTextField("Max Modification Isoforms", 1024, new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab6.addComponent(maxModificationIsoforms);
        this.minimumVariantDepth = new HorizontalLabelTextField("Minimum Variant Depth", 1, new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab6.addComponent(minimumVariantDepth);
        this.maximumHeterozygousVariants = new HorizontalLabelTextField("Maximum Heterozygous Variants", 4, new IntegerRangeValidator("Only positive integer allowed", 0, Integer.MAX_VALUE));
        tab6.addComponent(maximumHeterozygousVariants);

        /*tab 6*/
        VerticalLayout tab7 = new VerticalLayout();
        subContainer.addTab(tab7, "G-PTM Search");
        tab7.addComponent(this.runGPTMSearch);

        categoryTable = initCategoryTable();
        tab7.addComponent(this.categoryTable);

        String helpText = "<a href='https://github.com/smith-chem-wisc/MetaMorpheus/wiki' target='_blank'>";
        HelpPopupButton help = new HelpPopupButton(helpText, "<font style='line-height: 20px;'>Click to open the MetaMorpheus help page.</font>", 100, 20);
        container.addComponent(help, "left:10px;bottom:10px;");
        Button okBtn = new Button("OK");
        okBtn.setWidth(76, Unit.PIXELS);
        okBtn.setHeight(20, Unit.PIXELS);
        okBtn.setStyleName(ValoTheme.BUTTON_TINY);
        okBtn.addClickListener((Button.ClickEvent event) -> {
            if (numberofPartitions.isValid()&& maxNumberofModificationsperPeptide.isValid() && scoreCutOff.isValid()&& peptideLength.isValid() && deconIntensityRatio.isValid() &&deconvolutionMassTolerance.isValid()&& numberofPeakstoKeepperWindow.isValid() && minimumAllowedIntensityRatiotoBasePeak.isValid() && maxFragmentSize.isValid() && maxModificationIsoforms.isValid() && minimumVariantDepth.isValid() && maximumHeterozygousVariants.isValid()) {
                updateParameters();
                setPopupVisible(false);
            }
        });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyleName(ValoTheme.BUTTON_TINY);
        cancelBtn.setWidth(76, Unit.PIXELS);
        cancelBtn.setHeight(20, Unit.PIXELS);

        cancelBtn.addClickListener((Button.ClickEvent event) -> {
            MetaMorpheusAdvancedSettingsPanel.this.setPopupVisible(false);
        });

        container.addComponent(cancelBtn, "bottom:10px;right:10px");
        container.addComponent(okBtn, "bottom:10px;right:96px");
    }

    /**
     *
     * @param webSearchParameters
     */
    public void updateGUI(IdentificationParameters webSearchParameters) {
        this.webSearchParameters = webSearchParameters;//
        MetaMorpheusParameters oldCometParameters = (MetaMorpheusParameters) this.webSearchParameters.getSearchParameters().getIdentificationAlgorithmParameter(Advocate.metaMorpheus.getIndex());
        searchType.setSelected(oldCometParameters.getSearchType().name());
        numberofPartitions.setSelectedValue(oldCometParameters.getTotalPartitions());
        dissociationType.setSelected(oldCometParameters.getDissociationType().name());
        maxNumberofModificationsperPeptide.setSelectedValue(oldCometParameters.getMaxModsForPeptide());
        initiatorMethionineBehaviorType.setSelected(oldCometParameters.getInitiatorMethionineBehavior().name());
        scoreCutOff.setSelectedValue(oldCometParameters.getScoreCutoff());
        if (oldCometParameters.getUseDeltaScore()) {
            USE_DELTA_SCORE.setSelected(1 + "");
        } else {
            USE_DELTA_SCORE.setSelected(0 + "");
        }
        massDiffAcceptorType.setSelected(oldCometParameters.getMassDiffAcceptorType().name());
        peptideLength.setFirstSelectedValue(oldCometParameters.getMinPeptideLength());
        peptideLength.setSecondSelectedValue(oldCometParameters.getMaxPeptideLength());
        if (oldCometParameters.getWriteMzId()) {
            writemzIdentML.setSelected(1 + "");
        } else {
            writemzIdentML.setSelected(0 + "");
        }
        if (oldCometParameters.getWritePepXml()) {
            pepXML.setSelected(1 + "");
        } else {
            pepXML.setSelected(0 + "");
        }

        if (oldCometParameters.getUseProvidedPrecursorInfo()) {
            useProvidedPrecInfo.setSelected(1 + "");
        } else {
            useProvidedPrecInfo.setSelected(0 + "");
        }
        if (oldCometParameters.getDoPrecursorDeconvolution()) {
            doPrecDeco.setSelected(1 + "");
        } else {
            doPrecDeco.setSelected(0 + "");
        }

        deconIntensityRatio.setSelectedValue(oldCometParameters.getDeconvolutionIntensityRatio());
        deconvolutionMassTolerance.setSelectedValue(oldCometParameters.getDeconvolutionMassTolerance());
        deconMassToleranceType.setSelected(oldCometParameters.getDeconvolutionMassToleranceType().name());

        if (oldCometParameters.getTrimMs1Peaks()) {
            trimMS1Peaks.setSelected(1 + "");
        } else {
            trimMS1Peaks.setSelected(0 + "");
        }
        if (oldCometParameters.getTrimMsMsPeaks()) {
            trimMSMSPeaks.setSelected(1 + "");
        } else {
            trimMSMSPeaks.setSelected(0 + "");
        }
        numberofPeakstoKeepperWindow.setSelectedValue(oldCometParameters.getNumberOfPeaksToKeepPerWindow());
        minimumAllowedIntensityRatiotoBasePeak.setSelectedValue(oldCometParameters.getMinAllowedIntensityRatioToBasePeak());
        if (oldCometParameters.getWindowWidthThomsons() != null) {
            windowWidthinThomson.setSelectedValue(oldCometParameters.getWindowWidthThomsons());
        }
        if (oldCometParameters.getNumberOfWindows() != null) {
            numberofWindows.setSelectedValue(oldCometParameters.getNumberOfWindows());
        }
        if (oldCometParameters.getNormalizePeaksAcrossAllWindows()) {
            normalizePeaksAccrossAllWindows.setSelected(1 + "");
        } else {
            normalizePeaksAccrossAllWindows.setSelected(0 + "");
        }

        if (oldCometParameters.getModPeptidesAreDifferent()) {
            modifiedPeptidesAreDifferent.setSelected(1 + "");
        } else {
            modifiedPeptidesAreDifferent.setSelected(0 + "");
        }
        if (oldCometParameters.getNoOneHitWonders()) {
            exludeOneHitWonders.setSelected(1 + "");
        } else {
            exludeOneHitWonders.setSelected(0 + "");
        }

        fragmentationTerminus.setSelected(oldCometParameters.getFragmentationTerminus().name());
        maxFragmentSize.setSelectedValue(oldCometParameters.getMaxFragmentSize());
        if (oldCometParameters.getSearchTarget()) {
            searchTarget.setSelected(1 + "");
        } else {
            searchTarget.setSelected(0 + "");
        }
        decoyType.setSelected(oldCometParameters.getDecoyType().name());
        maxModificationIsoforms.setSelectedValue(oldCometParameters.getMaxModificationIsoforms());
        minimumVariantDepth.setSelectedValue(oldCometParameters.getMinVariantDepth());
        maximumHeterozygousVariants.setSelectedValue(oldCometParameters.getMaxHeterozygousVariants());
        if (oldCometParameters.runGptm()) {
            runGPTMSearch.setSelected(1 + "");
        } else {
            runGPTMSearch.setSelected(0 + "");
        }
        for (ModificationCategory tempModCategory : ModificationCategory.values()) {
            ((CheckBox) categoryTable.getItem(tempModCategory).getItemProperty("select").getValue()).setValue(false);
            if (oldCometParameters.getGPtmCategories().contains(tempModCategory)) {
                ((CheckBox) categoryTable.getItem(tempModCategory).getItemProperty("select").getValue()).setValue(true);
            }
        }
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
        }
        super.setPopupVisible(visible); //To change body of generated methods, choose Tools | Templates.
    }

    private void updateParameters() {
        System.out.println("update inputs");
        MetaMorpheusParameters oldCometParameters = (MetaMorpheusParameters) this.webSearchParameters.getSearchParameters().getIdentificationAlgorithmParameter(Advocate.metaMorpheus.getIndex());
        oldCometParameters.setSearchType(MetaMorpheusSearchType.valueOf(searchType.getSelectedValue()));
        oldCometParameters.setTotalPartitions(Integer.valueOf(numberofPartitions.getSelectedValue()));
        oldCometParameters.setDissociationType(MetaMorpheusDissociationType.valueOf(dissociationType.getSelectedValue()));
        oldCometParameters.setMaxModsForPeptide(Integer.valueOf(maxNumberofModificationsperPeptide.getSelectedValue()));
        oldCometParameters.setInitiatorMethionineBehavior(MetaMorpheusInitiatorMethionineBehaviorType.valueOf(initiatorMethionineBehaviorType.getSelectedValue()));
        oldCometParameters.setScoreCutoff(Double.valueOf(scoreCutOff.getSelectedValue()));
        oldCometParameters.setUseDeltaScore(USE_DELTA_SCORE.getSelectedValue().equals(1 + ""));
        oldCometParameters.setMassDiffAcceptorType(MetaMorpheusMassDiffAcceptorType.valueOf(massDiffAcceptorType.getSelectedValue()));
        oldCometParameters.setMinPeptideLength(Integer.valueOf(peptideLength.getFirstSelectedValue()));
        oldCometParameters.setMaxPeptideLength(Integer.valueOf(peptideLength.getSecondSelectedValue()));

        oldCometParameters.setWriteMzId(writemzIdentML.getSelectedValue().equals(1 + ""));
        oldCometParameters.setWritePepXml(pepXML.getSelectedValue().equals(1 + ""));

        oldCometParameters.setUseProvidedPrecursorInfo(useProvidedPrecInfo.getSelectedValue().equals(1 + ""));
        oldCometParameters.setDoPrecursorDeconvolution(doPrecDeco.getSelectedValue().equals(1 + ""));
        oldCometParameters.setDeconvolutionIntensityRatio(Double.valueOf(deconIntensityRatio.getSelectedValue()));
        oldCometParameters.setDeconvolutionMassTolerance(Double.valueOf(deconvolutionMassTolerance.getSelectedValue()));
        oldCometParameters.setDeconvolutionMassToleranceType(MetaMorpheusParameters.MetaMorpheusToleranceType.valueOf(deconMassToleranceType.getSelectedValue()));

        oldCometParameters.setTrimMs1Peaks(trimMS1Peaks.getSelectedValue().equals(1 + ""));
        oldCometParameters.setTrimMsMsPeaks(trimMSMSPeaks.getSelectedValue().equals(1 + ""));
        oldCometParameters.setNumberOfPeaksToKeepPerWindow(Integer.valueOf(numberofPeakstoKeepperWindow.getSelectedValue()));
        oldCometParameters.setMinAllowedIntensityRatioToBasePeak(Double.valueOf(minimumAllowedIntensityRatiotoBasePeak.getSelectedValue()));

        if (windowWidthinThomson.getSelectedValue() != null && !windowWidthinThomson.getSelectedValue().trim().equalsIgnoreCase("")) {
            oldCometParameters.setWindowWidthThomsons(Double.valueOf(windowWidthinThomson.getSelectedValue()));
        }
        if (numberofWindows.getSelectedValue() != null && !numberofWindows.getSelectedValue().trim().equalsIgnoreCase("")) {
            oldCometParameters.setNumberOfWindows(Integer.valueOf(numberofWindows.getSelectedValue()));
        }
        oldCometParameters.setNormalizePeaksAcrossAllWindows(normalizePeaksAccrossAllWindows.getSelectedValue().equals(1 + ""));

        oldCometParameters.setModPeptidesAreDifferent(modifiedPeptidesAreDifferent.getSelectedValue().equals(1 + ""));
        oldCometParameters.setNoOneHitWonders(exludeOneHitWonders.getSelectedValue().equals(1 + ""));

        oldCometParameters.setFragmentationTerminus(MetaMorpheusFragmentationTerminusType.valueOf(fragmentationTerminus.getSelectedValue()));
        oldCometParameters.setMaxFragmentSize(Double.valueOf(maxFragmentSize.getSelectedValue()));
        oldCometParameters.setSearchTarget(searchTarget.getSelectedValue().equals(1 + ""));
        oldCometParameters.setDecoyType(MetaMorpheusDecoyType.valueOf(decoyType.getSelectedValue()));
        oldCometParameters.setMaxModificationIsoforms(Integer.valueOf(maxModificationIsoforms.getSelectedValue().replace(",", "")));
        oldCometParameters.setMinVariantDepth(Integer.valueOf(minimumVariantDepth.getSelectedValue()));
        oldCometParameters.setMaxHeterozygousVariants(Integer.valueOf(maximumHeterozygousVariants.getSelectedValue()));

        oldCometParameters.setRunGptm(runGPTMSearch.getSelectedValue().equals(1 + ""));
        oldCometParameters.getGPtmCategories().clear();
        categoryTable.getItemIds().stream().filter((cat) -> (((CheckBox) categoryTable.getItem(cat).getItemProperty("select").getValue()).getValue())).forEachOrdered((cat) -> {
            oldCometParameters.getGPtmCategories().add((ModificationCategory) cat);
        });

    }

    /**
     * Initialise the modifications tables
     *
     * @return initialised modification table
     */
    private Table initCategoryTable() {
        Table table = new Table();
        table.setWidth(100, Unit.PERCENTAGE);
        table.setHeight(264, Unit.PIXELS);
        table.setCaptionAsHtml(true);
        table.addStyleName(ValoTheme.TREETABLE_SMALL);
        table.addStyleName("smalltable");
        table.setMultiSelect(true);
        table.setSelectable(true);
        table
                .addContainerProperty("index", Integer.class,
                        null, "", null, Table.Align.RIGHT);
        table
                .addContainerProperty("category", String.class,
                        null, "Category", null, Table.Align.LEFT);
        table
                .addContainerProperty("select", CheckBox.class,
                        null, "", null, Table.Align.CENTER);

        table.setColumnExpandRatio("index", 15);
        table.setColumnExpandRatio("category", 70);
        table.setColumnExpandRatio("select", 15);
        table.setSortEnabled(false);

        for (int i = 0; i < ModificationCategory.values().length; i++) {

            ModificationCategory tempModCategory = ModificationCategory.values()[i];
            CheckBox cb = new CheckBox();
            cb.setData(tempModCategory);           
            if (i > 0 && i < 4) {
//                cb.setValue(true);
            }
            table.addItem(new Object[]{i + 1, tempModCategory.name(), cb}, tempModCategory);

        }
        table.setValue(1);
        return table;
    }

}
