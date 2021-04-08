package com.uib.web.peptideshaker.ui.components.items.advancedsearchenginessettings;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.ui.views.modal.PopupWindow;
import com.uib.web.peptideshaker.ui.components.items.HorizontalLabelTextField;
import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author Yehia Mokhtar Farag
 */
public class FractionAnalysisPanel extends PopupWindow {

    private final HorizontalLabelTextField proteinConfidenceMW;
    private IdentificationParameters webSearchParameters;

    public FractionAnalysisPanel() {
        super(VaadinIcons.COG.getHtml() + " Fraction Analysis");
        Label title = new Label("Fraction Analysis");
        AbsoluteLayout container = new AbsoluteLayout();
        container.setStyleName("popuppanelmaincontainer");
        container.setWidth(360, Unit.PIXELS);
        container.setHeight(143, Unit.PIXELS);

        VerticalLayout subContainer = new VerticalLayout();
        subContainer.setSizeFull();
        subContainer.setCaption("Fractions (Beta)");
        subContainer.setStyleName("importfiltersubcontainer");
        subContainer.addStyleName("fractionsubcontainer");

        container.addComponent(title, "left:10px;top:10px");
        container.addComponent(subContainer, "left:10px;top:70px;right:10px;bottom:40px");
        proteinConfidenceMW = new HorizontalLabelTextField("Protein Confidence MW (%)", 0.0, new DoubleRangeValidator("0 to 100 % range", 0.0, 100.0));

        subContainer.addComponent(proteinConfidenceMW);

        FractionAnalysisPanel.this.setContent(container);
        FractionAnalysisPanel.this.setClosable(true);

        Button okBtn = new Button("OK");
        okBtn.setWidth(76, Unit.PIXELS);
        okBtn.setHeight(20, Unit.PIXELS);
        okBtn.setStyleName(ValoTheme.BUTTON_TINY);
        okBtn.addClickListener((Button.ClickEvent event) -> {
            if (proteinConfidenceMW.isValid()) {
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
            FractionAnalysisPanel.this.setPopupVisible(false);
        });

    }

    public void updateGUI(IdentificationParameters webSearchParameters) {
        this.webSearchParameters = webSearchParameters;
        proteinConfidenceMW.setSelectedValue(webSearchParameters.getFractionParameters().getProteinConfidenceMwPlots());
        super.setLabelValue(VaadinIcons.COG.getHtml() + " Fraction Analysis" + "<center>" + webSearchParameters.getFractionParameters().getShortDescription() + "</center>");

    }

    @Override
    public void onClosePopup() {
    }

    @Override
    public void setPopupVisible(boolean visible) {
        if (visible && webSearchParameters != null) {
            updateGUI(webSearchParameters);
        } else if (webSearchParameters != null) {
            updateParameters();
            super.setLabelValue(VaadinIcons.COG.getHtml() + " Fraction Analysis" + "<center>" + webSearchParameters.getFractionParameters().getShortDescription() + "</center>");
        }
        super.setPopupVisible(visible); //To change body of generated methods, choose Tools | Templates.
    }

    private void updateParameters() {
        webSearchParameters.getFractionParameters().setProteinConfidenceMwPlots(Double.valueOf(proteinConfidenceMW.getSelectedValue()));


    }

}
