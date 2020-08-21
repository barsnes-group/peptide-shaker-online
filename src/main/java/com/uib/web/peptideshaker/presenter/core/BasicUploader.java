package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;
import pl.exsio.plupload.Plupload;
import pl.exsio.plupload.PluploadError;
import pl.exsio.plupload.PluploadFile;
import pl.exsio.plupload.helper.filter.PluploadFilter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class represents Uploading class with progress bar
 *
 * @author Yehia Farag
 */
public abstract class BasicUploader extends AbsoluteLayout {

    private final Set<String> filterSet;
    private Plupload uploaderComponent;

    /**
     * Initialise the upload unit
     */
    public BasicUploader() {
        BasicUploader.this.setHeight(23, Unit.PIXELS);
        BasicUploader.this.setWidth(50, Unit.PIXELS);
        BasicUploader.this.setStyleName("uploaderlayout");
        this.filterSet = new LinkedHashSet<>();
        initUploaderComponent();

    }

    private void initUploaderComponent() {
        if (uploaderComponent != null) {
            BasicUploader.this.removeComponent(uploaderComponent);
        }
        uploaderComponent = new Plupload("Browse", VaadinIcons.FOLDER_OPEN_O);
        uploaderComponent.setMaxFileSize("1gb");
        uploaderComponent.addStyleName(ValoTheme.BUTTON_TINY);
        uploaderComponent.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        uploaderComponent.addStyleName(ValoTheme.BUTTON_LINK);
        uploaderComponent.addStyleName("smooth");
        BasicUploader.this.addComponent(uploaderComponent);
        filterSet.forEach((ext) -> {
            this.addAllowedFileExtension(ext);
        });
        /**
         * show notification after file is uploaded*
         */
        uploaderComponent.addFileUploadedListener((PluploadFile file) -> {
            Notification.show("File Uploaded: " + file.getName());
        });

        /**
         * update upload progress*
         */
        uploaderComponent.addUploadProgressListener((PluploadFile file) -> {
            this.uploadProgress(file.getPercent());
        });
        uploaderComponent.setPreventDuplicates(true);

        /**
         * autostart the uploader after add files*
         */
        uploaderComponent.addFilesAddedListener((PluploadFile[] files) -> {
            uploaderComponent.start();
            startFilesUpload(files[0].getName());
        });
        uploaderComponent.addUploadStopListener(() -> {
        });

        /**
         * notify, when the upload process is completed*
         */
        uploaderComponent.addUploadCompleteListener(() -> {
            filesUploaded(uploaderComponent.getUploadedFiles());
            initUploaderComponent();

        });

        /**
         * handle errors*
         */
        uploaderComponent.addErrorListener((PluploadError error) -> {
            uploadError(error.getMessage());
            Notification.show("Error in uploading file, only " + filterSet + " file format allowed", Notification.Type.ERROR_MESSAGE);
        });
        String uploadPath = VaadinSession.getCurrent().getAttribute("userDataFolderUrl") + "";
        uploaderComponent.setUploadPath(uploadPath);

    }

    /**
     * Add file type (extension) to upload
     *
     * @param fileExtension
     */
    public void addAllowedFileExtension(String fileExtension) {
        uploaderComponent.addFilter(new PluploadFilter(fileExtension, fileExtension));
        filterSet.add(fileExtension);
    }

    /**
     * Start uploading file
     *
     * @param fileName file name
     */
    public abstract void startFilesUpload(String fileName);

    /**
     * File done uploaded
     *
     * @param uploadedFiles array of uploaded files
     */
    public abstract void filesUploaded(PluploadFile[] uploadedFiles);

    /**
     * Update file upload progress
     *
     * @param progress the upload progress
     */
    public abstract void uploadProgress(double progress);

    /**
     * Error happened during upload
     *
     * @param error error message
     */
    public abstract void uploadError(String error);

}
