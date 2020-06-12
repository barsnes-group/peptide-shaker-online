package com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.compomics.util.parameters.identification.advanced.ValidationQcParameters;
import com.compomics.util.parameters.identification.search.SearchParameters;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 *
 * @author y-mok
 */
public class QualityControlPanel extends PopupWindow {

    private final OptionGroup generalSettingsQualityControl;
    private IdentificationParameters webSearchParameters;

    public QualityControlPanel() {
        super(VaadinIcons.COG.getHtml() + " Quality Control");
        Label title = new Label("Quality Control");
        AbsoluteLayout container = new AbsoluteLayout();
        container.setStyleName("popuppanelmaincontainer");
        container.setWidth(385, Unit.PIXELS);
        container.setHeight(225, Unit.PIXELS);

        VerticalLayout subContainer = new VerticalLayout();
        subContainer.setSizeFull();
        subContainer.setStyleName("importfiltersubcontainer");

        container.addComponent(title, "left:10px;top:10px");
        container.addComponent(subContainer, "left:10px;top:45px;right:10px;bottom:40px");
        generalSettingsQualityControl = new OptionGroup("Mark as Doubtful");
        generalSettingsQualityControl.setCaptionAsHtml(true);
        generalSettingsQualityControl.setSizeUndefined();
        generalSettingsQualityControl.setMultiSelect(true);
        generalSettingsQualityControl.setStyleName("optiongroup");

        generalSettingsQualityControl.addItem("dbCheck");
        generalSettingsQualityControl.addItem("nTargetCheck");
        generalSettingsQualityControl.addItem("confidenceCheck");
        generalSettingsQualityControl.setItemCaption("dbCheck", "Hits obtained on small databases (<" + SearchParameters.preferredMinSequences + " protein sequences)");
        generalSettingsQualityControl.setItemCaption("nTargetCheck", "Datasets with a low number of target hits");
        generalSettingsQualityControl.setItemCaption("confidenceCheck", "Hits near the confidence threshold (margin= 1 x resolution)");

        subContainer.addComponent(generalSettingsQualityControl);
        QualityControlPanel.this.setContent(container);
        QualityControlPanel.this.setClosable(true);

        Button okBtn = new Button("OK");
        okBtn.setWidth(76, Unit.PIXELS);
        okBtn.setHeight(20, Unit.PIXELS);
        okBtn.setStyleName(ValoTheme.BUTTON_TINY);
        okBtn.addClickListener((Button.ClickEvent event) -> {
            updateParameters();
                setPopupVisible(false);
            
        });
        
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyleName(ValoTheme.BUTTON_TINY);
        cancelBtn.setWidth(76, Unit.PIXELS);
        cancelBtn.setHeight(20, Unit.PIXELS);
        container.addComponent(okBtn, "bottom:10px;right:96px");
        container.addComponent(cancelBtn, "bottom:10px;right:10px");
        cancelBtn.addClickListener((Button.ClickEvent event) -> {
            QualityControlPanel.this.setPopupVisible(false);
        });

    }

    public void updateGUI(IdentificationParameters webSearchParameters) {
        this.webSearchParameters = webSearchParameters;
        ValidationQcParameters validationQcParameters = webSearchParameters.getIdValidationParameters().getValidationQCParameters();
        generalSettingsQualityControl.addItem("dbCheck");
        generalSettingsQualityControl.addItem("nTargetCheck");
        generalSettingsQualityControl.addItem("confidenceCheck");
        if (validationQcParameters.isDbSize()) {
            generalSettingsQualityControl.select("dbCheck");
        } else {
            generalSettingsQualityControl.unselect("dbCheck");
        }
        if (validationQcParameters.isFirstDecoy()) {
            generalSettingsQualityControl.select("nTargetCheck");
        } else {
            generalSettingsQualityControl.unselect("nTargetCheck");
        }
        if (validationQcParameters.getConfidenceMargin() == 1.0) {
            generalSettingsQualityControl.select("confidenceCheck");
        } else {
            generalSettingsQualityControl.unselect("confidenceCheck");
        }

        super.setLabelValue(VaadinIcons.COG.getHtml() + " Quality Control (beta)" + "<center>" + webSearchParameters.getIdValidationParameters().getValidationQCParameters().getShortDescription() + "</center>");

    }

    @Override
    public void onClosePopup() {
    }

    @Override
    public void setPopupVisible(boolean visible) {
        if (visible && webSearchParameters != null) {
            updateGUI(webSearchParameters);
        } else if (webSearchParameters != null) {
            super.setLabelValue(VaadinIcons.COG.getHtml() + " Quality Control (beta)" + "<center>" + webSearchParameters.getIdValidationParameters().getValidationQCParameters().getShortDescription() + "</center>");
        }
        super.setPopupVisible(visible); //To change body of generated methods, choose Tools | Templates.
    }

    private void updateParameters() {
        ValidationQcParameters validationQcParameters = webSearchParameters.getIdValidationParameters().getValidationQCParameters();
        validationQcParameters.setDbSize(generalSettingsQualityControl.isSelected("dbCheck"));
        validationQcParameters.setFirstDecoy(generalSettingsQualityControl.isSelected("nTargetCheck"));

        if (generalSettingsQualityControl.isSelected("confidenceCheck")) {
            validationQcParameters.setConfidenceMargin(1.0);
        } else {
            validationQcParameters.setConfidenceMargin(0.0);
        }

    }


}
