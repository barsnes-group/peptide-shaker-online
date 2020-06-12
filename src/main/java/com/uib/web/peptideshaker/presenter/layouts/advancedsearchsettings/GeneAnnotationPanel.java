package com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabelDropDownList;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author y-mok
 */
public class GeneAnnotationPanel extends PopupWindow {

    private final HorizontalLabelDropDownList useMapping;
    private final HorizontalLabelDropDownList autoUpdate;

    private IdentificationParameters webSearchParameters;

    public GeneAnnotationPanel() {
        super(VaadinIcons.COG.getHtml() + " Gene Annotation");
        Label title = new Label("Gene Annotation");
        AbsoluteLayout container = new AbsoluteLayout();
        container.setStyleName("popuppanelmaincontainer");
        container.setWidth(450, Unit.PIXELS);
        container.setHeight(150, Unit.PIXELS);

        VerticalLayout subContainer = new VerticalLayout();
        subContainer.setSizeFull();
        subContainer.setStyleName("importfiltersubcontainer");

        Set<String> values = new LinkedHashSet<>();
        values.add("Yes");
        values.add("No");

        autoUpdate = new HorizontalLabelDropDownList("&nbsp&nbsp-Based on UniProt Evidence Level");
        autoUpdate.updateData(values);

        container.addComponent(title, "left:10px;top:10px");
        container.addComponent(subContainer, "left:10px;top:45px;right:10px;bottom:40px");
        useMapping = new HorizontalLabelDropDownList("Simplify Protein Froups");
        useMapping.updateData(values);
        subContainer.addComponent(useMapping);
        subContainer.addComponent(autoUpdate);

        GeneAnnotationPanel.this.setContent(container);
        GeneAnnotationPanel.this.setClosable(true);

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
            GeneAnnotationPanel.this.setPopupVisible(false);
        });

    }

    public void updateGUI(IdentificationParameters webSearchParameters) {
        this.webSearchParameters = webSearchParameters;
        if (webSearchParameters.getGeneParameters().getUseGeneMapping()) {
            useMapping.setSelected("Yes");
        } else {
            useMapping.setSelected("No");
        }
        if (webSearchParameters.getGeneParameters().getAutoUpdate()) {
            autoUpdate.setSelected("Yes");
        } else {
            autoUpdate.setSelected("No");
        }

        super.setLabelValue(VaadinIcons.COG.getHtml() + " Gene Annotation" + "<center>" + webSearchParameters.getGeneParameters().getShortDescription() + "</center>");

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
            super.setLabelValue(VaadinIcons.COG.getHtml() + " Gene Annotation" + "<center>" + webSearchParameters.getGeneParameters().getShortDescription() + "</center>");
        }
        super.setPopupVisible(visible); //To change body of generated methods, choose Tools | Templates.
    }

    private void updateParameters() {
        webSearchParameters.getGeneParameters().setUseGeneMapping(useMapping.getSelectedValue().equalsIgnoreCase("Yes"));
        webSearchParameters.getGeneParameters().setUseGeneMapping(autoUpdate.getSelectedValue().equalsIgnoreCase("Yes"));

    }

}
