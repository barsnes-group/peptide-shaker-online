package com.uib.web.peptideshaker.ui.components;

import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.model.GalaxyFileModel;
import com.uib.web.peptideshaker.model.VisualizationDatasetModel;
import com.uib.web.peptideshaker.ui.views.modal.PopupWindow;
import com.uib.web.peptideshaker.ui.components.items.ActionLabel;
import com.uib.web.peptideshaker.ui.components.items.StatusLabel;
import com.uib.web.peptideshaker.utils.URLUtils;
import com.vaadin.event.LayoutEvents;
import com.vaadin.server.Page;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.VaadinSession;

import java.util.LinkedHashMap;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents the data view layout (equal to history in galaxy) the
 * class allows users to get an over view of their files on galaxy and allow
 * users to delete the files and datasets.
 *
 * @author Yehia Farag
 */
public class FilesTablePanel extends Panel {

    private final Panel topPanelLayout;
    private final VerticalLayout topDataTable;
    private final Panel bottomPanelLayout;
    private final VerticalLayout bottomDataTable;
    private final URLUtils urlUtils;
    private final float[] expandingRatio = new float[]{5f, 31f, 8f, 8f, 8f, 8f, 8f, 8f, 8f, 8f};
    private final AbsoluteLayout panelsContainers;
    private final Map<String, ActionLabel> datasetLabelSet;
    private final String html_Img = VaadinIcons.EYE.getHtml();
    private Component nameLabel;
    private AppManagmentBean appManagmentBean;

    /**
     * Constructor to initialise the main layout and attributes.
     */
    public FilesTablePanel() {

        FilesTablePanel.this.setWidth(100, Unit.PERCENTAGE);
        FilesTablePanel.this.setHeight(100, Unit.PERCENTAGE);
        FilesTablePanel.this.setStyleName("integratedframe");
        datasetLabelSet = new LinkedHashMap<>();

        this.urlUtils = new URLUtils();
        panelsContainers = new AbsoluteLayout();
        panelsContainers.setWidth(100, Unit.PERCENTAGE);
        panelsContainers.setHeight(100, Unit.PERCENTAGE);
        FilesTablePanel.this.setContent(panelsContainers);
        topPanelLayout = new Panel();
        topPanelLayout.setStyleName(ValoTheme.PANEL_BORDERLESS);
        topPanelLayout.setWidth(100, Unit.PERCENTAGE);
        topPanelLayout.setHeight(100, Unit.PERCENTAGE);
        topPanelLayout.addStyleName("maxheight50per");
        panelsContainers.addComponent(topPanelLayout, "top:-10px;left:10px;right:10px;bottom:50%;");
        topDataTable = new VerticalLayout();
        topDataTable.setWidth(100, Unit.PERCENTAGE);
        topDataTable.setHeightUndefined();
        topDataTable.setSpacing(true);
        topPanelLayout.setContent(topDataTable);

        Label inputFilesLabel = new Label("<font style='color: rgb(71, 71, 71); font-size: 14px;font-weight: 400; line-height:40px !important;'>Input Files</font>", ContentMode.HTML);
        panelsContainers.addComponent(inputFilesLabel, "top:50%;left:10px;right:10px;bottom:50%;");
        bottomPanelLayout = new Panel();
        bottomPanelLayout.setStyleName(ValoTheme.PANEL_BORDERLESS);
        bottomPanelLayout.setWidth(100, Unit.PERCENTAGE);
        bottomPanelLayout.setHeight(100, Unit.PERCENTAGE);
        bottomPanelLayout.addStyleName("maxheight50per");
        panelsContainers.addComponent(bottomPanelLayout, "top: 50%;left: 10px;right: 10px;bottom: 10px;");
        bottomDataTable = new VerticalLayout();
        bottomDataTable.setWidth(100, Unit.PERCENTAGE);
        bottomDataTable.setHeightUndefined();
        bottomDataTable.setSpacing(true);
        bottomPanelLayout.setContent(bottomDataTable);
        initHeaders();

    }

    private void initHeaders() {
        Label headerName = new Label("Name");
        Label headerType = new Label("Type");
        Label headerStatus = new Label("Valid");
        headerStatus.addStyleName("textalignmiddle");

        Label headerView = new Label("Information");
        headerView.addStyleName("textalignmiddle");

        Label headerShare = new Label("Share");
        headerShare.addStyleName("textalignmiddle");

        Label headerDownload = new Label("Download");
        headerDownload.addStyleName("textalignmiddle");
        Label headerDelete = new Label("Delete");
        headerDelete.addStyleName("textalignmiddle");
        HorizontalLayout headerRow = initializeRowData(new Component[]{new Label(""), headerName, headerType, headerView, headerShare, headerDownload, headerDelete, headerStatus}, true);
        headerRow.addStyleName("panelTableHeaders");
        panelsContainers.addComponent(headerRow, "top:-10px;left:10px;right:10px;bottom:50%;");
        headerName = new Label("Name");
        headerType = new Label("Type");
        headerStatus = new Label("Valid");
        headerStatus.addStyleName("textalignmiddle");
        headerView = new Label("Information");
        headerView.addStyleName("textalignmiddle");
        headerShare = new Label("Share");
        headerShare.addStyleName("textalignmiddle");

        headerDownload = new Label("Download");
        headerDownload.addStyleName("textalignmiddle");
        headerDelete = new Label("Delete");
        headerDelete.addStyleName("textalignmiddle");

        HorizontalLayout headerRow2 = initializeRowData(new Component[]{new Label(""), headerName, headerType, headerView, headerShare, headerDownload, headerDelete, headerStatus}, true);
        headerRow2.addStyleName("panelTableHeaders");
        panelsContainers.addComponent(headerRow2, "top:50%;left:10px;right:10px;bottom:50%;");

    }

    public void updateDatasetsTable() {
        if (appManagmentBean == null) {
            this.appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
        }

        datasetLabelSet.clear();
        topDataTable.removeAllComponents();
        bottomDataTable.removeAllComponents();
        Map<String, VisualizationDatasetModel> datasetMap = appManagmentBean.getUserHandler().getDatasetMap();
        List<GalaxyFileModel> filesMap = appManagmentBean.getUserHandler().getFilesToViewList();
        System.out.println("at file to view " + filesMap.size());
        int i = 1;
        int ii = 1;
        for (VisualizationDatasetModel dataset : datasetMap.values()) {

            StatusLabel statusLabel = new StatusLabel();
            statusLabel.setStatus(dataset.getStatus());
            if (statusLabel.getStatus().equals(CONSTANT.ERROR_STATUS)) {
                statusLabel.setStatus("Some files are missings or corrupted");
            }

            ActionLabel downloadLabel = new ActionLabel(VaadinIcons.DOWNLOAD_ALT, "Download File") {
                @Override
                public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                    Page.getCurrent().open(dataset.getDownloadUrl() + "to_ext=" + CONSTANT.ZIP_FILE_EXTENSION, "download='file'", true);
                }
            };
            ActionLabel deleteLabel = new ActionLabel(VaadinIcons.TRASH, "Delete Dataset") {
                @Override
                public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                    appManagmentBean.getNotificationFacade().confirmAlertNotification(VaadinIcons.TRASH.getHtml()+" Are you sure you want to delete the dataset?", (Button.ClickEvent event1) -> {
                        appManagmentBean.getUserHandler().deleteDataset(dataset);
                    });
                }

            };
            if (dataset.isUploadedDataset()) {
                String dsName = dataset.getName();
                nameLabel = new ActionLabel(VaadinIcons.CLUSTER, dsName, "Uploaded Project results ") {
                    @Override
                    public void layoutClick(LayoutEvents.LayoutClickEvent event) {

                    }

                };
                nameLabel.addStyleName("bluecolor");
                nameLabel.addStyleName("orangecolor");
                datasetLabelSet.put(dataset.getId(), (ActionLabel) nameLabel);
//
                PopupWindow infoLabel = new PopupWindow("   ") {
                    @Override
                    public void onClosePopup() {
                    }
                };
                infoLabel.setIcon(VaadinIcons.INFO_CIRCLE_O);
                VerticalLayout labelContainer = new VerticalLayout();
                labelContainer.addStyleName("maxwidth90per");
                labelContainer.setWidthUndefined();
                labelContainer.setHeight(260, Unit.PIXELS);
                Label l = new Label("<h1>Uploaded Project</h1><p>Project:      " + dataset.getName() + "</p><p>Upload time: " + dataset.getCreatedTime() + "</p><p>FASTA:       " + dataset.getFastaFileName() + "</p><p>Proteins:    " + dataset.getProteinFileName() + "</p><p>Peptides:    " + dataset.getPeptideFileName() + "</p>", ContentMode.HTML);
                l.setSizeFull();
                l.setStyleName("uploadeddsinfo");
                labelContainer.addComponent(l);
                infoLabel.addStyleName("centeredicon");
                infoLabel.setContent(labelContainer);
                infoLabel.setDescription("View search settings ");
                infoLabel.setClosable(true);

                String quant = "";
                String quantTooltip = "";
                if (dataset.getDatasetType().equals(CONSTANT.QUANT_DATASET)) {
                    quant = "<font>Quant</font>";
                }
                Label type = new Label(VaadinIcons.FILE_TEXT_O.getHtml() + "<div class='overlayicon'>" + VaadinIcons.ARROW_CIRCLE_UP_O.getHtml() + "</div>" + quant, ContentMode.HTML);

                type.setDescription(dataset.getDatasetType() + " " + quantTooltip);
                type.setStyleName("smalliconlabel");
                type.addStyleName("datatypeicon");
                ClipboardComponent shareLabel = new ClipboardComponent("");
                shareLabel.setEnabled(false);
                downloadLabel.setEnabled(false);
                HorizontalLayout rowLayout = initializeRowData(new Component[]{new Label(i + ""), nameLabel, type, infoLabel, shareLabel, downloadLabel, deleteLabel, statusLabel}, false);
                topDataTable.addComponent(rowLayout);
            } else {

                nameLabel = new ActionLabel(VaadinIcons.CLUSTER, dataset.getName(), "PeptideShaker results ") {
                    @Override
                    public void layoutClick(LayoutEvents.LayoutClickEvent event) {

                    }

                };
                nameLabel.addStyleName("bluecolor");
                nameLabel.addStyleName("orangecolor");
                datasetLabelSet.put(dataset.getId(), (ActionLabel) nameLabel);
                PopupWindow infoLabel = new PopupWindow("   ") {
                    @Override
                    public void onClosePopup() {
                    }

                };
                infoLabel.setIcon(VaadinIcons.INFO_CIRCLE_O);
                infoLabel.addStyleName("centeredicon");
                if (dataset.getStatus().equals(CONSTANT.OK_STATUS)) {
                    FileOverviewLayout fileOverview = new FileOverviewLayout(dataset) {
                        private final PopupWindow tFileOverview = (PopupWindow) infoLabel;

                        @Override
                        public void close() {
                            ((PopupWindow) tFileOverview).setPopupVisible(false);
                        }

                    };
                    infoLabel.setContent(fileOverview);
                }
                infoLabel.setDescription("Information! ");
                infoLabel.setClosable(false);
                if (statusLabel.getStatus().equals(CONSTANT.ERROR)) {
                    statusLabel.setStatus("Some files are missings or corrupted please re-run SearchGUI-PeptideShaker-WorkFlow");
                }//
                String link = "";//((PeptideShakerVisualizationDataset) dataset).getLinkToShare();
                int dsKey = -1;
//                if (link != null) {
//                    dsKey = insertDatsetLinkToShare(urlUtils.encrypt(link), urlUtils.encrypt(dataset.getName() + "-_-" + dataset.getPsZipFile().getId()));
//                    String appName = VaadinSession.getCurrent().getAttribute("appName") + "";
//                    String url = Page.getCurrent().getLocation().toString().split(appName)[0] + appName + "/";
//                    String encryptedDsKey = urlUtils.encrypt(dsKey + "");
//                    link = url + "toShare;" + encryptedDsKey;

//                }
                ClipboardComponent shareLabel = new ClipboardComponent(link);
                shareLabel.setReadOnly(dsKey != -1);
                infoLabel.addStyleName("centeredicon");
                String quant = null;
                String quantTooltip = "";
                if (dataset.getDatasetType().equals(CONSTANT.QUANT_DATASET)) {
                    quant = "Quant";
                }
                Label type = new Label(quant);
                type.setIcon(new ThemeResource("img/psiconHRNS.png"));
                type.setDescription(dataset.getDatasetType());
                type.setStyleName("smalliconlabel");
                HorizontalLayout rowLayout = initializeRowData(new Component[]{new Label(i + ""), nameLabel, type, infoLabel, shareLabel, downloadLabel, deleteLabel, statusLabel}, false);
                topDataTable.addComponent(rowLayout);
            }
            i++;
//
        }
        i = 1;
        for (GalaxyFileModel file : filesMap) {
            StatusLabel statusLabel = new StatusLabel();
            statusLabel.setStatus(file.getStatus());
            ActionLabel downloadLabel = new ActionLabel(VaadinIcons.DOWNLOAD_ALT, "Download File") {
                @Override
                public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                    Page.getCurrent().open(file.getDownloadUrl() + "to_ext=" + CONSTANT.ZIP_FILE_EXTENSION, "download='file'", true);
                }
            };
            ActionLabel deleteLabel = new ActionLabel(VaadinIcons.TRASH, "Delete File") {
                @Override
                public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                    appManagmentBean.getNotificationFacade().confirmAlertNotification(VaadinIcons.TRASH.getHtml()+" Are you sure you want to delete the file?", (Button.ClickEvent event1) -> {
                        appManagmentBean.getUserHandler().deleteFile(file);
                    });
                }

            };
            PopupWindow infoLabel = new PopupWindow("   ") {
                @Override
                public void onClosePopup() {
                }

            };
            infoLabel.setIcon(VaadinIcons.INFO_CIRCLE_O);
            infoLabel.addStyleName("centeredicon");
            FileOverviewLayout fileOverview = new FileOverviewLayout(file) {
                private final PopupWindow tFileOverview = (PopupWindow) infoLabel;

                @Override
                public void close() {
                    ((PopupWindow) tFileOverview).setPopupVisible(false);
                }

            };
            infoLabel.setContent(fileOverview);
            infoLabel.setDescription("Information! ");

            nameLabel = new Label(file.getName());

            Label type = new Label();

            type.setValue("<b>" + file.getExtension() + "</b>");

            type.setContentMode(ContentMode.HTML);
            type.setDescription(file.getExtension());
            ActionLabel shareLabel = new ActionLabel(VaadinIcons.LINK, "Share as link") {
                @Override
                public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                }

            };
            shareLabel.setEnabled(false);
            HorizontalLayout rowLayout = initializeRowData(new Component[]{new Label(ii++ + ""), nameLabel, type, infoLabel, shareLabel, downloadLabel, deleteLabel, statusLabel}, false);
            bottomDataTable.addComponent(rowLayout);

            if (statusLabel.getStatus().equals(CONSTANT.ERROR_STATUS)) {
                rowLayout.setEnabled(false);
            } else if (statusLabel.getStatus().equals(CONSTANT.RUNNING_STATUS)) {
                rowLayout.getComponent(0).setEnabled(false);
                rowLayout.getComponent(1).setEnabled(false);
                rowLayout.getComponent(2).setEnabled(false);
                rowLayout.getComponent(3).setEnabled(false);
                rowLayout.getComponent(4).setEnabled(false);
                rowLayout.getComponent(5).setEnabled(false);
                rowLayout.getComponent(6).setEnabled(true);
                rowLayout.getComponent(7).setEnabled(true);
            }
            rowLayout.setData(file.getId());
            i++;
        }

    }

    private HorizontalLayout initializeRowData(Component[] data, boolean header) {
        HorizontalLayout row = new HorizontalLayout();
        row.setSpacing(true);
        int i = 0;
        for (Component component : data) {
            component.addStyleName(ValoTheme.LABEL_NO_MARGIN);
            row.addComponent(component);
            row.setComponentAlignment(component, Alignment.MIDDLE_CENTER);
            row.setExpandRatio(component, expandingRatio[i]);
            i++;
        }
        row.setWidth(100, Unit.PERCENTAGE);
        row.setHeight(30, Unit.PIXELS);
        row.setStyleName("row");
        if (header) {
            row.addStyleName("header");
        }

        return row;
    }

    public void updateViewDataset() {
        if (appManagmentBean == null) {
            this.appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
        }
        datasetLabelSet.values().forEach((dsNameLabel) -> {
            dsNameLabel.updateLabelTitle(dsNameLabel.getLabelValue().replace(html_Img, ""));
        });
        if (appManagmentBean.getUI_Manager().getViewedDatasetId() != null) {
            ActionLabel dsNameLabel = datasetLabelSet.get(appManagmentBean.getUI_Manager().getViewedDatasetId());
            dsNameLabel.updateLabelTitle(dsNameLabel.getLabelValue() + html_Img);
        }

    }

}
