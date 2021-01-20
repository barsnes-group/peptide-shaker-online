package com.uib.web.peptideshaker.ui.views.subviews.proteinsview.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.ui.VerticalLayout;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import litemol.LiteMolComponent;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * This class provides an abstraction layer for LiteMOL 3D protein structure
 *
 * @author Yehia Farag
 */
public class LiteMOL3DComponent extends VerticalLayout {

    private final LiteMolComponent proteinStructurePanel;
    private String pdbId="notReal";
    private int litemolPluginInit = 0;

    public LiteMOL3DComponent() {
        LiteMOL3DComponent.this.setSizeFull();
        LiteMOL3DComponent.this.setStyleName("iframecontainer");
        LiteMOL3DComponent.this.addStyleName("litemolcontainer");
        proteinStructurePanel = new LiteMolComponent();
        proteinStructurePanel.setSizeFull();
        LiteMOL3DComponent.this.addComponent(proteinStructurePanel);
    }

    public void excuteQuery(String pdbId, int entity, String chainId, JsonObject proteinColor, JsonArray entriesSet) {
        JsonObject body = new JsonObject();

        JsonObject coloring = new JsonObject();
        coloring.put("base", proteinColor);
        coloring.put("entries", entriesSet);
        body.put("entity", entity);
        body.put("pdbId", pdbId);
        body.put("chainId", chainId);
        body.put("coloring", coloring);
        body.put("type", "query");
        System.out.println("at new value "+(!pdbId.equalsIgnoreCase(this.pdbId)));
        body.put("newid", (!pdbId.equalsIgnoreCase(LiteMOL3DComponent.this.pdbId)));
        try {
            proteinStructurePanel.setValue(body);
            this.pdbId = pdbId;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void setVisible(boolean visible) {

    }

    public boolean activate3DProteinView() {
        JsonObject body = new JsonObject();
        if (pdbId == null) {
            body.put("type", "reset");
            proteinStructurePanel.setValue(body);
            return false;
        } else {
            body.put("type", "update");
            proteinStructurePanel.setValue(body);
            return true;
        }
    }

    public void reset3DView() {
        JsonObject body = new JsonObject();
        pdbId = null;
        body.put("type", "reset");
        proteinStructurePanel.setValue(body);
    }

    public void redrawCanvas() {
        JsonObject body = new JsonObject();
        body.put("type", "redraw");
        proteinStructurePanel.setValue(body);
    }

}
