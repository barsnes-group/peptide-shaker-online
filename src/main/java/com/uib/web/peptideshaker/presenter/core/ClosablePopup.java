/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.VerticalLayout;

/**
 * This class represents popup layout with close button
 *
 * @author Yehia Farag
 */
public class ClosablePopup extends VerticalLayout {

    private final PopupView popupView;

    public ClosablePopup(String title, Component content, String caption) {

        AbsoluteLayout container = new AbsoluteLayout();
        container.setWidth(200, Unit.PIXELS);
        container.setHeight(90, Unit.PIXELS);
        container.setStyleName("popupclosablecontent");
        CloseButton btn = new CloseButton() {
            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                popupView.setPopupVisible(false);
            }
        };
        container.addComponent(btn, "right:-17px;top:-14px");
        Label l = new Label(title);
        container.addComponent(l, "left:5px;top:5px");
        
        container.addComponent(content, "left:5px;bottom:5px;top:32px;right:5px;");
        popupView = new PopupView(caption, container);
        popupView.setCaptionAsHtml(true);
        popupView.setHideOnMouseOut(false);
        popupView.setStyleName("popupwithicon");
        ClosablePopup.this.addComponent(popupView);
    }

}
