package com.uib.web.peptideshaker.ui.views.subviews.proteinsview.components;

import com.uib.web.peptideshaker.ui.components.items.PeptideLayout;
import com.compomics.util.experiment.biology.modifications.ModificationFactory;
import com.vaadin.event.LayoutEvents;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents coloured modification on the Protein-Proteoforms layout
 *
 * @author Yehia Farag
 */
public abstract class ProteoformModificationLayout extends VerticalLayout implements LayoutEvents.LayoutClickListener {

    private final Set<PeptideLayout> correspondingPeptidesSet;
    private ProteoformLayout parentLayout;

    public ProteoformLayout getParentLayout() {
        return parentLayout;
    }

    public void setParentLayout(ProteoformLayout parentLayout) {
        this.parentLayout = parentLayout;
    }

    public ProteoformModificationLayout(String modificationName, int location) {
        ProteoformModificationLayout.this.setWidth(5, Unit.PIXELS);
        ProteoformModificationLayout.this.setHeight(15, Unit.PIXELS);
        ProteoformModificationLayout.this.setStyleName("proteoformmodstyle");
        ProteoformModificationLayout.this.setDescription(modificationName + " (" + location + ")");
        Color c = new Color(ModificationFactory.getDefaultColor(modificationName));
        Label modification = new Label("<div  style='background:rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ");width: 100%;height: 100%;'></div>", ContentMode.HTML);
        modification.setSizeFull();
        ProteoformModificationLayout.this.addComponent(modification);
        this.correspondingPeptidesSet = new HashSet<>();
        ProteoformModificationLayout.this.addLayoutClickListener(ProteoformModificationLayout.this);

    }

    public void addCorrespondingPeptide(PeptideLayout peptide) {
        correspondingPeptidesSet.add(peptide);

    }

    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        selected(this);

    }

    public abstract void selected(ProteoformModificationLayout proteoformModificationLayout);
    public PeptideLayout select() {
//        System.out.println("at selected proteioformpeptide to highlight ");
//        for (PeptideLayout peptide : correspondingPeptidesSet) {
////            peptide.addStyleName("heighlightcorrespondingpeptide");
//        }
//        this.addStyleName("heighlightcorrespondingpeptide");
        if (correspondingPeptidesSet.size() == 1) {
            return correspondingPeptidesSet.iterator().next();
        }
        return null;
    }

}
