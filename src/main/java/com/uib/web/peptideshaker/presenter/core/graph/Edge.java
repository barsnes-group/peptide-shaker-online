/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.presenter.core.graph;

/**
 * This class represents edge in the developed graph
 *
 * @author Yehia Farag
 */
public class Edge {

    private final Node n1;
    private final Node n2;
    private final boolean dotted;
    private boolean hide;

    public Edge(Node n1, Node n2, boolean dotted) {
        this.n1 = n1;
        this.n2 = n2;
        this.dotted = dotted;
    }

    public boolean isSelected() {
        return (n1.isSelected() && n2.isSelected());
    }

    public void select(Node n, boolean uniqueOnly) {
        if (n == n2) {
            n2.setSelected(true);
            if (uniqueOnly && n1.getType() == 1 && n1.getEdgesNumber() == 1 || !uniqueOnly) {
                n1.setSelected(true);
            } else if (n1.getType() == 0) {
                n1.setSelected(true);
            }
        } else if (n == n1) {
            n1.setSelected(true);
            if (uniqueOnly && n2.getEdgesNumber() == 1 && n2.getType() == 1 || !uniqueOnly) {
                n2.setSelected(true);
            } else if (n2.getType() == 0) {
                n2.setSelected(true);
            }
        }
    }

    public double getStartX() {
        int n1Type = (int) n1.getType();
        switch (n1Type) {
            case 1:
                return n1.getX() + 8;
            case 0:
                return n1.getX() + 8;
            case 3:
                if (n1.isEnabled()) {
                    return n1.getX() + 10;
                }
                return n1.getX() + 5;
            default:
                return n1.getX();

        }
    }

    public double getStartY() {
        int n1Type = (int) n1.getType();
        switch (n1Type) {
            case 1:
                return n1.getY() + 8;
            case 0:
                return n1.getY() + 8;
            case 3:
                if (n1.isEnabled()) {
                    return n1.getY() + 10;
                }
                return n1.getY() + 5;
            default:
                return n1.getY();

        }
    }

    public double getEndX() {
        int n1Type = (int) n2.getType();
        switch (n1Type) {
            case 1:
                return n2.getX() + 15;
            case 0:
                return n2.getX() + 15;
            case 3:
                if (n1.isEnabled()) {
                    return n1.getX() + 10;
                }
                return n2.getX() + 5;
            default:
                return n2.getX();

        }
    }

    public double getEndY() {
        int n1Type = (int) n2.getType();
        switch (n1Type) {
            case 1:
                return n2.getY() + 15;
            case 0:
                return n2.getY() + 15;
            case 3:
                if (n1.isEnabled()) {
                    return n1.getY() + 10;
                }
                return n2.getY() + 5;
            default:
                return n2.getY();

        }
    }

    public Node getN1() {
        return n1;
    }

    public Node getN2() {
        return n2;
    }

    public boolean isDotted() {
        return dotted;
    }

    public boolean isHide() {
        return (n1.getStyleName().contains("nodedisabled") || n2.getStyleName().contains("nodedisabled"));
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public boolean isBelongToNode(String nodeId) {

        return n1.getNodeId().equalsIgnoreCase(nodeId) || n2.getNodeId().equalsIgnoreCase(nodeId);
    }

    public boolean isActive() {
        if (n1.getType() != 3) {
            return false;
        }
        return n1.isEnabled() && n2.isEnabled();
    }

}
