
package com.uib.web.peptideshaker.ui.views.subviews;

import com.uib.web.peptideshaker.ui.components.UserUploadFilesComponent;
import com.uib.web.peptideshaker.ui.abstracts.ViewableFrame;
import com.vaadin.ui.AbsoluteLayout;

/**
 *
 * @author Yehia Mokhtar Farag
 */
public class UserUploadDataSubView extends AbsoluteLayout implements ViewableFrame {

    public UserUploadDataSubView() {
        UserUploadDataSubView.this.setSizeFull();
        UserUploadFilesComponent userUploadFilesComponent = new UserUploadFilesComponent();
        userUploadFilesComponent.setSizeFull();
        UserUploadDataSubView.this.addComponent(userUploadFilesComponent);
    }

    @Override
    public String getViewId() {
        return UserUploadDataSubView.class.getName();
    }

    /**
     * Hide current presenter
     */
    @Override
    public void minimizeView() {
        this.addStyleName("hidepanel");
    }

    /**
     * View presenter
     */
    @Override
    public void maximizeView() {
        this.removeStyleName("hidepanel");
    }

    @Override
    public void update() {
    }
}
