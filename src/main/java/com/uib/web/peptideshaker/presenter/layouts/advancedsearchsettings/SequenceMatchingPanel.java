package com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.compomics.util.parameters.identification.advanced.SequenceMatchingParameters;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabelDropDownList;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabelTextField;
import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author y-mok
 */
public class SequenceMatchingPanel extends PopupWindow {

    private final HorizontalLabelTextField shareOfX;
    private final HorizontalLabelDropDownList matchingMethodList;
    private IdentificationParameters webSearchParameters;

    public SequenceMatchingPanel() {
        super(VaadinIcons.COG.getHtml() + " Sequence Matching");
        Label title = new Label("Sequence Matching");

        matchingMethodList = new HorizontalLabelDropDownList("Matching Method");
        Set<String> values = new LinkedHashSet<>();
        values.add(SequenceMatchingParameters.MatchingType.string.name());

        values.add(SequenceMatchingParameters.MatchingType.aminoAcid.name());
        values.add(SequenceMatchingParameters.MatchingType.indistiguishableAminoAcids.name());
        matchingMethodList.updateData(values);
        matchingMethodList.setItemCaption(SequenceMatchingParameters.MatchingType.string.name(), "Character Sequence");
        matchingMethodList.setItemCaption(SequenceMatchingParameters.MatchingType.aminoAcid.name(), "Amino Acids");
        matchingMethodList.setItemCaption(SequenceMatchingParameters.MatchingType.indistiguishableAminoAcids.name(), "Indistinguishable Amino Acids");
        shareOfX = new HorizontalLabelTextField("Maximum Share ofX's", 0.30, new DoubleRangeValidator("Range allowed between 0.05 and 0.95", 0.05, 0.95));
        AbsoluteLayout container = new AbsoluteLayout();
        container.setStyleName("popuppanelmaincontainer");
        container.setWidth(474, Unit.PIXELS);
        container.setHeight(145, Unit.PIXELS);

        VerticalLayout subContainer = new VerticalLayout();
        subContainer.setSizeFull();
        subContainer.setStyleName("subcontainer");
        subContainer.addComponent(matchingMethodList);
        subContainer.setComponentAlignment(matchingMethodList, Alignment.TOP_LEFT);
        subContainer.addComponent(shareOfX);
        subContainer.setComponentAlignment(shareOfX, Alignment.BOTTOM_LEFT);
        container.addComponent(title, "left:10px;top:10px");
        container.addComponent(subContainer, "left:10px;top:45px;right:10px;bottom:40px");
        SequenceMatchingPanel.this.setContent(container);
        SequenceMatchingPanel.this.setClosable(true);

        Button okBtn = new Button("OK");
        okBtn.setWidth(76, Unit.PIXELS);
        okBtn.setHeight(20, Unit.PIXELS);
        okBtn.setStyleName(ValoTheme.BUTTON_TINY);
        okBtn.addClickListener((Button.ClickEvent event) -> {
            if (shareOfX.isValid()) {
                setPopupVisible(false);
            }
        });
        container.addComponent(okBtn, "bottom:10px;right:96px");
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyleName(ValoTheme.BUTTON_TINY);
        cancelBtn.setWidth(76, Unit.PIXELS);
        cancelBtn.setHeight(20, Unit.PIXELS);
        container.addComponent(cancelBtn, "bottom:10px;right:10px");
        cancelBtn.addClickListener((Button.ClickEvent event) -> {
            SequenceMatchingPanel.this.setPopupVisible(false);
        });

    }

    public void updateGUI(IdentificationParameters webSearchParameters) {
        this.webSearchParameters = webSearchParameters;
        shareOfX.setSelectedValue(webSearchParameters.getSequenceMatchingParameters().getLimitX());
        matchingMethodList.setSelected(webSearchParameters.getSequenceMatchingParameters().getSequenceMatchingType().name());
        super.setLabelValue(VaadinIcons.COG.getHtml() + " Sequence Matching" + "<center>" + webSearchParameters.getSequenceMatchingParameters().getShortDescription() + "</center>");

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
            super.setLabelValue(VaadinIcons.COG.getHtml() + " Sequence Matching" + "<center>" + webSearchParameters.getSequenceMatchingParameters().getShortDescription() + "</center>");
        }
        super.setPopupVisible(visible); //To change body of generated methods, choose Tools | Templates.
    }

    private void updateParameters() {
        if (shareOfX.isValid()) {
            webSearchParameters.getSequenceMatchingParameters().setLimitX(Double.valueOf(shareOfX.getSelectedValue()));
        }
        webSearchParameters.getSequenceMatchingParameters().setSequenceMatchingType(SequenceMatchingParameters.MatchingType.valueOf(matchingMethodList.getSelectedValue()));

    }

}
