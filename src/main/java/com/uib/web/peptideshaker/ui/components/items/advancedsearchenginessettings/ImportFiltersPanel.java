package com.uib.web.peptideshaker.ui.components.items.advancedsearchenginessettings;

import com.compomics.util.experiment.identification.filtering.PeptideAssumptionFilter;
import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.ui.views.modal.PopupWindow;
import com.uib.web.peptideshaker.ui.components.items.HorizontalLabel2TextField;
import com.uib.web.peptideshaker.ui.components.items.HorizontalLabelTextFieldDropdownList;
import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author y-mok
 */
public class ImportFiltersPanel extends PopupWindow {

    private final HorizontalLabel2TextField peptideLength;
    private final HorizontalLabelTextFieldDropdownList precursorMzDeviation;
    private final HorizontalLabel2TextField missedCleavages;
    private final HorizontalLabel2TextField isotops;
    private final OptionGroup excludeUnKnownPTMs;
    private IdentificationParameters webSearchParameters;

    public ImportFiltersPanel() {
        super(VaadinIcons.COG.getHtml() + " Import Filters");
        Label title = new Label("Import Filters");
        AbsoluteLayout container = new AbsoluteLayout();
        container.setStyleName("popuppanelmaincontainer");
        container.setWidth(345, Unit.PIXELS);
        container.setHeight(234, Unit.PIXELS);

        VerticalLayout subContainer = new VerticalLayout();
        subContainer.setSizeFull();
        subContainer.setStyleName("importfiltersubcontainer");

        container.addComponent(title, "left:10px;top:10px");
        container.addComponent(subContainer, "left:10px;top:45px;right:10px;bottom:40px");
        peptideLength = new HorizontalLabel2TextField("Peptide Length", 0, 0, new IntegerRangeValidator("check the input range", (-1 * Integer.MAX_VALUE), Integer.MAX_VALUE));
        subContainer.addComponent(peptideLength);
        Set<String> values = new LinkedHashSet<>();
        values.add("ppm");
        values.add("Da");
        precursorMzDeviation = new HorizontalLabelTextFieldDropdownList("Precursor m/z Deviation", 10.0, values, new DoubleRangeValidator("check the input range", 0.0, Double.MAX_VALUE));
        subContainer.addComponent(precursorMzDeviation);

        missedCleavages = new HorizontalLabel2TextField("Missed Cleavages", "", "", new IntegerRangeValidator("check the input range", (-1 * Integer.MAX_VALUE), Integer.MAX_VALUE));
        subContainer.addComponent(missedCleavages);

        isotops = new HorizontalLabel2TextField("Isotops", "", "", new IntegerRangeValidator("check the input range", (-1 * Integer.MAX_VALUE), Integer.MAX_VALUE));
        subContainer.addComponent(isotops);

        excludeUnKnownPTMs = new OptionGroup();
        excludeUnKnownPTMs.setCaptionAsHtml(true);
        excludeUnKnownPTMs.setSizeUndefined();
        excludeUnKnownPTMs.setMultiSelect(true);
        excludeUnKnownPTMs.setStyleName("optiongroup");
        excludeUnKnownPTMs.addItem("Exclude UnKnown PTMs");
        subContainer.addComponent(excludeUnKnownPTMs);

        ImportFiltersPanel.this.setContent(container);
        ImportFiltersPanel.this.setClosable(true);

        Button okBtn = new Button("OK");
        okBtn.setWidth(76, Unit.PIXELS);
        okBtn.setHeight(20, Unit.PIXELS);
        okBtn.setStyleName(ValoTheme.BUTTON_TINY);
        okBtn.addClickListener((Button.ClickEvent event) -> {
            if (peptideLength.isValid() && precursorMzDeviation.isValid()) {
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
            ImportFiltersPanel.this.setPopupVisible(false);
        });

    }

    public void updateGUI(IdentificationParameters webSearchParameters) {
        this.webSearchParameters = webSearchParameters;
        PeptideAssumptionFilter filter = webSearchParameters.getPeptideAssumptionFilter();
        peptideLength.setFirstSelectedValue(filter.getMinPepLength());
        peptideLength.setSecondSelectedValue(filter.getMaxPepLength());
        precursorMzDeviation.setTextValue(filter.getMaxMzDeviation());
        if (filter.isIsPpm()) {
            precursorMzDeviation.setSelected("ppm");
        } else {
            precursorMzDeviation.setSelected("Da");
        }
        if (filter.getMinMissedCleavages() != null) {
            missedCleavages.setFirstSelectedValue(filter.getMinMissedCleavages());
        }
        if (filter.getMaxMissedCleavages() != null) {
            missedCleavages.setSecondSelectedValue(filter.getMaxMissedCleavages());
        }
        if (filter.getMinIsotopes() != null) {
            isotops.setFirstSelectedValue(filter.getMinIsotopes());
        }
        if (filter.getMaxIsotopes() != null) {
            isotops.setSecondSelectedValue(filter.getMaxIsotopes());
        }
        if (filter.removeUnknownModifications()) {
            excludeUnKnownPTMs.unselect("Exclude UnKnown PTMs");
        } else {
            excludeUnKnownPTMs.select("Exclude UnKnown PTMs");
        }
        super.setLabelValue(VaadinIcons.COG.getHtml() + " Import Filters" + "<center>" + webSearchParameters.getPeptideAssumptionFilter().getShortDescription() + "</center>");

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
            super.setLabelValue(VaadinIcons.COG.getHtml() + " Import Filters" + "<center>" + webSearchParameters.getPeptideAssumptionFilter().getShortDescription() + "</center>");
        }
        super.setPopupVisible(visible); //To change body of generated methods, choose Tools | Templates.
    }

    private void updateParameters() {
        PeptideAssumptionFilter filter = webSearchParameters.getPeptideAssumptionFilter();
        if (peptideLength.isValid()) {
            filter.setMinPepLength(Integer.valueOf(peptideLength.getFirstSelectedValue()));
            filter.setMaxPepLength(Integer.valueOf(peptideLength.getSecondSelectedValue()));
        }
        if (precursorMzDeviation.isValid()) {
            filter.setMaxMzDeviation(Double.valueOf(precursorMzDeviation.getFirstSelectedValue()));
            filter.setIsPpm(precursorMzDeviation.getSecondSelectedValue().equalsIgnoreCase("ppm"));
        }

        if (!missedCleavages.getFirstSelectedValue().equalsIgnoreCase("")) {
            filter.setMinMissedCleavages(Integer.valueOf(missedCleavages.getFirstSelectedValue()));

        }
        if (!missedCleavages.getSecondSelectedValue().equalsIgnoreCase("")) {
            filter.setMaxMissedCleavages(Integer.valueOf(missedCleavages.getSecondSelectedValue()));
        }
        if (!isotops.getFirstSelectedValue().equalsIgnoreCase("")) {
            filter.setMinIsotopes(Integer.valueOf(isotops.getFirstSelectedValue()));
        }
        if (!isotops.getSecondSelectedValue().equalsIgnoreCase("")) {
            filter.setMaxIsotopes(Integer.valueOf(isotops.getSecondSelectedValue()));
        }
        filter.setRemoveUnknownModifications(excludeUnKnownPTMs.getValue().toString().contains("Exclude UnKnown PTMs"));

    }

}
