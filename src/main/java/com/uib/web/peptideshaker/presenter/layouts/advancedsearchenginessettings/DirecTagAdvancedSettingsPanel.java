package com.uib.web.peptideshaker.presenter.layouts.advancedsearchenginessettings;

import com.compomics.util.experiment.identification.Advocate;
import com.compomics.util.parameters.identification.IdentificationParameters;
import com.compomics.util.parameters.identification.tool_specific.DirecTagParameters;
import com.uib.web.peptideshaker.ui.components.items.HelpPopupButton;
import com.uib.web.peptideshaker.ui.views.modal.PopupWindow;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabelDropDownList;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabelTextField;
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
public class DirecTagAdvancedSettingsPanel extends PopupWindow {

    private final HorizontalLabelTextField tagLength;
    private final HorizontalLabelTextField maxVariabalePTMs;
    private final HorizontalLabelTextField numberChargeState;
    private final HorizontalLabelDropDownList duplicateSpectraPerCharge;
    private final HorizontalLabelTextField isotopeMZTolerance;
    private final HorizontalLabelTextField deisptopingMode;
    private final HorizontalLabelTextField numberIntensityClasses;
    private final HorizontalLabelTextField outputSuffix;
    private final HorizontalLabelTextField maxPeakCount;
    private final HorizontalLabelTextField maxTagCount;

    private final HorizontalLabelTextField ticCuttoffPercentage;
    private final HorizontalLabelTextField complementMZTolerance;
    private final HorizontalLabelTextField precursorAdjustmentStep;
    private final HorizontalLabelTextField minPrecursorAdjustment;
    private final HorizontalLabelTextField maxPrecursorAdjustment;
    private final HorizontalLabelTextField intensityScoreWeight;
    private final HorizontalLabelTextField MZFidelityScoreWeight;
    private final HorizontalLabelTextField complementScoreWeight;

    private final HorizontalLabelDropDownList adjustPrecursorMass;
    private final HorizontalLabelDropDownList useSpectrumChargeState;
    private IdentificationParameters webSearchParameters;

    public DirecTagAdvancedSettingsPanel() {
        super(VaadinIcons.COG.getHtml() + " DirecTag Advanced Settings");
        AbsoluteLayout container = new AbsoluteLayout();
        container.setStyleName("popuppanelmaincontainer");
        container.setWidth(500, Unit.PIXELS);
        container.setHeight(500, Unit.PIXELS);

        Label title = new Label("DirecTag");
        container.addComponent(title, "left:10px;top:10px");
        HorizontalLayout subContainersFrame = new HorizontalLayout();
        subContainersFrame.setSizeFull();
        container.addComponent(subContainersFrame, "left:10px;top:45px;right:10px;bottom:40px");

        VerticalLayout leftSubContainer = new VerticalLayout();
        leftSubContainer.setSizeFull();
        leftSubContainer.setStyleName("subcontainer");
        subContainersFrame.addComponent(leftSubContainer);

        VerticalLayout rightSubContainer = new VerticalLayout();
        rightSubContainer.setSizeFull();
        rightSubContainer.setStyleName("subcontainer");
        subContainersFrame.addComponent(rightSubContainer);

        DirecTagAdvancedSettingsPanel.this.setContent(container);
        DirecTagAdvancedSettingsPanel.this.setClosable(true);

        tagLength = new HorizontalLabelTextField("Tag Length", 0, new IntegerRangeValidator("Postive integer only allowed", 0, Integer.MAX_VALUE));
        leftSubContainer.addComponent(tagLength);

        maxVariabalePTMs = new HorizontalLabelTextField("Max number of Variable PTMs", 0, new IntegerRangeValidator("Postive integer only allowed", 0, Integer.MAX_VALUE));
        leftSubContainer.addComponent(maxVariabalePTMs);

        numberChargeState = new HorizontalLabelTextField("Number Charge State", 0, new IntegerRangeValidator("Postive integer only allowed", 0, Integer.MAX_VALUE));
        leftSubContainer.addComponent(numberChargeState);

        duplicateSpectraPerCharge = new HorizontalLabelDropDownList("Duplicate Spectra Per Charge");
        leftSubContainer.addComponent(duplicateSpectraPerCharge);
        Set<String> values = new LinkedHashSet<>();
        values.add("Yes");
        values.add("No");
        duplicateSpectraPerCharge.updateData(values);

        isotopeMZTolerance = new HorizontalLabelTextField("Isotope MZ Tolerance (Da)", 0.0, new DoubleRangeValidator("Postive double only allowed", 0.0, Double.MAX_VALUE));
        leftSubContainer.addComponent(isotopeMZTolerance);

        deisptopingMode = new HorizontalLabelTextField("Deisptoping Mode", 0, new IntegerRangeValidator("Postive integer only allowed", 0, Integer.MAX_VALUE));
        leftSubContainer.addComponent(deisptopingMode);

        numberIntensityClasses = new HorizontalLabelTextField("Number of Intensity Classes", 0, new IntegerRangeValidator("Postive integer only allowed", 0, Integer.MAX_VALUE));
        leftSubContainer.addComponent(numberIntensityClasses);

        outputSuffix = new HorizontalLabelTextField("Output Suffix", "", null);
        leftSubContainer.addComponent(outputSuffix);

        maxPeakCount = new HorizontalLabelTextField("Max Peak Count", 0, new IntegerRangeValidator("Postive integer only allowed", 0, Integer.MAX_VALUE));
        leftSubContainer.addComponent(maxPeakCount);

        maxTagCount = new HorizontalLabelTextField("Max Tag Count", 1, new IntegerRangeValidator("Allowed range 1-2000", 1, 2000));
        leftSubContainer.addComponent(maxTagCount);

        ticCuttoffPercentage = new HorizontalLabelTextField("TIC Cuttoff Percentage", 0.0, new DoubleRangeValidator("Allowed range 0-100%", 0.0, 100.0));
        rightSubContainer.addComponent(ticCuttoffPercentage);

        complementMZTolerance = new HorizontalLabelTextField("Complement MZ Tolerance (Da)", 0.0, new DoubleRangeValidator("Postive double only allowed", 0.0, Double.MAX_VALUE));
        rightSubContainer.addComponent(complementMZTolerance);

        precursorAdjustmentStep = new HorizontalLabelTextField("Precursor Adjustment Step", 0.0, new DoubleRangeValidator("Postive double only allowed", 0.0, Double.MAX_VALUE));
        rightSubContainer.addComponent(precursorAdjustmentStep);

        minPrecursorAdjustment = new HorizontalLabelTextField("Min Precursor Adjustment", 0.0, new DoubleRangeValidator("Only double value allowed", (-1 * Double.MAX_VALUE), Double.MAX_VALUE));
        rightSubContainer.addComponent(minPrecursorAdjustment);

        maxPrecursorAdjustment = new HorizontalLabelTextField("Max Precursor Adjustment", 0.0, new DoubleRangeValidator("Postive double only allowed", 0.0, Double.MAX_VALUE));
        rightSubContainer.addComponent(maxPrecursorAdjustment);

        intensityScoreWeight = new HorizontalLabelTextField("Intensity Score Weight", 0.0, new DoubleRangeValidator("Postive double only allowed", 0.0, Double.MAX_VALUE));
        rightSubContainer.addComponent(intensityScoreWeight);

        MZFidelityScoreWeight = new HorizontalLabelTextField("MZ Fidelity Score Weight", 0.0, new DoubleRangeValidator("Postive double only allowed", 0.0, Double.MAX_VALUE));
        rightSubContainer.addComponent(MZFidelityScoreWeight);

        complementScoreWeight = new HorizontalLabelTextField("Complement Score Weight", 0.0, new DoubleRangeValidator("Postive double only allowed", 0.0, Double.MAX_VALUE));
        rightSubContainer.addComponent(complementScoreWeight);

        adjustPrecursorMass = new HorizontalLabelDropDownList("Adjust Precursor Mass");
        rightSubContainer.addComponent(adjustPrecursorMass);
        adjustPrecursorMass.updateData(values);

        useSpectrumChargeState = new HorizontalLabelDropDownList("Use Spectrum Charge State");
        rightSubContainer.addComponent(useSpectrumChargeState);
        useSpectrumChargeState.updateData(values);
        maxPeakCount.setEnabled(false);
        adjustPrecursorMass.setEnabled(false);

        String helpText = "<a href='' target='_blank'>";
        HelpPopupButton help = new HelpPopupButton(helpText, "<font style='line-height: 20px;'>Click to open the DirecTag help page.</font>", 100, 20);
        container.addComponent(help, "left:10px;bottom:10px;");
        Button okBtn = new Button("OK");
        okBtn.setWidth(76, Unit.PIXELS);
        okBtn.setHeight(20, Unit.PIXELS);
        okBtn.setStyleName(ValoTheme.BUTTON_TINY);
        okBtn.addClickListener((Button.ClickEvent event) -> {
            if (tagLength.isValid() && maxVariabalePTMs.isValid() && isotopeMZTolerance.isValid() && numberChargeState.isValid() && deisptopingMode.isValid() && numberIntensityClasses.isValid() && maxPeakCount.isValid() && maxTagCount.isValid() && ticCuttoffPercentage.isValid()
                    && complementMZTolerance.isValid() && precursorAdjustmentStep.isValid() && minPrecursorAdjustment.isValid() && maxPrecursorAdjustment.isValid() && intensityScoreWeight.isValid()
                    && MZFidelityScoreWeight.isValid() && complementScoreWeight.isValid()) {
                updateParameters();
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
            DirecTagAdvancedSettingsPanel.this.setPopupVisible(false);
        });

    }

    public void updateGUI(IdentificationParameters webSearchParameters) {
        this.webSearchParameters = webSearchParameters;
        DirecTagParameters direcTagParameters = (DirecTagParameters) webSearchParameters.getSearchParameters().getIdentificationAlgorithmParameter(Advocate.direcTag.getIndex());
        tagLength.setSelectedValue(direcTagParameters.getTagLength());
        maxVariabalePTMs.setSelectedValue(direcTagParameters.getMaxDynamicMods());
        numberChargeState.setSelectedValue(direcTagParameters.getNumChargeStates());
        if (direcTagParameters.isDuplicateSpectra()) {
            duplicateSpectraPerCharge.setSelected("Yes");
        } else {
            duplicateSpectraPerCharge.setSelected("No");
        }
        isotopeMZTolerance.setSelectedValue(direcTagParameters.getIsotopeMzTolerance());
        deisptopingMode.setSelectedValue(direcTagParameters.getDeisotopingMode());
        numberIntensityClasses.setSelectedValue(direcTagParameters.getNumIntensityClasses());
        outputSuffix.setSelectedValue(direcTagParameters.getOutputSuffix());
        maxPeakCount.setSelectedValue(direcTagParameters.getMaxPeakCount());
        maxTagCount.setSelectedValue(direcTagParameters.getMaxTagCount());

        ticCuttoffPercentage.setSelectedValue(direcTagParameters.getTicCutoffPercentage());
        complementMZTolerance.setSelectedValue(direcTagParameters.getComplementMzTolerance());
        precursorAdjustmentStep.setSelectedValue(direcTagParameters.getPrecursorAdjustmentStep());
        minPrecursorAdjustment.setSelectedValue(direcTagParameters.getMinPrecursorAdjustment());
        maxPrecursorAdjustment.setSelectedValue(direcTagParameters.getMaxPrecursorAdjustment());
        intensityScoreWeight.setSelectedValue(direcTagParameters.getIntensityScoreWeight());
        MZFidelityScoreWeight.setSelectedValue(direcTagParameters.getMzFidelityScoreWeight());
        complementScoreWeight.setSelectedValue(direcTagParameters.getComplementScoreWeight());
        if (direcTagParameters.isAdjustPrecursorMass()) {
            adjustPrecursorMass.setSelected("Yes");
        } else {
            adjustPrecursorMass.setSelected("No");
        }
        if (direcTagParameters.isUseChargeStateFromMS()) {
            useSpectrumChargeState.setSelected("Yes");
        } else {
            useSpectrumChargeState.setSelected("No");
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
//else if (webSearchParameters != null) {
//            updateParameters();
//        }
        super.setPopupVisible(visible); //To change body of generated methods, choose Tools | Templates.
    }

    private void updateParameters() {
        DirecTagParameters direcTagParameters = (DirecTagParameters) webSearchParameters.getSearchParameters().getIdentificationAlgorithmParameter(Advocate.direcTag.getIndex());
        direcTagParameters.setTagLength(Integer.valueOf(tagLength.getSelectedValue()));
        direcTagParameters.setMaxDynamicMods(Integer.valueOf(maxVariabalePTMs.getSelectedValue()));
        direcTagParameters.setNumChargeStates(Integer.valueOf(numberChargeState.getSelectedValue()));
        direcTagParameters.setDuplicateSpectra(duplicateSpectraPerCharge.getSelectedValue().equalsIgnoreCase("Yes"));
        direcTagParameters.setIsotopeMzTolerance(Double.valueOf(isotopeMZTolerance.getSelectedValue().replace(",", "")));
        direcTagParameters.setDeisotopingMode(Integer.valueOf(deisptopingMode.getSelectedValue()));

        direcTagParameters.setNumIntensityClasses(Integer.valueOf(numberIntensityClasses.getSelectedValue()));
        direcTagParameters.setMaxPeakCount(Integer.valueOf(maxPeakCount.getSelectedValue()));
        direcTagParameters.setMaxTagCount(Integer.valueOf(maxTagCount.getSelectedValue()));

        direcTagParameters.setTicCutoffPercentage(Double.valueOf(ticCuttoffPercentage.getSelectedValue().replace(",", "")));
        direcTagParameters.setComplementMzTolerance(Integer.valueOf(complementMZTolerance.getSelectedValue()));
        direcTagParameters.setPrecursorAdjustmentStep(Double.valueOf(precursorAdjustmentStep.getSelectedValue().replace(",", "")));
        direcTagParameters.setMinPrecursorAdjustment(Double.valueOf(minPrecursorAdjustment.getSelectedValue().replace(",", "")));
        direcTagParameters.setMaxPrecursorAdjustment(Double.valueOf(maxPrecursorAdjustment.getSelectedValue().replace(",", "")));
        direcTagParameters.setIntensityScoreWeight(Double.valueOf(intensityScoreWeight.getSelectedValue().replace(",", "")));
        direcTagParameters.setMzFidelityScoreWeight(Double.valueOf(MZFidelityScoreWeight.getSelectedValue().replace(",", "")));
        direcTagParameters.setComplementScoreWeight(Double.valueOf(complementScoreWeight.getSelectedValue().replace(",", "")));
        direcTagParameters.setAdjustPrecursorMass(adjustPrecursorMass.getSelectedValue().equalsIgnoreCase("Yes"));
        direcTagParameters.setUseChargeStateFromMS(useSpectrumChargeState.getSelectedValue().equalsIgnoreCase("Yes"));

    }

}
