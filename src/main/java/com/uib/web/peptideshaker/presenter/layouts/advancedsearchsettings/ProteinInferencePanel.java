package com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabelDropDounList;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabelTextField;
import com.vaadin.data.Property;
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
 *
 * @author y-mok
 */
public class ProteinInferencePanel extends PopupWindow {

    private final HorizontalLabelDropDounList simplyProteinGroups;
    private final HorizontalLabelDropDounList uniprotEvidenceLevel;
    private final HorizontalLabelDropDounList peptideConfidence;
    private final HorizontalLabelDropDounList enzymaticity;
    private final HorizontalLabelDropDounList variantMapping;
    private final HorizontalLabelTextField peptideIgnoredConfidence;
    private final HorizontalLabelDropDounList accountModificationforProteinMapping;

    private IdentificationParameters webSearchParameters;

    public ProteinInferencePanel() {
        super(VaadinIcons.COG.getHtml() + " Protein Inference");
        Label title = new Label("Protein Inference");
        AbsoluteLayout container = new AbsoluteLayout();
        container.setStyleName("popuppanelmaincontainer");
        container.setWidth(600, Unit.PIXELS);
        container.setHeight(285, Unit.PIXELS);

        VerticalLayout subContainer = new VerticalLayout();
        subContainer.setSizeFull();
        subContainer.setStyleName("importfiltersubcontainer");
        subContainer.setCaption("Protein Groups Simplification");

        Set<String> values = new LinkedHashSet<>();
        values.add("Yes");
        values.add("No");

        uniprotEvidenceLevel = new HorizontalLabelDropDounList("&nbsp&nbsp-Based on UniProt Evidence Level");
        uniprotEvidenceLevel.updateData(values);

        peptideConfidence = new HorizontalLabelDropDounList("&nbsp&nbsp-Based on Peptide Confidence");
        peptideConfidence.updateData(values);
        peptideIgnoredConfidence = new HorizontalLabelTextField("&nbsp&nbsp&nbsp&nbsp&nbspConfidence Below Which A Peptide Is Ignored", 0.0, new DoubleRangeValidator("Only double values allowd", (-1* Double.MAX_VALUE), Double.MAX_VALUE));

        enzymaticity = new HorizontalLabelDropDounList("&nbsp&nbsp-Based on Enzymaticity");
        enzymaticity.updateData(values);

        variantMapping = new HorizontalLabelDropDounList("&nbsp&nbsp-Based on Variant Mapping");
        variantMapping.updateData(values);

        accountModificationforProteinMapping = new HorizontalLabelDropDounList("Account for Modifications in Protein Mapping");
        accountModificationforProteinMapping.updateData(values);

        container.addComponent(title, "left:10px;top:10px");
        container.addComponent(subContainer, "left:10px;top:70px;right:10px;bottom:40px");
        simplyProteinGroups = new HorizontalLabelDropDounList("Simplify Protein Froups");
        simplyProteinGroups.updateData(values);
        subContainer.addComponent(simplyProteinGroups);
        subContainer.addComponent(uniprotEvidenceLevel);
        subContainer.addComponent(peptideConfidence);
        subContainer.addComponent(peptideIgnoredConfidence);
        subContainer.addComponent(enzymaticity);
        subContainer.addComponent(variantMapping);
        subContainer.addComponent(accountModificationforProteinMapping);

        simplyProteinGroups.addValueChangeListener((Property.ValueChangeEvent event) -> {
            uniprotEvidenceLevel.setEnabled(simplyProteinGroups.getSelectedValue().equalsIgnoreCase("Yes"));
            peptideConfidence.setEnabled(simplyProteinGroups.getSelectedValue().equalsIgnoreCase("Yes"));
            peptideIgnoredConfidence.setEnabled(simplyProteinGroups.getSelectedValue().equalsIgnoreCase("Yes"));
            enzymaticity.setEnabled(simplyProteinGroups.getSelectedValue().equalsIgnoreCase("Yes"));
            variantMapping.setEnabled(simplyProteinGroups.getSelectedValue().equalsIgnoreCase("Yes"));

        });

        ProteinInferencePanel.this.setContent(container);
        ProteinInferencePanel.this.setClosable(true);

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
            ProteinInferencePanel.this.setPopupVisible(false);
        });

    }

    public void updateGUI(IdentificationParameters webSearchParameters) {
        this.webSearchParameters = webSearchParameters;
        if (webSearchParameters.getProteinInferenceParameters().getSimplifyGroups()) {
            simplyProteinGroups.setSelected("Yes");
        } else {
            simplyProteinGroups.setSelected("No");
        }
        if (webSearchParameters.getProteinInferenceParameters().getSimplifyGroupsEvidence()) {
            uniprotEvidenceLevel.setSelected("Yes");
        } else {
            uniprotEvidenceLevel.setSelected("No");
        }
        if (webSearchParameters.getProteinInferenceParameters().getSimplifyGroupsEnzymaticity()) {
            enzymaticity.setSelected("Yes");
        } else {
            enzymaticity.setSelected("No");
        }
        if (webSearchParameters.getProteinInferenceParameters().getSimplifyGroupsVariants()) {
            variantMapping.setSelected("Yes");
        } else {
            variantMapping.setSelected("No");
        }
        if (webSearchParameters.getProteinInferenceParameters().getSimplifyGroupsConfidence()) {
            peptideConfidence.setSelected("Yes");
        } else {
            peptideConfidence.setSelected("No");
        }
        if (webSearchParameters.getProteinInferenceParameters().isModificationRefinement()) {
            accountModificationforProteinMapping.setSelected("Yes");
        } else {
            accountModificationforProteinMapping.setSelected("No");
        }
        peptideIgnoredConfidence.setSelectedValue(webSearchParameters.getProteinInferenceParameters().getConfidenceThreshold());

        super.setLabelValue(VaadinIcons.COG.getHtml() + " Protein Inference" + "<center>" + webSearchParameters.getProteinInferenceParameters().getShortDescription() + "</center>");

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
            super.setLabelValue(VaadinIcons.COG.getHtml() + " Protein Inference" + "<center>" + webSearchParameters.getProteinInferenceParameters().getShortDescription() + "</center>");
        }
        super.setPopupVisible(visible); //To change body of generated methods, choose Tools | Templates.
    }

    private void updateParameters() {
        webSearchParameters.getProteinInferenceParameters().setSimplifyGroups(simplyProteinGroups.getSelectedValue().equalsIgnoreCase("Yes"));
        webSearchParameters.getProteinInferenceParameters().setSimplifyGroupsEvidence(uniprotEvidenceLevel.getSelectedValue().equalsIgnoreCase("Yes"));
        webSearchParameters.getProteinInferenceParameters().setSimplifyGroupsConfidence(peptideConfidence.getSelectedValue().equalsIgnoreCase("Yes"));
        webSearchParameters.getProteinInferenceParameters().setConfidenceThreshold(Double.valueOf(peptideIgnoredConfidence.getSelectedValue()));
        webSearchParameters.getProteinInferenceParameters().setSimplifyGroupsEnzymaticity(enzymaticity.getSelectedValue().equalsIgnoreCase("Yes"));
        webSearchParameters.getProteinInferenceParameters().setSimplifyGroupsVariants(variantMapping.getSelectedValue().equalsIgnoreCase("Yes"));
      webSearchParameters.getProteinInferenceParameters().setModificationRefinement(accountModificationforProteinMapping.getSelectedValue().equalsIgnoreCase("Yes"));
//if
    }

}
