package com.uib.web.peptideshaker.presenter;

import com.uib.web.peptideshaker.presenter.core.ButtonWithLabel;
import com.uib.web.peptideshaker.presenter.core.SmallSideBtn;
import com.vaadin.ui.VerticalLayout;

/**
 * This interface represents the minimum requirement for presenter components to
 * be used as a view in web PeptideShaker application
 *
 * @author Yehia Farag
 */
public interface ViewableFrame {

    /**
     * Get Sub view action buttons container
     *
     * @return layout
     */
    VerticalLayout getSubViewButtonsActionContainerLayout();

    /**
     * Get main view container
     *
     * @return layout
     */
    VerticalLayout getMainView();

    /**
     * Get side button (top right button)
     *
     * @return button
     */
    SmallSideBtn getSmallPresenterControlButton();

    /**
     * Get main action button (in welcome page)
     *
     * @return main button with label
     */
    ButtonWithLabel getMainPresenterButton();

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

}
