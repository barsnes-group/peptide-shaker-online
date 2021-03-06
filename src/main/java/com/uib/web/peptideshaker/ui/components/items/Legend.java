package com.uib.web.peptideshaker.ui.components.items;

import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import graphmatcher.NetworkGraphNode;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class represents legend layout for graph and protein coverage
 *
 * @author Yehia Mokhtar Farag
 */
public abstract class Legend extends VerticalLayout {

    private final Map<String, VerticalLayout> layoutMap;
    private String currentModifications = "";

    public Legend(boolean proteoform) {
        Legend.this.setSpacing(true);
        Legend.this.setStyleName("viewframecontent");
        Legend.this.addStyleName("whitelayout");
        Legend.this.setWidth(500, Unit.PIXELS);
        this.layoutMap = new LinkedHashMap<>();

        Button closeBtn = new Button("Close");
        closeBtn.setIcon(VaadinIcons.CLOSE_SMALL, "Close window");
        closeBtn.setStyleName(ValoTheme.BUTTON_SMALL);
        closeBtn.addStyleName(ValoTheme.BUTTON_TINY);
        closeBtn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        closeBtn.addStyleName("centerbackground");
        closeBtn.setHeight(25, Unit.PIXELS);
        closeBtn.setWidth(25, Unit.PIXELS);

        closeBtn.addClickListener((Button.ClickEvent event) -> {
            close();
        });
        Legend.this.addComponent(closeBtn);
        Legend.this.setExpandRatio(closeBtn, 0.01f);

//        Legend.this.addComponent(title);
        HorizontalLayout container = new HorizontalLayout();
        if (proteoform) {
            container.setCaption("<b>Proteoforms</b>");
        } else {
            container.setCaption("<b>Proteins & Peptides</b>");
        }
        container.setCaptionAsHtml(true);
        container.setSpacing(true);
        Legend.this.addComponent(container);
        Legend.this.setExpandRatio(container, 0.5f);
        container.setWidth(100, Unit.PERCENTAGE);

        VerticalLayout mTContainer = new VerticalLayout();
        mTContainer.setSizeFull();
        container.addComponent(mTContainer);
        container.setExpandRatio(mTContainer, 30);

        if (!proteoform) {
            container.setHeight(75, Unit.PIXELS);
            Node protreinMT = generateNode("proteinnode");
            mTContainer.addComponent(generateLabel(protreinMT, "Protein"));
            Node peptideMT = generateNode("peptidenode");
            mTContainer.addComponent(generateLabel(peptideMT, "Peptide"));

            VerticalLayout enzymaticContainer = new VerticalLayout();
            enzymaticContainer.setSizeFull();
            container.addComponent(enzymaticContainer);
            container.setExpandRatio(enzymaticContainer, 70);
            Node proteinNodeEnzimatic = generateNode("proteinnode");
            Node peptideNodeEnzimatic = generateNode("peptidenode");
            HorizontalLayout enzymaticEdge = generateEdgeLabel(proteinNodeEnzimatic, peptideNodeEnzimatic, "Enzymatic");
            enzymaticContainer.addComponent(enzymaticEdge);

            Node proteinNodeNonEnzimatic = generateNode("proteinnode");
            Node peptideNodeNonEnzimatic = generateNode("peptidenode");
            enzymaticContainer.addComponent(generateEdgeLabel(proteinNodeNonEnzimatic, peptideNodeNonEnzimatic, "Non Enzymatic"));

            HorizontalLayout lowerContainer = new HorizontalLayout();
            lowerContainer.setWidth(100, Unit.PERCENTAGE);

            Legend.this.addComponent(lowerContainer);
            Legend.this.setExpandRatio(lowerContainer, 0.5f);
            VerticalLayout proteinEvidenceContainer = new VerticalLayout();
            proteinEvidenceContainer.setHeight(110, Unit.PIXELS);
            proteinEvidenceContainer.setCaption("<b>Protein Evidence</b>");
            proteinEvidenceContainer.setCaptionAsHtml(true);
            proteinEvidenceContainer.setVisible(false);
            layoutMap.put("Protein Evidence", proteinEvidenceContainer);
            lowerContainer.addComponent(proteinEvidenceContainer);
            Node protein = generateNode("proteinnode");
            protein.addStyleName("greenbackground");
            Node predect = generateNode("proteinnode");
            predect.addStyleName("purplebackground");
            proteinEvidenceContainer.addComponent(generateRow(generateLabel(protein, "Protein"), generateLabel(predect, "Predicted")));
            Node transcript = generateNode("proteinnode");
            transcript.addStyleName("orangebackground");
            Node uncertain = generateNode("proteinnode");
            uncertain.addStyleName("redbackground");
            proteinEvidenceContainer.addComponent(generateRow(generateLabel(transcript, "Transcript"), generateLabel(uncertain, "Uncertain")));
            Node homology = generateNode("proteinnode");
            homology.addStyleName("seabluebackground");
            Node notAvailable = generateNode("proteinnode");
            notAvailable.addStyleName("graybackground");
            proteinEvidenceContainer.addComponent(generateRow(generateLabel(homology, "Homology"), generateLabel(notAvailable, CONSTANT.NO_INFORMATION)));

            VerticalLayout proteinValidationContainer = new VerticalLayout();
            proteinValidationContainer.setHeight(75, Unit.PIXELS);
            proteinValidationContainer.setCaption("<b>Validation</b>");
            proteinValidationContainer.setCaptionAsHtml(true);
            proteinValidationContainer.setVisible(false);
            layoutMap.put("Validation", proteinValidationContainer);
            lowerContainer.addComponent(proteinValidationContainer);
            Node confedent = generateNode("proteinnode");
            confedent.addStyleName("greenbackground");
            Node notValidated = generateNode("proteinnode");
            notValidated.addStyleName("redbackground");
            proteinValidationContainer.addComponent(generateRow(generateLabel(confedent, CONSTANT.VALIDATION_CONFIDENT), generateLabel(notValidated, CONSTANT.VALIDATION_NOT_VALID)));
            Node doubtful = generateNode("proteinnode");
            doubtful.addStyleName("orangebackground");
            Node notAvailable2 = generateNode("proteinnode");
            notAvailable2.addStyleName("graybackground");
            proteinValidationContainer.addComponent(generateRow(generateLabel(doubtful, CONSTANT.VALIDATION_DOUBTFUL), generateLabel(notAvailable2, CONSTANT.NO_INFORMATION)));

            VerticalLayout psmNumberContainer = new VerticalLayout();
            psmNumberContainer.setHeightUndefined();
            psmNumberContainer.setVisible(true);
            psmNumberContainer.addStyleName("psmlegendrangeconatainer");
            psmNumberContainer.setCaption("<b>#PSMs</b>");
            psmNumberContainer.setCaptionAsHtml(true);
            layoutMap.put("PSMNumber", psmNumberContainer);
            lowerContainer.addComponent(psmNumberContainer);

            VerticalLayout proteinModificationContainer = new VerticalLayout();
            proteinModificationContainer.setHeightUndefined();
            proteinModificationContainer.setSpacing(true);
            proteinModificationContainer.setVisible(false);
            proteinModificationContainer.addStyleName("modificationlegendconatiner");
            proteinModificationContainer.setCaption("<b>Modifications</b>");
            proteinModificationContainer.setCaptionAsHtml(true);
            layoutMap.put("Modifications", proteinModificationContainer);
            lowerContainer.addComponent(proteinModificationContainer);
            Legend.this.updateModificationLayout("No Modifications");
            Legend.this.updateModificationLayout("Two or More Modifications");

            VerticalLayout proteinIntensityContainer = new VerticalLayout();
            proteinIntensityContainer.setHeightUndefined();
            proteinIntensityContainer.setVisible(false);
            proteinIntensityContainer.addStyleName("psmlegendrangeconatainer");
            proteinIntensityContainer.addStyleName("intensitylegendrangeconatainer");
            proteinIntensityContainer.setCaption("<b>Intensity</b>");
            proteinIntensityContainer.setCaptionAsHtml(true);
            layoutMap.put("Intensity", proteinIntensityContainer);
            lowerContainer.addComponent(proteinIntensityContainer);

//            VerticalLayout proteoformModificationsContainer = new VerticalLayout();
//            proteoformModificationsContainer.setHeightUndefined();
//            proteoformModificationsContainer.setSpacing(true);
//            proteoformModificationsContainer.setVisible(false);
//            proteoformModificationsContainer.addStyleName("modificationlegendconatiner");
//            proteoformModificationsContainer.setCaption("<b>Proteoform Type</b>");
//            proteoformModificationsContainer.setCaptionAsHtml(true);
//            layoutMap.put("proteoformType", proteoformModificationsContainer);
//            lowerContainer.addComponent(proteoformModificationsContainer);
//            Legend.this.updateModificationLayout("No Modifications");
//            Legend.this.updateModificationLayout("Two or More Modifications");
        } else {
            container.setHeight(120, Unit.PIXELS);
            Legend.this.addStyleName("proteoformslegend");
            mTContainer.setResponsive(false);
            NetworkGraphNode node = new NetworkGraphNode("", true, true) ;
            node.addStyleName("legend");
            node.addStyleName("proteoformparentnode");
            mTContainer.addComponent(generateLabel(node, "Protein"));

            NetworkGraphNode proteoformnode = new NetworkGraphNode("", true, false) ;
            proteoformnode.addStyleName("legend");
            mTContainer.addComponent(generateLabel(proteoformnode, " Internal Proteoform"));

            NetworkGraphNode externalproteoformnode = new NetworkGraphNode("", false, false) ;
            externalproteoformnode.addStyleName("legend");
            mTContainer.addComponent(generateLabel(externalproteoformnode, " External Proteoform"));

            VerticalLayout connectionEdgesContainer = new VerticalLayout();
            connectionEdgesContainer.setSizeFull();
            connectionEdgesContainer.addStyleName("proteoformedges");
            container.addComponent(connectionEdgesContainer);
            container.setExpandRatio(connectionEdgesContainer, 70);
            NetworkGraphNode proteinEdge = new NetworkGraphNode("", true, true) ;
            proteinEdge.addStyleName("legend");
            proteinEdge.addStyleName("proteoformparentnode");

            NetworkGraphNode proteoformEdge = new NetworkGraphNode("", true, false) ;
            proteoformEdge.addStyleName("legend");
            proteoformEdge.addStyleName("edgeside");
            HorizontalLayout internalEdge = generateEdgeLabel(proteinEdge, proteoformEdge, "Internal  Interaction");
            connectionEdgesContainer.addComponent(internalEdge);

            NetworkGraphNode proteoformEdge2 = new NetworkGraphNode("", true, false) ;
            proteoformEdge2.addStyleName("legend");

            NetworkGraphNode proteoformExternalEdge2 = new NetworkGraphNode("", false, false) ;
            proteoformExternalEdge2.addStyleName("legend");
            proteoformExternalEdge2.addStyleName("edgeside");
            HorizontalLayout externalEdge = generateEdgeLabel(proteoformEdge2, proteoformExternalEdge2, "External Interaction");
            connectionEdgesContainer.addComponent(externalEdge);
            
            HorizontalLayout lowerContainer = new HorizontalLayout();
            lowerContainer.setWidth(100, Unit.PERCENTAGE);
            Legend.this.addComponent(lowerContainer);
            Legend.this.setExpandRatio(lowerContainer, 0.5f);
            VerticalLayout proteinModificationContainer = new VerticalLayout();
            proteinModificationContainer.setHeightUndefined();
            proteinModificationContainer.setSpacing(true);
            proteinModificationContainer.addStyleName("modificationlegendconatiner");
            proteinModificationContainer.addStyleName("proteoformconatiner");
            proteinModificationContainer.setCaption("<b>Proteoform Types</b>");
            proteinModificationContainer.setCaptionAsHtml(true);
            layoutMap.put("Modifications", proteinModificationContainer);
            lowerContainer.addComponent(proteinModificationContainer);
            Legend.this.updateModificationLayout("No Modifications");
            Legend.this.updateModificationLayout("Two or More Modifications");

//            VerticalLayout proteinIntensityContainer = new VerticalLayout();
//            proteinIntensityContainer.setHeightUndefined();
//            proteinIntensityContainer.setVisible(false);
//            proteinIntensityContainer.addStyleName("psmlegendrangeconatainer");
//            proteinIntensityContainer.addStyleName("intensitylegendrangeconatainer");
//            proteinIntensityContainer.setCaption("<b>Intensity</b>");
//            proteinIntensityContainer.setCaptionAsHtml(true);
//            layoutMap.put("Intensity", proteinIntensityContainer);
//            lowerContainer.addComponent(proteinIntensityContainer);
//
//            VerticalLayout proteoformModificationsContainer = new VerticalLayout();
//            proteoformModificationsContainer.setHeightUndefined();
//            proteoformModificationsContainer.setSpacing(true);
//            proteoformModificationsContainer.setVisible(true);
//            proteoformModificationsContainer.addStyleName("modificationlegendconatiner");
//            proteoformModificationsContainer.setCaption("<b>Proteoform Type</b>");
//            proteoformModificationsContainer.setCaptionAsHtml(true);
//            layoutMap.put("proteoformType", proteoformModificationsContainer);
//            lowerContainer.addComponent(proteoformModificationsContainer);
//            Legend.this.updateModificationLayout("No Modifications");
//            Legend.this.updateModificationLayout("Two or More Modifications");
        }

    }

    private Node generateNode(String defaultStyleName) {
        Node node = new Node("prot", "prot", new ModificationMatch[]{}, "", -1, Color.lightGray, -1000, Color.lightGray) {
            @Override
            public void selected(String id) {

            }
        };
        node.setDefaultStyleName(defaultStyleName);
        node.setSelected(true);
        node.addStyleName("defaultcursor");
        return node;
    }

    public void updateLegend(String mode) {
        this.removeStyleName("intensitycontainer");
        layoutMap.values().forEach((c) -> {
            c.setVisible(false);
        });
        if (mode.equals("Molecule Type")) {
            return;
        }
        if (mode.equals("Intensity")) {
            this.addStyleName("intensitycontainer");
        }

        layoutMap.get(mode).setVisible(true);

    }

    public void updateModificationLayout(ModificationMatch[] modMatch, String modifications) {
//        modifications = modifications.split("\\(")[0];
        if (modMatch == null || modMatch.length == 0) {// || modifications.split("\\(")[0].contains("Pyrolidone") || modifications.split("\\(")[0].contains("Acetylation of protein N-term")) {
            return;
        }

//        currentModifications += modifications.split("\\(")[0] + ";";
//       ModificationMatch[] modMatch=  new ModificationMatch[]{};
        Node node = new Node("prot", "prot", modMatch, null, -1, Color.lightGray, -1000, Color.lightGray) {
            @Override
            public void selected(String id) {

            }
        };
        node.setDefaultStyleName("peptidenode");
        node.setSelected(true);
        node.addStyleName("defaultcursor");
        node.setNodeStatues("Modifications");
        node.setDescription(modMatch[0].getModification());
        layoutMap.get("Modifications").addComponent(generateLabel(node, modMatch[0].getModification()));
    }

    public void updateModificationLayout(String modifications) {
        if (modifications.split("\\(")[0].trim().equalsIgnoreCase("")) {
            return;
        }
        if (currentModifications.contains(modifications.split("\\(")[0])) {
            return;
        }

        currentModifications += modifications.split("\\(")[0] + ";";

        Node node = new Node("prot", "prot", modifications, null, -1, "red", -1000, "") {
            @Override
            public void selected(String id) {

            }
        };
        node.setDefaultStyleName("peptidenode");
        node.setSelected(true);
        node.addStyleName("defaultcursor");
        node.setNodeStatues("Modifications");
        node.setDescription(modifications.split("\\(")[0]);
        int rowNum = layoutMap.get("Modifications").getComponentCount();
        if (rowNum == 0) {
            HorizontalLayout genLabel = generateLabel(node, modifications.split("\\(")[0]);
            layoutMap.get("Modifications").addComponent(generateRow(genLabel, new VerticalLayout()));
        } else {
            HorizontalLayout row = ((HorizontalLayout) layoutMap.get("Modifications").getComponent(rowNum - 1));
            if (row.getComponent(1) instanceof VerticalLayout) {
                row.removeComponent(row.getComponent(1));
                row.addComponent(generateLabel(node, modifications.split("\\(")[0]));
            } else {
                HorizontalLayout genLabel = generateLabel(node, modifications.split("\\(")[0]);
                layoutMap.get("Modifications").addComponent(generateRow(genLabel, new VerticalLayout()));
            }
        }

    }

    public void updatePSMNumberLayout(Component range) {
        layoutMap.get("PSMNumber").removeAllComponents();
        layoutMap.get("PSMNumber").addComponent(range);

    }

    public void updateIntensityLayout(Component range) {
        layoutMap.get("Intensity").removeAllComponents();
        layoutMap.get("Intensity").addComponent(range);

    }

    private HorizontalLayout generateRow(Component left, Component right) {
        HorizontalLayout row = new HorizontalLayout();
        row.setSizeFull();
        row.setSpacing(true);
        row.addComponent(left);
        row.addComponent(right);
        return row;
    }

    private HorizontalLayout generateLabel(Component small, String title) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidth(100, Unit.PERCENTAGE);
        row.setHeight(100, Unit.PERCENTAGE);
        row.setSpacing(true);
        row.addComponent(small);
        row.setExpandRatio(small, 25);
        row.setComponentAlignment(small, Alignment.TOP_LEFT);

        Label l = new Label(title);
        l.setStyleName(ValoTheme.LABEL_TINY);
        row.addComponent(l);
        row.setExpandRatio(l, 75);
        row.setComponentAlignment(l, Alignment.TOP_LEFT);
        return row;

    }

    private HorizontalLayout generateEdgeLabel(Component small, Component small2, String text) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidth(100, Unit.PERCENTAGE);
        row.setHeight(30, Unit.PIXELS);
        row.setSpacing(true);
        row.addComponent(small);
        row.setExpandRatio(small, 20);
        row.setComponentAlignment(small, Alignment.TOP_LEFT);

        VerticalLayout edge = new VerticalLayout();
        edge.setHeight(2, Unit.PIXELS);
        if (text.contains("Non") || text.contains("External")) {
            edge.addStyleName("dottedborder");
        } else {
            edge.addStyleName("solidborder");
        }
        if (text.contains("Internal")) {
            edge.addStyleName("bluelayout");
        }
        row.addComponent(edge);
        row.setExpandRatio(edge, 20);
        row.setComponentAlignment(edge, Alignment.MIDDLE_LEFT);

        row.addComponent(small2);
        row.setExpandRatio(small2, 10);
        row.setComponentAlignment(small2, Alignment.TOP_LEFT);

        Label l = new Label(text);
        l.setStyleName(ValoTheme.LABEL_TINY);
        row.addComponent(l);
        row.setExpandRatio(l, 50);
        row.setComponentAlignment(l, Alignment.TOP_LEFT);
        return row;

    }

    public abstract void close();

}
