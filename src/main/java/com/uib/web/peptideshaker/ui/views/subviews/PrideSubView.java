
package com.uib.web.peptideshaker.ui.views.subviews;

import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.model.PRIDECompactProjectModel;
import com.uib.web.peptideshaker.model.ProteinGroupObject;
import com.uib.web.peptideshaker.ui.interfaces.ViewableFrame;
import com.uib.web.peptideshaker.ui.components.SearchableTable;
import com.uib.web.peptideshaker.ui.components.items.HelpPopupButton;
import com.uib.web.peptideshaker.ui.components.items.TableColumnHeader;
import com.uib.web.peptideshaker.ui.views.modal.PopupWindow;
import com.vaadin.data.Item;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.Map;

/**
 *
 * @author Yehia Mokhtar Farag
 */
public class PrideSubView extends AbsoluteLayout implements ViewableFrame {

    private final AppManagmentBean appManagmentBean;
    private AbsoluteLayout container;
    private Label errorLabel;
    private Label resultsQueryLabel;
    private SearchableTable projectsTable;
    private int pageNumber = 0;

    public PrideSubView() {
        appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
        PrideSubView.this.setSizeFull();
        PrideSubView.this.initLayout();

    }

    private void initLayout() {
        container = new AbsoluteLayout();
        container.setSizeFull();
        PrideSubView.this.addComponent(container);
        HorizontalLayout topLabelContainer = new HorizontalLayout();
        topLabelContainer.setSizeFull();
        topLabelContainer.addStyleName("minhight30");
        container.addComponent(topLabelContainer);
        HorizontalLayout topLeftLabelContainer = new HorizontalLayout();
        topLeftLabelContainer.setWidthUndefined();
        topLeftLabelContainer.setHeight(100, Unit.PERCENTAGE);
        topLabelContainer.addComponent(topLeftLabelContainer);

        // CONFIGURING GRAPHICAL COMPONENTS...
        Label titleLabel = new Label("PRIDE Database");
        titleLabel.setStyleName("frametitle");
        titleLabel.addStyleName("maintitleheader");
        container.addComponent(titleLabel, "left:40px;top:13px");
        HelpPopupButton helpBtn = new HelpPopupButton("<h1>Search / Visualize PRIDE Projects</h1>Users can search and visualize PRIDE projects, please note that the imported projects will be automatically deleted after the session expire.", "", 400, 110);
        container.addComponent(helpBtn, "left:162px;top:3px");

        AbsoluteLayout mainContainerLayout = new AbsoluteLayout();
        mainContainerLayout.setSizeFull();
        mainContainerLayout.setStyleName("maincontentcontainer");
        container.addComponent(mainContainerLayout, "left:25px;top:25px;bottom:25px;right:25px");

        errorLabel = new Label("");
        errorLabel.setStyleName(ValoTheme.LABEL_COLORED);
        errorLabel.addStyleName(ValoTheme.LABEL_SMALL);
        errorLabel.addStyleName("error");
        errorLabel.setContentMode(ContentMode.HTML);
        mainContainerLayout.addComponent(errorLabel, "left:475px;top:65px");

        TextField searchInputTextFiles = new TextField("<b style='font-size: 15px;'>Search PRIDE database</b>");
        searchInputTextFiles.setValue("PXD015203,PXD010230");
        searchInputTextFiles.setInputPrompt("Search by project name or PRIDE project accession");
        searchInputTextFiles.setCaptionAsHtml(true);
        searchInputTextFiles.setWidth(450, Unit.PIXELS);
        searchInputTextFiles.setHeight(50, Unit.PIXELS);
        searchInputTextFiles.setRequired(false);
        searchInputTextFiles.setStyleName("searchfieldwithcaption");
        mainContainerLayout.addComponent(searchInputTextFiles, "left:15px;top:45px");
        Button searchBtn = new Button("Search");
        searchBtn.setWidth(76, Unit.PIXELS);
        searchBtn.setHeight(20, Unit.PIXELS);
        searchBtn.setStyleName(ValoTheme.BUTTON_TINY);
        Button loadMoreBtn = new Button("Load more");
        loadMoreBtn.setStyleName(ValoTheme.BUTTON_TINY);
        loadMoreBtn.setWidth(90, Unit.PIXELS);
        loadMoreBtn.setHeight(20, Unit.PIXELS);
        loadMoreBtn.setVisible(false);
        mainContainerLayout.addComponent(loadMoreBtn, "top:110px;left:200px");
        loadMoreBtn.addClickListener((Button.ClickEvent event) -> {
            pageNumber++;
            Map<String, PRIDECompactProjectModel> projects = appManagmentBean.getPRIDEUtils().searchPRIDEProjects(searchInputTextFiles.getValue(), pageNumber);
            updateProjectsTable(searchInputTextFiles.getValue(), projects, false);
            loadMoreBtn.setVisible(projects.size() == 100);

        });
        searchBtn.addClickListener((Button.ClickEvent event) -> {
            pageNumber = 0;
            loadMoreBtn.setVisible(false);
            if (searchInputTextFiles.getValue().trim().equals("")) {
                errorLabel.setValue("Not valid keyword");
                return;
            }
            Map<String, PRIDECompactProjectModel> projects = appManagmentBean.getPRIDEUtils().searchPRIDEProjects(searchInputTextFiles.getValue(), pageNumber);
            if (projects == null) {
                errorLabel.setValue("Error in connecting to PRIDE databse, try again later! " + searchInputTextFiles.getValue());
            } else if (projects.isEmpty()) {
                errorLabel.setValue("No results found for the <i>'" + searchInputTextFiles.getValue() + "'</i> keyword");
            } else {
                errorLabel.setValue("");
                updateProjectsTable(searchInputTextFiles.getValue(), projects, true);
                loadMoreBtn.setVisible(true);
            }

        });

        Button cancelBtn = new Button("Clear");
        cancelBtn.setStyleName(ValoTheme.BUTTON_TINY);
        cancelBtn.setWidth(76, Unit.PIXELS);
        cancelBtn.setHeight(20, Unit.PIXELS);
        mainContainerLayout.addComponent(searchBtn, "top:110px;left:303px");
        mainContainerLayout.addComponent(cancelBtn, "top:110px;left:389px");
        cancelBtn.addClickListener((Button.ClickEvent event) -> {
            searchInputTextFiles.clear();
            errorLabel.setValue("");
            pageNumber = 0;
            loadMoreBtn.setVisible(false);
        });
        this.resultsQueryLabel = new Label();
        resultsQueryLabel.addStyleName(ValoTheme.LABEL_H3);
        resultsQueryLabel.setContentMode(ContentMode.HTML);
//        mainContainerLayout.addComponent(resultsQueryLabel, "left:15px;top:110px");
        this.projectsTable = initProjectsTable();
        mainContainerLayout.addComponent(projectsTable, "left:15px;top:130px;right:15px;bottom:15px");

    }

    private void updateProjectsTable(String keyword, Map<String, PRIDECompactProjectModel> projects, boolean reset) {
        int index = 1;
        if (reset) {
            projectsTable.updateLabel("Search Results ( <i>" + keyword + "</i> )");
            projectsTable.resetTable();
            index = 1;
        } else {
            index = projectsTable.getMainTable().getItemIds().size() + 1;
        }
        projectsTable.deActivateValueChangeListener();
        for (PRIDECompactProjectModel project : projects.values()) {
            String searchingKeyword = "";
            Link projectAccLink = new Link(project.getAccession(), new ExternalResource("https://www.ebi.ac.uk/pride/archive/projects/" + project.getAccession().toUpperCase())) {
                @Override
                public String toString() {
                    return project.getAccession();
                }

            };
            projectAccLink.setTargetName("_blank");
            projectAccLink.setStyleName("tablelink");
            searchingKeyword += project.getAccession() + "_" + project.getTitle();
            PopupWindow infoLabel = new PopupWindow(VaadinIcons.INFO_CIRCLE_O.getHtml()) {
                @Override
                public void onClosePopup() {
                }

            };
            infoLabel.setCaptionAsHtml(true);
            infoLabel.addStyleName("centeredicon");
            infoLabel.setDescription("Information! ");
            infoLabel.setClosable(true);

            VerticalLayout labelContainer = new VerticalLayout();
            infoLabel.setContent(labelContainer);
            labelContainer.addStyleName("maxwidth90per");
            labelContainer.setWidth(660, Unit.PIXELS);
            labelContainer.setHeightUndefined();
            Label projectoverviewLabel = new Label("<h1>" + project.getTitle() + "</h1><p><b>publishing date: </b>" + project.getPublicationDate() + "</p><p><b>Description: </b>" + project.getProjectDescription() + "</p><p><b>Data Processing Protocol: </b>" + project.getDataProcessingProtocol() + "</p><p><b>Instruments: </b>" + project.getInstruments() + "</p>", ContentMode.HTML);
            projectoverviewLabel.setSizeFull();
            projectoverviewLabel.setHeightUndefined();
            projectoverviewLabel.setStyleName("uploadeddsinfo");
            projectoverviewLabel.addStyleName("longtext");
            labelContainer.addComponent(projectoverviewLabel);
            Button loadBtn = new Button("Load Dataset");

            loadBtn.setStyleName(ValoTheme.BUTTON_TINY);
            loadBtn.addStyleName("loadpridebutton");
            loadBtn.setWidth(90, Unit.PIXELS);
            loadBtn.setHeight(20, Unit.PIXELS);
            labelContainer.addComponent(loadBtn);
            loadBtn.addClickListener((Button.ClickEvent event) -> {
                Map<Integer, ProteinGroupObject> importedProteins = appManagmentBean.getPRIDEUtils().importProjectProteins(project.getAccession());
                if (importedProteins == null || (importedProteins.isEmpty())) {
                    if (importedProteins == null) {
                        appManagmentBean.getNotificationFacade().showInfoNotification("Error in connecting to PRIDE database");
                    } else {
                        appManagmentBean.getNotificationFacade().showInfoNotification("Analyzed data not available");
                    }
                    loadBtn.setVisible(false);
                    
                    String updatedValue = projectoverviewLabel.getValue() + "<p><b>Analyzed data not available</b></p>";
                    projectoverviewLabel.setValue(updatedValue);
                } else {
                    infoLabel.setPopupVisible(false);
                }

            });

            Object[] items = new Object[]{index++, projectAccLink, project.getTitle(), infoLabel};
            projectsTable.addTableItem(project.getAccession(), items, searchingKeyword);
        }
        projectsTable.updateLabelCounter(projectsTable.getMainTable().getItemIds().size());
        projectsTable.activateValueChangeListener();

    }

    private SearchableTable initProjectsTable() {
        TableColumnHeader header1 = new TableColumnHeader("index", Integer.class, null, "", null, Table.Align.RIGHT);
        TableColumnHeader header2 = new TableColumnHeader("Accession", Link.class, null, generateCaptionWithTooltio("Accession", "Project accession"), null, Table.Align.CENTER);
        TableColumnHeader header3 = new TableColumnHeader("Title", String.class, null, "Title", null, Table.Align.LEFT);
        TableColumnHeader header4 = new TableColumnHeader("Information", PopupWindow.class, null, "Information", null, Table.Align.LEFT);

        TableColumnHeader[] tableHeaders = new TableColumnHeader[]{header1, header2, header3, header4};

        SearchableTable table = new SearchableTable("Search Results", "PRIDE project accession or name", tableHeaders, false) {
            @Override
            public void itemSelected(Object itemId) {
                if (itemId == null) {
                    return;
                }

                appManagmentBean.getPRIDEUtils().importProjectProteins(itemId.toString());
//                Item item = this.getMainTable().getItem(itemId);
//                ((PopupWindow) item.getItemProperty("Information").getValue()).setPopupVisible(true);
//                getMainTable().unselect(itemId);
            }

        };
        table.setSizeFull();
        return table;

    }

    @Override
    public String getViewId() {
        return PrideSubView.class.getName();
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

    private String generateCaptionWithTooltio(String caption, String tooltip) {
        return "<div class='tooltip'>" + caption + "<span class='tooltiptext'>" + tooltip + "</span></div>";

    }

}
