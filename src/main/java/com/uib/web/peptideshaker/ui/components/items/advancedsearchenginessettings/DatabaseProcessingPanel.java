package com.uib.web.peptideshaker.ui.components.items.advancedsearchenginessettings;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.ui.views.modal.PopupWindow;
import com.uib.web.peptideshaker.ui.components.items.HorizontalLabelDropDownList;
import com.uib.web.peptideshaker.ui.components.items.HorizontalLabelTextField;
import com.vaadin.data.Property;
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
public class DatabaseProcessingPanel extends PopupWindow {

    private final HorizontalLabelDropDownList targetDecoy;
    private final HorizontalLabelDropDownList decoyType;
    private final HorizontalLabelTextField decoyTag;
    private final HorizontalLabelTextField decoyFileTag;

    private IdentificationParameters webSearchParameters;

    public DatabaseProcessingPanel() {
        super(VaadinIcons.COG.getHtml() + " Database Processing");
        Label title = new Label("Database Processing");
        AbsoluteLayout container = new AbsoluteLayout();
        container.setStyleName("popuppanelmaincontainer");
        container.setWidth(400, Unit.PIXELS);
        container.setHeight(185, Unit.PIXELS);

        VerticalLayout subContainer = new VerticalLayout();
        subContainer.setSizeFull();
        subContainer.setStyleName("importfiltersubcontainer");

        Set<String> values = new LinkedHashSet<>();
        values.add("Prefix");
        values.add("Suffix");

        decoyType = new HorizontalLabelDropDownList("Decoy Type");
        decoyType.updateData(values);

        decoyTag = new HorizontalLabelTextField("Decoy Tag", "_REVERSED", null);

        container.addComponent(title, "left:10px;top:10px");

        container.addComponent(subContainer, "left:10px;top:45px;right:10px;bottom:40px");
        values.clear();
        values.add("Yes");
        values.add("No");

        targetDecoy = new HorizontalLabelDropDownList("Target-Decoy");
        targetDecoy.updateData(values);
        subContainer.addComponent(targetDecoy);
        subContainer.addComponent(decoyType);
        subContainer.addComponent(decoyTag);
        decoyFileTag = new HorizontalLabelTextField("Decoy File Tag", "concatenated_target_decoy", null);
        subContainer.addComponent(decoyFileTag);

        targetDecoy.addValueChangeListener((Property.ValueChangeEvent event) -> {
            decoyType.setEnabled(targetDecoy.getSelectedValue().equalsIgnoreCase("Yes"));
            decoyTag.setEnabled(targetDecoy.getSelectedValue().equalsIgnoreCase("Yes"));
        });

        DatabaseProcessingPanel.this.setContent(container);
        DatabaseProcessingPanel.this.setClosable(true);

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
            DatabaseProcessingPanel.this.setPopupVisible(false);
        });

    }

    public void updateGUI(IdentificationParameters webSearchParameters) {
        this.webSearchParameters = webSearchParameters;

        if (webSearchParameters.getFastaParameters().isTargetDecoy()) {
            targetDecoy.setSelected("Yes");
        } else {
            targetDecoy.setSelected("No");
        }
        if (webSearchParameters.getFastaParameters().isDecoySuffix()) {
            decoyType.setSelected("Suffix");
        } else {
            decoyType.setSelected("Prefix");
        }

        decoyTag.setSelectedValue(webSearchParameters.getFastaParameters().getDecoyFlag());
        decoyFileTag.setSelectedValue(webSearchParameters.getFastaParameters().getTargetDecoyFileNameSuffix());
        super.setLabelValue(VaadinIcons.COG.getHtml() + " Database Processing" + "<center>" + webSearchParameters.getFastaParameters().getShortDescription() + "</center>");

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
            super.setLabelValue(VaadinIcons.COG.getHtml() + " Database Processing" + "<center>" + webSearchParameters.getFastaParameters().getShortDescription() + "</center>");
        }
        super.setPopupVisible(visible); //To change body of generated methods, choose Tools | Templates.
    }

    private void updateParameters() {
        webSearchParameters.getFastaParameters().setTargetDecoy(targetDecoy.getSelectedValue().equalsIgnoreCase("Yes"));
        webSearchParameters.getFastaParameters().setDecoySuffix(decoyType.getSelectedValue().equalsIgnoreCase("Suffix"));
        webSearchParameters.getFastaParameters().setDecoyFlag(decoyTag.getSelectedValue());
        webSearchParameters.getFastaParameters().setTargetDecoyFileNameSuffix(decoyFileTag.getSelectedValue());

    }

}
