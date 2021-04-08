
package com.uib.web.peptideshaker.ui.components;

import com.uib.web.peptideshaker.model.GalaxyFileModel;
import com.uib.web.peptideshaker.model.VisualizationDatasetModel;
import com.uib.web.peptideshaker.ui.components.items.Horizontal2Label;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;


/**
 * @author Yehia Mokhtar Farag
 */
public abstract class FileOverviewLayout extends VerticalLayout {

    /**
     * Constructor to initialise the main setting parameters
     *
     * @param file
     */
    public FileOverviewLayout(GalaxyFileModel file) {
        FileOverviewLayout.this.setMargin(true);
        FileOverviewLayout.this.setWidth(300, Unit.PIXELS);
        FileOverviewLayout.this.setHeightUndefined();
        FileOverviewLayout.this.setSpacing(true);
        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setSizeFull();
        titleLayout.addStyleName("subpanelframe");

        FileOverviewLayout.this.addComponent(titleLayout);
        Label projectNameLabel = new Label(file.getName());
        projectNameLabel.addStyleName(ValoTheme.LABEL_BOLD);
        titleLayout.setWidth(100, Unit.PERCENTAGE);
        titleLayout.addComponent(projectNameLabel);
        titleLayout.setComponentAlignment(projectNameLabel, Alignment.TOP_CENTER);

        Button closeIconBtn = new Button("Close");
        closeIconBtn.setIcon(VaadinIcons.CLOSE_SMALL, "Close window");
        closeIconBtn.setStyleName(ValoTheme.BUTTON_SMALL);
        closeIconBtn.addStyleName(ValoTheme.BUTTON_TINY);
        closeIconBtn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        closeIconBtn.addStyleName("centerbackground");
        closeIconBtn.setHeight(25, Unit.PIXELS);
        closeIconBtn.setWidth(25, Unit.PIXELS);

        closeIconBtn.addClickListener((Button.ClickEvent event) -> {
            close();
        });
        titleLayout.addComponent(closeIconBtn);
        titleLayout.setComponentAlignment(closeIconBtn, Alignment.TOP_RIGHT);
        VerticalLayout upperPanel = new VerticalLayout();
        upperPanel.setWidth(100, Unit.PERCENTAGE);
        upperPanel.addStyleName("subpanelframe");
        FileOverviewLayout.this.addComponent(upperPanel);

        Horizontal2Label createdLabel = new Horizontal2Label("Created :", file.getCreatedDate());
        upperPanel.addComponent(createdLabel);
        Horizontal2Label sizeLabel = new Horizontal2Label("State    :", file.getStatus() + "");
        upperPanel.addComponent(sizeLabel);
        Horizontal2Label formatLabel = new Horizontal2Label("Format  :", file.getExtension());
        upperPanel.addComponent(formatLabel);

    }

    /**
     * Constructor to initialise the main setting parameters
     *
     * @param ds
     */
    public FileOverviewLayout(VisualizationDatasetModel ds) {
        FileOverviewLayout.this.setMargin(true);
        FileOverviewLayout.this.setWidth(300, Unit.PIXELS);
        FileOverviewLayout.this.setHeightUndefined();
        FileOverviewLayout.this.setSpacing(true);
        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setSizeFull();
        titleLayout.addStyleName("subpanelframe");

        FileOverviewLayout.this.addComponent(titleLayout);
        Label projectNameLabel = new Label(ds.getName());
        projectNameLabel.addStyleName(ValoTheme.LABEL_BOLD);
        titleLayout.setWidth(100, Unit.PERCENTAGE);
        titleLayout.addComponent(projectNameLabel);
        titleLayout.setComponentAlignment(projectNameLabel, Alignment.TOP_CENTER);

        Button closeIconBtn = new Button("Close");
        closeIconBtn.setIcon(VaadinIcons.CLOSE_SMALL, "Close window");
        closeIconBtn.setStyleName(ValoTheme.BUTTON_SMALL);
        closeIconBtn.addStyleName(ValoTheme.BUTTON_TINY);
        closeIconBtn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        closeIconBtn.addStyleName("centerbackground");
        closeIconBtn.setHeight(25, Unit.PIXELS);
        closeIconBtn.setWidth(25, Unit.PIXELS);

        closeIconBtn.addClickListener((Button.ClickEvent event) -> {
            close();
        });
        titleLayout.addComponent(closeIconBtn);
        titleLayout.setComponentAlignment(closeIconBtn, Alignment.TOP_RIGHT);
        VerticalLayout upperPanel = new VerticalLayout();
        upperPanel.setWidth(100, Unit.PERCENTAGE);
        upperPanel.addStyleName("subpanelframe");
        FileOverviewLayout.this.addComponent(upperPanel);
        Horizontal2Label createdLabel = new Horizontal2Label("Created :", ds.getPsZipFile().getCreatedDate());
        upperPanel.addComponent(createdLabel);
        Horizontal2Label projectTypeLabel = new Horizontal2Label("Type  :", ds.getDatasetTypeString());
        upperPanel.addComponent(projectTypeLabel);
        Horizontal2Label fastaFile = new Horizontal2Label("FASTA  :", ds.getFastaFileName());
        upperPanel.addComponent(fastaFile);
        Horizontal2Label searchEngines = new Horizontal2Label("Search Engines    :", ds.getSearchEngines() + "");
        upperPanel.addComponent(searchEngines);
        Horizontal2Label variableModifications = new Horizontal2Label("Variable Modifications    :", ds.getVariableModification().toString().replace("[","").replace("]","") + "");
        upperPanel.addComponent(variableModifications);
        Horizontal2Label fixedModifications = new Horizontal2Label("Fixed Modifications    :", ds.getFixedModification().toString().replace("[","").replace("]","") + "");
        upperPanel.addComponent(fixedModifications);

    }

    /**
     *
     */
    public abstract void close();

}
