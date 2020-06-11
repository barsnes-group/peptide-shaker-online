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
    private final Label info;
    private final Button uploaderBtn;
    private final Button closeBtn;
    private final ProgressBar bar;
    private final Set<String> filterSet;

    private PopupView popupUploaderUnit;

    /**
     * Initialise upload component
     */
    public Uploader() {

        Uploader.this.setHeight(28, Unit.PIXELS);
        Uploader.this.setWidth(100, Unit.PERCENTAGE);
        Uploader.this.setStyleName("uploaderlayout");
        this.filterSet = new LinkedHashSet<>();

        VerticalLayout uploaderLayout = new VerticalLayout();
        uploaderLayout.setWidth(300, Unit.PIXELS);
        uploaderLayout.setHeight(100, Unit.PERCENTAGE);
        uploaderLayout.setSpacing(false);
        Uploader.this.addComponent(uploaderLayout, "right:140px;top:2px");
        uploaderLayout.addStyleName("smooth");
        uploaderLayout.addStyleName("hidebywidth");

        bar = new ProgressBar(0.0f);
        uploaderLayout.addComponent(bar);
        uploaderLayout.setComponentAlignment(bar, Alignment.TOP_LEFT);
        bar.setWidth(300, Unit.PIXELS);

        info = new Label();
        info.setContentMode(ContentMode.HTML);
        info.setStyleName(ValoTheme.LABEL_TINY);
        info.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        uploaderLayout.addComponent(info);
        uploaderLayout.setComponentAlignment(info, Alignment.TOP_LEFT);
        info.setWidth(300, Unit.PIXELS);

        initUploaderComponent();
        uploaderComponent.addStyleName("hidebywidth");

        uploaderBtn = new Button("Upload", FontAwesome.UPLOAD);
        uploaderBtn.setWidth(28, Unit.PIXELS);
        uploaderBtn.setHeight(28, Unit.PIXELS);
        uploaderBtn.addStyleName(ValoTheme.BUTTON_TINY);
        uploaderBtn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        Uploader.this.addComponent(uploaderBtn, "right:2px;top:2px");

        closeBtn = new Button();
        closeBtn.setIcon(FontAwesome.CLOSE);
        closeBtn.setWidth(28, Unit.PIXELS);
        closeBtn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        closeBtn.setHeight(28, Unit.PIXELS);
        closeBtn.addStyleName(ValoTheme.BUTTON_TINY);
        closeBtn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        Uploader.this.addComponent(closeBtn, "right:2px;top:2px");
        closeBtn.addClickListener((Button.ClickEvent event) -> {
            if (popupUploaderUnit != null) {
                popupUploaderUnit.setPopupVisible(false);
                bar.setValue(0.0f);

            } else {
                closeBtn.setVisible(false);
                uploaderBtn.setVisible(true);
                uploaderLayout.addStyleName("hidebywidth");
                uploaderComponent.addStyleName("hidebywidth");
            }
            initUploaderComponent();

        });

        uploaderBtn.addClickListener((Button.ClickEvent event) -> {
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
            uploaderLayout.removeStyleName("hidebywidth");
            uploaderComponent.removeStyleName("hidebywidth");
            uploaderBtn.setVisible(false);
            closeBtn.setVisible(true);

        });
    }

    /**
     * Get the upload progress pop-up layout
     *
     * @return pop-up view component
     */
    public PopupView getPopupUploaderUnit() {
        if (popupUploaderUnit == null) {
            Uploader.this.setWidth(375, Unit.PIXELS);
            Uploader.this.setHeight(32, Unit.PIXELS);
            popupUploaderUnit = new PopupView(FontAwesome.UPLOAD.getHtml(), Uploader.this) {
                @Override
                public void setPopupVisible(boolean visible) {
                    if (!filterSet.isEmpty()) {
                        info.setValue("Upload " + filterSet);
                    }
                    if (visible) {
                        uploaderBtn.click();
                    }
                    if (!busy) {
                        bar.setValue(0.0f);
                        initUploaderComponent();
                        super.setPopupVisible(visible); 
                    } else {
                        super.setPopupVisible(false);
                    }
                }

            };
            popupUploaderUnit.setHideOnMouseOut(false);
            popupUploaderUnit.setCaptionAsHtml(true);
            popupUploaderUnit.setWidth(100, Unit.PIXELS);
            popupUploaderUnit.setHeight(28, Unit.PIXELS);
            popupUploaderUnit.addStyleName("popupuploader");
        }
        return popupUploaderUnit;
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
        Uploader.this.addComponent(uploaderComponent, "right:35px;top:2px");
        info.setValue("Upload " + filterSet);
        filterSet.forEach((ext) -> {
            this.addUploaderFilter(ext);
        });
        /**
         * show notification after file is uploaded*
         */
        uploaderComponent.addFileUploadedListener((PluploadFile file) -> {
            Notification.show("File uploaded : " + file.getName());
        });

        uploaderComponent.addUploadProgressListener((PluploadFile file) -> {
            info.setValue("File: " + file.getName() + " - " + file.getPercent() + " %");
            float current = bar.getValue();
            if (current < 1.0f) {
                float perc = (float) file.getPercent() / 100.0f;
                bar.setValue(perc);
                popupUploaderUnit.setDescription(((int) perc) + "%");

            } else {
                bar.setValue(0.0f);
            }

        });
        uploaderComponent.setPreventDuplicates(true);

        /**
         * autostart the upload after add files
         */
        uploaderComponent.addFilesAddedListener((PluploadFile[] files) -> {

            uploaderComponent.start();
        });
        uploaderComponent.addUploadStopListener(() -> {
        });

        /**
         * notify, when the upload process is completed
         *
         */
        uploaderComponent.addUploadCompleteListener(() -> {
            bar.setValue(0.0f);
            info.setValue("upload is done " + FontAwesome.SMILE_O.getHtml());
            filesUploaded(uploaderComponent.getUploadedFiles());
            initUploaderComponent();
            uploaderComponent.removeStyleName("hidebywidth");

            if (popupUploaderUnit != null) {
                popupUploaderUnit.setPopupVisible(false);
            }
        });

        /**
         * handle errors
         */
        uploaderComponent.addErrorListener((PluploadError error) -> {
            Notification.show("Error in uploading file, only " + filterSet + " file format allowed", Notification.Type.ERROR_MESSAGE);
            info.setValue("Not Supported File Format " + filterSet + " " + FontAwesome.FROWN_O.getHtml());
        });

    }

    private final String htmlLoadingImg = "<img src='VAADIN/themes/webpeptideshakertheme/img/globeearthanimation.gif' alt='' style='width: 17px;top: -17px;background-color: white;margin-left: -10px;position: absolute;'>";

    private boolean busy = false;

    /**
     * Set upload is temporary disable
     *
     * @param busy upload in progress
     */
    public void setBusy(boolean busy) {
        if (popupUploaderUnit == null) {
            return;
        }
        this.busy = busy;
        if (busy) {
            popupUploaderUnit.setCaption(htmlLoadingImg);
        } else {
            popupUploaderUnit.setCaption(null);
        }

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
