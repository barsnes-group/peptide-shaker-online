package com.uib.web.peptideshaker.ui.components.items;

import com.compomics.util.experiment.biology.modifications.ModificationFactory;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.vaadin.event.LayoutEvents;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class represents Node for graph layout
 *
 * @author Yehia Mokhtar Farag
 */
public abstract class Node extends VerticalLayout implements LayoutEvents.LayoutClickListener {

    private final String nodeId;
    private final VerticalLayout modificationLayout;
    private final VerticalLayout psmNumberLayout;
    private final VerticalLayout intensityLayout;
    private final String modificationStyleName = "nodemodificationbackground";
    private final DecimalFormat df1 = new DecimalFormat("0.00E00");// new DecimalFormat("#.##");
    private final Set<Edge> edges;
    /**
     * The post translational modifications factory.
     */
    private final ModificationFactory PTM = ModificationFactory.getInstance();
    private boolean selected;
    private String defaultStyleName;
    private String validationStatuesStyle;
    private String proteinEvidenceStyle;
    private int edgesNumber;
    private Color validationColor = Color.lightGray;
    private Color modificationColor = Color.lightGray;
    private Color evidenceColor = Color.lightGray;
    private Color psmColor = Color.lightGray;
    private Color intensityColor = Color.lightGray;
    private Color currentActiveColor;
    private int type;
    private double x;
    private double y;

    public Node(String id, String tooltip, String modifications, String sequence, int psmNumber, String PSMNumberColor, double intensity, String intensityColor) {

        Node.this.setStyleName("node");
        Node.this.addLayoutClickListener(Node.this);
        this.nodeId = id;
        this.modificationLayout = new VerticalLayout();
        this.modificationLayout.setSizeFull();
        Node.this.addComponent(modificationLayout);
        String subTooltip = "";
        Map<String, String> modificationsTooltip = new HashMap<>();
        for (String mod : modifications.split("\\),")) {
            if (mod.trim().equalsIgnoreCase("") || mod.contains("No Modifications") || mod.contains("Two or More Modifications")) {// || mod.contains("Pyrolidone") || mod.contains("Acetylation of protein N-term") || mod.contains("No Modifications") || mod.contains("Two or More Modifications")) {
                continue;
            } else if (mod.contains(";")) {
                mod = mod.split(";")[0];
            }
            String[] tmod = mod.split("\\(");
            Color c = new Color(ModificationFactory.getDefaultColor(tmod[0].trim()));
            Label modification = new Label("<div  style='background:rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ");border-radius:100%;width: 100%;height: 100%;opacity:0.2;'></div>", ContentMode.HTML);
            modification.setSizeFull();
            modificationLayout.addComponent(modification);
            String[] indexArr = tmod[1].replace(")", "").replace(" ", "").split(",");
            for (String indexStr : indexArr) {
                int i = Math.max(Integer.valueOf(indexStr) - 1, 0);
                if (sequence != null) {
                    modificationsTooltip.put(sequence.charAt(i) + "<" + PTM.getModification(tmod[0].trim()).getShortName() + ">", "<font style='background-color:rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")'>" + sequence.charAt(i) + "</font>");

                    if (!subTooltip.contains("</br><span style='width:20px;height:10px;background-color:rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")'>" + sequence.charAt(i) + "</span> - " + mod)) {
                        subTooltip += "</br><span style='width:20px;height:10px;background-color:rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")'>" + sequence.charAt(i) + "</span> - " + mod;
                    }
                }
            }

        }
        if (modificationLayout.getComponentCount() > 1 || modifications.equalsIgnoreCase("Two or More Modifications")) {
            modificationLayout.removeAllComponents();
            Label modification = new Label("<div style='background:orange; width:100%;height:100%;border-radius:100%;opacity:0.2;'></div>", ContentMode.HTML);
            modification.setSizeFull();
            modification.addStyleName("multiplemodificationstyle");
            modificationLayout.addComponent(modification);
        }
        for (String key : modificationsTooltip.keySet()) {
            tooltip = tooltip.replace(key, modificationsTooltip.get(key));
        }
        tooltip += subTooltip;
        modificationLayout.setVisible(false);

        this.psmNumberLayout = new VerticalLayout();
        this.psmNumberLayout.setSizeFull();
        Node.this.addComponent(psmNumberLayout);
        String tooltipExt = "</br>#PSMs: " + psmNumber + "";
        if (psmNumber == -1) {
            PSMNumberColor = "red";
            tooltipExt = "";
        }
        Label psmsColorLabel = new Label("<div  style='background:" + PSMNumberColor + ";border-radius:100%;width: 100%;height: 100%;opacity:0.2;'></div>", ContentMode.HTML);
        psmsColorLabel.setSizeFull();
        psmNumberLayout.addComponent(psmsColorLabel);
        psmNumberLayout.setVisible(true);

        this.intensityLayout = new VerticalLayout();
        this.intensityLayout.setSizeFull();
        Node.this.addComponent(intensityLayout);
        String tooltipExt2 = "</br>Intensity: " + df1.format(intensity) + "";
        if (intensity == -10000 || intensity == 0) {
            tooltipExt2 = "";
        }
        Label intensityColorLabel = new Label("<div  style='background:" + intensityColor + ";border-radius:100%;width: 100%;height: 100%;opacity:0.2;'></div>", ContentMode.HTML);
        intensityColorLabel.setSizeFull();
        intensityColorLabel.setStyleName("intensitycolornode");
        intensityLayout.addComponent(intensityColorLabel);
        intensityLayout.setVisible(false);

        tooltip += tooltipExt;
        tooltip += tooltipExt2;
        if (!tooltip.trim().equals("")) {
            Node.this.setDescription(tooltip);
        }

        this.edges = new LinkedHashSet<>();

    }

    public Node(String id, String tooltip, ModificationMatch[] modifications, String sequence, int psmNumber, Color PSMNumberColor, double intensity, Color intensityColor) {

        Node.this.setStyleName("node");
        Node.this.addLayoutClickListener(Node.this);
        this.nodeId = id;
        this.modificationLayout = new VerticalLayout();
        this.modificationLayout.setSizeFull();
        Node.this.addComponent(modificationLayout);

        String subTooltip = "";
        Map<String, String> modificationsTooltip = new HashMap<>();
         Set<String>modNames = new HashSet<>();          
        if (modifications != null && modifications.length > 0) {
            
            for (ModificationMatch mod : modifications) { 
                modNames.add(mod.getModification());
                if (mod.getModification().trim().equalsIgnoreCase("") || mod.getModification().contains("No Modifications") || mod.getModification().contains("Two or More Modifications")) {// || mod.contains("Pyrolidone") || mod.contains("Acetylation of protein N-term") || mod.contains("No Modifications") || mod.contains("Two or More Modifications")) {
                    continue;
                }
                modificationColor = new Color(ModificationFactory.getDefaultColor(mod.getModification()));
                Label modification = new Label("<div  style='background:rgb(" + modificationColor.getRed() + "," + modificationColor.getGreen() + "," + modificationColor.getBlue() + ");border-radius:100%;width: 100%;height: 100%;opacity:0.2;'></div>", ContentMode.HTML);
                modification.setSizeFull();
                modificationLayout.addComponent(modification);
                int i = Math.max(mod.getSite() - 1, 0);
                if (sequence != null) {
                    modificationsTooltip.put(sequence.charAt(i) + "<" + PTM.getModification(mod.getModification()).getShortName() + ">", "<font style='background-color:rgb(" + modificationColor.getRed() + "," + modificationColor.getGreen() + "," + modificationColor.getBlue() + ")'>" + sequence.charAt(i) + "</font>");
                    if (!subTooltip.contains("</br><span style='width:20px;height:10px;background-color:rgb(" + modificationColor.getRed() + "," + modificationColor.getGreen() + "," + modificationColor.getBlue() + ")'>" + sequence.charAt(i) + "</span> - " + mod.getModification())) {
                        subTooltip += "</br><span style='width:20px;height:10px;background-color:rgb(" + modificationColor.getRed() + "," + modificationColor.getGreen() + "," + modificationColor.getBlue() + ")'>" + sequence.charAt(i) + "</span> - " + mod.getModification();
                    }
                }

            }
        }
        if (modNames.size()>1 && modifications != null && (modificationLayout.getComponentCount() > 1 || modifications.length > 1)) {// modifications.equalsIgnoreCase("Two or More Modifications")) {
          
            
            modificationLayout.removeAllComponents();
            Label modification = new Label("<div style='background:orange; width:100%;height:100%;border-radius:100%;opacity:0.2;'></div>", ContentMode.HTML);
            modification.setSizeFull();
            modification.addStyleName("multiplemodificationstyle");
            modificationLayout.addComponent(modification);
            modificationColor = Color.ORANGE;
        }
        for (String key : modificationsTooltip.keySet()) {
            tooltip = tooltip.replace(key, modificationsTooltip.get(key));
        }
        tooltip += subTooltip;
        modificationLayout.setVisible(false);

        this.psmNumberLayout = new VerticalLayout();
        this.psmNumberLayout.setSizeFull();
        Node.this.addComponent(psmNumberLayout);
        String tooltipExt = "</br>#PSMs: " + psmNumber + "";
        if (psmNumber == -1) {
            PSMNumberColor = Color.RED;
            tooltipExt = "";
        }
        Label psmsColorLabel = new Label("<div  style='background:rgb(" + PSMNumberColor.getRed() + "," + PSMNumberColor.getGreen() + "," + PSMNumberColor.getBlue() + ");border-radius:100%;width: 100%;height: 100%;opacity:0.2;'></div>", ContentMode.HTML);
        psmsColorLabel.setSizeFull();
        psmNumberLayout.addComponent(psmsColorLabel);
        psmNumberLayout.setVisible(false);

        this.intensityLayout = new VerticalLayout();
        this.intensityLayout.setSizeFull();
        Node.this.addComponent(intensityLayout);
        String tooltipExt2 = "</br>Intensity: " + df1.format(intensity) + "";
        if (intensity == -10000 || intensity == 0) {
            tooltipExt2 = "";
        }
        Label intensityColorLabel = new Label("<div  style='background:rgb(" + intensityColor.getRed() + "," + intensityColor.getGreen() + "," + intensityColor.getBlue() + ");border-radius:100%;width: 100%;height: 100%;opacity:0.2;'></div>", ContentMode.HTML);
        intensityColorLabel.setSizeFull();
        intensityColorLabel.setStyleName("intensitycolornode");
        intensityLayout.addComponent(intensityColorLabel);
        intensityLayout.setVisible(false);

        tooltip += tooltipExt;
        tooltip += tooltipExt2;
        this.intensityColor = intensityColor;
        this.psmColor = PSMNumberColor;
        Node.this.setDescription(tooltip);
        this.edges = new LinkedHashSet<>();

    }

    public Color getCurrentActiveColor() {
        return currentActiveColor;
    }

    public void setValidationColor(Color validationColor) {
        this.validationColor = validationColor;
    }

    public void setEvidenceColor(Color evidenceColor) {
        this.evidenceColor = evidenceColor;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getProteinEvidenceStyle() {
        return proteinEvidenceStyle;
    }

    public void setProteinEvidenceStyle(String proteinEvidenceStyle) {
        this.proteinEvidenceStyle = proteinEvidenceStyle;
    }

    public int getEdgesNumber() {
        return edgesNumber;
    }

    public void addEdge() {
        this.edgesNumber++;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
        switch (type) {
            case 1:
                Node.this.setWidth(28, Unit.PIXELS);
                Node.this.setHeight(28, Unit.PIXELS);
                break;
            case 2:
                Node.this.setWidth(20, Unit.PIXELS);
                Node.this.setHeight(20, Unit.PIXELS);
                break;
            case 3:
                Node.this.setWidth(10, Unit.PIXELS);
                Node.this.setHeight(10, Unit.PIXELS);
                Node.this.setSelected(false);

        }
    }

    public void setNodeStatues(String statues) {
        this.resetStyle();
        if (statues.equalsIgnoreCase("Molecule Type")) {
            this.addStyleName(defaultStyleName);
        } else if (statues.equalsIgnoreCase("Validation")) {
            this.addStyleName(validationStatuesStyle);
            this.currentActiveColor = validationColor;
        } else if (statues.equalsIgnoreCase("Protein Evidence")) {
            this.addStyleName(proteinEvidenceStyle);
            this.currentActiveColor = evidenceColor;
        } else if (statues.equalsIgnoreCase("Modifications")) {
            modificationLayout.setVisible(true);
            this.addStyleName(modificationStyleName);
            this.currentActiveColor = modificationColor;
        } else if (statues.equalsIgnoreCase("PSMNumber")) {
            this.psmNumberLayout.setVisible(true);
            this.addStyleName(modificationStyleName);
            this.currentActiveColor = psmColor;
        } else if (statues.equalsIgnoreCase("Intensity")) {
            this.intensityLayout.setVisible(true);
            this.addStyleName(modificationStyleName);
            this.currentActiveColor = intensityColor;

        }

    }

    public String getValidationStatuesStyle() {
        return validationStatuesStyle;
    }

    public void setValidationStatuesStyle(String validationStatuesStyle) {
        this.validationStatuesStyle = validationStatuesStyle;
    }

    public String getDefaultStyleName() {
        return defaultStyleName;
    }

    public void setDefaultStyleName(String defaultStyleName) {
        this.setStyleName(defaultStyleName);
        this.defaultStyleName = defaultStyleName;
    }

    public void resetStyle() {
        this.removeStyleName(this.getStyleName());
        this.setStyleName(defaultStyleName);
        this.setSelected(selected);
        this.modificationLayout.setVisible(false);
        this.psmNumberLayout.setVisible(false);
        this.intensityLayout.setVisible(false);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean isSelected() {

        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            this.addStyleName("selectednode");
        } else {
            this.removeStyleName("selectednode");
        }

    }

    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        selected(nodeId);
    }

    public abstract void selected(String id);

    public void setUniqueOnlyMode(boolean uniqueOnlyMode) {
        if (type == 1 && uniqueOnlyMode && edgesNumber > 1) {
            Node.this.addStyleName("nodedisabled");
            Node.this.setEnabled(false);
        } else if (type == 1) {
            Node.this.removeStyleName("nodedisabled");
            Node.this.setEnabled(true);
        }
    }

    public void addEdge(Edge e) {

        edges.add(e);
        addEdge();
        this.setDescription(this.getDescription() + "(" + edges.size() + ")");
    }

    public Set<Edge> getRelatedEdges() {

        return edges;
    }

}
