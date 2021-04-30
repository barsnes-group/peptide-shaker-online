package graphmatcher;

import com.compomics.util.experiment.biology.modifications.ModificationFactory;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import graphmatcher.NetworkGraphComponent.WrappedComponent;

import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * This class represents NetworkGraphNode for graph layout
 *
 * @author Yehia Mokhtar Farag
 */
public class NetworkGraphNode extends VerticalLayout {

    private final String nodeId;
    private final Set<NetworkGraphEdge> edges;
    private final boolean internal;
    private final boolean proteoform;
    private final String accession;
    private final boolean parent;
    private final ModificationFactory modificationFactory = ModificationFactory.getInstance();
    private final Map<String, Integer> modificationsLocationsMap;
    private final Color finalColor;
    private boolean selected;
    private String defaultStyleName;
    private int edgesNumber;
    private NetworkGraphNode parentNode;
    private Label modificationLabel;
    private NetworkGraphComponent.WrappedComponent wrappedComponent;
    private int type;
    private double x;
    private double y;
    private final Label nodeLabel;

    public NetworkGraphNode(String id, boolean internal, boolean parentNode) {
        NetworkGraphNode.this.setStyleName("node");
        NetworkGraphNode.this.addStyleName("pathwaynode");
        this.nodeId = id;
        this.accession = id.split(";")[0].split("-")[0];
        this.proteoform = id.contains(":") || nodeId.contains("-") || nodeId.contains(";");
        this.internal = internal;
        this.parent = parentNode;
        this.nodeLabel = new Label();
        nodeLabel.setStyleName("nodelabel");
        this.modificationsLocationsMap = new HashMap<>();
        if (!id.trim().equals("")) {
            NetworkGraphNode.this.setDescription(id);
        }

        AbsoluteLayout subContainer = new AbsoluteLayout();
        subContainer.setSizeFull();
        NetworkGraphNode.this.addComponent(subContainer);
        subContainer.addComponent(nodeLabel, "left:50;top:50%");

        String tooltip = id.split(";")[0] + "<br/>";

        if (parentNode) {
            NetworkGraphNode.this.addStyleName("proteoformparentnode");
        } else {
            NetworkGraphNode.this.addStyleName("proteoformnode");
        }

        if (internal) {
            NetworkGraphNode.this.addStyleName("internalNode");
            if (nodeId.contains("-")) {
                NetworkGraphNode.this.addStyleName("isoform");
            }
        } else {
            NetworkGraphNode.this.addStyleName("externalNode");
        }

        NetworkGraphNode.this.setWidth(25, Unit.PIXELS);
        NetworkGraphNode.this.setHeight(25, Unit.PIXELS);
        NetworkGraphNode.this.setSelected(false);
        this.edges = new LinkedHashSet<>();
        String newIds = nodeId;
        if (nodeId.contains(":")) {
            String mods = "";
            String mod = nodeId.split(";")[1];
            for (String subMod : mod.split(",")) {
                mods += ";" + subMod;//.split(":")[0]
                tooltip = tooltip + subMod.replace(":", " (") + ")" + " <br/>";
            }
            mods = mods.replaceFirst(";", "");
            tooltip = tooltip.substring(0, tooltip.length() - 6);
            Set<String> modificationsSet = new HashSet<>(Arrays.asList(mods.split(";")));
            Map<String, Color> colorsSet = new HashMap<>();
            Map<String, List<String>> modMap = new HashMap<>();
            Map<String, Color> covertedModColors = new HashMap<>();

//            for(String smodification : modificationsSet) {
//                System.out.println("modification to map _ "+smodification);
//                List<String> modList = PSIModificationFactory.getModificationsForPsiAccession(smodification);
//                if (!modMap.containsKey(smodification)) {
//                    modMap.put(smodification, new ArrayList<>());
//                }
//                if (modList != null) {
//                    modMap.get(smodification).addAll(modList);
//                }
//            }
            modificationsSet.stream().map((modification) -> modification.split(":")[0]).forEachOrdered((smodification) -> {
                List<String> modList = modificationFactory.getModificationsForPsiAccession(smodification);
                if (!modMap.containsKey(smodification)) {
                    modMap.put(smodification, new ArrayList<>());
                }
                if (modList != null) {
                    modMap.get(smodification).addAll(modList);
                }
            });
            for (String key : modMap.keySet()) {
                List<String> moList = modMap.get(key);

                String subtooltip = "";
                newIds = newIds.replaceAll(key, moList.toString().replace(",", "-_-"));
                for (String convMod : new LinkedHashSet<>(moList)) {
                    Color modColor = new Color(modificationFactory.getColor(convMod));
                    colorsSet.put(modColor.toString(), modColor);
                    covertedModColors.put(convMod, modColor);
                    subtooltip += "<font  style='color:rgb(" + modColor.getRed() + "," + modColor.getGreen() + "," + modColor.getBlue() + "); border-radius:100%;width: 100%;height: 100%;'>" + VaadinIcons.CIRCLE.getHtml() + "</font> " + convMod + " / ";
                }
                if (subtooltip.length() > 5) {
                    subtooltip = subtooltip.substring(0, subtooltip.length() - 3);
                } else {
                    subtooltip = "PSI-" + key;
                }
                tooltip = tooltip.replace(key, subtooltip) + "<br/>";
            }
            if (colorsSet.size() == 1) {
                finalColor = colorsSet.values().iterator().next();
                modificationLabel = new Label("<div  style='background:rgb(" + finalColor.getRed() + "," + finalColor.getGreen() + "," + finalColor.getBlue() + "); border-radius:100%;width: 100%;height: 100%;    color: white; line-height: 21px; text-align: center;font-size: 12px;  font-weight: 700;'>ModIndex</div>", ContentMode.HTML);
                modificationLabel.setSizeFull();
                subContainer.addComponent(modificationLabel);

            } else {
                finalColor = Color.ORANGE;
                if (internal) {
                    modificationLabel = new Label("<div  style='background:rgb(" + finalColor.getRed() + "," + finalColor.getGreen() + "," + finalColor.getBlue() + "); border-radius:100%;width: 100%;height: 100%;    color: white; line-height: 21px; text-align: center;font-size: 12px;  font-weight: 700;'>ModIndex</div>", ContentMode.HTML);
                } else {
                    modificationLabel = new Label("<div  style='background:rgb(" + finalColor.getRed() + "," + finalColor.getGreen() + "," + finalColor.getBlue() + "); border-radius:100%;width: 100%;height: 100%;    color: white; line-height: 21px; text-align: center;font-size: 12px;  font-weight: 700;'>  </div>", ContentMode.HTML);
                }

                modificationLabel.setSizeFull();
                subContainer.addComponent(modificationLabel);
                NetworkGraphNode.this.addStyleName("multimodificationproteoform");
            }
        } else {
            finalColor = Color.GRAY;
            if (id.contains(";") && internal) {
                modificationLabel = new Label("<div  style='background:rgb(" + finalColor.getRed() + "," + finalColor.getGreen() + "," + finalColor.getBlue() + "); border-radius:100%;width: 100%;height: 100%;    color: white; line-height: 21px; text-align: center;font-size: 12px;  font-weight: 700;'>ModIndex</div>", ContentMode.HTML);
                modificationLabel.setSizeFull();
                subContainer.addComponent(modificationLabel);
            }
        }
        tooltip = tooltip.replace("(null)", "");
        if (!tooltip.trim().equals("")) {
            NetworkGraphNode.this.setDescription(tooltip);
        }
        if (internal && newIds.split(";").length > 1) {
            newIds = newIds.replace("null", "-1");
            newIds = newIds.split(";")[1];
            for (String singleMod : newIds.split(",")) {
                int loc = Integer.valueOf(singleMod.split(":")[1]);
                for (String conMod : singleMod.replace("[", "").replace("]", "").split(":")[0].split("-_-")) {
                    modificationsLocationsMap.put(conMod, loc);
                }
            }
        }
        if (!this.internal) {
            Link l = new Link("", new ExternalResource("https://www.uniprot.org/uniprot/" + id.split(";")[0]));
            l.setCaptionAsHtml(true);
            l.setSizeFull();
            l.setTargetName("_blank");
            subContainer.addComponent(l);
        }
        if (this.parent) {
            Label subParentNode = new Label(VaadinIcons.SUN_O.getHtml(), ContentMode.HTML);
            subParentNode.setSizeFull();
            subContainer.addComponent(subParentNode);
        }
        nodeLabel.setVisible(false);
        nodeLabel.setValue(accession.trim());
    }

    public NetworkGraphNode getParentNode() {
        if (parent || parentNode == null) {
            return this;
        }
        return parentNode;
    }

    public void setParentNode(NetworkGraphNode parentNode) {
        this.parentNode = parentNode;
    }

    public WrappedComponent getWrappedComponent() {
        return wrappedComponent;
    }

    public void setWrappedComponent(WrappedComponent wrappedComponent) {
        this.wrappedComponent = wrappedComponent;
    }

    public Map<String, Integer> getModificationsLocationsMap() {
        return modificationsLocationsMap;
    }

    public String getNodeId() {
        return nodeId;
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

    public void addEdge(NetworkGraphEdge e) {
        edges.add(e);
        addEdge();
    }

    public Set<NetworkGraphEdge> getRelatedEdges() {

        return edges;
    }

    public Set<NetworkGraphEdge> getEdges() {
        return edges;
    }

    public boolean isInternal() {
        return internal;
    }

    public boolean isProteoform() {
        return proteoform;
    }

    public String getAccession() {
        return accession;
    }

    public boolean isParent() {
        return parent;
    }

    public Color getFinalColor() {
        return finalColor;
    }

    public void updateProteformIndex(String proteformIndex) {

        if (modificationLabel != null) {
            if (!internal) {
                proteformIndex = "";
            }
            modificationLabel.setValue(modificationLabel.getValue().replace("ModIndex", proteformIndex));
        }
    }

    public void setShowLabel(boolean show) {
        nodeLabel.setVisible(show);
    }

}
