package com.uib.web.peptideshaker.ui.components;

import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.ui.components.items.SmallSideBtn;
import com.uib.web.peptideshaker.ui.views.FileSystemView;
import com.uib.web.peptideshaker.ui.views.ResultsView;
import com.uib.web.peptideshaker.ui.views.WorkflowInvokingView;
import com.uib.web.peptideshaker.ui.views.WelcomePageView;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbsoluteLayout;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents container for view action buttons
 *
 * @author Yehia Mokhtar Farag
 */
public class ViewActionButtonsComponent extends AbsoluteLayout implements LayoutEvents.LayoutClickListener {

    private final Map<String, SmallSideBtn> buttonsMap;
    private AppManagmentBean appManagmentBean;
    private final SmallSideBtn searchGUIPSWorkflowViewButton;

    public ViewActionButtonsComponent() {

        this.buttonsMap = new HashMap<>();
        ViewActionButtonsComponent.this.setWidth(102, Unit.PIXELS);
        ViewActionButtonsComponent.this.setHeight(100, Unit.PIXELS);
        ViewActionButtonsComponent.this.setStyleName("viewbuttonsactioncontainer");
        searchGUIPSWorkflowViewButton = new SmallSideBtn("img/searchguiblue.png");//spectra2.pngimg/searchgui-medium-shadow-2.png
        searchGUIPSWorkflowViewButton.setData(WorkflowInvokingView.class.getName());
        searchGUIPSWorkflowViewButton.setDescription("Search and process data (SearchGUI and PeptideShaker)");
        searchGUIPSWorkflowViewButton.addStyleName("smalltoolsbtn");
        searchGUIPSWorkflowViewButton.addStyleName("searchguiicon");
        searchGUIPSWorkflowViewButton.setWidth(35, Unit.PIXELS);
        searchGUIPSWorkflowViewButton.setHeight(35, Unit.PIXELS);
        ViewActionButtonsComponent.this.addComponent(searchGUIPSWorkflowViewButton, "left:9px;");
        searchGUIPSWorkflowViewButton.addLayoutClickListener(ViewActionButtonsComponent.this);
        buttonsMap.put(WorkflowInvokingView.class.getName(), searchGUIPSWorkflowViewButton);

        SmallSideBtn fileSystemViewButton = new SmallSideBtn("img/globeearthanimation.png");//VaadinIcons.GLOBE
        fileSystemViewButton.setData(FileSystemView.class.getName());
        fileSystemViewButton.addStyleName("glubimg");
        fileSystemViewButton.setDescription("Projects and available data files");
        fileSystemViewButton.addStyleName("dataoverviewsmallbtn");
        fileSystemViewButton.setWidth(35, Unit.PIXELS);
        fileSystemViewButton.setHeight(35, Unit.PIXELS);
        ViewActionButtonsComponent.this.addComponent(fileSystemViewButton, "left:9px;top:53px;");
        fileSystemViewButton.addLayoutClickListener(ViewActionButtonsComponent.this);
        buttonsMap.put(FileSystemView.class.getName(), fileSystemViewButton);

        SmallSideBtn welcomePageViewButton = new SmallSideBtn(VaadinIcons.HOME_O);
        welcomePageViewButton.addStyleName("homepagepresenterbtn");
        welcomePageViewButton.setData(WelcomePageView.class.getName());
        welcomePageViewButton.setDescription("Home page");
        welcomePageViewButton.setWidth(35, Unit.PIXELS);
        welcomePageViewButton.setHeight(35, Unit.PIXELS);
        ViewActionButtonsComponent.this.addComponent(welcomePageViewButton, "left:53px;top:53px;");
        welcomePageViewButton.addLayoutClickListener(ViewActionButtonsComponent.this);
        buttonsMap.put(WelcomePageView.class.getName(), welcomePageViewButton);

        SmallSideBtn ResultsViewButton = new SmallSideBtn(VaadinIcons.CLUSTER);
        ResultsViewButton.updateIconSourceURL("img/venn_color.png");
        ResultsViewButton.setDescription("Visualize selected projects / Upload your own project files");
        ResultsViewButton.setData(ResultsView.class.getName());
        ResultsViewButton.addStyleName("resultsmallbtn");
        ResultsViewButton.setWidth(35, Unit.PIXELS);
        ResultsViewButton.setHeight(35, Unit.PIXELS);
        ViewActionButtonsComponent.this.addComponent(ResultsViewButton, "left:53px;top:9px;");
        ResultsViewButton.addLayoutClickListener(ViewActionButtonsComponent.this);
        buttonsMap.put(ResultsView.class.getName(), ResultsViewButton);
    }

    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        String buttonId = ((SmallSideBtn) event.getComponent()).getData() + "";
        if (appManagmentBean == null) {
            this.appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
            if (!appManagmentBean.isAvailableGalaxy()) {
                searchGUIPSWorkflowViewButton.setEnabled(false);
                if(buttonId.equals(WorkflowInvokingView.class.getName()))
                    return;
            }
        }

        appManagmentBean.getUI_Manager().viewLayout(buttonId);
    }
}
