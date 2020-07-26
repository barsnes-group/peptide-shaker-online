package com.uib.web.peptideshaker.presenter.layouts;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings.DatabaseProcessingPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings.FractionAnalysisPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings.GeneAnnotationPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings.ImportFiltersPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings.PSMScoringPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings.PTMLocalizationPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings.PeptideVariantsPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings.ProteinInferencePanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings.QualityControlPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings.SequenceMatchingPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings.SpectrumAnnotationPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings.ValidationLevelsPanel;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * This class represents advanced search settings layout that allow users to
 * modify the search input parameters
 *
 * @author Yehia Farag
 */
public class AdvancedSearchSettings extends VerticalLayout {

    private final PopupWindow popupAdvancedSettingLayout;
    private IdentificationParameters webSearchParam;
    private SpectrumAnnotationPanel spectrumAnnotationPanel;
    private SequenceMatchingPanel sequenceMatchingPanel;
    private ImportFiltersPanel importFilters;
    private ValidationLevelsPanel validationLevel;
    private FractionAnalysisPanel fractionAnalysis;
    private ProteinInferencePanel proteinInference;
    private GeneAnnotationPanel geneAnnotation;
    private PTMLocalizationPanel ptmLocalization;
    private DatabaseProcessingPanel databaseProcessing;
    private PeptideVariantsPanel peptideVariants;
    private PSMScoringPanel psmScoring;
    private QualityControlPanel qualityControl;

    public AdvancedSearchSettings() {
        AdvancedSearchSettings.this.setSizeFull();
        AdvancedSearchSettings.this.setStyleName("advancedsearchlayout");
//        Label title = new Label("( Advanced Settings )");
//        AdvancedSearchSettings.this.addComponent(title);
//        AdvancedSearchSettings.this.addLayoutClickListener(AdvancedSearchSettings.this);
        popupAdvancedSettingLayout = new PopupWindow("( Advanced Settings |") {
            @Override
            public void onClosePopup() {

            }
        };
        AdvancedSearchSettings.this.addComponent(popupAdvancedSettingLayout);
        AbsoluteLayout vlo = initLayout();
        vlo.setWidth(590, Unit.PIXELS);
        vlo.setHeight(590, Unit.PIXELS);
        popupAdvancedSettingLayout.setClosable(true);
        popupAdvancedSettingLayout.setContent(vlo);

    }

    private AbsoluteLayout initLayout() {
        AbsoluteLayout container = new AbsoluteLayout();
        container.setSizeFull();
        container.setStyleName("advsettingcontainer");
        Label title = new Label("<font style='font-size:15px;'>Advanced Settings</font>", ContentMode.HTML);
        container.addComponent(title, "left:10px;top:10px");

        VerticalLayout subContainer = new VerticalLayout();
        subContainer.setWidth(100, Unit.PERCENTAGE);
        subContainer.setHeightUndefined();
        container.addComponent(subContainer, "left:10px;top:37px;right:10px;");
        subContainer.setMargin(false);
        subContainer.setStyleName("advsettingssubcontainer");

        spectrumAnnotationPanel = new SpectrumAnnotationPanel();
        subContainer.addComponent(spectrumAnnotationPanel);

        sequenceMatchingPanel = new SequenceMatchingPanel();
        subContainer.addComponent(sequenceMatchingPanel);
        peptideVariants = new PeptideVariantsPanel();
        peptideVariants.setEnabled(false);
        peptideVariants.addStyleName("disablefont");
        subContainer.addComponent(peptideVariants);

        importFilters = new ImportFiltersPanel();
        subContainer.addComponent(importFilters);
        psmScoring = new PSMScoringPanel();
        subContainer.addComponent(psmScoring);
        ptmLocalization = new PTMLocalizationPanel();
        subContainer.addComponent(ptmLocalization);
        geneAnnotation = new GeneAnnotationPanel();
        subContainer.addComponent(geneAnnotation);
        proteinInference = new ProteinInferencePanel();
        subContainer.addComponent(proteinInference);
        validationLevel = new ValidationLevelsPanel();
        subContainer.addComponent(validationLevel);
        fractionAnalysis = new FractionAnalysisPanel();
        subContainer.addComponent(fractionAnalysis);
        qualityControl = new QualityControlPanel();
        subContainer.addComponent(qualityControl);

        databaseProcessing = new DatabaseProcessingPanel();
        subContainer.addComponent(databaseProcessing);

        return container;
    }

    /**
     * Update search input forms based on user selection (add/edit) from search
     * files drop-down list
     *
     * @param searchParameters search parameter object from selected parameter
     */
    public void updateAdvancedSearchParamForms(IdentificationParameters searchParameters) {
        this.webSearchParam = searchParameters;
        if (webSearchParam != null) {
            spectrumAnnotationPanel.updateGUI(searchParameters);
            sequenceMatchingPanel.updateGUI(searchParameters);
            importFilters.updateGUI(searchParameters);
            validationLevel.updateGUI(searchParameters);
            fractionAnalysis.updateGUI(searchParameters);
            proteinInference.updateGUI(searchParameters);
            geneAnnotation.updateGUI(searchParameters);
            ptmLocalization.updateGUI(searchParameters);
            databaseProcessing.updateGUI(searchParameters);
            peptideVariants.updateGUI(searchParameters);
            psmScoring.updateGUI(searchParameters);
            qualityControl.updateGUI(searchParameters);
        }

    }

}
