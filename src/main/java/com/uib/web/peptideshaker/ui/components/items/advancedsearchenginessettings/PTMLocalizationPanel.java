package com.uib.web.peptideshaker.ui.components.items.advancedsearchenginessettings;

import com.compomics.util.experiment.identification.modification.ModificationLocalizationScore;
import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.ui.views.modal.PopupWindow;
import com.uib.web.peptideshaker.ui.components.items.HorizontalLabelDropDownList;
import com.uib.web.peptideshaker.ui.components.items.HorizontalLabelTextField;
import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author y-mok
 */
public class PTMLocalizationPanel extends PopupWindow {

    private final HorizontalLabelDropDownList probabilisticScore;
    private final HorizontalLabelDropDownList accountNeutralLosses;
    private final HorizontalLabelDropDownList confidentSites;
    private final HorizontalLabelTextField threshold;

    private IdentificationParameters webSearchParameters;

    public PTMLocalizationPanel() {
        super(VaadinIcons.COG.getHtml() + " PTM Localization");
        Label title = new Label("PTM Localization");
        AbsoluteLayout container = new AbsoluteLayout();
        container.setStyleName("popuppanelmaincontainer");
        container.setWidth(400, Unit.PIXELS);
        container.setHeight(256, Unit.PIXELS);

        VerticalLayout subContainer = new VerticalLayout();
        subContainer.setSizeFull();
        subContainer.setStyleName("importfiltersubcontainer");
        subContainer.setCaption("Modification Scoring");
        Set<String> values = new LinkedHashSet<>();
        values.add(ModificationLocalizationScore.PhosphoRS.getName());
        values.add(ModificationLocalizationScore.None.getName());

        probabilisticScore = new HorizontalLabelDropDownList("Probabilistic Score");
        probabilisticScore.updateData(values);
        values.clear();

        values.add("Yes");
        values.add("No");

        accountNeutralLosses = new HorizontalLabelDropDownList("Account Neutral Losses");
        accountNeutralLosses.updateData(values);
        threshold = new HorizontalLabelTextField("Threshold", 0.0, new DoubleRangeValidator("Only double values allowd", (-1 * Double.MAX_VALUE), Double.MAX_VALUE));

        confidentSites = new HorizontalLabelDropDownList("Confident Sites");
        confidentSites.updateData(values);

        container.addComponent(title, "left:10px;top:10px");
        container.addComponent(subContainer, "left:10px;top:70px;right:10px;bottom:110px");
        subContainer.addComponent(probabilisticScore);
        subContainer.addComponent(accountNeutralLosses);
        subContainer.addComponent(threshold);

//        Label title2 = new Label("Site Alignment");
        VerticalLayout subContainer2 = new VerticalLayout();
        subContainer2.setSizeFull();
        subContainer2.setStyleName("importfiltersubcontainer");
        subContainer2.setCaption("Site Alignment");
//        container.addComponent(title2, "left:10px;top:150px");
        container.addComponent(subContainer2, "left:10px;top:180px;right:10px;bottom:40px");

        subContainer2.addComponent(confidentSites);

        PTMLocalizationPanel.this.setContent(container);
        PTMLocalizationPanel.this.setClosable(true);

        Button okBtn = new Button("OK");
        okBtn.setWidth(76, Unit.PIXELS);
        okBtn.setHeight(20, Unit.PIXELS);
        okBtn.setStyleName(ValoTheme.BUTTON_TINY);
        okBtn.addClickListener((Button.ClickEvent event) -> {
            setPopupVisible(false);

        });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyleName(ValoTheme.BUTTON_TINY);
        cancelBtn.setWidth(76, Unit.PIXELS);
        cancelBtn.setHeight(20, Unit.PIXELS);
        container.addComponent(okBtn, "bottom:10px;right:96px");
        container.addComponent(cancelBtn, "bottom:10px;right:10px");
        cancelBtn.addClickListener((Button.ClickEvent event) -> {
            PTMLocalizationPanel.this.setPopupVisible(false);
        });

    }

    public void updateGUI(IdentificationParameters webSearchParameters) {
        this.webSearchParameters = webSearchParameters;
        probabilisticScore.setSelected(webSearchParameters.getModificationLocalizationParameters().getSelectedProbabilisticScore().getName());

        if (webSearchParameters.getModificationLocalizationParameters().isProbabilisticScoreNeutralLosses()) {
            accountNeutralLosses.setSelected("Yes");
        } else {
            accountNeutralLosses.setSelected("No");
        }

        if (webSearchParameters.getModificationLocalizationParameters().getAlignNonConfidentModifications()) {
            confidentSites.setSelected("Yes");
        } else {
            confidentSites.setSelected("No");
        }

        threshold.setSelectedValue(webSearchParameters.getModificationLocalizationParameters().getProbabilisticScoreThreshold());

        super.setLabelValue(VaadinIcons.COG.getHtml() + " PTM Localization" + "<center>" + webSearchParameters.getModificationLocalizationParameters().getShortDescription() + "</center>");

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
            super.setLabelValue(VaadinIcons.COG.getHtml() + " PTM Localization" + "<center>" + webSearchParameters.getModificationLocalizationParameters().getShortDescription() + "</center>");
        }
        super.setPopupVisible(visible); //To change body of generated methods, choose Tools | Templates.
    }

    private void updateParameters() {
        webSearchParameters.getModificationLocalizationParameters().setSelectedProbabilisticScore(ModificationLocalizationScore.getScore(probabilisticScore.getSelectedValue()));
        webSearchParameters.getModificationLocalizationParameters().setProbabilisticScoreNeutralLosses(accountNeutralLosses.getSelectedValue().equalsIgnoreCase("Yes"));
        webSearchParameters.getModificationLocalizationParameters().setAlignNonConfidentModifications(confidentSites.getSelectedValue().equalsIgnoreCase("Yes"));
        webSearchParameters.getModificationLocalizationParameters().setProbabilisticScoreThreshold(Double.valueOf(threshold.getSelectedValue()));
    }

}
