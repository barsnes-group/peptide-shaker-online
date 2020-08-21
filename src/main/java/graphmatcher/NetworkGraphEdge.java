/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphmatcher;

/**
 * This class represents edge in the developed graph
 *
 * @author Yehia Farag
 */
public class NetworkGraphEdge {

    private final NetworkGraphNode n1;
    private final NetworkGraphNode n2;
    private final boolean localEdge;
    private NetworkGraphNode pn1;
    private NetworkGraphNode pn2;
    private NetworkGraphNode firstNode;
    private NetworkGraphNode secondNode;
    private boolean hide;
//    private final String containerId;
//    private final String type;
    //    private final Link edgeLabel;
    private String cssLabelPosition;

    public NetworkGraphEdge(NetworkGraphNode n1, NetworkGraphNode n2, boolean localEdge) {
        this.n1 = n1;
        this.n2 = n2;
//        this.containerId = containerId;
//        this.type = type;
        this.firstNode = n1;
        this.secondNode = n2;
        this.localEdge = localEdge;
//        String query;
//        if (localEdge) {
//            query = n1.getAccession();
//        } else {
//            query = n2.getAccession();
//        }
//        edgeLabel = new Link(query, new ExternalResource("https://reactome.org/content/query?q=" + query + "&types=Protein&cluster=true")) {
//            @Override
//            public void setVisible(boolean visible) {
//                if (localEdge) {
//                    visible = false;
//                }
//                super.setVisible(visible);
//
//            }
//
//        };
//        edgeLabel.setStyleName("edgeLabel");
//        edgeLabel.setVisible(false);
//        edgeLabel.setTargetName("_blank");
//        edgeLabel.setDescription("View in Reactom");

    }

//    public String getType() {
//        return type;
//    }

    public boolean isSelected() {
        return (firstNode.isSelected() && secondNode.isSelected());
    }

    public boolean isLocalEdge() {
        return localEdge;
    }

    public void setHideChildNodes(boolean hideChildNodes) {
        if (hideChildNodes) {
            firstNode = n1.getParentNode();
            secondNode = n2.getParentNode();
        } else {
            firstNode = n1;
            secondNode = n2;

        }

    }

    public void select(NetworkGraphNode n, boolean uniqueOnly) {
        if (n == secondNode) {
            secondNode.setSelected(true);
            if (uniqueOnly && firstNode.getType() == 1 && firstNode.getEdgesNumber() == 1 || !uniqueOnly) {
                firstNode.setSelected(true);
            } else if (firstNode.getType() == 0) {
                firstNode.setSelected(true);
            }
        } else if (n == firstNode) {
            firstNode.setSelected(true);
            if (uniqueOnly && secondNode.getEdgesNumber() == 1 && secondNode.getType() == 1 || !uniqueOnly) {
                secondNode.setSelected(true);
            } else if (secondNode.getType() == 0) {
                secondNode.setSelected(true);
            }
        }
    }

    public boolean isInternalEdge() {
        return firstNode.isInternal() && secondNode.isInternal();
    }

    public double getStartX() {

        return firstNode.getX();

    }

    public double getStartY() {

        return firstNode.getY();

    }

    public double getEndX() {

        return secondNode.getX();

    }

    public double getEndY() {

        return secondNode.getY();

    }

    public NetworkGraphNode getN1() {
        return firstNode;
    }

    public NetworkGraphNode getN2() {
        return secondNode;
    }

    public boolean isHide() {
        return hide;//(firstNode.getStyleName().contains("nodedisabled") || secondNode.getStyleName().contains("nodedisabled"));
    }

    public void setHide(boolean hide) {
//        this.hide = hide;
//        if (hide) {
//            edgeLabel.setVisible(false);
//        }
    }

    public boolean isBelongToNode(String nodeId) {

        return firstNode.getNodeId().equalsIgnoreCase(nodeId) || secondNode.getNodeId().equalsIgnoreCase(nodeId);
    }

    public boolean isActive() {
        if (firstNode.getType() != 3) {
            return false;
        }
        return firstNode.isEnabled() && secondNode.isEnabled();
    }

//    public String getContainerId() {
//        return containerId;
//    }

    public void setLabelPostion(int x, int y) {
        cssLabelPosition = "left:" + x + "px; top:" + y + "px;";

    }

//    public Link getEdgeLabel() {
//        return edgeLabel;
//    }

    public String getCssLabelPosition() {
        return cssLabelPosition;
    }

}
