package com.uib.web.peptideshaker.ui.views.subviews.proteinsview.components;

import com.uib.web.peptideshaker.ui.components.items.HighlightPeptide;
import com.vaadin.event.LayoutEvents;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.*;
import graphmatcher.ReactomWindow;

import java.awt.*;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class represents the proteins proteoform layout that link the proteoform
 * to the protein coverage and to the modified peptides
 *
 * @author Yehia Mokhtar Farag
 */
public abstract class ProteoformLayout extends AbsoluteLayout implements LayoutEvents.LayoutClickListener {

    private final String proteoformKey;
    private final Set<ProteoformModificationLayout> includedModifications;
    private final ReactomWindow localReactomWindow;

    public ProteoformLayout(int index, int topLocation, Color finalBackgroundColor, String proteoformKey, boolean availableOnReactome) {
        ProteoformLayout.this.setWidth(100, Unit.PERCENTAGE);
        ProteoformLayout.this.setHeight(15, Unit.PIXELS);
        this.localReactomWindow = new ReactomWindow();
        this.includedModifications = new LinkedHashSet<>();
        this.proteoformKey = proteoformKey;
        String fontColor = "white";
        if (finalBackgroundColor.toString().contains("r=255,g=200,b=0")) {
            fontColor = "black";
        }
        ProteoformLayout.this.setStyleName("proteoformlayout");
        Label label = new Label("<font style='background:rgb(" + finalBackgroundColor.getRed() + "," + finalBackgroundColor.getGreen() + "," + finalBackgroundColor.getBlue() + ");color:" + fontColor + ";'>P " + index + "</font>", ContentMode.HTML);
        label.setWidth(25, Unit.PIXELS);
        ProteoformLayout.this.addComponent(label, "left:0px;top:0px;");

        VerticalLayout centeredLine = new VerticalLayout();
        centeredLine.setWidth(100, Unit.PERCENTAGE);
        centeredLine.setHeight(2, Unit.PIXELS);
        centeredLine.setStyleName("graylayout");
        ProteoformLayout.this.addComponent(centeredLine, "left:0px;top:6.5px;right:0px");

        ProteoformLayout.this.addLayoutClickListener(ProteoformLayout.this);
//        if (availableOnReactome) {
        Image reactomIcon = new Image();
        reactomIcon.setSource(new ThemeResource("img/reactom_gray.png"));
        reactomIcon.setStyleName("reactomicon");
        ProteoformLayout.this.addComponent(reactomIcon, "top:0px;right:0px");
        reactomIcon.setDescription("Pathway");
        reactomIcon.setEnabled(availableOnReactome);

        Link reactomLink = new Link("", new ExternalResource("https://reactome.org/content/query?q=" + proteoformKey.substring(0, proteoformKey.length() - 1) + "&types=Protein&cluster=true"));
        reactomLink.setCaptionAsHtml(true);
        reactomLink.setIcon(new ThemeResource("img/reactom.png"));
        reactomLink.setStyleName("reactomicon");
        reactomLink.setSizeFull();
        reactomLink.setTargetName("_blank");
        ProteoformLayout.this.addComponent(reactomLink, "top:0px;right:0px");
        reactomLink.setDescription("View in Reactom");
        reactomLink.setEnabled(availableOnReactome);
//        }
    }

    public void updateHighlightedComponents(AbsoluteLayout containerToCopy) {
        Iterator<Component> itr = containerToCopy.iterator();
        while (itr.hasNext()) {
            Component comp = itr.next();
            if (comp instanceof HighlightPeptide) {
                HighlightPeptide c = (HighlightPeptide) comp;
                ProteoformLayout.this.addComponent(c.cloneComponent(), containerToCopy.getPosition(c).getCSSString());
            }
        }

    }

    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        Component c = event.getClickedComponent();
        if (c != null && (c instanceof Image)) {
            if (c.isEnabled()) {
                localReactomWindow.visulaizeProtein(proteoformKey);
            }
        } else {
            selectProteoform(this);
        }
    }

    public void addModificationLayout(ProteoformModificationLayout mod) {
        this.includedModifications.add(mod);
    }

    public Set<ProteoformModificationLayout> getIncludedModifications() {
        return includedModifications;
    }

    public abstract void selectProteoform(ProteoformLayout proteoform);

}
