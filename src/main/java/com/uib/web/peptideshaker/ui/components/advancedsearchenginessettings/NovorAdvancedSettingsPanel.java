package com.uib.web.peptideshaker.ui.components.advancedsearchenginessettings;

import com.compomics.util.experiment.identification.Advocate;
import com.compomics.util.parameters.identification.IdentificationParameters;
import com.compomics.util.parameters.identification.tool_specific.NovorParameters;
import com.uib.web.peptideshaker.ui.components.items.HelpPopupButton;
import com.uib.web.peptideshaker.ui.views.modal.PopupWindow;
import com.uib.web.peptideshaker.ui.components.items.HorizontalLabelDropDownList;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Yehia Mokhtar Farag
 */
public class NovorAdvancedSettingsPanel extends PopupWindow {

    private final HorizontalLabelDropDownList fragmentaionMethod;
    private final HorizontalLabelDropDownList massAnalyzer;
    private IdentificationParameters webSearchParameters;

    /**
     *
     */
    public NovorAdvancedSettingsPanel() {
        super(VaadinIcons.COG.getHtml() + " Novor Advanced Settings");
        AbsoluteLayout container = new AbsoluteLayout();
        container.setStyleName("popuppanelmaincontainer");
        container.setWidth(385, Unit.PIXELS);
        container.setHeight(170, Unit.PIXELS);

        Label title = new Label("Novor");
        container.addComponent(title, "left:10px;top:10px");
        VerticalLayout subContainer = new VerticalLayout();
        subContainer.setCaption("De Novo Settings");

        subContainer.setSizeFull();
        subContainer.setStyleName("subcontainer");
        subContainer.addStyleName("importfiltersubcontainer");
        subContainer.addStyleName("noversubcontainer");
        container.addComponent(subContainer, "left:10px;top:70px;right:10px;bottom:40px");
        NovorAdvancedSettingsPanel.this.setContent(container);
        NovorAdvancedSettingsPanel.this.setClosable(true);

        fragmentaionMethod = new HorizontalLabelDropDownList("Fragmentation Method");
        subContainer.addComponent(fragmentaionMethod);
        Set<String> values2 = new LinkedHashSet<>();
        /**
         * The fragmentation type ID: 0: HCD, 1: CID.
         */
        values2.add("HCD");
        values2.add("CID");
        fragmentaionMethod.updateData(values2);

        massAnalyzer = new HorizontalLabelDropDownList("Mass Analyzer");
        subContainer.addComponent(massAnalyzer);
        values2.clear();
        values2.add("Trap");
        values2.add("TOF");
        values2.add("FT");
        massAnalyzer.updateData(values2);


        String helpText = "<a href='https://www.rapidnovor.com' target='_blank'>";
        HelpPopupButton help = new HelpPopupButton(helpText, "<font style='line-height: 20px;'>Click to open Rapid Novor  page.</font>", 100, 20);
        container.addComponent(help, "left:10px;bottom:10px;");
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
            NovorAdvancedSettingsPanel.this.setPopupVisible(false);
        });

    }

    /**
     *
     * @param webSearchParameters
     */
    public void updateGUI(IdentificationParameters webSearchParameters) {
        this.webSearchParameters = webSearchParameters;
        NovorParameters noverParameters = (NovorParameters) webSearchParameters.getSearchParameters().getIdentificationAlgorithmParameter(Advocate.novor.getIndex());


        massAnalyzer.setSelected(noverParameters.getMassAnalyzer() + "");
        fragmentaionMethod.setSelected(noverParameters.getFragmentationMethod() + "");


    }

    /**
     *
     */
    @Override
    public void onClosePopup() {
    }

    /**
     *
     * @param visible
     */
    @Override
    public void setPopupVisible(boolean visible) {
        if (visible && webSearchParameters != null) {
            updateGUI(webSearchParameters);
        } else if (webSearchParameters != null) {
            updateParameters();
        }
        super.setPopupVisible(visible); 
    }

    private void updateParameters() {
        NovorParameters noverParameters = (NovorParameters) webSearchParameters.getSearchParameters().getIdentificationAlgorithmParameter(Advocate.novor.getIndex());

        noverParameters.setMassAnalyzer(massAnalyzer.getSelectedValue());
        noverParameters.setFragmentationMethod(fragmentaionMethod.getSelectedValue());


    }

}
