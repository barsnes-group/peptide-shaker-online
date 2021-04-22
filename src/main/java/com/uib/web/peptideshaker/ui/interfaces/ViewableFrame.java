package com.uib.web.peptideshaker.ui.interfaces;

/**
 * This interface represents the minimum requirement for presenter components to
 * be used as a view in web PeptideShaker application
 *
 * @author Yehia Mokhtar Farag
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
