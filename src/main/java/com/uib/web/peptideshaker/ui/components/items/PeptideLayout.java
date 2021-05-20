package com.uib.web.peptideshaker.ui.components.items;

import com.compomics.util.experiment.biology.modifications.ModificationFactory;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.uib.web.peptideshaker.model.PeptideObject;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yfa041
 */
public class PeptideLayout extends AbsoluteLayout implements Comparable<PeptideLayout> {

    private final int startIndex;
    private final float x;
    private final PeptideObject peptide;
    private final String validationStatuesStyle;
    private final String proteinEvidenceStyle;
    private final VerticalLayout psmNumberLayout;
    private final VerticalLayout intensityLayout;
    private final List<Float> postionsList;
    private final String modificationStyleName = "nodemodificationbackground";
    /**
     * The post translational modifications factory.
     */
    private final ModificationFactory PTM = ModificationFactory.getInstance();
    private boolean modifiedPeptide;
    private VerticalLayout modificationLayout;

    public PeptideLayout(PeptideObject peptide, float width, int startIndex, float x, String validationStatuesStyle, String proteinEvidenceStyle, boolean enzymatic, String PSMNumberColor) {

        PeptideLayout.this.setHeight(15, Unit.PIXELS);
        PeptideLayout.this.setWidth(width, Unit.PERCENTAGE);
        PeptideLayout.this.addStyleName("lightbluelayout");
        PeptideLayout.this.addStyleName("peptidelayout");
        PeptideLayout.this.addStyleName("transparent");

        if (enzymatic) {
            PeptideLayout.this.addStyleName("blackborder");
        } else {
            PeptideLayout.this.addStyleName("dottedborder");
        }

        this.startIndex = startIndex;
        this.x = x;
        this.peptide = peptide;
        this.validationStatuesStyle = validationStatuesStyle;
        this.proteinEvidenceStyle = proteinEvidenceStyle;

        modificationLayout = new VerticalLayout();
        modificationLayout.setSizeFull();
        PeptideLayout.this.addComponent(modificationLayout);
        modificationLayout.setData(peptide.getModifiedSequence());
        modificationLayout.setStyleName("basicpeptidemodification");
        String subTooltip = "";
        Map<String, String> modificationsTooltip = new HashMap<>();

        for (ModificationMatch mod : peptide.getVariableModifications()) {

            Color c = new Color(ModificationFactory.getDefaultColor(mod.getModification()));
            Label modification = new Label("<div  style='background:rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ");;width: 100%;height: 100%;'></div>", ContentMode.HTML);
            modification.setSizeFull();
            modification.setData(peptide.getModifiedSequence());
            modificationLayout.addComponent(modification);
            modifiedPeptide = true;
            int i = Math.max(mod.getSite() - 1, 0);
            modificationsTooltip.put(peptide.getSequence().charAt(i) + "<" + PTM.getModification(mod.getModification()).getShortName() + ">", "<font style='background-color:rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")'>" + peptide.getSequence().charAt(i) + "</font>");
            if (!subTooltip.contains("</br><span style='width:20px;height:10px;background-color:rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")'>" + peptide.getSequence().charAt(i) + "</span> - " + mod.getModification())) {
                subTooltip += "</br><span style='width:20px;height:10px;background-color:rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")'>" + peptide.getSequence().charAt(i) + "</span> - " + mod.getModification();
            }
        }

        String tooltip = peptide.getModifiedSequence();
        for (String key : modificationsTooltip.keySet()) {
            tooltip = tooltip.replace(key, modificationsTooltip.get(key));
        }
        tooltip += subTooltip;

        PeptideLayout.this.setDescription(tooltip);//peptide.getModifiedSequence().replace("<", "&lt;").replace(">", "&gt;")
        if (modificationLayout.getComponentCount() > 1) {
            modificationLayout.removeAllComponents();
            Label modification = new Label("<div style='background:orange; width:100%;height:100%;'></div>", ContentMode.HTML);
            modification.setSizeFull();
            modification.setData(peptide.getModifiedSequence());
            modificationLayout.addComponent(modification);
            modification.setData(peptide.getModifiedSequence());
        }
        this.modificationLayout.setVisible(false);

        psmNumberLayout = new VerticalLayout();
        psmNumberLayout.setSizeFull();
        PeptideLayout.this.addComponent(psmNumberLayout);
        psmNumberLayout.setData(peptide.getModifiedSequence());
//        psmNumberLayout.setStyleName("basicpeptidemodification");

        tooltip += "</br>#PSMs: " + peptide.getPSMsNumber() + "";

        Label psmsColorLabel = new Label("<div style='background:" + PSMNumberColor + "; width:100%;height:100%;'></div>", ContentMode.HTML);
        psmsColorLabel.setSizeFull();
        psmsColorLabel.setData(peptide.getModifiedSequence());
        psmNumberLayout.addComponent(psmsColorLabel);
        psmNumberLayout.setVisible(true);

        intensityLayout = new VerticalLayout();
        intensityLayout.setSizeFull();
        PeptideLayout.this.addComponent(intensityLayout);
        intensityLayout.setData(peptide.getModifiedSequence());
//        psmNumberLayout.setStyleName("basicpeptidemodification");
        if (peptide.getIntensity() > 0) {
            tooltip += "</br>Intensity: " + peptide.getIntensity() + "";
        }

        Label intensityColorLabel = new Label("<div style='background:" + peptide.getIntensityColor() + "; width:100%;height:100%;'></div>", ContentMode.HTML);
        intensityColorLabel.setSizeFull();
        intensityColorLabel.setData(peptide.getModifiedSequence());
        intensityLayout.addComponent(intensityColorLabel);
        intensityLayout.setVisible(false);

        peptide.setTooltip(tooltip);
        PeptideLayout.this.setDescription(tooltip);
        this.postionsList = new ArrayList<>();

    }

    public PeptideObject getPeptide() {
        return peptide;
    }

    public boolean isModifiedPeptide() {
        return modifiedPeptide;
    }

    public Object getPeptideId() {
        return peptide.getModifiedSequence();
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return startIndex + peptide.getSequence().length();
    }

    public float getX() {
        return x;
    }

    public void addLocation(float location) {
        postionsList.add(location);
    }

    public void setSelected(boolean selected) {
        if (selected) {
            PeptideLayout.this.addStyleName("selectedpeptide");
        } else {
            PeptideLayout.this.removeStyleName("selectedpeptide");
        }
    }
    public void setSingleSelected(boolean selected) {
        if (selected) {
            PeptideLayout.this.addStyleName("singleselectedpeptide");
        } else {
            PeptideLayout.this.removeStyleName("singleselectedpeptide");
        }
    }

    @Override
    public int compareTo(PeptideLayout o) {
        if (this.getWidth() > o.getWidth()) {
            return 1;
        } else {
            return -1;
        }
    }

    public void updateStylingMode(String statues) {
        resetStyle();
        if (statues.equalsIgnoreCase("Validation")) {
            this.addStyleName(validationStatuesStyle);
        } else if (statues.equalsIgnoreCase("Protein Evidence")) {
            this.addStyleName(proteinEvidenceStyle);
        } else if (statues.equalsIgnoreCase("Modifications")) {
            this.modificationLayout.setVisible(true);
            this.addStyleName(modificationStyleName);
        } else if (statues.equalsIgnoreCase("PSMNumber")) {
            this.psmNumberLayout.setVisible(true);
            this.addStyleName(modificationStyleName);
        } else if (statues.equalsIgnoreCase("Intensity")) {
            this.intensityLayout.setVisible(true);
            this.addStyleName(modificationStyleName);
        }

    }

    private void resetStyle() {
        if (this.getStyleName().contains(proteinEvidenceStyle)) {
            this.removeStyleName(proteinEvidenceStyle);
        }
        if (this.getStyleName().contains(validationStatuesStyle)) {
            this.removeStyleName(validationStatuesStyle);
        }
        if (this.getStyleName().contains(modificationStyleName)) {
            this.removeStyleName(modificationStyleName);
        }
        this.modificationLayout.setVisible(false);
        this.psmNumberLayout.setVisible(false);
        this.intensityLayout.setVisible(false);

    }

    public List<Float> getPostionsList() {
        return postionsList;
    }

}
