package com.uib.web.peptideshaker.ui.views.subviews.proteinsview.components;

import com.vaadin.ui.VerticalLayout;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import litemol.LiteMolComponent;


/**
 * This class provides an abstraction layer for LiteMOL 3D protein structure
 *
 * @author Yehia Farag
 */
public class LiteMOL3DComponent extends VerticalLayout {

    private final LiteMolComponent proteinStructurePanel;
    private String pdbId = "notReal";
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
        JsonObject fullQuery = new JsonObject();
        JsonObject values = new JsonObject();
//        System.out.println("'{\"pdbId\":\"3iuc\",\"chainId\":\"A\",\"coloring\":{\"entries\":[{\"start_residue_number\":44,\"color\":{\"r\":255,\"b\":0,\"g\":0},\"end_residue_number\":404,\"struct_asym_id\":\"A\",\"entity_id\":\"1\"}],\"base\":{\"r\":255,\"b\":255,\"g\":255}}}';");
        JsonArray queryEntriesSet = new JsonArray();
        for (int i = 0; i < entriesSet.size(); i++) {
            JsonObject peptide = entriesSet.getJsonObject(i);
            JsonObject pepQuery = new JsonObject();
            pepQuery.put("start_residue_number", peptide.getInteger("start_residue_number"));
            pepQuery.put("color", peptide.getJsonObject("color"));
            pepQuery.put("end_residue_number", peptide.getInteger("end_residue_number"));
            pepQuery.put("struct_asym_id", peptide.getString("struct_asym_id"));
            pepQuery.put("entity_id", peptide.getString("entity_id"));
            queryEntriesSet.add(pepQuery);
        }

        values.put("pdbId", pdbId);
        values.put("chainId", chainId);
        JsonObject coloring = new JsonObject();
        coloring.put("entries", queryEntriesSet);
        coloring.put("base", proteinColor);
        values.put("coloring", coloring);
        values.put("entity", entity);
        fullQuery.put("values", values);
        fullQuery.put("type", "query");
        fullQuery.put("newid", (!pdbId.equalsIgnoreCase(LiteMOL3DComponent.this.pdbId)));
        try {
            proteinStructurePanel.setValue(fullQuery);
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

    public void reset() {
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
