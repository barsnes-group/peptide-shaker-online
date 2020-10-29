package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.ui.VerticalLayout;
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
    private String pdbId;
    private int litemolPluginInit = 0;

    public LiteMOL3DComponent() {
        LiteMOL3DComponent.this.setSizeFull();
        LiteMOL3DComponent.this.setStyleName("iframecontainer");
        LiteMOL3DComponent.this.addStyleName("litemolcontainer");
        proteinStructurePanel = new LiteMolComponent();
        proteinStructurePanel.setSizeFull();
        LiteMOL3DComponent.this.addComponent(proteinStructurePanel);

    }

    public void excuteQuery(String pdbId, int entity, String chainId, HashMap<String, Integer> proteinColor, HashSet<HashMap> entriesSet) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> jsonQuery = new HashMap();
        HashMap<String, Object> coloring = new HashMap<>();
        coloring.put("base", proteinColor);
        coloring.put("entries", entriesSet);
        jsonQuery.put("entity", entity);
        jsonQuery.put("pdbId", pdbId);
        jsonQuery.put("chainId", chainId);
        jsonQuery.put("coloring", coloring);
        try {
            String json = mapper.writeValueAsString(jsonQuery);
            proteinStructurePanel.setValue("query-_-" + json + "-_-" + (!pdbId.equalsIgnoreCase(LiteMOL3DComponent.this.pdbId)));
            LiteMOL3DComponent.this.pdbId = pdbId;
            if (litemolPluginInit==1) {
                proteinStructurePanel.setValue("query-_-" + json + "-_-" + true);
            }
            litemolPluginInit++;
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void setVisible(boolean visible) {

    }

    public boolean activate3DProteinView() {
        if (pdbId == null) {

            proteinStructurePanel.setValue("reset-_-");
            return false;
        } else {
            proteinStructurePanel.setValue("update-_-");
            return true;
        }
    }

    public void reset3DView() {
        pdbId = null;
        proteinStructurePanel.setValue("reset-_-");
    }

    public void redrawCanvas() {
        proteinStructurePanel.setValue("redraw-_-");
    }

}
