
package com.uib.web.peptideshaker.ui.components.items;

import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;

/**
 * @author Yehia Farag
 */
public class HelpPopupButton extends AbsoluteLayout {

    private final PopupView infoPopup;

    public HelpPopupButton(String text, String noteText, int width, int height) {
        HelpPopupButton.this.setHeight(40, Unit.PIXELS);
        HelpPopupButton.this.setStyleName("infopopupcontainer");
        HelpPopupButton.this.setDescription("Information");

        AbsoluteLayout closableLayout = new AbsoluteLayout();
        closableLayout.setStyleName("infolayout");
        closableLayout.setWidth(width, Unit.PIXELS);
        closableLayout.setHeight(height, Unit.PIXELS);

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
        HelpPopupButton.this.addComponent(infoPopup, "left:10px;top:0px");
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
            HelpPopupButton.this.addComponent(note, "left:30px;top:0px");
        }

    }

}
