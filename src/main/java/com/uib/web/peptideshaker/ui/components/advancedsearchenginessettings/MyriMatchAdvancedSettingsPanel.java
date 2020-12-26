package com.uib.web.peptideshaker.ui.components.advancedsearchenginessettings;

import com.compomics.util.experiment.identification.Advocate;
import com.compomics.util.parameters.identification.IdentificationParameters;
import com.compomics.util.parameters.identification.tool_specific.MyriMatchParameters;
import com.uib.web.peptideshaker.ui.components.items.HelpPopupButton;
import com.uib.web.peptideshaker.ui.views.modal.PopupWindow;
import com.uib.web.peptideshaker.ui.components.items.HorizontalLabel2TextField;
import com.uib.web.peptideshaker.ui.components.items.HorizontalLabelDropDownList;
import com.uib.web.peptideshaker.ui.components.items.HorizontalLabelTextField;
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
public class MyriMatchAdvancedSettingsPanel extends PopupWindow {

    private final HorizontalLabel2TextField peptideLength;
    private final HorizontalLabel2TextField precursorMass;
    private final HorizontalLabelTextField numberOfSpectrumMatches;
    private final HorizontalLabelTextField maxVariablePTMperPeptide;
    private final HorizontalLabelDropDownList fragmentationMethodList;
    private final HorizontalLabelDropDownList enzymaticTerminalsList;
    private final HorizontalLabelDropDownList useSmartPlusTreeModelList;
    private final HorizontalLabelDropDownList computeXCorrList;

    private final HorizontalLabelTextField ticCuttoffPercentage;
    private final HorizontalLabelTextField numberOfIntensityClasses;
    private final HorizontalLabelTextField classSizeMultiplier;
    private final HorizontalLabelTextField numberOfBatches;
    private final HorizontalLabelTextField maxPeakCount;
    private final HorizontalLabelDropDownList outputFormat;

    private IdentificationParameters webSearchParameters;

    public MyriMatchAdvancedSettingsPanel() {
        super(VaadinIcons.COG.getHtml() + " MyriMatch Advanced Settings");
        AbsoluteLayout container = new AbsoluteLayout();
        container.setStyleName("popuppanelmaincontainer");
        container.setWidth(500, Unit.PIXELS);
        container.setHeight(500, Unit.PIXELS);

        Label title = new Label("MyriMatch");
        container.addComponent(title, "left:10px;top:10px");
        VerticalLayout subContainer = new VerticalLayout();
        subContainer.setSizeFull();
        subContainer.setStyleName("subcontainer");
        subContainer.addStyleName("paddingvertical5");
        container.addComponent(subContainer, "left:10px;top:45px;right:10px;bottom:40px");
        MyriMatchAdvancedSettingsPanel.this.setContent(container);
        MyriMatchAdvancedSettingsPanel.this.setClosable(true);

        peptideLength = new HorizontalLabel2TextField("Peptide Length (min-max)", 0, 0, new IntegerRangeValidator("Only integer number allowd", (-1 * Integer.MAX_VALUE), Integer.MAX_VALUE));
        peptideLength.setSpacing(true);
        subContainer.addComponent(peptideLength);
        subContainer.setComponentAlignment(peptideLength, Alignment.BOTTOM_LEFT);
        precursorMass = new HorizontalLabel2TextField("Precursor Mass (min-max)", 0.0, 0.0, new DoubleRangeValidator("Only double values allowd", (-1 * Double.MAX_VALUE), Double.MAX_VALUE));
        precursorMass.setSpacing(true);
        subContainer.addComponent(precursorMass);
        numberOfSpectrumMatches = new HorizontalLabelTextField("Number of Spectrum Matches", 0, new IntegerRangeValidator("Postive integer only allowed", 0, Integer.MAX_VALUE));
        subContainer.addComponent(numberOfSpectrumMatches);

        maxVariablePTMperPeptide = new HorizontalLabelTextField("Max Variable PTMs per Peptide", 0, new IntegerRangeValidator("Postive integer only allowed", 0, Integer.MAX_VALUE));
        subContainer.addComponent(maxVariablePTMperPeptide);

        fragmentationMethodList = new HorizontalLabelDropDownList("Fragmentation Method");
        subContainer.addComponent(fragmentationMethodList);
        Set<String> values = new LinkedHashSet<>();
        values.add("CID");
        values.add("HCD");
        values.add("ETD");
        fragmentationMethodList.updateData(values);

        enzymaticTerminalsList = new HorizontalLabelDropDownList("Enzymatic Terminals");
        subContainer.addComponent(enzymaticTerminalsList);
        values.clear();
        values.add(0 + "");
        values.add(1 + "");
        values.add(2 + "");
        enzymaticTerminalsList.updateData(values);
        enzymaticTerminalsList.setItemCaption(0, "None Required");
        enzymaticTerminalsList.setItemCaption(1, "At Least One");
        enzymaticTerminalsList.setItemCaption(2, "Both");

        useSmartPlusTreeModelList = new HorizontalLabelDropDownList("Use Smart Plus Tree Model");
        subContainer.addComponent(useSmartPlusTreeModelList);
        values.clear();
        values.add("Yes");
        values.add("No");
        useSmartPlusTreeModelList.updateData(values);
        computeXCorrList = new HorizontalLabelDropDownList("Compute XCorr");
        subContainer.addComponent(computeXCorrList);
        computeXCorrList.updateData(values);

        ticCuttoffPercentage = new HorizontalLabelTextField("TIC Cutoff Percentage (0.0-1.0)", 0.0, new DoubleRangeValidator("Only 0.0 to 1.0 allowed", 0.0, 1.0));
        subContainer.addComponent(ticCuttoffPercentage);

        numberOfIntensityClasses = new HorizontalLabelTextField("Number of Intensity Classes", 0, new IntegerRangeValidator("Postive integer only allowed", 0, Integer.MAX_VALUE));
        subContainer.addComponent(numberOfIntensityClasses);

        classSizeMultiplier = new HorizontalLabelTextField("Class Size Multiplier", 0, new IntegerRangeValidator("Postive integer only allowed", 0, Integer.MAX_VALUE));
        subContainer.addComponent(classSizeMultiplier);

        numberOfBatches = new HorizontalLabelTextField("Number of Batches", 0, new IntegerRangeValidator("Postive integer only allowed", 0, Integer.MAX_VALUE));
        subContainer.addComponent(numberOfBatches);
        maxPeakCount = new HorizontalLabelTextField("Max Peak Count", 0, new IntegerRangeValidator("Postive integer only allowed", 0, Integer.MAX_VALUE));
        subContainer.addComponent(maxPeakCount);

        outputFormat = new HorizontalLabelDropDownList("Output Format");
        subContainer.addComponent(outputFormat);
        subContainer.setComponentAlignment(outputFormat, Alignment.BOTTOM_LEFT);
        values.clear();
        values.add("mzIdentML");
        values.add("pepXML");
        outputFormat.updateData(values);
        outputFormat.addValueChangeListener((Property.ValueChangeEvent event) -> {
            if (outputFormat.getSelectedValue().equalsIgnoreCase("pepXML")) {
                Notification.show("Note that the MyriMatch pepXML format is not compatible with PeptideShaker.", Notification.Type.WARNING_MESSAGE);
            }
        });

        String helpText = "<a href='http://htmlpreview.github.io/?https://github.com/ProteoWizard/pwiz/blob/master/pwiz_tools/Bumbershoot/myrimatch/doc/index.html' target='_blank'>";
        HelpPopupButton help = new HelpPopupButton(helpText, "The advanced settings are for expert use only. See help for details", 100, 20);
        container.addComponent(help, "left:10px;bottom:10px;");

        Button okBtn = new Button("OK");
        okBtn.setWidth(76, Unit.PIXELS);
        okBtn.setHeight(20, Unit.PIXELS);
        okBtn.setStyleName(ValoTheme.BUTTON_TINY);
        okBtn.addClickListener((Button.ClickEvent event) -> {
            if (peptideLength.isValid() && precursorMass.isValid() && numberOfSpectrumMatches.isValid() && maxVariablePTMperPeptide.isValid() && ticCuttoffPercentage.isValid() && numberOfIntensityClasses.isValid() && classSizeMultiplier.isValid() && numberOfBatches.isValid() && maxPeakCount.isValid()) {
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
            MyriMatchAdvancedSettingsPanel.this.setPopupVisible(false);
        });

    }

    public void updateGUI(IdentificationParameters webSearchParameters) {
        this.webSearchParameters = webSearchParameters;
        MyriMatchParameters myriMatchParameters = (MyriMatchParameters) webSearchParameters.getSearchParameters().getIdentificationAlgorithmParameter(Advocate.myriMatch.getIndex());
        peptideLength.setFirstSelectedValue(myriMatchParameters.getMinPeptideLength());
        peptideLength.setSecondSelectedValue(myriMatchParameters.getMaxPeptideLength());
        precursorMass.setFirstSelectedValue(myriMatchParameters.getMinPrecursorMass());
        precursorMass.setSecondSelectedValue(myriMatchParameters.getMaxPrecursorMass());
        numberOfSpectrumMatches.setSelectedValue(myriMatchParameters.getNumberOfSpectrumMatches());
        maxVariablePTMperPeptide.setSelectedValue(myriMatchParameters.getMaxDynamicMods());
        fragmentationMethodList.setSelected(myriMatchParameters.getFragmentationRule());
        enzymaticTerminalsList.setSelected(myriMatchParameters.getMinTerminiCleavages());

        if (myriMatchParameters.getUseSmartPlusThreeModel()) {
            useSmartPlusTreeModelList.setSelected("Yes");
        } else {
            useSmartPlusTreeModelList.setSelected("No");
        }
        if (myriMatchParameters.getComputeXCorr()) {
            computeXCorrList.setSelected("Yes");
        } else {
            computeXCorrList.setSelected("No");
        }
        ticCuttoffPercentage.setSelectedValue(myriMatchParameters.getTicCutoffPercentage());
        numberOfIntensityClasses.setSelectedValue(myriMatchParameters.getNumIntensityClasses());
        classSizeMultiplier.setSelectedValue(myriMatchParameters.getClassSizeMultiplier());
        numberOfBatches.setSelectedValue(myriMatchParameters.getNumberOfBatches());
        maxPeakCount.setSelectedValue(myriMatchParameters.getMaxPeakCount());
        outputFormat.setSelected(myriMatchParameters.getOutputFormat());

    }

    @Override
    public void onClosePopup() {
    }

    @Override
    public void setPopupVisible(boolean visible) {
        if (visible && webSearchParameters != null) {
            updateGUI(webSearchParameters);
        }
//        else if (webSearchParameters != null) {
//            updateParameters();
//        }
        super.setPopupVisible(visible); //To change body of generated methods, choose Tools | Templates.
    }

    private void updateParameters() {
        MyriMatchParameters myriMatchParameters = (MyriMatchParameters) webSearchParameters.getSearchParameters().getIdentificationAlgorithmParameter(Advocate.myriMatch.getIndex());
        myriMatchParameters.setMinPeptideLength(Integer.valueOf(peptideLength.getFirstSelectedValue()));
        myriMatchParameters.setMaxPeptideLength(Integer.valueOf(peptideLength.getSecondSelectedValue()));
        myriMatchParameters.setNumberOfSpectrumMatches(Integer.valueOf(numberOfSpectrumMatches.getSelectedValue()));
        myriMatchParameters.setMaxDynamicMods(Integer.valueOf(maxVariablePTMperPeptide.getSelectedValue()));
        myriMatchParameters.setNumIntensityClasses(Integer.valueOf(numberOfIntensityClasses.getSelectedValue()));
        myriMatchParameters.setClassSizeMultiplier(Integer.valueOf(classSizeMultiplier.getSelectedValue()));
        myriMatchParameters.setNumberOfBatches(Integer.valueOf(numberOfBatches.getSelectedValue()));
        myriMatchParameters.setMaxPeakCount(Integer.valueOf(maxPeakCount.getSelectedValue()));

        myriMatchParameters.setMinPrecursorMass(Double.valueOf(precursorMass.getFirstSelectedValue()));
        myriMatchParameters.setMaxPrecursorMass(Double.valueOf(precursorMass.getSecondSelectedValue()));
        myriMatchParameters.setTicCutoffPercentage(Double.valueOf(ticCuttoffPercentage.getSelectedValue()));
        myriMatchParameters.setFragmentationRule(fragmentationMethodList.getSelectedValue());
        myriMatchParameters.setOutputFormat(outputFormat.getSelectedValue());
        myriMatchParameters.setMinTerminiCleavages(Integer.valueOf(enzymaticTerminalsList.getSelectedValue()));
        myriMatchParameters.setUseSmartPlusThreeModel(useSmartPlusTreeModelList.getSelectedValue().equalsIgnoreCase("Yes"));
        myriMatchParameters.setComputeXCorr(computeXCorrList.getSelectedValue().equalsIgnoreCase("Yes"));

        peptideLength.setFirstSelectedValue(myriMatchParameters.getMinPeptideLength());
        peptideLength.setSecondSelectedValue(myriMatchParameters.getMaxPeptideLength());
        precursorMass.setFirstSelectedValue(myriMatchParameters.getMinPrecursorMass());
        precursorMass.setSecondSelectedValue(myriMatchParameters.getMaxPrecursorMass());
        numberOfSpectrumMatches.setSelectedValue(myriMatchParameters.getNumberOfSpectrumMatches());
        maxVariablePTMperPeptide.setSelectedValue(myriMatchParameters.getMaxDynamicMods());
        fragmentationMethodList.setSelected(myriMatchParameters.getFragmentationRule());
        enzymaticTerminalsList.setSelected(myriMatchParameters.getMinTerminiCleavages());

        if (myriMatchParameters.getUseSmartPlusThreeModel()) {
            useSmartPlusTreeModelList.setSelected("Yes");
        } else {
            useSmartPlusTreeModelList.setSelected("No");
        }
        if (myriMatchParameters.getComputeXCorr()) {
            computeXCorrList.setSelected("Yes");
        } else {
            computeXCorrList.setSelected("No");
        }
        ticCuttoffPercentage.setSelectedValue(myriMatchParameters.getTicCutoffPercentage());
        numberOfIntensityClasses.setSelectedValue(myriMatchParameters.getNumIntensityClasses());
        classSizeMultiplier.setSelectedValue(myriMatchParameters.getClassSizeMultiplier());
        numberOfBatches.setSelectedValue(myriMatchParameters.getNumberOfBatches());
        maxPeakCount.setSelectedValue(myriMatchParameters.getMaxPeakCount());
        outputFormat.setSelected(myriMatchParameters.getOutputFormat());
    }

}
