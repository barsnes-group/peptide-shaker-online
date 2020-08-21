package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * This class represents protein 3d structure coverage that placed over protein
 * coverage to show which part of the protein is covered by the 3d structure
 *
 * @author Yehia Farag
 */
public class Protein3DStructureCoverage extends AbsoluteLayout {

    private final Label leftLabel;
    private final Label rightLabel;
    private final AbsoluteLayout coverageContainer;
    private final VerticalLayout middleLine;

    /**
     * Initialise the protein 3d coverage component
     */
    public Protein3DStructureCoverage() {
        Protein3DStructureCoverage.this.setStyleName("protein3dstructurecoverage");
        Protein3DStructureCoverage.this.setWidth(100, Unit.PERCENTAGE);
        Protein3DStructureCoverage.this.setHeight(30, Unit.PIXELS);
        this.leftLabel = new Label("3D");
        leftLabel.setWidth(23, Unit.PIXELS);
        leftLabel.setHeight(100, Unit.PERCENTAGE);
        Protein3DStructureCoverage.this.addComponent(leftLabel);
        this.rightLabel = new Label("");
        rightLabel.setHeight(100, Unit.PERCENTAGE);
        rightLabel.setWidth(23, Unit.PIXELS);
        Protein3DStructureCoverage.this.addComponent(rightLabel, "right:0px;top:0px ");
        this.coverageContainer = new AbsoluteLayout();
        Protein3DStructureCoverage.this.addComponent(coverageContainer, "left:25px;right:25px;top:0px ");
        this.middleLine = new VerticalLayout();
        this.middleLine.setWidth(100, Unit.PERCENTAGE);
        this.middleLine.setHeight(2, Unit.PIXELS);
        this.middleLine.setStyleName("graymiddleline");
        this.coverageContainer.addComponent(middleLine, "left:0px;top:50%");

    }

    /**
     * Show end terminal label
     *
     * @param text end terminal label
     */
    public void setRightLabelValue(String text) {
        this.rightLabel.setValue(text);

    }

    /**
     * Reset the layout (un-select all)
     */
    public void reset() {
        this.coverageContainer.removeAllComponents();
        this.coverageContainer.addComponent(middleLine, "left:0px;top:50%");

    }

    /**
     * Add coverage component
     *
     * @param left      the x position
     * @param width     the width of the component
     * @param highlight is the component highlighted
     */
    public void addCoverageComponent(double left, Double width, boolean highlight) {
        VerticalLayout coverageComponent = new VerticalLayout();
        coverageComponent.setHeight(100, Unit.PERCENTAGE);
        width = (width * 100) / (100.0 - left);
        coverageComponent.setWidth(width.floatValue(), Unit.PERCENTAGE);
        coverageComponent.addStyleName("coveragecompoenent");
        if (highlight) {
            coverageComponent.addStyleName("heighlightcoverage");
        }
        coverageContainer.addComponent(coverageComponent, "left:" + left + "%;top:0px");

    }

}
