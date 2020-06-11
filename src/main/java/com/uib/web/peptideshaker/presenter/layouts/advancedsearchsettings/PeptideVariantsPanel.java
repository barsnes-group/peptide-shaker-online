package com.uib.web.peptideshaker.presenter.layouts.advancedsearchsettings;

import com.compomics.util.experiment.biology.variants.AaSubstitutionMatrix;
import com.compomics.util.parameters.identification.IdentificationParameters;
import com.compomics.util.parameters.identification.advanced.PeptideVariantsParameters;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabelDropDounList;
import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabelTextField;
import com.vaadin.data.Property;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author y-mok
 */
public class PeptideVariantsPanel extends PopupWindow {

    private final HorizontalLabelDropDounList variantType;
    private final HorizontalLabelTextField total;
    private final HorizontalLabelTextField aminoAcidDeletion;
    private final HorizontalLabelTextField aminoAcidInsertion;
    private final HorizontalLabelTextField aminoAcidSwap;
    private final HorizontalLabelTextField aminoAcidSubstitutions;
    private final ComboBox allowedAminoAcidSubstitutions;
    private final Image aminoAcidSubstitutionsMatrix;
    private AaSubstitutionMatrix aaSubstitutionMatrix;

    private IdentificationParameters webSearchParameters;

    public PeptideVariantsPanel() {
        super(VaadinIcons.COG.getHtml() + " Peptide Variants");
        Label title = new Label("Number of Variants Allowed per Peptides");
        AbsoluteLayout container = new AbsoluteLayout();
        container.setStyleName("popuppanelmaincontainer");
        container.setWidth(600, Unit.PIXELS);
        container.setHeight(600, Unit.PIXELS);

        VerticalLayout subContainer = new VerticalLayout();
        subContainer.setSizeFull();
        subContainer.setStyleName("importfiltersubcontainer");

        Set<String> values = new LinkedHashSet<>();
        values.add(PeptideVariantsParameters.VariantType.NO_VARIANT.name());
        values.add(PeptideVariantsParameters.VariantType.GENERIC.name());
        values.add(PeptideVariantsParameters.VariantType.SPECIFIC.name());
        values.add(PeptideVariantsParameters.VariantType.FIXED.name());

        total = new HorizontalLabelTextField("Total", 0, new IntegerRangeValidator("Only integer values allowd", 0, Integer.MAX_VALUE));

        variantType = new HorizontalLabelDropDounList("Variant Type");
        variantType.updateData(values);
        variantType.setItemCaption(PeptideVariantsParameters.VariantType.NO_VARIANT.name(), "None");

        aminoAcidDeletion = new HorizontalLabelTextField("&nbsp&nbsp&nbsp&nbsp&nbspAmino Acid Deletions ", 0, new IntegerRangeValidator("Only integer values allowd", 0, Integer.MAX_VALUE));
        aminoAcidInsertion = new HorizontalLabelTextField("&nbsp&nbsp&nbsp&nbsp&nbspAmino Acid Insertions", 0, new IntegerRangeValidator("Only integer values allowd", 0, Integer.MAX_VALUE));
        aminoAcidSwap = new HorizontalLabelTextField("&nbsp&nbsp&nbsp&nbsp&nbspAmino Acid Swap", 0, new IntegerRangeValidator("Only integer values allowd", 0, Integer.MAX_VALUE));
        aminoAcidSubstitutions = new HorizontalLabelTextField("&nbsp&nbsp&nbsp&nbsp&nbspAmino Acid Substitutions", 0, new IntegerRangeValidator("Only integer values allowd", 0, Integer.MAX_VALUE));

        container.addComponent(title, "left:10px;top:10px");
        container.addComponent(subContainer, "left:10px;top:40px;right:10px;bottom:400px");
        subContainer.addComponent(total);
        subContainer.addComponent(variantType);
        subContainer.addComponent(aminoAcidDeletion);
        subContainer.addComponent(aminoAcidInsertion);

        subContainer.addComponent(aminoAcidSwap);
        subContainer.addComponent(aminoAcidSubstitutions);

        variantType.addValueChangeListener((Property.ValueChangeEvent event) -> {
            aminoAcidDeletion.setEnabled(variantType.getSelectedValue().equalsIgnoreCase(PeptideVariantsParameters.VariantType.NO_VARIANT.name()));
            aminoAcidInsertion.setEnabled(variantType.getSelectedValue().equalsIgnoreCase(PeptideVariantsParameters.VariantType.NO_VARIANT.name()));
            aminoAcidSwap.setEnabled(variantType.getSelectedValue().equalsIgnoreCase(PeptideVariantsParameters.VariantType.NO_VARIANT.name()));
            aminoAcidSubstitutions.setEnabled(variantType.getSelectedValue().equalsIgnoreCase(PeptideVariantsParameters.VariantType.NO_VARIANT.name()));

        });

        Label title2 = new Label("Allowed Amino Acid Substitutions");

        AbsoluteLayout subContainer2 = new AbsoluteLayout();
        subContainer2.setSizeFull();
        subContainer2.setStyleName("importfiltersubcontainer");
        container.addComponent(title2, "left:10px;top:200px");
        container.addComponent(subContainer2, "left:10px;top:230px;right:10px;bottom:40px");

        allowedAminoAcidSubstitutions = new ComboBox();
        allowedAminoAcidSubstitutions.setTextInputAllowed(false);
        allowedAminoAcidSubstitutions.setWidth(100, Unit.PERCENTAGE);
        allowedAminoAcidSubstitutions.setHeight(20, Unit.PIXELS);
        allowedAminoAcidSubstitutions.setStyleName(ValoTheme.COMBOBOX_SMALL);
        allowedAminoAcidSubstitutions.addStyleName(ValoTheme.COMBOBOX_TINY);
        allowedAminoAcidSubstitutions.addStyleName(ValoTheme.COMBOBOX_ALIGN_CENTER);
        allowedAminoAcidSubstitutions.addStyleName("nolabelfullcombobox");
        allowedAminoAcidSubstitutions.setNullSelectionAllowed(false);
        allowedAminoAcidSubstitutions.setImmediate(true);

        allowedAminoAcidSubstitutions.addItem("No Substitution");
        allowedAminoAcidSubstitutions.addItem("Single Base Substitution");
        allowedAminoAcidSubstitutions.addItem("Single Base Transition");
        allowedAminoAcidSubstitutions.addItem("Single Base Transversion");
        allowedAminoAcidSubstitutions.addItem("Synonymous Variant");
        allowedAminoAcidSubstitutions.addItem("All");

        subContainer2.addComponent(allowedAminoAcidSubstitutions, "left:10px;top:10px;");

        this.aminoAcidSubstitutionsMatrix = new Image();
        aminoAcidSubstitutionsMatrix.setSizeFull();
        aminoAcidSubstitutionsMatrix.setStyleName("lightgrayborder");
        subContainer2.addComponent(aminoAcidSubstitutionsMatrix, "left:0px;top:40px;bottom:0px;");

        PeptideVariantsPanel.this.setContent(container);
        PeptideVariantsPanel.this.setClosable(true);

        Button okBtn = new Button("OK");
        okBtn.setWidth(76, Unit.PIXELS);
        okBtn.setHeight(20, Unit.PIXELS);
        okBtn.setStyleName(ValoTheme.BUTTON_TINY);
        okBtn.addClickListener((Button.ClickEvent event) -> {
            setPopupVisible(false);

        });
        container.addComponent(okBtn, "bottom:10px;right:10px");
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyleName(ValoTheme.BUTTON_TINY);
        cancelBtn.setWidth(76, Unit.PIXELS);
        cancelBtn.setHeight(20, Unit.PIXELS);
        container.addComponent(cancelBtn, "bottom:10px;right:96px");
        cancelBtn.addClickListener((Button.ClickEvent event) -> {
            PeptideVariantsPanel.this.setPopupVisible(false);
        });
        allowedAminoAcidSubstitutions.addValueChangeListener((Property.ValueChangeEvent event) -> {
            String selection = allowedAminoAcidSubstitutions.getValue().toString();
            switch (selection) {
                case "No Substitution":
                    aaSubstitutionMatrix = AaSubstitutionMatrix.noSubstitution;
                    aminoAcidSubstitutionsMatrix.setSource(new ThemeResource("img/pv1.png"));
                    break;
                case "Single Base Substitution":
                    aminoAcidSubstitutionsMatrix.setSource(new ThemeResource("img/pv2.png"));
                    aaSubstitutionMatrix = AaSubstitutionMatrix.singleBaseSubstitution;
                    break;
                case "Single Base Transition":
                    aminoAcidSubstitutionsMatrix.setSource(new ThemeResource("img/pv3.png"));
                    aaSubstitutionMatrix = AaSubstitutionMatrix.transitionsSingleBaseSubstitution;
                    break;
                case "Single Base Transversion":
                    aminoAcidSubstitutionsMatrix.setSource(new ThemeResource("img/pv4.png"));
                    aaSubstitutionMatrix = AaSubstitutionMatrix.transversalSingleBaseSubstitution;
                    break;
                case "Synonymous Variant":
                    aminoAcidSubstitutionsMatrix.setSource(new ThemeResource("img/pv5.png"));
                    aaSubstitutionMatrix = AaSubstitutionMatrix.synonymousVariant;
                    break;
                case "All":
                    aminoAcidSubstitutionsMatrix.setSource(new ThemeResource("img/pv6.png"));
                    aaSubstitutionMatrix = AaSubstitutionMatrix.allSubstitutions;
                    break;
            }
        });
    }

    public void updateGUI(IdentificationParameters webSearchParameters) {
        this.webSearchParameters = webSearchParameters;
        total.setSelectedValue(webSearchParameters.getPeptideVariantsParameters().getnVariants());
        variantType.setSelected(webSearchParameters.getPeptideVariantsParameters().getVariantType().name());
        aminoAcidDeletion.setSelectedValue(webSearchParameters.getPeptideVariantsParameters().getnVariants());
        aminoAcidInsertion.setSelectedValue(webSearchParameters.getPeptideVariantsParameters().getnVariants());
        aminoAcidSwap.setSelectedValue(webSearchParameters.getPeptideVariantsParameters().getnVariants());
        aminoAcidSubstitutions.setSelectedValue(webSearchParameters.getPeptideVariantsParameters().getnVariants());
        allowedAminoAcidSubstitutions.select(webSearchParameters.getPeptideVariantsParameters().getAaSubstitutionMatrix().getName());
        super.setLabelValue(VaadinIcons.COG.getHtml() + " Peptide Variants" + "<center>" + webSearchParameters.getPeptideVariantsParameters().getShortDescription() + "</center>");

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
            super.setLabelValue(VaadinIcons.COG.getHtml() + " Peptide Variants" + "<center>" + webSearchParameters.getPeptideVariantsParameters().getShortDescription() + "</center>");
        }
        super.setPopupVisible(visible); //To change body of generated methods, choose Tools | Templates.
    }

    private void updateParameters() {
        webSearchParameters.getPeptideVariantsParameters().setVatiantType(PeptideVariantsParameters.VariantType.valueOf(variantType.getSelectedValue()));
        webSearchParameters.getPeptideVariantsParameters().setnVariants(Integer.parseInt(total.getSelectedValue()));
        webSearchParameters.getPeptideVariantsParameters().setnAaDeletions(Integer.parseInt(aminoAcidDeletion.getSelectedValue()));
        webSearchParameters.getPeptideVariantsParameters().setnAaInsertions(Integer.parseInt(aminoAcidInsertion.getSelectedValue()));
        webSearchParameters.getPeptideVariantsParameters().setnAaSwap(Integer.parseInt(aminoAcidSwap.getSelectedValue()));
        webSearchParameters.getPeptideVariantsParameters().setnAaSubstitutions(Integer.parseInt(aminoAcidSubstitutions.getSelectedValue()));
        webSearchParameters.getPeptideVariantsParameters().setAaSubstitutionMatrix(aaSubstitutionMatrix);

    }

}
