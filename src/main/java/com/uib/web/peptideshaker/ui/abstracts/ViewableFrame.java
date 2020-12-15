package com.uib.web.peptideshaker.ui.abstracts;

import com.uib.web.peptideshaker.ui.components.items.ButtonWithLabel;
import com.uib.web.peptideshaker.ui.components.items.SmallSideBtn;
import com.vaadin.ui.VerticalLayout;

/**
 * This interface represents the minimum requirement for presenter components to
 * be used as a view in web PeptideShaker application
 *
 * @author Yehia Farag
 */
public interface ViewableFrame {    

    /**
     * Get the presenter id
     *
     * @return unique id
     */
    String getViewId();

    /**
     * hide the presenter view
     */
    void minimizeView();

    /**
     * Show the presenter view
     */
    void maximizeView();
    /**
     * update all panels data
     */
    void update();

}
