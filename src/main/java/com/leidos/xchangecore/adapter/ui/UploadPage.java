package com.leidos.xchangecore.adapter.ui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.core.util.resource.UrlResourceStream;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.file.Folder;
import org.apache.wicket.util.file.IResourceFinder;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leidos.xchangecore.adapter.XchangeCoreAdapter;
import com.leidos.xchangecore.adapter.csv.CSVFileParser;
import com.leidos.xchangecore.adapter.csv.ConfigFilePaser;
import com.leidos.xchangecore.adapter.model.CsvConfiguration;
import com.leidos.xchangecore.adapter.model.MappedRecord;
import com.leidos.xchangecore.adapter.webclient.WebServiceClient;

@SuppressWarnings("serial")
public class UploadPage
extends WebPage {

    /**
     * Form for uploads.
     */
    private class FileUploadForm
    extends Form<Void> {

        FileUploadField fileUploadField;

        /**
         * Construct.
         *
         * @param name
         *            Component name
         */
        public FileUploadForm(String name) {

            super(name);

            // set this form to multipart mode (always needed for uploads!)
            this.setMultiPart(true);

            // Add one file input field
            this.add(this.fileUploadField = new FileUploadField("fileInput"));

            // Set maximum size to 100K for demo purposes
            // setMaxSize(Bytes.kilobytes(100));
        }

        /**
         * @see org.apache.wicket.markup.html.form.Form#onSubmit()
         */
        @Override
        protected void onSubmit() {

            final List<FileUpload> uploads = this.fileUploadField.getFileUploads();
            if (uploads != null) {

                try {
                    UploadPage.this.getUploadFolder().ensureExists();
                } catch (final IOException ioe) {
                    final String errorMessage = "Upload Folder not existed and creation failed: " +
                        ioe.getMessage();
                    logger.error(errorMessage);
                    UploadPage.this.error(errorMessage);
                    return;
                }

                final boolean isRemoved = UploadPage.this.getUploadFolder().removeFiles();
                logger.debug("Removed all the files under xchangecore-uploads: " +
                    (isRemoved ? " success" : " failure"));

                for (final FileUpload upload : uploads) {
                    // Create a new file
                    final File newFile = new File(UploadPage.this.getUploadFolder(),
                        upload.getClientFileName());

                    UploadPage.this.info("Upload file: [" + upload.getClientFileName() + "] ...");
                    // Check new file, delete if it already existed
                    // UploadPage.this.checkFileExists(newFile);

                    try {
                        newFile.createNewFile();
                        upload.writeTo(newFile);
                    } catch (final Exception e) {
                        throw new IllegalStateException("Unable to write file", e);
                    }

                    try {
                        final CSVFileParser csvFileParser = new CSVFileParser(newFile,
                                                                              UploadPage.this.getFileStream(UploadPage.this.baseFilename),
                                                                              UploadPage.this.csvConfiguration);

                        redirectUrl = UploadPage.this.csvConfiguration.getRedirectUrl();
                        UploadPage.this.info("After parsed, send request to " +
                                             UploadPage.this.csvConfiguration.getUri() +
                                             " as user: " +
                                             UploadPage.this.csvConfiguration.getUsername());
                        final WebServiceClient wsClient = new WebServiceClient(UploadPage.this.csvConfiguration.getUri(),
                                                                               UploadPage.this.csvConfiguration.getUsername(),
                                                                               UploadPage.this.csvConfiguration.getPassword());

                        // get the new Incidents
                        final MappedRecord[] records = csvFileParser.getRecords();

                        if (records != null) {
                            UploadPage.this.numOfCreation = records.length;
                            UploadPage.this.info("Created: " + UploadPage.this.numOfCreation +
                                                 " records");
                            for (final MappedRecord r : records) {
                                if (wsClient.createIncident(r)) {
                                    CSVFileParser.getMappedRecordDao().makePersistent(r);
                                }
                            }
                        }

                        // update the incidents
                        final MappedRecord[] updateRecordSet = csvFileParser.getUpdateRecords();
                        if (updateRecordSet != null) {
                            UploadPage.this.numOfUpdate = updateRecordSet.length;
                            UploadPage.this.info("Updated: " + UploadPage.this.numOfUpdate +
                                                 " records");
                            for (final MappedRecord r : updateRecordSet) {
                                if (wsClient.updateIncident(r)) {
                                    CSVFileParser.getMappedRecordDao().makePersistent(r);
                                }
                            }
                        }

                        // delete the incidents
                        final MappedRecord[] deleteRecordSet = csvFileParser.getDeleteRecords();
                        if (deleteRecordSet != null) {
                            UploadPage.this.numOfDeletion = deleteRecordSet.length;
                            UploadPage.this.info("Deleted: " + UploadPage.this.numOfDeletion +
                                                 " records");
                            for (final MappedRecord r : deleteRecordSet) {
                                if (wsClient.deleteIncident(r)) {
                                    // remove only if it's close/archive in XchangeCore properly
                                    CSVFileParser.getMappedRecordDao().makeTransient(r);
                                }
                            }
                        }

                        logger.debug("number of creation/update/deletion: " +
                            UploadPage.this.numOfCreation + "/" +
                                     UploadPage.this.numOfUpdate + "/" +
                                     UploadPage.this.numOfDeletion);
                        UploadPage.this.info("Upload is done ...");
                        Files.remove(newFile);
                    } catch (final Throwable e) {
                        logger.error("Exception: " + e.getMessage());
                        UploadPage.this.error("Exception: " + e.getMessage());
                    }
                }
            }
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(UploadPage.class);

    private static final String ConfigParameterName = "config";
    private static final String ConfigFilePostfix = ".config";
    private static final String BaseFilePostfix = ".csv";
    private static String redirectUrl = "http://www.google.com";

    private int numOfCreation = 0;
    private int numOfUpdate = 0;
    private int numOfDeletion = 0;

    // private final InputStream configInputStream = null;
    // private final InputStream baseInputStream = null;
    private String baseFilename;
    private String configFilename;
    private CsvConfiguration csvConfiguration = null;

    // private ConfigFilePaser configFileParser = null;

    /** Reference to listview for easy access. */
    // private final FileListView fileListView = null;

    /**
     * Constructor.
     *
     * @param parameters
     *            Page parameters
     */
    public UploadPage(final PageParameters parameters) {

        // turn the version off
        this.setVersioned(false);

        this.numOfCreation = 0;
        this.numOfUpdate = 0;
        this.numOfDeletion = 0;

        this.getUploadFolder().removeFiles();
        // this.cleanUpFiles();

        // Create feedback panels
        final FeedbackPanel uploadFeedback = new FeedbackPanel("uploadFeedback");

        // Add uploadFeedback to the page itself
        this.add(uploadFeedback);

        // Add simple upload form, which is hooked up to its feedback panel by
        // virtue of that panel being nested in the form.
        final FileUploadForm simpleUploadForm = new FileUploadForm("simpleUpload");
        simpleUploadForm.add(new UploadProgressBar("progress",
                                                   simpleUploadForm,
                                                   simpleUploadForm.fileUploadField));
        this.add(simpleUploadForm);

        final ModalWindow statusPage = this.createStatusPage(this.numOfCreation,
            this.numOfUpdate,
            this.numOfDeletion);

        this.add(statusPage);
        this.add(new AjaxLink<Void>("showStatus") {

            @Override
            public void onClick(AjaxRequestTarget target) {

                statusPage.show(target);
            }
        });

        this.add(new AjaxLink<Void>("redirect") {

            @Override
            public void onClick(AjaxRequestTarget target) {

                logger.debug("redirect to " + redirectUrl);
                throw new RestartResponseAtInterceptPageException(new RedirectPage(redirectUrl));
            }
        });

        if (parameters.get(ConfigParameterName) == null) {
            UploadPage.this.error("No configuration defined: Usage: xcadapter?config=somename");
            return;
        }
        this.configFilename = parameters.get(ConfigParameterName) + ConfigFilePostfix;
        this.baseFilename = parameters.get(ConfigParameterName) + BaseFilePostfix;
        this.info("UploadPage: Configuration File: " + this.configFilename);
        logger.debug("UploadPage: configFilename: " + this.configFilename);

        // UploadPage.baseInputStream = this.getFileStream(this.baseFilename);

        if (this.getFileStream(this.configFilename) == null) {
            this.error("Configuration File: " + this.configFilename + " Not existed");
            return;
        }
        try {
            this.csvConfiguration = new ConfigFilePaser(this.configFilename,
                                                        this.getFileStream(this.configFilename)).getConfigMap();
        } catch (final Exception e) {
            this.error(e.getMessage());
        }
    }

    /**
     * Check whether the file allready exists, and if so, try to delete it.
     *
     * @param newFile
     *            the file to check
     */
    private void checkFileExists(File newFile) {

        if (newFile.exists()) {
            // Try to delete the file
            if (!Files.remove(newFile)) {
                logger.error("Cannot delete " + newFile.getAbsolutePath() + " ...");
                // throw new IllegalStateException("Unable to remove " + newFile.getAbsolutePath());
            }
        }
    }

    private void cleanUpFiles() {

        final File[] files = this.getUploadFolder().listFiles();

        if (files == null) {
            return;
        }

        for (final File file : files) {
            logger.debug("delete file: " + file.getAbsolutePath() + " ...");
            Files.remove(file);
        }
    }

    private ModalWindow createStatusPage(final int created, final int updated, final int deleted) {

        final ModalWindow modal = new ModalWindow("statusPage");

        modal.setCookieName("status window");

        modal.setResizable(false);
        modal.setInitialWidth(30);
        modal.setInitialHeight(15);
        modal.setWidthUnit("em");
        modal.setHeightUnit("em");

        modal.setCssClassName(ModalWindow.CSS_CLASS_GRAY);

        modal.setPageCreator(new ModalWindow.PageCreator() {

            @Override
            public Page createPage() {

                return new StatusPage(modal, created, updated, deleted);
            }
        });

        modal.setCloseButtonCallback(new ModalWindow.CloseButtonCallback() {

            @Override
            public boolean onCloseButtonClicked(AjaxRequestTarget target) {

                target.appendJavaScript("alert('You can\\'t close this modal window using close button."
                    + " Use the link inside the window instead.');");

                return false;
            }
        });

        return modal;

    }

    private InputStream getFileStream(String filename) {

        final List<IResourceFinder> finders = UploadPage.this.getApplication().getResourceSettings().getResourceFinders();
        for (final IResourceFinder finder : finders) {
            final IResourceStream resource = finder.find(UrlResourceStream.class, "/config/" +
                                                                                  filename);
            if ((resource != null) && (resource instanceof UrlResourceStream)) {
                try {
                    return ((UrlResourceStream) resource).getInputStream();
                } catch (final ResourceStreamNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return null;
                }
            }
        }

        return null;
    }

    private Folder getUploadFolder() {

        return ((XchangeCoreAdapter) Application.get()).getUploadFolder();
    }
}