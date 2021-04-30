package com.uib.web.peptideshaker.ui.views;

import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.ui.interfaces.ViewableFrame;
import com.uib.web.peptideshaker.ui.components.items.SubViewSideButton;
import com.uib.web.peptideshaker.ui.views.subviews.DatasetProteinsSubView;
import com.uib.web.peptideshaker.ui.views.subviews.PeptidePsmsSubView;
import com.uib.web.peptideshaker.ui.views.subviews.ProteinPeptidesSubView;
import com.uib.web.peptideshaker.ui.views.subviews.UserUploadDataSubView;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;

import java.util.Iterator;

/**
 * This class represent PeptideShaker view presenter which is responsible for
 * viewing the peptide shaker results on web
 *
 * @author Yehia Mokhtar Farag
 */
public class ResultsView extends AbsoluteLayout implements ViewableFrame {

    /**
     * The main left side buttons container in big screen mode.
     */
    private VerticalLayout leftSideButtonsContainer;
    private SubViewSideButton proteinPeptidesOverviewBtn;
    private SubViewSideButton peptidePsmoverviewBtn;

    private PeptidePsmsSubView peptidePsmsSubView;
    private ProteinPeptidesSubView proteinPeptidesSubView;
    private DatasetProteinsSubView datasetProteinsSubView;

    /**
     * The view is in maximised mode.
     */
    private SubViewSideButton datasetProteinsOverviewBtn;
    private SubViewSideButton uploadOwnDataBtn;
    private boolean allJobsAreDone = false;

    private final AppManagmentBean appManagmentBean;

    /**
     * Constructor to initialise the main layout and attributes.
     *
     */
    public ResultsView() {
        ResultsView.this.setSizeFull();
        ResultsView.this.setStyleName("activelayout");
        ResultsView.this.addStyleName("hidelowerpanel");
        this.appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
        this.initLayout();
    }

    /**
     * Initialise the container layout.
     */
    private void initLayout() {
        leftSideButtonsContainer = new VerticalLayout();
        leftSideButtonsContainer.setWidth(100, Unit.PIXELS);
        leftSideButtonsContainer.setHeightUndefined();
        leftSideButtonsContainer.setSpacing(true);
        leftSideButtonsContainer.addStyleName("leftsidebuttonscontainer");
        this.addComponent(leftSideButtonsContainer, "top:120px;left:4px");

        /**
         *******left side buttons*******
         */
        LayoutEvents.LayoutClickListener listener = (LayoutEvents.LayoutClickEvent event) -> {
            String subViewId = ((SubViewSideButton) event.getComponent()).getData() + "";
            appManagmentBean.getUI_Manager().viewSubLayout(ResultsView.class.getName(), subViewId);
        };

        int buttonIndex = 1;

        uploadOwnDataBtn = new SubViewSideButton("upload-project", buttonIndex++);
        uploadOwnDataBtn.setVisible(!appManagmentBean.isSharingDatasetMode());
        uploadOwnDataBtn.setDescription("Upload project files");
        if (!appManagmentBean.isSharingDatasetMode()) {
            leftSideButtonsContainer.addComponent(uploadOwnDataBtn);
            leftSideButtonsContainer.setComponentAlignment(uploadOwnDataBtn, Alignment.TOP_CENTER);
        }
        uploadOwnDataBtn.addStyleName("uploadbigbtn");
        uploadOwnDataBtn.setData(UserUploadDataSubView.class.getName());
        uploadOwnDataBtn.addLayoutClickListener(listener);
        uploadOwnDataBtn.updateIconByHTMLCode(VaadinIcons.FILE_TEXT_O.getHtml() + "<div class='overlayicon'>" + VaadinIcons.ARROW_CIRCLE_UP_O.getHtml() + "</div>");//VaadinIcons.UPLOAD.getHtml()

        datasetProteinsOverviewBtn = new SubViewSideButton("Dataset overview", buttonIndex++) {
            @Override
            public void setId(String id) {
                Iterator<Component> itr = this.iterator();
                while (itr.hasNext()) {
                    itr.next().setId(id);
                }
                super.setId(id);
            }
        };
        datasetProteinsOverviewBtn.addStyleName("dsoverviewbtn");
        datasetProteinsOverviewBtn.setData(DatasetProteinsSubView.class.getName());
        datasetProteinsOverviewBtn.addStyleName("inactive");
        datasetProteinsOverviewBtn.setDescription("Dataset Overview");
        datasetProteinsOverviewBtn.updateIconByHTMLCode(VaadinIcons.CLUSTER.getHtml());
        datasetProteinsOverviewBtn.updateIconByResource(new ThemeResource("img/venn_color.png"));//img/vizicon.png
        leftSideButtonsContainer.addComponent(datasetProteinsOverviewBtn);
        datasetProteinsOverviewBtn.addLayoutClickListener(listener);
        leftSideButtonsContainer.setComponentAlignment(datasetProteinsOverviewBtn, Alignment.TOP_CENTER);

        proteinPeptidesOverviewBtn = new SubViewSideButton("Protein Overview", buttonIndex++);
        proteinPeptidesOverviewBtn.setDescription("Protein Overview");
        proteinPeptidesOverviewBtn.updateIconByResource(null);
        proteinPeptidesOverviewBtn.setData(ProteinPeptidesSubView.class.getName());
        proteinPeptidesOverviewBtn.addStyleName("proteinoverviewbtn");
        proteinPeptidesOverviewBtn.addLayoutClickListener(listener);
        leftSideButtonsContainer.addComponent(proteinPeptidesOverviewBtn);
        leftSideButtonsContainer.setComponentAlignment(proteinPeptidesOverviewBtn, Alignment.TOP_CENTER);
        peptidePsmoverviewBtn = new SubViewSideButton("PSM Overview", buttonIndex++);
        peptidePsmoverviewBtn.updateIconByResource(null);
        peptidePsmoverviewBtn.setDescription("Peptide Spectrum Matches");
        peptidePsmoverviewBtn.setData(PeptidePsmsSubView.class.getName());
        peptidePsmoverviewBtn.addStyleName("psmoverviewbtn");
        peptidePsmoverviewBtn.addLayoutClickListener(listener);
        leftSideButtonsContainer.addComponent(peptidePsmoverviewBtn);
        leftSideButtonsContainer.setComponentAlignment(peptidePsmoverviewBtn, Alignment.TOP_CENTER);

        /**
         ***** end left side buttons / start sub view ******
         */
        AbsoluteLayout subviewContainerFrame = new AbsoluteLayout();
        subviewContainerFrame.setSizeFull();
        subviewContainerFrame.setStyleName("integratedframe");
        this.addComponent(subviewContainerFrame, "left:100px");

        AbsoluteLayout subviewContainerContent = new AbsoluteLayout();
        subviewContainerContent.addStyleName("viewframecontent");
        subviewContainerContent.setSizeFull();
        subviewContainerFrame.addComponent(subviewContainerContent, "left:10px;right:10px;top:10px;bottom:10px;");

        UserUploadDataSubView userUploadDataSubView = new UserUploadDataSubView();
        
        if (!appManagmentBean.isSharingDatasetMode()) {
            subviewContainerContent.addComponent(userUploadDataSubView);
            appManagmentBean.getUI_Manager().registerSubView(this.getViewId(), userUploadDataSubView);
        }

        datasetProteinsSubView = new DatasetProteinsSubView();
        subviewContainerContent.addComponent(datasetProteinsSubView);
        appManagmentBean.getUI_Manager().registerSubView(this.getViewId(), datasetProteinsSubView);

        proteinPeptidesSubView = new ProteinPeptidesSubView();
        subviewContainerContent.addComponent(proteinPeptidesSubView);
        appManagmentBean.getUI_Manager().registerSubView(this.getViewId(), proteinPeptidesSubView);

        peptidePsmsSubView = new PeptidePsmsSubView();
        subviewContainerContent.addComponent(peptidePsmsSubView);
        appManagmentBean.getUI_Manager().registerSubView(this.getViewId(), peptidePsmsSubView);
        if (appManagmentBean.isSharingDatasetMode()) {
            appManagmentBean.getUI_Manager().viewSubLayout(this.getViewId(), datasetProteinsSubView.getViewId());
        } else {
            appManagmentBean.getUI_Manager().viewSubLayout(this.getViewId(), userUploadDataSubView.getViewId());
        }

    }

    /**
     * Get the current view ID
     *
     * @return view id
     */
    @Override
    public String getViewId() {
        return ResultsView.class.getName();
    }

    /**
     * Hide the main view for the current component.
     */
    @Override
    public void minimizeView() {
        this.addStyleName("hidepanel");
        this.leftSideButtonsContainer.removeStyleName("visible");

    }

    /**
     * Show the main view for the current component.
     */
    @Override
    public void maximizeView() {
        this.leftSideButtonsContainer.addStyleName("visible");
        this.removeStyleName("hidepanel");
    }

    @Override
    public void update() {
        try {
            if (appManagmentBean.getUI_Manager().isToUpdatePeptidePSm()) {
                peptidePsmoverviewBtn.updateIconByResource(new ExternalResource(appManagmentBean.getUI_Manager().getEncodedPeptideButtonImage()));
            }
            if (appManagmentBean.getUI_Manager().getSelectedDatasetId() != null) {
                datasetProteinsOverviewBtn.removeStyleName("inactive");
                if (appManagmentBean.getUI_Manager().getSelectedProteinIndex() != -1) {
                    proteinPeptidesOverviewBtn.setVisible(true);
                    proteinPeptidesOverviewBtn.updateIconByResource(new ExternalResource(appManagmentBean.getUI_Manager().getEncodedProteinButtonImage()));
                } else {
                    proteinPeptidesOverviewBtn.setVisible(false);
                }
                if (appManagmentBean.getUI_Manager().getSelectedPeptideIndex() != -1) {
                    peptidePsmoverviewBtn.setVisible(true);

                } else {
                    peptidePsmoverviewBtn.setVisible(false);
                }

            } else {
                datasetProteinsOverviewBtn.addStyleName("inactive");
                peptidePsmoverviewBtn.setVisible(false);
                proteinPeptidesOverviewBtn.setVisible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
