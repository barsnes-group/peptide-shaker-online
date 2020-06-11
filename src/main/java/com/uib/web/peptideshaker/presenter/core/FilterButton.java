/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.event.LayoutEvents;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Image;
import com.vaadin.ui.VerticalLayout;

/**
 *
 * @author Yehia Farag
 */
public abstract class FilterButton extends VerticalLayout implements LayoutEvents.LayoutClickListener {

    private final Image icon;
    private final ThemeResource activeBunResource = new ThemeResource("img/removefilter.png");
    private final ThemeResource inActiveBunResource = new ThemeResource("img/nofilter_disabled_1.png");

    public FilterButton() {
        FilterButton.this.setStyleName("btnlayout");
        FilterButton.this.setWidth(22, Unit.PIXELS);
        FilterButton.this.setHeight(22, Unit.PIXELS);
        icon = new Image();
        icon.setSizeFull(); 
        icon.setSource(activeBunResource);
//        icon.setSource(inActiveBunResource);
FilterButton.this.addStyleName("hide");
        FilterButton.this.addComponent(icon);
        FilterButton.this.addLayoutClickListener(FilterButton.this);
        FilterButton.this.setEnabled(false);

    }

    private void setActiveBtn(boolean active) {
        if (active) {
            this.removeStyleName("hide");
           
            this.addStyleName("pointer");
            this.addStyleName("highlightfilter");

        } else {
            this.addStyleName("hide");
//            icon.setSource(inActiveBunResource);
//            this.removeStyleName("pointer");
//            this.removeStyleName("highlightfilter");
        }
        this.setEnabled(active);
    }

    @Override
    public void setVisible(boolean visible) {
        setActiveBtn(visible);
    }

}
