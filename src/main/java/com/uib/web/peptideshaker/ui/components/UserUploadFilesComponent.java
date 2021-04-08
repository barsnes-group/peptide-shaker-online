package com.uib.web.peptideshaker.ui.components;

import com.uib.web.peptideshaker.AppManagmentBean;
import com.uib.web.peptideshaker.model.CONSTANT;
import com.uib.web.peptideshaker.ui.components.items.HelpPopupButton;
import com.uib.web.peptideshaker.ui.components.items.StatusProgressLabel;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import pl.exsio.plupload.PluploadFile;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class represents the layout that contains PeptideShaker datasets
 * overview
 *
 * @author Yehia Mokhtar Farag
 */
public class UserUploadFilesComponent extends HorizontalLayout {

    private final AbsoluteLayout container;
    private final AbsoluteLayout subContainerLayout;
    private final Map<String, PluploadFile> uploadedFileMap;
    private TextField projectNameField;
    private AbsoluteLayout textFilesInputContainer;
    private AbsoluteLayout projectVisulizationButtonsContainer;
    private AbsoluteLayout fastaFileContainerLayout;
    private AbsoluteLayout proteinFileContainerLayout;
    private AbsoluteLayout peptideFileContainerLayout;
    private final AppManagmentBean appManagmentBean;

    /**
     * Constructor to initialise the main layout and variables.
     */
    public UserUploadFilesComponent() {

        appManagmentBean = (AppManagmentBean) VaadinSession.getCurrent().getAttribute(CONSTANT.APP_MANAGMENT_BEAN);
        UserUploadFilesComponent.this.setSizeFull();
        UserUploadFilesComponent.this.setStyleName("transitionallayout");
        UserUploadFilesComponent.this.setSpacing(false);
        UserUploadFilesComponent.this.setMargin(false);

        uploadedFileMap = new LinkedHashMap<>();
        container = new AbsoluteLayout();
        container.setSizeFull();

        UserUploadFilesComponent.this.addComponent(container);
        HorizontalLayout topLabelContainer = new HorizontalLayout();
        topLabelContainer.setSizeFull();
        topLabelContainer.addStyleName("minhight30");
        container.addComponent(topLabelContainer);
        HorizontalLayout topLeftLabelContainer = new HorizontalLayout();
        topLeftLabelContainer.setWidthUndefined();
        topLeftLabelContainer.setHeight(100, Unit.PERCENTAGE);
        topLabelContainer.addComponent(topLeftLabelContainer);

        // CONFIGURING GRAPHICAL COMPONENTS...
        Label titleLabel = new Label("Upload Your Own Project Files");
        titleLabel.setStyleName("frametitle");
        titleLabel.addStyleName("maintitleheader");
        container.addComponent(titleLabel, "left:40px;top:13px");
        HelpPopupButton helpBtn = new HelpPopupButton("<h1>Upload / Visualize Projects</h1>Users can upload and visualise their own processed files ( FASTA, protein and  peptide files )the format of the protein and peptide files should follow the sample files format <a href='VAADIN/sample_files.zip' download>[download sample files]</a>.\n"
                + "please note that the files will be automatically deleted after the session expire.", "", 400, 150);
        container.addComponent(helpBtn, "left:265px;top:10px");

        AbsoluteLayout mainContainerLayout = new AbsoluteLayout();
        mainContainerLayout.setSizeFull();
        mainContainerLayout.setStyleName("maincontentcontainer");
        container.addComponent(mainContainerLayout, "left:25px;top:25px;bottom:25px;right:25px");

        subContainerLayout = new AbsoluteLayout();
        subContainerLayout.setWidth(500, Unit.PIXELS);
        subContainerLayout.setHeight(386, Unit.PIXELS);
        subContainerLayout.addStyleName("marginleft-250");
        subContainerLayout.addStyleName("margintop-198");
        subContainerLayout.addStyleName("datainputsubcontainer");
        mainContainerLayout.addComponent(subContainerLayout, "left:50%;top:50%;");
        initAllForms();

    }

    private void initAllForms() {
        subContainerLayout.removeAllComponents();
        AbsoluteLayout projectNameContainer = initializeProjectNameLayout();
        subContainerLayout.addComponent(projectNameContainer, "left:25px;top:25px;right:25px");

        AbsoluteLayout fastaFileContainer = initializeProjectFastaFileLayout();
        subContainerLayout.addComponent(fastaFileContainer, "left:25px;top:105px;right:25px");

        textFilesInputContainer = initializeProjectInputFilesLayout();
        subContainerLayout.addComponent(textFilesInputContainer, "left:25px;top:190px;right:25px");
        projectVisulizationButtonsContainer = initializeProjecVisulizationButtonsLayout();
        subContainerLayout.addComponent(projectVisulizationButtonsContainer, "left:25px;top:320px;right:25px");

    }

    private AbsoluteLayout initializeProjectNameLayout() {

        AbsoluteLayout projectNameContainer = new AbsoluteLayout();
        projectNameContainer.setCaption("Project Name");
        projectNameContainer.setWidth(100, Unit.PERCENTAGE);
        projectNameContainer.setHeight(55, Unit.PIXELS);
        projectNameContainer.setStyleName("titleinborder");

        projectNameField = new TextField();
        projectNameField.setInputPrompt("New Project Name");
        projectNameField.setCaptionAsHtml(true);
        projectNameField.setWidth(100, Unit.PERCENTAGE);
        projectNameField.setRequired(false);
        projectNameField.addStyleName("psprojectname");
        projectNameField.setSizeFull();
        projectNameContainer.addComponent(projectNameField, "left:15px;top:15px;right:15px;bottom:15px");
        return projectNameContainer;
    }

    private AbsoluteLayout initializeProjectFastaFileLayout() {

        AbsoluteLayout FastaFileContainer = new AbsoluteLayout();
        FastaFileContainer.setCaption("Input Database");
        FastaFileContainer.setWidth(100, Unit.PERCENTAGE);
        FastaFileContainer.setHeight(60, Unit.PIXELS);
        FastaFileContainer.setStyleName("titleinborder");

        fastaFileContainerLayout = this.initUploaderField("FASTA File", CONSTANT.FASTA_FILE_EXTENSION, CONSTANT.FASTA_FILE_EXTENSION);
        FastaFileContainer.addComponent(fastaFileContainerLayout, "left:15px;top:15px;right:15px;bottom:15px");
        return FastaFileContainer;
    }

    private AbsoluteLayout initializeProjectInputFilesLayout() {
        AbsoluteLayout initTextFilesInputContainer = new AbsoluteLayout();
        initTextFilesInputContainer.setCaption("Project Files");
        initTextFilesInputContainer.setWidth(100, Unit.PERCENTAGE);
        initTextFilesInputContainer.setHeight(106, Unit.PIXELS);
        initTextFilesInputContainer.setStyleName("titleinborder");
        proteinFileContainerLayout = this.initUploaderField("Protein File", CONSTANT.PROTEIN_FILE_TYPE, "txt");
        initTextFilesInputContainer.addComponent(proteinFileContainerLayout, "left:15px;top:15px;right:15px");

        peptideFileContainerLayout = this.initUploaderField("Peptide File", CONSTANT.PEPTIDE_FILE_TYPE, "txt");
        initTextFilesInputContainer.addComponent(peptideFileContainerLayout, "left:15px;top:60px;right:15px");
        return initTextFilesInputContainer;

    }

    private AbsoluteLayout initializeProjecVisulizationButtonsLayout() {

        AbsoluteLayout projectVisulizationButtonsLayoutContainer = new AbsoluteLayout();
        projectVisulizationButtonsLayoutContainer.setWidth(100, Unit.PERCENTAGE);
        projectVisulizationButtonsLayoutContainer.setHeight(40, Unit.PIXELS);
        projectVisulizationButtonsLayoutContainer.setStyleName("titleinborder");

        Button visualizeDataBtn = new Button("Visualize");
        visualizeDataBtn.setStyleName(ValoTheme.BUTTON_SMALL);
        visualizeDataBtn.addStyleName(ValoTheme.BUTTON_TINY);
        visualizeDataBtn.setHeight(100, Unit.PERCENTAGE);
        visualizeDataBtn.setWidth(100, Unit.PIXELS);
        visualizeDataBtn.addStyleName("aligntoleft");
        visualizeDataBtn.addClickListener((Button.ClickEvent event) -> {
            validateAndVisualise();
        });

        Button clearBtn = new Button("Clear");
        clearBtn.setStyleName(ValoTheme.BUTTON_SMALL);
        clearBtn.addStyleName(ValoTheme.BUTTON_TINY);
        clearBtn.setHeight(100, Unit.PERCENTAGE);
        clearBtn.setWidth(100, Unit.PIXELS);
        clearBtn.addStyleName("aligntoright");
        clearBtn.addClickListener((Button.ClickEvent event) -> {
            clearForms();
        });

        projectVisulizationButtonsLayoutContainer.addComponent(visualizeDataBtn, "left:50%;top:7.5px;bottom:7.5px");
        projectVisulizationButtonsLayoutContainer.addComponent(clearBtn, "left:50%;top:7.5px;bottom:7.5px");
        return projectVisulizationButtonsLayoutContainer;
    }

    private AbsoluteLayout initUploaderField(String title, String fileType, String format) {
        AbsoluteLayout uploaderFieldContainer = new AbsoluteLayout();
        uploaderFieldContainer.setWidth(100, Unit.PERCENTAGE);
        uploaderFieldContainer.setHeight(30, Unit.PIXELS);
        uploaderFieldContainer.setStyleName("uploaderfilefield");
        Label label = new Label(title, ContentMode.HTML);
        label.setHeight(100, Unit.PERCENTAGE);
        label.setWidth(100, Unit.PIXELS);
        uploaderFieldContainer.addComponent(label, "left:5px;");
        final StatusProgressLabel validationProgressLabel = new StatusProgressLabel();
        final ProgressBar progressBar = new ProgressBar();
        progressBar.setWidth(20, Unit.PIXELS);
        progressBar.setHeight(30, Unit.PIXELS);

        final Label uploadedFileLabel = new Label();
        uploadedFileLabel.setSizeFull();

        BasicUploader uploader = new BasicUploader(appManagmentBean.getAppConfig().getUserFolderUri()) {
            private final String localFileType = fileType;

            @Override
            public void startFilesUpload(String fileName) {
                uploadedFileLabel.setStyleName("uploadedfilelabel");
                uploadedFileLabel.removeStyleName("done");
                uploadedFileLabel.setValue(fileName);
            }

            @Override
            public void filesUploaded(PluploadFile[] uploadedFiles) {
                for (PluploadFile file : uploadedFiles) {
                    boolean check = appManagmentBean.getFilesUtils().validateUploadedFiles((File) file.getUploadedFile(), localFileType);
                    uploadedFileLabel.addStyleName("done");
                    if (check) {
                        uploadedFileMap.put(localFileType, file);
                        uploaderFieldContainer.removeStyleName("errorstyle");
                        validationProgressLabel.setStatus(CONSTANT.OK_STATUS);
                        appManagmentBean.getNotificationFacade().showInfoNotification("File uploaded");
                    } else {
                        uploadedFileMap.remove(localFileType);
                        ((File) file.getUploadedFile()).delete();
                        validationProgressLabel.setStatus(CONSTANT.ERROR_STATUS);
                        uploaderFieldContainer.addStyleName("errorstyle");
                        appManagmentBean.getNotificationFacade().showErrorNotification("Check the input fields");

                    }

                }

            }

            @Override
            public void uploadProgress(double progress) {
                float current = progressBar.getValue();
                if (current < 1.0f) {
                    progressBar.setValue((float) progress / 100.0f);
                    validationProgressLabel.setStatus("running");
                } else {
                    progressBar.setValue(0.0f);

                }
            }

            @Override
            public void notification(String message, boolean error) {
                if (error) {
                    appManagmentBean.getNotificationFacade().showErrorNotification(message);
                }
            }
        };
        progressBar.setWidth(100, Unit.PERCENTAGE);
        uploaderFieldContainer.addComponent(uploader, "right:75px");
        uploader.addAllowedFileExtension(format);
        uploaderFieldContainer.addComponent(validationProgressLabel, "right:5px; top:4px");
        uploaderFieldContainer.addComponent(progressBar, "left:82px;top:10px;right:68px");
        uploaderFieldContainer.addComponent(uploadedFileLabel, "left:82px;top:5px;right:68px;bottom:5px");
        return uploaderFieldContainer;

    }

    private void clearForms() {
        uploadedFileMap.values().stream().map((file) -> (File) file.getUploadedFile()).forEachOrdered((tFile) -> {
            tFile.delete();
        });
        uploadedFileMap.clear();
        initAllForms();

    }

    private void validateAndVisualise() {
        projectNameField.setRequired(false);
        proteinFileContainerLayout.removeStyleName("errorstyle");
        peptideFileContainerLayout.removeStyleName("errorstyle");
        projectNameField.removeStyleName("errorstyle");
        fastaFileContainerLayout.removeStyleName("errorstyle");
        if (projectNameField.getValue() == null || projectNameField.getValue().trim().equalsIgnoreCase("")) {
            projectNameField.setRequired(true);
            projectNameField.addStyleName("errorstyle");
            appManagmentBean.getNotificationFacade().showErrorNotification("Check the input fields");
            return;

        }
        if (!uploadedFileMap.containsKey(CONSTANT.FASTA_FILE_EXTENSION)) {
            fastaFileContainerLayout.addStyleName("errorstyle");
            appManagmentBean.getNotificationFacade().showErrorNotification("Check the input fields");
            return;

        }
        if (!uploadedFileMap.containsKey(CONSTANT.PROTEIN_FILE_TYPE)) {
            proteinFileContainerLayout.addStyleName("errorstyle");
            appManagmentBean.getNotificationFacade().showErrorNotification("Check the input fields");
            return;
        }
        if (!uploadedFileMap.containsKey(CONSTANT.PEPTIDE_FILE_TYPE)) {
            peptideFileContainerLayout.addStyleName("errorstyle");
            appManagmentBean.getNotificationFacade().showErrorNotification("Check the input fields");
            return;
        }
        appManagmentBean.getDatasetUtils().initialiseDatasetFromUploadedFiles(projectNameField.getValue(), uploadedFileMap);
        clearForms();
    }

}
