package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.coverage;

import com.vaadin.ui.AbsoluteLayout;

/**
 * this class represents protein coverage layout
 *
 * @author YEhia Farag
 */
public class ProteinCoverageLayout extends AbsoluteLayout {

    private final String validationStatuesStyle;
    private final String proteinEvidenceStyle;

    public ProteinCoverageLayout(String validationStatuesStyle, String proteinEvidenceStyle) {

        ProteinCoverageLayout.this.setWidth(100, Unit.PERCENTAGE);
        ProteinCoverageLayout.this.setHeight(15, Unit.PIXELS);
        ProteinCoverageLayout.this.addStyleName("proteincoverage");
        this.validationStatuesStyle = validationStatuesStyle;
        this.proteinEvidenceStyle = proteinEvidenceStyle;

    }

    public void updateStylingMode(String statues) {
        resetStyle();
        if (statues.equalsIgnoreCase("Validation")) {
            this.addStyleName(validationStatuesStyle);
        } else if (statues.equalsIgnoreCase("Protein Evidence")) {
            this.addStyleName(proteinEvidenceStyle);
        }else{
        }

    }

    private void resetStyle() {
        if (this.getStyleName().contains(proteinEvidenceStyle)) {
            this.removeStyleName(proteinEvidenceStyle);
        }

        if (this.getStyleName().contains(validationStatuesStyle)) {
            this.removeStyleName(validationStatuesStyle);
        }

    }

}
