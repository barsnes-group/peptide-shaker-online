/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.presenter.core;

import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.GalaxyFileObject;
import com.uib.web.peptideshaker.presenter.core.form.Horizontal2Label;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author y-mok
 */
public abstract class FileOverviewLayout extends VerticalLayout {
/**
     * Decimal Format for memory usage
     */
    private final DecimalFormat dsFormater = new DecimalFormat("#.##");
    /**
     * Constructor to initialise the main setting parameters
     *
     * @param dataset
     */
    public FileOverviewLayout(GalaxyFileObject dataset) {
        FileOverviewLayout.this.setMargin(true);
        FileOverviewLayout.this.setWidth(300,Unit.PIXELS);
        FileOverviewLayout.this.setHeightUndefined();
        FileOverviewLayout.this.setSpacing(true);
        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setSizeFull();
        titleLayout.addStyleName("subpanelframe");

        FileOverviewLayout.this.addComponent(titleLayout);
        Label projectNameLabel = new Label(dataset.getName().split("___")[0]);
        projectNameLabel.addStyleName(ValoTheme.LABEL_BOLD);
        titleLayout.setWidth(100,Unit.PERCENTAGE);
        titleLayout.addComponent(projectNameLabel);
        titleLayout.setComponentAlignment(projectNameLabel, Alignment.TOP_CENTER);

        Button closeIconBtn = new Button("Close");
        closeIconBtn.setIcon(VaadinIcons.CLOSE_SMALL, "Close window");
        closeIconBtn.setStyleName(ValoTheme.BUTTON_SMALL);
        closeIconBtn.addStyleName(ValoTheme.BUTTON_TINY);
        closeIconBtn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        closeIconBtn.addStyleName("centerbackground");
        closeIconBtn.setHeight(25, Unit.PIXELS);
        closeIconBtn.setWidth(25, Unit.PIXELS);

        closeIconBtn.addClickListener((Button.ClickEvent event) -> {
            close();
        });
        titleLayout.addComponent(closeIconBtn);
        titleLayout.setComponentAlignment(closeIconBtn, Alignment.TOP_RIGHT);
        VerticalLayout upperPanel = new VerticalLayout();
        upperPanel.setWidth(100, Unit.PERCENTAGE);
        upperPanel.addStyleName("subpanelframe");
        FileOverviewLayout.this.addComponent(upperPanel);

        Horizontal2Label createdLabel = new Horizontal2Label("Created :", dataset.getCreate_time());
        upperPanel.addComponent(createdLabel);
        Horizontal2Label sizeLabel = new Horizontal2Label("Size    :", dsFormater.format(dataset.getSize())+" MB");
        upperPanel.addComponent(sizeLabel);
        Horizontal2Label formatLabel = new Horizontal2Label("Format  :", dataset.getType());
        upperPanel.addComponent(formatLabel);

    }

    public abstract void close();

    private Map<String, Object> jsonToMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = jsonToMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    private List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = jsonToMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

}
