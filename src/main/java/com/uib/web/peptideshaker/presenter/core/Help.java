/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.VerticalLayout;

/**
 *
 * @author y-mok
 */
public class Help extends AbsoluteLayout {

    private final PopupView infoPopup;

    public Help(String text, String noteText ,int width,int height) {
        Help.this.setHeight(40, Unit.PIXELS);
        Help.this.setStyleName("infopopupcontainer");

        AbsoluteLayout closableLayout = new AbsoluteLayout();
        closableLayout.setStyleName("infolayout");
        closableLayout.setWidth(width,Unit.PIXELS);
        closableLayout.setHeight(height,Unit.PIXELS);

        String popupCaption = VaadinIcons.QUESTION_CIRCLE.getHtml();
        if (text.startsWith("<a")) {
            popupCaption = text + VaadinIcons.QUESTION_CIRCLE.getHtml() + "</a>";
            infoPopup = new PopupView(popupCaption, closableLayout);
            infoPopup.setEnabled(false);
        } else {
            infoPopup = new PopupView(popupCaption, closableLayout);

        }
        infoPopup.setCaptionAsHtml(true);
        infoPopup.setStyleName("infopopup");
        infoPopup.setHideOnMouseOut(false);
        Help.this.addComponent(infoPopup, "left:10px;top:0px");
        CloseButton closeBtn = new CloseButton() {
            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                infoPopup.setPopupVisible(false);
            }
        };
        closableLayout.addComponent(closeBtn, "top:10px; right:20px;");
        Label infoLabel = new Label(text, ContentMode.HTML);
        closableLayout.addComponent(infoLabel, "left:10px;top:10px; right:20px;");
        if (noteText != null) {
            Label note = new Label(noteText, ContentMode.HTML);
            note.setDescription(noteText);
            Help.this.addComponent(note, "left:30px;top:0px");
        }

    }

}
