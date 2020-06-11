package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;
import pl.exsio.plupload.Plupload;
import pl.exsio.plupload.PluploadError;
import pl.exsio.plupload.PluploadFile;
import pl.exsio.plupload.helper.filter.PluploadFilter;

/**
 * This class represents Uploading class with progress bar
 *
 * @author Yehia Farag
 */
public abstract class Uploader extends AbsoluteLayout {

    private File userUploadFolder;
    private Plupload uploaderComponent;
    private final Label busyUpload;
    private final Set<String> filterSet;

    /**
     * Initialise upload component
     */
    public Uploader() {

        Uploader.this.setHeight(25, Unit.PIXELS);
        Uploader.this.setWidth(25, Unit.PIXELS);
        Uploader.this.setStyleName("uploaderlayout");
        this.filterSet = new LinkedHashSet<>();

        Uploader.this.addStyleName("uploaderbtnonly");
        busyUpload = new Label();
        busyUpload.setContentMode(ContentMode.HTML);
        busyUpload.setStyleName(ValoTheme.LABEL_TINY);
        busyUpload.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        busyUpload.setValue(htmlLoadingImg);
        Uploader.this.addComponent(busyUpload);
        initUploaderComponent();
        busyUpload.setVisible(true);
        uploaderComponent.setVisible(false);
    }

    /**
     * Get the main upload component
     *
     * @return uploaderComponent
     */
    public Plupload getUploaderComponent() {
        return uploaderComponent;
    }

    private void initUploaderComponent() {
        if (uploaderComponent != null) {
            Uploader.this.removeComponent(uploaderComponent);
        }
        uploaderComponent = new Plupload("Browse", VaadinIcons.FOLDER_OPEN_O);
        uploaderComponent.setMaxFileSize("1gb");
        uploaderComponent.addStyleName(ValoTheme.BUTTON_TINY);
        uploaderComponent.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        uploaderComponent.addStyleName("smooth");
        Uploader.this.addComponent(uploaderComponent);
        filterSet.forEach((ext) -> {
            this.addUploaderFilter(ext);
        });
        /**
         * show notification after file is uploaded*
         */
        uploaderComponent.addFileUploadedListener((PluploadFile file) -> {
            Notification.show("File uploaded : " + file.getName(),Notification.Type.TRAY_NOTIFICATION);
        });
        uploaderComponent.setPreventDuplicates(true);

        /**
         * auto start the upload after add files
         */
        uploaderComponent.addFilesAddedListener((PluploadFile[] files) -> {
            if (userUploadFolder == null) {
                String userDataFolderUrl = VaadinSession.getCurrent().getAttribute("userDataFolderUrl") + "";
                String APIKey = VaadinSession.getCurrent().getAttribute("ApiKey").toString();
                File user_folder = new File(userDataFolderUrl, APIKey);
                if (!user_folder.exists()) {
                    user_folder.mkdir();
                }
                userUploadFolder = new File(user_folder, "uploadedFiles");
                userUploadFolder.mkdir();
                uploaderComponent.setUploadPath(userUploadFolder.getAbsolutePath());
            }
            uploaderComponent.start();
            busyUpload.setVisible(true);
            uploaderComponent.setVisible(false);
        });
        uploaderComponent.addUploadStopListener(() -> {
        });

        /**
         * notify, when the upload process is completed
         *
         */
        uploaderComponent.addUploadCompleteListener(() -> {
            filesUploaded(uploaderComponent.getUploadedFiles());
            initUploaderComponent();
        });

        /**
         * handle errors
         */
        uploaderComponent.addErrorListener((PluploadError error) -> {
            Notification.show("Error", "Only " + filterSet + " file format allowed", Notification.Type.TRAY_NOTIFICATION);
        });

    }

    private final String htmlLoadingImg = "<img src='VAADIN/themes/webpeptideshakertheme/img/globeearthanimation.gif' alt='' style='width: 17px;top: -2px;background-color: white;position: absolute;'>";

    /**
     * Set upload is temporary disable
     *
     * @param busy upload in progress
     */
    public void setBusy(boolean busy) {
        busyUpload.setVisible(busy);
        uploaderComponent.setVisible(!busy);
    }

    /**
     * Add upload file extension
     *
     * @param fileExtension file extension to filter on
     */
    public void addUploaderFilter(String fileExtension) {
        uploaderComponent.addFilter(new PluploadFilter(fileExtension, fileExtension));
        filterSet.add(fileExtension);
    }

    /**
     * Files Uploaded
     *
     * @param uploadedFiles array of files uploaded
     */
    public abstract void filesUploaded(PluploadFile[] uploadedFiles);

}
