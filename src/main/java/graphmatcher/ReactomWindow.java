/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphmatcher;

import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

/**
 * This class represents popup window that show Reactom data
 *
 * @author Yehia Mokhtar Farag
 */
public class ReactomWindow extends Window {

    private final BrowserFrame frame;

    public ReactomWindow() {
        ReactomWindow.this.setWidth(95, Unit.PERCENTAGE);
        ReactomWindow.this.setHeight(95, Unit.PERCENTAGE);
        ReactomWindow.this.center();
        ReactomWindow.this.setStyleName("reactomwindow");
        ReactomWindow.this.setModal(true);
        ReactomWindow.this.setWindowMode(WindowMode.NORMAL);
        ReactomWindow.this.setResizable(false);
        ReactomWindow.this.setClosable(true);
        UI.getCurrent().addWindow(ReactomWindow.this);
        frame = new BrowserFrame();
        frame.setSizeFull();
        ReactomWindow.this.setContent(frame);
        ReactomWindow.this.setVisible(false);
    }

    @Override
    public void close() {
        this.setVisible(false);
    }

    public void visulaizeProtein(String protAccession) {
        System.out.println("at protein acc "+protAccession);
        frame.setSource(new ThemeResource("reactom/reactomcontainer.html?id=" + protAccession.substring(0, protAccession.length() - 1)));
        ReactomWindow.this.setVisible(true);
    }
}
