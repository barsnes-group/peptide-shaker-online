package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.coverage;

import com.vaadin.ui.VerticalLayout;

/**
 * This class represents the highlight peptides on the protein coverage area
 *
 * @author Yehia Farag
 */
public class HighlightPeptide extends VerticalLayout {

    public HighlightPeptide(float w, String description) {
        HighlightPeptide.this.setHeight(100, Unit.PERCENTAGE);
        HighlightPeptide.this.setStyleName("lightgraylayout");
        HighlightPeptide.this.addStyleName("peptidelayout");
        HighlightPeptide.this.setDescription(description);
        HighlightPeptide.this.setWidth(w, Unit.PERCENTAGE);
    }

    public VerticalLayout cloneComponent() {
        VerticalLayout clonedComponent = new VerticalLayout();
        clonedComponent.setHeight(100, Unit.PERCENTAGE);
        clonedComponent.setStyleName("lightgraylayout");
        clonedComponent.addStyleName("peptidelayout");
        clonedComponent.setDescription(HighlightPeptide.this.getDescription());
        clonedComponent.setWidth(HighlightPeptide.this.getWidth(), Unit.PERCENTAGE);
        return clonedComponent;

    }

}
