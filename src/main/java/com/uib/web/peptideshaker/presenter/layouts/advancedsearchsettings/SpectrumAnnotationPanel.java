/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings;

import com.compomics.util.experiment.biology.ions.Ion.IonType;
import com.compomics.util.experiment.biology.ions.IonFactory;
import com.compomics.util.experiment.biology.ions.NeutralLoss;
import com.compomics.util.experiment.biology.ions.impl.PeptideFragmentIon;
import com.compomics.util.experiment.biology.ions.impl.TagFragmentIon;
import com.compomics.util.experiment.identification.spectrum_annotation.AnnotationParameters;
import com.compomics.util.experiment.identification.spectrum_annotation.SpectrumAnnotator;
import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.presenter.core.MultiSelectOptionGroup;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabelTextField;
import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author y-mok
 */
public class SpectrumAnnotationPanel extends PopupWindow {

    private final MultiSelectOptionGroup ionTypesList;
    private final MultiSelectOptionGroup neutralLossList;
    private final HorizontalLabelTextField annotationLevel;
    private final HorizontalLabelTextField fragmentIonAccuracy;
    private final HorizontalLabelTextField fragmentIonAccuracyDa;
    private final OptionGroup listII;
    private final ComboBox list;
    private final GridLayout peakMatchingContainer;
    private IdentificationParameters webSearchParameters;
    private final AbsoluteLayout subSubContainer;
    private ArrayList<NeutralLoss> possibleNeutralLosses;
    /**
     * List of possible reporter ions.
     */
    private ArrayList<Integer> reporterIons;

    public SpectrumAnnotationPanel() {
        super(VaadinIcons.COG.getHtml() + " Spectrum Annotation");
        AbsoluteLayout container = new AbsoluteLayout();
        container.setStyleName("popuppanelmaincontainer");
        container.setWidth(500, Unit.PIXELS);
        container.setHeight(476, Unit.PIXELS);
        
        
         Label title = new Label("Spectrum Annotation");
        container.addComponent(title, "left:10px;top:10px;right:10px;bottom:50px;");
        
        
        VerticalLayout subContainer = new VerticalLayout();
        subContainer.setSizeFull();
        subContainer.setSpacing(true);
        subContainer.setStyleName("popuppanelcontainer");
        
        
        
        container.addComponent(subContainer, "left:10px;top:40px;right:10px;bottom:50px;");
        ionTypesList = new MultiSelectOptionGroup("", false) {
            @Override
            public void setEnabled(boolean enable) {

                if (!enable) {
                    this.removeStyleName("focos");
                }
                super.setEnabled(enable);
            }
        };
        ionTypesList.setCaption("Ion Types");
        ionTypesList.setWidth(100, Unit.PERCENTAGE);
        ionTypesList.setRequired(false, "Select at least 1  file");
        ionTypesList.setViewList(true);
        ionTypesList.addStyleName("smallscreenfloatright");
        ionTypesList.addStyleName("top220");
        ionTypesList.addStyleName("mgfliststyle");
        Map<String, String> ionTypes = new LinkedHashMap<>();
        ionTypes.put("a_ion", "a-ion");
        ionTypes.put("b_ion", "b-ion");
        ionTypes.put("c_ion", "c-ion");
        ionTypes.put("x_ion", "x-ion");
        ionTypes.put("y_ion", "y-ion");
        ionTypes.put("z_ion", "z-ion");
        ionTypes.put("Precursor", "Precursor");
        ionTypes.put("Immonium", "Immonium");
        ionTypes.put("Reporter", "Reporter");
        ionTypes.put("Related", "Related");
        ionTypesList.updateList(ionTypes);
        subContainer.addComponent(ionTypesList);
         subContainer.setExpandRatio(ionTypesList,36);

        neutralLossList = new MultiSelectOptionGroup("", false) {
            @Override
            public void setEnabled(boolean enable) {

                if (!enable) {
                    this.removeStyleName("focos");
                }
                super.setEnabled(enable);
            }
        };
        neutralLossList.setCaption("Neutral Loss");
        neutralLossList.setWidth(100, Unit.PERCENTAGE);
        neutralLossList.setRequired(false, "Select at least 1  file");
        neutralLossList.setViewList(true);
        neutralLossList.addStyleName("smallscreenfloatright");
        neutralLossList.addStyleName("top220");
        neutralLossList.addStyleName("mgfliststyle");

        subContainer.addComponent(neutralLossList);
         subContainer.setExpandRatio(neutralLossList,36);

        peakMatchingContainer = new GridLayout(2, 2);
        peakMatchingContainer.setSizeFull();
        subContainer.addComponent(peakMatchingContainer);
         subContainer.setExpandRatio(peakMatchingContainer,30);
        peakMatchingContainer.setCaption("Peak Matching");

        annotationLevel = new HorizontalLabelTextField("Annotation Level", 75, new IntegerRangeValidator("0-100% range", 0, 100));
        annotationLevel.addComponent(new Label("%", ContentMode.HTML));
        annotationLevel.updateExpandingRatio(0.5f, 0.5f);
        peakMatchingContainer.addComponent(annotationLevel, 0, 0);
        subSubContainer = new AbsoluteLayout();
        subSubContainer.setSizeFull();
        peakMatchingContainer.addComponent(subSubContainer, 0, 1);

        fragmentIonAccuracy = new HorizontalLabelTextField("Fragment Ion Accuracy", 10, new IntegerRangeValidator("0-10 ppm range", 0, 10));
        fragmentIonAccuracy.addComponent(new Label("ppm", ContentMode.HTML));
        subSubContainer.addComponent(fragmentIonAccuracy);
        fragmentIonAccuracyDa = new HorizontalLabelTextField("Fragment Ion Accuracy", 10000.0, new DoubleRangeValidator("0-10000 Da range", 0.0, 10000.0));
        fragmentIonAccuracyDa.updateExpandingRatio(0.5f, 0.5f);
        fragmentIonAccuracyDa.addComponent(new Label("Da", ContentMode.HTML));
        subSubContainer.addComponent(fragmentIonAccuracyDa);
        list = new ComboBox();
        list.setTextInputAllowed(false);
        list.setWidth(100, Unit.PERCENTAGE);
        list.setHeight(20, Unit.PIXELS);
        list.setStyleName(ValoTheme.COMBOBOX_SMALL);
        list.addStyleName(ValoTheme.COMBOBOX_TINY);
        list.addStyleName(ValoTheme.COMBOBOX_ALIGN_CENTER);
        list.setNullSelectionAllowed(false);
        list.setImmediate(true);
        list.addItem("percentile");
        list.addItem("snp");
        peakMatchingContainer.addComponent(list, 1, 0);

        listII = new OptionGroup();
        listII.setCaptionAsHtml(true);
        listII.setSizeUndefined();
        listII.setMultiSelect(true);
        listII.setStyleName("optiongroup");
        listII.addItem("High Resolution");
        peakMatchingContainer.addComponent(listII, 1, 1);

        SpectrumAnnotationPanel.this.setContent(container);
        SpectrumAnnotationPanel.this.setClosable(true);

        Button okBtn = new Button("OK");
        okBtn.setWidth(100, Unit.PIXELS);
        okBtn.setHeight(25, Unit.PIXELS);
        okBtn.setStyleName(ValoTheme.BUTTON_TINY);
        okBtn.addClickListener((Button.ClickEvent event) -> {
            saveParameters();
        });
       
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyleName(ValoTheme.BUTTON_TINY);
        cancelBtn.setWidth(100, Unit.PIXELS);
        cancelBtn.setHeight(25, Unit.PIXELS);
        container.addComponent(cancelBtn, "bottom:10px;right:10px");
        container.addComponent(okBtn , "bottom:10px;right:120px");
        cancelBtn.addClickListener((Button.ClickEvent event) -> {
            SpectrumAnnotationPanel.this.setPopupVisible(false);
        });
    }

    @Override
    public void onClosePopup() {

    }

    @Override
    public void setPopupVisible(boolean visible) {
        if (visible && webSearchParameters != null) {
            updateGUI(webSearchParameters);
        } else {
            super.setLabelValue(VaadinIcons.COG.getHtml() + " Spectrum Annotation" + "<center>" + annotationParameters.getShortDescription() + "</center>");
        }
        super.setPopupVisible(visible); //To change body of generated methods, choose Tools | Templates.
    }

    public void updateGUI(IdentificationParameters webSearchParameters) {
        this.webSearchParameters = webSearchParameters;

        annotationParameters = new AnnotationParameters(webSearchParameters.getSearchParameters());
        reporterIons = new ArrayList<>(IonFactory.getReporterIons(webSearchParameters.getSearchParameters().getModificationParameters()));
        super.setLabelValue(VaadinIcons.COG.getHtml() + " Spectrum Annotation" + "<center>" + annotationParameters.getShortDescription() + "</center>");
        ionTypesList.unselectAll();
        annotationParameters.getIonTypes().keySet().stream().map((it) -> {
            if (it.name().equalsIgnoreCase("TAG_FRAGMENT_ION")) {
                ionTypesList.selectByIndexes(annotationParameters.getIonTypes().get(it));
            }
            return it;
        }).map((it) -> {
            if (it.name().equalsIgnoreCase("PRECURSOR_ION")) {
                ionTypesList.selectByIndex(6);
            }
            return it;
        }).filter((it) -> (it.name().equalsIgnoreCase("IMMONIUM_ION"))).forEachOrdered((_item) -> {
            ionTypesList.selectByIndex(7);
        });
        if (annotationParameters.getReporterIons()) {
            ionTypesList.selectByIndex(8);
        }
        if (annotationParameters.getRelatedIons()) {
            ionTypesList.selectByIndex(9);
        }
        possibleNeutralLosses = annotationParameters.getNeutralLosses();
        Map<String, String> neutralLoss = new LinkedHashMap<>();
        possibleNeutralLosses.forEach((nl) -> {
            neutralLoss.put(nl.name, nl.name);
        });
        neutralLossList.updateList(neutralLoss);
        neutralLossList.selectAll();
        annotationLevel.setSelectedValue((int) (annotationParameters.getAnnotationIntensityLimit() * 100));
        if (annotationParameters.isFragmentIonPpm()) {
            fragmentIonAccuracy.setSelectedValue(annotationParameters.getFragmentIonAccuracy());
            fragmentIonAccuracy.setVisible(false);
            fragmentIonAccuracyDa.setVisible(true);
        } else {
            fragmentIonAccuracyDa.setSelectedValue(annotationParameters.getFragmentIonAccuracy());
            fragmentIonAccuracyDa.setVisible(true);
            fragmentIonAccuracy.setVisible(false);

        }
        list.setValue(annotationParameters.getIntensityThresholdType().name());
        if (annotationParameters.getTiesResolution().name().equalsIgnoreCase("mostAccurateMz")) {
            listII.select("High Resolution");
        } else {
            listII.unselect("High Resolution");
        }

    }

    private AnnotationParameters annotationParameters;

    private void saveParameters() {
        annotationParameters = getAnnotationSettings();
        this.webSearchParameters.setAnnotationParameters(annotationParameters);

        this.setPopupVisible(false);

    }

     /**
     * Returns the annotation settings as set by the user.
     *
     * @return the annotation settings as set by the user
     */

    public AnnotationParameters getAnnotationSettings() {

        AnnotationParameters annotationSettings = new AnnotationParameters();

        if (ionTypesList.getSelectedValue().contains("a_ion")) {
            annotationSettings.addIonType(IonType.PEPTIDE_FRAGMENT_ION, PeptideFragmentIon.A_ION);
            annotationSettings.addIonType(IonType.TAG_FRAGMENT_ION, TagFragmentIon.A_ION);
        }
        if (ionTypesList.getSelectedValue().contains("b_ion")) {
            annotationSettings.addIonType(IonType.PEPTIDE_FRAGMENT_ION, PeptideFragmentIon.B_ION);
            annotationSettings.addIonType(IonType.TAG_FRAGMENT_ION, TagFragmentIon.B_ION);
        }
        if (ionTypesList.getSelectedValue().contains("c_ion")) {
            annotationSettings.addIonType(IonType.PEPTIDE_FRAGMENT_ION, PeptideFragmentIon.C_ION);
            annotationSettings.addIonType(IonType.TAG_FRAGMENT_ION, TagFragmentIon.C_ION);
        }
        if (ionTypesList.getSelectedValue().contains("x_ion")) {
            annotationSettings.addIonType(IonType.PEPTIDE_FRAGMENT_ION, PeptideFragmentIon.X_ION);
            annotationSettings.addIonType(IonType.TAG_FRAGMENT_ION, TagFragmentIon.X_ION);
        }
        if (ionTypesList.getSelectedValue().contains("y_ion")) {
            annotationSettings.addIonType(IonType.PEPTIDE_FRAGMENT_ION, PeptideFragmentIon.Y_ION);
            annotationSettings.addIonType(IonType.TAG_FRAGMENT_ION, TagFragmentIon.Y_ION);
        }
        if (ionTypesList.getSelectedValue().contains("z_ion")) {
            annotationSettings.addIonType(IonType.PEPTIDE_FRAGMENT_ION, PeptideFragmentIon.Z_ION);
            annotationSettings.addIonType(IonType.TAG_FRAGMENT_ION, TagFragmentIon.Z_ION);
        }
        if (ionTypesList.getSelectedValue().contains("Precursor")) {
            annotationSettings.addIonType(IonType.PRECURSOR_ION);
        }
        if (ionTypesList.getSelectedValue().contains("Immonium")) {
            annotationSettings.addIonType(IonType.IMMONIUM_ION);
        }
        if (ionTypesList.getSelectedValue().contains("Reporter")) {
            reporterIons.forEach((reporterIonSubType) -> {
                annotationSettings.addIonType(IonType.REPORTER_ION, reporterIonSubType);
            });
        }
        if (ionTypesList.getSelectedValue().contains("Related")) {
            annotationSettings.addIonType(IonType.RELATED_ION);
        }

        if (list.getValue().toString().equalsIgnoreCase(AnnotationParameters.IntensityThresholdType.percentile.name())) {
            annotationSettings.setIntensityThresholdType(AnnotationParameters.IntensityThresholdType.percentile);
        } else {
            annotationSettings.setIntensityThresholdType(AnnotationParameters.IntensityThresholdType.snp);
        }
        annotationSettings.setIntensityLimit(Integer.valueOf(annotationLevel.getSelectedValue()) / 100.0);
        if (fragmentIonAccuracy.isVisible()) {
            annotationSettings.setFragmentIonAccuracy(Double.valueOf(fragmentIonAccuracy.getSelectedValue()));
            annotationSettings.setFragmentIonPpm(true);
        } else {
            annotationSettings.setFragmentIonAccuracy(Double.valueOf(fragmentIonAccuracyDa.getSelectedValue()));
            annotationSettings.setFragmentIonPpm(false);
        }
        SpectrumAnnotator.TiesResolution tiesResolution = listII.getValue().toString().contains("High Resolution") ? SpectrumAnnotator.TiesResolution.mostAccurateMz : SpectrumAnnotator.TiesResolution.mostIntense;
        annotationSettings.setTiesResolution(tiesResolution); //@TODO: replace by a drop down menu
        neutralLossList.getSelectedValue().forEach((entry) -> {
            annotationSettings.addNeutralLoss(NeutralLoss.getNeutralLoss(entry));
        });
        return annotationSettings;
    }

}
