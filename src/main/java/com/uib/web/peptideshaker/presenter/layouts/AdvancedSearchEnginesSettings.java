package com.uib.web.peptideshaker.presenter.layouts;

import com.compomics.util.parameters.identification.IdentificationParameters;
import com.uib.web.peptideshaker.presenter.core.PopupWindow;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchenginessettings.AndromedaAdvancedSettingsPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchenginessettings.CometAdvancedSettingsPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchenginessettings.DirecTagAdvancedSettingsPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchenginessettings.MSAmandaAdvancedSettingsPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchenginessettings.MsGFAdvancedSettingsPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchenginessettings.MyriMatchAdvancedSettingsPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchenginessettings.NovorAdvancedSettingsPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchenginessettings.OmssaAdvancedSettingsPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchenginessettings.TideAdvancedSettingsPanel;
import com.uib.web.peptideshaker.presenter.layouts.advancedsearchenginessettings.XTandemAdvancedSettingsPanel;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * This class represents advanced search settings layout that allow users to
 * modify the search input parameters
 *
 * @author Yehia Farag
 */
public class AdvancedSearchEnginesSettings extends VerticalLayout {

    private final PopupWindow popupAdvancedSettingLayout;
    private IdentificationParameters webSearchParam;
    private XTandemAdvancedSettingsPanel xTandemAdvancedSettingsPanel;
    private MyriMatchAdvancedSettingsPanel myriMatchAdvancedSettingsPanel;
    private MSAmandaAdvancedSettingsPanel mSAmandaAdvancedSettingsPanel;
    private AndromedaAdvancedSettingsPanel andromedaAdvancedSettingsPanel;
    private TideAdvancedSettingsPanel tideAdvancedSettingsPanel;
    private OmssaAdvancedSettingsPanel omssaAdvancedSettingsPanel;
    private NovorAdvancedSettingsPanel novorAdvancedSettingsPanel;
    private DirecTagAdvancedSettingsPanel direcTagAdvancedSettingsPanel;
    private MsGFAdvancedSettingsPanel msGFAdvancedSettingsPanel;
    private CometAdvancedSettingsPanel cometAdvancedSettingsPanel;

    public AdvancedSearchEnginesSettings() {
        AdvancedSearchEnginesSettings.this.setSizeFull();
        AdvancedSearchEnginesSettings.this.setStyleName("advancedsearchenglayout");
//        Label title = new Label("( Advanced Settings )");
//        AdvancedSearchEnginesSettings.this.addComponent(title);
//        AdvancedSearchEnginesSettings.this.addLayoutClickListener(AdvancedSearchEnginesSettings.this);
        popupAdvancedSettingLayout = new PopupWindow(" Search Engines Settings )") {
            @Override
            public void onClosePopup() {

            }
        };
        AdvancedSearchEnginesSettings.this.addComponent(popupAdvancedSettingLayout);
        AbsoluteLayout vlo = initLayout();
        vlo.setWidth(500, Unit.PIXELS);
        vlo.setHeight(500, Unit.PIXELS);
        popupAdvancedSettingLayout.setClosable(true);
        popupAdvancedSettingLayout.setContent(vlo);

    }

    private AbsoluteLayout initLayout() {
        AbsoluteLayout container = new AbsoluteLayout();
        container.setSizeFull();
        container.setStyleName("advsettingcontainer");
        Label title = new Label("<font style='font-size:15px;'> Search Engines Settings</font>", ContentMode.HTML);
        container.addComponent(title, "left:10px;top:10px");

        VerticalLayout subContainer = new VerticalLayout();
        subContainer.setWidth(100, Unit.PERCENTAGE);
        subContainer.setHeightUndefined();
        container.addComponent(subContainer, "left:10px;top:37px;right:10px;");
        subContainer.setMargin(false);
        subContainer.setStyleName("advsettingssubcontainer");

        xTandemAdvancedSettingsPanel = new XTandemAdvancedSettingsPanel();
        subContainer.addComponent(xTandemAdvancedSettingsPanel);

        myriMatchAdvancedSettingsPanel = new MyriMatchAdvancedSettingsPanel();
        subContainer.addComponent(myriMatchAdvancedSettingsPanel);

        mSAmandaAdvancedSettingsPanel = new MSAmandaAdvancedSettingsPanel();
        subContainer.addComponent(mSAmandaAdvancedSettingsPanel);
        msGFAdvancedSettingsPanel = new MsGFAdvancedSettingsPanel();
        subContainer.addComponent(msGFAdvancedSettingsPanel);

        omssaAdvancedSettingsPanel = new OmssaAdvancedSettingsPanel();
        subContainer.addComponent(omssaAdvancedSettingsPanel);

        cometAdvancedSettingsPanel = new CometAdvancedSettingsPanel();
        subContainer.addComponent(cometAdvancedSettingsPanel);
        tideAdvancedSettingsPanel = new TideAdvancedSettingsPanel();
        subContainer.addComponent(tideAdvancedSettingsPanel);
        andromedaAdvancedSettingsPanel = new AndromedaAdvancedSettingsPanel();
        subContainer.addComponent(andromedaAdvancedSettingsPanel);
        novorAdvancedSettingsPanel = new NovorAdvancedSettingsPanel();
        subContainer.addComponent(novorAdvancedSettingsPanel);
        direcTagAdvancedSettingsPanel = new DirecTagAdvancedSettingsPanel();
        subContainer.addComponent(direcTagAdvancedSettingsPanel);

        return container;
    }

    /**
     * Update search input forms based on user selection (add/edit) from search
     * files drop-down list
     *
     * @param searchParameters search parameter object from selected parameter
     */
    public void updateAdvancedSearchParamForms(IdentificationParameters searchParameters) {
        this.webSearchParam = searchParameters;
        IdentificationParameters idSearchParameter = webSearchParam;
        if (idSearchParameter != null) {
            xTandemAdvancedSettingsPanel.updateGUI(searchParameters);
            myriMatchAdvancedSettingsPanel.updateGUI(searchParameters);
            mSAmandaAdvancedSettingsPanel.updateGUI(searchParameters);
            msGFAdvancedSettingsPanel.updateGUI(searchParameters);
            novorAdvancedSettingsPanel.updateGUI(searchParameters);
            andromedaAdvancedSettingsPanel.updateGUI(searchParameters);
            tideAdvancedSettingsPanel.updateGUI(searchParameters);
            omssaAdvancedSettingsPanel.updateGUI(searchParameters);
            direcTagAdvancedSettingsPanel.updateGUI(searchParameters);
            cometAdvancedSettingsPanel.updateGUI(searchParameters);
        }

    }

}
