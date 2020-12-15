package com.uib.web.peptideshaker.uimanager;

import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.ui.abstracts.ViewableFrame;
import com.uib.web.peptideshaker.ui.components.ViewActionButtonsComponent;
import com.vaadin.event.LayoutEvents;
import com.vaadin.server.VaadinSession;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class represents the main layout of the application and main view
 * controller manager
 *
 * @author Yehia Farag
 */
public class UIManager implements LayoutEvents.LayoutClickListener {

    private String viewedDatasetId;
    /**
     * Top layout container.
     */
//    private final HorizontalLayout topLayoutContainer;
//    /**
//     * Left layout container is the main layout container that contain the main
//     * views.
//     */
//    private final AbsoluteLayout subViewButtonsActionContainer;
//    /**
//     * Left layout container is the main layout container that contain the main
//     * views.
//     */
//    private final AbsoluteLayout topMiddleLayoutContainer;
//
//    /**
//     * Presenter buttons container layout container is layout container that
//     * contain the small presenter control buttons.
//     */
//    private final AbsoluteLayout presenterButtonsContainerLayout;
//    /**
//     * Presenter buttons layout container contains the presenter control buttons
//     * layout.
//     */
//    private final AbsoluteLayout subPresenterButtonsContainer;
    /**
     * Map of current registered views.
     */
    private final Map<String, ViewableFrame> visualizationMap = new LinkedHashMap<>();
//    private final Map<String, SmallSideBtn> presenterBtnsMap = new LinkedHashMap<>();
//    private int x = 0;
//    private int y = 0;

    private LayoutEvents.LayoutClickEvent lastEvent;
    private ViewActionButtonsComponent viewActionButtonComponent;

    public void setViewActionButtonComponent(ViewActionButtonsComponent viewActionButtonComponent) {
        this.viewActionButtonComponent = viewActionButtonComponent;
    }

    /**
     * Constructor to initialise UI Manager
     */
    public UIManager() {
        AppManagmentBean appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
        appManagmentBean.setUI_Manager(UIManager.this);

//        this.subViewButtonsActionContainer = subViewButtonsActionContainer;
//        this.topMiddleLayoutContainer = topMiddleLayoutContainer;
//        this.presenterButtonsContainerLayout = presenterButtonsContainerLayout;
//        this.subPresenterButtonsContainer = subPresenterButtonsContainer;
    }

    /**
     * Register view into the view management system.
     *
     * @param view visualisation layout.
     */
    public void registerView(ViewableFrame view) {
//        if (visualizationMap.containsKey(view.getViewId())) {
//            presenterBtnsMap.remove(view.getViewId());
////            ViewableFrame tview = visualizationMap.get(view.getViewId());
////            AbstractOrderedLayout cBtn = tview.getMainPresenterButton();
////            cBtn.removeLayoutClickListener(UIManager.this);
////            subViewButtonsActionContainer.removeComponent(tview.getSubViewButtonsActionContainerLayout());
////            topMiddleLayoutContainer.removeComponent(tview.getMainView());
////            y = presenterButtonsContainerLayout.getPosition(cBtn).getTopValue().intValue();
////            x = presenterButtonsContainerLayout.getPosition(cBtn).getLeftValue().intValue();
////            presenterButtonsContainerLayout.removeComponent(cBtn);
//
//        }

        visualizationMap.put(view.getViewId(), view);
//        presenterBtnsMap.put(view.getViewId(), view.getSmallPresenterControlButton());

//        subViewButtonsActionContainer.addComponent(view.getSubViewButtonsActionContainerLayout());
//        topMiddleLayoutContainer.addComponent(view.getMainView());
//        view.getSmallPresenterControlButton().addLayoutClickListener(UIManager.this);
//        if (!view.getViewId().equalsIgnoreCase("com.uib.web.peptideshaker.presenter.WelcomePagePresenter")) {
//            view.getMainPresenterButton().addLayoutClickListener(UIManager.this);
//            presenterButtonsContainerLayout.addComponent(view.getMainPresenterButton(), "left:" + x + "%;top:" + y + "%;");
//            y += 50;
//            if (y == 100) {
//                y = 0;
//                x += 50;
//            }
//
//        } else {
//            view.getSmallPresenterControlButton().addLayoutClickListener(UIManager.this);
//        }
//        reOrganizePresenterButtons();
    }

    /**
     * View only selected view and hide the rest of registered layout
     *
     * @param viewId selected view id
     */
    public void viewLayout(String viewId) {
        visualizationMap.values().forEach((view) -> {
            if (viewId.equalsIgnoreCase(view.getViewId())) {
                view.maximizeView();
            } else {
                view.minimizeView();
            }
        });
        viewActionButtonComponent.viewLayout(viewId);

    }

    /**
     * On click on the side button view the selected layout
     *
     * @param event action on side buttons
     */
    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        lastEvent = event;
//        Component com = event.getComponent();
//        String selectedBtnData;
//        if (com == null) {
//            return;
//        }
//        if (com instanceof SmallSideBtn) {
//            selectedBtnData = ((SmallSideBtn) com).getData().toString();
//        } else {
//            selectedBtnData = ((ButtonWithLabel) com).getData().toString();
//        }
//        if (selectedBtnData.equalsIgnoreCase("controlBtnsAction")) {
//            return;
//        }
//        this.viewLayout(selectedBtnData);
    }

//    public Map<String, SmallSideBtn> getPresenterBtnsMap() {
//        return presenterBtnsMap;
//    }
    private void reOrganizePresenterButtons() {
//        if (presenterBtnsMap.size() < 4) {
//            return;
//        }
//        int l = 0;
//        int t = 0;
//        subPresenterButtonsContainer.removeAllComponents();
//        for (SmallSideBtn btn : presenterBtnsMap.values()) {
//            subPresenterButtonsContainer.addComponent(btn, "left:" + l + "px; top:" + t + "px;");
//            l = l + 45;
//            if (l > 50) {
//                l = 0;
//                t = 45;
//            }
//        }
    }

    public void updateAll() {
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
                visualizationMap.values().forEach((view) -> {
                    view.update();
                });
//            }
//        });
//        t.start();

    }

    public String getViewedDatasetId() {
        return viewedDatasetId;
    }

    public void setViewedDatasetId(String viewedDatasetId) {
        this.viewedDatasetId = viewedDatasetId;
    }

}
